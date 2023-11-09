package com.ruoyi.activiti.domain.car;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.activiti.domain.SysAttachment;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author : zh
 * @date : 2022-02-24
 * @desc : 还车补贴申请
 */
@Data
@Table(name="biz_car_subsidy_apply")
public class BizCarSubsidyApply implements Serializable{
    /** id */
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 审批编号 */
    private String subsidyCode ;
    /** 标题 */
    private String title ;
    /** 抄送人 */
    private String cc ;
    /** 申请部门 */
    private Long deptId ;
    /** 申请公司 */
    private Long companyId ;
    /** 所属部门(前端传 搜索) */
    private Long belongDeptId ;
    /** 所属公司(前端传 搜索) */
    private Long belongCompanyId ;
    /** 关联审批单号 */
    private String relationCode ;
    /** 车辆属性(0:公车1：私车2：租车) */
    private Integer carTypes ;
    /** 牌照号 */
    private String plateNumber ;
    /** 实际用车日期 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date useCarDate ;
    /** 实际还车日期 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date stillCarDate ;
    /** 出车前里程（公里） */
    private BigDecimal goMileage ;
    /** 还车时里程（公里） */
    private BigDecimal backMileage ;
    /** 出车前、出车后里程照片 */
    @TableField(exist = false)
    private  List<SysAttachment> mileageImgs ;
    /** 出车前、出车后车内外卫生照片 */
    @TableField(exist = false)
    private  List<SysAttachment> hygieneImgs ;
    /** 导航路线截图 */
    @TableField(exist = false)
    private  List<SysAttachment> navigationImgs ;
    /** 行驶里程数 */
    private BigDecimal travelMileage ;
    /** 申请补贴里程数 */
    private BigDecimal applyMileage ;
    /** 加油里程（1） */
    private BigDecimal fuelMileage ;
    /** 加油金额（1） */
    private BigDecimal fuelMoney ;
    /** 本次出行加油金额 */
    private BigDecimal money ;
    /** 本次出行加油金额大写 */
    private String capitalizeMoney ;
    /** 车辆情况备注 */
    private String remark ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private Long createBy ;
    /** 创建时间（申请时间） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;

    /** 申请部门名称 */
    @TableField(exist = false)
    private String deptName ;
    /** 申请公司名称 */
    @TableField(exist = false)
    private String companyName ;
    /** 流程id */
    @TableField(exist = false)
    private Long buId ;
    /** 流程实例编号 */
    @TableField(exist = false)
    private Long procInstId ;

    /** 所属部门（前端传，展示）名称 */
    @TableField(exist = false)
    private String deptUseName ;
    /** 所属公司 （前端传，展示）名称*/
    @TableField(exist = false)
    private String companyUseName ;

    /** 申请人名称 */
    @TableField(exist = false)
    private String createByName ;

    /**申请时间查询*/
    @TableField(exist = false)
    private String createTime1;

    /**申请时间查询*/
    @TableField(exist = false)
    private String createTime2;

    /**抄送人名称*/
    @TableField(exist = false)
    private String ccName;
    /**
     * 关联多个审批单
     */
    @TableField(exist = false)
    private List<Map<String,Object>> associateApply;
    /**审批状态*/
    @TableField(exist = false)
    private Integer result;
    /**审批结果*/
    @TableField(exist = false)
    private Integer status;
    /**审批结束时间*/
    @TableField(exist = false)
    private String endTime;
    /**属性名称*/
    @TableField(exist = false)
    private String carTypesName;

    /**详情去除权限*/
    @TableField(exist = false)
    private Integer oneStatus;
    /**
     * 加油明细
     */
    @TableField(exist = false)
    private List<BizCarOil> oils;
    /**
     * 最近里程数
     */
    private BigDecimal latestMileage;
    /**
     * 目的地
     */
    private String destination;

    /**
     * 车辆情况 1完好2其他
     */
    private Integer vehicleCondition;
    /**
     * 项目数
     */
    private Integer projectNum;
    /**
     * 备注
     */
    private String remark2;

    /**
     * 实际用车日期开始
     */
    @TableField(exist = false)
    private String stillCarDate1;
    /**
     * 实际用车日期结束
     */
    @TableField(exist = false)
    private String stillCarDate2;

}