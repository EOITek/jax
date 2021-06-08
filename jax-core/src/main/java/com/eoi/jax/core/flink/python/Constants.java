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

package com.eoi.jax.core.flink.python;

public class Constants {
    public static final int DATA_FEED = 0xf4f4; // feed data to python process
    public static final int DATA_FEED_SNAPSHOT = 0xf3f3; // feed data to python process and take snapshot of state
    public static final int DATA_RET = 0xf5f5; // data return
    public static final int STATE_REQ = 0xdada; // request state from python process
    public static final int STATE_FEED = 0xe4e4; // feed state data to python process
    public static final int STATE_UPDATE = 0xe5e5; // request snapshot of state from python process

    public static final int TRANSITION_STATE_START = 0;
    public static final int TRANSITION_STATE_WAIT = 1;
    public static final int TRANSITION_STATE_STATE_FEED = 2;
    public static final int TRANSITION_STATE_STATE_UPDATE = 3;
    public static final int TRANSITION_STATE_DATA_RET = 4;
}
