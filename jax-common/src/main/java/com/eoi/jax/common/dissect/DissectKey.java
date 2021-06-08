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

import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DissectKey {

    private static final Pattern LEFT_MODIFIER_PATTERN = Pattern.compile("([+*&?])(.*?)(->)?$", Pattern.DOTALL);
    private static final Pattern RIGHT_PADDING_PATTERN = Pattern.compile("^(.*?)(->)?$", Pattern.DOTALL);
    private static final Pattern APPEND_WITH_ORDER_PATTERN = Pattern.compile("[+](.*?)(/)([0-9]+)(->)?$", Pattern.DOTALL);
    private final Modifier modifier;
    private boolean skip;
    private boolean skipRightPadding;
    private int appendPosition;
    private String name;


    public DissectKey(String key) {
        skip = key == null || key.isEmpty();
        modifier = Modifier.findModifier(key);
        switch (modifier) {
            case NONE:
                Matcher matcher = RIGHT_PADDING_PATTERN.matcher(key);
                while (matcher.find()) {
                    name = matcher.group(1);
                    skipRightPadding = matcher.group(2) != null;
                }
                skip = name.isEmpty();
                break;
            case NAMED_SKIP:
                matcher = LEFT_MODIFIER_PATTERN.matcher(key);
                while (matcher.find()) {
                    name = matcher.group(2);
                    skipRightPadding = matcher.group(3) != null;
                }
                skip = true;
                break;
            case APPEND:
                matcher = LEFT_MODIFIER_PATTERN.matcher(key);
                while (matcher.find()) {
                    name = matcher.group(2);
                    skipRightPadding = matcher.group(3) != null;
                }
                break;
            case FIELD_NAME:
                matcher = LEFT_MODIFIER_PATTERN.matcher(key);
                while (matcher.find()) {
                    name = matcher.group(2);
                    skipRightPadding = matcher.group(3) != null;
                }
                break;
            case FIELD_VALUE:
                matcher = LEFT_MODIFIER_PATTERN.matcher(key);
                while (matcher.find()) {
                    name = matcher.group(2);
                    skipRightPadding = matcher.group(3) != null;
                }
                break;
            case APPEND_WITH_ORDER:
                matcher = APPEND_WITH_ORDER_PATTERN.matcher(key);
                while (matcher.find()) {
                    name = matcher.group(1);
                    appendPosition = Short.valueOf(matcher.group(3));
                    skipRightPadding = matcher.group(4) != null;
                }
                break;
            default:
                break;
        }

        if (name == null || (name.isEmpty() && !skip)) {
            throw new DissectException.KeyParse(key, "The key name could be determined");
        }
    }

    /**
     * Copy constructor to explicitly override the modifier.
     *
     * @param key      The key to copy (except for the modifier)
     * @param modifier the modifer to use for this copy
     */
    public DissectKey(DissectKey key, Modifier modifier) {
        this.modifier = modifier;
        this.skipRightPadding = key.skipRightPadding;
        this.skip = key.skip;
        this.name = key.name;
        this.appendPosition = key.appendPosition;
    }

    public Modifier getModifier() {
        return modifier;
    }

    public boolean skip() {
        return skip;
    }

    public boolean skipRightPadding() {
        return skipRightPadding;
    }

    int getAppendPosition() {
        return appendPosition;
    }

    public String getName() {
        return name;
    }

    public enum Modifier {
        NONE(""), APPEND_WITH_ORDER("/"), APPEND("+"), FIELD_NAME("*"), FIELD_VALUE("&"), NAMED_SKIP("?");

        private static final Pattern MODIFIER_PATTERN = Pattern.compile("[/+*&?]");

        private final String modifier;

        @Override
        public String toString() {
            return modifier;
        }

        Modifier(final String modifier) {
            this.modifier = modifier;
        }

        //package private for testing
        static Modifier fromString(String modifier) {
            return EnumSet.allOf(Modifier.class).stream().filter(km -> km.modifier.equals(modifier))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Found invalid modifier.")); //throw should never happen
        }

        private static Modifier findModifier(String key) {
            Modifier modifier = Modifier.NONE;
            if (key != null && !key.isEmpty()) {
                Matcher matcher = MODIFIER_PATTERN.matcher(key);
                int matches = 0;
                while (matcher.find()) {
                    Modifier priorModifier = modifier;
                    modifier = Modifier.fromString(matcher.group());
                    if (++matches > 1 && !(APPEND.equals(priorModifier) && APPEND_WITH_ORDER.equals(modifier))) {
                        throw new DissectException.KeyParse(key, "multiple modifiers are not allowed.");
                    }
                }
            }
            return modifier;
        }
    }
}
