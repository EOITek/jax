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

package com.eoi.jax.core.spark.python;

import java.util.concurrent.CountDownLatch;

public class PythonSync {

    private final CountDownLatch countDownLatch;

    public PythonSync() {
        countDownLatch = new CountDownLatch(1);
    }

    public void await() throws InterruptedException {
        countDownLatch.await();
    }

    public Object release() {
        countDownLatch.countDown();
        return null;
    }
}
