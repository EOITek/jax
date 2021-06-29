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

package com.eoi.jax.flink.job.common;

import com.eoi.jax.common.CopyUtil;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class AviatorUtil {

    static Logger LOG = LoggerFactory.getLogger(AviatorUtil.class.getName());
    public static AviatorEvaluatorInstance instance = AviatorEvaluator.newInstance();

    static {
        instance.setOption(Options.USE_USER_ENV_AS_TOP_ENV_DIRECTLY, false);
        // copy 内置script到临时文件
        try {
            CopyUtil.copyResource(AviatorUtil.class, "/custom_fn.av");
        } catch (IOException ex) {
            LOG.error("copyResource /custom_fn.av failed", ex);
        }
    }

    public static <T> T eval(String expStr, Map<String,Object> env) {
        return eval(expStr,env,true,null);
    }

    public static <T> T eval(String expStr, Map<String,Object> env, T defaultValue) {
        return eval(expStr,env,true,defaultValue);
    }

    public static <T> T eval(String expStr, Map<String,Object> env, boolean cached, T defaultValue) {
        try {
            Expression exp = instance.compile(expStr, cached);
            return (T)exp.execute(env);
        } catch (Exception ex) {
            LOG.error("Aviator expression execute failed:" + expStr, ex);
            return defaultValue;
        }
    }
}
