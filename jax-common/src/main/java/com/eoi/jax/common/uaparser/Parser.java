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

package com.eoi.jax.common.uaparser;

import com.eoi.jax.common.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Java implementation of <a href="https://github.com/tobie/ua-parser">UA Parser</a>
 */
public class Parser {

    private static final String REGEX_YAML_PATH = "/uaparser/regexes.yaml";
    private UserAgentParser uaParser;
    private OsParser osParser;
    private DeviceParser deviceParser;

    public Parser() throws IOException {
        initialize();
    }

    public Client parse(String agentString) {
        UserAgent ua = parseUserAgent(agentString);
        OS os = parseOS(agentString);
        Device device = parseDevice(agentString);
        return new Client(ua, os, device);
    }

    public UserAgent parseUserAgent(String agentString) {
        return uaParser.parse(agentString);
    }

    public Device parseDevice(String agentString) {
        return deviceParser.parse(agentString);
    }

    public OS parseOS(String agentString) {
        return osParser.parse(agentString);
    }

    private void initialize() {
        List<Map<String, String>> uaParserConfigs;
        try {
            uaParserConfigs = JsonUtil.decode(
                    Constants.USER_AGENT_PARSERS,
                    new TypeReference<List<Map<String, String>>>() {
                    });
        } catch (IOException e) {
            throw new IllegalArgumentException("user_agent_parsers is invalid");
        }
        if (uaParserConfigs == null) {
            throw new IllegalArgumentException("user_agent_parsers is missing");
        }
        uaParser = UserAgentParser.fromList(uaParserConfigs);

        List<Map<String, String>> osParserConfigs;
        try {
            osParserConfigs = JsonUtil.decode(
                    Constants.OS_PARSERS,
                    new TypeReference<List<Map<String, String>>>() {
                    });
        } catch (IOException e) {
            throw new IllegalArgumentException("os_parsers is invalid");
        }
        if (osParserConfigs == null) {
            throw new IllegalArgumentException("os_parsers is missing");
        }
        osParser = OsParser.fromList(osParserConfigs);

        List<Map<String, String>> deviceParserConfigs;
        try {
            deviceParserConfigs = JsonUtil.decode(
                    Constants.DEVICE_PARSERS,
                    new TypeReference<List<Map<String, String>>>() {
                    });
        } catch (IOException e) {
            throw new IllegalArgumentException("device_parsers is invalid");
        }
        if (deviceParserConfigs == null) {
            throw new IllegalArgumentException("device_parsers is missing");
        }
        deviceParser = DeviceParser.fromList(deviceParserConfigs);
    }
}
