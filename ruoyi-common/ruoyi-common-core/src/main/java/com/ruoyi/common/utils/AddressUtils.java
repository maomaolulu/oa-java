package com.ruoyi.common.utils;

import cn.hutool.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruoyi.common.json.JSON;
import com.ruoyi.common.json.JSONObject;
import com.ruoyi.common.utils.http.HttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取地址类
 * 
 * @author ruoyi
 */
public class AddressUtils
{
    private static final Logger log    = LoggerFactory.getLogger(AddressUtils.class);

    public static final String  IP_URL = "http://47.114.172.13";

    public static String getRealAddressByIP(String ip)
    {
        String address = "XX XX";
        // 内网不查询
        if (IpUtils.internalIp(ip))
        {
            return "内网IP";
        }
//        String rspStr = HttpUtils.sendPost(IP_URL, "ip=" + ip);
        String rspStr = "";
        // 获取高德定位信息
        Map<String,Object> map = new HashMap<>(1);
        map.put("ip",ip);
//        map.put("key","bc1d930157bc84407375987cc0eb9089");
//        rspStr = HttpUtil.get("https://restapi.amap.com/v3/ip", map);
        //获取腾讯定位信息
        map.put("key","QEFBZ-ULBKU-IRVV3-BZYJF-H2UUZ-CFBRW");
        rspStr = HttpUtil.get("https://apis.map.qq.com/ws/location/v1/ip", map);
        if (StringUtils.isEmpty(rspStr))
        {
            log.error("获取地理位置异常 {}", ip);
            return address;
        }
        JSONObject obj;
        try
        {
            obj = JSON.unmarshal(rspStr, JSONObject.class);
            JSONObject adInfo = obj.getObj("result").getObj("ad_info");
            address = adInfo.getStr("province")+adInfo.getStr("city");
            if("[ ][ ]".equals(address)){
                address = "未知地点";
            }
        }
        catch (Exception e)
        {
            log.error("获取地理位置异常 {}", ip);
        }
        return address;
    }

    public static void main(String[] args) {
        System.out.println(getRealAddressByIP("223.104.159.226"));
    }
}
