package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.proc.BizAudit;
import com.ruoyi.activiti.vo.RuTask;
import com.ruoyi.common.core.page.PageDomain;

import java.util.Map;

/**
 * 任务管理 服务层
 * @author zx
 * @date 2022/1/25 11:54
 */
public interface ActTaskService {
    /**
     * 获取待办
     * @param ruTask 待办信息
     * @param page 分页信息
     * @return
     */
    Map<String, Object> getIng(RuTask ruTask, PageDomain page);
    Map<String, Object> getIngForRelation(RuTask ruTask, PageDomain page,Boolean hasPurchase);

    /**
     * 获取抄送未读信息数量
     * @param currentUserId 当前用户id
     * @return
     */
    int getUnReadCCCount(Long currentUserId);

    /**
     * 抄送已读
     * @param processInstanceId 流程实例id
     * @param userId 用户id
     */
    void readCc(String processInstanceId, Long userId);

    /**
     * 自动审批
     * @param bizAudit 审批参数
     */
    void autoAudit(BizAudit bizAudit);
}
