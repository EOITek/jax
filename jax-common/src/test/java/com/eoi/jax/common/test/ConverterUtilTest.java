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

import com.eoi.jax.common.ConverterUtil;
import org.junit.Assert;
import org.junit.Test;

public class ConverterUtilTest {

    @Test
    public void testBoolean() {
        Assert.assertEquals(true, ConverterUtil.convert(true, "boolean"));
        Assert.assertEquals(false, ConverterUtil.convert(false, "boolean"));
        Assert.assertEquals(true, ConverterUtil.convert("true", "boolean"));
        Assert.assertEquals(false, ConverterUtil.convert("false", "boolean"));
        Assert.assertEquals(true, ConverterUtil.convert("1", "boolean"));
        Assert.assertEquals(false, ConverterUtil.convert("0", "boolean"));
        Assert.assertEquals(true, ConverterUtil.convert("yes", "boolean"));
        Assert.assertEquals(false, ConverterUtil.convert("no", "boolean"));
    }

    @Test
    public void testDouble() {
        Assert.assertEquals(1.222222D, ConverterUtil.convert(1.222222D, "double"));
        Assert.assertEquals(1.333333D, ConverterUtil.convert(1.333333F, "double"));
        Assert.assertEquals(1234567890123456789D, ConverterUtil.convert(1234567890123456789L, "double"));
        Assert.assertEquals(1234567890D, ConverterUtil.convert(1234567890, "double"));
        Assert.assertEquals(1.222222D, ConverterUtil.convert("1.222222", "double"));
        Assert.assertEquals(1.333333D, ConverterUtil.convert("1.333333", "double"));
        Assert.assertEquals(1234567890123456789D, ConverterUtil.convert("1234567890123456789", "double"));
        Assert.assertEquals(1234567890D, ConverterUtil.convert("1234567890", "double"));
    }

    @Test
    public void testFloat() {
        Assert.assertEquals(1.222222F, ConverterUtil.convert(1.222222D, "float"));
        Assert.assertEquals(1.333333F, ConverterUtil.convert(1.333333F, "float"));
        Assert.assertEquals(1234567890123456789F, ConverterUtil.convert(1234567890123456789L, "float"));
        Assert.assertEquals(1234567890F, ConverterUtil.convert(1234567890, "float"));
        Assert.assertEquals(1.222222F, ConverterUtil.convert("1.222222", "float"));
        Assert.assertEquals(1.333333F, ConverterUtil.convert("1.333333", "float"));
        Assert.assertEquals(1234567890123456789F, ConverterUtil.convert("1234567890123456789", "float"));
        Assert.assertEquals(1234567890F, ConverterUtil.convert("1234567890", "float"));
    }

    @Test
    public void testInteger() {
        Assert.assertEquals(1234567890, ConverterUtil.convert(1234567890D, "integer"));
        Assert.assertEquals(12345678, ConverterUtil.convert(12345678F, "integer"));
        Assert.assertEquals(1234567892, ConverterUtil.convert(1234567892L, "integer"));
        Assert.assertEquals(1234567893, ConverterUtil.convert(1234567893, "integer"));
        Assert.assertEquals(1234567890, ConverterUtil.convert("1234567890", "integer"));
        Assert.assertEquals(1234567891, ConverterUtil.convert("1234567891", "integer"));
        Assert.assertEquals(1234567892, ConverterUtil.convert("1234567892", "integer"));
        Assert.assertEquals(1234567893, ConverterUtil.convert("1234567893", "integer"));
    }

    @Test
    public void testLong() {
        Assert.assertEquals(1L, ConverterUtil.convert(1D, "long"));
        Assert.assertEquals(2L, ConverterUtil.convert(2F, "long"));
        Assert.assertEquals(3L, ConverterUtil.convert(3L, "long"));
        Assert.assertEquals(4L, ConverterUtil.convert(4, "long"));
        Assert.assertEquals(1L, ConverterUtil.convert("1", "long"));
        Assert.assertEquals(2L, ConverterUtil.convert("2", "long"));
        Assert.assertEquals(3L, ConverterUtil.convert("3", "long"));
        Assert.assertEquals(4L, ConverterUtil.convert("4", "long"));
    }

    @Test
    public void testString() {
        Assert.assertEquals("1", ConverterUtil.convert(1D, "string"));
        Assert.assertEquals("1.111111111111111", ConverterUtil.convert(1.111111111111111D, "string"));
        Assert.assertEquals("2.222222", ConverterUtil.convert(2.222222F, "string"));
        Assert.assertEquals("3333333333333", ConverterUtil.convert(3333333333333L, "string"));
        Assert.assertEquals("444444444", ConverterUtil.convert(444444444, "string"));
        Assert.assertEquals("123456asdassd", ConverterUtil.convert("123456asdassd", "string"));
    }
}
