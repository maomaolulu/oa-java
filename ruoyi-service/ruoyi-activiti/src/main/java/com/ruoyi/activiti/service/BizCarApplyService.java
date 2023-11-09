package com.ruoyi.activiti.service;


import com.ruoyi.activiti.domain.car.BizCarApply;
import com.ruoyi.activiti.domain.dto.CancelUseCarDTO;
import com.ruoyi.activiti.domain.dto.CheckDelayCarDTO;
import com.ruoyi.common.core.domain.R;

import java.util.List;

/**
 * @author zh
 * @date 2022-02-22
 * @desc 用车申请
 */
public interface BizCarApplyService {
    /**
     * 用车申请数据分页
     * @param bizCarApply
     * @return
     */
    List<BizCarApply> listAllPage(BizCarApply bizCarApply);

    /**
     * 新增用车申请
     * @param bizCarApply
     * @return
     */
    BizCarApply save(BizCarApply bizCarApply);
    /**
     * 修改用车申请
     * @param bizCarApply
     * @return
     */
    BizCarApply update(BizCarApply bizCarApply);
    /**
     * 删除用车申请
     * @param ids
     * @return
     */
    void delete(Long [] ids);

    /**
     * 查询详情
     * @param tableId
     * @return
     */
     BizCarApply selectOne(Long tableId);



    /**
     * 未还车辆数据分页
     * @param bizCarApply
     * @return
     */
    List<BizCarApply> getUnpaidCarList(BizCarApply bizCarApply);

    /**
     * 取消用车，提前还车，延迟还车
     * @return
     */
    R cancelUseCar(CancelUseCarDTO cancelUseCarDTO);


    /**
     * 延迟还车审核页面
     * @param bizCarApply
     * @return
     */
    List<BizCarApply> getCarCheckList(BizCarApply bizCarApply);

    /**
     * 审核
     * @param checkDelayCarDTO
     * @return
     */
    R checkCarDelay(CheckDelayCarDTO checkDelayCarDTO);
}
