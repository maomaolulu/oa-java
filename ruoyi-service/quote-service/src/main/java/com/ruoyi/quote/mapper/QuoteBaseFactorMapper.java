package com.ruoyi.quote.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.quote.domain.entity.QuoteBaseFactor;
import com.ruoyi.quote.domain.vo.QuoteBaseFactorVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 危害因素基础信息Mapper接口
 *
 * @author yrb
 * @date 2022-05-06
 */
@Repository
public interface QuoteBaseFactorMapper extends BaseMapper {

    /**
     * 批量插入危害因素
     *
     * @param quoteBaseFactorList
     * @return 结果
     */
    int insertQuoteBaseFactorBatch(@Param("quoteBaseFactorList") List<QuoteBaseFactor> quoteBaseFactorList);

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
     * @param quoteBaseFactor 危害因素基础信息
     * @return 危害因素基础信息集合
     */
    public List<QuoteBaseFactor> selectQuoteBaseFactorList(QuoteBaseFactor quoteBaseFactor);

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
     * 删除危害因素基础信息
     *
     * @param id 危害因素基础信息主键
     * @return 结果
     */
    public int deleteQuoteBaseFactorById(Long id);

    /**
     * 批量删除危害因素基础信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteQuoteBaseFactorByIds(Long[] ids);

    /**
     * 查询危害因素基础信息
     *
     * @param quoteBaseFactorVO 危害因素信息
     * @return 危害因素基础信息
     */
    QuoteBaseFactorVO selectQuoteBaseFactor(QuoteBaseFactorVO quoteBaseFactorVO);

    /**
     * 查询危害因素基础信息列表
     *
     * @param quoteBaseFactorVO 危害因素基础信息
     * @return 危害因素基础信息集合
     */
    List<QuoteBaseFactorVO> selectQuoteBaseFactorUserList(QuoteBaseFactorVO quoteBaseFactorVO);

    /**
     * 查询危害因素基础信息列表(校验是否存在)
     *
     * @param quoteBaseFactor 危害因素基础信息
     * @return 危害因素基础信息集合
     */
    List<QuoteBaseFactorVO> selectQuoteBaseFactorExistsList(QuoteBaseFactor quoteBaseFactor);

    /**
     * 查询危害因素基础信息列表 过滤已关联的
     *
     * @param quoteBaseFactorVO 危害因素基础信息
     * @return 危害因素基础信息集合
     */
    List<QuoteBaseFactorVO> selectQuoteBaseFactorFilterList(QuoteBaseFactorVO quoteBaseFactorVO);

    /**
     * 查询危害因素基础信息列表 (公卫)
     *
     * @param quoteBaseFactorVO 危害因素基础信息
     * @return 危害因素基础信息集合
     */
    List<QuoteBaseFactor> selectQuoteBaseFactorRelationList(QuoteBaseFactorVO quoteBaseFactorVO);

    /**
     * 查询危害因素基础信息列表 (职卫)
     *
     * @param quoteBaseFactorVO 危害因素基础信息
     * @return 危害因素基础信息集合
     */
    List<QuoteBaseFactorVO>  selectQuoteBaseFactorZwRelationList(QuoteBaseFactorVO quoteBaseFactorVO);

    /**
     * 查询危害因素列表 （公卫）
     *
     * @param id 关联表主键id
     * @return 结果
     */
    List<QuoteBaseFactor> selectRelationBaseFactorList(Long id);

    /**
     * 获取检测类别关联的污染物（过滤当前点位已报价的）
     *
     * @param quoteBaseFactorVO id--关联主键id  list已报价污染物id
     * @return result
     */
    List<QuoteBaseFactor> selectPollutantTypeRelationFactorList(QuoteBaseFactorVO quoteBaseFactorVO);

    /**
     * 获取污染物信息 （数据导入使用）
     *
     * @param quoteBaseFactor 污染物信息
     * @return result
     */
    QuoteBaseFactor selectQuoteBaseFactorImport(QuoteBaseFactor quoteBaseFactor);

    /**
     * 获取污染物信息 （公卫重构-数据导入）
     *
     * @param quoteBaseFactor 污染物信息
     * @return result
     */
    List<QuoteBaseFactor> selectQuoteBaseFactorImportGwNew(QuoteBaseFactor quoteBaseFactor);
}
