package com.ruoyi.quote.service;

import java.util.List;

import com.ruoyi.quote.domain.dto.PostExpenseDetailsDTO;
import com.ruoyi.quote.domain.entity.QuotePostItems;

/**
 * 岗位检测项目Service接口
 * 
 * @author yrb
 * @date 2022-06-10
 */
public interface IQuotePostItemsService 
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
     * 批量删除岗位检测项目
     * 
     * @param ids 需要删除的岗位检测项目主键集合
     * @return 结果
     */
    public int deleteQuotePostItemsByIds(Long[] ids);

    /**
     * 删除岗位检测项目信息
     * 
     * @param id 岗位检测项目主键
     * @return 结果
     */
    public int deleteQuotePostItemsById(Long id);

    /**
     * 删除岗位检测项目信息
     *
     * @param sheetId 表单id
     * @return
     */
    int deleteQuotePostItemsBySheetId(String sheetId);

    /**
     * 子类对应岗位检测费用信息
     *
     * @param postExpenseDetailsDTO 表单id 公司名称 子类id 岗位id 所选岗位id集合
     * @return result
     */
    List<QuotePostItems> findSubPostExpenseDetails(PostExpenseDetailsDTO postExpenseDetailsDTO);

    /**
     * 删除子类对应的岗位检测信息
     *
     * @param postExpenseDetailsDTO 表单id 子类id 岗位id
     * @return
     */
    boolean deleteSubPostExpenseDetails(PostExpenseDetailsDTO postExpenseDetailsDTO);
}
