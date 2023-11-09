package com.ruoyi.quote.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.quote.domain.entity.QuoteSubCategory;
import org.springframework.stereotype.Repository;

/**
 * 报价分类子类Mapper接口
 * 
 * @author yrb
 * @date 2022-04-26
 */
@Repository
public interface QuoteSubCategoryMapper extends BaseMapper<QuoteSubCategory>
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
     * 删除报价分类子类
     * 
     * @param id 报价分类子类主键
     * @return 结果
     */
    public int deleteQuoteSubCategoryById(Long id);

    /**
     * 批量删除报价分类子类
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteQuoteSubCategoryByIds(Long[] ids);
}
