package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.AES;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 饭团电影
 * <p>
 * Author: 
 */
public class Fantuan extends Spider {
    private static final String siteUrl = "https://www.fantuanhd.com";
    private static final String siteHost = "www.fantuanhd.com";
    /**
     * 播放源配置
     */
    private JSONObject playerConfig;
    /**
     * 筛选配置
     */
    private JSONObject filterConfig;

    private Pattern regexCategory = Pattern.compile("/type/id-(\\w+).html");
    private Pattern regexVid = Pattern.compile("/detail/id-(\\w+).html");
    private Pattern regexPlay = Pattern.compile("/play/id-(\\w+)-(\\d+)-(\\d+).html");
    private Pattern regexPage = Pattern.compile("/show/id-(\\d+)/page/(\\d+).html");

    @Override
    public void init(Context context) {
        super.init(context);
        try {
            playerConfig = new JSONObject("{\"xg_app_player\":{\"show\":\"app全局解析\",\"or\":999,\"ps\":\"1\",\"parse\":\"\"},\"duoduozy\":{\"show\":\"自营极速\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"uploadixigua\":{\"show\":\"自营蓝光\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"gpmp4\":{\"show\":\"自营超清\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"rx\":{\"show\":\"融兴线路\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"xigua\":{\"show\":\"西瓜高清\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"mgtv\":{\"show\":\"芒果高清\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"qq\":{\"show\":\"腾讯高清\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"youku\":{\"show\":\"优酷高清\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"qiyi\":{\"show\":\"奇艺高清\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"pptv\":{\"show\":\"PPTV\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"letv\":{\"show\":\"乐视云\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"sohu\":{\"show\":\"搜狐云\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"bilibili\":{\"show\":\"哔哩云\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"dbm3u8\":{\"show\":\"百度云\",\"des\":\"在线播放\",\"ps\":\"0\",\"parse\":\"\"},\"tkm3u8\":{\"show\":\"天空云\",\"des\":\"天空播放器\",\"ps\":\"0\",\"parse\":\"\"},\"dplayer\":{\"show\":\"DPlayer-H5播放器\",\"des\":\"dplayer.js.org\",\"ps\":\"0\",\"parse\":\"\"},\"videojs\":{\"show\":\"videojs-H5播放器\",\"des\":\"videojs.com\",\"ps\":\"0\",\"parse\":\"\"},\"iva\":{\"show\":\"iva-H5播放器\",\"des\":\"videojj.com\",\"ps\":\"0\",\"parse\":\"\"},\"iframe\":{\"show\":\"iframe外链数据\",\"des\":\"iframe外链数据\",\"ps\":\"0\",\"parse\":\"\"},\"link\":{\"show\":\"外链数据\",\"des\":\"外部网站播放链接\",\"ps\":\"0\",\"parse\":\"\"},\"swf\":{\"show\":\"Flash文件\",\"des\":\"swf\",\"ps\":\"0\",\"parse\":\"\"},\"flv\":{\"show\":\"Flv文件\",\"des\":\"flv\",\"ps\":\"0\",\"parse\":\"\"},\"bdxm3u8\":{\"show\":\"北斗云\",\"des\":\"在线播放\",\"ps\":\"0\",\"parse\":\"\"},\"lym3u8\":{\"show\":\"老鸭资源\",\"des\":\"在线播放\",\"ps\":\"0\",\"parse\":\"\"},\"aliplayer\":{\"show\":\"阿里云播放器\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"hnm3u8\":{\"show\":\"红牛云\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"kbm3u8\":{\"show\":\"快播云\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"wjm3u8\":{\"show\":\"无尽备用\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"}}");
            filterConfig = new JSONObject("{\"1\":[{\"key\":\"tid\",\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"1\"},{\"n\":\"动作片\",\"v\":\"6\"},{\"n\":\"喜剧片\",\"v\":\"7\"},{\"n\":\"爱情片\",\"v\":\"8\"},{\"n\":\"科幻片\",\"v\":\"9\"},{\"n\":\"恐怖片\",\"v\":\"10\"},{\"n\":\"剧情片\",\"v\":\"11\"},{\"n\":\"战争片\",\"v\":\"12\"},{\"n\":\"犯罪片\",\"v\":\"20\"},{\"n\":\"惊悚片\",\"v\":\"21\"},{\"n\":\"冒险片\",\"v\":\"22\"},{\"n\":\"悬疑片\",\"v\":\"23\"},{\"n\":\"武侠片\",\"v\":\"24\"},{\"n\":\"奇幻片\",\"v\":\"25\"},{\"n\":\"纪录片\",\"v\":\"26\"},{\"n\":\"动画片\",\"v\":\"27\"}]},{\"key\":\"1\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"意大利\",\"v\":\"意大利\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"加拿大\",\"v\":\"加拿大\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"11\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"4\",\"name\":\"语言\",\"value\":[{\"n\":\"全部\",\"v\":\"\"}]}],\"2\":[{\"key\":\"tid\",\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"2\"},{\"n\":\"国产剧\",\"v\":\"13\"},{\"n\":\"港台剧\",\"v\":\"14\"},{\"n\":\"日韩剧\",\"v\":\"15\"},{\"n\":\"欧美剧\",\"v\":\"16\"},{\"n\":\"泰国剧\",\"v\":\"28\"}]},{\"key\":\"1\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"11\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"4\",\"name\":\"语言\",\"value\":[{\"n\":\"全部\",\"v\":\"\"}]}],\"4\":[{\"key\":\"3\",\"name\":\"剧情\",\"value\":[{\"n\":\"全部\",\"v\":\"3\"},{\"n\":\"情感\",\"v\":\"情感\"},{\"n\":\"科幻\",\"v\":\"科幻\"},{\"n\":\"热血\",\"v\":\"热血\"},{\"n\":\"推理\",\"v\":\"推理\"},{\"n\":\"搞笑\",\"v\":\"搞笑\"},{\"n\":\"冒险\",\"v\":\"冒险\"},{\"n\":\"萝莉\",\"v\":\"萝莉\"},{\"n\":\"校园\",\"v\":\"校园\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"机战\",\"v\":\"机战\"},{\"n\":\"运动\",\"v\":\"运动\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"少年\",\"v\":\"少年\"},{\"n\":\"少女\",\"v\":\"少女\"},{\"n\":\"社会\",\"v\":\"社会\"},{\"n\":\"原创\",\"v\":\"原创\"},{\"n\":\"亲子\",\"v\":\"亲子\"},{\"n\":\"益智\",\"v\":\"益智\"},{\"n\":\"励志\",\"v\":\"励志\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"1\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"内地\",\"v\":\"内地\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"11\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"4\",\"name\":\"语言\",\"value\":[{\"n\":\"全部\",\"v\":\"\"}]}]}");
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
    }

