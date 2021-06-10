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
import static org.junit.Assert.assertNotNull;
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

    @Test
    public void test_doProcess2() throws Exception {
        GrokParser grok = new GrokParser(Arrays.asList(
                "(?<time>\\S+\\s+\\d+,\\s+\\d+\\s+\\d+:\\d+:\\d+\\s+\\S+) (?<component>\\S+) (?<method>\\S+)\\n(?<loglevel>\\S+): (?<message>[\\S\\W]+)"
        ));
        // CHECKSTYLE.OFF:
        String str = "May 2, 2018 6:10:46 PM org.apache.catalina.core.StandardWrapperValve invoke\n" +
                "SEVERE: Servlet.service() for servlet dispatcher threw exception\n\n" +
                "Expression _merchant is undefined on line 858, column 109 in include/boccfc_credit_common_js.html.\n" +
                "The problematic instruction:\n" +
                "----------\n" +
                "==> ${_merchant.centerAreaCode} [on line 858, column 107 in include/boccfc_credit_common_js.html]\n" +
                " in include \"include/boccfc_credit_common_js.html\" [on line 128, column 1 in include/applyHead.html]\n" +
                " in include \"include/applyHead.html\" [on line 1, column 1 in applylist.html]\n" +
                "----------\n\n" +
                "Java backtrace for programmers:\n" +
                "----------\nfreemarker.core.InvalidReferenceException: Expression _merchant is undefined on line 858, column 109 in include/boccfc_credit_common_js.html.\n\t" +
                "at freemarker.core.TemplateObject.assertNonNull(TemplateObject.java:125)\n\t" +
                "at freemarker.core.TemplateObject.invalidTypeException(TemplateObject.java:135)\n\t" +
                "at freemarker.core.Dot._getAsTemplateModel(Dot.java:78)\n\t" +
                "at freemarker.core.Expression.getAsTemplateModel(Expression.java:89)\n\t" +
                "at freemarker.core.Expression.getStringValue(Expression.java:93)\n\t" +
                "at freemarker.core.DollarVariable.accept(DollarVariable.java:76)\n\t" +
                "at freemarker.core.Environment.visit(Environment.java:221)\n\t" +
                "at freemarker.core.MixedContent.accept(MixedContent.java:92)\n\t" +
                "at freemarker.core.Environment.visit(Environment.java:221)\n\t" +
                "at freemarker.core.Environment.include(Environment.java:1508)\n\t" +
                "at freemarker.core.Include.accept(Include.java:169)\n\t" +
                "at freemarker.core.Environment.visit(Environment.java:221)\n\t" +
                "at freemarker.core.MixedContent.accept(MixedContent.java:92)\n\t" +
                "at freemarker.core.Environment.visit(Environment.java:221)\n\t" +
                "at freemarker.core.Environment.include(Environment.java:1508)\n\t" +
                "at freemarker.core.Include.accept(Include.java:169)\n\t" +
                "at freemarker.core.Environment.visit(Environment.java:221)\n\t" +
                "at freemarker.core.MixedContent.accept(MixedContent.java:92)\n\t" +
                "at freemarker.core.Environment.visit(Environment.java:221)\n\t" +
                "at freemarker.core.Environment.process(Environment.java:199)\n\t" +
                "at freemarker.template.Template.process(Template.java:259)\n\\tat org.springframework.web.servlet.view.freemarker.FreeMarkerView.processTemplate(FreeMarkerView.java:366)\n\\tat org.springframework.web.servlet.view.freemarker.FreeMarkerView.doRender(FreeMarkerView.java:283)\n\\tat org.springframework.web.servlet.view.freemarker.FreeMarkerView.renderMergedTemplateModel(FreeMarkerView.java:233)\n\\tat org.springframework.web.servlet.view.AbstractTemplateView.renderMergedOutputModel(AbstractTemplateView.java:167)\n\\tat org.springframework.web.servlet.view.AbstractView.render(AbstractView.java:262)\n\\tat org.springframework.web.servlet.DispatcherServlet.render(DispatcherServlet.java:1180)\n\\tat org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:950)\n\\tat org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:852)\n\\tat org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:882)\n\\tat org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:789)\n\\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:643)\n\\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:723)\n\\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:290)\n\\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)\n\\tat cn.boccfc.merchant.front.filter.MerwebSessionFilter.doFilter(MerwebSessionFilter.java:65)\n\\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:235)\n\\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)\n\\tat org.apache.shiro.web.servlet.ProxiedFilterChain.doFilter(ProxiedFilterChain.java:61)\n\\tat org.apache.shiro.web.servlet.AdviceFilter.executeChain(AdviceFilter.java:108)\n\\tat org.apache.shiro.web.servlet.AdviceFilter.doFilterInternal(AdviceFilter.java:137)\n\\tat org.apache.shiro.web.servlet.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:125)\n\\tat org.apache.shiro.web.servlet.ProxiedFilterChain.doFilter(ProxiedFilterChain.java:66)\n\\tat org.apache.shiro.web.servlet.AbstractShiroFilter.executeChain(AbstractShiroFilter.java:449)\n\\tat org.apache.shiro.web.servlet.AbstractShiroFilter$1.call(AbstractShiroFilter.java:365)\n\\tat org.apache.shiro.subject.support.SubjectCallable.doCall(SubjectCallable.java:90)\n\\tat org.apache.shiro.subject.support.SubjectCallable.call(SubjectCallable.java:83)\n\\tat org.apache.shiro.subject.support.DelegatingSubject.execute(DelegatingSubject.java:383)\n\\tat org.apache.shiro.web.servlet.AbstractShiroFilter.doFilterInternal(AbstractShiroFilter.java:362)\n\\tat org.apache.shiro.web.servlet.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:125)\n\\tat org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:346)\n\\tat org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:259)\n\\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:235)\n\\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)\n\\tat org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:88)\n\\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:76)\n\\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:235)\n\\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)\n\\tat org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:233)\n\\tat org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:191)\n\\tat org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:127)\n\\tat org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:103)\n\\tat org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:109)\n\\tat org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:293)\n\\tat org.apache.coyote.http11.Http11Processor.process(Http11Processor.java:861)\n\\tat org.apache.coyote.http11.Http11Protocol$Http11ConnectionHandler.process(Http11Protocol.java:606)\n\\tat org.apache.tomcat.util.net.JIoEndpoint$Worker.run(JIoEndpoint.java:489)\n\\tat java.lang.Thread.run(Thread.java:662)";
        // CHECKSTYLE.ON:
        Map<String, String> result = grok.parse(str);
        assertEquals(result.get("time"), "May 2, 2018 6:10:46 PM");
        assertEquals(result.get("component"), "org.apache.catalina.core.StandardWrapperValve");
        assertEquals(result.get("method"), "invoke");
        assertEquals(result.get("loglevel"), "SEVERE");
        assertNotNull(result.get("message"));
    }
}
