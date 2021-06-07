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

package com.eoi.jax.api.reflect.v2;

import com.eoi.jax.api.annotation.model.JobParamMeta;
import com.eoi.jax.api.reflect.JobParamReflector;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class ScanParamsTest {

    @Test
    public void scanParamsTest_1() {
        JobParamReflector reflector = new JobParamReflector();
        Map<String, JobParamMeta> map = reflector.scanParams(ConfigX.class);

        Assert.assertEquals("p1 == 'eoi'", map.get("p2").getAvailableCondition());
        Assert.assertEquals("p2!=''", map.get("p3").getAvailableCondition());
        Assert.assertEquals("p2!='' && (p3 != 'ok')", map.get("p4").getAvailableCondition());
        Assert.assertEquals("p2!='' && (p4!='')", map.get("p5").getAvailableCondition());
        Assert.assertEquals("p2!='' && (p4!='') && (p5!='end')", map.get("p6").getAvailableCondition());
    }
}
