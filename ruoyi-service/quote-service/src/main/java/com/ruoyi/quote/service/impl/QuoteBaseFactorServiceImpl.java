package com.ruoyi.quote.service.impl;

import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.quote.domain.dto.QuoteRelationFactorDTO;
import com.ruoyi.quote.domain.entity.QuoteBaseFactor;
import com.ruoyi.quote.domain.entity.QuoteCategoryPollutant;
import com.ruoyi.quote.domain.vo.QuoteBaseFactorVO;
import com.ruoyi.quote.mapper.*;
import com.ruoyi.quote.utils.QuoteUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.service.IQuoteBaseFactorService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 危害因素基础信息Service业务层处理
 *
 * @author yrb
 * @date 2022-05-06
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class QuoteBaseFactorServiceImpl implements IQuoteBaseFactorService {
    private final QuoteBaseFactorMapper quoteBaseFactorMapper;
    private final QuoteHarmFactorMapper quoteHarmFactorMapper;
    private final QuoteCategoryPollutantMapper quoteCategoryPollutantMapper;
    private final QuoteTestPollutantMapper quoteTestPollutantMapper;
    private final QuoteChargeCategoryMapper quoteChargeCategoryMapper;

    @Autowired
    public QuoteBaseFactorServiceImpl(QuoteBaseFactorMapper quoteBaseFactorMapper,
                                      QuoteHarmFactorMapper quoteHarmFactorMapper,
                                      QuoteCategoryPollutantMapper quoteCategoryPollutantMapper,
                                      QuoteTestPollutantMapper quoteTestPollutantMapper,
                                      QuoteChargeCategoryMapper quoteChargeCategoryMapper) {
        this.quoteBaseFactorMapper = quoteBaseFactorMapper;
        this.quoteHarmFactorMapper = quoteHarmFactorMapper;
        this.quoteCategoryPollutantMapper = quoteCategoryPollutantMapper;
        this.quoteTestPollutantMapper = quoteTestPollutantMapper;
        this.quoteChargeCategoryMapper = quoteChargeCategoryMapper;
    }

    /**
     * 批量插入危害因素
     *
     * @param quoteBaseFactorList 危害因素列表
     * @return 结果
     */
    @Override
    public int insertQuoteBaseFactorBatch(List<QuoteBaseFactor> quoteBaseFactorList) {
        return quoteBaseFactorMapper.insertQuoteBaseFactorBatch(quoteBaseFactorList);
    }

    /**
     * 查询危害因素基础信息
     *
     * @param id 危害因素基础信息主键
     * @return 危害因素基础信息
     */
    @Override
    public QuoteBaseFactor selectQuoteBaseFactorById(Long id) {
        return quoteBaseFactorMapper.selectQuoteBaseFactorById(id);
    }

    /**
     * 查询危害因素基础信息列表
     *
     * @param quoteBaseFactorVO 危害因素基础信息
     * @return 危害因素基础信息
     */
    @Override
    public List<QuoteBaseFactorVO> selectQuoteBaseFactorList(QuoteBaseFactorVO quoteBaseFactorVO) {
        return quoteBaseFactorMapper.selectQuoteBaseFactorUserList(quoteBaseFactorVO);
    }

    /**
     * 查询危害因素基础信息列表
     *
     * @param quoteBaseFactorVO 危害因素基础信息
     * @return 危害因素基础信息集合
     */
    @Override
    public List<QuoteBaseFactorVO> selectQuoteBaseFactorUserList(QuoteBaseFactorVO quoteBaseFactorVO) {
        List<QuoteBaseFactorVO> quoteBaseFactorVOList = quoteBaseFactorMapper.selectQuoteBaseFactorUserList(quoteBaseFactorVO);
        if (CollUtil.isNotEmpty(quoteBaseFactorVOList)) {
            for (QuoteBaseFactorVO item : quoteBaseFactorVOList) {
                // 拼接全称 危害因素名称+检测标准及编号（若不为空）
                String factorName = item.getFactorName();
                String standardInfo = item.getStandardInfo();
                if (StrUtil.isBlank(standardInfo)) {
                    item.setFullName(factorName);
                } else {
                    item.setFullName(factorName + " (" + standardInfo + " )");
                }
            }
        }
        return quoteBaseFactorVOList;
    }

    /**
     * 新增危害因素基础信息
     *
     * @param quoteBaseFactor 危害因素基础信息
     * @return 结果
     */
    @Override
    public int insertQuoteBaseFactor(QuoteBaseFactor quoteBaseFactor) {
        QuoteBaseFactor factor = new QuoteBaseFactor();
        factor.setCategoryId(quoteBaseFactor.getCategoryId());
        factor.setSubcategoryId(quoteBaseFactor.getSubcategoryId());
        factor.setFactorName(quoteBaseFactor.getFactorName());
        factor.setStandardInfo(quoteBaseFactor.getStandardInfo());
        factor.setLimitRange(quoteBaseFactor.getLimitRange());
        List<QuoteBaseFactorVO> quoteBaseFactorList = quoteBaseFactorMapper.selectQuoteBaseFactorExistsList(factor);
        if (CollUtil.isNotEmpty(quoteBaseFactorList)) {
            throw new RuntimeException("该物质已存在！");
        }
        // 设置分包和扩项
        Integer factorType = quoteBaseFactor.getFactorType();
        if (factorType != null) {
            List<Long> fenBaoList = quoteChargeCategoryMapper.selectChargeCategoryIdListByCategoryName(QuoteUtil.CHARGE_CATEGORY_FENBAO);
            List<Long> kuoXiangList = quoteChargeCategoryMapper.selectChargeCategoryIdListByCategoryName(QuoteUtil.CHARGE_CATEGORY_KUOXIANG);
            if (fenBaoList.contains(new Long(factorType))) {
                quoteBaseFactor.setFactorType(QuoteUtil.FACTOR_TYPE_FENBAO);
            }
            if (kuoXiangList.contains(new Long(factorType))) {
                quoteBaseFactor.setFactorType(QuoteUtil.FACTOR_TYPE_KUOXIANG);
            }
        }
        quoteBaseFactor.setCreateTime(DateUtils.getNowDate());
        return quoteBaseFactorMapper.insertQuoteBaseFactor(quoteBaseFactor);
    }

    /**
     * 修改危害因素基础信息
     *
     * @param quoteBaseFactor 危害因素基础信息
     * @return 结果
     */
    @Override
    public int updateQuoteBaseFactor(QuoteBaseFactor quoteBaseFactor) {
        QuoteBaseFactor factor = new QuoteBaseFactor();
        factor.setCategoryId(quoteBaseFactor.getCategoryId());
        factor.setSubcategoryId(quoteBaseFactor.getSubcategoryId());
        factor.setFactorName(quoteBaseFactor.getFactorName());
        factor.setStandardInfo(quoteBaseFactor.getStandardInfo());
        factor.setLimitRange(quoteBaseFactor.getLimitRange());
        factor.setPrice(quoteBaseFactor.getPrice());
        List<QuoteBaseFactorVO> quoteBaseFactorList = quoteBaseFactorMapper.selectQuoteBaseFactorExistsList(factor);
        if (CollUtil.isNotEmpty(quoteBaseFactorList)) {
            throw new RuntimeException("未编辑任何内容");
        }
        quoteBaseFactor.setUpdateTime(DateUtils.getNowDate());
        return quoteBaseFactorMapper.updateQuoteBaseFactor(quoteBaseFactor);
    }

    /**
     * 批量删除危害因素基础信息
     *
     * @param ids 需要删除的危害因素基础信息主键
     * @return 结果
     */
    @Override
    public int deleteQuoteBaseFactorByIds(Long[] ids) {
        return quoteBaseFactorMapper.deleteQuoteBaseFactorByIds(ids);
    }

    /**
     * 删除危害因素基础信息信息
     *
     * @param id 危害因素基础信息主键
     * @return 结果
     */
    @Override
    public int deleteQuoteBaseFactorById(Long id) {
        return quoteBaseFactorMapper.deleteQuoteBaseFactorById(id);
    }

    /**
     * 查询危害因素基础信息
     *
     * @param quoteBaseFactorVO 危害因素基础信息主键
     * @return 危害因素基础信息
     */
    @Override
    public QuoteBaseFactorVO findQuoteBaseFactor(QuoteBaseFactorVO quoteBaseFactorVO) {
        return quoteBaseFactorMapper.selectQuoteBaseFactor(quoteBaseFactorVO);
    }

    /**
     * 批量删除危害因素基础信息 (先删除关联)
     *
     * @param ids 需要删除的危害因素基础信息主键集合
     * @return 结果
     */
    @Override
    public boolean deleteBaseFactorByIds(Long[] ids) {
        // 1-1 职卫 删除关联污染物
        if (quoteHarmFactorMapper.countByBaseId(ids) > 0) {
            if (quoteHarmFactorMapper.deleteQuoteHarmFactorByBaseIds(ids) == 0) {
                throw new RuntimeException("删除危害因素关联岗位信息失败！");
            }
        }
        // 1-2 环境 删除关联污染物
        if (quoteCategoryPollutantMapper.countByPollutantId(ids) > 0) {
            if (quoteCategoryPollutantMapper.deleteQuoteCategoryPollutantByPollutantIds(ids) == 0) {
                throw new RuntimeException("删除关联污染物失败！");
            }
        }
        // 1-3 公卫 删除关联污染物
        if (quoteTestPollutantMapper.countByPollutantId(ids) > 0) {
            if (quoteTestPollutantMapper.deleteTestPollutantByPollutantIds(ids) == 0) {
                throw new RuntimeException("删除关联污染物失败！");
            }
        }
        // 2 删除基础信息
        if (quoteBaseFactorMapper.deleteQuoteBaseFactorByIds(ids) == 0) {
            throw new RuntimeException("危害因素信息删除失败！");
        }
        return true;
    }

    /**
     * 查询危害因素基础信息列表 过滤已关联的
     *
     * @param quoteBaseFactorVO
     * @return 危害因素基础信息集合
     */
    @Override
    public List<QuoteBaseFactorVO> findQuoteBaseFactorFilterList(QuoteBaseFactorVO quoteBaseFactorVO) {
        return quoteBaseFactorMapper.selectQuoteBaseFactorFilterList(quoteBaseFactorVO);
    }

    /**
     * 查询危害因素基础信息列表 （公卫--过滤已关联的）
     *
     * @param quoteBaseFactorVO （行业类别、检测类型关联主键id）（污染物名称）（主类id）（子类id）
     * @return 危害因素基础信息集合
     */
    @Override
    public List<QuoteBaseFactor> findQuoteBaseFactorFilterRelationList(QuoteBaseFactorVO quoteBaseFactorVO) {
        return quoteBaseFactorMapper.selectQuoteBaseFactorRelationList(quoteBaseFactorVO);
    }

    /**
     * 获取检测类别关联主键id
     *
     * @param quoteRelationFactorDTO 大类id、检测类别id
     * @return result
     */
    @Override
    public Long findQuoteRelationTypeId(QuoteRelationFactorDTO quoteRelationFactorDTO) {
        // 获取大类、检测类别关联主键id
        QuoteCategoryPollutant quoteCategoryPollutant = new QuoteCategoryPollutant();
        quoteCategoryPollutant.setMasterCategoryId(quoteRelationFactorDTO.getMasterCategoryId());
        quoteCategoryPollutant.setPollutantTypeId(quoteRelationFactorDTO.getPollutantTypeId());
        QuoteCategoryPollutant categoryPollutant = quoteCategoryPollutantMapper.selectRelationPollutantInfo(quoteCategoryPollutant);
        if (categoryPollutant == null || categoryPollutant.getId() == null) {
            throw new RuntimeException("关联id获取失败！");
        }
        return categoryPollutant.getId();
    }

    /**
     * 获取检测类别关联的污染物（过滤当前点位已报价的）
     *
     * @param quoteBaseFactorVO id--关联主键id  list已报价污染物id
     * @return result
     */
    @Override
    public List<QuoteBaseFactor> findPollutantTypeRelationFactorList(QuoteBaseFactorVO quoteBaseFactorVO) {
        return quoteBaseFactorMapper.selectPollutantTypeRelationFactorList(quoteBaseFactorVO);
    }

    /**
     * 获取污染物信息 （数据导入使用）
     *
     * @param quoteBaseFactor 污染物信息
     * @return result
     */
    @Override
    public QuoteBaseFactor findQuoteBaseFactorImport(QuoteBaseFactor quoteBaseFactor) {
        return quoteBaseFactorMapper.selectQuoteBaseFactorImport(quoteBaseFactor);
    }

    /**
     * 获取已关联的污染物id
     *
     * @param map 主类id 子类id 检测类别id
     * @return result
     */
    @Override
    public List<Long> findRelationPollutantId(Map<String, Object> map) {
        return quoteCategoryPollutantMapper.selectRelationPollutantId(map);
    }

    /**
     * 获取污染物信息 （公卫重构-数据导入）
     *
     * @param quoteBaseFactor 污染物信息
     * @return result
     */
    @Override
    public List<QuoteBaseFactor> findQuoteBaseFactorImportGwNew(QuoteBaseFactor quoteBaseFactor) {
        return quoteBaseFactorMapper.selectQuoteBaseFactorImportGwNew(quoteBaseFactor);
    }
}
