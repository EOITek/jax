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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User Agent parser using ua-parser regexes
 */
public class UserAgentParser {

    private final List<UaPattern> patterns;

    public UserAgentParser(List<UaPattern> patterns) {
        this.patterns = patterns;
    }

    public static UserAgentParser fromList(List<Map<String, String>> configList) {
        List<UaPattern> configPatterns = new ArrayList<UaPattern>();

        for (Map<String, String> configMap : configList) {
            configPatterns.add(UserAgentParser.patternFromMap(configMap));
        }
        return new UserAgentParser(configPatterns);
    }

    public UserAgent parse(String agentString) {
        if (agentString == null) {
            return null;
        }

        UserAgent agent;
        for (UaPattern p : patterns) {
            if ((agent = p.match(agentString)) != null) {
                return agent;
            }
        }
        return new UserAgent("Other", null, null, null);
    }

    protected static UaPattern patternFromMap(Map<String, String> configMap) {
        String regex = configMap.get("regex");
        if (regex == null) {
            throw new IllegalArgumentException("User agent is missing regex");
        }

        return (new UaPattern(Pattern.compile(regex),
                configMap.get("family_replacement"),
                configMap.get("v1_replacement"),
                configMap.get("v2_replacement")));
    }

    protected static class UaPattern {

        private final Pattern pattern;
        private final String familyReplacement;
        private final String v1Replacement;
        private final String v2Replacement;

        public UaPattern(Pattern pattern, String familyReplacement, String v1Replacement, String v2Replacement) {
            this.pattern = pattern;
            this.familyReplacement = familyReplacement;
            this.v1Replacement = v1Replacement;
            this.v2Replacement = v2Replacement;
        }

        public UserAgent match(String agentString) {
            String family = null;
            String v1 = null;
            String v2 = null;
            String v3 = null;
            Matcher matcher = pattern.matcher(agentString);

            if (!matcher.find()) {
                return null;
            }

            int groupCount = matcher.groupCount();

            if (familyReplacement != null) {
                if (familyReplacement.contains("$1") && groupCount >= 1 && matcher.group(1) != null) {
                    family = familyReplacement.replaceFirst("\\$1", Matcher.quoteReplacement(matcher.group(1)));
                } else {
                    family = familyReplacement;
                }
            } else if (groupCount >= 1) {
                family = matcher.group(1);
            }

            if (v1Replacement != null) {
                v1 = v1Replacement;
            } else if (groupCount >= 2) {
                v1 = matcher.group(2);
            }

            if (v2Replacement != null) {
                v2 = v2Replacement;
            } else if (groupCount >= 3) {
                v2 = matcher.group(3);
                if (groupCount >= 4) {
                    v3 = matcher.group(4);
                }
            }
            return family == null ? null : new UserAgent(family, v1, v2, v3);
        }
    }
}
