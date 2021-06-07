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

import com.eoi.jax.api.annotation.DataType;
import com.eoi.jax.api.annotation.model.JobParamMeta;
import com.eoi.jax.api.reflect.ParamUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamUtilTest {

    @Test
    public void test_1() {
        List<JobParamMeta> list = ParamUtil.scanJobParams(Config1.class);
        Assert.assertEquals(13, list.size());

        JobParamMeta p1 = list.stream().filter(i -> "p1".equals(i.getName())).findAny().orElse(null);
        Assert.assertNotNull(p1);
        Assert.assertTrue(p1.getOptional());
        Assert.assertEquals("1", p1.getDefaultValue());
        Assert.assertEquals(DataType.STRING, p1.getType()[0]);

        JobParamMeta p2 = list.stream().filter(i -> "p2".equals(i.getName())).findAny().orElse(null);
        Assert.assertNotNull(p2);
        Assert.assertTrue(p2.getOptional());
        Assert.assertEquals("2", p2.getDefaultValue());
        Assert.assertEquals(DataType.INT, p2.getType()[0]);

        JobParamMeta p3 = list.stream().filter(i -> "p3".equals(i.getName())).findAny().orElse(null);
        Assert.assertNotNull(p3);
        Assert.assertTrue(p3.getOptional());
        Assert.assertEquals("3", p3.getDefaultValue());
        Assert.assertEquals(DataType.LONG, p3.getType()[0]);

        JobParamMeta p4 = list.stream().filter(i -> "p4".equals(i.getName())).findAny().orElse(null);
        Assert.assertNotNull(p4);
        Assert.assertTrue(p4.getOptional());
        Assert.assertEquals("4.4", p4.getDefaultValue());
        Assert.assertEquals(DataType.FLOAT, p4.getType()[0]);

        JobParamMeta p5 = list.stream().filter(i -> "p5".equals(i.getName())).findAny().orElse(null);
        Assert.assertNotNull(p5);
        Assert.assertTrue(p5.getOptional());
        Assert.assertEquals("5.5", p5.getDefaultValue());
        Assert.assertEquals(DataType.DOUBLE, p5.getType()[0]);

        JobParamMeta p6 = list.stream().filter(i -> "p6".equals(i.getName())).findAny().orElse(null);
        Assert.assertNotNull(p6);
        Assert.assertEquals("true", p6.getDefaultValue());
        Assert.assertEquals(DataType.BOOL, p6.getType()[0]);

        JobParamMeta p7 = list.stream().filter(i -> "p7".equals(i.getName())).findAny().orElse(null);
        Assert.assertNotNull(p7);
        Assert.assertTrue(p7.getOptional());
        Assert.assertEquals(DataType.LIST, p7.getType()[0]);
        Assert.assertEquals(DataType.STRING, p7.getListParameter().getType()[0]);

        JobParamMeta p8 = list.stream().filter(i -> "p8".equals(i.getName())).findAny().orElse(null);
        Assert.assertNotNull(p8);
        Assert.assertTrue(p8.getOptional());
        Assert.assertEquals(DataType.MAP, p8.getType()[0]);

        JobParamMeta p11 = list.stream().filter(i -> "p11".equals(i.getName())).findAny().orElse(null);
        Assert.assertNotNull(p11);
        Assert.assertTrue(p11.getOptional());
        Assert.assertEquals(DataType.LIST, p11.getType()[0]);
        Assert.assertEquals(DataType.LIST, p11.getListParameter().getType()[0]);
        Assert.assertEquals(DataType.STRING, p11.getListParameter().getListParameter().getType()[0]);

        JobParamMeta p12 = list.stream().filter(i -> "p12".equals(i.getName())).findAny().orElse(null);
        Assert.assertNotNull(p12);
        Assert.assertTrue(p12.getOptional());
        Assert.assertEquals(DataType.LIST, p12.getType()[0]);
        Assert.assertEquals(DataType.LIST, p12.getListParameter().getType()[0]);
        Assert.assertEquals(DataType.LIST, p12.getListParameter().getListParameter().getType()[0]);
        Assert.assertEquals(DataType.STRING, p12.getListParameter().getListParameter().getListParameter().getType()[0]);

        JobParamMeta config7 = list.stream().filter(i -> "config7".equals(i.getName())).findAny().orElse(null);
        Assert.assertNotNull(config7);
        test_2_assertJobParamMeta(config7);

        JobParamMeta config6 = list.stream().filter(i -> "config6".equals(i.getName())).findAny().orElse(null);
        Assert.assertNotNull(config6);
        test_3_assertConfig6(config6);

        JobParamMeta config5 = list.stream().filter(i -> "config5".equals(i.getName())).findAny().orElse(null);
        Assert.assertNotNull(config5);
        Assert.assertTrue(config5.getOptional());
        Assert.assertEquals(DataType.OBJECT, config5.getType()[0]);

        JobParamMeta p9 = Arrays.stream(config5.getObjectParameters()).filter(i -> "p9".equals(i.getName())).findAny()
                .orElse(null);
        Assert.assertNotNull(p9);
        Assert.assertTrue(p9.getOptional());
        Assert.assertEquals(DataType.STRING, p9.getType()[0]);

        JobParamMeta p10 = Arrays.stream(config5.getObjectParameters()).filter(i -> "p10".equals(i.getName())).findAny()
                .orElse(null);
        Assert.assertNotNull(p10);
        Assert.assertTrue(p10.getOptional());
        Assert.assertEquals(DataType.INT, p10.getType()[0]);

        test_1_assertConfig8(config5);

        JobParamMeta config9 = Arrays.stream(config5.getObjectParameters()).filter(i -> "config9".equals(i.getName()))
                .findAny().orElse(null);
        Assert.assertNotNull(config9);
        Assert.assertTrue(config9.getOptional());
        Assert.assertEquals(DataType.OBJECT, config9.getType()[0]);

        JobParamMeta p17 = Arrays.stream(config9.getObjectParameters()).filter(i -> "p17".equals(i.getName())).findAny()
                .orElse(null);
        Assert.assertNotNull(p17);
        Assert.assertTrue(p17.getOptional());
        Assert.assertEquals(DataType.LIST, p17.getType()[0]);
        Assert.assertEquals(DataType.OBJECT, p17.getListParameter().getType()[0]);
        test_1_assertConfig8(p17.getListParameter());

        JobParamMeta p18 = Arrays.stream(config9.getObjectParameters()).filter(i -> "p18".equals(i.getName())).findAny()
                .orElse(null);
        Assert.assertNotNull(p18);
        Assert.assertTrue(p18.getOptional());
        Assert.assertEquals(DataType.LIST, p18.getType()[0]);
        Assert.assertEquals(DataType.LIST, p18.getListParameter().getType()[0]);
        Assert.assertEquals(DataType.OBJECT, p18.getListParameter().getListParameter().getType()[0]);
        test_1_assertConfig8(p18.getListParameter().getListParameter());
    }

    @Test
    public void test_3() {
        Config1 config = new Config1();

        ParamUtil.configJobParams(config, new HashMap<>());

        Assert.assertEquals("1", config.getP1());
        Assert.assertEquals((Integer) 2, config.getP2());
        Assert.assertEquals((Long) 3L, config.getP3());
        Assert.assertEquals((Float) 4.4F, config.getP4());
    }

    @Test
    public void test_2() throws Exception {
        Config1 config = new Config1();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map2 = mapper.readValue("{\n"
                + "\t\"p1\": \"p1p1p1\",\n"
                + "\t\"p2\": 222,\n"
                + "\t\"p3\": 333,\n"
                + "\t\"config5\": {\n"
                + "\t\t\"p16\": [\n"
                + "\t\t\t[{\n"
                + "\t\t\t\t\"p14\": 141414.141414\n"
                + "\t\t\t}]\n"
                + "\t\t],\n"
                + "\t\t\"p15\": [{\n"
                + "\t\t\t\"p14\": 141414.141414\n"
                + "\t\t}],\n"
                + "\t\t\"config9\": {\n"
                + "\t\t\t\"p18\": [\n"
                + "\t\t\t\t[{\n"
                + "\t\t\t\t\t\"p16\": [\n"
                + "\t\t\t\t\t\t[{\n"
                + "\t\t\t\t\t\t\t\"p14\": 141414.141414\n"
                + "\t\t\t\t\t\t}]\n"
                + "\t\t\t\t\t],\n"
                + "\t\t\t\t\t\"p15\": [{\n"
                + "\t\t\t\t\t\t\"p14\": 141414.141414\n"
                + "\t\t\t\t\t}]\n"
                + "\t\t\t\t}]\n"
                + "\t\t\t],\n"
                + "\t\t\t\"p17\": [{\n"
                + "\t\t\t\t\"p16\": [\n"
                + "\t\t\t\t\t[{\n"
                + "\t\t\t\t\t\t\"p14\": 141414.141414\n"
                + "\t\t\t\t\t}]\n"
                + "\t\t\t\t],\n"
                + "\t\t\t\t\"p15\": [{\n"
                + "\t\t\t\t\t\"p14\": 141414.141414\n"
                + "\t\t\t\t}]\n"
                + "\t\t\t}]\n"
                + "\t\t},\n"
                + "\t\t\"p9\": \"p9p9p9\",\n"
                + "\t\t\"p10\": 101010\n"
                + "\t},\n"
                + "\t\"p4\": 444.444,\n"
                + "\t\"config6\": {\n"
                + "\t\t\"p12\": [\n"
                + "\t\t\t[\n"
                + "\t\t\t\t[\"p12-1\", \"p12-2\"],\n"
                + "\t\t\t\t[\"p12-3\", \"p12-4\"]\n"
                + "\t\t\t],\n"
                + "\t\t\t[\n"
                + "\t\t\t\t[\"p12-5\", \"p12-6\"],\n"
                + "\t\t\t\t[\"p12-7\", \"p12-8\"]\n"
                + "\t\t\t]\n"
                + "\t\t],\n"
                + "\t\t\"p11\": [\n"
                + "\t\t\t[\"p11-1\", \"p11-2\"],\n"
                + "\t\t\t[\"p11-3\", \"p11-4\"]\n"
                + "\t\t]\n"
                + "\t},\n"
                + "\t\"p5\": 555.555,\n"
                + "\t\"config7\": {\n"
                + "\t\t\"p14\": 141414.141414\n"
                + "\t},\n"
                + "\t\"p6\": false,\n"
                + "\t\"p7\": [\"p7-1\", \"p7-2\"],\n"
                + "\t\"p8\": {\n"
                + "\t\t\"p8-1\": \"p8-1v\",\n"
                + "\t\t\"p8-2\": \"p8-2v\"\n"
                + "\t},\n"
                + "\t\"p12\": [\n"
                + "\t\t[\n"
                + "\t\t\t[\"p12-1\", \"p12-2\"],\n"
                + "\t\t\t[\"p12-3\", \"p12-4\"]\n"
                + "\t\t],\n"
                + "\t\t[\n"
                + "\t\t\t[\"p12-5\", \"p12-6\"],\n"
                + "\t\t\t[\"p12-7\", \"p12-8\"]\n"
                + "\t\t]\n"
                + "\t],\n"
                + "\t\"p11\": [\n"
                + "\t\t[\"p11-1\", \"p11-2\"],\n"
                + "\t\t[\"p11-3\", \"p11-4\"]\n"
                + "\t]\n"
                + "}",  new TypeReference<Map<String, Object>>() { });

        ParamUtil.configJobParams(config, map2);

        Assert.assertEquals("p1p1p1", config.getP1());
        Assert.assertEquals((Integer) 222, config.getP2());
        Assert.assertEquals((Long) 333L, config.getP3());
        Assert.assertEquals((Float) 444.444F, config.getP4());

        test_2_assertConfig3(config.getConfig3());
        test_2_assertConfig4(config.getConfig4());
        test_2_assertConfig5(config.getConfig5());
        test_2_assertConfig6(config.getConfig6());
    }

    public void test_2_assertConfig3(Config3 config3) {
        Assert.assertEquals((Double) 555.555, config3.getP5());
        Assert.assertEquals(false, config3.getP6());
        test_2_assertConfig6(config3.getConfig6());
        test_2_assertConfig7(config3.getConfig7());
    }

    public void test_2_assertConfig4(Config4 config4) {
        Assert.assertEquals(2, config4.getP7().size());
        Assert.assertEquals("p7-1", config4.getP7().get(0));
        Assert.assertEquals("p7-2", config4.getP7().get(1));
        Assert.assertEquals(2, config4.getP8().size());
        Assert.assertEquals("p8-1v", config4.getP8().get("p8-1"));
        Assert.assertEquals("p8-2v", config4.getP8().get("p8-2"));
    }

    public void test_2_assertConfig5(Config5 config5) {
        Assert.assertEquals("p9p9p9", config5.getP9());
        Assert.assertEquals((Integer) 101010, config5.getP10());
        test_2_assertConfig8(config5.getConfig8());
        test_2_assertConfig9(config5.getConfig9());
    }

    public void test_2_assertConfig6(Config6 config6) {
        Assert.assertEquals(2, config6.getP11().size());
        Assert.assertEquals(2, config6.getP11().get(0).size());
        Assert.assertEquals("p11-1", config6.getP11().get(0).get(0));
        Assert.assertEquals("p11-2", config6.getP11().get(0).get(1));
        Assert.assertEquals(2, config6.getP11().get(1).size());
        Assert.assertEquals("p11-3", config6.getP11().get(1).get(0));
        Assert.assertEquals("p11-4", config6.getP11().get(1).get(1));

        Assert.assertEquals(2, config6.getP12().size());
        Assert.assertEquals(2, config6.getP12().get(0).size());
        Assert.assertEquals(2, config6.getP12().get(0).get(0).size());
        Assert.assertEquals("p12-1", config6.getP12().get(0).get(0).get(0));
        Assert.assertEquals("p12-2", config6.getP12().get(0).get(0).get(1));
        Assert.assertEquals(2, config6.getP12().get(0).get(1).size());
        Assert.assertEquals("p12-3", config6.getP12().get(0).get(1).get(0));
        Assert.assertEquals("p12-4", config6.getP12().get(0).get(1).get(1));
        Assert.assertEquals(2, config6.getP12().get(1).size());
        Assert.assertEquals(2, config6.getP12().get(1).get(0).size());
        Assert.assertEquals("p12-5", config6.getP12().get(1).get(0).get(0));
        Assert.assertEquals("p12-6", config6.getP12().get(1).get(0).get(1));
        Assert.assertEquals(2, config6.getP12().get(1).get(1).size());
        Assert.assertEquals("p12-7", config6.getP12().get(1).get(1).get(0));
        Assert.assertEquals("p12-8", config6.getP12().get(1).get(1).get(1));
    }

    public void test_2_assertConfig7(Config7 config7) {
        Assert.assertEquals((Double) 141414.141414, config7.getP14());
        Assert.assertEquals("p15", config7.getP15());
    }

    public void test_2_assertConfig8(Config8 config8) {
        Assert.assertEquals(1, config8.getP15().size());
        test_2_assertConfig7(config8.getP15().get(0));
        Assert.assertEquals(1, config8.getP16().size());
        Assert.assertEquals(1, config8.getP16().get(0).size());
        test_2_assertConfig7(config8.getP16().get(0).get(0));
    }

    public void test_2_assertConfig9(Config9 config9) {
        Assert.assertEquals(1, config9.getP17().size());
        test_2_assertConfig8(config9.getP17().get(0));
        Assert.assertEquals(1, config9.getP18().size());
        Assert.assertEquals(1, config9.getP18().get(0).size());
        test_2_assertConfig8(config9.getP18().get(0).get(0));
    }


    private void test_1_assertConfig8(JobParamMeta config8) {
        JobParamMeta p15 = Arrays.stream(config8.getObjectParameters()).filter(i -> "p15".equals(i.getName())).findAny()
                .orElse(null);
        Assert.assertNotNull(p15);
        Assert.assertTrue(p15.getOptional());
        Assert.assertEquals(DataType.LIST, p15.getType()[0]);
        Assert.assertEquals(DataType.OBJECT, p15.getListParameter().getType()[0]);
        test_2_assertJobParamMeta(p15.getListParameter());

        JobParamMeta p16 = Arrays.stream(config8.getObjectParameters()).filter(i -> "p16".equals(i.getName())).findAny()
                .orElse(null);
        Assert.assertNotNull(p16);
        Assert.assertTrue(p16.getOptional());
        Assert.assertEquals(DataType.LIST, p16.getType()[0]);
        Assert.assertEquals(DataType.LIST, p16.getListParameter().getType()[0]);
        Assert.assertEquals(DataType.OBJECT, p16.getListParameter().getListParameter().getType()[0]);
        test_2_assertJobParamMeta(p16.getListParameter().getListParameter());
    }

    private void test_2_assertJobParamMeta(JobParamMeta config7) {
        JobParamMeta p14 = Arrays.stream(config7.getObjectParameters()).filter(i -> "p14".equals(i.getName())).findAny()
                .orElse(null);
        Assert.assertNotNull(p14);
        Assert.assertTrue(p14.getOptional());
        Assert.assertEquals(DataType.DOUBLE, p14.getType()[0]);
        JobParamMeta p15 = Arrays.stream(config7.getObjectParameters()).filter(i -> "p15".equals(i.getName())).findAny()
                .orElse(null);
        Assert.assertNotNull(p15);
        Assert.assertTrue(p15.getOptional());
        Assert.assertEquals(DataType.STRING, p15.getType()[0]);
        Assert.assertEquals("p15", p15.getDefaultValue());
    }

    private void test_3_assertConfig6(JobParamMeta config6) {
        JobParamMeta p11 = Arrays.stream(config6.getObjectParameters()).filter(i -> "p11".equals(i.getName())).findAny()
                .orElse(null);
        Assert.assertNotNull(p11);
        Assert.assertTrue(p11.getOptional());
        Assert.assertEquals(DataType.LIST, p11.getType()[0]);
        Assert.assertEquals(DataType.LIST, p11.getListParameter().getType()[0]);
        Assert.assertEquals(DataType.STRING, p11.getListParameter().getListParameter().getType()[0]);

        JobParamMeta p12 = Arrays.stream(config6.getObjectParameters()).filter(i -> "p12".equals(i.getName())).findAny()
                .orElse(null);
        Assert.assertNotNull(p12);
        Assert.assertTrue(p12.getOptional());
        Assert.assertEquals(DataType.LIST, p12.getType()[0]);
        Assert.assertEquals(DataType.LIST, p12.getListParameter().getType()[0]);
        Assert.assertEquals(DataType.LIST, p12.getListParameter().getListParameter().getType()[0]);
        Assert.assertEquals(DataType.STRING, p12.getListParameter().getListParameter().getListParameter().getType()[0]);
    }
}
