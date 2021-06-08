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

package com.eoi.jax.common.dissect;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DissectContext implements Serializable {

    private static final Pattern LEADING_DELIMITER_PATTERN = Pattern.compile("^(.*?)%");
    private static final Pattern KEY_DELIMITER_FIELD_PATTERN = Pattern.compile("%\\{([^}]*?)}([^%]*)", Pattern.DOTALL);
    private static final EnumSet<DissectKey.Modifier> ASSOCIATE_MODIFIERS = EnumSet.of(
            DissectKey.Modifier.FIELD_NAME,
            DissectKey.Modifier.FIELD_VALUE);
    private static final EnumSet<DissectKey.Modifier> APPEND_MODIFIERS = EnumSet.of(
            DissectKey.Modifier.APPEND,
            DissectKey.Modifier.APPEND_WITH_ORDER);
    private static final Function<DissectPair, String> KEY_NAME = val -> val.getKey().getName();


    private List<DissectPair> matchPairs;
    private String appendSeparator;
    private int maxMatches;
    private int maxResults;
    private int appendCount;
    private int referenceCount;
    private String leadingDelimiter;

    public List<DissectPair> getMatchPairs() {
        return matchPairs;
    }

    public void setMatchPairs(List<DissectPair> matchPairs) {
        this.matchPairs = matchPairs;
    }

    public String getAppendSeparator() {
        return appendSeparator;
    }

    public void setAppendSeparator(String appendSeparator) {
        this.appendSeparator = appendSeparator;
    }

    public int getMaxMatches() {
        return maxMatches;
    }

    public void setMaxMatches(int maxMatches) {
        this.maxMatches = maxMatches;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getAppendCount() {
        return appendCount;
    }

    public void setAppendCount(int appendCount) {
        this.appendCount = appendCount;
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(int referenceCount) {
        this.referenceCount = referenceCount;
    }

    public String getLeadingDelimiter() {
        return leadingDelimiter;
    }

    public void setLeadingDelimiter(String leadingDelimiter) {
        this.leadingDelimiter = leadingDelimiter;
    }

    public Map<String, String> doDissect(String inputString) {
        DissectMatch dissectMatch = new DissectMatch(
                this.appendSeparator, this.maxMatches, this.maxResults, this.appendCount, this.referenceCount);
        Iterator<DissectPair> it = this.matchPairs.iterator();
        //ensure leading delimiter matches
        if (inputString != null && inputString.length() > this.leadingDelimiter.length()
                && this.leadingDelimiter.equals(inputString.substring(0, this.leadingDelimiter.length()))) {
            byte[] input = inputString.getBytes(StandardCharsets.UTF_8);
            //grab the first key/delimiter pair
            DissectPair dissectPair = it.next();
            DissectKey key = dissectPair.getKey();
            byte[] delimiter = dissectPair.getDelimiter().getBytes(StandardCharsets.UTF_8);
            //start dissection after the first delimiter
            int i = this.leadingDelimiter.length();
            int valueStart = i;
            int lookAheadMatches;
            //start walking the input string byte by byte, look ahead for matches where needed
            //if a match is found jump forward to the end of the match
            for (; i < input.length; i++) {
                lookAheadMatches = 0;
                //potential match between delimiter and input string
                if (delimiter.length > 0 && input[i] == delimiter[0]) {
                    //look ahead to see if the entire delimiter matches the input string
                    for (int j = 0; j < delimiter.length; j++) {
                        if (i + j < input.length && input[i + j] == delimiter[j]) {
                            lookAheadMatches++;
                        }
                    }
                    //found a full delimiter match
                    if (lookAheadMatches == delimiter.length) {
                        //record the key/value tuple
                        byte[] value = Arrays.copyOfRange(input, valueStart, i);
                        dissectMatch.add(key, new String(value, StandardCharsets.UTF_8));
                        //jump to the end of the match
                        i += lookAheadMatches;
                        //look for consecutive delimiters (e.g. a,,,,d,e)
                        while (i < input.length) {
                            lookAheadMatches = 0;
                            for (int j = 0; j < delimiter.length; j++) {
                                if (i + j < input.length && input[i + j] == delimiter[j]) {
                                    lookAheadMatches++;
                                }
                            }
                            //found consecutive delimiters
                            if (lookAheadMatches == delimiter.length) {
                                //jump to the end of the match
                                i += lookAheadMatches;
                                if (!key.skipRightPadding()) {
                                    //progress the keys/delimiter if possible
                                    if (!it.hasNext()) {
                                        break; //the while loop
                                    }
                                    dissectPair = it.next();
                                    key = dissectPair.getKey();
                                    //add the key with an empty value for the empty delimiter
                                    dissectMatch.add(key, "");
                                }
                            } else {
                                break; //the while loop
                            }
                        }
                        //progress the keys/delimiter if possible
                        if (!it.hasNext()) {
                            break; //the for loop
                        }
                        dissectPair = it.next();
                        key = dissectPair.getKey();
                        delimiter = dissectPair.getDelimiter().getBytes(StandardCharsets.UTF_8);
                        //i is always one byte after the last found delimiter, aka the start of the next value
                        valueStart = i;
                    }
                }
            }
            //the last key, grab the rest of the input (unless consecutive delimiters already grabbed the last key)
            //and there is no trailing delimiter
            if (!dissectMatch.fullyMatched() && delimiter.length == 0) {
                byte[] value = Arrays.copyOfRange(input, valueStart, input.length);
                String valueString = new String(value, StandardCharsets.UTF_8);
                dissectMatch.add(key, valueString);
            }
        }
        return dissectMatch.getResults();
    }

    public static DissectContext parseDissectContext(String pattern, String appendSeparator) {
        Matcher matcher = LEADING_DELIMITER_PATTERN.matcher(pattern);
        String leadingDelimiter = "";
        while (matcher.find()) {
            leadingDelimiter = matcher.group(1);
        }
        List<DissectPair> matchPairs = new ArrayList<>();
        matcher = KEY_DELIMITER_FIELD_PATTERN.matcher(pattern.substring(leadingDelimiter.length()));
        while (matcher.find()) {
            DissectKey key = new DissectKey(matcher.group(1));
            String delimiter = matcher.group(2);
            matchPairs.add(new DissectPair(key, delimiter));
        }
        int maxMatches = matchPairs.size();
        int maxResults = Long.valueOf(matchPairs.stream()
                .filter(dissectPair -> !dissectPair.getKey().skip()).map(KEY_NAME).distinct().count()).intValue();
        if (maxMatches == 0 || maxResults == 0) {
            throw new DissectException.PatternParse(pattern, "Unable to find any keys or delimiters.");
        }
        //append validation - look through all of the keys to see if there are any keys that need to participate in an append operation
        // but don't have the '+' defined
        Set<String> appendKeyNames = matchPairs.stream()
                .filter(dissectPair -> APPEND_MODIFIERS.contains(dissectPair.getKey().getModifier()))
                .map(KEY_NAME).distinct().collect(Collectors.toSet());
        if (!appendKeyNames.isEmpty()) {
            List<DissectPair> modifiedMatchPairs = new ArrayList<>(matchPairs.size());
            for (DissectPair p : matchPairs) {
                if (p.getKey().getModifier().equals(DissectKey.Modifier.NONE) && appendKeyNames.contains(p.getKey().getName())) {
                    modifiedMatchPairs.add(new DissectPair(new DissectKey(p.getKey(), DissectKey.Modifier.APPEND), p.getDelimiter()));
                } else {
                    modifiedMatchPairs.add(p);
                }
            }
            matchPairs = modifiedMatchPairs;
        }
        int appendCount = appendKeyNames.size();

        //reference validation - ensure that '*' and '&' come in pairs
        Map<String, List<DissectPair>> referenceGroupings = matchPairs.stream()
                .filter(dissectPair -> ASSOCIATE_MODIFIERS.contains(dissectPair.getKey().getModifier()))
                .collect(Collectors.groupingBy(KEY_NAME));
        for (Map.Entry<String, List<DissectPair>> entry : referenceGroupings.entrySet()) {
            if (entry.getValue().size() != 2) {
                throw new DissectException.PatternParse(pattern, "Found invalid key/reference associations: '"
                        + entry.getValue().stream().map(KEY_NAME).collect(Collectors.joining(","))
                        + "' Please ensure each '*<key>' is matched with a matching '&<key>");
            }
        }

        int referenceCount = referenceGroupings.size() * 2;
        DissectContext dissectContext = new DissectContext();
        dissectContext.leadingDelimiter = leadingDelimiter;
        dissectContext.matchPairs = Collections.unmodifiableList(matchPairs);
        dissectContext.appendSeparator = appendSeparator;
        dissectContext.maxMatches = maxMatches;
        dissectContext.maxResults = maxResults;
        dissectContext.appendCount = appendCount;
        dissectContext.referenceCount = referenceCount;
        return dissectContext;
    }
}
