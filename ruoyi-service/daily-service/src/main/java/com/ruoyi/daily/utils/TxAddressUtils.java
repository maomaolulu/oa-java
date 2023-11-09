package com.ruoyi.daily.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

import java.util.Map;

public class TxAddressUtils {
    /**
     * 根据坐标查询地址
     * @return
     */
    public static String getAddress(String location) {

        String url = "https://apis.map.qq.com/ws/geocoder/v1";
        Map<String, Object> parameters = Maps.newHashMap();
        // https://apis.map.qq.com/ws/geocoder/v1/?location=39.984154,116.307490&key=OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77&get_poi=1
        parameters.put("key", "QEFBZ-ULBKU-IRVV3-BZYJF-H2UUZ-CFBRW");
        parameters.put("get_poi", 1);
        parameters.put("location", location);
        String jsonResult = HttpUtil.get(url, parameters);
        if (StrUtil.isNotBlank(jsonResult)) {
            JSONObject resultJson = JSON.parseObject(jsonResult);
            System.err.println(resultJson);
            JSONObject jsonObject = JSONObject.parseObject(resultJson.get("result").toString());

            return jsonObject.get("address").toString();
        }
        return "";
    }

}
