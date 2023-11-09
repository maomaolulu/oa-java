package com.ruoyi.daily.domain.asset.dto;

import lombok.Data;

/**
 * 实验室仪器参数
 *
 * @author zx
 * @date 2022-11-11 13:41:11
 */
@Data
public class LabDeviceDto {
    /**
     * 仪器名称
     */
    private String name;
    /**
     * 仪器型号
     */
    private String model;
    /**
     * 仪器编号
     */
    private String assetSn;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 部门编号
     */
    private Long deptId;
    /**
     * 数据归属公司
     */
    private String dataBelong;


}
