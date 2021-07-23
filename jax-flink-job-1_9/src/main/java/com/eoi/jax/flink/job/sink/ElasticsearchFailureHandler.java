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

package com.eoi.jax.flink.job.sink;

import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch.util.RetryRejectedExecutionFailureHandler;
import org.apache.flink.util.ExceptionUtils;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.common.util.concurrent.EsRejectedExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ElasticsearchFailureHandler extends RetryRejectedExecutionFailureHandler {
    private static Logger logger = LoggerFactory.getLogger("ElasticsearchFailureHandler");

    private Set<String> shouldThrowException;

    public ElasticsearchFailureHandler(List<String> shouldThrowExMessages) {
        shouldThrowException = new HashSet<>();
        shouldThrowException.add("Invalid index");

        if (shouldThrowExMessages != null && !shouldThrowExMessages.isEmpty()) {
            for (String shouldThrowExMessage : shouldThrowExMessages) {
                shouldThrowException.add(shouldThrowExMessage);
            }
        }
    }

    @Override
    public void onFailure(ActionRequest action, Throwable failure, int restStatusCode, RequestIndexer indexer) throws Throwable {
        if (ExceptionUtils.findThrowable(failure, EsRejectedExecutionException.class).isPresent()) {
            indexer.add(new ActionRequest[]{action});
        } else if (shouldThrow(failure.toString())) {
            logger.error("sink es get error to throw: resultCode: " + restStatusCode + ", failure: " + failure.toString() + ", action: " + action.toString());
            throw failure;
        } else {
            logger.error("sink es get error: resultCode: " + restStatusCode + ", failure: " + failure.toString() + ", action: " + action.toString());
            return;
        }
    }

    private boolean shouldThrow(String message) {
        return shouldThrowException.stream().filter(keyword -> message.contains(keyword)).count() > 0;
    }
}
