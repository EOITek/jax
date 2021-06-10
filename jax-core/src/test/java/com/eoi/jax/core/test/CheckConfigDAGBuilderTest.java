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

package com.eoi.jax.core.test;

import com.eoi.jax.api.JobConfigValidationException;
import com.eoi.jax.core.CheckConfigDAGBuilder;
import com.eoi.jax.core.CheckConfigNode;
import com.eoi.jax.core.JobBuildException;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class CheckConfigDAGBuilderTest {

    static CheckConfigNode buildBaseNode(String id, Boolean invalid) {
        CheckConfigNode baseNode = new CheckConfigNode();
        baseNode.setId(id);
        baseNode.setEntry(TestJob.class.getName());
        baseNode.setConfig(new HashMap<String, Object>() {{
                put("invalid", invalid);
            }}
        );
        return baseNode;
    }

    static CheckConfigNode buildSourceNode(String id) {
        CheckConfigNode baseNode = new CheckConfigNode();
        baseNode.setId(id);
        baseNode.setEntry(TestSourceJob.class.getName());
        return baseNode;
    }

    @Test
    public void test_CheckConfigDAGBuilderTest_1() throws Throwable {
        CheckConfigDAGBuilder checkConfigDAGBuilder = new CheckConfigDAGBuilder();
        CheckConfigNode job1 = buildSourceNode("1");
        CheckConfigNode job2 = buildBaseNode("2", false);
        CheckConfigNode job3 = buildBaseNode("3", true);

        checkConfigDAGBuilder.putEdge(job1, job2);
        checkConfigDAGBuilder.putEdge(job2, job3);
        try {
            checkConfigDAGBuilder.check();
        } catch (JobBuildException ex) {
            Assert.assertEquals("3", ex.getInvalidJobId());
            Assert.assertTrue(ex.getCause() instanceof JobConfigValidationException);
        }
    }
}
