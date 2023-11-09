package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.my_apply.BizSealApply;
import com.ruoyi.activiti.domain.dto.SealApplyDto;
import com.ruoyi.activiti.domain.vo.SealApplyVo;

import java.util.List;

/**
 * 用印申请
 * @author zx
 * @date 2022/1/12 20:20
 */
public interface BizSealApplyService {
    /**
     * 新增申请
     * @param sealApply
     * @return
     */
    int insert(BizSealApply sealApply);

    /**
     * 获取详情
     * @param id
     * @return
     */
    BizSealApply selectById(Long id);

    /**
     * 查询列表
     * @param sealApplyDto
     * @return
     */
    List<SealApplyVo> selectList(SealApplyDto sealApplyDto);
}
