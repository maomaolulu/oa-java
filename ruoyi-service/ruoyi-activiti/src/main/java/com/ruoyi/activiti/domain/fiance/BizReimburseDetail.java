package com.ruoyi.activiti.domain.fiance;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 报销明细
 * @author zx
 * @date 2021/12/17 11:30
 */
@Data
@TableName("biz_reimburse_detail")
public class BizReimburseDetail implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long  id ;
    /** 报销申请id */
    private Long  parentId ;
    /** 费用发生日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date expenseTime ;
    /** 报销类别 */
    private Integer types ;
    /** 报销类别名称 */
    @TableField(exist = false)
    private String typesName;
    /** 报销金额 */
    private BigDecimal money ;
    /** 费用明细 */
    private String remark ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private String createBy ;
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime ;
    /** 电子发票 */
    private String invoice;
}
