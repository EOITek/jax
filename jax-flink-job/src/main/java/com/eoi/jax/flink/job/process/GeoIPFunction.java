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

package com.eoi.jax.flink.job.process;

import cn.hutool.core.util.StrUtil;
import com.eoi.jax.common.geoip.CnRegionInfo;
import com.eoi.jax.common.geoip.CountryInfo;
import com.eoi.jax.common.geoip.IpSeeker;
import com.eoi.jax.common.geoip.RegionDetail;
import org.apache.flink.api.common.functions.RichMapFunction;

import java.util.HashMap;
import java.util.Map;

public class GeoIPFunction extends RichMapFunction<Map<String, Object>, Map<String, Object>> {

    public static final String IP_REGEX = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    private GeoIPJobConfig config;

    public GeoIPFunction(GeoIPJobConfig config) {
        this.config = config;
    }

    @Override
    public Map<String, Object> map(Map<String, Object> value) throws Exception {
        Object ipObj = value.get(config.getIpField());
        if (ipObj == null) {
            return value;
        }
        
        String ipStr = ipObj.toString();
        String prefix = "";
        if (!StrUtil.isEmpty(config.getOutputPrefix())) {
            prefix = config.getOutputPrefix();
        }
        
        if (ipStr.matches(IP_REGEX)) {
            Map<String, Object> result = new HashMap<>(value);
            String detail = IpSeeker.getSingleInstance().getCountry(ipStr);

            RegionDetail cnDetail = CnRegionInfo.getRegionDetail(detail);
            if (!cnDetail.province.equals("其他")) {
                result.put(prefix + "country", "中国");
            } else {
                Map<String, String> countryInfo = CountryInfo.getSimpleNameMap();
                // 先全部匹配，如果匹配不上则用包含匹配
                boolean flag = false;
                for (String key : countryInfo.keySet()) {
                    if (detail.equals(key)) {
                        flag = true;
                        result.put(prefix + "country", countryInfo.get(key));
                        break;
                    }
                }

                if (!flag) {
                    //全部匹配没有匹配到
                    for (String key : countryInfo.keySet()) {
                        if (detail.contains(key)) {
                            flag = true;
                            result.put(prefix + "country", countryInfo.get(key));
                            break;
                        }
                    }
                    if (!flag) {
                        result.put(prefix + "country", detail);
                    }
                }
            }

            result.put(prefix + "province", cnDetail.province);
            result.put(prefix + "city", cnDetail.city);
            result.put(prefix + "town", cnDetail.town);
            result.put(prefix + "isp", IpSeeker.getSingleInstance().getArea(ipStr));
            return result;
        } else {
            return value;
        }
    }
}
