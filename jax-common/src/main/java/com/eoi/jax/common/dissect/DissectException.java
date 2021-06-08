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

/**
 * Parent class for all dissect related exceptions. Consumers may catch this exception or more specific child exceptions.
 */
public abstract class DissectException extends RuntimeException {

    DissectException(String message) {
        super(message);
    }

    /**
     * Error while parsing a dissect pattern
     */
    public static class PatternParse extends DissectException {

        public PatternParse(String pattern, String reason) {
            super("Unable to parse pattern: " + pattern + " Reason: " + reason);
        }
    }

    /**
     * Error while parsing a dissect key
     */
    public static class KeyParse extends DissectException {

        public KeyParse(String key, String reason) {
            super("Unable to parse key: " + key + " Reason: " + reason);
        }
    }

    /**
     * Unable to find a match between pattern and source string
     */
    public static class FindMatch extends DissectException {

        public FindMatch(String pattern, String source) {
            super("Unable to find match for dissect pattern: " + pattern + " against source: " + source);

        }
    }
}
