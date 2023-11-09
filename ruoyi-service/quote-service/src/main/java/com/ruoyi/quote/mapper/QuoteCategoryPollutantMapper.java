package com.ruoyi.quote.mapper;

import java.util.List;
import java.util.Map;

import com.ruoyi.quote.domain.entity.*;
import com.ruoyi.quote.domain.vo.QuoteIndustryInfoVO;
import com.ruoyi.quote.domain.vo.QuotePollutantTestTypeVO;
import com.ruoyi.quote.domain.vo.QuotePollutantTypeVO;
import com.ruoyi.quote.domain.vo.QuoteTestTypeVO;
import org.springframework.stereotype.Repository;

/**
 * 行业类别污染物类别关联Mapper接口
 * 
 * @author yrb
 * @date 2022-06-15
 */
@Repository
public interface QuoteCategoryPollutantMapper
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
     * 删除行业类别污染物类别关联
     * 
     * @param id 行业类别污染物类别关联主键
     * @return 结果
     */
    int deleteQuoteCategoryPollutantById(Long id);

    /**
     * 批量删除行业类别污染物类别关联
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteQuoteCategoryPollutantByIds(Long[] ids);

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
     * @param map 主类id、子类id、检测类型id
     * @return result
     */
    List<QuoteBaseFactor> selectRelationPollutant(Map<String,Object> map);

    /**
     * 通过主类id、子类id、检测类型id获取污染物信息
     *
     * @param map 主类id、子类id、检测类型id
     * @return result
     */
    List<Long> selectRelationPollutantId(Map<String,Object> map);

    /**
     * 获取检测类别列表
     *
     * @param quoteIndustryInfoVO 行业大类、行业子类
     * @return result
     */
    List<QuoteTestTypeVO> selectTestTypeList(QuoteIndustryInfoVO quoteIndustryInfoVO);

    /**
     * 获取行业大类、行业子类、检测类型对应的污染物
     *
     * @param quoteCategoryPollutant 行业大类id、行业子类id、检测类型id
     * @return 结果
     */
    List<QuoteBaseFactor> selectRelationPollutantByTypes(QuoteCategoryPollutant quoteCategoryPollutant);

    /**
     * 删除关联污染物
     *
     * @param quoteCategoryPollutant 行业大类id、行业子类id、检测类型id、污染物id
     * @return result
     */
    int deleteRelationPollutant(QuoteCategoryPollutant quoteCategoryPollutant);

    /**
     * 删除行业大类 关联删除污染物关联关系
     *
     * @param quoteCategoryPollutant 行业大类id、行业子类id
     * @return result
     */
    int deleteRelationPollutantByCategory(QuoteCategoryPollutant quoteCategoryPollutant);

    /**
     * 获取检测类别列表 （公卫--web端--参数设置）
     *
     * @param quotePollutantTypeVO
     * @return 结果
     */
    List<QuotePollutantTypeVO> selectPollutantTypeList(QuotePollutantTypeVO quotePollutantTypeVO);

    /**
     * 获取关联对象
     *
     * @param quoteCategoryPollutant 大类id、检测类别id
     */
    QuoteCategoryPollutant selectRelationPollutantInfo(QuoteCategoryPollutant quoteCategoryPollutant);

    /**
     * 新增行业类别污染物类别关联(返回主键)
     *
     * @param quoteCategoryPollutant 行业类别污染物类别关联
     * @return 结果
     */
    int insertQuoteCategoryPollutantInfo(QuoteCategoryPollutant quoteCategoryPollutant);

    /**
     * 批量删除行业类别污染物类别关联
     *
     * @param ids 需要删除的pollutantId集合
     * @return 结果
     */
    int deleteQuoteCategoryPollutantByPollutantIds(Long[] ids);

    /**
     * 查询关联个数
     *
     * @param ids 需要删除的pollutantId集合
     * @return result
     */
    int countByPollutantId(Long[] ids);

    /**
     *  查询是否已存在关联关系（数据导入用）
     *
     * @param quoteCategoryPollutant 相关id
     * @return result
     */
    QuoteCategoryPollutant selectQuoteCategoryPollutant(QuoteCategoryPollutant quoteCategoryPollutant);

    /**
     * 获取检测类别列表
     *
     * @param quoteCategoryPollutant 主类id 检测性质id
     * @return result
     */
    List<QuotePollutantType> selectRelationPollutantTypeGw(QuoteCategoryPollutant quoteCategoryPollutant);

    /**
     * 获取检测类别列表
     *
     * @param quoteCategoryPollutant 主类id 检测性质id
     * @return result
     */
    List<QuotePollutantTestTypeVO> selectRelationPollutantTypeCheckedGw(QuoteCategoryPollutant quoteCategoryPollutant);
}
