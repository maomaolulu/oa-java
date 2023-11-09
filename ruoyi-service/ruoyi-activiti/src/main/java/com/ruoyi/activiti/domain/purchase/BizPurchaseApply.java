package com.ruoyi.activiti.domain.purchase;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.activiti.domain.SysAttachment;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 采购申请(新)对象 biz_purchase_apply
 *
 * @author zx
 * @date 2021-11-16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("biz_purchase_apply")
public class BizPurchaseApply extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type= IdType.AUTO)
    private Long id;
    /**
     * 部门id(数据权限)
     */
    private Long deptId;

    /**
     * 申请标题
     */
    private String title;

    /**
     * 采购申请编号
     */
    private String purchaseCode;


    /**
     * 采购类型
     */
    @TableField(exist = false)
    private String itemTypeName;

    /**
     * 期望交付时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date expectDate;

    /**
     * 验收人
     */
    @Excel(name = "验收人")
    private String acceptor;

    /**
     * 是否验收
     */
    @Excel(name = "是否验收")
    private String isAcceptance;

    /**
     * 采购人
     */
    @Excel(name = "采购人")
    private String purchaser;

    /**
     * 是否已采购
     */
    @Excel(name = "是否已采购")
    private String isPurchase;

    /**
     * 抄送人
     */
    @Excel(name = "抄送人")
    private String cc;

    /**
     * 抄送人名称
     */
    @Excel(name = "抄送人名称")
    @TableField(exist = false)
    private String ccName;

    /**
     * 逻辑删
     */
    private String delFlag;

    /**
     * 流程 用于导出
     */
    @TableField(exist = false)
    private List<Map<String, Object>> hiTaskVos;

    /**
     * 期望交付时间 导出
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private String pdfExpectDate;

    /**
     * 申请时间 用于导出
     */
    @TableField(exist = false)
    private String pdfCreateTime;
    /**
     * 部门全称
     */
    @TableField(exist = false)
    private String deptFullName;
    /**
     * 费用归属部门
     */
    @TableField(exist = false)
    private String ascriptionDept;
    /**
     * 费用归属部门
     */
    @TableField(exist = false)
    private Long ascriptionDeptId;
    /**
     * 物品使用部门
     */
    @TableField(exist = false)
    private String usages;
    /**
     * 公司全称
     */
    @TableField(exist = false)
    private String companyName;

    /**
     * 打印人
     */
    @TableField(exist = false)
    private String pdfName;

    /**
     * 物品类型
     */
    @TableField(exist = false)
    private String itemType;

    /**
     * 物品数组
     */
    @TableField(exist = false)
    private List<BizGoodsInfo> bizGoodsInfos;

    /**
     * 报价单
     */
    @TableField(exist = false)
    private List<SysAttachment> quoteList;
    /**
     * 采购物品附件
     */
    @TableField(exist = false)
    private List<SysAttachment> purchaseList;


}
