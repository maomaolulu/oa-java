package com.ruoyi.quote.mapper;

import java.util.List;
import com.ruoyi.quote.domain.entity.QuotePostItems;
import org.springframework.stereotype.Repository;

/**
 * 岗位检测项目Mapper接口
 * 
 * @author yrb
 * @date 2022-06-10
 */
@Repository
public interface QuotePostItemsMapper
{
    /**
     * 查询岗位检测项目
     * 
     * @param id 岗位检测项目主键
     * @return 岗位检测项目
     */
    public QuotePostItems selectQuotePostItemsById(Long id);

    /**
     * 查询岗位检测项目列表
     * 
     * @param quotePostItems 岗位检测项目
     * @return 岗位检测项目集合
     */
    public List<QuotePostItems> selectQuotePostItemsList(QuotePostItems quotePostItems);

    /**
     * 新增岗位检测项目
     * 
     * @param quotePostItems 岗位检测项目
     * @return 结果
     */
    public int insertQuotePostItems(QuotePostItems quotePostItems);

    /**
     * 修改岗位检测项目
     * 
     * @param quotePostItems 岗位检测项目
     * @return 结果
     */
    public int updateQuotePostItems(QuotePostItems quotePostItems);

    /**
     * 删除岗位检测项目
     * 
     * @param id 岗位检测项目主键
     * @return 结果
     */
    public int deleteQuotePostItemsById(Long id);

    /**
     * 删除岗位检测项目(sheetId)
     *
     * @param sheetId
     * @return
     */
    int deleteQuotePostItemsBySheetId(String sheetId);

    /**
     * 批量删除岗位检测项目
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteQuotePostItemsByIds(Long[] ids);

    /**
     * 删除岗位对应的检测项
     *
     * @param quotePostItems 表单id or 子类id or 岗位名称
     * @return result
     */
    int deleteQuotePostItems(QuotePostItems quotePostItems);
}
