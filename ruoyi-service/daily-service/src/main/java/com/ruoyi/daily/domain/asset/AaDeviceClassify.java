package com.ruoyi.daily.domain.asset;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 仪器设备分类信息
 * @author zx
 * @date 2022-11-16 13:26:46
 */
@Data
@TableName(value = "aa_device_classify")
public class AaDeviceClassify {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 固定资产id
     */
    private Long assetId;
    /**
     * 大分类key 1采样2实验室
     */
    private String deviceType;
    /**
     * 小分类key
     */
    private String dictKey;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新人
     */
    private String updateBy;
    /**
     * 更新时间
     */
    private Date updateTime;


}
