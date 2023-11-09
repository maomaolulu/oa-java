package com.ruoyi.quote.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.quote.domain.dto.QuoteRelationFactorDTO;
import com.ruoyi.quote.domain.entity.QuoteBaseFactor;
import com.ruoyi.quote.domain.vo.QuoteBaseFactorVO;

/**
 * 危害因素基础信息Service接口
 * 
 * @author yrb
 * @date 2022-05-06
 */
public interface IQuoteBaseFactorService 
{
    /**
     * 批量插入危害因素
     *
     * @param quoteBaseFactorList 危害因素列表
     * @return 结果
     */
    int insertQuoteBaseFactorBatch(List<QuoteBaseFactor> quoteBaseFactorList);

    /**
     * 查询危害因素基础信息
     * 
     * @param id 危害因素基础信息主键
     * @return 危害因素基础信息
     */
    public QuoteBaseFactor selectQuoteBaseFactorById(Long id);

    /**
     * 查询危害因素基础信息列表
     * 
     * @param quoteBaseFactorVO 危害因素基础信息
     * @return 危害因素基础信息集合
     */
    public List<QuoteBaseFactorVO> selectQuoteBaseFactorList(QuoteBaseFactorVO quoteBaseFactorVO);

    /**
     * 查询危害因素基础信息列表
     *
     * @param quoteBaseFactorVO 危害因素基础信息
     * @return 危害因素基础信息集合
     */
    List<QuoteBaseFactorVO> selectQuoteBaseFactorUserList(QuoteBaseFactorVO quoteBaseFactorVO);

    /**
     * 新增危害因素基础信息
     * 
     * @param quoteBaseFactor 危害因素基础信息
     * @return 结果
     */
    public int insertQuoteBaseFactor(QuoteBaseFactor quoteBaseFactor);

    /**
     * 修改危害因素基础信息
     * 
     * @param quoteBaseFactor 危害因素基础信息
     * @return 结果
     */
    public int updateQuoteBaseFactor(QuoteBaseFactor quoteBaseFactor);

    /**
     * 批量删除危害因素基础信息
     * 
     * @param ids 需要删除的危害因素基础信息主键集合
     * @return 结果
     */
    public int deleteQuoteBaseFactorByIds(Long[] ids);

    /**
     * 删除危害因素基础信息信息
     * 
     * @param id 危害因素基础信息主键
     * @return 结果
     */
    public int deleteQuoteBaseFactorById(Long id);

    /**
     * 查询危害因素基础信息
     *
     * @param quoteBaseFactorVO 危害因素基础信息主键
     * @return 危害因素基础信息
     */
    QuoteBaseFactorVO findQuoteBaseFactor(QuoteBaseFactorVO quoteBaseFactorVO);

    /**
     * 查询危害因素基础信息列表 过滤已关联的
     *
     * @param quoteBaseFactorVO 危害因素基础信息
     * @return 危害因素基础信息集合
     */
    List<QuoteBaseFactorVO> findQuoteBaseFactorFilterList(QuoteBaseFactorVO quoteBaseFactorVO);

    /**
     * 批量删除危害因素基础信息 (先删除关联)
     *
     * @param ids 需要删除的危害因素基础信息主键集合
     * @return 结果
     */
    boolean deleteBaseFactorByIds(Long[] ids);

    /**
     * 查询危害因素基础信息列表 （公卫--过滤已关联的）
     *
     * @param quoteBaseFactorVO （行业类别、检测类型关联主键id）（污染物名称）（主类id）（子类id）
     * @return 危害因素基础信息集合
     */
    List<QuoteBaseFactor> findQuoteBaseFactorFilterRelationList(QuoteBaseFactorVO quoteBaseFactorVO);

    /**
     * 获取检测类别关联主键id
     *
     * @param quoteRelationFactorDTO 大类id、检测类别id
     * @return result
     */
    Long findQuoteRelationTypeId(QuoteRelationFactorDTO quoteRelationFactorDTO);

    /**
     * 获取检测类别关联的污染物（过滤当前点位已报价的）
     *
     * @param quoteBaseFactorVO id--关联主键id  list已报价污染物id
     * @return result
     */
    List<QuoteBaseFactor> findPollutantTypeRelationFactorList(QuoteBaseFactorVO quoteBaseFactorVO);

    /**
     * 获取污染物信息 （数据导入使用）
     *
     * @param quoteBaseFactor 污染物信息
     * @return result
     */
    QuoteBaseFactor findQuoteBaseFactorImport(QuoteBaseFactor quoteBaseFactor);

    /**
     * 获取已关联的污染物id
     *
     * @param map 主类id 子类id 检测类别id
     * @return result
     */
    List<Long> findRelationPollutantId(Map<String, Object> map);

    /**
     * 获取污染物信息 （公卫重构-数据导入）
     *
     * @param quoteBaseFactor 污染物信息
     * @return result
     */
    List<QuoteBaseFactor> findQuoteBaseFactorImportGwNew(QuoteBaseFactor quoteBaseFactor);
}
