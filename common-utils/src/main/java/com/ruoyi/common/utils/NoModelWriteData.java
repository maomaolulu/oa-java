package com.ruoyi.common.utils;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
@Data
public class NoModelWriteData implements Serializable {
    /** 文件名 */
    private String fileName;
    /** 表头数组 */
    private String[] headMap;
    /** 对应数据字段数组 */
    private String[] dataStrMap;
    /** 数据集合 */
    private List<Map<String, Object>> dataList;
    /** 数据集合 */
    private List<T> objList;
}
