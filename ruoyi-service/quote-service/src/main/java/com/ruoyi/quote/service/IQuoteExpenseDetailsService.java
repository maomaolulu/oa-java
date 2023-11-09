package com.ruoyi.quote.service;

import java.util.List;

import com.ruoyi.quote.domain.dto.QuoteExpenseDetailsDTO;
import com.ruoyi.quote.domain.entity.QuoteExpenseDetails;

/**
 * 检测费用明细Service接口
 * 
 * @author yrb
 * @date 2022-04-29
 */
public interface IQuoteExpenseDetailsService 
{
    /**
     * 查询检测费用明细
     * 
     * @param id 检测费用明细主键
     * @return 检测费用明细
     */
    public QuoteExpenseDetails selectQuoteExpenseDetailsById(Long id);

    /**
     * 查询检测费用明细列表
     * 
     * @param quoteExpenseDetails 检测费用明细
     * @return 检测费用明细集合
     */
    public List<QuoteExpenseDetails> selectQuoteExpenseDetailsList(QuoteExpenseDetails quoteExpenseDetails);

    /**
     * 新增检测费用明细
     * 
     * @param quoteExpenseDetails 检测费用明细
     * @return 结果
     */
    public int insertQuoteExpenseDetails(QuoteExpenseDetails quoteExpenseDetails);

    /**
     * 修改检测费用明细
     * 
     * @param quoteExpenseDetails 检测费用明细
     * @return 结果
     */
    public int updateQuoteExpenseDetails(QuoteExpenseDetails quoteExpenseDetails);

    /**
     * 批量删除检测费用明细
     * 
     * @param ids 需要删除的检测费用明细主键集合
     * @return 结果
     */
    public int deleteQuoteExpenseDetailsByIds(Long[] ids);

    /**
     * 删除检测费用明细信息
     * 
     * @param id 检测费用明细主键
     * @return 结果
     */
    public int deleteQuoteExpenseDetailsById(Long id);

    /**
     * 设置临时文件为永久文件
     *
     * @return
     */
    int updateTempFlag(QuoteExpenseDetails quoteExpenseDetails);

    /**
     * 获取子类检测费用明细
     *
     * @param quoteExpenseDetailsDTO 表单id、子类全称、子类简称...
     * @return
     */
    List<QuoteExpenseDetails> findSubQuoteExpenseDetails(QuoteExpenseDetailsDTO quoteExpenseDetailsDTO);

    /**
     * 删除子类报价费用检测明细
     *
     * @param quoteExpenseDetails 表单id 子类id
     * @return result
     */
    boolean deleteQuoteExpenseDetailsBySubId(QuoteExpenseDetails quoteExpenseDetails);

    /**
     * 获取子类检测费用明细
     *
     * @param quoteExpenseDetails 表单id、子类id
     * @return result
     */
    QuoteExpenseDetails findSubExpenseDetails(QuoteExpenseDetails quoteExpenseDetails);
}
