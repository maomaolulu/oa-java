package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.purchase.BizAnticipateGoods;

import java.util.List;

/**
 * @author zh
 * @date 2021-02-10
 * @desc 采购预提交
 */
public interface BizAnticipateGoodsService {
    /**
     * 查询本人预提交列表
     * @param bizAnticipateGoods
     * @return
     */
    List<BizAnticipateGoods> listAll(BizAnticipateGoods bizAnticipateGoods);
    /**
     * 修改
     * */
    BizAnticipateGoods updateById(BizAnticipateGoods bizAnticipateGoods);
    /**
     * 新增
     * */
    BizAnticipateGoods saveBizAnticipateGoods(List<BizAnticipateGoods> bizAnticipateGoods);
    /**
     * 删除
     * */
    String deleteBizAnticipateGoods( Long[] ids );

}