    /**
     * 爬虫headers
     *
     * @param url
     * @return
     */
    protected HashMap<String, String> getHeaders(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("method", "GET");
        if (!TextUtils.isEmpty(url)) {
            headers.put("Referer", url);
        }
//        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9");
        return headers;
    }

    /**
     * 获取分类数据 + 首页最近更新视频列表数据
     *
     * @param filter 是否开启筛选 关联的是 软件设置中 首页数据源里的筛选开关
     * @return
     */
    @Override
    public String homeContent(boolean filter) {
        try {
            Document doc = Jsoup.parse(OkHttpUtil.string(siteUrl, getHeaders(siteUrl)));
            // 分类节点
            Elements elements = doc.select("ul.stui-header__menu > li > a");
            JSONArray classes = new JSONArray();
            for (Element ele : elements) {
                String name = ele.text();            
                Matcher mather = regexCategory.matcher(ele.attr("href"));
                if (!mather.find())
                    continue;
                String id = mather.group(1).trim();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type_id", id);
                jsonObject.put("type_name", name);
                classes.put(jsonObject);
            
         }
            JSONObject result = new JSONObject();
            if (filter) {
                result.put("filters", filterConfig);
            }
            result.put("class", classes);
            try {
                // 取首页推荐视频列表
                Elements list = doc.select("ul.stui-vodlist:nth-child(3) li div.stui-vodlist__box");
                JSONArray videos = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);        
                    String matcher = "";
                    try {
                        Matcher matcher = regexVid.matcher(vod.selectFirst("div.stui-vodlist__detail h4 a").attr("href"));
                    } catch (Exception e) {
                        Matcher matcher = regexVid.matcher(vod.selectFirst(".stui-vodlist__thumb").attr("href"));
                    }
                    if (!matcher.find())
                        continue;
                    String title = vod.selectFirst(".stui-vodlist__thumb").attr("title");
                    String cover = vod.selectFirst(".stui-vodlist__thumb").attr("data-original");
//                    if (!TextUtils.isEmpty(cover) && !cover.startsWith("http")) {
 //                       cover = siteUrl + cover;
//                    }
                    String remark = vod.selectFirst("span.pic-text").text();
                    String id = matcher.group(1);
                    JSONObject v = new JSONObject();
                    v.put("vod_id", id);
                    v.put("vod_name", title);
                    v.put("vod_pic", cover);
                    v.put("vod_remarks", remark);
                    videos.put(v);
                }
                result.put("list", videos);
            } catch (Exception e) {
                SpiderDebug.log(e);
            }
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    /**
     * 获取分类信息数据
     *
     * @param tid    分类id
     * @param pg     页数
     * @param filter 同homeContent方法中的filter
     * @param extend 筛选参数{k:v, k1:v1}
     * @return
     */
    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        try {
            String url = siteUrl + "/show/id-";
            if (extend != null && extend.size() > 0 && extend.containsKey("tid") && extend.get("tid").length() > 0) {
                url += extend.get("tid");
            } else {
                url += tid;
            }
            if (extend != null && extend.size() > 0) {
                for (Iterator<String> it = extend.keySet().iterator(); it.hasNext(); ) {
                    String key = it.next();
                    String value = extend.get(key);
                    if (value.length() > 0) {
                        url += "/" + key + "/" + URLEncoder.encode(value);
                    }
                }
            }
            url += "/page/" + pg + ".html";
            // 获取分类数据的url
            //String url = siteUrl + "/show/" + tid + "/page/" + pg + "/";
            String html = OkHttpUtil.string(url, getHeaders(url));
            Document doc = Jsoup.parse(html);
            JSONObject result = new JSONObject();
            int pageCount = 0;
            int page = -1;
            // 取页码相关信息
            Elements pageInfo = doc.select("ul.stui-page__item > li");
            if (pageInfo.size() == 0) {
                page = Integer.parseInt(pg);
                pageCount = page;
            } else {
                for (int i = 0; i < pageInfo.size(); i++) {
                    Element li = pageInfo.get(i);
                    Element a = li.selectFirst("a");
                    if (a == null)
                        continue;
                    String name = a.text();
                    if (page == -1 && li.hasClass("active")) {
                        Matcher matcher = regexPage.matcher(a.attr("href"));
                        if (matcher.find()) {
                            page = Integer.parseInt(matcher.group(1));
                        } else {
                            page = 0;
                        }
                    }
                    if (name.equals("尾页")) {
                        Matcher matcher = regexPage.matcher(a.attr("href"));
                        if (matcher.find()) {
                            pageCount = Integer.parseInt(matcher.group(1));
                        } else {
                            pageCount = 0;
                        }
                        break;
                    }
                }
            }

            JSONArray videos = new JSONArray();
            if (!html.contains("没有找到您想要的结果哦")) {         // 取当前分类页的视频列表
                Elements list = doc.select("ul.stui-vodlist li div.stui-vodlist__box");
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst(".stui-vodlist__thumb").attr("title");
                    String cover = vod.selectFirst(".stui-vodlist__thumb").attr("data-original");
                    if (!TextUtils.isEmpty(cover) && !cover.startsWith("http")) {
                        cover = siteUrl + cover;
                    }
                    String remark = vod.selectFirst("span.pic-text").text();
                    Matcher matcher = regexVid.matcher(vod.selectFirst("div.stui-vodlist__detail h4 a").attr("href"));
                    if (!matcher.find())
                        continue;
                    String id = matcher.group(1);
                    JSONObject v = new JSONObject();
                    v.put("vod_id", id);
                    v.put("vod_name", title);
                    v.put("vod_pic", cover);
                    v.put("vod_remarks", remark);
                    videos.put(v);
                }
            }
            result.put("page", page);
            result.put("pagecount", pageCount);
            result.put("limit", 30);
            result.put("total", pageCount <= 1 ? videos.length() : pageCount * 30);

            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }
    
    private static String Regex(Pattern pattern, String content) {
        if (pattern == null) {
            return content;
        }
        try {
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return content;
    }

    /**
     * 视频详情信息
     *
     * @param ids 视频id
     * @return
     */
    @Override
    public String detailContent(List<String> ids) {
        try {
            // 视频详情url
            String url = siteUrl + "/detail/id-" + ids.get(0) + ".html";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();
            JSONObject vodList = new JSONObject();

            // 取基本数据            
            String cover = doc.selectFirst("a.pic img").attr("data-original");
 //           if (!TextUtils.isEmpty(cover) && !cover.startsWith("http")) {
//                cover = siteUrl + cover;
//            }
            String title = doc.selectFirst("a.pic").attr("title");
            String category = "", area = "", year = "", remark = "", director = "", actor = "", desc = "";
            Elements data = doc.select("p.data");
            desc = doc.selectFirst("span.detail-content").text().trim();
            category = Regex(Pattern.compile("类型：(\\S+)"), data.get(0).text());
            area = Regex(Pattern.compile("地区：(\\S+)"), data.get(0).text());
            year = Regex(Pattern.compile("年份：(\\S+)"), data.get(0).text());
            actor = Regex(Pattern.compile("主演：(\\S+)"), data.get(1).text());
            director = Regex(Pattern.compile("导演：(\\S+)"), data.get(1).text());

            vodList.put("vod_id", ids.get(0));
            vodList.put("vod_name", title);
            vodList.put("vod_pic", cover);
            vodList.put("type_name", category);
            vodList.put("vod_year", year);
            vodList.put("vod_area", area);
            vodList.put("vod_remarks", remark);
            vodList.put("vod_actor", actor);
            vodList.put("vod_director", director);
            vodList.put("vod_content", desc);

            Map<String, String> vod_play = new TreeMap<>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    try {
                        int sort1 = playerConfig.getJSONObject(o1).getInt("or");
                        int sort2 = playerConfig.getJSONObject(o2).getInt("or");

                        if (sort1 == sort2) {
                            return 1;
                        }
                        return sort1 - sort2 > 0 ? 1 : -1;
                    } catch (JSONException e) {
                        SpiderDebug.log(e);
                    }
                    return 1;
                }
            });
            // 取播放列表数据
