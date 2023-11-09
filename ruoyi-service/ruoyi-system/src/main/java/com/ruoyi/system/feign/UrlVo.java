package com.ruoyi.system.feign;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author zx
 * @date 2021/12/19 9:52
 */
@Data
public class UrlVo {
    private String bucketName;
    private List<Map<String,Object>> fileNameUrl;

}
