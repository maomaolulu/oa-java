package com.ruoyi.activiti.domain.asset;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * 申领出库
 * @author zx
 * @date 2021/11/30 16:37
 */
@Data
@Table(name = "biz_claim_apply")
public class BizClaimApply extends BaseEntity implements Serializable {
    @Id
    private  Long id ;
    /** 申领单号 */
    private String claimCode ;
    /** 物品用途 */
    private String reason ;
    /** 领用人 */
    private String  applyer ;
    /** 部门id（数据权限） */
    private Integer deptId ;
    @Transient
    private String companyName;
    /** 逻辑删 */
    private String delFlag ;
    /** 标题 */
    private String title;
    /** 抄送人 */
    private String  cc;
    /** 抄送人名称 */
    @Transient
    private String  ccName;

    @Transient
    private List<BizClaimGoods> goods;
}
