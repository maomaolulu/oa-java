package com.ruoyi.activiti.domain.asset;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author yrb
 * @Date 2023/8/8 13:43
 * @Version 1.0
 * @Description 仪器设备采购信息
 */
@Data
public class BizEquipPurchaseInfo implements Serializable {
    /**
     * 公司id
     */
    private Long company_id;
    /**
     * 部门id
     */
    private Long dept_id;
    /**
     * 设备编号(物品编号)
     */
    private String asset_sn;
    /**
     * 物品名称（设备名称）
     */
    private String name;
    /**
     * 设备状态
     */
    private Integer status;
    /**
     * 打印标签：0未打印、1已打印
     */
    private Integer is_labelled;
    /**
     * 上传图片：0未上传，1已上传
     */
    private Integer has_pic;
    /**
     * 开始时间
     */
    private Date start_date;
    /**
     * 结束时间
     */
    private Date end_date;
}
