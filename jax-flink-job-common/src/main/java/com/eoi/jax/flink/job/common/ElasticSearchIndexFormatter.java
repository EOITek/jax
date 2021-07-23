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

package com.eoi.jax.flink.job.common;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElasticSearchIndexFormatter implements Serializable {

    final String datePatternBegin = "%\\{\\+";

    final String patternEnd = "}";

    final String patternRegex = "%\\{[^}]+}";

    final String fieldPatternBegin = "%\\{";

    final String unknownFieldReplacement = "NA";

    String indexPattern;

    IndexPattern[] patterns;

    String[] cache;

    public ElasticSearchIndexFormatter(String indexPattern) {
        Pattern pattern = Pattern.compile(patternRegex);
        Matcher matcher = pattern.matcher(indexPattern);
        if (matcher.find()) {
            List<String> patternList = new ArrayList<>();
            patternList.add(matcher.group());
            while (matcher.find()) {
                patternList.add(matcher.group());
            }
            String[] strPatterns = patternList.toArray(new String[patternList.size()]);
            patterns = new IndexPattern[strPatterns.length];
            cache = new String[patterns.length];
            for (int i = 0; i < strPatterns.length; i++) {
                if (strPatterns[i].contains("%{+")) {
                    patterns[i] = new DateTimeIndexPattern(strPatterns[i]
                            .replaceAll(datePatternBegin, "")
                            .replaceAll(patternEnd, "")
                    );
                } else {
                    patterns[i] = new FieldIndexPattern(strPatterns[i]
                            .replaceAll(fieldPatternBegin, "")
                            .replaceAll(patternEnd, "")
                    );
                }
            }
        }
        this.indexPattern = indexPattern.replaceAll(patternRegex, "%s");
    }

    public String getIndex(Map element, DateTime dt) {
        if (patterns != null && patterns.length > 0) {
            for (int i = 0; i < patterns.length; i++) {
                cache[i] = patterns[i].resolve(element, dt);
            }
            return String.format(indexPattern, cache);
        } else {
            return this.indexPattern;
        }
    }

    interface IndexPattern extends Serializable {
        String resolve(Map element, DateTime dt);
    }

    class DateTimeIndexPattern implements IndexPattern {

        String dtPattern;

        DateTimeIndexPattern(String dtPattern) {
            this.dtPattern = dtPattern;
        }

        @Override
        public String resolve(Map element, DateTime dt) {
            return dt.toString(dtPattern);
        }
    }

    class FieldIndexPattern implements IndexPattern {
        String fieldName;

        FieldIndexPattern(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public String resolve(Map element, DateTime dt) {
            return element.getOrDefault(fieldName, unknownFieldReplacement).toString();
        }
    }
}