//            Elements sources = doc.select("div.stui-pannel__bd");
            Elements sources = doc.select("div.stui-vodlist__head");
            for (int i = 0; i < sources.size(); i++) {
                Element source = sources.get(i);
                String sourceName = source.selectFirst("h3").text().trim();
//                String tabHref = source.attr("href");
                boolean found = false;
                for (Iterator<String> it = playerConfig.keys(); it.hasNext(); ) {
                    String flag = it.next();
                    if (playerConfig.getJSONObject(flag).getString("show").equals(sourceName)) {
                        sourceName = playerConfig.getJSONObject(flag).getString("show");
                        found = true;
                        break;
                    }
                }
                if (!found)
                    continue;
                String playList = "";
                Elements playListA = source.select("ul.stui-content__playlist > li > a");
                List<String> vodItems = new ArrayList<>();

                for (int j = 0; j < playListA.size(); j++) {
                    Element vod = playListA.get(j);
                    Matcher matcher = regexPlay.matcher(vod.attr("href"));
                    if (!matcher.find())
                        continue;
                    String playURL = matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3);
                    vodItems.add(vod.text() + "$" + playURL);
                }
                if (vodItems.size() > 0)
                    playList = TextUtils.join("#", vodItems);

                if (playList.length() == 0)
                    continue;

                vod_play.put(sourceName, playList);
            }

            if (vod_play.size() > 0) {
                String vod_play_from = TextUtils.join("$$$", vod_play.keySet());
                String vod_play_url = TextUtils.join("$$$", vod_play.values());
                vodList.put("vod_play_from", vod_play_from);
                vodList.put("vod_play_url", vod_play_url);
            }
            JSONArray list = new JSONArray();
            list.put(vodList);
            result.put("list", list);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    private final Pattern regexUrl = Pattern.compile("(?<=urls = \").+?(?=\";)");
    /**
     * 获取视频播放信息
     *
     * @param flag     播放源
     * @param id       视频id
     * @param vipFlags 所有可能需要vip解析的源
     * @return
     */
    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            JSONObject headers = new JSONObject();
            headers.put("Origin", " https://api.10static.com");
            //headers.put("Referer", " http://www.1010dy1.com/");
            //headers.put("Icy-MetaData", "1");
            //headers.put("scheme", " https");
            headers.put("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
            headers.put("Accept", " */*");
            //headers.put("Accept-Language", " zh-CN,zh;q=0.9,en-GB;q=0.8,en-US;q=0.7,en;q=0.6");
            //headers.put("Accept-Encoding", " gzip, deflate");
            headers.put("Connection", " close");
            // 播放页 url
            String url = siteUrl + "/play/id-" + id + ".html";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            Elements allScript = doc.select("script");
            JSONObject result = new JSONObject();
            for (int i = 0; i < allScript.size(); i++) {
                String scContent = allScript.get(i).html().trim();
                if (scContent.startsWith("var player_")) {
                    int start = scContent.indexOf('{');
                    int end = scContent.lastIndexOf('}') + 1;
                    String json = scContent.substring(start, end);
                    JSONObject player = new JSONObject(json);
                    if (playerConfig.has(player.getString("from"))) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
                        hashMap.put("Referer", url);
                        String jxurl = "https://api.10static.com/?url=" + player.getString("url")+"&next="+player.getString("link_next")+"&id="+player.getString("id")+"&nid="+player.getString("nid")+"&from="+player.getString("from");
                        String doc1 = OkHttpUtil.string(jxurl, hashMap);
                        Matcher matcher = regexUrl.matcher(doc1);
                        if (!matcher.find()) {
                            return "";
                        }
                        String data = matcher.group(0);
//                        byte[] decode = Base64.decode(data, Base64.DEFAULT);
                        String iv = "c487ebl2e38a0faO";
                        String key = "Of84ff0clf252cba";
                        String directUrl = AES.CBC(data, key, iv);
                        if (player.getString("from").equals("duoduozy")) {
                            result.put("parse", 0);
                            result.put("playUrl", "");
                            result.put("url", directUrl);
                            result.put("header", headers.toString());
                        } else {
                            result.put("parse", 0);
                            result.put("playUrl", "");
                            result.put("url", directUrl);
                            result.put("header", "");
                        }
                    }
                }
            }
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    /**
     * 搜索
     *
     * @param key
     * @param quick 是否播放页的快捷搜索
     * @return
     */
    @Override
    public String searchContent(String key, boolean quick) {
        try {
            long currentTime = System.currentTimeMillis();
            String url = siteUrl + "/index.php/ajax/suggest?mid=1&wd=" + URLEncoder.encode(key) + "&limit=10&timestamp=" + currentTime;
            JSONObject searchResult = new JSONObject(OkHttpUtil.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            if (searchResult.getInt("total") > 0) {
                JSONArray lists = new JSONArray(searchResult.getString("list"));
                for (int i = 0; i < lists.length(); i++) {
                    JSONObject vod = lists.getJSONObject(i);
                    String id = vod.getString("id");
                    String title = vod.getString("name");
                    String cover = vod.getString("pic");
                    JSONObject v = new JSONObject();
                    v.put("vod_id", id);
                    v.put("vod_name", title);
                    v.put("vod_pic", cover);
                    v.put("vod_remarks", "");
                    videos.put(v);
                }
            }
            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }
}
