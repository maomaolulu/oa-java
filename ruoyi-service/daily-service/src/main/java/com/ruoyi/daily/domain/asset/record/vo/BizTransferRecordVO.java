package com.ruoyi.daily.domain.asset.record.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.models.auth.In;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created by WuYang on 2022/8/23 11:48
 */
@Data
public class BizTransferRecordVO {
    /**
     * id
     */
    private Long id;
    /**
     * 品类名称
     */
    private String categoryName;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 物品编号
     */
    private String assetSn;
    /**
     * 物品名称
     */
    private String name ;
    /**
     * 规格型号
     */
    private String model;
    /**
     * 原责任部门
     */
    private String oldDept;
    /**
     * 原保管人
     */
    private String oldKeeper;
    /**
     * 原状态
     */
    private Integer originalState;
    /**
     * 当前状态
     */
    private Integer currentState;
    /**
     * 当前部门
     */
    private String deptName;
    /**
     * 当前保管
     */
    private String keeper;
    /**
     * 记录类型 0 采购移交 1 资产移交
     */
    private Integer type;
    /**
     * 操作人
     */
    private String handler;
    /**
     * 时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    /**
     * 原物品状态 0 库存 1 在用
     */
    private Integer isChecked;
}
