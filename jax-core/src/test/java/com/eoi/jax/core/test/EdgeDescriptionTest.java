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

import com.eoi.jax.core.entry.EdgeDescription;
import org.junit.Assert;
import org.junit.Test;

public class EdgeDescriptionTest {
    @Test
    public void edgeDescriptionTest_1() {
        Assert.assertEquals(0, EdgeDescription.split("1[0]").f1.intValue());
        Assert.assertEquals("1", EdgeDescription.split("1[0]").f0);
        Assert.assertNull(EdgeDescription.split("1[a]"));
        Assert.assertNull(EdgeDescription.split("1[0"));
        Assert.assertNull(EdgeDescription.split("10]"));
        Assert.assertNull(EdgeDescription.split("[0]1"));
    }

    @Test
    public void edgeDescriptionTest_2() {
        EdgeDescription edgeDescription = new EdgeDescription();
        edgeDescription.setFrom("1[0]");
        edgeDescription.setTo("2[1]");
        Assert.assertEquals("1", edgeDescription.getFrom());
        Assert.assertEquals("2", edgeDescription.getTo());
        Assert.assertEquals(0, edgeDescription.getFromSlot().intValue());
        Assert.assertEquals(1, edgeDescription.getToSlot().intValue());
    }
}
