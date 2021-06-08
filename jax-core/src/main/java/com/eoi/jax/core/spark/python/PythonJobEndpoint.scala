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

import com.eoi.jax.api.SparkEnvironment
import scala.collection.JavaConverters._

case class PythonJobEndpoint[IN, OUT](sparkBuilderContext: SparkEnvironment,
                                      jobConfig: Any,
                                      sync: PythonSync,
                                      input: IN,
                                      output: java.util.List[OUT]
                                     ) {
  def getPy4JImports: java.util.List[String] = PythonJobEndpoint.pythonImports.toList.asJava
}

object PythonJobEndpoint {
  val pythonImports = Seq(
    "org.apache.spark.api.java.*",
    "org.apache.spark.api.python.*",
    "org.apache.spark.SparkConf",
    "scala.Tuple2",
    "org.apache.spark.mllib.api.python.*",
    "org.apache.spark.sql.SQLContext",
    "org.apache.spark.sql.UDFRegistration",
    "org.apache.spark.sql.hive.HiveContext",
    "org.apache.spark.sql.*",
    "org.apache.spark.sql.hive.*"
  )
}