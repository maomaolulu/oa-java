package com.ruoyi.quote.service;

import java.util.List;
import com.ruoyi.quote.domain.entity.QuoteSheetInfo;

/**
 * 报价单信息Service接口
 * 
 * @author yrb
 * @date 2022-04-29
 */
public interface IQuoteSheetInfoService 
{
    /**
     * 查询报价单信息
     * 
     * @param id 报价单信息主键
     * @return 报价单信息
     */
    public QuoteSheetInfo selectQuoteSheetInfoById(String id);

    /**
     * 查询报价单信息列表
     * 
     * @param quoteSheetInfo 报价单信息
     * @return 报价单信息集合
     */
    public List<QuoteSheetInfo> selectQuoteSheetInfoList(QuoteSheetInfo quoteSheetInfo);

    /**
     * 新增报价单信息
     * 
     * @param quoteSheetInfo 报价单信息
     * @return 结果
     */
    public int insertQuoteSheetInfo(QuoteSheetInfo quoteSheetInfo)throws RuntimeException;

    /**
     * 修改报价单信息
     * 
     * @param quoteSheetInfo 报价单信息
     * @return 结果
     */
    public int updateQuoteSheetInfo(QuoteSheetInfo quoteSheetInfo);

    /**
     * 批量删除报价单信息
     * 
     * @param ids 需要删除的报价单信息主键集合
     * @return 结果
     */
    public int deleteQuoteSheetInfoByIds(String[] ids);

    /**
     * 删除报价单信息信息
     * 
     * @param id 报价单信息主键
     * @return 结果
     */
    public int deleteQuoteSheetInfoById(String id);
}
