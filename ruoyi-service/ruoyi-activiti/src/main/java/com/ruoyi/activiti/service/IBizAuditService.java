package com.ruoyi.activiti.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.activiti.domain.proc.BizAudit;
import com.ruoyi.activiti.domain.dto.ReassignmentDto;
import com.ruoyi.activiti.vo.HiTaskVo;

import javax.mail.MessagingException;

/**
 * <p>File：IBizAuditService.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2020 2020年1月6日 下午3:38:40</p>
 * <p>Company: zmrit.com </p>
 * @author zmr
 * @version 1.0
 */
public interface IBizAuditService
{
    /**
     * 查询审核记录
     * 
     * @param id 审核记录ID
     * @return 审核记录
     */
    public BizAudit selectBizAuditById(String id);

    /**
     * 查询审核记录列表
     * 
     * @param bizAudit 审核记录
     * @return 审核记录集合
     */
    public List<BizAudit> selectBizAuditList(BizAudit bizAudit);

    /**
     * 新增审核记录
     * 
     * @param bizAudit 审核记录
     * @return 结果
     */
    public int insertBizAudit(BizAudit bizAudit);

    /**
     * 修改审核记录
     * 
     * @param bizAudit 审核记录
     * @return 结果
     */
    public int updateBizAudit(BizAudit bizAudit);

    /**
     * 批量删除审核记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizAuditByIds(String ids);

    /**
     * 批量删除审核记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizAuditLogic(String ids);

    /**
     * 删除审核记录信息
     * 
     * @param id 审核记录ID
     * @return 结果
     */
    public int deleteBizAuditById(Long id);

    /**
     * history task 历史任务记录
     * 
     * @param hiTaskVo
     * @return
     * @author zmr
     */
    List<HiTaskVo> getHistoryTaskList(HiTaskVo hiTaskVo);
    /**
     * history task 历史任务记录
     *
     * @param hiTaskVo
     * @return
     * @author zmr
     */
    List<HiTaskVo> getHistoryTaskListForRelate(HiTaskVo hiTaskVo);

    /**
     * 抄送我的
     * @param hiTaskVo
     * @return
     */
    List<HiTaskVo> getHistoryTaskListCc(HiTaskVo hiTaskVo);

    /**
     * 抄送我的
     * @param hiTaskVo
     * @return
     */
    List<HiTaskVo> getHistoryTaskListCcForRelate(HiTaskVo hiTaskVo);

    /**
     * history task 历史任务记录Map 用于导出pdf
     *
     * @param hiTaskVo
     * @return
     * @author zmr
     */
    List<Map<String,Object>> getHistoryTaskMap(HiTaskVo hiTaskVo);

    /**
     * 转交
     * @param reassignmentDto
     */
    void reassignment(ReassignmentDto reassignmentDto) throws MessagingException;
}
