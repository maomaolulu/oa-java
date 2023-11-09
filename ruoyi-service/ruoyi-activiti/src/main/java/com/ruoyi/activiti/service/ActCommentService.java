package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.ActComment;

import java.util.List;

/**
 * @author zx
 * @date 2022/2/21 14:57
 */
public interface ActCommentService {
    /**
     * 新增评论
     * @param actComment
     * @return
     */
    ActComment save(ActComment actComment);

    /**
     * 删除评论
     * @param id
     * @return
     */
    ActComment delete(Long id);

    /**
     * 统计
     * @param procInstId
     * @return
     */
    int count(String procInstId);

    /**
     * 获取评论列表
     * @param procInstId
     * @return
     */
    List<ActComment> list(String procInstId);
}
