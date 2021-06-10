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

import com.eoi.jax.api.annotation.DataType;
import com.eoi.jax.api.annotation.model.JobParamMeta;
import com.eoi.jax.core.JobMeta;
import com.eoi.jax.core.JobMetaScanner;
import org.junit.Assert;
import org.junit.Test;

public class JobMetaScannerTest {

    @Test
    public void test_JobMetaScanner_1() throws Throwable {
        JobMeta jm = JobMetaScanner.getJobMetaByClass(TestProcessJob.class);
        Assert.assertEquals("TestProcessJob", jm.jobInfo.getName());
        Assert.assertEquals(1, jm.inTypes.size());
        Assert.assertEquals("org.apache.flink.streaming.api.datastream.DataStream", jm.inTypes.get(0).raw);
        Assert.assertEquals(1, jm.inTypes.get(0).parameterizedTypes.size());
        Assert.assertEquals("java.util.Map<java.lang.String, java.lang.Object>", jm.inTypes.get(0).parameterizedTypes.get(0));
        Assert.assertEquals("org.apache.flink.streaming.api.datastream.DataStream", jm.outTypes.get(0).raw);
    }

    @Test
    public void test_JobMetaScanner_2() throws Throwable {
        JobMeta jm = JobMetaScanner.getJobMetaByClass(TestProcessJob2.class);
        Assert.assertEquals("TestProcessJob2", jm.jobInfo.getName());
        Assert.assertEquals(1, jm.inTypes.size());
        Assert.assertEquals("org.apache.flink.streaming.api.datastream.DataStream", jm.inTypes.get(0).raw);
        Assert.assertEquals(1, jm.inTypes.get(0).parameterizedTypes.size());
        Assert.assertEquals("java.lang.Long", jm.inTypes.get(0).parameterizedTypes.get(0));
        Assert.assertEquals("org.apache.flink.streaming.api.datastream.DataStream", jm.outTypes.get(0).raw);
        Assert.assertEquals(8, jm.jobParameters.size());

        JobParamMeta param1 = jm.jobParameters.get(0);
        Assert.assertEquals("first", param1.getName());
        Assert.assertEquals("first2", param1.getLabel());

        JobParamMeta param2 = jm.jobParameters.get(1);
        Assert.assertEquals("second", param2.getName());
        Assert.assertEquals("second", param2.getLabel());

        JobParamMeta param3 = jm.jobParameters.get(2);
        Assert.assertEquals("third", param3.getName());
        Assert.assertEquals("third", param3.getLabel());

        JobParamMeta param4 = jm.jobParameters.get(3);
        Assert.assertEquals("fourth", param4.getName());
        Assert.assertEquals("fourth", param4.getLabel());

        JobParamMeta param5 = jm.jobParameters.get(4);
        Assert.assertEquals("p33", param5.getName());
        Assert.assertEquals("p3", param5.getLabel());
        Assert.assertEquals(DataType.LONG, param5.getType()[0]);
        Assert.assertTrue(param5.getOptional());

        JobParamMeta param6 = jm.jobParameters.get(5);
        Assert.assertEquals("p4", param6.getName());
        Assert.assertEquals("p4", param6.getLabel());
        Assert.assertEquals(DataType.DOUBLE, param6.getType()[0]);
        Assert.assertTrue(param6.getOptional());

        JobParamMeta param7 = jm.jobParameters.get(6);
        Assert.assertEquals("p1", param7.getName());
        Assert.assertEquals("p1", param7.getLabel());
        Assert.assertEquals(DataType.STRING, param7.getType()[0]);
        Assert.assertTrue(param7.getOptional());

        JobParamMeta param8 = jm.jobParameters.get(7);
        Assert.assertEquals("p2", param8.getName());
        Assert.assertEquals("p2", param8.getLabel());
        Assert.assertEquals(DataType.LONG, param8.getType()[0]);
        Assert.assertTrue(param8.getOptional());
    }
}
