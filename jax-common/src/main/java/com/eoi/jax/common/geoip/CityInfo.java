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

package com.eoi.jax.common.geoip;

import java.util.List;

public class CityInfo {

    public static final String CityType_Normal = "Normal";//普通城市
    public static final String CityType_MunicipalityDistrict = "District";//直辖市的区

    public String name;
    public String style;
    public List<String> townList;

    public CityInfo(String name, String style, List<String> townList) {
        this.name = name;
        this.style = style;
        this.townList = townList;
    }

    public CityInfo(String name, List<String> townList) {
        this(name, CityType_Normal, townList);
    }
}