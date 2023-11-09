package com.ruoyi.activiti.domain.my_apply;

import com.ruoyi.system.domain.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2023/5/22 10:38
 * @Version 1.0
 * @Description 合同评审
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BizContractReview implements Serializable {
    private final static long serialVersionUID = 1L;
    /**
     * 合同信息
     */
    private BizContractInfo bizContractInfo;
    /**
     * 项目信息（集合）
     */
    private List<BizContractProjectInfo> bizContractProjectInfoList;
    /**
     * 用户信息
     */
    private SysUser user;
}
