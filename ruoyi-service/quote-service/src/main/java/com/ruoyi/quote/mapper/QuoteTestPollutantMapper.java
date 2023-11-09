package com.ruoyi.quote.mapper;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.ruoyi.quote.domain.entity.QuoteTestPollutant;

/**
 * 检测类型污染物关联Mapper接口
 * 
 * @author yrb
 * @date 2022-06-28
 */
@Repository
public interface QuoteTestPollutantMapper
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
     * 删除检测类型污染物关联
     * 
     * @param id 检测类型污染物关联主键
     * @return 结果
     */
    int deleteQuoteTestPollutantById(Long id);

    /**
     * 批量删除检测类型污染物关联
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteQuoteTestPollutantByIds(Long[] ids);

    /**
     * 通过行业大类、检测类别关联的主键id查询关联的污染物id
     *
     * @param id 主键id
     * @return result
     */
    List<Long> selectPollutantIdListById(Long id);

    /**
     * 查询检测类型污染物关联
     *
     * @param id 检测类型污染物关联主键
     * @return 检测类型污染物关联
     */
    List<QuoteTestPollutant> selectTestPollutantById(Long id);

    /**
     * 批量删除行业类别污染物类别关联
     *
     * @param ids 需要删除的pollutantId集合
     * @return 结果
     */
    int deleteTestPollutantByPollutantIds(Long[] ids);

    /**
     * 查询关联个数
     *
     * @param ids 需要删除的pollutantId集合
     * @return result
     */
    int countByPollutantId(Long[] ids);

    /**
     * 查询检测类型污染物关联信息 (公卫-数据导入)
     *
     * @param quoteTestPollutant 检测类型污染物关联
     * @return result
     */
    QuoteTestPollutant selectQuoteTestPollutant(QuoteTestPollutant quoteTestPollutant);

    /**
     * 公卫 删除检测类别关联的污染物
     * @param quoteTestPollutant 关联id 污染物id
     * @return result
     */
    int deleteRelationPollutant(QuoteTestPollutant quoteTestPollutant);
}
