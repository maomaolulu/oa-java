package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.activiti.domain.proc.ActRuVariable;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * 流程运行时变量
 * @author zx
 * @date 2021/12/8 16:12
 */
@Repository
public interface ActRuVariableMapper extends BaseMapper<ActRuVariable> {
    /**
     * 更新流程历史变量
     * @param money
     * @param procInstId
     * @return
     */
    @Update("update ACT_HI_VARINST set DOUBLE_ = #{money} where NAME_ = 'money' and PROC_INST_ID_ = #{procInstId} ")
    int updateHiVar(@Param("money")Double money, @Param("procInstId")String procInstId);
}
