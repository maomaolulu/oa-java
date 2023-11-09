package com.ruoyi.activiti.domain.asset;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @description: 报废出库申请
 * @author: zx
 * @date: 2021/11/21 19:02
 */
@Data
@TableName("biz_scrapped_apply")
public class BizScrappedApply extends BaseEntity implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 部门id(数据权限)
     */
    private Integer deptId;
    @TableField(exist = false)
    private String companyName;
    /**
     * 申请编号
     */
    private String applyCode;
    /**
     * 抄送人
     */
    private String cc;
    /**
     * 资产id
     */
    private Long assertId;
    /**
     * 逻辑删
     */
    private String delFlag;
    /**
     * 固定资产
     */
    @TableField(exist = false)
    private Asset asset;
    /**
     * 申请时间
     */
    @TableField(exist = false)
    private String applyTime;

    /**
     * 申请人
     */
    @TableField(exist = false)
    private String applyer;
    /**
     * 流程 用于导出
     */
    @TableField(exist = false)
    private List<Map<String, Object>> hiTaskVos;
    /**
     * 申请时间 pdf导出
     */
    @TableField(exist = false)
    private String pdfCreateTime;
    /**
     * 到处人 pdf导出
     */
    @TableField(exist = false)
    private String pdfName;
    @TableField(exist = false)
    private String pdfDate;

    /**
     * 抄送人
     */
    @TableField(exist = false)
    private String ccName;

    /**
     * 报废残值价格
     */
    private BigDecimal scrappedPrice;

    /**
     * 实际处理价格
     */
    private BigDecimal realPrice;

    /**
     * 实际处理方式
     */
    private String dealMethod;

    /**
     * 收款方式
     */
    private String rcvMethod;

    /**
     * 流程唯一标识
     */
    @TableField(exist = false)
    private String processKey;
}
