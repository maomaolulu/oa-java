package com.ruoyi.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * map工具
 * @author zx
 * @date 2022-09-15 16:52:36
 */
public class MapUtil {
    private MapUtil() {}
    private static ObjectMapper mapper = new ObjectMapper();


    public static Map<String, Object> toMap(Object obj) {
        try {
            String json = mapper.writeValueAsString(obj);
            Map<String, Object> readValue = mapper.readValue(json, HashMap.class);
            return readValue;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new HashMap<>(0);
        }
    }
}
