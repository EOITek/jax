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

import com.eoi.jax.common.grok.GrokParser;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GrokParserTest {
    @Test
    public void test_readGrokFromStream() throws Exception {
        File file = new File(getClass().getResource("/haproxy").getFile());
        Map<String, String> grok = new GrokParser().readGrokFromStream(new FileInputStream(file));
        assertEquals(7, grok.size());

        Map<String, String> groks = new GrokParser().readGrokFromResource();
        groks.entrySet().stream().forEach(e -> System.out.println(e));
    }

    @Test
    public void test_expandPatternAlias() {
        GrokParser grok = new GrokParser();
        String expaned = grok.expandPatternAlias("^(?<logtime>\\S+) %{USER:user} (-|(?<level>\\w+)) %{DATA:msg}$");
        assertEquals("^(?<logtime>\\S+) (?<user>[a-zA-Z0-9._-]+) (-|(?<level>\\w+)) (?<msg>.*?)$", expaned);

        expaned = grok.expandPatternAlias("^(?<logtime>\\S+) %{USER:user}");
        assertEquals("^(?<logtime>\\S+) (?<user>[a-zA-Z0-9._-]+)", expaned);

        expaned = grok.expandPatternAlias("^(?<logtime>\\S+)");
        assertEquals("^(?<logtime>\\S+)", expaned);
    }

    @Test
    public void test_doProcess1() throws Exception {
        GrokParser grok = new GrokParser(Arrays.asList(
                "^(?<logtime>\\S+) %{USER:user} (-|(?<level>\\w+)) %{DATA:msg}$"
        ));
        Map<String, Object> event = new HashMap<>();
        event.put("message", "2015-12-27T15:44:19+0800 childe - this is a test line");
        Map<String, String> result = grok.parse("2015-12-27T15:44:19+0800 childe - this is a test line");
        assertEquals(result.get("user"), "childe");
        assertEquals(result.get("logtime"), "2015-12-27T15:44:19+0800");
        assertEquals(result.get("msg"), "this is a test line");
        assertNull(result.get("level"));
    }
}
