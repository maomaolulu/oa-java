package com.ruoyi.quote.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.mapper.QuoteSubCategoryMapper;
import com.ruoyi.quote.domain.entity.QuoteSubCategory;
import com.ruoyi.quote.service.IQuoteSubCategoryService;

/**
 * 报价分类子类Service业务层处理
 *
 * @author yrb
 * @date 2022-04-26
 */
@Service
public class QuoteSubCategoryServiceImpl implements IQuoteSubCategoryService {
    private QuoteSubCategoryMapper quoteSubCategoryMapper;

    @Autowired
    public QuoteSubCategoryServiceImpl(QuoteSubCategoryMapper quoteSubCategoryMapper) {
        this.quoteSubCategoryMapper = quoteSubCategoryMapper;
    }

    /**
     * 查询报价分类子类
     *
     * @param id 报价分类子类主键
     * @return 报价分类子类
     */
    @Override
    public QuoteSubCategory selectQuoteSubCategoryById(Long id) {
        return quoteSubCategoryMapper.selectQuoteSubCategoryById(id);
    }

    /**
     * 查询报价分类子类列表
     *
     * @param quoteSubCategory 报价分类子类
     * @return 报价分类子类
     */
    @Override
    public List<QuoteSubCategory> selectQuoteSubCategoryList(QuoteSubCategory quoteSubCategory) {
        return quoteSubCategoryMapper.selectQuoteSubCategoryList(quoteSubCategory);
    }

    /**
     * 新增报价分类子类
     *
     * @param quoteSubCategory 报价分类子类
     * @return 结果
     */
    @Override
    public int insertQuoteSubCategory(QuoteSubCategory quoteSubCategory) {
        quoteSubCategory.setCreateTime(DateUtils.getNowDate());
        return quoteSubCategoryMapper.insertQuoteSubCategory(quoteSubCategory);
    }

    /**
     * 修改报价分类子类
     *
     * @param quoteSubCategory 报价分类子类
     * @return 结果
     */
    @Override
    public int updateQuoteSubCategory(QuoteSubCategory quoteSubCategory) {
        quoteSubCategory.setUpdateTime(DateUtils.getNowDate());
        return quoteSubCategoryMapper.updateQuoteSubCategory(quoteSubCategory);
    }

    /**
     * 批量删除报价分类子类
     *
     * @param ids 需要删除的报价分类子类主键
     * @return 结果
     */
    @Override
    public int deleteQuoteSubCategoryByIds(Long[] ids) {
        return quoteSubCategoryMapper.deleteQuoteSubCategoryByIds(ids);
    }

    /**
     * 删除报价分类子类信息
     *
     * @param id 报价分类子类主键
     * @return 结果
     */
    @Override
    public int deleteQuoteSubCategoryById(Long id) {
        return quoteSubCategoryMapper.deleteQuoteSubCategoryById(id);
    }
}
