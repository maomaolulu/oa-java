package com.ruoyi.activiti.mapper;

import com.ruoyi.activiti.domain.purchase.BizPurchaseApply;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 采购申请(新)Mapper接口
 * 
 * @author zx
 * @date 2021-11-16
 */
@Repository
public interface BizPurchaseApplyMapper 
{
    /**
     * 查询采购申请(新)
     * 
     * @param id 采购申请(新)ID
     * @return 采购申请(新)
     */
    public BizPurchaseApply selectBizPurchaseApplyById(Long id);

    /**
     * 查询采购申请(新)列表
     * 
     * @param bizPurchaseApply 采购申请(新)
     * @return 采购申请(新)集合
     */
    public List<BizPurchaseApply> selectBizPurchaseApplyList(BizPurchaseApply bizPurchaseApply);

    /**
     * 新增采购申请(新)
     * 
     * @param bizPurchaseApply 采购申请(新)
     * @return 结果
     */
    public int insertBizPurchaseApply(BizPurchaseApply bizPurchaseApply);

    /**
     * 修改采购申请(新)
     * 
     * @param bizPurchaseApply 采购申请(新)
     * @return 结果
     */
    public int updateBizPurchaseApply(BizPurchaseApply bizPurchaseApply);

    /**
     * 删除采购申请(新)
     * 
     * @param id 采购申请(新)ID
     * @return 结果
     */
    public int deleteBizPurchaseApplyById(Long id);

    /**
     * 批量删除采购申请(新)
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizPurchaseApplyByIds(String[] ids);
}