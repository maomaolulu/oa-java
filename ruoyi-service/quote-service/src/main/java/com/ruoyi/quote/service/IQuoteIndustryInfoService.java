package com.ruoyi.quote.service;

import java.util.List;
import com.ruoyi.quote.domain.entity.QuoteIndustryInfo;
import com.ruoyi.quote.domain.vo.QuoteIndustryInfoVO;

/**
 * 行业信息Service接口
 * 
 * @author yrb
 * @date 2022-04-26
 */
public interface IQuoteIndustryInfoService 
{
    /**
     * 查询行业信息
     * 
     * @param id 行业信息主键
     * @return 行业信息
     */
    QuoteIndustryInfo selectQuoteIndustryInfoById(Long id);

    /**
     * 查询行业信息列表
     * 
     * @param quoteIndustryInfo 行业信息
     * @return 行业信息集合
     */
    List<QuoteIndustryInfo> selectQuoteIndustryInfoList(QuoteIndustryInfo quoteIndustryInfo);

    /**
     * 新增行业信息
     * 
     * @param quoteIndustryInfo 行业信息
     * @return 结果
     */
    int insertQuoteIndustryInfo(QuoteIndustryInfo quoteIndustryInfo);

    /**
     * 修改行业信息
     * 
     * @param quoteIndustryInfo 行业信息
     * @return 结果
     */
    int updateQuoteIndustryInfo(QuoteIndustryInfo quoteIndustryInfo);

    /**
     * 批量删除行业信息
     * 
     * @param ids 需要删除的行业信息主键集合
     * @return 结果
     */
    int deleteQuoteIndustryInfoByIds(Long[] ids);

    /**
     * 删除行业信息信息
     * 
     * @param id 行业信息主键
     * @return 结果
     */
    public int deleteQuoteIndustryInfoById(Long id);

    /**
     * 查询行业信息
     *
     * @param quoteIndustryInfo 行业信息
     * @return 行业信息
     */
    QuoteIndustryInfo findQuoteIndustryInfo(QuoteIndustryInfo quoteIndustryInfo);

    /**
     * 查询行业信息列表(包含子父类)
     *
     * @param quoteIndustryInfoVO 行业信息
     * @return 行业信息集合
     */
    List<QuoteIndustryInfoVO> findQuoteIndustryInfoUserList(QuoteIndustryInfoVO quoteIndustryInfoVO);

    /**
     * 删除行业大类（环境、公卫）
     *
     * @param ids 行业大类ids
     * @return 结果
     */
    boolean deleteIndustryInfo(Long[] ids);

    /**
     * 获取行业列表（公卫、职卫）
     *
     * @param quoteIndustryInfo 行业名称（可选） 项目id
     * @return 结果
     */
    List<QuoteIndustryInfoVO> findIndustryInfoList(QuoteIndustryInfo quoteIndustryInfo);
}
