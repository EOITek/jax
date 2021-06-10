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

package com.eoi.jax.common.test;

import com.eoi.jax.common.VersionUtil;
import org.junit.Assert;
import org.junit.Test;

public class VersionUtilTest {

    @Test
    public void matchTest() {
        Assert.assertEquals(true, VersionUtil.match("= 1.9.1", "1.9.1"));
        Assert.assertEquals(false,VersionUtil.match("=1.9.1", "1.9.2"));
        Assert.assertEquals(true,VersionUtil.match("1.9.1", "1.9.1"));
        Assert.assertEquals(false,VersionUtil.match("!= 1.9.1", "1.9.1"));
        Assert.assertEquals(true,VersionUtil.match("!= 1.9.1", "1.9.2"));
        Assert.assertEquals(false,VersionUtil.match(">1.9.1", "1.9.1"));
        Assert.assertEquals(true,VersionUtil.match(">1.9.1", "1.9.2"));
        Assert.assertEquals(true,VersionUtil.match(">1.9.1", "1.10"));
        Assert.assertEquals(true,VersionUtil.match(">1.9.1", "2"));
        Assert.assertEquals(false,VersionUtil.match(">=1.9.1", "1.9.0"));
        Assert.assertEquals(true,VersionUtil.match(">=1.9.1", "1.9.1"));
        Assert.assertEquals(true,VersionUtil.match(">=1.9.1", "1.9.2"));
        Assert.assertEquals(false,VersionUtil.match("<1.9.1", "1.9.2"));
        Assert.assertEquals(true,VersionUtil.match("<1.9.1", "1.9.0"));
        Assert.assertEquals(true,VersionUtil.match("<1.9.1", "1.8"));
        Assert.assertEquals(true,VersionUtil.match("<1.9.1", "1"));
        Assert.assertEquals(false,VersionUtil.match("<=1.9.1", "1.9.2"));
        Assert.assertEquals(true,VersionUtil.match("<=1.9.1", "1.9.1"));
        Assert.assertEquals(true,VersionUtil.match("<=1.9.1", "1.9.0"));
        Assert.assertEquals(true,VersionUtil.match("~>1.9", "1.9.0"));
        Assert.assertEquals(true,VersionUtil.match("~>1.9", "1.9.1"));
        Assert.assertEquals(false,VersionUtil.match("~>1.9", "1.8.0"));
        Assert.assertEquals(false,VersionUtil.match("~>1.9", "2"));
        Assert.assertEquals(true,VersionUtil.match("~>1", "1.9.0"));
        Assert.assertEquals(true,VersionUtil.match("~>1", "1.9"));
        Assert.assertEquals(false,VersionUtil.match("~>1.9", "2"));
        Assert.assertEquals(true,VersionUtil.match("*", String.valueOf(System.currentTimeMillis())));
        Assert.assertEquals(false,VersionUtil.match("abc", "1.9.1"));
        Assert.assertEquals(false,VersionUtil.match("", "1.9.1"));
        Assert.assertEquals(true,VersionUtil.match(">=1.11", "1.11.0"));

    }

    @Test
    public void containsTest() {
        Assert.assertEquals(false,VersionUtil.validate("[[\"~> 1\",\"!= 1.9.2\",\">= 1.9.2\"],[\"~> 2\"]]", "1.9.2"));
        Assert.assertEquals(true,VersionUtil.validate("[[\"~> 1\",\">= 1.9.2\"],[\"~> 2\"]]", "1.9.2"));
        Assert.assertEquals(true,VersionUtil.validate("[[\"~> 1\",\">= 1.9.2\"],[\"~> 2\"]]", "2.9.2"));
        Assert.assertEquals(false,VersionUtil.validate("[]", "2.9.2"));
        Assert.assertEquals(false,VersionUtil.validate("", "2.9.2"));
    }
}
