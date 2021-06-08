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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountryInfo {

    public static Map<String, String> getSimpleNameMap() {
        return InnerLazyClass.nameMap;
    }

    private static void putItem(Map<String, String> map, String name, String... alias) {
        map.put(name, name);
        Arrays.stream(alias).forEach(x -> map.put(x, name));
    }

    public static List<String> countryList = Arrays.asList(
            "索马里",
            "列支敦士登",
            "摩洛哥",
            "撒哈拉沙漠西部",
            "塞尔维亚",
            "阿富汗",
            "安哥拉",
            "阿尔巴尼亚",
            "阿兰德",
            "安道尔",
            "阿拉伯联合酋长国",
            "阿根廷",
            "亚美尼亚",
            "美属萨摩亚",
            "南极地区",
            "安提瓜和巴布达",
            "澳大利亚",
            "奥地利",
            "阿塞拜疆",
            "布隆迪",
            "比利时",
            "贝宁",
            "布基纳法索",
            "孟加拉国",
            "保加利亚",
            "巴林",
            "巴哈马",
            "波斯尼亚和赫兹",
            "白俄罗斯",
            "伯利兹",
            "百慕大",
            "玻利维亚",
            "巴西",
            "巴巴多斯",
            "文莱",
            "不丹",
            "博茨瓦纳",
            "中非共和国",
            "加拿大",
            "瑞士",
            "智利",
            "中国",
            "科特迪瓦",
            "喀麦隆",
            "刚果金",
            "刚果",
            "哥伦比亚",
            "科摩罗",
            "佛得角",
            "哥斯达黎加",
            "古巴",
            "库拉索",
            "开曼岛",
            "北塞浦路斯",
            "塞浦路斯",
            "捷克共和国",
            "德国",
            "吉布提",
            "多米尼加",
            "丹麦",
            "多米尼加共和国",
            "阿尔及利亚",
            "厄瓜多尔",
            "埃及",
            "厄立特里亚",
            "西班牙",
            "爱沙尼亚",
            "埃塞俄比亚",
            "芬兰",
            "斐济共和国",
            "福克兰岛",
            "法国",
            "法罗群岛",
            "密克罗尼西亚",
            "加蓬",
            "英国",
            "格鲁吉亚",
            "加纳",
            "几内亚",
            "冈比亚",
            "几内亚比绍",
            "赤道几内亚",
            "希腊",
            "格林纳达",
            "格陵兰",
            "危地马拉",
            "关岛",
            "圭亚那",
            "赫德岛和麦克唐纳群岛",
            "洪都拉斯",
            "克罗地亚",
            "海地",
            "匈牙利",
            "印度尼西亚",
            "马恩岛",
            "印度",
            "英屬印度洋領地",
            "爱尔兰",
            "伊朗",
            "伊拉克",
            "冰岛",
            "以色列",
            "意大利",
            "牙买加",
            "新泽西",
            "约旦",
            "日本",
            "锡亚琴冰川",
            "哈萨克斯坦",
            "肯尼亚",
            "吉尔吉斯斯坦",
            "柬埔寨",
            "基里巴斯",
            "韩国",
            "科威特",
            "老挝人民民主共和国",
            "黎巴嫩",
            "利比里亚",
            "利比亚",
            "圣卢西亚",
            "斯里兰卡",
            "莱索托",
            "立陶宛",
            "卢森堡",
            "拉脱维亚",
            "摩尔多瓦",
            "马达加斯加",
            "墨西哥",
            "马其顿",
            "马里",
            "马耳他",
            "缅甸",
            "黑山",
            "蒙古",
            "马里亚纳群岛",
            "莫桑比克",
            "毛里塔尼亚",
            "蒙特塞拉特",
            "毛里求斯",
            "马拉维",
            "马来西亚",
            "纳米比亚",
            "新喀里多尼亚",
            "尼日尔",
            "尼日利亚",
            "尼加拉瓜",
            "纽埃",
            "荷兰",
            "挪威",
            "尼泊尔",
            "新西兰",
            "阿曼",
            "巴基斯坦",
            "巴拿马",
            "秘鲁",
            "菲律宾",
            "帕劳",
            "巴布亚新几内亚",
            "波兰",
            "波多黎各",
            "朝鲜民主主义人民共和国",
            "葡萄牙",
            "巴拉圭",
            "巴勒斯坦",
            "波利尼西亚",
            "卡塔尔",
            "罗马尼亚",
            "俄国",
            "卢旺达",
            "沙特阿拉伯",
            "苏丹",
            "南苏丹",
            "塞内加尔",
            "新加坡",
            "南乔治亚岛和南桑威奇群岛",
            "圣赫勒拿岛",
            "所罗门群岛",
            "塞拉利昂",
            "萨尔瓦多",
            "圣皮埃尔和密克隆",
            "圣多美和普林西比",
            "苏里南",
            "斯洛伐克",
            "斯洛文尼亚",
            "瑞典",
            "斯威士兰",
            "塞舌尔",
            "叙利亚",
            "特克斯和凯科斯群岛",
            "乍得湖",
            "多哥",
            "泰国",
            "吉克斯坦",
            "土库曼斯坦",
            "东帝汶",
            "汤加",
            "特立尼达和多巴哥",
            "突尼斯",
            "土耳其",
            "坦桑尼亚",
            "乌干达",
            "乌克兰",
            "乌拉圭",
            "美国",
            "乌兹别克斯坦",
            "圣文森特和格林纳丁斯",
            "委内瑞拉",
            "美属维尔京群岛",
            "越南",
            "瓦努阿图",
            "萨摩亚",
            "也门",
            "南非",
            "赞比亚",
            "津巴布韦"
    );

    private static class InnerLazyClass {

        static final Map<String, String> nameMap = new HashMap<>();

        static {
            countryList.forEach(item -> {
                if (item.equals("阿拉伯联合酋长国")) {
                    putItem(nameMap, item, "阿联酋");
                } else if (item.equals("印度尼西亚")) {
                    putItem(nameMap, item, "印尼");
                } else if (item.equals("英屬印度洋領地")) {
                    putItem(nameMap, item, "英属印度洋领地");
                } else if (item.equals("美属维尔京群岛")) {
                    putItem(nameMap, item, "维尔京群岛", "维尔京");
                } else if (item.equals("特克斯和凯科斯群岛")) {
                    putItem(nameMap, item, "特克斯和凯科斯");
                } else if (item.equals("赫德岛和麦克唐纳群岛")) {
                    putItem(nameMap, item, "赫德岛和麦克唐纳");
                } else if (item.equals("所罗门群岛")) {
                    putItem(nameMap, item, "所罗门");
                } else if (item.equals("开曼岛")) {
                    putItem(nameMap, item, "开曼");
                } else if (item.equals("南极地区")) {
                    putItem(nameMap, item, "南极");
                } else if (item.equals("刚果金")) {
                    putItem(nameMap, item, "刚果(金)");
                } else if (item.equals("刚果")) {
                    putItem(nameMap, item, "刚果(布)");
                } else if (item.equals("刚果布")) {
                    putItem(nameMap, item, "刚果(布)");
                } else {
                    String k = item.replace("民主主义人民共和国", "")
                            .replace("人民民主共和国", "")
                            .replace("民主共和国", "")
                            .replace("共和国", "");
                    nameMap.put(k, item);
                }
            });
        }
    }
}
