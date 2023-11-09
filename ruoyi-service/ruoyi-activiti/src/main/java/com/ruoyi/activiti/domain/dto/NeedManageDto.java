package com.ruoyi.activiti.domain.dto;

import com.ruoyi.activiti.domain.proc.BizBusinessPlus;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 需求管理
 * @author zx
 * @date 2022/3/26 10:20
 */
@Data
public class NeedManageDto extends BizBusinessPlus implements Serializable {
    /** 实施原因 */
    private String implementationReasons ;
    /** 实施后预计效果 */
    private String expectedEffect ;
    /** 实施内容 */
    private String content ;
    /**
     * 公司名称
     */
    private String companyName;
    private String deptName;
    private String startDate;
    private String endDate;
    /**
     * 审批编号
     */
    private String applyCode;
    /**
     * 所属系统
     */
    private String affiliatedSystem;
    private List<String> affiliatedSystemList;
}
