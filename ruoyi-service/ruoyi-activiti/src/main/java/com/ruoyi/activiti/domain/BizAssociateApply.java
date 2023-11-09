package com.ruoyi.activiti.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 关联审批单
 * @author zx
 * @date 2022/1/6 14:05
 */
@TableName("biz_associate_apply")
@Data
public class BizAssociateApply implements Serializable {
    private final static long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 主审批单类型 */
    private String types ;
    /** 主审批单id */
    private Long applyId ;
    /** 关联审批单标题 */
    private String associateTitle ;
    /** 关联审批单类型procDefKey */
    private String associateTypes ;
    /** 关联审批单businessKey */
    private Long associateApply ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private String createBy ;
    /** 创建时间 */
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;
}
