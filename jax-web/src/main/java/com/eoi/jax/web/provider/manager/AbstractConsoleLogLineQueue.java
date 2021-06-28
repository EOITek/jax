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

package com.eoi.jax.web.provider.manager;

import cn.hutool.core.collection.CollUtil;
import com.eoi.jax.manager.api.UUID;
import com.eoi.jax.manager.process.LineHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class AbstractConsoleLogLineQueue<T> implements LineHandler {
    private BlockingQueue<T> queue = new LinkedBlockingQueue<>(100000);
    private static final Logger logger = LoggerFactory.getLogger(ConsoleLogLineQueue.class);
    private static final Long RETENTION_MS = 24 * 3600 * 1000L;

    @Override
    public void handleLine(UUID uuid, String line) {
        try {
            T console = console(uuid, line);
            if (console == null) {
                return;
            }
            put(console);
        } catch (Exception e) {
            logger.warn("console log error", e);
        }
    }

    public void persist() {
        try {
            while (!queue.isEmpty()) {
                List<T> batch = new ArrayList<>();
                while (!queue.isEmpty() && batch.size() <= 1000) {
                    T console = take();
                    if (console == null) {
                        break;
                    }
                    batch.add(console);
                }
                if (CollUtil.isNotEmpty(batch)) {
                    saveBatch(batch);
                }
            }
        } catch (Exception e) {
            logger.warn("persist console log error", e);
        }
    }

    public void retention() {
        Long limit = System.currentTimeMillis() - RETENTION_MS;
        removeBeforeTimestamp(limit);
    }

    private T take() throws Exception {
        //尽量有值的情况下取值，没有值直接返回null
        if (queue.isEmpty()) {
            return null;
        }
        return queue.poll(1, TimeUnit.SECONDS);
    }

    private void put(T log) throws Exception {
        //尽量在有容的情况下插入，没容量直接丢弃
        if (queue.remainingCapacity() > 0) {
            queue.offer(log, 1, TimeUnit.SECONDS);
        }
    }

    abstract T console(UUID uuid, String line);

    abstract void saveBatch(List<T> batch);

    abstract void removeBeforeTimestamp(Long timestamp);
}
