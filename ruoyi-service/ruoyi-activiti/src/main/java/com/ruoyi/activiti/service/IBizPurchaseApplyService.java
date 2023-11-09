package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.asset.AaSpu;
import com.ruoyi.activiti.domain.dto.BizSupplierQuoteDto;
import com.ruoyi.activiti.domain.purchase.BizGoodsInfo;
import com.ruoyi.activiti.domain.purchase.BizPurchaseApply;
import com.ruoyi.activiti.domain.dto.GoodsRunVariableDto;
import com.ruoyi.activiti.domain.purchase.BizSupplierQuote;
import com.ruoyi.common.core.domain.R;

import java.util.List;

/**
 * 采购申请(新)Service接口
 * 
 * @author zx
 * @date 2021-11-16
 */
public interface IBizPurchaseApplyService 
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
    public R insertBizPurchaseApply(BizPurchaseApply bizPurchaseApply);

    /**
     * 修改采购申请(新)
     * 
     * @param bizPurchaseApply 采购申请(新)
     * @return 结果
     */
    public int updateBizPurchaseApply(BizPurchaseApply bizPurchaseApply);

    /**
     * 批量删除采购申请(新)
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizPurchaseApplyByIds(String[] ids);

    /**
     * 删除采购申请(新)信息
     * 
     * @param id 采购申请(新)ID
     * @return 结果
     */

    public int deleteBizPurchaseApplyById(Long id);
    /**
     * 查询采购物品信息
     *
     * @param id 采购物品信息ID
     * @return 采购物品信息
     */
    public BizGoodsInfo selectBizGoodsInfoById(Long id);

    /**
     * 查询采购物品信息列表
     *
     * @param bizGoodsInfo 采购物品信息
     * @return 采购物品信息集合
     */
    public List<BizGoodsInfo> selectBizGoodsInfoList(BizGoodsInfo bizGoodsInfo);

    /**
     * 修改采购物品信息
     *
     * @param goodsRunVariableDto 采购物品信息
     * @return 结果
     */
    public int updateBizGoodsInfo(GoodsRunVariableDto goodsRunVariableDto);
    /**
     * 根据名称查询耗材详情
     * @param name
     * @return
     */
    public List<AaSpu> listByName(String name,String deptId );
    /**
     * 根据id查询耗材详情
     * @param id
     * @return
     */
    public AaSpu AaSpuById(Long id );

    /**
     * 保存供应商报价
     * @param supplierQuoteDto 供应商报价信息
     */
    void saveQuote(BizSupplierQuoteDto supplierQuoteDto);

    /**
     * 根据id删除供应商报价
     * @param id 供应商报价id
     */
    void deleteQuote(Long id,String procInstId);

    /**
     * 根据物品id获取供应商报价信息
     * @param id 物品id
     * @return
     */
     List<BizSupplierQuote>getQuote(Long id);
}