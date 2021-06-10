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

package com.eoi.jax.core.test;

import com.eoi.jax.common.JsonUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import py4j.GatewayServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class Py4jTest {

    protected static Boolean envPresent;
    protected static String jaxBathPath;

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @BeforeClass
    public static void checkEnv() {
        envPresent = false;
        // 无论是mvn命令还是intellij，在运行测试的时候都会设置PWD为当前module目录
        // .../jax/jax-spark-job
        String moduleRoot = System.getenv("PWD");
        String solutionRoot = new File(moduleRoot).getParent();
        String jaxPythonRoot = new File(solutionRoot, "jax_python").getAbsolutePath();
        jaxBathPath = String.join(":", Arrays.asList(solutionRoot, jaxPythonRoot));
    }

    // CHECKSTYLE.OFF:
    @Before
    public void setPYTHONPATH() {
        environmentVariables.set("PYTHONPATH", jaxBathPath);
    }
    // CHECKSTYLE.ON:

    public static class EntryPoint {
        private Map<String, Object> map;

        public Map<String, Object> get_map() {
            return map;
        }

        public void set_map(Map<String, Object> map) {
            this.map = map;
        }
    }

    @Test
    @Ignore
    public void javaToPythonConvertTest() throws Exception {
        String secret = UUID.randomUUID().toString();
        Map<String, Object> entryPoint =
                JsonUtil.decode2Map("{\"a\":\"b\",\"c\":\"d\",\"e\":1,\"f\":1.01,\"g\":{\"ia\":\"b\",\"ib\":"
                        + "[\"java\",\"python\",\"scala\"],\"ic\":[{\"la\":\"la\"},{\"lb\":\"lb\"}]}}");
        System.out.println(JsonUtil.encode(entryPoint));
        EntryPoint ep = new EntryPoint();
        ep.set_map(entryPoint);
        GatewayServer gatewayServer =
                new GatewayServer.GatewayServerBuilder()
                        .entryPoint(ep)
                        .authToken(secret)
                        .javaPort(0)
                        .build();
        gatewayServer.start();
        ProcessBuilder pb = new ProcessBuilder("python", "-m", "jax_python.test");
        Map<String, String> env = pb.environment();
        env.put("PY4J_SECRET", secret);
        env.put("PY4J_PORT", String.format("%d", gatewayServer.getListeningPort()));
        env.put("PYTHONUNBUFFERED", "YES");
        pb.redirectErrorStream(true);
        Process pythonProcess = pb.start();

        Thread stdout = new Thread(() -> {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pythonProcess.getInputStream()));
            String line;
            try {
                line = bufferedReader.readLine();
                while (line != null) {
                    System.out.println(String.format("[FP] %s", line));
                    line = bufferedReader.readLine();
                }
            } catch (Exception ex) {
                System.out.println(ex.toString());
            } finally {
                try {
                    pythonProcess.waitFor();
                } catch (Exception ignore) { }
            }
        });
        stdout.start();
        stdout.join();
        gatewayServer.shutdown();
    }
}
