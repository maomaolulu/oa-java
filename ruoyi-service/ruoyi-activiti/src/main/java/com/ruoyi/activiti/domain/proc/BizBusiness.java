package com.ruoyi.activiti.domain.proc;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 流程业务总表
 * @author zx
 * @date 2022-07-01 14:51:50
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("biz_business")
public class BizBusiness implements Serializable
{
    private static final long serialVersionUID = -7562556845627977390L;

    @Id
    @KeySql(useGeneratedKeys = true)
    @TableId(type = IdType.AUTO)
    private Long              id;

    private String            title;

    private Long              userId;

    private String            tableId;

    private String            procDefId;
    @ApiModelProperty(value = "流程定义key",required = true)
    private String            procDefKey;

    private String            procInstId;



    /** 流程名称 */
    private String            procName;

    /** 当前任务节点名称 */
    private String            currentTask;

    private String            applyer;

    private Integer           status;

    private Integer           result;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date              applyTime;

    private Boolean           delFlag;
    @Transient
    @TableField(exist = false)
    private String searchValue;
    /**
     * 详情
     */
    @Transient
    @TableField(exist = false)
    private List<Map<String,Object>> detailMap;
    /**
     * 款项类别
     */
    @Transient
    @TableField(exist = false)
    private String type;
    /**
     * 当前审批人
     */
    private String auditors;

    /**
     * 审批意见
     */
    @Transient
    @TableField(exist = false)
    private String comment;
    /**
     * 组合公司和部门
     */
    @Transient
    @TableField(exist = false)
    private String companyAndDept;

    @Transient
    @TableField(exist = false)
    private Date       startTime;
    @Transient
    @TableField(exist = false)
    private Date     endTime;
    /**
     * 耗时开始
     */
    @Transient
    @TableField(exist = false)
    private Long duration;
    /**
     * 耗时结束
     */
    @Transient
    @TableField(exist = false)
    private Long durationStart;
    /**
     * 单选日期搜索
     */
    @Transient
    @TableField(exist = false)
    private String singleDate;
    /**
     * 总价格
     */
    @Transient
    @TableField(exist = false)
    private BigDecimal totalPrice;

    /**
     * 总价格
     */
    @Transient
    @TableField(exist = false)
    private String projectMoney;
    /**
     * 报销需要展示的内容
     */
    @Transient
    @TableField(exist = false)
    private Map<String,Object> paymentMap;
    /**
     * 付款需要展示的内容
     */
    @Transient
    @TableField(exist = false)
    private Map<String,Object> reimburseMap;
    /**
     * 付款申请总金额
     */
    @Transient
    @TableField(exist = false)
    private String totalMoney;

    /**
     * 最后审批人
     */
    private String lastAuditor;
    /**
     * 最后审批时间
     */
    private Date lastAuditorTime;
    /**
     * 申请人公司id
     */
    private Long companyId;
    /**
     * 申请人部门id
     */
    private Long deptId;
    /**
     * 申请人公司名称
     */
    private String companyName;
    /**
     * 申请人部门名称
     */
    private String deptName;
    /**
     * 申请编码
     */
    private String applyCode;
    /**
     * 自定义下一个审批人
     */
    @TableField(exist = false)
    @Transient
    private Long customizedUserId;

    /**
     * 当前审批节点
     */
    @Transient
    @TableField(exist = false)
    private String taskDefKey;

    /**
     * 采购物品名称
     */
    @Transient
    @TableField(exist = false)
    private String goodsName;
    /**
     * 物品编号
     */
    @Transient
    @TableField(exist = false)
    private String assetSn;
    /**
     * 我的转交ids
     */
    @Transient
    @TableField(exist = false)
    private Set<Long> ids;

    @Transient
    @TableField(exist = false)
    Boolean hasPurchase;

    /**
     * 合同项目信息修改列表页面（必传字段）
     */
    @Transient
    @TableField(exist = false)
    private String function;

    /**
     * 自动审批
     */
    @Transient
    @TableField(exist = false)
    private String autoAdit;
}
