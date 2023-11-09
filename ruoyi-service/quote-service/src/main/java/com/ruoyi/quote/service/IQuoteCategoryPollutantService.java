package com.ruoyi.quote.service;

import java.util.List;

import com.ruoyi.quote.domain.dto.QuoteCategoryItemDTO;
import com.ruoyi.quote.domain.dto.QuotePollutantTypeDTO;
import com.ruoyi.quote.domain.entity.QuoteBaseFactor;
import com.ruoyi.quote.domain.entity.QuoteCategoryPollutant;
import com.ruoyi.quote.domain.entity.QuotePollutantType;
import com.ruoyi.quote.domain.vo.QuoteIndustryInfoVO;
import com.ruoyi.quote.domain.vo.QuotePollutantTypeVO;
import com.ruoyi.quote.domain.vo.QuoteTestTypeVO;

/**
 * 行业类别污染物类别关联Service接口
 * 
 * @author yrb
 * @date 2022-06-15
 */
public interface IQuoteCategoryPollutantService 
{
    /**
     * 查询行业类别污染物类别关联
     * 
     * @param id 行业类别污染物类别关联主键
     * @return 行业类别污染物类别关联
     */
    QuoteCategoryPollutant selectQuoteCategoryPollutantById(Long id);

    /**
     * 查询行业类别污染物类别关联列表
     * 
     * @param quoteCategoryPollutant 行业类别污染物类别关联
     * @return 行业类别污染物类别关联集合
     */
    List<QuoteCategoryPollutant> selectQuoteCategoryPollutantList(QuoteCategoryPollutant quoteCategoryPollutant);

    /**
     * 新增行业类别污染物类别关联
     * 
     * @param quoteCategoryPollutant 行业类别污染物类别关联
     * @return 结果
     */
    int insertQuoteCategoryPollutant(QuoteCategoryPollutant quoteCategoryPollutant);

    /**
     * 修改行业类别污染物类别关联
     * 
     * @param quoteCategoryPollutant 行业类别污染物类别关联
     * @return 结果
     */
    int updateQuoteCategoryPollutant(QuoteCategoryPollutant quoteCategoryPollutant);

    /**
     * 批量删除行业类别污染物类别关联
     * 
     * @param ids 需要删除的行业类别污染物类别关联主键集合
     * @return 结果
     */
    int deleteQuoteCategoryPollutantByIds(Long[] ids);

    /**
     * 删除行业类别污染物类别关联信息
     * 
     * @param id 行业类别污染物类别关联主键
     * @return 结果
     */
    int deleteQuoteCategoryPollutantById(Long id);

    /**
     * 根据主类id和子类id查找检测类别
     *
     * @param quoteCategoryPollutant 主类id和子类id
     * @return result
     */
    List<QuotePollutantType> selectRelationPollutantType(QuoteCategoryPollutant quoteCategoryPollutant);

    /**
     * 通过主类id、子类id、检测类型id获取污染物信息
     *
     * @param quoteCategoryItemDTO 主类id、子类id、检测类型id
     * @return result
     */
    List<QuoteBaseFactor> selectRelationPollutant(QuoteCategoryItemDTO quoteCategoryItemDTO);

    /**
     * 获取检测类别列表
     *
     * @param quoteIndustryInfoVO 行业大类、行业子类
     * @return result
     */
    List<QuoteTestTypeVO> findTestTypeList(QuoteIndustryInfoVO quoteIndustryInfoVO);

    /**
     * 获取行业大类、行业子类、检测类型对应的污染物
     *
     * @param quoteCategoryPollutant 行业大类id、行业子类id、检测类型id
     * @return 结果
     */
    List<QuoteBaseFactor> findRelationPollutantByTypes(QuoteCategoryPollutant quoteCategoryPollutant);

    /**
     * 添加关联污染物
     *
     * @param quoteCategoryPollutant 行业大类id、行业子类id、检测类型id
     * @param ids 污染物id集合
     * @return result
     */
    boolean addRelation(QuoteCategoryPollutant quoteCategoryPollutant,List<Long> ids);

    /**
     * 删除关联污染物
     *
     * @param quoteCategoryPollutant 行业大类id、行业子类id、检测类型id、污染物id
     * @return result
     */
    boolean deleteRelationPollutant(QuoteCategoryPollutant quoteCategoryPollutant);

    /**
     * 删除检测类型
     *
     * @param quoteCategoryPollutantList 行业大类id、行业子类id、检测类型id
     * @return result
     */
    boolean deleteTestType(List<QuoteCategoryPollutant> quoteCategoryPollutantList);

    /**
     * 获取检测类别列表 （公卫--web端--参数设置）
     *
     * @param quotePollutantTypeVO 行业名称
     * @return 结果
     */
    List<QuotePollutantTypeVO> findPollutantTypeList(QuotePollutantTypeVO quotePollutantTypeVO);

    /**
     * 编辑检测类别
     *
     * @param quotePollutantTypeDTO 关联主键id、新的检测类型名称
     * @return result
     */
    boolean editPollutantType(QuotePollutantTypeDTO quotePollutantTypeDTO);

    /**
     * 批量删除行业类别污染物类别关联(关联删除关联的污染物)
     *
     * @param ids 需要删除的行业类别污染物类别关联主键集合
     * @return 结果
     */
    boolean deleteCategoryPollutantByIds(Long[] ids);

    /**
     *  查询是否已存在关联关系（数据导入用）
     *
     * @param quoteCategoryPollutant 相关id
     * @return result
     */
    QuoteCategoryPollutant findQuoteCategoryPollutant(QuoteCategoryPollutant quoteCategoryPollutant);

    /**
     * 获取检测类别列表
     *
     * @param quoteCategoryPollutant 主类id 检测性质id
     * @return result
     */
    List<QuotePollutantType> findRelationPollutantTypeGw(QuoteCategoryPollutant quoteCategoryPollutant);
}
