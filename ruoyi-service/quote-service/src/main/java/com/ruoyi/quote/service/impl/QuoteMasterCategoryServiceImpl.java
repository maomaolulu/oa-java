package com.ruoyi.quote.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.mapper.QuoteMasterCategoryMapper;
import com.ruoyi.quote.domain.entity.QuoteMasterCategory;
import com.ruoyi.quote.service.IQuoteMasterCategoryService;

/**
 * 报价主分类Service业务层处理
 *
 * @author yrb
 * @date 2022-04-25
 */
@Service
public class QuoteMasterCategoryServiceImpl implements IQuoteMasterCategoryService {
    private final QuoteMasterCategoryMapper quoteMasterCategoryMapper;

    @Autowired
    public QuoteMasterCategoryServiceImpl(QuoteMasterCategoryMapper quoteMasterCategoryMapper) {
        this.quoteMasterCategoryMapper = quoteMasterCategoryMapper;
    }

    /**
     * 查询报价主分类
     *
     * @param id 报价主分类主键
     * @return 报价主分类
     */
    @Override
    public QuoteMasterCategory selectQuoteMasterCategoryById(Long id) {
        return quoteMasterCategoryMapper.selectQuoteMasterCategoryById(id);
    }

    /**
     * 查询报价主分类列表
     *
     * @param quoteMasterCategory 报价主分类
     * @return 报价主分类
     */
    @Override
    public List<QuoteMasterCategory> selectQuoteMasterCategoryList(QuoteMasterCategory quoteMasterCategory) {
        return quoteMasterCategoryMapper.selectQuoteMasterCategoryList(quoteMasterCategory);
    }

    /**
     * 新增报价主分类
     *
     * @param quoteMasterCategory 报价主分类
     * @return 结果
     */
    @Override
    public int insertQuoteMasterCategory(QuoteMasterCategory quoteMasterCategory) {
        quoteMasterCategory.setCreateTime(DateUtils.getNowDate());
        return quoteMasterCategoryMapper.insertQuoteMasterCategory(quoteMasterCategory);
    }

    /**
     * 修改报价主分类
     *
     * @param quoteMasterCategory 报价主分类
     * @return 结果
     */
    @Override
    public int updateQuoteMasterCategory(QuoteMasterCategory quoteMasterCategory) {
        quoteMasterCategory.setUpdateTime(DateUtils.getNowDate());
        return quoteMasterCategoryMapper.updateQuoteMasterCategory(quoteMasterCategory);
    }

    /**
     * 批量删除报价主分类
     *
     * @param ids 需要删除的报价主分类主键
     * @return 结果
     */
    @Override
    public int deleteQuoteMasterCategoryByIds(Long[] ids) {
        return quoteMasterCategoryMapper.deleteQuoteMasterCategoryByIds(ids);
    }

    /**
     * 删除报价主分类信息
     *
     * @param id 报价主分类主键
     * @return 结果
     */
    @Override
    public int deleteQuoteMasterCategoryById(Long id) {
        return quoteMasterCategoryMapper.deleteQuoteMasterCategoryById(id);
    }
}
