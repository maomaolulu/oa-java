package com.ruoyi.quote.service;

import java.util.List;
import com.ruoyi.quote.domain.entity.QuoteChargeCategory;
import com.ruoyi.quote.domain.vo.QuoteChargeCategoryVO;

/**
 * 收费标准详细分类Service接口
 * 
 * @author yrb
 * @date 2022-05-17
 */
public interface IQuoteChargeCategoryService 
{
    /**
     * 查询收费标准信息
     *
     * @param categoryName 分类名称
     * @return 结果
     */
    QuoteChargeCategory findQuoteChargeCategoryByCategoryName(String categoryName);

    /**
     * 查询收费标准详细分类
     * 
     * @param categoryId 收费标准详细分类主键
     * @return 收费标准详细分类
     */
    public QuoteChargeCategory selectQuoteChargeCategoryByCategoryId(Long categoryId);

    /**
     * 查询收费标准详细分类列表
     * 
     * @param quoteChargeCategory 收费标准详细分类
     * @return 收费标准详细分类集合
     */
    public List<QuoteChargeCategory> selectQuoteChargeCategoryList(QuoteChargeCategory quoteChargeCategory);

    /**
     * 新增收费标准详细分类
     * 
     * @param quoteChargeCategory 收费标准详细分类
     * @return 结果
     */
    public int insertQuoteChargeCategory(QuoteChargeCategory quoteChargeCategory);

    /**
     * 修改收费标准详细分类
     * 
     * @param quoteChargeCategory 收费标准详细分类
     * @return 结果
     */
    public int updateQuoteChargeCategory(QuoteChargeCategory quoteChargeCategory);

    /**
     * 批量删除收费标准详细分类
     * 
     * @param categoryIds 需要删除的收费标准详细分类主键集合
     * @return 结果
     */
    public int deleteQuoteChargeCategoryByCategoryIds(Long[] categoryIds);

    /**
     * 删除收费标准详细分类信息
     * 
     * @param categoryId 收费标准详细分类主键
     * @return 结果
     */
    public int deleteQuoteChargeCategoryByCategoryId(Long categoryId);

    /**
     * 收费分类树
     *
     * @param quoteChargeCategory 收费标准详细分类
     * @return 结果
     */
    List<QuoteChargeCategoryVO>  quoteChargeCategoryTree(QuoteChargeCategory quoteChargeCategory);
}
