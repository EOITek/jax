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

package com.eoi.jax.web.service.diagnosis;

import cn.hutool.http.HttpUtil;
import com.eoi.jax.web.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class FlinkDiagnosisBase {
    private static final Logger logger = LoggerFactory.getLogger(FlinkDiagnosisBase.class);
    static final String API_ACCESS_ERROR = "获取诊断信息失败";

    String get(String url, Map<String, Object> params) {
        String result = null;
        try {
            result = HttpUtil.get(url, params, 10000);
        } catch (Exception ex) {
            logger.debug("get url error:" + url, ex);
        }
        return result;
    }

    List<Map<String, Object>> getList(String url, Map<String, Object> params) {
        List<Map<String, Object>>  result = null;
        try {
            result = JsonUtil.decode2ListMap(get(url, params));
        } catch (Exception ex) {
            logger.debug("get list map decode error:" + url, ex);
        }
        return result;
    }

    Map<String, Object> getMap(String url, Map<String, Object> params) {
        Map<String, Object> result = null;
        try {
            result = JsonUtil.decode2Map(get(url, params));
        } catch (Exception ex) {
            logger.debug("get map decode error:" + url, ex);
        }
        return result;
    }

    String getUrl(String prefix, String path) {
        return prefix + path;
    }
}
