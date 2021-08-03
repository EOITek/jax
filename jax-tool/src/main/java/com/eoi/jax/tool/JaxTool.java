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

package com.eoi.jax.tool;

public class JaxTool {
    private static final String ACTION = "action";
    private static final String VALIDATE = "validate";
    private static final String SCAN = "scan";
    private static final String MODE = "mode";
    private static final String PATH = "path";
    private static final String OUTPUT = "output";

    public static void main(String[] args) {
        CliArgTool argTool = CliArgTool.fromArgs(args);
        String action = argTool.getRequired(ACTION);
        String path = argTool.getRequired(PATH);
        String output = argTool.getRequired(OUTPUT);
        String mode = argTool.get(MODE);
        if (VALIDATE.equalsIgnoreCase(action)) {
            JaxDAGValidator validator = new JaxDAGValidator(mode, path, output);
            validator.run();
        } else if (SCAN.equalsIgnoreCase(action)) {
            JaxJobScanner scanner = new JaxJobScanner(mode, path, output);
            scanner.run();
        } else {
            throw new IllegalArgumentException(String.format("Not support action '%s'", action));
        }
    }
}
