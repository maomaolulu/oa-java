package com.ruoyi.activiti.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.json.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 小程序消息订阅工具
 *
 * @author zx
 * @date 2022/2/22 16:16
 */
@Slf4j
public class SubscriptionUtil {
    private SubscriptionUtil() {
    }

    /**
     * 评论消息
     *
     * @param openId
     * @param ting2  任务名称
     * @param thing3 评论内容
     * @param name4  评论人
     * @param date5  评论时间
     */

    public static void commentSend(String openId, String ting2, String thing3, String name4, String date5) {
        String token = getToken();
        Map<String, Object> map2 = new HashMap<>(5);
        map2.put("touser", openId);
        map2.put("template_id", "Mp21vEhPpGc4S-xJkEMNP7IRcaw0kuLQasHUYKlU_pY");
        map2.put("lang", "zh_CN");
        map2.put("miniprogram_state", "developer");
        Map<String, Object> map3 = new HashMap<>(4);
        map3.put("thing2", new HashMap<String, Object>(1) {{
            put("value", ting2);
        }});
        map3.put("thing3", new HashMap<String, Object>(1) {{
            put("value", thing3);
        }});
        map3.put("name4", new HashMap<String, Object>(1) {{
            put("value", name4);
        }});
        map3.put("date5", new HashMap<String, Object>(1) {{
            put("value", date5);
        }});

        map2.put("data", map3);
        String jsonStr = getJsonStr(map2);
        String post = HttpUtil.post("https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + token, jsonStr);
        log.info(post);
    }

    public static void auditSend(String openId, String ting2, String thing3, String name4, String date5) {
        String token = getToken();
        Map<String, Object> map2 = new HashMap<>(5);
        map2.put("touser", openId);
        map2.put("template_id", "Mp21vEhPpGc4S-xJkEMNP7IRcaw0kuLQasHUYKlU_pY");
        map2.put("lang", "zh_CN");
        map2.put("miniprogram_state", "developer");
        Map<String, Object> map3 = new HashMap<>(4);
        map3.put("thing2", new HashMap<String, Object>(1) {{
            put("value", ting2);
        }});
        map3.put("thing3", new HashMap<String, Object>(1) {{
            put("value", thing3);
        }});
        map3.put("name4", new HashMap<String, Object>(1) {{
            put("value", name4);
        }});
        map3.put("date5", new HashMap<String, Object>(1) {{
            put("value", date5);
        }});

        map2.put("data", map3);
        String jsonStr = getJsonStr(map2);
        String post = HttpUtil.post("https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + token, jsonStr);
        log.info(post);
    }

    private static String getToken() {
        Map<String, Object> map = new HashMap<>(3);
        map.put("grant_type", "client_credential");
        map.put("appid", "wx22f79ec3e21630a7");
        map.put("secret", "fe3adf98ad4b9063214f9e5889615192");
        String rspStr = HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token", map);
        JSONObject obj;
        String token = "";
        try {
            obj = JSON.unmarshal(rspStr, JSONObject.class);
            token = obj.getStr("access_token");
        } catch (Exception e) {
            log.error("获取token，json解析失败", e);
        }
        return token;
    }

    /**
     * map转jsonStr
     *
     * @param map
     * @return
     */
    private static String getJsonStr(Map<String, Object> map) {
        ObjectMapper json = new ObjectMapper();
        String params = null;
        try {
            //把map对象转成json格式的String字符串
            params = json.writeValueAsString(map);
            log.info(params);
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return params;
    }
}
