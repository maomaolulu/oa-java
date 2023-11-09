package com.ruoyi.quote.service;

import java.util.List;

import com.ruoyi.quote.domain.entity.QuoteBaseFactor;
import com.ruoyi.quote.domain.entity.QuoteTestPollutant;

/**
 * 检测类型污染物关联Service接口
 * 
 * @author yrb
 * @date 2022-06-28
 */
public interface IQuoteTestPollutantService 
{
    /**
     * 查询检测类型污染物关联
     * 
     * @param id 检测类型污染物关联主键
     * @return 检测类型污染物关联
     */
    QuoteTestPollutant selectQuoteTestPollutantById(Long id);

    /**
     * 查询检测类型污染物关联列表
     * 
     * @param quoteTestPollutant 检测类型污染物关联
     * @return 检测类型污染物关联集合
     */
    List<QuoteTestPollutant> selectQuoteTestPollutantList(QuoteTestPollutant quoteTestPollutant);

    /**
     * 新增检测类型污染物关联
     * 
     * @param quoteTestPollutant 检测类型污染物关联
     * @return 结果
     */
    int insertQuoteTestPollutant(QuoteTestPollutant quoteTestPollutant);

    /**
     * 修改检测类型污染物关联
     * 
     * @param quoteTestPollutant 检测类型污染物关联
     * @return 结果
     */
    int updateQuoteTestPollutant(QuoteTestPollutant quoteTestPollutant);

    /**
     * 批量删除检测类型污染物关联
     * 
     * @param ids 需要删除的检测类型污染物关联主键集合
     * @return 结果
     */
    int deleteQuoteTestPollutantByIds(Long[] ids);

    /**
     * 删除检测类型污染物关联信息
     * 
     * @param id 检测类型污染物关联主键
     * @return 结果
     */
    int deleteQuoteTestPollutantById(Long id);

    /**
     * 查询危害因素列表 （公卫）
     *
     * @param id 关联表主键id
     * @return 结果
     */
    List<QuoteBaseFactor> findRelationBaseFactorList(Long id);

    /**
     * 通过行业大类、检测类别关联的主键id查询关联的污染物id
     *
     * @param id 主键id
     * @return result
     */
    List<Long> findPollutantIdListById(Long id);

    /**
     * 查询检测类型污染物关联信息 (公卫-数据导入)
     *
     * @param quoteTestPollutant 检测类型污染物关联
     * @return result
     */
    QuoteTestPollutant findQuoteTestPollutant(QuoteTestPollutant quoteTestPollutant);

    /**
     * 公卫 删除检测类别关联的污染物
     * @param quoteTestPollutant 关联id 污染物id
     * @return  result
     */
    boolean deleteRelationPollutant(QuoteTestPollutant quoteTestPollutant);
}
