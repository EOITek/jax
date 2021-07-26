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

import org.apache.flink.util.OutputTag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OutputTagsWrapper implements Serializable {
    private List<OutputTag<Map<String, Object>>> outputTags;

    public List<OutputTag<Map<String, Object>>> getOutputTags() {
        return outputTags;
    }

    public void setOutputTags(List<OutputTag<Map<String, Object>>> outputTags) {
        this.outputTags = outputTags;
    }

    public OutputTagsWrapper(int tagCount) {
        outputTags = new ArrayList<>();
        for (int i = 0; i < tagCount; i++) {
            OutputTag<Map<String, Object>> outputTag = new OutputTag<Map<String, Object>>(String.valueOf(i)){};
            outputTags.add(outputTag);
        }
    }
}
