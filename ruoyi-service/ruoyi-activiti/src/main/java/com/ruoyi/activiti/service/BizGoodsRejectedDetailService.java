package com.ruoyi.activiti.service;


import com.ruoyi.activiti.domain.purchase.BizGoodsRejectedDetail;

import java.util.List;

/**
 * lx
 * 22/01/14
 */
public interface BizGoodsRejectedDetailService {

    /**
     * 通过退货信息ID获取具体信息列表
     * @param goodsRejectedId
     * @return
     */
    List<BizGoodsRejectedDetail> getListByGoodsId(Long goodsRejectedId);

}
