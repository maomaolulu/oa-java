package com.ruoyi.quote.service;

import java.util.List;
import com.ruoyi.quote.domain.entity.QuoteSubCategory;

/**
 * 报价分类子类Service接口
 * 
 * @author yrb
 * @date 2022-04-26
 */
public interface IQuoteSubCategoryService 
{
    /**
     * 查询报价分类子类
     * 
     * @param id 报价分类子类主键
     * @return 报价分类子类
     */
    public QuoteSubCategory selectQuoteSubCategoryById(Long id);

    /**
     * 查询报价分类子类列表
     * 
     * @param quoteSubCategory 报价分类子类
     * @return 报价分类子类集合
     */
    List<QuoteSubCategory> selectQuoteSubCategoryList(QuoteSubCategory quoteSubCategory);

    /**
     * 新增报价分类子类
     * 
     * @param quoteSubCategory 报价分类子类
     * @return 结果
     */
    public int insertQuoteSubCategory(QuoteSubCategory quoteSubCategory);

    /**
     * 修改报价分类子类
     * 
     * @param quoteSubCategory 报价分类子类
     * @return 结果
     */
    public int updateQuoteSubCategory(QuoteSubCategory quoteSubCategory);

    /**
     * 批量删除报价分类子类
     * 
     * @param ids 需要删除的报价分类子类主键集合
     * @return 结果
     */
    public int deleteQuoteSubCategoryByIds(Long[] ids);

    /**
     * 删除报价分类子类信息
     * 
     * @param id 报价分类子类主键
     * @return 结果
     */
    public int deleteQuoteSubCategoryById(Long id);
}
