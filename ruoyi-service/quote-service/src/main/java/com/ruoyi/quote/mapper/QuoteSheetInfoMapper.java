package com.ruoyi.quote.mapper;

import java.util.List;
import com.ruoyi.quote.domain.entity.QuoteSheetInfo;
import org.springframework.stereotype.Repository;

/**
 * 报价单信息Mapper接口
 * 
 * @author yrb
 * @date 2022-04-29
 */
@Repository
public interface QuoteSheetInfoMapper 
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
    public int insertQuoteSheetInfo(QuoteSheetInfo quoteSheetInfo);

    /**
     * 修改报价单信息
     * 
     * @param quoteSheetInfo 报价单信息
     * @return 结果
     */
    public int updateQuoteSheetInfo(QuoteSheetInfo quoteSheetInfo);

    /**
     * 删除报价单信息
     * 
     * @param id 报价单信息主键
     * @return 结果
     */
    public int deleteQuoteSheetInfoById(String id);

    /**
     * 批量删除报价单信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteQuoteSheetInfoByIds(String[] ids);

    /**
     * 查询报价单信息列表(自定义)
     *
     * @param quoteSheetInfo 报价单信息
     * @return 报价单信息集合
     */
    List<QuoteSheetInfo> selectQuoteSheetInfoUserList(QuoteSheetInfo quoteSheetInfo);
}
