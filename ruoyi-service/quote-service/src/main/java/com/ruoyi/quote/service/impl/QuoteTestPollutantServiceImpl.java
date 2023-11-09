package com.ruoyi.quote.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.quote.domain.entity.QuoteBaseFactor;
import com.ruoyi.quote.mapper.QuoteBaseFactorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.mapper.QuoteTestPollutantMapper;
import com.ruoyi.quote.domain.entity.QuoteTestPollutant;
import com.ruoyi.quote.service.IQuoteTestPollutantService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 检测类型污染物关联Service业务层处理
 *
 * @author yrb
 * @date 2022-06-28
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class QuoteTestPollutantServiceImpl implements IQuoteTestPollutantService {
    private final QuoteTestPollutantMapper quoteTestPollutantMapper;
    private final QuoteBaseFactorMapper quoteBaseFactorMapper;

    @Autowired
    public QuoteTestPollutantServiceImpl(QuoteTestPollutantMapper quoteTestPollutantMapper,
                                         QuoteBaseFactorMapper quoteBaseFactorMapper) {
        this.quoteTestPollutantMapper = quoteTestPollutantMapper;
        this.quoteBaseFactorMapper = quoteBaseFactorMapper;
    }

    /**
     * 查询检测类型污染物关联
     *
     * @param id 检测类型污染物关联主键
     * @return 检测类型污染物关联
     */
    @Override
    public QuoteTestPollutant selectQuoteTestPollutantById(Long id) {
        return quoteTestPollutantMapper.selectQuoteTestPollutantById(id);
    }

    /**
     * 查询检测类型污染物关联列表
     *
     * @param quoteTestPollutant 检测类型污染物关联
     * @return 检测类型污染物关联
     */
    @Override
    public List<QuoteTestPollutant> selectQuoteTestPollutantList(QuoteTestPollutant quoteTestPollutant) {
        return quoteTestPollutantMapper.selectQuoteTestPollutantList(quoteTestPollutant);
    }

    /**
     * 新增检测类型污染物关联
     *
     * @param quoteTestPollutant 检测类型污染物关联
     * @return 结果
     */
    @Override
    public int insertQuoteTestPollutant(QuoteTestPollutant quoteTestPollutant) {
        quoteTestPollutant.setCreateTime(DateUtils.getNowDate());
        return quoteTestPollutantMapper.insertQuoteTestPollutant(quoteTestPollutant);
    }

    /**
     * 修改检测类型污染物关联
     *
     * @param quoteTestPollutant 检测类型污染物关联
     * @return 结果
     */
    @Override
    public int updateQuoteTestPollutant(QuoteTestPollutant quoteTestPollutant) {
        return quoteTestPollutantMapper.updateQuoteTestPollutant(quoteTestPollutant);
    }

    /**
     * 批量删除检测类型污染物关联
     *
     * @param ids 需要删除的检测类型污染物关联主键
     * @return 结果
     */
    @Override
    public int deleteQuoteTestPollutantByIds(Long[] ids) {
        return quoteTestPollutantMapper.deleteQuoteTestPollutantByIds(ids);
    }

    /**
     * 删除检测类型污染物关联信息
     *
     * @param id 检测类型污染物关联主键
     * @return 结果
     */
    @Override
    public int deleteQuoteTestPollutantById(Long id) {
        return quoteTestPollutantMapper.deleteQuoteTestPollutantById(id);
    }

    /**
     * 查询危害因素列表 （公卫）
     *
     * @param id 关联表主键id
     * @return 结果
     */
    @Override
    public List<QuoteBaseFactor> findRelationBaseFactorList(Long id) {
        return quoteBaseFactorMapper.selectRelationBaseFactorList(id);
    }

    /**
     * 通过行业大类、检测类别关联的主键id查询关联的污染物id
     *
     * @param id 主键id
     * @return result
     */
    @Override
    public List<Long> findPollutantIdListById(Long id) {
        return quoteTestPollutantMapper.selectPollutantIdListById(id);
    }

    /**
     * 查询检测类型污染物关联信息 (公卫-数据导入)
     *
     * @param quoteTestPollutant 检测类型污染物关联
     * @return result
     */
    @Override
    public QuoteTestPollutant findQuoteTestPollutant(QuoteTestPollutant quoteTestPollutant) {
        return quoteTestPollutantMapper.selectQuoteTestPollutant(quoteTestPollutant);
    }

    /**
     * 公卫 删除检测类别关联的污染物
     *
     * @param quoteTestPollutant 关联id 污染物id
     * @return result
     */
    @Override
    public boolean deleteRelationPollutant(QuoteTestPollutant quoteTestPollutant) {
        QuoteTestPollutant testPollutant = new QuoteTestPollutant();
        testPollutant.setId(quoteTestPollutant.getId());
        testPollutant.setPollutantId(quoteTestPollutant.getPollutantId());
        return quoteTestPollutantMapper.deleteRelationPollutant(testPollutant) != 0;
    }
}
