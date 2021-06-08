/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eoi.jax.core.spark.python

import cn.hutool.core.util.StrUtil

import java.util
import java.util.UUID
import java.util.concurrent.TimeUnit
import com.eoi.jax.api.{SparkEnvironment, SparkProcessJobBuilder}
import com.eoi.jax.api.annotation.Job
import org.apache.spark.scheduler.{SparkListener, SparkListenerApplicationEnd}
import org.apache.spark.sql.DataFrame

import scala.io.Source
import scala.collection.JavaConverters._


@Job(
  isInternal = true,
  name = "BridgeJob",
  display = "BridgeJob",
  description = "用于适配任意基于python实现的spark算法job，配合jax_python.spark_bridge.py工作。输入和输出都是DataFrame。"
)
class BridgeJob extends SparkProcessJobBuilder[DataFrame, DataFrame, BridgeJobConfig] with Logging {

  /**
   * 将mapConfig反序列化成一个配置类
   *
   * @param mapConfig 配置map
   * @return
   */
  override def configure(mapConfig: util.Map[String, AnyRef]): BridgeJobConfig = {
    BridgeJobConfig.fromMap(mapConfig)
  }

  /**
   * 启动一个JavaGateway，将文件复制到temp目录下，以及启动Python进程
   *
   * @param context { @link SparkBuilderContext} 中包含必要的流对象和表对象
   * @param in     输入对象，上游输入的流对象或者表对象
   * @param config 配置对象, 保存构建需要的配置, 会通过configure接口实例化
   * @return 可向下游传递的对象，通常是DataStream或者Table
   * @throws Exception 如果出现严重错误，请抛出异常
   */
  override def build(context: SparkEnvironment, in: DataFrame, config: BridgeJobConfig): DataFrame = {
    // start py4j Gateway
    val secret = UUID.randomUUID().toString
    val out = new util.ArrayList[DataFrame]()
    val sync = new PythonSync
    val pythonJobEndpoint = PythonJobEndpoint[DataFrame, DataFrame](context, config, sync, in, out)
    val server = new py4j.GatewayServer.GatewayServerBuilder()
      .authToken(secret)
      .javaPort(0)
      .entryPoint(pythonJobEndpoint)
      .build()
    // PYSPARK_DRIVER_PYTHON and PYSPARK_PYTHON is predefined env var for pyspark
    // here we just reuse the definition
    val pythonExec = sys.env.get("PYSPARK_DRIVER_PYTHON")
      .orElse(sys.env.get("PYSPARK_PYTHON"))
      .getOrElse("python")
    server.start()
    LOG.info("py4j java gateway started, listening on port {}", server.getListeningPort.toString)

    // Launch Python process
    val builder = new ProcessBuilder(Seq(pythonExec, "-m","jax_python.spark_bridge").asJava)
    val env = builder.environment()
    if (sys.env.contains("PYTHONPATH")) {
      env.put("PYTHONPATH", sys.env("PYTHONPATH"))
    }

    LOG.info("python entry:{}",System.getProperty("JAX_PYTHON_ENTRY"))
    val jaxPythonEntry = System.getProperty("JAX_PYTHON_ENTRY")
    if (jaxPythonEntry!=null){
      if (StrUtil.isNotEmpty(env.get("PYTHONPATH"))){
        env.put("PYTHONPATH",env.get("PYTHONPATH")+":"+jaxPythonEntry)
      } else {
        env.put("PYTHONPATH",jaxPythonEntry)
      }
    }

    env.put("PY4J_SECRET", secret)
    env.put("PY4J_PORT", server.getListeningPort.toString)
    env.put("PYTHONUNBUFFERED", "YES") // python print as be buffered by default
    if (config.getDisableLogAccumulator!=null && config.getDisableLogAccumulator) {
      env.put("_DISABLE_LOG_ACCUMULATOR", "yes") // for debug, python `print` can easy to see
    }
    builder.redirectErrorStream(true) // Ugly but needed for stdout and stderr to synchronize
    // start the process (non-block)
    LOG.info("starting python process: {}", builder.command().asScala.mkString(" "))
    val process = builder.start()
    context.sparkContext.isStopped
    context.sparkContext.addSparkListener(new SparkListener {
      override def onApplicationEnd(applicationEnd: SparkListenerApplicationEnd): Unit = {
        process.waitFor(5, TimeUnit.SECONDS)
        LOG.info("destroying the process 'python -m  jax_python.spark_bridge' if still alive")
        server.shutdown()
        if (process.isAlive) {
          process.destroy()
        }
      }
    })

    // start a thread to read lines from the output stream of the process
    val thread = new Thread(new Runnable() {
      override def run(): Unit = {
        try {
          Source.fromInputStream(process.getInputStream).getLines().foreach(line => {
            LOG.info("[python_on_driver] {}", line)
          })
          sync.release()
          val exitValue = process.waitFor()
          exitValue match {
            case 0 => LOG.info("[python_on_driver] driver process exit with 0")
            case ev => LOG.error("[python_on_driver] driver process exit with {}", ev)
          }
        } catch {
          case t: Throwable =>
            sync.release()
            LOG.error("reading python output on driver got exception: {}", t.toString)
        }
      }
    })
    thread.setDaemon(true)
    thread.start()
    // await the python process signal
    LOG.info("waiting for python sync signal")
    sync.await()
    LOG.info("wake up since python process release the sync signal")
    if (out.size() < 1) {
      LOG.error("missing result from python job")
      throw new PythonOnDriverException("python -m jax_python.spark_bridge")
    }
    out.get(0)
  }
}
