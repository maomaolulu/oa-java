package com.ruoyi.quote.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.quote.domain.dto.QuoteBaseFactorDTO;
import com.ruoyi.quote.domain.entity.QuoteSheetItems;
import com.ruoyi.quote.domain.vo.QuoteBaseFactorVO;
import com.ruoyi.quote.domain.vo.QuoteHarmFactorVO;
import com.ruoyi.quote.mapper.QuoteBaseFactorMapper;
import com.ruoyi.quote.mapper.QuoteSheetItemsMapper;
import com.ruoyi.quote.utils.QuoteUtil;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.mapper.QuoteHarmFactorMapper;
import com.ruoyi.quote.domain.entity.QuoteHarmFactor;
import com.ruoyi.quote.service.IQuoteHarmFactorService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 危害因素Service业务层处理
 *
 * @author yrb
 * @date 2022-04-27
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class QuoteHarmFactorServiceImpl implements IQuoteHarmFactorService {
    private final QuoteHarmFactorMapper quoteHarmFactorMapper;
    private final RedisUtils redisUtils;
    private final QuoteBaseFactorMapper quoteBaseFactorMapper;
    private final QuoteSheetItemsMapper quoteSheetItemsMapper;

    @Autowired
    public QuoteHarmFactorServiceImpl(QuoteHarmFactorMapper quoteHarmFactorMapper,
                                      RedisUtils redisUtils,
                                      QuoteBaseFactorMapper quoteBaseFactorMapper,
                                      QuoteSheetItemsMapper quoteSheetItemsMapper) {
        this.quoteHarmFactorMapper = quoteHarmFactorMapper;
        this.redisUtils = redisUtils;
        this.quoteBaseFactorMapper = quoteBaseFactorMapper;
        this.quoteSheetItemsMapper = quoteSheetItemsMapper;
    }

    /**
     * 查询危害因素
     *
     * @param id 危害因素主键
     * @return 危害因素
     */
    @Override
    public QuoteHarmFactor selectQuoteHarmFactorById(Long id) {
        return quoteHarmFactorMapper.selectQuoteHarmFactorById(id);
    }

    /**
     * 查询危害因素列表
     *
     * @param quoteBaseFactorVO 危害因素
     * @return 危害因素
     */
    @Override
    public List<QuoteBaseFactorVO> selectQuoteHarmFactorList(QuoteBaseFactorVO quoteBaseFactorVO) {
        return quoteHarmFactorMapper.selectQuoteHarmFactorRelationBaseFactorList(quoteBaseFactorVO);
    }

    /**
     * 查询危害因素列表
     *
     * @param quoteHarmFactor 危害因素
     * @return 危害因素集合
     */
    @Override
    public List<QuoteHarmFactorVO> selectQuoteHarmFactorUserList(QuoteHarmFactor quoteHarmFactor) {
        List<QuoteHarmFactorVO> quoteHarmFactorList = quoteHarmFactorMapper.selectQuoteHarmFactorUserList(quoteHarmFactor);
        if (CollUtil.isNotEmpty(quoteHarmFactorList)) {
            for (QuoteHarmFactorVO item : quoteHarmFactorList) {
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
        return quoteHarmFactorList;
    }

    /**
     * 新增危害因素
     *
     * @param quoteHarmFactor 危害因素
     * @return 结果
     */
    @Override
    public int insertQuoteHarmFactor(QuoteHarmFactor quoteHarmFactor) {
        quoteHarmFactor.setCreateTime(DateUtils.getNowDate());
        return quoteHarmFactorMapper.insertQuoteHarmFactor(quoteHarmFactor);
    }

    /**
     * 修改危害因素
     *
     * @param quoteHarmFactor 危害因素
     * @return 结果
     */
    @Override
    public int updateQuoteHarmFactor(QuoteHarmFactor quoteHarmFactor) {
        quoteHarmFactor.setUpdateTime(DateUtils.getNowDate());
        return quoteHarmFactorMapper.updateQuoteHarmFactor(quoteHarmFactor);
    }

    /**
     * 批量删除危害因素
     *
     * @param ids 需要删除的危害因素主键
     * @return 结果
     */
    @Override
    public int deleteQuoteHarmFactorByIds(Long[] ids) {
        return quoteHarmFactorMapper.deleteQuoteHarmFactorByIds(ids);
    }

    /**
     * 删除危害因素信息
     *
     * @param id 危害因素主键
     * @return 结果
     */
    @Override
    public int deleteQuoteHarmFactorById(Long id) {
        return quoteHarmFactorMapper.deleteQuoteHarmFactorById(id);
    }

    /**
     * 查询危害因素列表 通过岗位id
     *
     * @param quoteBaseFactorVO 岗位id 已选危害因素id集合
     * @return 危害因素集合
     */
    @Override
    public List<QuoteBaseFactorVO> findQuoteHarmFactorAppList(QuoteBaseFactorVO quoteBaseFactorVO) {
        // 通过岗位id 获取未选择的危害因素列表
        String checkedIdsKey = QuoteUtil.getZwCheckedIdsKey(quoteBaseFactorVO.getSheetId(), quoteBaseFactorVO.getSubId(), quoteBaseFactorVO.getPostId(), SystemUtil.getUserId());
        String ids = redisUtils.get(checkedIdsKey);
        if (StrUtil.isNotBlank(ids)) {
            List<Long> list = QuoteUtil.getList(ids);
            quoteBaseFactorVO.setList(list);
        }
        List<QuoteBaseFactorVO> quoteBaseFactorVOList = quoteHarmFactorMapper.selectQuoteHarmFactorRelationBaseFactorList(quoteBaseFactorVO);
        if (CollUtil.isNotEmpty(quoteBaseFactorVOList)) {
            for (QuoteBaseFactorVO baseFactorVO : quoteBaseFactorVOList) {
                baseFactorVO.setRelationFlag(QuoteUtil.RELATION_FLAG);
            }
        }
        return quoteBaseFactorVOList;
    }

    /**
     * 查询危害因素列表 置顶列表
     *
     * @param quoteHarmFactorVO 表单id 子类id
     * @return 危害因素集合
     */
    @Override
    public List<QuoteBaseFactorVO> findQuoteHarmFactorAppTopList(QuoteHarmFactorVO quoteHarmFactorVO) {
        // 获取已选择的危害因素列表
        String testListKey = QuoteUtil.getZwTestListKey(quoteHarmFactorVO.getSheetId(), quoteHarmFactorVO.getSubId(), quoteHarmFactorVO.getPostId(), SystemUtil.getUserId());
        String listStr = redisUtils.get(testListKey);
        if (StrUtil.isNotBlank(listStr)) {
            return JSONObject.parseArray(listStr, QuoteBaseFactorVO.class);
        }
        return new ArrayList<>();
    }

    /**
     * 查询危害因素列表 置顶列表 所有
     *
     * @param quoteHarmFactorVO 表单id 子类id
     * @return 危害因素集合
     */
    @Override
    public List<QuoteBaseFactorVO> findQuoteHarmFactorAppTopBaseList(QuoteHarmFactorVO quoteHarmFactorVO) {
        // 获取已选择的危害因素列表
        String baseTestListKey = QuoteUtil.getZwBaseTestListKey(quoteHarmFactorVO.getSheetId(), quoteHarmFactorVO.getSubId(), quoteHarmFactorVO.getPostId(), SystemUtil.getUserId());
        String baseListStr = redisUtils.get(baseTestListKey);
        if (StrUtil.isNotBlank(baseListStr)) {
            return JSONObject.parseArray(baseListStr, QuoteBaseFactorVO.class);
        }
        return new ArrayList<>();
    }

    /**
     * 查询危害因素列表 置顶列表 所有
     *
     * @param quoteBaseFactorVO 表单id 子类id
     * @return 危害因素集合
     */
    @Override
    public List<QuoteBaseFactorVO> findQuoteHarmFactorAppBaseList(QuoteBaseFactorVO quoteBaseFactorVO) {
        String sheetId = quoteBaseFactorVO.getSheetId();
        Long subId = quoteBaseFactorVO.getSubId();
        Long postId = quoteBaseFactorVO.getPostId();
        Long userId = SystemUtil.getUserId();
        String postIdsKey = QuoteUtil.getZwPostIdsKey(sheetId, subId, postId, userId);
        String postIds = redisUtils.get(postIdsKey);
        if (StrUtil.isBlank(postIds)) {
            QuoteBaseFactorVO baseFactorVO = new QuoteBaseFactorVO();
            baseFactorVO.setPostId(quoteBaseFactorVO.getPostId());
            List<QuoteBaseFactorVO> quoteBaseFactorVOList = quoteHarmFactorMapper.selectQuoteHarmFactorRelationBaseFactorList(baseFactorVO);
            List<Long> list = new ArrayList<>();
            if (quoteBaseFactorVOList != null && quoteBaseFactorVOList.size() != 0) {
                for (QuoteBaseFactorVO qbf : quoteBaseFactorVOList) {
                    list.add(qbf.getId());
                }
                postIds = QuoteUtil.getFormatString(list, ",");
                if (StrUtil.isNotBlank(postIds)) {
                    redisUtils.set(postIdsKey, postIds);
                }
            }
        }
        String checkedIdsKey = QuoteUtil.getZwCheckedIdsKey(sheetId, subId, postId, userId);
        String checkedIds = redisUtils.get(checkedIdsKey);
        Set<Long> set = new HashSet<>();
        if (StrUtil.isNotBlank(checkedIds)) {
            String[] checked = checkedIds.split(",");
            for (String s : checked) {
                set.add(new Long(s));
            }
        }
        if (StrUtil.isNotBlank(postIds)) {
            String[] post = postIds.split(",");
            for (String s : post) {
                set.add(new Long(s));
            }
        }
        QuoteBaseFactorVO baseFactorVO = new QuoteBaseFactorVO();
        baseFactorVO.setCategoryId(QuoteUtil.ZW_CATEGORY_ID);
        baseFactorVO.setFactorName(quoteBaseFactorVO.getFactorName());
        if (set.size() != 0) {
            List<Long> list = new ArrayList<>(set);
            baseFactorVO.setList(list);
        }
        return quoteBaseFactorMapper.selectQuoteBaseFactorZwRelationList(baseFactorVO);
    }

    /**
     * 更新缓存
     *
     * @param quoteBaseFactorDTO 危害因素信息
     * @return result
     */
    @Override
    public boolean updateCache(QuoteBaseFactorDTO quoteBaseFactorDTO) {
        String sheetId = quoteBaseFactorDTO.getSheetId();
        Long subId = quoteBaseFactorDTO.getSubId();
        Long postId = quoteBaseFactorDTO.getPostId();
        Long userId = SystemUtil.getUserId();
        String testListKey = QuoteUtil.getZwTestListKey(sheetId, subId, postId, userId);
        String baseTestListKey = QuoteUtil.getZwBaseTestListKey(sheetId, subId, postId, userId);
        String checkedIdsKey = QuoteUtil.getZwCheckedIdsKey(sheetId, subId, postId, userId);
        // 根据岗位获取危害因素页面 置顶信息缓存
        List<QuoteBaseFactorVO> baseFactorVOList = quoteBaseFactorDTO.getQuoteBaseFactorVOList();
        if (CollUtil.isNotEmpty(baseFactorVOList)) {
            String listStr = JSONObject.toJSON(baseFactorVOList).toString();
            if (StrUtil.isBlank(listStr)) {
                throw new RuntimeException("获取json字符串失败");
            }
            redisUtils.set(testListKey, listStr);
            List<QuoteBaseFactorVO> baseTestList = new ArrayList<>();
            List<Long> idsList = new ArrayList<>();
            for (QuoteBaseFactorVO quoteBaseFactorVO : baseFactorVOList) {
                idsList.add(quoteBaseFactorVO.getId());
                if (quoteBaseFactorVO.getRelationFlag() == null || quoteBaseFactorVO.getRelationFlag() != 1) {
                    baseTestList.add(quoteBaseFactorVO);
                }
            }
            if (baseTestList.size() != 0) {
                String baseListStr = JSONObject.toJSON(baseTestList).toString();
                redisUtils.set(baseTestListKey, baseListStr);
            }
            String checkedIds = QuoteUtil.getFormatString(idsList, ",");
            redisUtils.set(checkedIdsKey, checkedIds);
        } else {
            redisUtils.delete(testListKey);
            redisUtils.delete(baseTestListKey);
            redisUtils.delete(checkedIdsKey);
        }
        return true;
    }

    /**
     * 从危害因素基础信息中选择未关联岗位的危害因素
     *
     * @param quoteBaseFactorDTO
     * @return result
     */
    @Override
    public boolean addFactorFromBaseHarmFactor(QuoteBaseFactorDTO quoteBaseFactorDTO) {
        String sheetId = quoteBaseFactorDTO.getSheetId();
        Long subId = quoteBaseFactorDTO.getSubId();
        Long postId = quoteBaseFactorDTO.getPostId();
        Long userId = SystemUtil.getUserId();
        // 替换原有的基础信息报价项
        String baseTestListKey = QuoteUtil.getZwBaseTestListKey(sheetId, subId, postId, userId);
        redisUtils.delete(baseTestListKey);
        List<QuoteBaseFactorVO> quoteBaseFactorVOList = quoteBaseFactorDTO.getQuoteBaseFactorVOList();
        if (CollUtil.isNotEmpty(quoteBaseFactorVOList)) {
            String baseListStr = JSONObject.toJSON(quoteBaseFactorVOList).toString();
            redisUtils.set(baseTestListKey, baseListStr);
            String baseTestListTempKey = QuoteUtil.getZwBaseTestListTempKey(sheetId, subId, postId, userId);
            redisUtils.set(baseTestListTempKey, baseListStr);
        } else {
            quoteBaseFactorVOList = new ArrayList<>();
        }
        // 更新所有已选的报价项
        String testListKey = QuoteUtil.getZwTestListKey(sheetId, subId, postId, userId);
        String testList = redisUtils.get(testListKey);
        if (StrUtil.isNotBlank(testList)) {
            List<QuoteBaseFactorVO> baseFactorVOList = JSONObject.parseArray(testList, QuoteBaseFactorVO.class);
            for (QuoteBaseFactorVO quoteBaseFactorVO : baseFactorVOList) {
                if (quoteBaseFactorVO.getRelationFlag() != null && quoteBaseFactorVO.getRelationFlag() == 1) {
                    quoteBaseFactorVOList.add(quoteBaseFactorVO);
                }
            }
        }
        if (quoteBaseFactorVOList.size() != 0) {
            String listStr = JSONObject.toJSON(quoteBaseFactorVOList).toString();
            redisUtils.set(testListKey, listStr);
            String testListTempKey = QuoteUtil.getZwTestListTempKey(sheetId, subId, postId, userId);
            redisUtils.set(testListTempKey, listStr);
        }
        // 更新所有已选的报价项id集合
        String checkedIdsKey = QuoteUtil.getZwCheckedIdsKey(sheetId, subId, postId, userId);
        redisUtils.delete(checkedIdsKey);
        if (quoteBaseFactorVOList.size() != 0) {
            List<Long> list = new ArrayList<>();
            for (QuoteBaseFactorVO quoteBaseFactorVO : quoteBaseFactorVOList) {
                list.add(quoteBaseFactorVO.getId());
            }
            String checkedIds = QuoteUtil.getFormatString(list, ",");
            redisUtils.set(checkedIdsKey, checkedIds);
            String checkedIdsTempKey = QuoteUtil.getZwCheckedIdsTempKey(sheetId, subId, postId, userId);
            redisUtils.set(checkedIdsTempKey, checkedIds);
        }
        return true;
    }

    /**
     * 获取点位数
     *
     * @param quoteSheetItems 表单id、子类id、岗位id
     * @return result
     */
    @Override
    public BigDecimal findPointNumber(QuoteSheetItems quoteSheetItems) {
        List<QuoteSheetItems> quoteSheetItemsList = quoteSheetItemsMapper.selectQuoteSheetItemsList(quoteSheetItems);
        if (CollUtil.isNotEmpty(quoteSheetItemsList)) {
            return quoteSheetItemsList.get(0).getPointNumber();
        }
        return BigDecimal.ONE;
    }

    /**
     * 恢复缓存 职卫报价
     *
     * @param quoteHarmFactorVO 表单id 子类id
     * @return 危害因素集合
     */
    @Override
    public boolean recoverCache(QuoteHarmFactorVO quoteHarmFactorVO) {
        // 获取表单id、子类id、用户id
        String sheetId = quoteHarmFactorVO.getSheetId();
        Long subId = quoteHarmFactorVO.getSubId();
        Long postId = quoteHarmFactorVO.getPostId();
        Long userId = SystemUtil.getUserId();
        // 获取全部已选检测项目列表key、基本信息检测项目列表key、检测项目列表id集合key
        String testListKey = QuoteUtil.getZwTestListKey(sheetId, subId, postId, userId);
        String baseTestListKey = QuoteUtil.getZwBaseTestListKey(sheetId, subId, postId, userId);
        String checkedIdsKey = QuoteUtil.getZwCheckedIdsKey(sheetId, subId, postId, userId);
        // 删除新添加的缓存 设置原有的缓存
        String testListTempKey = QuoteUtil.getZwTestListTempKey(sheetId, subId, postId, userId);
        String testListTempStr = redisUtils.get(testListTempKey);
        redisUtils.set(testListKey, testListTempStr);
        // 基础危害因素缓存
        String baseTestListTempKey = QuoteUtil.getZwBaseTestListTempKey(sheetId, subId, postId, userId);
        String baseTestListTempStr = redisUtils.get(baseTestListTempKey);
        redisUtils.set(baseTestListKey, baseTestListTempStr);
        // 已选择所有检测项目id集合
        String checkedIdsTempKey = QuoteUtil.getZwCheckedIdsTempKey(sheetId, subId, postId, userId);
        String checkedIdsTempStr = redisUtils.get(checkedIdsTempKey);
        redisUtils.set(checkedIdsKey, checkedIdsTempStr);
        return true;
    }
}
