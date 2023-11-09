package com.ruoyi.quote.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.quote.domain.dto.QuotePollutantTypeAddDTO;
import com.ruoyi.quote.domain.dto.QuotePollutantTypeDTO;
import com.ruoyi.quote.domain.entity.QuoteCategoryPollutant;
import com.ruoyi.quote.domain.entity.QuotePollutantType;
import com.ruoyi.quote.domain.entity.QuoteTestNature;
import com.ruoyi.quote.domain.entity.QuoteTestPollutant;
import com.ruoyi.quote.mapper.QuoteCategoryPollutantMapper;
import com.ruoyi.quote.mapper.QuoteTestPollutantMapper;
import com.ruoyi.quote.utils.QuoteUtil;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.mapper.QuotePollutantTypeMapper;
import com.ruoyi.quote.service.IQuotePollutantTypeService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 污染物类别Service业务层处理
 *
 * @author yrb
 * @date 2022-06-15
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class QuotePollutantTypeServiceImpl implements IQuotePollutantTypeService {
    private final QuotePollutantTypeMapper quotePollutantTypeMapper;
    private final QuoteCategoryPollutantMapper quoteCategoryPollutantMapper;
    private final QuoteTestPollutantMapper quoteTestPollutantMapper;

    @Autowired
    public QuotePollutantTypeServiceImpl(QuotePollutantTypeMapper quotePollutantTypeMapper,
                                         QuoteCategoryPollutantMapper quoteCategoryPollutantMapper,
                                         QuoteTestPollutantMapper quoteTestPollutantMapper) {
        this.quotePollutantTypeMapper = quotePollutantTypeMapper;
        this.quoteCategoryPollutantMapper = quoteCategoryPollutantMapper;
        this.quoteTestPollutantMapper = quoteTestPollutantMapper;
    }

    /**
     * 查询污染物类别
     *
     * @param id 污染物类别主键
     * @return 污染物类别
     */
    @Override
    public QuotePollutantType selectQuotePollutantTypeById(Long id) {
        return quotePollutantTypeMapper.selectQuotePollutantTypeById(id);
    }

    /**
     * 查询污染物类别列表
     *
     * @param quotePollutantType 污染物类别
     * @return 污染物类别
     */
    @Override
    public List<QuotePollutantType> selectQuotePollutantTypeList(QuotePollutantType quotePollutantType) {
        return quotePollutantTypeMapper.selectQuotePollutantTypeList(quotePollutantType);
    }

    /**
     * 新增污染物类别
     *
     * @param quotePollutantType 污染物类别
     * @return 结果
     */
    @Override
    public int insertQuotePollutantType(QuotePollutantType quotePollutantType) {
        quotePollutantType.setCreateTime(DateUtils.getNowDate());
        return quotePollutantTypeMapper.insertQuotePollutantType(quotePollutantType);
    }

    /**
     * 修改污染物类别
     *
     * @param quotePollutantType 污染物类别
     * @return 结果
     */
    @Override
    public int updateQuotePollutantType(QuotePollutantType quotePollutantType) {
        quotePollutantType.setUpdateTime(DateUtils.getNowDate());
        return quotePollutantTypeMapper.updateQuotePollutantType(quotePollutantType);
    }

    /**
     * 批量删除污染物类别
     *
     * @param ids 需要删除的污染物类别主键
     * @return 结果
     */
    @Override
    public int deleteQuotePollutantTypeByIds(Long[] ids) {
        return quotePollutantTypeMapper.deleteQuotePollutantTypeByIds(ids);
    }

    /**
     * 删除污染物类别信息
     *
     * @param id 污染物类别主键
     * @return 结果
     */
    @Override
    public int deleteQuotePollutantTypeById(Long id) {
        return quotePollutantTypeMapper.deleteQuotePollutantTypeById(id);
    }

    /**
     * 添加检测类别 （公卫--web端参数设置）
     *
     * @param quotePollutantTypeAddDTO 参数
     * @return 结果
     */
    @Override
    public boolean addPollutantType(QuotePollutantTypeAddDTO quotePollutantTypeAddDTO) {
        // 校验检测类别是否存在
        QuotePollutantType quotePollutantType = new QuotePollutantType();
        quotePollutantType.setProjectId(quotePollutantTypeAddDTO.getProjectId());
        quotePollutantType.setPollutantName(quotePollutantTypeAddDTO.getPollutantName());
        List<QuotePollutantType> pollutantTypeList = quotePollutantTypeMapper.selectQuotePollutantTypeList(quotePollutantType);
        if (CollUtil.isNotEmpty(pollutantTypeList)) {
            throw new RuntimeException("此检测类别已存在！");
        }
        quotePollutantType.setCreateTime(new Date());
        if (quotePollutantTypeMapper.insertQuotePollutantType(quotePollutantType) == 0) {
            throw new RuntimeException("插入检测类别失败！");
        }
        // 检验行业id、检测类别id关联表中是否有相应记录
        QuoteCategoryPollutant quoteCategoryPollutant = new QuoteCategoryPollutant();
        quoteCategoryPollutant.setMasterCategoryId(quotePollutantTypeAddDTO.getIndustryId());
        quoteCategoryPollutant.setPollutantTypeId(quotePollutantType.getId());
        List<QuoteCategoryPollutant> quoteCategoryPollutantList = quoteCategoryPollutantMapper.selectQuoteCategoryPollutantList(quoteCategoryPollutant);
        if (CollUtil.isNotEmpty(quoteCategoryPollutantList)) {
            throw new RuntimeException("此关联关系已存在！");
        }
        quoteCategoryPollutant.setProjectId(quotePollutantTypeAddDTO.getProjectId());
        quoteCategoryPollutant.setCreateTime(new Date());
        quoteCategoryPollutant.setUpdateTime(new Date());
        quoteCategoryPollutant.setCreator(SystemUtil.getUserNameCn());

        // 设置检测类别和检测类别id
        List<QuoteTestNature> quoteTestNatureList = quotePollutantTypeAddDTO.getQuoteTestNatureList();
        if (CollUtil.isNotEmpty(quoteTestNatureList)) {
            Map<String, String> map = QuoteUtil.getNatureNamesAndIds(quoteTestNatureList);
            quoteCategoryPollutant.setNatureIds(map.get(QuoteUtil.TEST_NATURE_IDS));
            quoteCategoryPollutant.setNatureName(map.get(QuoteUtil.TEST_NATURE_NAMES));
        }

        if (quoteCategoryPollutantMapper.insertQuoteCategoryPollutantInfo(quoteCategoryPollutant) == 0) {
            throw new RuntimeException("检测类别关联关系插入失败！");
        }
        // 检测类别关联污染物
        List<Long> ids = quotePollutantTypeAddDTO.getIds();
        if (CollUtil.isNotEmpty(ids)) {
            // 获取检测类别关联id
            Long id = quoteCategoryPollutant.getId();
            addRelationCaregoryAndPollutant(id, ids);
        }
        return true;
    }

    /**
     * 污染物关联检测类别、行业类别
     *
     * @param id  关联关系主键id
     * @param ids 污染物结合
     * @return result
     */
    @Override
    public boolean addRelationCaregoryAndPollutant(Long id, List<Long> ids) {
        for (Long pollutantId : ids) {
            QuoteTestPollutant quoteTestPollutant = new QuoteTestPollutant();
            quoteTestPollutant.setId(id);
            quoteTestPollutant.setPollutantId(pollutantId);
            quoteTestPollutant.setCreator(SystemUtil.getUserNameCn());
            quoteTestPollutant.setCreateTime(new Date());
            if (quoteTestPollutantMapper.insertQuoteTestPollutant(quoteTestPollutant) == 0) {
                throw new RuntimeException("检测类型、污染物关联关系插入失败！");
            }
        }
        return true;
    }

    /**
     * 获取检测类型列表（过滤当前子类已报价的）
     *
     * @param quotePollutantTypeDTO 大类id 过滤list
     * @return result
     */
    @Override
    public List<QuotePollutantType> findQuotePollutantTypeFilterList(QuotePollutantTypeDTO quotePollutantTypeDTO) {
        return quotePollutantTypeMapper.selectQuotePollutantTypeFilterList(quotePollutantTypeDTO);
    }

    /**
     * 查询检测类别信息 （数据导入时使用）
     *
     * @param quotePollutantType 检测类型名称 所属项目id
     * @return result
     */
    @Override
    public QuotePollutantType findQuotePollutantType(QuotePollutantType quotePollutantType) {
        return quotePollutantTypeMapper.selectQuotePollutantType(quotePollutantType);
    }
}
