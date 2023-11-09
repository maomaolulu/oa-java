package com.ruoyi.quote.mapper;

import java.util.List;
import com.ruoyi.quote.domain.entity.QuoteExpenseDetails;
import org.springframework.stereotype.Repository;

/**
 * 检测费用明细Mapper接口
 * 
 * @author yrb
 * @date 2022-04-29
 */
@Repository
public interface QuoteExpenseDetailsMapper 
{
    List<QuoteExpenseDetails> selectSubAbbBySheetId(String sheetId);

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
     * 删除检测费用明细
     * 
     * @param id 检测费用明细主键
     * @return 结果
     */
    public int deleteQuoteExpenseDetailsById(Long id);

    /**
     * 批量删除检测费用明细
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteQuoteExpenseDetailsByIds(Long[] ids);

    /**
     * 设置临时文件为永久文件
     *
     * @return
     */
    int updateTempFlag(QuoteExpenseDetails quoteExpenseDetails);

    /**
     * 删除检测费用明细
     *
     * @param quoteExpenseDetails 检测费用明细
     * @return result
     */
    int deleteQuoteExpenseDetails(QuoteExpenseDetails quoteExpenseDetails);

    /**
     * 获取子类检测费用明细
     *
     * @param quoteExpenseDetails 表单id、子类id
     * @return result
     */
    QuoteExpenseDetails selectSubExpenseDetails(QuoteExpenseDetails quoteExpenseDetails);

    /**
     * 删除检测费用临时数据
     *
     * @param quoteExpenseDetails 临时标志 创建时间
     * @return result
     */
    int deleteTempExpenseDetails(QuoteExpenseDetails quoteExpenseDetails);
}
