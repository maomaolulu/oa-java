package com.ruoyi.common.utils;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class SimpleWriteData implements Serializable {
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 数据列表
     */
    private List<?> dataList;
}
