package com.ruoyi.quote.service;

import java.util.List;
import com.ruoyi.quote.domain.entity.QuoteMasterCategory;

/**
 * 报价主分类Service接口
 * 
 * @author yrb
 * @date 2022-04-25
 */
public interface IQuoteMasterCategoryService 
{
    /**
     * 查询报价主分类
     * 
     * @param id 报价主分类主键
     * @return 报价主分类
     */
    public QuoteMasterCategory selectQuoteMasterCategoryById(Long id);

    /**
     * 查询报价主分类列表
     * 
     * @param quoteMasterCategory 报价主分类
     * @return 报价主分类集合
     */
    List<QuoteMasterCategory> selectQuoteMasterCategoryList(QuoteMasterCategory quoteMasterCategory);

    /**
     * 新增报价主分类
     * 
     * @param quoteMasterCategory 报价主分类
     * @return 结果
     */
    public int insertQuoteMasterCategory(QuoteMasterCategory quoteMasterCategory);

    /**
     * 修改报价主分类
     * 
     * @param quoteMasterCategory 报价主分类
     * @return 结果
     */
    public int updateQuoteMasterCategory(QuoteMasterCategory quoteMasterCategory);

    /**
     * 批量删除报价主分类
     * 
     * @param ids 需要删除的报价主分类主键集合
     * @return 结果
     */
    public int deleteQuoteMasterCategoryByIds(Long[] ids);

    /**
     * 删除报价主分类信息
     * 
     * @param id 报价主分类主键
     * @return 结果
     */
    public int deleteQuoteMasterCategoryById(Long id);
}
