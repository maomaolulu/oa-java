package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.asset.BizClaimApply;
import com.ruoyi.activiti.vo.SkuCheckVo;
import com.ruoyi.common.core.domain.R;

import java.util.List;

/**
 * @author zx
 * @date 2021/11/30 18:55
 */
public interface BizClaimApplyService {
    /**
     * 新增领用申请
     * @param claimApply
     * @return
     */
    R insert(BizClaimApply claimApply);

    /**
     * 查询详情
     * @param tableId
     * @return
     */
    BizClaimApply selectBizClaimApplyById(String tableId);

    /**
     * 批量删除
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteClaimByIds(String ids);

    /**
     * 检查库存
     * @param skuCheckVos
     * @return
     */
    String checkSku(List<SkuCheckVo> skuCheckVos);
}
