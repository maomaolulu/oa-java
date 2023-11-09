package com.ruoyi.quote.service.impl;

import java.util.List;
import java.util.Objects;

import cn.hutool.core.collection.CollUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.quote.domain.entity.QuoteCategoryPollutant;
import com.ruoyi.quote.domain.entity.QuoteTestPollutant;
import com.ruoyi.quote.domain.vo.QuoteIndustryInfoVO;
import com.ruoyi.quote.mapper.QuoteCategoryPollutantMapper;
import com.ruoyi.quote.mapper.QuoteTestPollutantMapper;
import com.ruoyi.quote.service.IQuoteTestPollutantService;
import com.ruoyi.quote.utils.QuoteUtil;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.mapper.QuoteIndustryInfoMapper;
import com.ruoyi.quote.domain.entity.QuoteIndustryInfo;
import com.ruoyi.quote.service.IQuoteIndustryInfoService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 行业信息Service业务层处理
 *
 * @author yrb
 * @date 2022-04-26
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class QuoteIndustryInfoServiceImpl implements IQuoteIndustryInfoService {
    private final QuoteIndustryInfoMapper quoteIndustryInfoMapper;
    private final QuoteCategoryPollutantMapper quoteCategoryPollutantMapper;
    private final QuoteTestPollutantMapper quoteTestPollutantMapper;
    private final IQuoteTestPollutantService quoteTestPollutantService;

    @Autowired
    public QuoteIndustryInfoServiceImpl(QuoteIndustryInfoMapper quoteIndustryInfoMapper,
                                        QuoteCategoryPollutantMapper quoteCategoryPollutantMapper,
                                        QuoteTestPollutantMapper quoteTestPollutantMapper,
                                        IQuoteTestPollutantService quoteTestPollutantService) {
        this.quoteIndustryInfoMapper = quoteIndustryInfoMapper;
        this.quoteCategoryPollutantMapper = quoteCategoryPollutantMapper;
        this.quoteTestPollutantMapper = quoteTestPollutantMapper;
        this.quoteTestPollutantService = quoteTestPollutantService;
    }

    /**
     * 查询行业信息
     *
     * @param id 行业信息主键
     * @return 行业信息
     */
    @Override
    public QuoteIndustryInfo selectQuoteIndustryInfoById(Long id) {
        return quoteIndustryInfoMapper.selectQuoteIndustryInfoById(id);
    }

    /**
     * 查询行业信息列表
     *
     * @param quoteIndustryInfo 行业信息
     * @return 行业信息
     */
    @Override
    public List<QuoteIndustryInfo> selectQuoteIndustryInfoList(QuoteIndustryInfo quoteIndustryInfo) {
        return quoteIndustryInfoMapper.selectQuoteIndustryInfoList(quoteIndustryInfo);
    }

    /**
     * 新增行业信息
     *
     * @param quoteIndustryInfo 行业信息
     * @return 结果
     */
    @Override
    public int insertQuoteIndustryInfo(QuoteIndustryInfo quoteIndustryInfo) {
        // 校验是否添加过该行业
        QuoteIndustryInfo industryInfo = new QuoteIndustryInfo();
        industryInfo.setIndustryName(quoteIndustryInfo.getIndustryName());
        industryInfo.setProjectId(quoteIndustryInfo.getProjectId());
        industryInfo.setParentId(quoteIndustryInfo.getParentId());
        List<QuoteIndustryInfo> quoteIndustryInfoList = quoteIndustryInfoMapper.selectQuoteIndustryInfoList(industryInfo);
        if (CollUtil.isNotEmpty(quoteIndustryInfoList)) {
            throw new RuntimeException("该行业名称已存在！");
        }
        quoteIndustryInfo.setCreator(SystemUtil.getUserNameCn());
        quoteIndustryInfo.setCreateTime(DateUtils.getNowDate());
        if (quoteIndustryInfo.getParentId() == null) {
            quoteIndustryInfo.setParentId(0L);
        }
        return quoteIndustryInfoMapper.insertQuoteIndustryInfo(quoteIndustryInfo);
    }

    /**
     * 修改行业信息
     *
     * @param quoteIndustryInfo 行业信息
     * @return 结果
     */
    @Override
    public int updateQuoteIndustryInfo(QuoteIndustryInfo quoteIndustryInfo) {
        // 校验是否添加过该行业
        QuoteIndustryInfo industryInfo = new QuoteIndustryInfo();
        industryInfo.setIndustryName(quoteIndustryInfo.getIndustryName());
        industryInfo.setProjectId(quoteIndustryInfo.getProjectId());
        industryInfo.setParentId(quoteIndustryInfo.getParentId());
        List<QuoteIndustryInfo> quoteIndustryInfoList = quoteIndustryInfoMapper.selectQuoteIndustryInfoEditList(industryInfo);
        if (CollUtil.isNotEmpty(quoteIndustryInfoList)) {
            throw new RuntimeException("该行业名称已存在！");
        }
        quoteIndustryInfo.setUpdateTime(DateUtils.getNowDate());
        return quoteIndustryInfoMapper.updateQuoteIndustryInfo(quoteIndustryInfo);
    }

    /**
     * 批量删除行业信息
     *
     * @param ids 需要删除的行业信息主键
     * @return 结果
     */
    @Override
    public int deleteQuoteIndustryInfoByIds(Long[] ids) {
        return quoteIndustryInfoMapper.deleteQuoteIndustryInfoByIds(ids);
    }

    /**
     * 删除行业信息信息
     *
     * @param id 行业信息主键
     * @return 结果
     */
    @Override
    public int deleteQuoteIndustryInfoById(Long id) {
        return quoteIndustryInfoMapper.deleteQuoteIndustryInfoById(id);
    }

    /**
     * 查询行业信息
     *
     * @param quoteIndustryInfo 行业信息
     * @return 行业信息
     */
    @Override
    public QuoteIndustryInfo findQuoteIndustryInfo(QuoteIndustryInfo quoteIndustryInfo) {
        return quoteIndustryInfoMapper.selectQuoteIndustryInfo(quoteIndustryInfo);
    }

    /**
     * 查询行业信息列表(包含子父类)
     *
     * @param quoteIndustryInfoVO 行业信息
     * @return 行业信息集合
     */
    @Override
    public List<QuoteIndustryInfoVO> findQuoteIndustryInfoUserList(QuoteIndustryInfoVO quoteIndustryInfoVO) {
        return quoteIndustryInfoMapper.selectQuoteIndustryInfoUserList(quoteIndustryInfoVO);
    }

    /**
     * 删除行业大类（环境、公卫）
     *
     * @param ids 行业大类ids
     * @return 结果
     */
    @Override
    public boolean deleteIndustryInfo(Long[] ids) {
        Long industryId = ids[0];
        QuoteIndustryInfo industryInfo = quoteIndustryInfoMapper.selectQuoteIndustryInfoById(industryId);
        if (industryInfo == null || industryInfo.getProjectId() == null) {
            throw new RuntimeException("行业信息缺失");
        }
        Long projectId = industryInfo.getProjectId();
        if (Objects.equals(projectId, QuoteUtil.HJ_PROJECT_ID)) {
            for (Long id : ids) {
                // 查询并删除关联的子类信息
                QuoteIndustryInfo quoteIndustryInfo = new QuoteIndustryInfo();
                quoteIndustryInfo.setParentId(id);
                List<QuoteIndustryInfo> quoteIndustryInfoList = quoteIndustryInfoMapper.selectQuoteIndustryInfoList(quoteIndustryInfo);
                if (CollUtil.isNotEmpty(quoteIndustryInfoList)) {
                    // 主类
                    if (quoteIndustryInfoMapper.deleteQuoteIndustryInfoByParentId(id) == 0) {
                        throw new RuntimeException("删除行业大类关联的子类失败！");
                    }
                    QuoteCategoryPollutant quoteCategoryPollutant = new QuoteCategoryPollutant();
                    quoteCategoryPollutant.setMasterCategoryId(id);
                    List<QuoteCategoryPollutant> quoteCategoryPollutantList = quoteCategoryPollutantMapper.selectQuoteCategoryPollutantList(quoteCategoryPollutant);
                    if (CollUtil.isNotEmpty(quoteCategoryPollutantList)) {
                        if (quoteCategoryPollutantMapper.deleteRelationPollutantByCategory(quoteCategoryPollutant) == 0) {
                            throw new RuntimeException("删除行业大类关联信息失败！");
                        }
                        for (QuoteCategoryPollutant categoryPollutant : quoteCategoryPollutantList) {
                            Long key = categoryPollutant.getId();
                            QuoteTestPollutant quoteTestPollutant = new QuoteTestPollutant();
                            quoteTestPollutant.setId(key);
                            List<QuoteTestPollutant> list = quoteTestPollutantService.selectQuoteTestPollutantList(quoteTestPollutant);
                            if (CollUtil.isNotEmpty(list)) {
                                if (quoteTestPollutantMapper.deleteQuoteTestPollutantById(key) == 0) {
                                    throw new RuntimeException("关联的污染物信息删除失败！");
                                }
                            }
                        }
                    }
                } else {
                    // 子类
                    QuoteCategoryPollutant quoteCategoryPollutant = new QuoteCategoryPollutant();
                    quoteCategoryPollutant.setSubCategoryId(id);
                    List<QuoteCategoryPollutant> quoteCategoryPollutantList = quoteCategoryPollutantMapper.selectQuoteCategoryPollutantList(quoteCategoryPollutant);
                    if (CollUtil.isNotEmpty(quoteCategoryPollutantList)) {
                        if (quoteCategoryPollutantMapper.deleteRelationPollutantByCategory(quoteCategoryPollutant) == 0) {
                            throw new RuntimeException("删除行业子类关联信息失败！");
                        }
                        for (QuoteCategoryPollutant categoryPollutant : quoteCategoryPollutantList) {
                            Long key = categoryPollutant.getId();
                            QuoteTestPollutant quoteTestPollutant = new QuoteTestPollutant();
                            quoteTestPollutant.setId(key);
                            List<QuoteTestPollutant> list = quoteTestPollutantService.selectQuoteTestPollutantList(quoteTestPollutant);
                            if (CollUtil.isNotEmpty(list)) {
                                if (quoteTestPollutantMapper.deleteQuoteTestPollutantById(key) == 0) {
                                    throw new RuntimeException("关联的污染物信息删除失败！");
                                }
                            }
                        }
                    }
                }
                if (quoteIndustryInfoMapper.deleteQuoteIndustryInfoById(id) == 0) {
                    throw new RuntimeException("删除行业信息失败！");
                }
            }
        } else {
            for (Long id : ids) {
                // 查询是否为子父类关系
                QuoteIndustryInfo quoteIndustryInfo = new QuoteIndustryInfo();
                quoteIndustryInfo.setParentId(id);
                List<QuoteIndustryInfo> quoteIndustryInfoList = quoteIndustryInfoMapper.selectQuoteIndustryInfoList(quoteIndustryInfo);
                if (CollUtil.isNotEmpty(quoteIndustryInfoList)) {
                    if (quoteIndustryInfoMapper.deleteQuoteIndustryInfoByParentId(id) == 0) {
                        throw new RuntimeException("删除行业大类关联的子类失败！");
                    }
                }
                // 删除关联关系
                QuoteCategoryPollutant quoteCategoryPollutant = new QuoteCategoryPollutant();
                quoteCategoryPollutant.setMasterCategoryId(id);
                List<QuoteCategoryPollutant> quoteCategoryPollutantList = quoteCategoryPollutantMapper.selectQuoteCategoryPollutantList(quoteCategoryPollutant);
                if (CollUtil.isNotEmpty(quoteCategoryPollutantList)) {
                    if (quoteCategoryPollutantMapper.deleteRelationPollutantByCategory(quoteCategoryPollutant) == 0) {
                        throw new RuntimeException("删除行业大类关联信息失败！");
                    }
                    for (QuoteCategoryPollutant categoryPollutant : quoteCategoryPollutantList) {
                        Long key = categoryPollutant.getId();
                        QuoteTestPollutant quoteTestPollutant = new QuoteTestPollutant();
                        quoteTestPollutant.setId(key);
                        List<QuoteTestPollutant> list = quoteTestPollutantService.selectQuoteTestPollutantList(quoteTestPollutant);
                        if (CollUtil.isNotEmpty(list)) {
                            if (quoteTestPollutantMapper.deleteQuoteTestPollutantById(key) == 0) {
                                throw new RuntimeException("关联的污染物信息删除失败！");
                            }
                        }
                    }
                }
                QuoteCategoryPollutant quoteCategoryPollutant1 = new QuoteCategoryPollutant();
                quoteCategoryPollutant1.setSubCategoryId(id);
                List<QuoteCategoryPollutant> quoteCategoryPollutantList1 = quoteCategoryPollutantMapper.selectQuoteCategoryPollutantList(quoteCategoryPollutant1);
                if (CollUtil.isNotEmpty(quoteCategoryPollutantList1)) {
                    if (quoteCategoryPollutantMapper.deleteRelationPollutantByCategory(quoteCategoryPollutant1) == 0) {
                        throw new RuntimeException("删除行业子类关联信息失败！");
                    }
                    for (QuoteCategoryPollutant categoryPollutant : quoteCategoryPollutantList1) {
                        Long key = categoryPollutant.getId();
                        QuoteTestPollutant quoteTestPollutant = new QuoteTestPollutant();
                        quoteTestPollutant.setId(key);
                        List<QuoteTestPollutant> list = quoteTestPollutantService.selectQuoteTestPollutantList(quoteTestPollutant);
                        if (CollUtil.isNotEmpty(list)) {
                            if (quoteTestPollutantMapper.deleteQuoteTestPollutantById(key) == 0) {
                                throw new RuntimeException("关联的污染物信息删除失败！");
                            }
                        }
                    }
                }
                if (quoteIndustryInfoMapper.deleteQuoteIndustryInfoById(id) == 0) {
                    throw new RuntimeException("删除行业信息失败！");
                }
            }
        }
        return true;
    }

    /**
     * 获取行业列表（公卫、职卫）
     *
     * @param quoteIndustryInfo 行业名称（可选） 项目id
     * @return 结果
     */
    @Override
    public List<QuoteIndustryInfoVO> findIndustryInfoList(QuoteIndustryInfo quoteIndustryInfo) {
        return quoteIndustryInfoMapper.selectIndustryInfoList(quoteIndustryInfo);
    }
}
