package com.ruoyi.quote.service.impl;

import java.util.*;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.quote.domain.dto.QuoteCategoryItemDTO;
import com.ruoyi.quote.domain.dto.QuotePollutantTypeDTO;
import com.ruoyi.quote.domain.entity.*;
import com.ruoyi.quote.domain.vo.QuoteIndustryInfoVO;
import com.ruoyi.quote.domain.vo.QuotePollutantTypeVO;
import com.ruoyi.quote.domain.vo.QuoteTestTypeVO;
import com.ruoyi.quote.mapper.QuotePollutantTypeMapper;
import com.ruoyi.quote.mapper.QuoteTestPollutantMapper;
import com.ruoyi.quote.utils.QuoteUtil;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.mapper.QuoteCategoryPollutantMapper;
import com.ruoyi.quote.service.IQuoteCategoryPollutantService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 行业类别污染物类别关联Service业务层处理
 *
 * @author yrb
 * @date 2022-06-15
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class QuoteCategoryPollutantServiceImpl implements IQuoteCategoryPollutantService {
    private final QuoteCategoryPollutantMapper quoteCategoryPollutantMapper;
    private final QuotePollutantTypeMapper quotePollutantTypeMapper;
    private final QuoteTestPollutantMapper quoteTestPollutantMapper;
    private final RedisUtils redisUtils;

    @Autowired
    public QuoteCategoryPollutantServiceImpl(QuoteCategoryPollutantMapper quoteCategoryPollutantMapper,
                                             QuotePollutantTypeMapper quotePollutantTypeMapper,
                                             QuoteTestPollutantMapper quoteTestPollutantMapper,
                                             RedisUtils redisUtils) {
        this.quoteCategoryPollutantMapper = quoteCategoryPollutantMapper;
        this.quotePollutantTypeMapper = quotePollutantTypeMapper;
        this.quoteTestPollutantMapper = quoteTestPollutantMapper;
        this.redisUtils = redisUtils;
    }

    /**
     * 查询行业类别污染物类别关联
     *
     * @param id 行业类别污染物类别关联主键
     * @return 行业类别污染物类别关联
     */
    @Override
    public QuoteCategoryPollutant selectQuoteCategoryPollutantById(Long id) {
        return quoteCategoryPollutantMapper.selectQuoteCategoryPollutantById(id);
    }

    /**
     * 查询行业类别污染物类别关联列表
     *
     * @param quoteCategoryPollutant 行业类别污染物类别关联
     * @return 行业类别污染物类别关联
     */
    @Override
    public List<QuoteCategoryPollutant> selectQuoteCategoryPollutantList(QuoteCategoryPollutant quoteCategoryPollutant) {
        return quoteCategoryPollutantMapper.selectQuoteCategoryPollutantList(quoteCategoryPollutant);
    }

    /**
     * 新增行业类别污染物类别关联
     *
     * @param quoteCategoryPollutant 行业类别污染物类别关联
     * @return 结果
     */
    @Override
    public int insertQuoteCategoryPollutant(QuoteCategoryPollutant quoteCategoryPollutant) {
        quoteCategoryPollutant.setCreateTime(DateUtils.getNowDate());
        return quoteCategoryPollutantMapper.insertQuoteCategoryPollutant(quoteCategoryPollutant);
    }

    /**
     * 修改行业类别污染物类别关联
     *
     * @param quoteCategoryPollutant 行业类别污染物类别关联
     * @return 结果
     */
    @Override
    public int updateQuoteCategoryPollutant(QuoteCategoryPollutant quoteCategoryPollutant) {
        quoteCategoryPollutant.setUpdateTime(DateUtils.getNowDate());
        return quoteCategoryPollutantMapper.updateQuoteCategoryPollutant(quoteCategoryPollutant);
    }

    /**
     * 批量删除行业类别污染物类别关联
     *
     * @param ids 需要删除的行业类别污染物类别关联主键
     * @return 结果
     */
    @Override
    public int deleteQuoteCategoryPollutantByIds(Long[] ids) {
        return quoteCategoryPollutantMapper.deleteQuoteCategoryPollutantByIds(ids);
    }

    /**
     * 删除行业类别污染物类别关联信息
     *
     * @param id 行业类别污染物类别关联主键
     * @return 结果
     */
    @Override
    public int deleteQuoteCategoryPollutantById(Long id) {
        return quoteCategoryPollutantMapper.deleteQuoteCategoryPollutantById(id);
    }

    /**
     * 根据主类id和子类id查找检测类别
     *
     * @param quoteCategoryPollutant 主类id和子类id
     * @return result
     */
    @Override
    public List<QuotePollutantType> selectRelationPollutantType(QuoteCategoryPollutant quoteCategoryPollutant) {
        return quoteCategoryPollutantMapper.selectRelationPollutantType(quoteCategoryPollutant);
    }

    /**
     * 通过主类id、子类id、检测类型id获取污染物信息
     *
     * @param quoteCategoryItemDTO --> quoteCategoryPollutant 主类id、子类id、检测类型id
     * @param quoteCategoryItemDTO --> quoteTestItem 表单id、子类id、检测点位
     * @return result
     */
    @Override
    public List<QuoteBaseFactor> selectRelationPollutant(QuoteCategoryItemDTO quoteCategoryItemDTO) {
        QuoteCategoryPollutant quoteCategoryPollutant = quoteCategoryItemDTO.getQuoteCategoryPollutant();
        Map<String, Object> map = new HashMap<>();
        map.put("masterCategoryId", quoteCategoryPollutant.getMasterCategoryId());
        map.put("subCategoryId", quoteCategoryPollutant.getSubCategoryId());
        map.put("pollutantTypeId", quoteCategoryPollutant.getPollutantTypeId());
        // 通过表单id、子类id、检测点位过滤已选的污染物
        QuoteTestItem quoteTestItem = quoteCategoryItemDTO.getQuoteTestItem();
        String idsKey = QuoteUtil.getHjTestItemsIdsKey(quoteTestItem.getSheetId(), quoteTestItem.getSubId(), quoteTestItem.getPointId(), SystemUtil.getUserId());
        String idsValue = redisUtils.get(idsKey);
        if (StrUtil.isNotBlank(idsValue)) {
            List<Long> list = QuoteUtil.getList(redisUtils.get(idsKey));
            map.put("list", list);
        }
        return quoteCategoryPollutantMapper.selectRelationPollutant(map);
    }

    /**
     * 获取检测类别列表
     *
     * @param quoteIndustryInfoVO 行业大类、行业子类
     * @return result
     */
    @Override
    public List<QuoteTestTypeVO> findTestTypeList(QuoteIndustryInfoVO quoteIndustryInfoVO) {
        return quoteCategoryPollutantMapper.selectTestTypeList(quoteIndustryInfoVO);
    }

    /**
     * 获取行业大类、行业子类、检测类型对应的污染物
     *
     * @param quoteCategoryPollutant 行业大类id、行业子类id、检测类型id
     * @return 结果
     */
    @Override
    public List<QuoteBaseFactor> findRelationPollutantByTypes(QuoteCategoryPollutant quoteCategoryPollutant) {
        return quoteCategoryPollutantMapper.selectRelationPollutantByTypes(quoteCategoryPollutant);
    }

    /**
     * 添加关联污染物
     *
     * @param quoteCategoryPollutant 行业大类id、行业子类id、检测类型id
     * @param ids                    污染物id集合
     * @return result
     */
    @Override
    public boolean addRelation(QuoteCategoryPollutant quoteCategoryPollutant, List<Long> ids) {
        for (Long id : ids) {
            QuoteCategoryPollutant pollutant = new QuoteCategoryPollutant();
            pollutant.setMasterCategoryId(quoteCategoryPollutant.getMasterCategoryId());
            pollutant.setSubCategoryId(quoteCategoryPollutant.getSubCategoryId());
            pollutant.setPollutantTypeId(quoteCategoryPollutant.getPollutantTypeId());
            pollutant.setPollutantId(id);
            List<QuoteCategoryPollutant> quoteCategoryPollutantList = quoteCategoryPollutantMapper.selectQuoteCategoryPollutantList(pollutant);
            if (CollUtil.isNotEmpty(quoteCategoryPollutantList)) {
                throw new RuntimeException("该关联关系已存在！");
            }
            pollutant.setProjectId(quoteCategoryPollutant.getProjectId());
            pollutant.setCreator(SystemUtil.getUserNameCn());
            pollutant.setCreateTime(new Date());
            if (quoteCategoryPollutantMapper.insertQuoteCategoryPollutant(pollutant) == 0) {
                throw new RuntimeException("插入关联信息失败！");
            }
        }
        return true;
    }

    /**
     * 删除关联污染物
     *
     * @param quoteCategoryPollutant 行业大类id、行业子类id、检测类型id、污染物id
     * @return result
     */
    @Override
    public boolean deleteRelationPollutant(QuoteCategoryPollutant quoteCategoryPollutant) {
        return quoteCategoryPollutantMapper.deleteRelationPollutant(quoteCategoryPollutant) != 0;
    }

    /**
     * 删除检测类型
     *
     * @param quoteCategoryPollutantList 行业大类id、行业子类id、检测类型id
     * @return result
     */
    @Override
    public boolean deleteTestType(List<QuoteCategoryPollutant> quoteCategoryPollutantList) {
        for (QuoteCategoryPollutant quoteCategoryPollutant : quoteCategoryPollutantList) {
            Long masterCategoryId = quoteCategoryPollutant.getMasterCategoryId();
            if (null == masterCategoryId) {
                throw new RuntimeException("行业大类id为为空，请检查！");
            }
            Long subCategoryId = quoteCategoryPollutant.getSubCategoryId();
            if (null == subCategoryId) {
                throw new RuntimeException("行业子类id为为空，请检查！");
            }
            Long pollutantTypeId = quoteCategoryPollutant.getPollutantTypeId();
            if (null == pollutantTypeId) {
                throw new RuntimeException("检测类型id为空，请检查！");
            }
            QuoteCategoryPollutant categoryPollutant = new QuoteCategoryPollutant();
            categoryPollutant.setMasterCategoryId(masterCategoryId);
            categoryPollutant.setSubCategoryId(subCategoryId);
            categoryPollutant.setPollutantTypeId(pollutantTypeId);
            if (quoteCategoryPollutantMapper.deleteRelationPollutant(categoryPollutant) == 0) {
                throw new RuntimeException("检测类别信息删除失败！");
            }
        }
        return true;
    }

    /**
     * 获取检测类别列表 （公卫--web端--参数设置）
     *
     * @param quotePollutantTypeVO 行业名称
     * @return 结果
     */
    @Override
    public List<QuotePollutantTypeVO> findPollutantTypeList(QuotePollutantTypeVO quotePollutantTypeVO) {
        return quoteCategoryPollutantMapper.selectPollutantTypeList(quotePollutantTypeVO);
    }

    /**
     * 编辑检测类别
     *
     * @param quotePollutantType 关联主键id、新的检测类型名称
     * @return result
     */
    @Override
    public boolean editPollutantType(QuotePollutantTypeDTO quotePollutantType) {
        // 获取检测类型id
        Long id = quotePollutantType.getId();
        QuoteCategoryPollutant quoteCategoryPollutant = quoteCategoryPollutantMapper.selectQuoteCategoryPollutantById(id);
        Long pollutantTypeId = quoteCategoryPollutant.getPollutantTypeId();
        // 获取检测类型所属项目id
        QuotePollutantType pollutantType = quotePollutantTypeMapper.selectQuotePollutantTypeById(pollutantTypeId);
        // 校验数据库中是否有该名称
        QuotePollutantType qpt = new QuotePollutantType();
        qpt.setId(pollutantTypeId);
        qpt.setPollutantName(quotePollutantType.getPollutantName());
        qpt.setProjectId(pollutantType.getProjectId());
        List<QuotePollutantType> quotePollutantTypeList = quotePollutantTypeMapper.selectQuotePollutantTypeListForEdit(qpt);
        if (CollUtil.isNotEmpty(quotePollutantTypeList)) {
            throw new RuntimeException("要更改的检测类型名称[" + quotePollutantType.getPollutantName() + "]已存在！");
        }
        qpt.setId(pollutantTypeId);
        List<QuoteTestNature> quoteTestNatureList = quotePollutantType.getQuoteTestNatureList();
        if (quotePollutantTypeMapper.updateQuotePollutantType(qpt) != 0) {
            if (CollUtil.isNotEmpty(quoteTestNatureList)) {
                Map<String, String> map = QuoteUtil.getNatureNamesAndIds(quoteTestNatureList);
                QuoteCategoryPollutant categoryPollutant = new QuoteCategoryPollutant();
                categoryPollutant.setId(id);
                categoryPollutant.setNatureName(map.get(QuoteUtil.TEST_NATURE_NAMES));
                categoryPollutant.setNatureIds(map.get(QuoteUtil.TEST_NATURE_IDS));
                if (quoteCategoryPollutantMapper.updateQuoteCategoryPollutant(categoryPollutant) == 0) {
                    throw new RuntimeException("更新检测类型名称及id失败");
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 批量删除行业类别污染物类别关联(关联删除关联的污染物)
     *
     * @param ids 需要删除的行业类别污染物类别关联主键集合
     * @return 结果
     */
    @Override
    public boolean deleteCategoryPollutantByIds(Long[] ids) {
        for (Long id : ids) {
            QuoteCategoryPollutant quoteCategoryPollutant = quoteCategoryPollutantMapper.selectQuoteCategoryPollutantById(id);
            if (null == quoteCategoryPollutant || null == quoteCategoryPollutant.getPollutantTypeId()) {
                throw new RuntimeException("关联信息错误！");
            }
            List<QuoteTestPollutant> list = quoteTestPollutantMapper.selectTestPollutantById(id);
            if (null != list && list.size() != 0) {
                if (quoteTestPollutantMapper.deleteQuoteTestPollutantById(id) == 0) {
                    throw new RuntimeException("删除失败！");
                }
            }
        }
        if (quoteCategoryPollutantMapper.deleteQuoteCategoryPollutantByIds(ids) == 0) {
            throw new RuntimeException("删除检测类别关联关系失败！");
        }
        return true;
    }

    /**
     * 查询是否已存在关联关系（数据导入用）
     *
     * @param quoteCategoryPollutant 相关id
     * @return result
     */
    @Override
    public QuoteCategoryPollutant findQuoteCategoryPollutant(QuoteCategoryPollutant quoteCategoryPollutant) {
        return quoteCategoryPollutantMapper.selectQuoteCategoryPollutant(quoteCategoryPollutant);
    }

    /**
     * 获取检测类别列表
     *
     * @param quoteCategoryPollutant 主类id 检测性质id
     * @return result
     */
    @Override
    public List<QuotePollutantType> findRelationPollutantTypeGw(QuoteCategoryPollutant quoteCategoryPollutant) {
        QuoteCategoryPollutant categoryPollutant = new QuoteCategoryPollutant();
        categoryPollutant.setMasterCategoryId(quoteCategoryPollutant.getMasterCategoryId());
        categoryPollutant.setNatureIds(quoteCategoryPollutant.getNatureIds());
        return quoteCategoryPollutantMapper.selectRelationPollutantTypeGw(categoryPollutant);
    }
}
