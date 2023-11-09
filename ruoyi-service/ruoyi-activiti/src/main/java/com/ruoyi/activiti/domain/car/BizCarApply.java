package com.ruoyi.activiti.domain.car;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author : zh
 * @date : 2022-02-22
 * @desc : 用车申请
 */
@Data
@TableName(value="biz_car_apply")
public class BizCarApply implements Serializable{

    /** id */
    @TableId(type = IdType.AUTO)
    private Long id ;

    /** 创建时间（申请时间） */
    @Excel(name = "申请日期",dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime ;

    /** 审批编号 */
    @Excel(name = "审批编号")
    private String carCode ;

    /** 申请公司名称 */
    @TableField(exist = false)
    @Excel(name = "所属公司")
    private String companyName ;

    /** 所属公司 （前端传）名称*/
    @TableField(exist = false)
    private String companyUseName ;

    /** 申请人名称 */
    @TableField(exist = false)
    @Excel(name = "申请人")
    private String createByName ;

    /** 审批结果 */
    @TableField(exist = false)
    private Integer result;

    /** 审批结果 */
    @TableField(exist = false)
    @Excel(name = "审批结果")
    private String resultCn;

    /** 车辆属性 */
    @TableField(exist = false)
    @Excel(name = "车辆属性")
    private String carType;

    /** 车牌号 */
    @Excel(name = "车牌号码")
    private String plateNumber ;

    /** 出差地;具体到区） */
    @Excel(name = "出差地")
    private String prefecture ;

    /** 标题 */
    private String title ;
    /** 抄送人 */
    private String cc ;
    /** 申请部门 */
    private Long deptId ;
    /** 申请公司 */
    private Long companyId ;
    /** 所属部门（前端传） */
    private Long deptUseId ;
    /** 所属公司 （前端传）*/
    private Long companyUseId ;
    /** 司机预约(0：不需要，1：需要) */
    @Excel(name = "司机预约")
    private String isDriver ;
    /** 预计出行日期 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "预计出行日期",dateFormat = "yyyy-MM-dd")
    private Date goToDate ;
    /** 预计回程日期 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "预计回程日期",dateFormat = "yyyy-MM-dd")
    private Date goBackDate ;

    /** 预计用车时长（天） */
    @Excel(name = "预计用车时长（天）")
    private Integer days ;

    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private Long createBy ;

    /** 更新人 */
    private Long updateBy ;
    /** 更新时间 */
    private Date updateTime ;

    /** 临时用车说明 */
    @TableField(exist = false)
    private String temporaryUseExplain ;

    /** 申请部门名称 */
    @TableField(exist = false)
    private String deptName ;

    /** 用车详情 */
    @TableField(exist = false)
    private List<BizCarInfo> bizCarInfos ;
    /** 流程id */
    @TableField(exist = false)
    private Long buId ;
    /** 流程实例编号 */
    @TableField(exist = false)
    private Long procInstId ;

    /** 所属部门（前端传）名称 */
    @TableField(exist = false)
    @Excel(name = "实际用车部门")
    private String deptUseName ;

    /**申请时间查询*/
    @TableField(exist = false)
    private String createTime1;

    /**申请时间查询*/
    @TableField(exist = false)
    private String createTime2;

    /**抄送人名称*/
    @TableField(exist = false)
    private String ccName;

    /**详情去除权限*/
    @TableField(exist = false)
    private Integer oneStatus;

    /** 用车事由 */
    @Excel(name = "用车事由")
    private String carReason ;
    /** 用车人 */
    @Excel(name = "用车人")
    private String carUser ;
    /** 同行人 */
    @Excel(name = "同行人")
    private String peer ;

    /** 车辆属性 */
    private String carTypes ;

    /** 最新里程数 */
    @Excel(name = "最新里程数")
    private BigDecimal latestMileage ;

    /** 延迟次数 */
    @TableField(exist = false)
    private Integer time ;
    /** 延迟次数 */
    @TableField(exist = false)
    private Date delayTime;
    /** 延迟id */
    @TableField(exist = false)
    private long delayId;

    /** 备注 */
    @Excel(name = "备注")
    private String remark ;

    /** 导出标识 */
    private String export;
}