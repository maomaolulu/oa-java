package com.ruoyi.activiti.domain.fiance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @author : zh
 * @date : 2021-12-17
 * @desc : 业务费申请
 */
@Data
@Table(name="biz_business_fee_apply")
public class BizBusinessFeeApply {
    /** id */
    @TableId(type = IdType.AUTO)
    private Integer id ;
    /** 标题 */
    private String title ;
    /** 抄送人 */
    private String cc ;
    /** 用户id */
    private Integer userId ;
    /** 部门id(数据权限) */
    private Integer deptId ;
    /** 所属公司id */
    private Integer companyId ;
    /** 隶属部门id */
    private Long applyDeptId;
    /** 申请编号 */
    private String applyCode ;
    /** 项目编号 */
    private String projectCode ;
    /** 项目名称 */
    private String projectName ;
    /** 项目总金额 */
    private BigDecimal projectPrice ;
    /** 支付对象 */
    private String paymentObject ;
    /** 报价单 */
    private String quotation ;
    /** 申请人 */
    private String applyUser ;
    /** 新老业务(0新业务，1续签业务) */
    private Integer ifOld ;
    /** 逻辑删 */
    private String delFlag ;
    /** 搜索值 */
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date                createTime;

    /** 创建者 */
    private String              createBy;
    /** 更新者 */
    private String              updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /** 备注 */
    private String              remark;


}