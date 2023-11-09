package com.ruoyi.quote.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.quote.domain.entity.QuoteChargeCategory;
import com.ruoyi.quote.domain.vo.QuoteChargeCategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.mapper.QuoteChargeCategoryMapper;
import com.ruoyi.quote.service.IQuoteChargeCategoryService;

/**
 * 收费标准详细分类Service业务层处理
 *
 * @author yrb
 * @date 2022-05-17
 */
@Service
public class QuoteChargeCategoryServiceImpl implements IQuoteChargeCategoryService {
    private final QuoteChargeCategoryMapper quoteChargeCategoryMapper;

    @Autowired
    public QuoteChargeCategoryServiceImpl(QuoteChargeCategoryMapper quoteChargeCategoryMapper) {
        this.quoteChargeCategoryMapper = quoteChargeCategoryMapper;
    }

    /**
     * 查询收费标准信息
     *
     * @param categoryName 分类名称
     * @return 结果
     */
    @Override
    public QuoteChargeCategory findQuoteChargeCategoryByCategoryName(String categoryName) {
        return quoteChargeCategoryMapper.selectQuoteChargeCategoryByCategoryName(categoryName);
    }

    /**
     * 查询收费标准详细分类
     *
     * @param categoryId 收费标准详细分类主键
     * @return 收费标准详细分类
     */
    @Override
    public QuoteChargeCategory selectQuoteChargeCategoryByCategoryId(Long categoryId) {
        return quoteChargeCategoryMapper.selectQuoteChargeCategoryByCategoryId(categoryId);
    }

    /**
     * 查询收费标准详细分类列表
     *
     * @param quoteChargeCategory 收费标准详细分类
     * @return 收费标准详细分类
     */
    @Override
    public List<QuoteChargeCategory> selectQuoteChargeCategoryList(QuoteChargeCategory quoteChargeCategory) {
        return quoteChargeCategoryMapper.selectQuoteChargeCategoryList(quoteChargeCategory);
    }

    /**
     * 新增收费标准详细分类
     *
     * @param quoteChargeCategory 收费标准详细分类
     * @return 结果
     */
    @Override
    public int insertQuoteChargeCategory(QuoteChargeCategory quoteChargeCategory) {
        quoteChargeCategory.setCreateTime(DateUtils.getNowDate());
        return quoteChargeCategoryMapper.insertQuoteChargeCategory(quoteChargeCategory);
    }

    /**
     * 修改收费标准详细分类
     *
     * @param quoteChargeCategory 收费标准详细分类
     * @return 结果
     */
    @Override
    public int updateQuoteChargeCategory(QuoteChargeCategory quoteChargeCategory) {
        quoteChargeCategory.setUpdateTime(DateUtils.getNowDate());
        return quoteChargeCategoryMapper.updateQuoteChargeCategory(quoteChargeCategory);
    }

    /**
     * 批量删除收费标准详细分类
     *
     * @param categoryIds 需要删除的收费标准详细分类主键
     * @return 结果
     */
    @Override
    public int deleteQuoteChargeCategoryByCategoryIds(Long[] categoryIds) {
        return quoteChargeCategoryMapper.deleteQuoteChargeCategoryByCategoryIds(categoryIds);
    }

    /**
     * 删除收费标准详细分类信息
     *
     * @param categoryId 收费标准详细分类主键
     * @return 结果
     */
    @Override
    public int deleteQuoteChargeCategoryByCategoryId(Long categoryId) {
        return quoteChargeCategoryMapper.deleteQuoteChargeCategoryByCategoryId(categoryId);
    }

    /**
     * 收费分类树
     *
     * @param quoteChargeCategory 收费标准详细分类
     * @return 结果
     */
    @Override
    public List<QuoteChargeCategoryVO> quoteChargeCategoryTree(QuoteChargeCategory quoteChargeCategory) {
        List<QuoteChargeCategoryVO> quoteChargeCategoryList = quoteChargeCategoryMapper.selectQuoteChargeCategoryTreeList(quoteChargeCategory);
        Map<Long, QuoteChargeCategory> quoteChargeCategoryMap = quoteChargeCategoryList.stream().collect(Collectors.toMap(QuoteChargeCategoryVO::getCategoryId, QuoteChargeCategoryVO -> QuoteChargeCategoryVO));
        List<QuoteChargeCategoryVO> chargeCategoryList = new ArrayList<>();
        quoteChargeCategoryList.forEach(chargeCategoryVO -> {
            if (chargeCategoryVO.getParentId() == 0) {
                chargeCategoryList.add(chargeCategoryVO);
            } else {
                // 获取父类
                QuoteChargeCategoryVO category = (QuoteChargeCategoryVO) quoteChargeCategoryMap.get(chargeCategoryVO.getParentId());
                // 添加子类
                category.getChildrenList().add(chargeCategoryVO);
            }
        });
        return chargeCategoryList;
    }
}
