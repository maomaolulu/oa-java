package com.ruoyi.activiti.mapper;

import com.ruoyi.activiti.domain.proc.BizAudit;
import com.ruoyi.activiti.vo.CarSubsidyApplyVO;
import com.ruoyi.activiti.vo.HiTaskVo;
import com.ruoyi.common.core.dao.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>File：BizAuditMapper.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2020 2020年1月6日 下午3:38:12</p>
 * <p>Company: zmrit.com </p>
 * @author zmr
 * @version 1.0
 */
@Repository
public interface BizAuditMapper extends BaseMapper<BizAudit>
{
    List<HiTaskVo> getHistoryTaskList(HiTaskVo hiTaskVo);
    List<HiTaskVo> getHistoryTaskList2(HiTaskVo hiTaskVo);
    List<HiTaskVo> getHistoryTaskListCc(HiTaskVo hiTaskVo);
    List<HiTaskVo> getHistoryTaskListCcASC(HiTaskVo hiTaskVo);
    @Select("SELECT " +
            "  PROC_INST_ID_ AS id, " +
            "  END_TIME_ AS time  " +
            "FROM " +
            "  ACT_HI_TASKINST  " +
            "WHERE " +
            "  PROC_DEF_ID_ LIKE 'carSubsidyApply%' AND END_TIME_ is not null ")
    List<CarSubsidyApplyVO> getList();

    /**
     * logic删除
     * @param ids
     * @return
     * @author zmr
     */
    int deleteLogic(String[] ids);

    /**
     * 获取抄送我的流程实例id
     * @param ccId
     * @return
     */
    @Select("select a.proc_inst_id as procInstId from biz_act_cc a where a.del_flag = '0' and a.cc_id = #{ccId} ")
    List<String> getCcProcInst(@Param("ccId") String ccId);
}
