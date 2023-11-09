package com.ruoyi.quote.mapper;

import java.util.List;
import com.ruoyi.quote.domain.entity.QuoteChargeCategory;
import com.ruoyi.quote.domain.vo.QuoteChargeCategoryVO;
import org.springframework.stereotype.Repository;

/**
 * 收费标准详细分类Mapper接口
 * 
 * @author yrb
 * @date 2022-05-17
 */
@Repository
public interface QuoteChargeCategoryMapper 
{
    /**
     * 查询收费标准信息
     *
     * @param categoryName 分类名称
     * @return 结果
     */
    QuoteChargeCategory selectQuoteChargeCategoryByCategoryName(String categoryName);

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
     * 删除收费标准详细分类
     * 
     * @param categoryId 收费标准详细分类主键
     * @return 结果
     */
    public int deleteQuoteChargeCategoryByCategoryId(Long categoryId);

    /**
     * 批量删除收费标准详细分类
     * 
     * @param categoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteQuoteChargeCategoryByCategoryIds(Long[] categoryIds);

    /**
     * 查询收费标准详细分类列表
     *
     * @param quoteChargeCategory 收费标准详细分类
     * @return 收费标准详细分类集合
     */
    List<QuoteChargeCategoryVO> selectQuoteChargeCategoryTreeList(QuoteChargeCategory quoteChargeCategory);

    /**
     * 查询扩项、分包类型id
     *
     * @param categoryName 分类名称
     * @return result
     */
    List<Long> selectChargeCategoryIdListByCategoryName(String categoryName);
}
