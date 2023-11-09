package com.ruoyi.activiti.domain.my_apply;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.activiti.domain.SysAttachment;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author yrb
 * @Date 2023/5/31 15:57
 * @Version 1.0
 * @Description 招标审批
 */
@Data
@TableName("biz_bid_apply")
public class BizBidApply implements Serializable {

    private final static long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 抄送人 */
    private String cc ;

    /** 标题 */
    private String title ;

    /** 审批编号 */
    private String applyCode ;

    /** 部门ID */
    private Long deptId;

    /** 公司id */
    private Long comId ;

    /** 业务员 */
    private String salesman;

    /** 项目名称 */
    private String projectName;

    /** 投标截止日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deadline ;

    /** 标书费用 */
    private BigDecimal bidFee;

    /** 保证金 */
    private BigDecimal deposit;

    /** 是否寄送  1是 0否 */
    private Integer send;

    /** 申请原因 */
    private String reason;

    /** 创建人 */
    private String createBy ;

    /** 创建时间 */
    private Date createTime ;

    /** 部门名臣 */
    @TableField(exist = false)
    private String deptName;

    /** 公司名称 */
    @TableField(exist = false)
    private String comName;

    /** 抄送人（中文） */
    @TableField(exist = false)
    private String ccName;

    /**
     * 招标审批附件
     */
    @TableField(exist = false)
    private List<SysAttachment> attachment;
}
