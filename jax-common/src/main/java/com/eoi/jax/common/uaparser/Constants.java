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

package com.eoi.jax.common.uaparser;

/**
 * @author sojern
 */
public class Constants {

    public static final String EMPTY_STRING = "\"\"";

    //CHECKSTYLE.OFF:
    public static final String USER_AGENT_PARSERS = "[" +
            "{" +
            "    \"regex\":\"(HbbTV)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+) \\\\(\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Chimera|SeaMonkey|Camino)/(\\\\d+)\\\\.(\\\\d+)\\\\.?([ab]?\\\\d+[a-z]*)?\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Pale[Mm]oon)/(\\\\d+)\\\\.(\\\\d+)\\\\.?(\\\\d+)?\"," +
            "    \"family_replacement\":\"Pale Moon (Firefox Variant)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Fennec)/(\\\\d+)\\\\.(\\\\d+)\\\\.?([ab]?\\\\d+[a-z]*)\"," +
            "    \"family_replacement\":\"Firefox Mobile\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Fennec)/(\\\\d+)\\\\.(\\\\d+)(pre)\"," +
            "    \"family_replacement\":\"Firefox Mobile\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Fennec)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Firefox Mobile\"" +
            "}," +
            "{" +
            "    \"regex\":\"Mobile.*(Firefox)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Firefox Mobile\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Namoroka|Shiretoko|Minefield)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+(?:pre)?)\"," +
            "    \"family_replacement\":\"Firefox ($1)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Firefox)/(\\\\d+)\\\\.(\\\\d+)(a\\\\d+[a-z]*)\"," +
            "    \"family_replacement\":\"Firefox Alpha\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Firefox)/(\\\\d+)\\\\.(\\\\d+)(b\\\\d+[a-z]*)\"," +
            "    \"family_replacement\":\"Firefox Beta\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Firefox)-(?:\\\\d+\\\\.\\\\d+)?/(\\\\d+)\\\\.(\\\\d+)(a\\\\d+[a-z]*)\"," +
            "    \"family_replacement\":\"Firefox Alpha\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Firefox)-(?:\\\\d+\\\\.\\\\d+)?/(\\\\d+)\\\\.(\\\\d+)(b\\\\d+[a-z]*)\"," +
            "    \"family_replacement\":\"Firefox Beta\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Namoroka|Shiretoko|Minefield)/(\\\\d+)\\\\.(\\\\d+)([ab]\\\\d+[a-z]*)?\"," +
            "    \"family_replacement\":\"Firefox ($1)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Firefox).*Tablet browser (\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"MicroB\"" +
            "}," +
            "{" +
            "    \"regex\":\"(MozillaDeveloperPreview)/(\\\\d+)\\\\.(\\\\d+)([ab]\\\\d+[a-z]*)?\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Flock)/(\\\\d+)\\\\.(\\\\d+)(b\\\\d+?)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(RockMelt)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Navigator)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Netscape\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Navigator)/(\\\\d+)\\\\.(\\\\d+)([ab]\\\\d+)\"," +
            "    \"family_replacement\":\"Netscape\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Netscape6)/(\\\\d+)\\\\.(\\\\d+)\\\\.?([ab]?\\\\d+)?\"," +
            "    \"family_replacement\":\"Netscape\"" +
            "}," +
            "{" +
            "    \"regex\":\"(MyIBrow)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"My Internet Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Opera Tablet).*Version/(\\\\d+)\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Opera)/.+Opera Mobi.+Version/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Opera Mobile\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Opera)/(\\\\d+)\\\\.(\\\\d+).+Opera Mobi\"," +
            "    \"family_replacement\":\"Opera Mobile\"" +
            "}," +
            "{" +
            "    \"regex\":\"Opera Mobi.+(Opera)(?:/|\\\\s+)(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Opera Mobile\"" +
            "}," +
            "{" +
            "    \"regex\":\"Opera Mobi\"," +
            "    \"family_replacement\":\"Opera Mobile\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Opera Mini)(?:/att)?/(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Opera)/9.80.*Version/(\\\\d+)\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?\"" +
            "}," +
            "{" +
            "    \"regex\":\"(?:Mobile Safari).*(OPR)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Opera Mobile\"" +
            "}," +
            "{" +
            "    \"regex\":\"(?:Chrome).*(OPR)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Opera\"" +
            "}," +
            "{" +
            "    \"regex\":\"(hpw|web)OS/(\\\\d+)\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?\"," +
            "    \"family_replacement\":\"webOS Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(luakit)\"," +
            "    \"family_replacement\":\"LuaKit\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Snowshoe)/(\\\\d+)\\\\.(\\\\d+).(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Lightning)/(\\\\d+)\\\\.(\\\\d+)\\\\.?((?:[ab]?\\\\d+[a-z]*)|(?:\\\\d*))\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Firefox)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+(?:pre)?) \\\\(Swiftfox\\\\)\"," +
            "    \"family_replacement\":\"Swiftfox\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Firefox)/(\\\\d+)\\\\.(\\\\d+)([ab]\\\\d+[a-z]*)? \\\\(Swiftfox\\\\)\"," +
            "    \"family_replacement\":\"Swiftfox\"" +
            "}," +
            "{" +
            "    \"regex\":\"(rekonq)/(\\\\d+)\\\\.(\\\\d+)\\\\.?(\\\\d+)? Safari\"," +
            "    \"family_replacement\":\"Rekonq\"" +
            "}," +
            "{" +
            "    \"regex\":\"rekonq\"," +
            "    \"family_replacement\":\"Rekonq\"" +
            "}," +
            "{" +
            "    \"regex\":\"(conkeror|Conkeror)/(\\\\d+)\\\\.(\\\\d+)\\\\.?(\\\\d+)?\"," +
            "    \"family_replacement\":\"Conkeror\"" +
            "}," +
            "{" +
            "    \"regex\":\"(konqueror)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Konqueror\"" +
            "}," +
            "{" +
            "    \"regex\":\"(WeTab)-Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Comodo_Dragon)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Comodo Dragon\"" +
            "}," +
            "{" +
            "    \"regex\":\"(YottaaMonitor|BrowserMob|HttpMonitor|YandexBot|Slurp|BingPreview|PagePeeker|ThumbShotsBot|WebThumb|URL2PNG|ZooShot|GomezA|Catchpoint bot|Willow Internet Crawler|Google SketchUp|Read%20Later)\""
            +
            "}," +
            "{" +
            "    \"regex\":\"(Symphony) (\\\\d+).(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Minimo)\"" +
            "}," +
            "{" +
            "    \"regex\":\"PLAYSTATION 3.+WebKit\"," +
            "    \"family_replacement\":\"NetFront NX\"" +
            "}," +
            "{" +
            "    \"regex\":\"PLAYSTATION 3\"," +
            "    \"family_replacement\":\"NetFront\"" +
            "}," +
            "{" +
            "    \"regex\":\"(PlayStation Portable)\"," +
            "    \"family_replacement\":\"NetFront\"" +
            "}," +
            "{" +
            "    \"regex\":\"(PlayStation Vita)\"," +
            "    \"family_replacement\":\"NetFront NX\"" +
            "}," +
            "{" +
            "    \"regex\":\"AppleWebKit.+ (NX)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"NetFront NX\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Nintendo 3DS)\"," +
            "    \"family_replacement\":\"NetFront NX\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Silk)/(\\\\d+)\\\\.(\\\\d+)(?:\\\\.([0-9\\\\-]+))?\"," +
            "    \"family_replacement\":\"Amazon Silk\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Puffin)/(\\\\d+)\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?\"" +
            "}," +
            "{" +
            "    \"regex\":\"(CrMo)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Chrome Mobile\"" +
            "}," +
            "{" +
            "    \"regex\":\"(CriOS)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Chrome Mobile iOS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Chrome)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+) Mobile\"," +
            "    \"family_replacement\":\"Chrome Mobile\"" +
            "}," +
            "{" +
            "    \"regex\":\"(chromeframe)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Chrome Frame\"" +
            "}," +
            "{" +
            "    \"regex\":\"(UCBrowser)[ /](\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"UC Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(UC Browser)[ /](\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(UC Browser|UCBrowser|UCWEB)(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"UC Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(SLP Browser)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Tizen Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(SE 2\\\\.X) MetaSr (\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Sogou Explorer\"" +
            "}," +
            "{" +
            "    \"regex\":\"(baidubrowser)[/\\\\s](\\\\d+)\"," +
            "    \"family_replacement\":\"Baidu Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(FlyFlow)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Baidu Explorer\"" +
            "}," +
            "{" +
            "    \"regex\":\"(MQQBrowser/Mini)(?:(\\\\d+)(?:\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?)?)?\"," +
            "    \"family_replacement\":\"QQ Browser Mini\"" +
            "}," +
            "{" +
            "    \"regex\":\"(MQQBrowser)(?:/(\\\\d+)(?:\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?)?)?\"," +
            "    \"family_replacement\":\"QQ Browser Mobile\"" +
            "}," +
            "{" +
            "    \"regex\":\"(QQBrowser)(?:/(\\\\d+)(?:\\\\.(\\\\d+)\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?)?)?\"," +
            "    \"family_replacement\":\"QQ Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Pingdom.com_bot_version_)(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"PingdomBot\"" +
            "}," +
            "{" +
            "    \"regex\":\"(facebookexternalhit)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"FacebookBot\"" +
            "}," +
            "{" +
            "    \"regex\":\"(LinkedInBot)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"LinkedInBot\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Twitterbot)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"TwitterBot\"" +
            "}," +
            "{" +
            "    \"regex\":\"Google.*/\\\\+/web/snippet\"," +
            "    \"family_replacement\":\"GooglePlusBot\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Rackspace Monitoring)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"RackspaceBot\"" +
            "}," +
            "{" +
            "    \"regex\":\"(PyAMF)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(YaBrowser)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Yandex Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Chrome)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+).* MRCHROME\"," +
            "    \"family_replacement\":\"Mail.ru Chromium Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(AOL) (\\\\d+)\\\\.(\\\\d+); AOLBuild (\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(AdobeAIR|FireWeb|Jasmine|ANTGalio|Midori|Fresco|Lobo|PaleMoon|Maxthon|Lynx|OmniWeb|Dillo|Camino|Demeter|Fluid|Fennec|Epiphany|Shiira|Sunrise|Flock|Netscape|Lunascape|WebPilot|NetFront|Netfront|Konqueror|SeaMonkey|Kazehakase|Vienna|Iceape|Iceweasel|IceWeasel|Iron|K-Meleon|Sleipnir|Galeon|GranParadiso|Opera Mini|iCab|NetNewsWire|ThunderBrowse|Iris|UP\\\\.Browser|Bunjalloo|Google Earth|Raven for Mac|Openwave)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\""
            +
            "}," +
            "{" +
            "    \"regex\":\"MSOffice 12\"," +
            "    \"family_replacement\":\"Outlook\"," +
            "    \"v1_replacement\":\"2007\"" +
            "}," +
            "{" +
            "    \"regex\":\"MSOffice 14\"," +
            "    \"family_replacement\":\"Outlook\"," +
            "    \"v1_replacement\":\"2010\"" +
            "}," +
            "{" +
            "    \"regex\":\"Microsoft Outlook 15\\\\.\\\\d+\\\\.\\\\d+\"," +
            "    \"family_replacement\":\"Outlook\"," +
            "    \"v1_replacement\":\"2013\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Airmail) (\\\\d+)\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Thunderbird)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+(?:pre)?)\"," +
            "    \"family_replacement\":\"Thunderbird\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Chromium|Chrome)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"\\\\b(Dolphin)(?: |HDCN/|/INT\\\\-)(\\\\d+)\\\\.(\\\\d+)\\\\.?(\\\\d+)?\"" +
            "}," +
            "{" +
            "    \"regex\":\"(bingbot|Bolt|Jasmine|IceCat|Skyfire|Midori|Maxthon|Lynx|Arora|IBrowse|Dillo|Camino|Shiira|Fennec|Phoenix|Chrome|Flock|Netscape|Lunascape|Epiphany|WebPilot|Opera Mini|Opera|NetFront|Netfront|Konqueror|Googlebot|SeaMonkey|Kazehakase|Vienna|Iceape|Iceweasel|IceWeasel|Iron|K-Meleon|Sleipnir|Galeon|GranParadiso|iCab|NetNewsWire|Space Bison|Stainless|Orca|Dolfin|BOLT|Minimo|Tizen Browser|Polaris|Abrowser|Planetweb|ICE Browser|mDolphin)/(\\\\d+)\\\\.(\\\\d+)\\\\.?(\\\\d+)?\""
            +
            "}," +
            "{" +
            "    \"regex\":\"(Chromium|Chrome)/(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(IEMobile)[ /](\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"IE Mobile\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iRider|Crazy Browser|SkipStone|iCab|Lunascape|Sleipnir|Maemo Browser) (\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iCab|Lunascape|Opera|Android|Jasmine|Polaris) (\\\\d+)\\\\.(\\\\d+)\\\\.?(\\\\d+)?\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Kindle)/(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Android) Donut\"," +
            "    \"v1_replacement\":\"1\"," +
            "    \"v2_replacement\":\"2\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Android) Eclair\"," +
            "    \"v1_replacement\":\"2\"," +
            "    \"v2_replacement\":\"1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Android) Froyo\"," +
            "    \"v1_replacement\":\"2\"," +
            "    \"v2_replacement\":\"2\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Android) Gingerbread\"," +
            "    \"v1_replacement\":\"2\"," +
            "    \"v2_replacement\":\"3\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Android) Honeycomb\"," +
            "    \"v1_replacement\":\"3\"" +
            "}," +
            "{" +
            "    \"regex\":\"(MSIE) (\\\\d+)\\\\.(\\\\d+).*XBLWP7\"," +
            "    \"family_replacement\":\"IE Large Screen\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Obigo)InternetBrowser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Obigo)\\\\-Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Obigo|OBIGO)[^\\\\d]*(\\\\d+)(?:.(\\\\d+))?\"," +
            "    \"family_replacement\":\"Obigo\"" +
            "}," +
            "{" +
            "    \"regex\":\"(MAXTHON|Maxthon) (\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Maxthon\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Maxthon|MyIE2|Uzbl|Shiira)\"," +
            "    \"v1_replacement\":\"0\"" +
            "}," +
            "{" +
            "    \"regex\":\"(BrowseX) \\\\((\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(NCSA_Mosaic)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"NCSA Mosaic\"" +
            "}," +
            "{" +
            "    \"regex\":\"(POLARIS)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Polaris\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Embider)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Polaris\"" +
            "}," +
            "{" +
            "    \"regex\":\"(BonEcho)/(\\\\d+)\\\\.(\\\\d+)\\\\.?([ab]?\\\\d+)?\"," +
            "    \"family_replacement\":\"Bon Echo\"" +
            "}," +
            "{" +
            "    \"regex\":\"(CFNetwork)(?:/(\\\\d+)\\\\.(\\\\d+)\\\\.?(\\\\d+)?)?\"," +
            "    \"family_replacement\":\"CFNetwork\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iPod).+Version/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Mobile Safari\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iPod).*Version/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Mobile Safari\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iPhone).*Version/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Mobile Safari\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iPhone).*Version/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Mobile Safari\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iPad).*Version/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Mobile Safari\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iPad).*Version/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Mobile Safari\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iPod|iPhone|iPad);.*CPU.*OS (\\\\d+)_(\\\\d+)(?:_(\\\\d+))?.*Mobile\"," +
            "    \"family_replacement\":\"Mobile Safari\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iPod|iPhone|iPad)\"," +
            "    \"family_replacement\":\"Mobile Safari\"" +
            "}," +
            "{" +
            "    \"regex\":\"(AvantGo) (\\\\d+).(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(OneBrowser)/(\\\\d+).(\\\\d+)\"," +
            "    \"family_replacement\":\"ONE Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Avant)\"," +
            "    \"v1_replacement\":\"1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(QtCarBrowser)\"," +
            "    \"v1_replacement\":\"1\"" +
            "}," +
            "{" +
            "    \"regex\":\"^(iBrowser/Mini)(\\\\d+).(\\\\d+)\"," +
            "    \"family_replacement\":\"iBrowser Mini\"" +
            "}," +
            "{" +
            "    \"regex\":\"^(iBrowser|iRAPP)/(\\\\d+).(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"^(Nokia)\"," +
            "    \"family_replacement\":\"Nokia Services (WAP) Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(NokiaBrowser)/(\\\\d+)\\\\.(\\\\d+).(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Nokia Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(NokiaBrowser)/(\\\\d+)\\\\.(\\\\d+).(\\\\d+)\"," +
            "    \"family_replacement\":\"Nokia Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(NokiaBrowser)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Nokia Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(BrowserNG)/(\\\\d+)\\\\.(\\\\d+).(\\\\d+)\"," +
            "    \"family_replacement\":\"Nokia Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Series60)/5\\\\.0\"," +
            "    \"family_replacement\":\"Nokia Browser\"," +
            "    \"v1_replacement\":\"7\"," +
            "    \"v2_replacement\":\"0\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Series60)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Nokia OSS Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(S40OviBrowser)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Ovi Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Nokia)[EN]?(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(BB10);\"," +
            "    \"family_replacement\":\"BlackBerry WebKit\"" +
            "}," +
            "{" +
            "    \"regex\":\"(PlayBook).+RIM Tablet OS (\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"BlackBerry WebKit\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Black[bB]erry).+Version/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"BlackBerry WebKit\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Black[bB]erry)\\\\s?(\\\\d+)\"," +
            "    \"family_replacement\":\"BlackBerry\"" +
            "}," +
            "{" +
            "    \"regex\":\"(OmniWeb)/v(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Blazer)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Palm Blazer\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Pre)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Palm Pre\"" +
            "}," +
            "{" +
            "    \"regex\":\"(ELinks)/(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(ELinks) \\\\((\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Links) \\\\((\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(QtWeb) Internet Browser/(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(PhantomJS)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(AppleWebKit)/(\\\\d+)\\\\.?(\\\\d+)?\\\\+ .* Safari\"," +
            "    \"family_replacement\":\"WebKit Nightly\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Version)/(\\\\d+)\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?.*Safari/\"," +
            "    \"family_replacement\":\"Safari\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Safari)/\\\\d+\"" +
            "}," +
            "{" +
            "    \"regex\":\"(OLPC)/Update(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(OLPC)/Update()\\\\.(\\\\d+)\"," +
            "    \"v1_replacement\":\"0\"" +
            "}," +
            "{" +
            "    \"regex\":\"(SEMC\\\\-Browser)/(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Teleca)\"," +
            "    \"family_replacement\":\"Teleca Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Phantom)/V(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Phantom Browser\"" +
            "}," +
            "{" +
            "    \"regex\":\"Trident(.*)rv.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"IE\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Espial)/(\\\\d+)(?:\\\\.(\\\\d+))?(?:\\\\.(\\\\d+))?\"" +
            "}," +
            "{" +
            "    \"regex\":\"(AppleWebKit)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"AppleMail\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Firefox)/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Firefox)/(\\\\d+)\\\\.(\\\\d+)(pre|[ab]\\\\d+[a-z]*)?\"" +
            "}," +
            "{" +
            "    \"regex\":\"([MS]?IE) (\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"IE\"" +
            "}," +
            "{" +
            "    \"regex\":\"(python-requests)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"family_replacement\":\"Python Requests\"" +
            "}" +
            "]";

    public static final String OS_PARSERS = "[" +
            "{" +
            "    \"regex\":\"HbbTV/\\\\d+\\\\.\\\\d+\\\\.\\\\d+ \\\\( ;(LG)E ;NetCast 4.0\"," +
            "    \"os_v1_replacement\":\"2013\"" +
            "}," +
            "{" +
            "    \"regex\":\"HbbTV/\\\\d+\\\\.\\\\d+\\\\.\\\\d+ \\\\( ;(LG)E ;NetCast 3.0\"," +
            "    \"os_v1_replacement\":\"2012\"" +
            "}," +
            "{" +
            "    \"regex\":\"HbbTV/1.1.1 \\\\(;;;;;\\\\) Maple_2011\"," +
            "    \"os_replacement\":\"Samsung\"," +
            "    \"os_v1_replacement\":\"2011\"" +
            "}," +
            "{" +
            "    \"regex\":\"HbbTV/\\\\d+\\\\.\\\\d+\\\\.\\\\d+ \\\\(;(Samsung);SmartTV([0-9]{4});.*FXPDEUC\"," +
            "    \"os_v2_replacement\":\"UE40F7000\"" +
            "}," +
            "{" +
            "    \"regex\":\"HbbTV/\\\\d+\\\\.\\\\d+\\\\.\\\\d+ \\\\(;(Samsung);SmartTV([0-9]{4});.*MST12DEUC\"," +
            "    \"os_v2_replacement\":\"UE32F4500\"" +
            "}," +
            "{" +
            "    \"regex\":\"HbbTV/1.1.1 \\\\(; (Philips);.*NETTV/4\"," +
            "    \"os_v1_replacement\":\"2013\"" +
            "}," +
            "{" +
            "    \"regex\":\"HbbTV/1.1.1 \\\\(; (Philips);.*NETTV/3\"," +
            "    \"os_v1_replacement\":\"2012\"" +
            "}," +
            "{" +
            "    \"regex\":\"HbbTV/1.1.1 \\\\(; (Philips);.*NETTV/2\"," +
            "    \"os_v1_replacement\":\"2011\"" +
            "}," +
            "{" +
            "    \"regex\":\"HbbTV/\\\\d+\\\\.\\\\d+\\\\.\\\\d+.*(firetv)-firefox-plugin (\\\\d+).(\\\\d+).(\\\\d+)\"," +
            "    \"os_replacement\":\"FireHbbTV\"" +
            "}," +
            "{" +
            "    \"regex\":\"HbbTV/\\\\d+\\\\.\\\\d+\\\\.\\\\d+ \\\\(.*; ?([a-zA-Z]+) ?;.*(201[1-9]).*\\\\)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Windows Phone) (?:OS[ /])?(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Android)[ \\\\-/](\\\\d+)\\\\.(\\\\d+)(?:[.\\\\-]([a-z0-9]+))?\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Android) Donut\"," +
            "    \"os_v1_replacement\":\"1\"," +
            "    \"os_v2_replacement\":\"2\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Android) Eclair\"," +
            "    \"os_v1_replacement\":\"2\"," +
            "    \"os_v2_replacement\":\"1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Android) Froyo\"," +
            "    \"os_v1_replacement\":\"2\"," +
            "    \"os_v2_replacement\":\"2\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Android) Gingerbread\"," +
            "    \"os_v1_replacement\":\"2\"," +
            "    \"os_v2_replacement\":\"3\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Android) Honeycomb\"," +
            "    \"os_v1_replacement\":\"3\"" +
            "}," +
            "{" +
            "    \"regex\":\"^UCWEB.*; (Adr) (\\\\d+)\\\\.(\\\\d+)(?:[.\\\\-]([a-z0-9]+))?;\"," +
            "    \"os_replacement\":\"Android\"" +
            "}," +
            "{" +
            "    \"regex\":\"^UCWEB.*; (iPad OS|iPh OS) (\\\\d+)_(\\\\d+)(?:_(\\\\d+))?;\"," +
            "    \"os_replacement\":\"iOS\"" +
            "}," +
            "{" +
            "    \"regex\":\"^UCWEB.*; (wds) (\\\\d+)\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?;\"," +
            "    \"os_replacement\":\"Windows Phone\"" +
            "}," +
            "{" +
            "    \"regex\":\"^(JUC).*; ?U; ?(?:Android)?(\\\\d+)\\\\.(\\\\d+)(?:[\\\\.\\\\-]([a-z0-9]+))?\"," +
            "    \"os_replacement\":\"Android\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Silk-Accelerated=[a-z]{4,5})\"," +
            "    \"os_replacement\":\"Android\"" +
            "}," +
            "{" +
            "    \"regex\":\"(XBLWP7)\"," +
            "    \"os_replacement\":\"Windows Phone\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Windows ?Mobile)\"," +
            "    \"os_replacement\":\"Windows Mobile\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Windows (?:NT 5\\\\.2|NT 5\\\\.1))\"," +
            "    \"os_replacement\":\"Windows XP\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Windows NT 6\\\\.1)\"," +
            "    \"os_replacement\":\"Windows 7\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Windows NT 6\\\\.0)\"," +
            "    \"os_replacement\":\"Windows Vista\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Win 9x 4\\\\.90)\"," +
            "    \"os_replacement\":\"Windows ME\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Windows 98|Windows XP|Windows ME|Windows 95|Windows CE|Windows 7|Windows NT 4\\\\.0|Windows Vista|Windows 2000|Windows 3.1)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Windows NT 6\\\\.2; ARM;)\"," +
            "    \"os_replacement\":\"Windows RT\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Windows NT 6\\\\.2)\"," +
            "    \"os_replacement\":\"Windows 8\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Windows NT 6\\\\.3; ARM;)\"," +
            "    \"os_replacement\":\"Windows RT 8.1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Windows NT 6\\\\.3)\"," +
            "    \"os_replacement\":\"Windows 8.1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Windows NT 5\\\\.0)\"," +
            "    \"os_replacement\":\"Windows 2000\"" +
            "}," +
            "{" +
            "    \"regex\":\"(WinNT4.0)\"," +
            "    \"os_replacement\":\"Windows NT 4.0\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Windows ?CE)\"," +
            "    \"os_replacement\":\"Windows CE\"" +
            "}," +
            "{" +
            "    \"regex\":\"Win ?(95|98|3.1|NT|ME|2000)\"," +
            "    \"os_replacement\":\"Windows $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Win16\"," +
            "    \"os_replacement\":\"Windows 3.1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Win32\"," +
            "    \"os_replacement\":\"Windows 95\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Tizen)/(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Mac OS X) (\\\\d+)[_.](\\\\d+)(?:[_.](\\\\d+))?\"" +
            "}," +
            "{" +
            "    \"regex\":\" (Dar)(win)/(9).(\\\\d+).*\\\\((?:i386|x86_64|Power Macintosh)\\\\)\"," +
            "    \"os_replacement\":\"Mac OS X\"," +
            "    \"os_v1_replacement\":\"10\"," +
            "    \"os_v2_replacement\":\"5\"" +
            "}," +
            "{" +
            "    \"regex\":\" (Dar)(win)/(10).(\\\\d+).*\\\\((?:i386|x86_64)\\\\)\"," +
            "    \"os_replacement\":\"Mac OS X\"," +
            "    \"os_v1_replacement\":\"10\"," +
            "    \"os_v2_replacement\":\"6\"" +
            "}," +
            "{" +
            "    \"regex\":\" (Dar)(win)/(11).(\\\\d+).*\\\\((?:i386|x86_64)\\\\)\"," +
            "    \"os_replacement\":\"Mac OS X\"," +
            "    \"os_v1_replacement\":\"10\"," +
            "    \"os_v2_replacement\":\"7\"" +
            "}," +
            "{" +
            "    \"regex\":\" (Dar)(win)/(12).(\\\\d+).*\\\\((?:i386|x86_64)\\\\)\"," +
            "    \"os_replacement\":\"Mac OS X\"," +
            "    \"os_v1_replacement\":\"10\"," +
            "    \"os_v2_replacement\":\"8\"" +
            "}," +
            "{" +
            "    \"regex\":\" (Dar)(win)/(13).(\\\\d+).*\\\\((?:i386|x86_64)\\\\)\"," +
            "    \"os_replacement\":\"Mac OS X\"," +
            "    \"os_v1_replacement\":\"10\"," +
            "    \"os_v2_replacement\":\"9\"" +
            "}," +
            "{" +
            "    \"regex\":\"Mac_PowerPC\"," +
            "    \"os_replacement\":\"Mac OS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(?:PPC|Intel) (Mac OS X)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(CPU OS|iPhone OS|CPU iPhone) +(\\\\d+)[_\\\\.](\\\\d+)(?:[_\\\\.](\\\\d+))?\"," +
            "    \"os_replacement\":\"iOS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iPhone|iPad|iPod); Opera\"," +
            "    \"os_replacement\":\"iOS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iPhone|iPad|iPod).*Mac OS X.*Version/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"os_replacement\":\"iOS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(AppleTV)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"os_replacement\":\"ATV OS X\"" +
            "}," +
            "{" +
            "    \"regex\":\"(CFNetwork)/(5)48\\\\.0\\\\.3.* Darwin/11\\\\.0\\\\.0\"," +
            "    \"os_replacement\":\"iOS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(CFNetwork)/(5)48\\\\.(0)\\\\.4.* Darwin/(1)1\\\\.0\\\\.0\"," +
            "    \"os_replacement\":\"iOS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(CFNetwork)/(5)48\\\\.(1)\\\\.4\"," +
            "    \"os_replacement\":\"iOS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(CFNetwork)/(4)85\\\\.1(3)\\\\.9\"," +
            "    \"os_replacement\":\"iOS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(CFNetwork)/(6)09\\\\.(1)\\\\.4\"," +
            "    \"os_replacement\":\"iOS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(CFNetwork)/(6)(0)9\"," +
            "    \"os_replacement\":\"iOS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(CFNetwork)/6(7)2\\\\.(1)\\\\.13\"," +
            "    \"os_replacement\":\"iOS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(CFNetwork)/6(7)2\\\\.(1)\\\\.(1)4\"," +
            "    \"os_replacement\":\"iOS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(CF)(Network)/6(7)(2)\\\\.1\\\\.15\"," +
            "    \"os_replacement\":\"iOS\"," +
            "    \"os_v1_replacement\":\"7\"," +
            "    \"os_v2_replacement\":\"1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(CFNetwork)/6(7)2\\\\.(0)\\\\.(?:2|8)\"," +
            "    \"os_replacement\":\"iOS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(CFNetwork)/709\\\\.1\"," +
            "    \"os_replacement\":\"iOS\"," +
            "    \"os_v1_replacement\":\"8\"," +
            "    \"os_v2_replacement\":\"0.b5\"" +
            "}," +
            "{" +
            "    \"regex\":\"CFNetwork/.* Darwin/(9)\\\\.\\\\d+\"," +
            "    \"os_replacement\":\"iOS\"," +
            "    \"os_v1_replacement\":\"1\"" +
            "}," +
            "{" +
            "    \"regex\":\"CFNetwork/.* Darwin/(10)\\\\.\\\\d+\"," +
            "    \"os_replacement\":\"iOS\"," +
            "    \"os_v1_replacement\":\"4\"" +
            "}," +
            "{" +
            "    \"regex\":\"CFNetwork/.* Darwin/(11)\\\\.\\\\d+\"," +
            "    \"os_replacement\":\"iOS\"," +
            "    \"os_v1_replacement\":\"5\"" +
            "}," +
            "{" +
            "    \"regex\":\"CFNetwork/.* Darwin/(13)\\\\.\\\\d+\"," +
            "    \"os_replacement\":\"iOS\"," +
            "    \"os_v1_replacement\":\"6\"" +
            "}," +
            "{" +
            "    \"regex\":\"CFNetwork/6.* Darwin/(14)\\\\.\\\\d+\"," +
            "    \"os_replacement\":\"iOS\"," +
            "    \"os_v1_replacement\":\"7\"" +
            "}," +
            "{" +
            "    \"regex\":\"CFNetwork/7.* Darwin/(14)\\\\.\\\\d+\"," +
            "    \"os_replacement\":\"iOS\"," +
            "    \"os_v1_replacement\":\"8\"," +
            "    \"os_v2_replacement\":\"0\"" +
            "}," +
            "{" +
            "    \"regex\":\"\\\\b(iOS[ /]|iPhone(?:/| v|[ _]OS[/,]|; | OS : |\\\\d,\\\\d/|\\\\d,\\\\d; )|iPad/)(\\\\d{1,2})[_\\\\.](\\\\d{1,2})(?:[_\\\\.](\\\\d+))?\"," +
            "    \"os_replacement\":\"iOS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(CrOS) [a-z0-9_]+ (\\\\d+)\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?\"," +
            "    \"os_replacement\":\"Chrome OS\"" +
            "}," +
            "{" +
            "    \"regex\":\"([Dd]ebian)\"," +
            "    \"os_replacement\":\"Debian\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Linux Mint)(?:/(\\\\d+))?\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Mandriva)(?: Linux)?/(?:[\\\\d.-]+m[a-z]{2}(\\\\d+).(\\\\d))?\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Symbian[Oo][Ss])[/ ](\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"os_replacement\":\"Symbian OS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Symbian/3).+NokiaBrowser/7\\\\.3\"," +
            "    \"os_replacement\":\"Symbian^3 Anna\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Symbian/3).+NokiaBrowser/7\\\\.4\"," +
            "    \"os_replacement\":\"Symbian^3 Belle\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Symbian/3)\"," +
            "    \"os_replacement\":\"Symbian^3\"" +
            "}," +
            "{" +
            "    \"regex\":\"\\\\b(Series 60|SymbOS|S60Version|S60V\\\\d|S60\\\\b)\"," +
            "    \"os_replacement\":\"Symbian OS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(MeeGo)\"" +
            "}," +
            "{" +
            "    \"regex\":\"Symbian [Oo][Ss]\"," +
            "    \"os_replacement\":\"Symbian OS\"" +
            "}," +
            "{" +
            "    \"regex\":\"Series40;\"," +
            "    \"os_replacement\":\"Nokia Series 40\"" +
            "}," +
            "{" +
            "    \"regex\":\"Series30Plus;\"," +
            "    \"os_replacement\":\"Nokia Series 30 Plus\"" +
            "}," +
            "{" +
            "    \"regex\":\"(BB10);.+Version/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"os_replacement\":\"BlackBerry OS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Black[Bb]erry)[0-9a-z]+/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?\"," +
            "    \"os_replacement\":\"BlackBerry OS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Black[Bb]erry).+Version/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?\"," +
            "    \"os_replacement\":\"BlackBerry OS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(RIM Tablet OS) (\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"os_replacement\":\"BlackBerry Tablet OS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Play[Bb]ook)\"," +
            "    \"os_replacement\":\"BlackBerry Tablet OS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Black[Bb]erry)\"," +
            "    \"os_replacement\":\"BlackBerry OS\"" +
            "}," +
            "{" +
            "    \"regex\":\"\\\\((?:Mobile|Tablet);.+Firefox/\\\\d+\\\\.\\\\d+\"," +
            "    \"os_replacement\":\"Firefox OS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(BREW)[ /](\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(BREW);\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Brew MP|BMP)[ /](\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"os_replacement\":\"Brew MP\"" +
            "}," +
            "{" +
            "    \"regex\":\"BMP;\"," +
            "    \"os_replacement\":\"Brew MP\"" +
            "}," +
            "{" +
            "    \"regex\":\"(GoogleTV)(?: (\\\\d+)\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?|/[\\\\da-z]+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(WebTV)/(\\\\d+).(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(hpw|web)OS/(\\\\d+)\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?\"," +
            "    \"os_replacement\":\"webOS\"" +
            "}," +
            "{" +
            "    \"regex\":\"(VRE);\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Fedora|Red Hat|PCLinuxOS|Puppy|Ubuntu|Kindle|Bada|Lubuntu|BackTrack|Slackware|(?:Free|Open|Net|\\\\b)BSD)[/ ](\\\\d+)\\\\.(\\\\d+)(?:\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?)?\""
            +
            "}," +
            "{" +
            "    \"regex\":\"(Linux)[ /](\\\\d+)\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?.*gentoo\"," +
            "    \"os_replacement\":\"Gentoo\"" +
            "}," +
            "{" +
            "    \"regex\":\"\\\\((Bada);\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Windows|Android|WeTab|Maemo)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Ubuntu|Kubuntu|Arch Linux|CentOS|Slackware|Gentoo|openSUSE|SUSE|Red Hat|Fedora|PCLinuxOS|Gentoo|Mageia|(?:Free|Open|Net|\\\\b)BSD)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Linux)(?:[ /](\\\\d+)\\\\.(\\\\d+)(?:\\\\.(\\\\d+))?)?\"" +
            "}," +
            "{" +
            "    \"regex\":\"SunOS\"," +
            "    \"os_replacement\":\"Solaris\"" +
            "}" +
            "]";

    public static final String DEVICE_PARSERS = "[" +
            "{" +
            "    \"regex\":\"HTC ([A-Z][a-z0-9]+) Build\"," +
            "    \"device_replacement\":\"HTC $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"HTC ([A-Z][a-z0-9 ]+) \\\\d+\\\\.\\\\d+\\\\.\\\\d+\\\\.\\\\d+\"," +
            "    \"device_replacement\":\"HTC $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"HTC_Touch_([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"HTC Touch ($1)\"" +
            "}," +
            "{" +
            "    \"regex\":\"USCCHTC(\\\\d+)\"," +
            "    \"device_replacement\":\"HTC $1 (US Cellular)\"" +
            "}," +
            "{" +
            "    \"regex\":\"Sprint APA(9292)\"," +
            "    \"device_replacement\":\"HTC $1 (Sprint)\"" +
            "}," +
            "{" +
            "    \"regex\":\"HTC ([A-Za-z0-9]+ [A-Z])\"," +
            "    \"device_replacement\":\"HTC $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"HTC[-_/\\\\s]([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"HTC $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(ADR[A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"HTC $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(HTC)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(QtCarBrowser)\"," +
            "    \"device_replacement\":\"Tesla Model S\"" +
            "}," +
            "{" +
            "    \"regex\":\"(SamsungSGHi560)\"," +
            "    \"device_replacement\":\"Samsung SGHi560\"" +
            "}," +
            "{" +
            "    \"regex\":\"(SCH-[A-Za-z0-9_-]+)\"," +
            "    \"device_replacement\":\"Samsung $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(SGH-[A-Za-z0-9_-]+)\"," +
            "    \"device_replacement\":\"Samsung $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(GT-[A-Za-z0-9_-]+)\"," +
            "    \"device_replacement\":\"Samsung $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(SM-[A-Za-z0-9_-]+)\"," +
            "    \"device_replacement\":\"Samsung $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(SPH-[A-Za-z0-9_-]+)\"," +
            "    \"device_replacement\":\"Samsung $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"SAMSUNG-([A-Za-z0-9_-]+)\"," +
            "    \"device_replacement\":\"Samsung $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"SAMSUNG ([A-Za-z0-9_-]+)\"," +
            "    \"device_replacement\":\"Samsung $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"SonyEricsson([A-Za-z0-9]+)/\"," +
            "    \"device_replacement\":\"Ericsson $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"PLAYSTATION 3\"," +
            "    \"device_replacement\":\"PlayStation 3\"" +
            "}," +
            "{" +
            "    \"regex\":\"(PlayStation (:?Portable|Vita))\"" +
            "}," +
            "{" +
            "    \"regex\":\"(PlayStation (:?\\\\d+))\"" +
            "}," +
            "{" +
            "    \"regex\":\"(KFOT Build)\"," +
            "    \"device_replacement\":\"Kindle Fire\"" +
            "}," +
            "{" +
            "    \"regex\":\"(KFTT Build)\"," +
            "    \"device_replacement\":\"Kindle Fire HD\"" +
            "}," +
            "{" +
            "    \"regex\":\"(KFJWI Build)\"," +
            "    \"device_replacement\":\"Kindle Fire HD 8.9\\\" WiFi\"" +
            "}," +
            "{" +
            "    \"regex\":\"(KFJWA Build)\"," +
            "    \"device_replacement\":\"Kindle Fire HD 8.9\\\" 4G\"" +
            "}," +
            "{" +
            "    \"regex\":\"(KFSOWI Build)\"," +
            "    \"device_replacement\":\"Kindle Fire HD 7\\\" WiFi\"" +
            "}," +
            "{" +
            "    \"regex\":\"(KFTHWI Build)\"," +
            "    \"device_replacement\":\"Kindle Fire HDX 7\\\" WiFi\"" +
            "}," +
            "{" +
            "    \"regex\":\"(KFTHWA Build)\"," +
            "    \"device_replacement\":\"Kindle Fire HDX 7\\\" 4G\"" +
            "}," +
            "{" +
            "    \"regex\":\"(KFAPWI Build)\"," +
            "    \"device_replacement\":\"Kindle Fire HDX 8.9\\\" WiFi\"" +
            "}," +
            "{" +
            "    \"regex\":\"(KFAPWA Build)\"," +
            "    \"device_replacement\":\"Kindle Fire HDX 8.9\\\" 4G\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Kindle Fire)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Kindle)\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Silk)/(\\\\d+)\\\\.(\\\\d+)(?:\\\\.([0-9\\\\-]+))?\"," +
            "    \"device_replacement\":\"Kindle Fire\"" +
            "}," +
            "{" +
            "    \"regex\":\"NokiaN([0-9]+)\"," +
            "    \"device_replacement\":\"Nokia N$1\"" +
            "}," +
            "{" +
            "    \"regex\":\"NOKIA([A-Za-z0-9\\\\v-]+)\"," +
            "    \"device_replacement\":\"Nokia $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Nokia([A-Za-z0-9\\\\v-]+)\"," +
            "    \"device_replacement\":\"Nokia $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"NOKIA ([A-Za-z0-9\\\\-]+)\"," +
            "    \"device_replacement\":\"Nokia $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Nokia ([A-Za-z0-9\\\\-]+)\"," +
            "    \"device_replacement\":\"Nokia $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Lumia ([A-Za-z0-9\\\\-]+)\"," +
            "    \"device_replacement\":\"Lumia $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Symbian\"," +
            "    \"device_replacement\":\"Nokia\"" +
            "}," +
            "{" +
            "    \"regex\":\"BB10; ([A-Za-z0-9\\\\- ]+)\\\\)\"," +
            "    \"device_replacement\":\"BlackBerry $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(PlayBook).+RIM Tablet OS\"," +
            "    \"device_replacement\":\"BlackBerry Playbook\"" +
            "}," +
            "{" +
            "    \"regex\":\"Black[Bb]erry ([0-9]+);\"," +
            "    \"device_replacement\":\"BlackBerry $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Black[Bb]erry([0-9]+)\"," +
            "    \"device_replacement\":\"BlackBerry $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Black[Bb]erry;\"," +
            "    \"device_replacement\":\"BlackBerry\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Pre)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"device_replacement\":\"Palm Pre\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Pixi)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"device_replacement\":\"Palm Pixi\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Touch[Pp]ad)/(\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"device_replacement\":\"HP TouchPad\"" +
            "}," +
            "{" +
            "    \"regex\":\"HPiPAQ([A-Za-z0-9]+)/(\\\\d+).(\\\\d+)\"," +
            "    \"device_replacement\":\"HP iPAQ $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Palm([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Palm $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Treo([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Palm Treo $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"webOS.*(P160UNA)/(\\\\d+).(\\\\d+)\"," +
            "    \"device_replacement\":\"HP Veer\"" +
            "}," +
            "{" +
            "    \"regex\":\"(AppleTV)\"," +
            "    \"device_replacement\":\"AppleTV\"" +
            "}," +
            "{" +
            "    \"regex\":\"AdsBot-Google-Mobile\"," +
            "    \"device_replacement\":\"Spider\"" +
            "}," +
            "{" +
            "    \"regex\":\"Googlebot-Mobile/(\\\\d+).(\\\\d+)\"," +
            "    \"device_replacement\":\"Spider\"" +
            "}," +
            "{" +
            "    \"regex\":\"Googlebot/\\\\d+.\\\\d+\"," +
            "    \"device_replacement\":\"Spider\"" +
            "}," +
            "{" +
            "    \"regex\":\"NING/(\\\\d+).(\\\\d+)\"," +
            "    \"device_replacement\":\"Spider\"" +
            "}," +
            "{" +
            "    \"regex\":\"MsnBot-Media /(\\\\d+).(\\\\d+)\"," +
            "    \"device_replacement\":\"Spider\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iPad) Simulator;\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iPad);\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iPod) touch;\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iPod);\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iPhone) Simulator;\"" +
            "}," +
            "{" +
            "    \"regex\":\"(iPhone);\"" +
            "}," +
            "{" +
            "    \"regex\":\"acer_([A-Za-z0-9]+)_\"," +
            "    \"device_replacement\":\"Acer $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"acer_([A-Za-z0-9]+)_\"," +
            "    \"device_replacement\":\"Acer $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"ALCATEL-([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Alcatel $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Alcatel-([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Alcatel $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"ALCATEL_ONE_TOUCH_([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Alcatel ONE TOUCH $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"ALCATEL (ONE TOUCH [A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Alcatel $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"ALCATEL (one touch [A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Alcatel $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"ALCATEL ([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Alcatel $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Amoi\\\\-([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Amoi $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"AMOI\\\\-([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Amoi $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Asus\\\\-([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Asus $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"ASUS\\\\-([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Asus $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"BIRD\\\\-([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Bird $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"BIRD\\\\.([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Bird $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"BIRD ([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Bird $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Dell ([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Dell $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"DoCoMo/2\\\\.0 ([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"DoCoMo $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"([A-Za-z0-9]+)_W\\\\;FOMA\"," +
            "    \"device_replacement\":\"DoCoMo $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"([A-Za-z0-9]+)\\\\;FOMA\"," +
            "    \"device_replacement\":\"DoCoMo $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Huawei([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Huawei $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"HUAWEI-([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Huawei $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"vodafone([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Huawei Vodafone $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"i\\\\-mate ([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"i-mate $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Kyocera\\\\-([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Kyocera $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"KWC\\\\-([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Kyocera $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Lenovo\\\\-([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Lenovo $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Lenovo_([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Lenovo $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(HbbTV)/[0-9]+\\\\.[0-9]+\\\\.[0-9]+\"" +
            "}," +
            "{" +
            "    \"regex\":\"LG/([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"LG $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"LG-LG([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"LG $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"LGE-LG([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"LG $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"LGE VX([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"LG $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"LG ([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"LG $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"LGE LG\\\\-AX([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"LG $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"LG\\\\-([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"LG $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"LGE\\\\-([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"LG $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"LG([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"LG $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(KIN)\\\\.One (\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"device_replacement\":\"Microsoft $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(KIN)\\\\.Two (\\\\d+)\\\\.(\\\\d+)\"," +
            "    \"device_replacement\":\"Microsoft $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Motorola)\\\\-([A-Za-z0-9]+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"MOTO\\\\-([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Motorola $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"MOT\\\\-([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Motorola $1\"" +
            "}," +
            "{" +
            "    \"regex\":\" (DROID RAZR [A-Za-z0-9 ]+) \"," +
            "    \"device_replacement\":\"Motorola $1\"" +
            "}," +
            "{" +
            "    \"regex\":\" (DROID[2 ][A-Za-z0-9 ]+) \"," +
            "    \"device_replacement\":\"Motorola $1\"" +
            "}," +
            "{" +
            "    \"regex\":\" (Droid2| )\"," +
            "    \"device_replacement\":\"Motorola $1\"" +
            "}," +
            "{" +
            "    \"regex\":\" (DROID2| )\"," +
            "    \"device_replacement\":\"Motorola $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(Nintendo WiiU)\"," +
            "    \"device_replacement\":\"Nintendo Wii U\"" +
            "}," +
            "{" +
            "    \"regex\":\"Nintendo (DS|3DS|DSi|Wii);\"," +
            "    \"device_replacement\":\"Nintendo $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Pantech([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Pantech $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Philips([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Philips $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Philips ([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Philips $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"SAMSUNG-([A-Za-z0-9\\\\-]+)\"," +
            "    \"device_replacement\":\"Samsung $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"SAMSUNG\\\\; ([A-Za-z0-9\\\\-]+)\"," +
            "    \"device_replacement\":\"Samsung $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"ZTE-([A-Za-z0-9\\\\-]+)\"," +
            "    \"device_replacement\":\"ZTE $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"ZTE ([A-Za-z0-9\\\\-]+)\"," +
            "    \"device_replacement\":\"ZTE $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"ZTE_([A-Za-z0-9\\\\-]+)\"," +
            "    \"device_replacement\":\"ZTE $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Dreamcast\"," +
            "    \"device_replacement\":\"Sega Dreamcast\"" +
            "}," +
            "{" +
            "    \"regex\":\"Softbank/1\\\\.0/([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Softbank $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Softbank/2\\\\.0/([A-Za-z0-9]+)\"," +
            "    \"device_replacement\":\"Softbank $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"Sony([^ ]+) \"," +
            "    \"device_replacement\":\"Sony $1\"" +
            "}," +
            "{" +
            "    \"regex\":\"(WebTV)/(\\\\d+).(\\\\d+)\"" +
            "}," +
            "{" +
            "    \"regex\":\"Android[\\\\- ][\\\\d]+\\\\.[\\\\d]+\\\\.[\\\\d]+; [^;]+; ([A-Za-z0-9 _-]+) \"" +
            "}," +
            "{" +
            "    \"regex\":\"Android[\\\\- ][\\\\d]+\\\\.[\\\\d]+; [^;]+; ([A-Za-z0-9 _-]+) \"" +
            "}," +
            "{" +
            "    \"regex\":\"Android[\\\\- ][\\\\d]+\\\\.[\\\\d]+; [^;]+; WOWMobile ([A-Za-z0-9 _-]+) \"" +
            "}," +
            "{" +
            "    \"regex\":\"Android[\\\\- ][\\\\d]+\\\\.[\\\\d]+\\\\-update1; [^;]+; ([A-Za-z0-9 _-]+) \"" +
            "}," +
            "{" +
            "    \"regex\":\"Android[\\\\- ][\\\\d]+\\\\.[\\\\d]+\\\\.[\\\\d]+;[^;]+;([A-Za-z0-9 _-]+) \"" +
            "}," +
            "{" +
            "    \"regex\":\"Android[\\\\- ][\\\\d]+\\\\.[\\\\d]+\\\\.[\\\\d]+; ([A-Za-z0-9 _-]+) \"" +
            "}," +
            "{" +
            "    \"regex\":\"Android[\\\\- ][\\\\d]+\\\\.[\\\\d]+; ([A-Za-z0-9 _-]+) \"" +
            "}," +
            "{" +
            "    \"regex\":\"Android[\\\\- ][\\\\d]+\\\\.[\\\\d]+\\\\.[\\\\d]+; [^;]+; ([A-Za-z0-9\\\\.\\\\/_-]+) \"" +
            "}," +
            "{" +
            "    \"regex\":\"Android[\\\\- ][\\\\d]+\\\\.[\\\\d]+; [^;]+; ([A-Za-z0-9\\\\.\\\\/_-]+) \"" +
            "}," +
            "{" +
            "    \"regex\":\"(hiptop|avantgo|plucker|xiino|blazer|elaine|up.browser|up.link|mmp|smartphone|midp|wap|vodafone|o2|pocket|mobile|pda)\"," +
            "    \"device_replacement\":\"Generic Smartphone\"" +
            "}," +
            "{" +
            "    \"regex\":\"^(1207|3gso|4thp|501i|502i|503i|504i|505i|506i|6310|6590|770s|802s|a wa|acer|acs\\\\-|airn|alav|asus|attw|au\\\\-m|aur |aus |abac|acoo|aiko|alco|alca|amoi|anex|anny|anyw|aptu|arch|argo|bell|bird|bw\\\\-n|bw\\\\-u|beck|benq|bilb|blac|c55/|cdm\\\\-|chtm|capi|comp|cond|craw|dall|dbte|dc\\\\-s|dica|ds\\\\-d|ds12|dait|devi|dmob|doco|dopo|el49|erk0|esl8|ez40|ez60|ez70|ezos|ezze|elai|emul|eric|ezwa|fake|fly\\\\-|fly_|g\\\\-mo|g1 u|g560|gf\\\\-5|grun|gene|go.w|good|grad|hcit|hd\\\\-m|hd\\\\-p|hd\\\\-t|hei\\\\-|hp i|hpip|hs\\\\-c|htc |htc\\\\-|htca|htcg)\","
            +
            "    \"device_replacement\":\"Generic Feature Phone\"" +
            "}," +
            "{" +
            "    \"regex\":\"^(htcp|htcs|htct|htc_|haie|hita|huaw|hutc|i\\\\-20|i\\\\-go|i\\\\-ma|i230|iac|iac\\\\-|iac/|ig01|im1k|inno|iris|jata|java|kddi|kgt|kgt/|kpt |kwc\\\\-|klon|lexi|lg g|lg\\\\-a|lg\\\\-b|lg\\\\-c|lg\\\\-d|lg\\\\-f|lg\\\\-g|lg\\\\-k|lg\\\\-l|lg\\\\-m|lg\\\\-o|lg\\\\-p|lg\\\\-s|lg\\\\-t|lg\\\\-u|lg\\\\-w|lg/k|lg/l|lg/u|lg50|lg54|lge\\\\-|lge/|lynx|leno|m1\\\\-w|m3ga|m50/|maui|mc01|mc21|mcca|medi|meri|mio8|mioa|mo01|mo02|mode|modo|mot |mot\\\\-|mt50|mtp1|mtv |mate|maxo|merc|mits|mobi|motv|mozz|n100|n101|n102|n202|n203|n300|n302|n500|n502|n505|n700|n701|n710|nec\\\\-|nem\\\\-|newg|neon)\","
            +
            "    \"device_replacement\":\"Generic Feature Phone\"" +
            "}," +
            "{" +
            "    \"regex\":\"^(netf|noki|nzph|o2 x|o2\\\\-x|opwv|owg1|opti|oran|ot\\\\-s|p800|pand|pg\\\\-1|pg\\\\-2|pg\\\\-3|pg\\\\-6|pg\\\\-8|pg\\\\-c|pg13|phil|pn\\\\-2|pt\\\\-g|palm|pana|pire|pock|pose|psio|qa\\\\-a|qc\\\\-2|qc\\\\-3|qc\\\\-5|qc\\\\-7|qc07|qc12|qc21|qc32|qc60|qci\\\\-|qwap|qtek|r380|r600|raks|rim9|rove|s55/|sage|sams|sc01|sch\\\\-|scp\\\\-|sdk/|se47|sec\\\\-|sec0|sec1|semc|sgh\\\\-|shar|sie\\\\-|sk\\\\-0|sl45|slid|smb3|smt5|sp01|sph\\\\-|spv |spv\\\\-|sy01|samm|sany|sava|scoo|send|siem|smar|smit|soft|sony|t\\\\-mo|t218|t250|t600|t610|t618|tcl\\\\-|tdg\\\\-|telm|tim\\\\-|ts70|tsm\\\\-|tsm3|tsm5|tx\\\\-9|tagt)\","
            +
            "    \"device_replacement\":\"Generic Feature Phone\"" +
            "}," +
            "{" +
            "    \"regex\":\"^(talk|teli|topl|tosh|up.b|upg1|utst|v400|v750|veri|vk\\\\-v|vk40|vk50|vk52|vk53|vm40|vx98|virg|vite|voda|vulc|w3c |w3c\\\\-|wapj|wapp|wapu|wapm|wig |wapi|wapr|wapv|wapy|wapa|waps|wapt|winc|winw|wonu|x700|xda2|xdag|yas\\\\-|your|zte\\\\-|zeto|aste|audi|avan|blaz|brew|brvw|bumb|ccwa|cell|cldc|cmd\\\\-|dang|eml2|fetc|hipt|http|ibro|idea|ikom|ipaq|jbro|jemu|jigs|keji|kyoc|kyok|libw|m\\\\-cr|midp|mmef|moto|mwbp|mywa|newt|nok6|o2im|pant|pdxg|play|pluc|port|prox|rozo|sama|seri|smal|symb|treo|upsi|vx52|vx53|vx60|vx61|vx70|vx80|vx81|vx83|vx85|wap\\\\-|webc|whit|wmlb|xda\\\\-|xda_)\","
            +
            "    \"device_replacement\":\"Generic Feature Phone\"" +
            "}," +
            "{" +
            "    \"regex\":\"(bingbot|bot|borg|google(^tv)|yahoo|slurp|msnbot|msrbot|openbot|archiver|netresearch|lycos|scooter|altavista|teoma|gigabot|baiduspider|blitzbot|oegp|charlotte|furlbot|http%20client|polybot|htdig|ichiro|mogimogi|larbin|pompos|scrubby|searchsight|seekbot|semanticdiscovery|silk|snappy|speedy|spider|voila|vortex|voyager|zao|zeal|fast\\\\-webcrawler|converacrawler|dataparksearch|findlinks|crawler|Netvibes|Sogou Pic Spider|ICC\\\\-Crawler|Innovazion Crawler|Daumoa|EtaoSpider|A6\\\\-Indexer|YisouSpider|Riddler|DBot|wsr\\\\-agent|Xenu|SeznamBot|PaperLiBot|SputnikBot|CCBot|ProoXiBot|Scrapy|Genieo|Screaming Frog|YahooCacheSystem|CiBra|Nutch)\","
            +
            "    \"device_replacement\":\"Spider\"" +
            "}" +
            "]";
            //CHECKSTYLE.ON:
}
