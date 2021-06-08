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

package com.eoi.jax.common.grok;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GrokParser {

    private static final Pattern grokReference = Pattern.compile("%\\{([^}>':]+)(?::([^}>':]+)(?::(?:int|float))?)?\\}");
    private static final Pattern grokFileLinePattern = Pattern.compile("(\\w+) (.*)");
    private static final Pattern grokGroupName = Pattern.compile("\\(\\?<(\\w+)>");
    private static final String grokFileDir = "/grok.patterns";
    private static final String[] grokFiles = {"aws", "bacula", "bro", "exim", "firewalls", "grok-patterns",
            "haproxy", "java", "junos", "linux-syslog", "mcollective", "mcollective-patterns", "mongodb",
            "nagios", "postgresql", "rails", "redis", "ruby"};

    private Map<String, String> buildInPatternAlias;
    private Map<Pattern, List<String>> patternInfos;

    public GrokParser() {
        this(null);
    }

    public GrokParser(List<String> matches) {
        this.buildInPatternAlias = readGrokFromResource();
        if (matches != null && !matches.isEmpty()) {
            List<Pattern> finalMatches = matches.stream()
                    .map(m -> expandPatternAlias(m)) // expand pattern alias
                    .map(m -> Pattern.compile(m, Pattern.MULTILINE | Pattern.DOTALL)) // convert to pattern
                    .collect(Collectors.toList());
            patternInfos = new HashMap<>();
            for (Pattern pattern : finalMatches) {
                // try to find groupNames in the pattern expression
                Matcher groupNameMatcher = grokGroupName.matcher(pattern.pattern());
                List<String> groupNames = new ArrayList<>();
                while (groupNameMatcher.find()) {
                    groupNames.add(groupNameMatcher.group(1));
                }
                patternInfos.put(pattern, groupNames);
            }
        }
    }

    public Map<String, String> parse(String value) {
        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<Pattern, List<String>> pattern : this.patternInfos.entrySet()) {
            Matcher matcher = pattern.getKey().matcher(value);
            if (matcher.find()) {
                for (String groupName : pattern.getValue()) {
                    String groupValue = matcher.group(groupName);
                    if (groupValue != null && !groupValue.isEmpty()) {
                        result.put(groupName, groupValue);
                    }
                }
            }
        }
        return result;
    }

    // try to expand pattern alias from the match
    public String expandPatternAlias(String match) {
        Matcher matcher = grokReference.matcher(match);
        StringBuilder newMatch = new StringBuilder();
        int lastMatchEnd = 0;
        while (matcher.find()) {
            if (lastMatchEnd != matcher.start()) {
                newMatch.append(match.substring(lastMatchEnd, matcher.start()));
            }
            String alias = matcher.group(1);
            if (this.buildInPatternAlias.containsKey(alias)) {
                if (matcher.group(2) == null) {
                    newMatch.append(this.buildInPatternAlias.get(alias));
                } else {
                    newMatch.append("(?<");
                    newMatch.append(matcher.group(2));
                    newMatch.append(">");
                    newMatch.append(this.buildInPatternAlias.get(alias));
                    newMatch.append(")");
                }
            } else {
                // TODO: [L] throw exception?
            }
            lastMatchEnd = matcher.end();
        }
        if (lastMatchEnd < match.length()) {
            newMatch.append(match.substring(lastMatchEnd, match.length()));
        }
        if (lastMatchEnd == 0) {
            return newMatch.toString();
        } else {
            return expandPatternAlias(newMatch.toString());
        }
    }


    public Map<String, String> readGrokFromResource() {
        Map<String, String> grokFromResource = new HashMap<>();
        for (String file : grokFiles) {
            try (InputStream is = this.getClass().getResourceAsStream(grokFileDir + "/" + file)) {
                Map<String, String> result = readGrokFromStream(is);
                grokFromResource.putAll(result);
            } catch (Exception ignore) {
            }
        }
        if (grokFromResource.isEmpty()) {
            //读取失败,尝试延用旧的读取逻辑
            URL url = this.getClass().getResource(grokFileDir);
            if (url != null) {
                for (String file : grokFiles) {
                    try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(grokFileDir + "/" + file)) {
                        Map<String, String> result = readGrokFromStream(is);
                        grokFromResource.putAll(result);
                    } catch (Exception ignore) {
                    }
                }
                if (grokFromResource.isEmpty()) {
                    //如果依旧为空,尝试在当前目录下直接读取
                    File patternsDir = new File(url.getFile());
                    File[] files = patternsDir.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            try (InputStream is = new FileInputStream(file)) {
                                Map<String, String> result = readGrokFromStream(is);
                                grokFromResource.putAll(result);
                            } catch (Exception ignore) {
                            }
                        }
                    }
                }
            }
        }

        if (grokFromResource.isEmpty()) {
            throw new GrokParseException("Grok init failed,unable to read patterns from resource");
        }

        return grokFromResource;
    }

    public Map<String, String> readGrokFromStream(InputStream stream) {
        if (stream == null) {
            return new HashMap<>();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        return br.lines()
                .filter(l -> l.trim().length() > 0 && !l.startsWith("#"))
                .map(l -> {
                    Matcher matcher = grokFileLinePattern.matcher(l);
                    if (matcher.matches() && matcher.groupCount() == 2) {
                        return new GrokKeyValue(matcher.group(1), matcher.group(2));
                    }
                    return null;
                })
                .filter(entry -> entry != null)
                // 解决异常：java.lang.IllegalStateException: Duplicate key **
                // https://colorpanda.iteye.com/blog/2319688
                .collect(Collectors.toMap(GrokKeyValue::getKey, GrokKeyValue::getValue, (k, v) -> v));
    }
}
