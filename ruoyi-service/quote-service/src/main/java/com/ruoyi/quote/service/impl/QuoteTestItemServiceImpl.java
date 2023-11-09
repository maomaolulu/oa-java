package com.ruoyi.quote.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.quote.domain.dto.QuoteRelationFactorDTO;
import com.ruoyi.quote.domain.dto.QuoteTestInfoDTO;
import com.ruoyi.quote.domain.dto.QuoteTestItemAddDTO;
import com.ruoyi.quote.domain.dto.QuoteTestItemDTO;
import com.ruoyi.quote.domain.entity.*;
import com.ruoyi.quote.domain.vo.*;
import com.ruoyi.quote.mapper.*;
import com.ruoyi.quote.utils.QuoteUtil;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.service.IQuoteTestItemService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * (环境)子类检测项目Service业务层处理
 *
 * @author yrb
 * @date 2022-06-16
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class QuoteTestItemServiceImpl implements IQuoteTestItemService {
    private final QuoteTestItemMapper quoteTestItemMapper;
    private final QuotePointInfoMapper quotePointInfoMapper;
    private final QuotePollutantTypeMapper quotePollutantTypeMapper;
    private final RedisUtils redisUtils;
    private final QuoteSheetItemsMapper quoteSheetItemsMapper;
    private final QuoteExpenseDetailsMapper quoteExpenseDetailsMapper;
    private final QuoteTestNatureMapper quoteTestNatureMapper;
    private final QuoteCategoryPollutantMapper quoteCategoryPollutantMapper;
    private final QuoteIndustryInfoMapper quoteIndustryInfoMapper;

    @Autowired
    public QuoteTestItemServiceImpl(QuoteTestItemMapper quoteTestItemMapper,
                                    QuotePointInfoMapper quotePointInfoMapper,
                                    QuotePollutantTypeMapper quotePollutantTypeMapper,
                                    RedisUtils redisUtils,
                                    QuoteSheetItemsMapper quoteSheetItemsMapper,
                                    QuoteExpenseDetailsMapper quoteExpenseDetailsMapper,
                                    QuoteTestNatureMapper quoteTestNatureMapper,
                                    QuoteCategoryPollutantMapper quoteCategoryPollutantMapper,
                                    QuoteIndustryInfoMapper quoteIndustryInfoMapper) {
        this.quoteTestItemMapper = quoteTestItemMapper;
        this.quotePointInfoMapper = quotePointInfoMapper;
        this.quotePollutantTypeMapper = quotePollutantTypeMapper;
        this.redisUtils = redisUtils;
        this.quoteSheetItemsMapper = quoteSheetItemsMapper;
        this.quoteExpenseDetailsMapper = quoteExpenseDetailsMapper;
        this.quoteTestNatureMapper = quoteTestNatureMapper;
        this.quoteCategoryPollutantMapper = quoteCategoryPollutantMapper;
        this.quoteIndustryInfoMapper = quoteIndustryInfoMapper;
    }

    /**
     * 查询(环境)子类检测项目
     *
     * @param id (环境)子类检测项目主键
     * @return (环境)子类检测项目
     */
    @Override
    public QuoteTestItem selectQuoteTestItemById(Long id) {
        return quoteTestItemMapper.selectQuoteTestItemById(id);
    }

    /**
     * 查询(环境)子类检测项目列表
     *
     * @param quoteTestItem (环境)子类检测项目
     * @return (环境)子类检测项目
     */
    @Override
    public List<QuoteTestItem> selectQuoteTestItemList(QuoteTestItem quoteTestItem) {
        return quoteTestItemMapper.selectQuoteTestItemList(quoteTestItem);
    }

    /**
     * 新增(环境)子类检测项目
     *
     * @param quoteTestItem (环境)子类检测项目
     * @return 结果
     */
    @Override
    public int insertQuoteTestItem(QuoteTestItem quoteTestItem) {
        quoteTestItem.setCreateTime(DateUtils.getNowDate());
        return quoteTestItemMapper.insertQuoteTestItem(quoteTestItem);
    }

    /**
     * 修改(环境)子类检测项目
     *
     * @param quoteTestItem (环境)子类检测项目
     * @return 结果
     */
    @Override
    public int updateQuoteTestItem(QuoteTestItem quoteTestItem) {
        quoteTestItem.setUpdateTime(DateUtils.getNowDate());
        return quoteTestItemMapper.updateQuoteTestItem(quoteTestItem);
    }

    /**
     * 批量删除(环境)子类检测项目
     *
     * @param ids 需要删除的(环境)子类检测项目主键
     * @return 结果
     */
    @Override
    public int deleteQuoteTestItemByIds(Long[] ids) {
        return quoteTestItemMapper.deleteQuoteTestItemByIds(ids);
    }

    /**
     * 删除(环境)子类检测项目信息
     *
     * @param id (环境)子类检测项目主键
     * @return 结果
     */
    @Override
    public int deleteQuoteTestItemById(Long id) {
        return quoteTestItemMapper.deleteQuoteTestItemById(id);
    }

    /**
     * （环境）批量添加检测项目
     *
     * @param quoteTestItemAddDTO
     * @return 结果
     */
    @Override
    public boolean addBatchTestItem(QuoteTestItemAddDTO quoteTestItemAddDTO) {
        List<Long> pollutantIdList = quoteTestItemAddDTO.getPollutantIdList();
        if (CollUtil.isEmpty(pollutantIdList)) {
            throw new RuntimeException("污染物id不能为空！");
        }
        QuoteTestItem item = quoteTestItemAddDTO.getQuoteTestItem();
        for (Long pollutantId : pollutantIdList) {
            QuoteTestItem quoteTestItem = new QuoteTestItem();
            quoteTestItem.setSheetId(item.getSheetId());
            quoteTestItem.setSubId(item.getSubId());
            quoteTestItem.setPollutantTypeId(item.getPollutantTypeId());
            quoteTestItem.setPointId(item.getPointId());
            quoteTestItem.setPollutantId(pollutantId);
            quoteTestItem.setCreateTime(new Date());
            if (quoteTestItemMapper.insertQuoteTestItem(quoteTestItem) == 0) {
                throw new RuntimeException("数据插入失败！");
            }
        }
        // 缓存污染物id
        String idsKey;
        if (item.getPointId() != null) {
            idsKey = QuoteUtil.getHjTestItemsIdsKey(item.getSheetId(), item.getSubId(), item.getPointId(), SystemUtil.getUserId());
        } else {
            idsKey = QuoteUtil.getGwTestItemsIdsKey(item.getSheetId(), item.getSubId(), item.getPollutantTypeId(), SystemUtil.getUserId());
        }
        String idsValue = redisUtils.get(idsKey);
        if (StrUtil.isBlank(idsValue)) {
            redisUtils.set(idsKey, QuoteUtil.getFormatString(pollutantIdList, ","));
        } else {
            // 追加
            List<Long> list = QuoteUtil.getList(idsValue);
            list.addAll(pollutantIdList);
            redisUtils.set(idsKey, QuoteUtil.getFormatString(list, ","));
        }
        return true;
    }

    /**
     * （环境）批量编辑检测项目
     *
     * @param quoteTestItemDTOList 检测项信息
     * @return 结果
     */
    @Override
    public boolean editBatchTestItem(List<QuoteTestItemDTO> quoteTestItemDTOList) {
        QuoteTestItemDTO quoteTestItemDTO = quoteTestItemDTOList.get(0);
        if (quoteTestItemDTO.getPollutantTypeId() == null) {
            throw new RuntimeException("检测类别id不能为空！");
        }
        // 获取污染物和总价
        Set<String> pollutantSet = new HashSet<>();
        BigDecimal sum = new BigDecimal("0.0");
        Map<Long, String> map = new HashMap<>();
        for (QuoteTestItemDTO testItem : quoteTestItemDTOList) {
            testItem.setFactorName(testItem.getPollutantName());
            testItem.setUpdateTime(new Date());
            Long pointId = testItem.getPointId();
            if (pointId != null) {
                String pn = map.get(pointId);
                if (StrUtil.isBlank(pn)) {
                    QuotePointInfo pointInfo = quotePointInfoMapper.selectQuotePointInfoById(pointId);
                    String pointName = pointInfo.getPointName();
                    testItem.setPointName(pointName);
                    map.put(pointId, pointName);
                } else {
                    testItem.setPointName(pn);
                }
            }
            if (quoteTestItemMapper.updateQuoteTestItem(testItem) == 0) {
                throw new RuntimeException("更新检测项目信息失败！");
            }
            pollutantSet.add(testItem.getPollutantName());
            sum = sum.add(testItem.getTotalPrice());
        }
        if (map.size() <= 1) {
            String pollutantName;
            if (pollutantSet.size() > 3) {
                List<String> list = new ArrayList<>(pollutantSet);
                pollutantName = QuoteUtil.getFormatString(list.subList(0, 3),"、");
            } else {
                pollutantName = QuoteUtil.getFormatString(pollutantSet,"、");
            }
            QuotePointInfo pointInfo = new QuotePointInfo();
            pointInfo.setSheetId(quoteTestItemDTO.getSheetId());
            pointInfo.setSubId(quoteTestItemDTO.getSubId());
            pointInfo.setPollutantTypeId(quoteTestItemDTO.getPollutantTypeId());
            pointInfo.setTotalPrice(sum);
            pointInfo.setPollutantName(pollutantName);
            if (null != quoteTestItemDTO.getPointId()) {
                Long pointId = quoteTestItemDTO.getPointId();
                pointInfo.setId(pointId);
                pointInfo.setUpdateTime(new Date());
                if (quotePointInfoMapper.updateQuotePointInfo(pointInfo) == 0) {
                    throw new RuntimeException("点位检测记录插入失败！");
                }
            } else {
                QuotePollutantType quotePollutantType = quotePollutantTypeMapper.selectQuotePollutantTypeById(quoteTestItemDTO.getPollutantTypeId());
                pointInfo.setPointName(quotePollutantType.getPollutantName());
                pointInfo.setCreateTime(new Date());
                if (quotePointInfoMapper.insertQuotePointInfo(pointInfo) == 0) {
                    throw new RuntimeException("点位检测记录插入失败！");
                }
            }
        }
        return true;
    }

    /**
     * 通过sheetId删除数据
     *
     * @param quoteTestItem
     * @return 结果
     */
    @Override
    public boolean updateTempFlag(QuoteTestItem quoteTestItem) {
        return quoteTestItemMapper.updateTempFlag(quoteTestItem) != 0;
    }

    /**
     * 删除用户取消操作的数据
     *
     * @param quoteTestItem 表单id、子类id、点位名称
     * @return 结果
     */
    @Override
    public boolean deleteQuoteTestItemAddTemp(QuoteTestItem quoteTestItem) {
        QuoteTestItem item = new QuoteTestItem();
        item.setPointId(quoteTestItem.getPointId());
        List<QuoteTestItem> quoteTestItemList = quoteTestItemMapper.selectQuoteTestItemList(item);
        if (CollUtil.isEmpty(quoteTestItemList)) {
            throw new RuntimeException("要删除的数据不存在！");
        }
        return quoteTestItemMapper.deleteQuoteTestItemAddTemp(quoteTestItem) != 0;
    }

    /**
     * 根据表单id、子类id、点位id获取检测细项列表
     *
     * @param quoteTestItem 表单id、子类id、点位id或检测类别id
     * @return 结果
     */
    @Override
    public List<QuoteTestItemDetailsVO> findTestItemByPointId(QuoteTestItem quoteTestItem) {
        return quoteTestItemMapper.selectTestItemByPointId(quoteTestItem);
    }

    /**
     * 获取子类已报价的污染物id
     *
     * @param quoteRelationFactorDTO 表单id、子类id、检测类别id
     * @return result
     */
    @Override
    public List<Long> findPollutantIdListBySub(QuoteRelationFactorDTO quoteRelationFactorDTO) {
        QuoteTestItem quoteTestItem = new QuoteTestItem();
        quoteTestItem.setSheetId(quoteRelationFactorDTO.getSheetId());
        quoteTestItem.setSubId(quoteRelationFactorDTO.getSubId());
        quoteTestItem.setPollutantTypeId(quoteRelationFactorDTO.getPollutantTypeId());
        return quoteTestItemMapper.selectPollutantIdListBySub(quoteTestItem);
    }

    /**
     * 获取子类已报价的检测类别id
     *
     * @param quoteTestItem 表单id、子类id
     * @return result
     */
    @Override
    public List<Long> findPollutantTypeIdListBySub(QuoteTestItem quoteTestItem) {
        QuoteTestItem item = new QuoteTestItem();
        item.setSheetId(quoteTestItem.getSheetId());
        item.setSubId(quoteTestItem.getSubId());
        return quoteTestItemMapper.selectPollutantTypeIdListBySub(item);
    }

    /**
     * 环境、公卫-web端-编辑-获取数据
     *
     * @param quoteTestItem 表单id、子类id、点位id
     * @return 结果
     */
    @Override
    public QuoteTestItemEditVO findTestItemEdit(QuoteTestItem quoteTestItem) {
        QuoteTestItem testItem = new QuoteTestItem();
        testItem.setSheetId(quoteTestItem.getSheetId());
        testItem.setSubId(quoteTestItem.getSubId());
        if (quoteTestItem.getPointId() != null) {
            testItem.setPointId(quoteTestItem.getPointId());
        } else {
            testItem.setPollutantTypeId(quoteTestItem.getPollutantTypeId());
        }
        List<QuoteTestItemDetailsVO> list = quoteTestItemMapper.selectTestItemByPointId(testItem);
        if (CollUtil.isEmpty(list)) {
            throw new RuntimeException("报价数据获取失败");
        }
        QuoteTestItemDetailsVO itemDetailsVO = list.get(0);
        QuotePollutantType quotePollutantType = quotePollutantTypeMapper.selectQuotePollutantTypeById(itemDetailsVO.getPollutantTypeId());
        QuoteTestItemEditVO quoteTestItemEditVO = new QuoteTestItemEditVO();
        quoteTestItemEditVO.setQuoteTestItemDetailsVOList(list);
        quoteTestItemEditVO.setQuotePollutantType(quotePollutantType);
        return quoteTestItemEditVO;
    }

    /**
     * 环境、公卫--删除点位和报价信息
     *
     * @param quoteTestItem 报价单id、子类id、点位id、检测类型id
     * @return 结果
     */
    @Override
    public boolean deleteQuoteTestItem(QuoteTestItem quoteTestItem) {
        // 删除点位
        QuotePointInfo pointInfo = new QuotePointInfo();
        pointInfo.setSheetId(quoteTestItem.getSheetId());
        pointInfo.setSubId(quoteTestItem.getSubId());
        pointInfo.setId(quoteTestItem.getPointId());
        pointInfo.setPollutantTypeId(quoteTestItem.getPollutantTypeId());
        if (quotePointInfoMapper.deleteQuotePointInfo(pointInfo) == 0) {
            throw new RuntimeException("点位信息删除失败！");
        }
        // 删除检测项目
        if (quoteTestItemMapper.deleteQuoteTestItemAddTemp(quoteTestItem) == 0) {
            throw new RuntimeException("检测项目信息删除失败！");
        }
        return true;
    }

    /**
     * 通过表单id、子类id、检测类别获取检测项
     *
     * @param quoteTestItem 表单id、子类id、检测类别id
     * @return result
     */
    @Override
    public List<QuoteTestItemTypeVO> findQuoteTestItem(QuoteTestItem quoteTestItem) {
        String sheetId = quoteTestItem.getSheetId();
        Long subId = quoteTestItem.getSubId();
        QuoteTestItem testItem = new QuoteTestItem();
        testItem.setSheetId(sheetId);
        testItem.setSubId(subId);
        List<QuoteTestItemDetailsVO> quoteTestItemDetailsVOList = quoteTestItemMapper.selectTestItemByPointId(testItem);
        List<QuoteTestItemTypeVO> quoteTestItemTypeVOList = new ArrayList<>();
        // 清空原有报价项缓存
        String key = QuoteUtil.getGwOrHjTestListKey(subId, SystemUtil.getUserId(), sheetId);
        redisUtils.delete(key);
        String type = QuoteUtil.getGwPollutantTypeKey(subId, SystemUtil.getUserId(), sheetId);
        redisUtils.delete(type);
        // 缓存原有的数据
        List<QuoteTestItem> quoteTestItemList = quoteTestItemMapper.selectQuoteTestItemList(testItem);
        if (CollUtil.isNotEmpty(quoteTestItemList)) {
            // 设置缓存
            QuoteTestItem item = quoteTestItemList.get(0);
            Long pointId = item.getPointId();
            Long pollutantTypeId = item.getPollutantTypeId();
            Long userId = SystemUtil.getUserId();
            Map<Long, List<QuoteTestItem>> map = quoteTestItemList.stream().collect(Collectors.groupingBy(QuoteTestItem::getSubId));
            String idsKey;
            for (Long sub : map.keySet()) {
                if (pointId != null) {
                    idsKey = QuoteUtil.getHjTestItemsIdsKey(sheetId, sub, pointId, userId);
                } else {
                    idsKey = QuoteUtil.getGwTestItemsIdsKey(sheetId, sub, pollutantTypeId, userId);
                }
                List<QuoteTestItem> testItemList = map.get(sub);
                List<Long> list = testItemList.stream().map(QuoteTestItem::getId).collect(Collectors.toList());
                redisUtils.set(idsKey, QuoteUtil.getFormatString(list, ","));
            }
            redisUtils.set(key, JSONObject.toJSON(quoteTestItemList).toString());
        }
        if (CollUtil.isNotEmpty(quoteTestItemDetailsVOList)) {
            QuoteTestItemDetailsVO qti = quoteTestItemDetailsVOList.get(0);
            Map<Long, List<QuoteTestItemDetailsVO>> map = new HashMap<>();
            if (qti.getPointId() == null) {
                Set<Long> set = new HashSet<>();
                for (QuoteTestItemDetailsVO item : quoteTestItemDetailsVOList) {
                    Long pollutantTypeId = item.getPollutantTypeId();
                    set.add(pollutantTypeId);
                    List<QuoteTestItemDetailsVO> testItems = map.get(pollutantTypeId);
                    if (testItems == null || testItems.size() == 0) {
                        testItems = new ArrayList<>();
                        testItems.add(item);
                        map.put(pollutantTypeId, testItems);
                    } else {
                        testItems.add(item);
                    }
                }
                List<Long> list = new ArrayList<>(set);
                List<QuotePollutantType> quotePollutantTypeList = quotePollutantTypeMapper.selectPollutantTypeInTestItem(list);
                for (QuotePollutantType quotePollutantType : quotePollutantTypeList) {
                    QuoteTestItemTypeVO quoteTestItemTypeVO = new QuoteTestItemTypeVO();
                    quoteTestItemTypeVO.setQuotePollutantType(quotePollutantType);
                    quoteTestItemTypeVO.setQuoteTestItemDetailsVOList(map.get(quotePollutantType.getId()));
                    quoteTestItemTypeVOList.add(quoteTestItemTypeVO);
                }
                // 检测类型删除原有缓存 设置新的缓存
                String ids = QuoteUtil.getFormatString(list, ",");
                redisUtils.set(type, ids);
            } else {
                Map<Long, QuotePollutantType> pollutantTypeMap = new HashMap<>();
                Map<Long, QuotePointInfo> pointInfoMap = new HashMap<>();
                for (QuoteTestItemDetailsVO item : quoteTestItemDetailsVOList) {
                    Long pointId = item.getPointId();
                    List<QuoteTestItemDetailsVO> testItems = map.get(pointId);
                    if (testItems == null || testItems.size() == 0) {
                        testItems = new ArrayList<>();
                        testItems.add(item);
                        map.put(pointId, testItems);
                        QuotePollutantType quotePollutantType = quotePollutantTypeMapper.selectQuotePollutantTypeById(item.getPollutantTypeId());
                        QuotePointInfo quotePointInfo = quotePointInfoMapper.selectQuotePointInfoById(item.getPointId());
                        pollutantTypeMap.put(pointId, quotePollutantType);
                        pointInfoMap.put(pointId, quotePointInfo);
                    } else {
                        testItems.add(item);
                    }
                }
                for (Long k : map.keySet()) {
                    QuoteTestItemTypeVO quoteTestItemTypeVO = new QuoteTestItemTypeVO();
                    quoteTestItemTypeVO.setQuotePollutantType(pollutantTypeMap.get(k));
                    quoteTestItemTypeVO.setQuotePointInfo(pointInfoMap.get(k));
                    quoteTestItemTypeVO.setQuoteTestItemDetailsVOList(map.get(k));
                    quoteTestItemTypeVOList.add(quoteTestItemTypeVO);
                }
            }
        }
        return quoteTestItemTypeVOList;
    }

    /**
     * 公卫 子类取消报价
     *
     * @param quoteTestItem 表单id、子类id
     * @return result
     */
    @Override
    public boolean cancelOperrationToGW(QuoteTestItem quoteTestItem) {
        // 删除改变后的报价记录
        QuoteTestItem testItem = new QuoteTestItem();
        String sheetId = quoteTestItem.getSheetId();
        testItem.setSheetId(sheetId);
        Long subId = quoteTestItem.getSubId();
        testItem.setSubId(subId);
        List<QuoteTestItem> quoteTestItemList = quoteTestItemMapper.selectQuoteTestItemList(testItem);
        if (CollUtil.isNotEmpty(quoteTestItemList)) {
            // 清除缓存
            QuoteTestItem item = quoteTestItemList.get(0);
            Long pointId = item.getPointId();
            Long pollutantTypeId = item.getPollutantTypeId();
            Long userId = SystemUtil.getUserId();
            Map<Long, List<QuoteTestItem>> map = quoteTestItemList.stream().collect(Collectors.groupingBy(QuoteTestItem::getSubId));
            String idsKey;
            for (Long sub : map.keySet()) {
                if (pointId != null) {
                    idsKey = QuoteUtil.getHjTestItemsIdsKey(sheetId, sub, pointId, userId);
                } else {
                    idsKey = QuoteUtil.getGwTestItemsIdsKey(sheetId, sub, pollutantTypeId, userId);
                }
                redisUtils.delete(idsKey);
            }
            if (quoteTestItemMapper.deleteQuoteTestItemAddTemp(testItem) == 0) {
                throw new RuntimeException("删除原有子类检测项目失败");
            }
        }
        // 恢复原有的报价记录
        String key = QuoteUtil.getGwOrHjTestListKey(subId, SystemUtil.getUserId(), sheetId);
        String record = redisUtils.get(key);
        if (StrUtil.isNotBlank(record)) {
            List<QuoteTestItem> itemList = JSONObject.parseArray(record, QuoteTestItem.class);
            for (QuoteTestItem item : itemList) {
                if (quoteTestItemMapper.updateQuoteTestItem(item) == 0) {
                    throw new RuntimeException("恢复原有检测记录失败");
                }
            }
        }
        return true;
    }

    /**
     * 公卫--删除检测类别
     *
     * @param quoteTestItem 报价单id、子类id、检测类型id
     * @return 结果
     */
    @Override
    public boolean deleteQuoteTestItemToGW(QuoteTestItem quoteTestItem) {
        QuoteTestItem item = new QuoteTestItem();
        String sheetId = quoteTestItem.getSheetId();
        item.setSheetId(sheetId);
        Long subId = quoteTestItem.getSubId();
        item.setSubId(subId);
        Long pollutantTypeId = quoteTestItem.getPollutantTypeId();
        item.setPollutantTypeId(pollutantTypeId);
        Long pointId = quoteTestItem.getPointId();
        item.setPointId(pointId);
        List<QuoteTestItem> quoteTestItemList = quoteTestItemMapper.selectQuoteTestItemList(item);
        if (CollUtil.isNotEmpty(quoteTestItemList)) {
            if (quoteTestItemMapper.deleteQuoteTestItemAddTemp(item) == 0) {
                throw new RuntimeException("当前检测类别关联信息删除失败");
            }
        }
        if (pointId != null) {
            if (quotePointInfoMapper.deleteQuotePointInfoById(pointId) == 0) {
                throw new RuntimeException("删除点位信息失败");
            }
        }
        String key = QuoteUtil.getGwPollutantTypeKey(subId, SystemUtil.getUserId(), sheetId);
        String ids = redisUtils.get(key);
        if (StrUtil.isNotBlank(ids)) {
            List<Long> list = QuoteUtil.getList(ids);
            if (list.size() > 1) {
                list.remove(pollutantTypeId);
                ids = QuoteUtil.getFormatString(list, ",");
                redisUtils.set(key, ids);
            } else {
                redisUtils.delete(key);
            }
        }
        return true;
    }

    /**
     * 职卫、环境、公卫 行业改变 删除原有报价信息
     *
     * @param quoteTestItem 表单id
     * @return result
     */
    @Override
    public boolean deleteQuoteTestItemRelationInfo(QuoteTestItem quoteTestItem) {
        // 此处逻辑 环境咨询的不调用此接口
        String sheetId = quoteTestItem.getSheetId();
        Long userId = SystemUtil.getUserId();
        // 此处表示行业id 若是环境检测则为一级行业id
        String id = quoteTestItem.getId().toString();
        // 此处表示环境二级行业id
        String subId = "";
        if (quoteTestItem.getSubId() != null) {
            subId = quoteTestItem.getSubId().toString();
        }
        // 判断行业是否改变
        String industryIdKey = QuoteUtil.getIndustryIdKey(sheetId, userId);
        String industryId = redisUtils.get(industryIdKey);
        if (StrUtil.isBlank(subId)) {
            // 非环境检测
            if (StrUtil.isBlank(industryId) || StrUtil.equals(id, industryId)) {
                redisUtils.set(industryIdKey, id);
                return true;
            }
            redisUtils.set(industryIdKey, id);
        } else {
            // 环境检测
            String newId = id + "+" + subId;
            if (StrUtil.isBlank(industryId) || StrUtil.equals(newId, industryId)) {
                redisUtils.set(industryIdKey, newId);
                return true;
            }
            redisUtils.set(industryIdKey, newId);
        }

        QuoteTestItem item = new QuoteTestItem();
        item.setSheetId(sheetId);
        QuoteSheetItems sheetItems = new QuoteSheetItems();
        sheetItems.setSheetId(sheetId);
        // 职卫 删除检测项信息
        List<QuoteSheetItems> quoteSheetItems = quoteSheetItemsMapper.selectQuoteSheetItemsList(sheetItems);
        if (CollUtil.isNotEmpty(quoteSheetItems)) {
            if (quoteSheetItemsMapper.deleteQuoteSheetItems(sheetItems) == 0) {
                throw new RuntimeException("删除职卫检测信息失败");
            }
        }
        // 环境、公卫 删除检测项信息
        List<QuoteTestItem> quoteTestItemList = quoteTestItemMapper.selectQuoteTestItemList(quoteTestItem);
        if (CollUtil.isNotEmpty(quoteTestItemList)) {
            if (quoteTestItemMapper.deleteQuoteTestItemAddTemp(item) == 0) {
                throw new RuntimeException("删除环境或公卫检测信息失败");
            }
        }
        // 职卫、环境、公卫 删除报价检测费用明细
        QuoteExpenseDetails quoteExpenseDetails = new QuoteExpenseDetails();
        quoteExpenseDetails.setSheetId(sheetId);
        List<QuoteExpenseDetails> quoteExpenseDetailsList = quoteExpenseDetailsMapper.selectQuoteExpenseDetailsList(quoteExpenseDetails);
        if (CollUtil.isNotEmpty(quoteExpenseDetailsList)) {
            if (quoteExpenseDetailsMapper.deleteQuoteExpenseDetails(quoteExpenseDetails) == 0) {
                throw new RuntimeException("删除检测费用明细信息失败");
            }
        }
        return true;
    }

    /**
     * app端-环境-删除单个检测项目
     *
     * @param quoteTestItem 报价信息
     * @return result
     */
    @Override
    public boolean deleteHjTestItem(QuoteTestItem quoteTestItem) {
        String sheetId = quoteTestItem.getSheetId();
        Long subId = quoteTestItem.getSubId();
        Long pointId = quoteTestItem.getPointId();
        // 更新缓存-从缓存从移除当前污染物
        String idsKey = QuoteUtil.getHjTestItemsIdsKey(sheetId, subId, pointId, SystemUtil.getUserId());
        String idsValue = redisUtils.get(idsKey);
        if (StrUtil.isBlank(idsValue)) {
            throw new RuntimeException("获取环境报价检测项失败");
        }
        List<Long> list = QuoteUtil.getList(idsValue);
        list.remove(quoteTestItem.getPollutantId());
        if (list.size() == 0) {
            redisUtils.delete(idsKey);
        } else {
            redisUtils.set(idsKey, QuoteUtil.getFormatString(list, ","));
        }
        // 删除检测项
        QuoteTestItem item = new QuoteTestItem();
        item.setSheetId(sheetId);
        item.setSubId(subId);
        item.setPointId(pointId);
        item.setId(quoteTestItem.getId());
        if (quoteTestItemMapper.deleteQuoteTestItemAddTemp(item) == 0) {
            redisUtils.set(idsKey, idsValue);
            return false;
        }
        return true;
    }

    /**
     * app端-公卫-删除单个检测项目
     *
     * @param quoteTestItem 报价信息
     * @return result
     */
    @Override
    public boolean deleteGwTestItem(QuoteTestItem quoteTestItem) {
        String sheetId = quoteTestItem.getSheetId();
        Long subId = quoteTestItem.getSubId();
        Long pollutantTypeId = quoteTestItem.getPollutantTypeId();
        Long id = quoteTestItem.getId();
        // 更新缓存-从缓存中移除当前污染物
        String idsKey = QuoteUtil.getGwTestItemsIdsKey(sheetId, subId, pollutantTypeId, SystemUtil.getUserId());
        String idsValue = redisUtils.get(idsKey);
        if (StrUtil.isBlank(idsValue)) {
            throw new RuntimeException("获取环境报价检测项失败");
        }
        List<Long> list = QuoteUtil.getList(idsValue);
        list.remove(id);
        if (list.size() == 0) {
            redisUtils.delete(idsKey);
        } else {
            redisUtils.set(idsKey, QuoteUtil.getFormatString(list, ","));
        }
        // 删除检测项
        QuoteTestItem item = new QuoteTestItem();
        item.setSheetId(sheetId);
        item.setSubId(subId);
        item.setPollutantTypeId(pollutantTypeId);
        item.setId(id);
        if (quoteTestItemMapper.deleteQuoteTestItemAddTemp(item) == 0) {
            redisUtils.set(idsKey, idsValue);
            return false;
        }
        return true;
    }

    /**
     * 定时任务 删除两天前的临时数据
     *
     * @param quoteTestItem 临时数据标记    时间（两天前）
     * @return result
     */
    @Override
    public int deleteTempTestItem(QuoteTestItem quoteTestItem) {
        return quoteTestItemMapper.deleteTempTestItem(quoteTestItem);
    }

    /**
     * 查询已报价的检测信息
     *
     * @param quoteTestItem 表单id 子类id
     * @return result
     */
    @Override
    public QuoteTestItemInfoVO findTestItemInfo(QuoteTestItem quoteTestItem) {
        String sheetId = quoteTestItem.getSheetId();
        Long subId = quoteTestItem.getSubId();
        Long userId = SystemUtil.getUserId();
        // 先从缓存去取检测项,没有再从数据库取
        String gwCgNormalTestItemKey = QuoteUtil.getGwCgNormalTestItemKey(sheetId, subId, userId);
        String gwCgOtherTestItemKey = QuoteUtil.getGwCgOtherTestItemKey(sheetId, subId, userId);
        List<QuoteTestItemDetailsVO> quoteTestItemDetailsVOList = new ArrayList<>();
        String gwCgNormalTestItemList = redisUtils.get(gwCgNormalTestItemKey);
        String gwCgOtherTestItemList = redisUtils.get(gwCgOtherTestItemKey);
        if (StrUtil.isBlank(gwCgNormalTestItemList) && StrUtil.isBlank(gwCgOtherTestItemList)) {
            QuoteTestItem item = new QuoteTestItem();
            item.setSheetId(sheetId);
            item.setSubId(subId);
            quoteTestItemDetailsVOList = quoteTestItemMapper.selectTestItemByPointId(item);
        } else {
            if (StrUtil.isNotBlank(gwCgNormalTestItemList)) {
                quoteTestItemDetailsVOList.addAll(JSON.parseArray(gwCgNormalTestItemList, QuoteTestItemDetailsVO.class));
            }
            if (StrUtil.isNotBlank(gwCgOtherTestItemList)) {
                quoteTestItemDetailsVOList.addAll(JSON.parseArray(gwCgOtherTestItemList, QuoteTestItemDetailsVO.class));
            }
        }
        QuoteTestItemInfoVO quoteTestItemInfoVO = new QuoteTestItemInfoVO();
        // 获取检测性质列表
        String list = redisUtils.get(QuoteUtil.TEST_NATURE_LIST);
        List<QuoteTestNature> quoteTestNatureList;
        if (StrUtil.isBlank(list)) {
            synchronized (QuoteTestNatureServiceImpl.class) {
                if (StrUtil.isBlank(redisUtils.get(QuoteUtil.TEST_NATURE_LIST))) {
                    quoteTestNatureList = quoteTestNatureMapper.selectQuoteTestNatureList(new QuoteTestNature());
                    redisUtils.set(QuoteUtil.TEST_NATURE_LIST, JSON.toJSONString(quoteTestNatureList), 4 * 3600L);
                } else {
                    quoteTestNatureList = JSON.parseArray(redisUtils.get(QuoteUtil.TEST_NATURE_LIST), QuoteTestNature.class);
                }
            }
        } else {
            quoteTestNatureList = JSON.parseArray(list, QuoteTestNature.class);
        }
        if (CollUtil.isNotEmpty(quoteTestItemDetailsVOList)) {
            // 1.标记已选检测性质
            String gwCgTestNatureIdCacheKey = QuoteUtil.getGwCgTestNatureIdCacheKey(sheetId, subId, userId);
            String natureId = redisUtils.get(gwCgTestNatureIdCacheKey);
            if (StrUtil.isBlank(natureId)) {
                throw new RuntimeException("获取检测性质id失败");
            }
            Long testNatureId = new Long(natureId);
            if (CollUtil.isEmpty(quoteTestNatureList)) throw new RuntimeException("检测性质列表为空");
            for (QuoteTestNature quoteTestNature : quoteTestNatureList) {
                if (testNatureId.equals(quoteTestNature.getId())) {
                    quoteTestNature.setChecked(QuoteUtil.CHECKED_FLAG);
                }
            }
            quoteTestItemInfoVO.setQuoteTestNatureList(quoteTestNatureList);
            // 2.标记已选检测类别
            List<Long> pollutantTypeIds = quoteTestItemDetailsVOList.stream().map(QuoteTestItemDetailsVO::getPollutantTypeId).distinct().collect(Collectors.toList());
            if (CollUtil.isEmpty(pollutantTypeIds)) {
                throw new RuntimeException("已报价的检测类别id为空,请联系管理员！");
            }
            QuoteCategoryPollutant categoryPollutant = new QuoteCategoryPollutant();
            // 此处quoteTestItem.getId()表示行业大类id
            categoryPollutant.setMasterCategoryId(quoteTestItem.getId());
            categoryPollutant.setNatureIds(natureId.toString());
            List<QuotePollutantTestTypeVO> quotePollutantTestTypeVOList = quoteCategoryPollutantMapper.selectRelationPollutantTypeCheckedGw(categoryPollutant);
            if (CollUtil.isEmpty(quotePollutantTestTypeVOList)) {
                throw new RuntimeException("主类、检测性质关联检测类别为空，请联系管理员！");
            }
            for (QuotePollutantTestTypeVO quotePollutantTestTypeVO : quotePollutantTestTypeVOList) {
                Long id = quotePollutantTestTypeVO.getId();
                if (pollutantTypeIds.contains(id)) {
                    quotePollutantTestTypeVO.setChecked(QuoteUtil.CHECKED_FLAG);
                }
            }
            // 2.1 设置检测类型
            quoteTestItemInfoVO.setQuotePollutantTestTypeVOList(quotePollutantTestTypeVOList);
            // 3.设置报价项
            quoteTestItemInfoVO.setQuoteTestItemDetailsVOList(quoteTestItemDetailsVOList);
            // 缓存其他检测项
            Map<Long, List<QuoteTestItemDetailsVO>> map = quoteTestItemDetailsVOList.stream().collect(Collectors.groupingBy(QuoteTestItemDetailsVO::getOtherType));
            List<QuoteTestItemDetailsVO> otherTestItemList = map.get(QuoteUtil.OTHER_TEST_ITEM_FLAG);
            if (CollUtil.isNotEmpty(otherTestItemList)) {
                redisUtils.set(gwCgOtherTestItemKey, JSON.toJSONString(otherTestItemList));
            }
        } else {
            quoteTestItemInfoVO.setQuoteTestNatureList(quoteTestNatureList);
            List<QuotePollutantTestTypeVO> quotePollutantTestTypeVOList = new ArrayList<>();
            quoteTestItemInfoVO.setQuotePollutantTestTypeVOList(quotePollutantTestTypeVOList);
            quoteTestItemInfoVO.setQuoteTestItemDetailsVOList(quoteTestItemDetailsVOList);
        }
        return quoteTestItemInfoVO;
    }

    /**
     * 查询关联的检测物质信息
     *
     * @param quoteTestInfoDTO 大类id 检测性质id 检测类别id集合 表单id 子类id
     * @return result
     */
    @Override
    public List<QuoteTestItemDetailsVO> findTestItemInfo(QuoteTestInfoDTO quoteTestInfoDTO) {
        String sheetId = quoteTestInfoDTO.getSheetId();
        Long subId = quoteTestInfoDTO.getSubId();
        QuoteTestInfoDTO dto = new QuoteTestInfoDTO();
        dto.setSheetId(sheetId);
        dto.setSubId(subId);
        dto.setMasterCategoryId(quoteTestInfoDTO.getMasterCategoryId());
        dto.setNatureId(quoteTestInfoDTO.getNatureId());
        dto.setList(quoteTestInfoDTO.getList());
        // 设置检测项目类型
        dto.setOtherType(QuoteUtil.NORMAL_TEST_ITEM_FLAG);
        // 从数据库查找数据
        List<QuoteTestItemDetailsVO> quoteTestItemDetailsVOList = quoteTestItemMapper.selectTestItemInfo(dto);
        // 取缓存
        Long userId = SystemUtil.getUserId();
        String gwCgOtherTestItemKey = QuoteUtil.getGwCgOtherTestItemKey(sheetId, subId, userId);
        String list = redisUtils.get(gwCgOtherTestItemKey);
        if (StrUtil.isNotBlank(list)) {
            quoteTestItemDetailsVOList.addAll(JSON.parseArray(list, QuoteTestItemDetailsVO.class));
        }
        return quoteTestItemDetailsVOList;
    }

    /**
     * 获取已选择的其他检测项  缓存一般检测项
     *
     * @param quoteTestItemDTO 报价项集合
     * @return result
     */
    @Override
    public List<QuoteTestItemDetailsVO> findOtherTestItem(QuoteTestItemDTO quoteTestItemDTO) {
        // 报价单信息
        String sheetId = quoteTestItemDTO.getSheetId();
        Long subId = quoteTestItemDTO.getSubId();
        Long userId = SystemUtil.getUserId();
        // 获取缓存key
        String gwCgOtherTestItemKey = QuoteUtil.getGwCgOtherTestItemKey(sheetId, subId, userId);
        String gwCgNormalTestItemKey = QuoteUtil.getGwCgNormalTestItemKey(sheetId, subId, userId);
        String gwCgOtherTestItemIdsKey = QuoteUtil.getGwCgOtherTestItemIdsKey(sheetId, subId, userId);
        // 获取检测项列表
        List<QuoteTestItemDetailsVO> quoteTestItemDetailsVOList = quoteTestItemDTO.getQuoteTestItemDetailsVOList();
        Map<Long, List<QuoteTestItemDetailsVO>> map = new HashMap<>();
        if (CollUtil.isNotEmpty(quoteTestItemDetailsVOList)) {
            map = quoteTestItemDetailsVOList.stream().collect(Collectors.groupingBy(QuoteTestItemDetailsVO::getOtherType));
        }
        List<QuoteTestItemDetailsVO> otherTestItemList = map.get(QuoteUtil.OTHER_TEST_ITEM_FLAG);
        List<QuoteTestItemDetailsVO> normalTestItemList = map.get(QuoteUtil.NORMAL_TEST_ITEM_FLAG);
        if (CollUtil.isNotEmpty(otherTestItemList)) {
            redisUtils.set(gwCgOtherTestItemKey, JSON.toJSONString(otherTestItemList));
            List<Long> list = otherTestItemList.stream().map(QuoteTestItemDetailsVO::getPollutantId).collect(Collectors.toList());
            if (CollUtil.isEmpty(list)) {
                throw new RuntimeException("检测列表不为空，但是列表中的污染物id为空，请联系管理员！");
            }
            redisUtils.set(gwCgOtherTestItemIdsKey, QuoteUtil.getFormatString(list, ","));
        } else {
            redisUtils.delete(gwCgOtherTestItemIdsKey);
        }
        if (CollUtil.isNotEmpty(normalTestItemList)) {
            redisUtils.set(gwCgNormalTestItemKey, JSON.toJSONString(normalTestItemList));
        } else {
            redisUtils.delete(gwCgNormalTestItemKey);
        }
        if (otherTestItemList == null) {
            otherTestItemList = new ArrayList<>();
        }
        return otherTestItemList;
    }

    /**
     * 提交已选择的其他检测项
     *
     * @param quoteTestItemDTO 表单id 子类id ...
     * @return result
     */
    @Override
    public boolean commitOtherTestItem(QuoteTestItemDTO quoteTestItemDTO) {
        String sheetId = quoteTestItemDTO.getSheetId();
        Long subId = quoteTestItemDTO.getSubId();
        Long userId = SystemUtil.getUserId();
        String gwCgOtherTestItemKey = QuoteUtil.getGwCgOtherTestItemKey(sheetId, subId, userId);
        List<QuoteTestItemDetailsVO> quoteTestItemDetailsVOList = quoteTestItemDTO.getQuoteTestItemDetailsVOList();
        if (CollUtil.isNotEmpty(quoteTestItemDetailsVOList)) {
            redisUtils.set(gwCgOtherTestItemKey, JSON.toJSONString(quoteTestItemDetailsVOList));
        } else {
            redisUtils.delete(gwCgOtherTestItemKey);
        }
        return true;
    }

    /**
     * 删除常规检测项
     *
     * @param quoteTestItemDTO 表单id 子类id 检测项信息
     * @return result
     */
    @Override
    public boolean removeNormalTestItem(QuoteTestItemDTO quoteTestItemDTO) {
        List<QuoteTestItemDetailsVO> quoteTestItemDetailsVOList = quoteTestItemDTO.getQuoteTestItemDetailsVOList();
        QuoteTestItemDetailsVO quoteTestItemDetailsVO = quoteTestItemDetailsVOList.get(0);
        if (quoteTestItemDetailsVO.getOtherType() == null) {
            throw new RuntimeException("检测项类别为空，请联系管理员");
        }
        if (quoteTestItemDetailsVO.getOtherType() == 2L) {
            return true;
        }
        String gwCgOtherTestItemKey = QuoteUtil.getGwCgOtherTestItemKey(quoteTestItemDTO.getSheetId(), quoteTestItemDTO.getSubId(), SystemUtil.getUserId());
        String listStr = redisUtils.get(gwCgOtherTestItemKey);
        if (StrUtil.isBlank(listStr)) {
            return true;
        }
        List<QuoteTestItemDetailsVO> list = JSON.parseArray(listStr, QuoteTestItemDetailsVO.class);
        Map<Long, QuoteTestItemDetailsVO> map = list.stream().collect(Collectors.toMap(QuoteTestItemDetailsVO::getPollutantId, detailsVO -> detailsVO));
        if (CollUtil.isNotEmpty(map)) {
            map.remove(quoteTestItemDetailsVO.getPollutantId());
            // 重新设置缓存
            redisUtils.set(gwCgOtherTestItemKey, JSON.toJSONString(new ArrayList<>(map.values())));
        }
        return true;
    }

    /**
     * 公卫（重构） 返回上一步
     *
     * @param quoteTestItem 表单id 子类id
     * @return result
     */
    @Override
    public boolean revertPreviousStep(QuoteTestItem quoteTestItem) {
        String sheetId = quoteTestItem.getSheetId();
        Long subId = quoteTestItem.getSubId();
        Long userId = SystemUtil.getUserId();
        String gwCgNormalTestItemKey = QuoteUtil.getGwCgNormalTestItemKey(sheetId, subId, userId);
        String gwCgOtherTestItemKey = QuoteUtil.getGwCgOtherTestItemKey(sheetId, subId, userId);
        // 删除缓存
        removeCache(gwCgNormalTestItemKey, gwCgOtherTestItemKey);
        return true;
    }

    /**
     * 批量添加检测项
     *
     * @param quoteTestItemDTO 表单id 子类id 检测项信息
     * @return result
     */
    @Override
    public boolean addTestItemBatch(QuoteTestItemDTO quoteTestItemDTO) {
        String sheetId = quoteTestItemDTO.getSheetId();
        Long subId = quoteTestItemDTO.getSubId();
        QuoteTestItem item = new QuoteTestItem();
        item.setSheetId(sheetId);
        item.setSubId(subId);
        // 1.删除原有的报价项
        List<QuoteTestItem> quoteTestItemList = quoteTestItemMapper.selectQuoteTestItemList(item);
        if (CollUtil.isNotEmpty(quoteTestItemList)) {
            if (quoteTestItemMapper.deleteQuoteTestItemAddTemp(item) == 0) {
                throw new RuntimeException("删除原有检测项目失败");
            }
        }
        // 2.插入新的报价项
        List<QuoteTestItemDetailsVO> quoteTestItemDetailsVOList = quoteTestItemDTO.getQuoteTestItemDetailsVOList();
        if (CollUtil.isNotEmpty(quoteTestItemDetailsVOList)) {
            for (QuoteTestItemDetailsVO quoteTestItemDetailsVO : quoteTestItemDetailsVOList) {
                quoteTestItemDetailsVO.setSheetId(sheetId);
                quoteTestItemDetailsVO.setSubId(subId);
                quoteTestItemDetailsVO.setCreateTime(new Date());
            }
            quoteTestItemMapper.addTestItemBatch(quoteTestItemDetailsVOList);
        }
        // 3.删除缓存
        Long userId = SystemUtil.getUserId();
        String gwCgNormalTestItemKey = QuoteUtil.getGwCgNormalTestItemKey(sheetId, subId, userId);
        String gwCgOtherTestItemKey = QuoteUtil.getGwCgOtherTestItemKey(sheetId, subId, userId);
        removeCache(gwCgNormalTestItemKey, gwCgOtherTestItemKey);
        return true;
    }

    /**
     * 获取其他检测物质信息列表
     *
     * @param quoteTestInfoDTO 大类id 检测性质id 检测类别id集合
     * @return result
     */
    @Override
    public List<QuoteTestItemDetailsVO> findOtherTestItemInfoList(QuoteTestInfoDTO quoteTestInfoDTO) {
        String sheetId = quoteTestInfoDTO.getSheetId();
        Long subId = quoteTestInfoDTO.getSubId();
        Long userId = SystemUtil.getUserId();
        QuoteTestInfoDTO dto = new QuoteTestInfoDTO();
        // 设置检测项目类型
        dto.setOtherType(QuoteUtil.OTHER_TEST_ITEM_FLAG);
        dto.setMasterCategoryId(new Long(getGwCgOtherIndustryId()));
        dto.setList(quoteTestInfoDTO.getList());
        // 获取并设置已报价的污染物id
        String ids = redisUtils.get(QuoteUtil.getGwCgOtherTestItemIdsKey(sheetId, subId, userId));
        if (StrUtil.isNotBlank(ids)) {
            dto.setPollutantIdList(QuoteUtil.getList(ids));
        }
        // 按危害因素名称查询
        dto.setFactorName(quoteTestInfoDTO.getFactorName());
        return quoteTestItemMapper.selectOtherTestItemInfoList(dto);
    }

    /**
     * 获取其他检测物质信息列表 （web端）
     *
     * @param quoteTestInfoDTO 大类id 检测性质id 检测类别id集合
     * @return result
     */
    @Override
    public Map<String, Object> findOtherTestItemInfoListForWeb(QuoteTestInfoDTO quoteTestInfoDTO) {
        // 报价单信息
        String sheetId = quoteTestInfoDTO.getSheetId();
        Long subId = quoteTestInfoDTO.getSubId();
        Long userId = SystemUtil.getUserId();
        // 获取缓存key
        String gwCgOtherTestItemKey = QuoteUtil.getGwCgOtherTestItemKey(sheetId, subId, userId);
        String gwCgNormalTestItemKey = QuoteUtil.getGwCgNormalTestItemKey(sheetId, subId, userId);
        String gwCgOtherTestItemIdsKey = QuoteUtil.getGwCgOtherTestItemIdsKey(sheetId, subId, userId);
        // 获取检测项列表
        List<QuoteTestItemDetailsVO> quoteTestItemDetailsVOList1 = quoteTestInfoDTO.getQuoteTestItemDetailsVOList();
        Map<Long, List<QuoteTestItemDetailsVO>> map1 = new HashMap<>();
        if (CollUtil.isNotEmpty(quoteTestItemDetailsVOList1)) {
            map1 = quoteTestItemDetailsVOList1.stream().collect(Collectors.groupingBy(QuoteTestItemDetailsVO::getOtherType));
        }
        List<QuoteTestItemDetailsVO> otherTestItemList = map1.get(QuoteUtil.OTHER_TEST_ITEM_FLAG);
        List<QuoteTestItemDetailsVO> normalTestItemList = map1.get(QuoteUtil.NORMAL_TEST_ITEM_FLAG);
        if (CollUtil.isNotEmpty(otherTestItemList)) {
            redisUtils.set(gwCgOtherTestItemKey, JSON.toJSONString(otherTestItemList));
            List<Long> list = otherTestItemList.stream().map(QuoteTestItemDetailsVO::getPollutantId).collect(Collectors.toList());
            if (CollUtil.isEmpty(list)) {
                throw new RuntimeException("检测列表不为空，但是列表中的污染物id为空，请联系管理员！");
            }
            redisUtils.set(gwCgOtherTestItemIdsKey, QuoteUtil.getFormatString(list, ","));
        }
        if (CollUtil.isNotEmpty(normalTestItemList)) {
            redisUtils.set(gwCgNormalTestItemKey, JSON.toJSONString(normalTestItemList));
        }

        // 获取其他检测项列表
        String gwCgOtherTestItemForWebKey = QuoteUtil.getGwCgOtherTestItemForWebKey(sheetId, subId, userId);
        String testItemWebList = redisUtils.get(gwCgOtherTestItemForWebKey);
        List<QuoteTestItemDetailsVO> quoteTestItemDetailsVOList;
        if (StrUtil.isBlank(testItemWebList)) {
            QuoteTestInfoDTO dto = new QuoteTestInfoDTO();
            // 设置检测项目类型
            dto.setOtherType(QuoteUtil.OTHER_TEST_ITEM_FLAG);
            dto.setList(quoteTestInfoDTO.getList());
            dto.setFactorName(quoteTestInfoDTO.getFactorName());
            dto.setMasterCategoryId(new Long(getGwCgOtherIndustryId()));
            quoteTestItemDetailsVOList = quoteTestItemMapper.selectOtherTestItemInfoList(dto);
            if (CollUtil.isNotEmpty(quoteTestItemDetailsVOList)) {
                redisUtils.set(gwCgOtherTestItemForWebKey, quoteTestItemDetailsVOList);
            } else {
                return new HashMap<>();
            }
        } else {
            quoteTestItemDetailsVOList = JSON.parseArray(testItemWebList, QuoteTestItemDetailsVO.class);
        }
        // 设置已经勾选的检测项
        Map<Long, QuoteTestItemDetailsVO> map = quoteTestItemDetailsVOList.stream().collect(Collectors.toMap(QuoteTestItemDetailsVO::getPollutantId, item -> item));
        String ids = redisUtils.get(QuoteUtil.getGwCgOtherTestItemIdsKey(sheetId, subId, userId));
        if (StrUtil.isNotBlank(ids)) {
            for (String id : ids.split(",")) {
                Long aLong = new Long(id);
                QuoteTestItemDetailsVO quoteTestItemDetailsVO = map.get(aLong);
                quoteTestItemDetailsVO.setChecked(QuoteUtil.CHECKED_FLAG);
            }
        }
        // 排序
        List<QuoteTestItemDetailsVO> sortList = quoteTestItemDetailsVOList.stream().sorted(Comparator.comparing(QuoteTestItemDetailsVO::getChecked, Comparator.nullsFirst(Integer::compareTo)).reversed()).collect(Collectors.toList());
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageIndex = ServletUtils.getParameterToInt("pageIndex");
        if (pageIndex != null) {
            pageDomain.setPageNum(pageIndex);
        }
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        pageNum = pageNum == null ? 1 : pageNum;
//        pageSize = pageSize == null ? 10 : pageSize;
        pageSize = pageSize == null ? sortList.size() : pageSize;
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = fromIndex + pageSize;
        List<QuoteTestItemDetailsVO> list = sortList.subList(fromIndex, toIndex);
        // 封装返回值
        int size = sortList.size();
        Map<String, Object> m = new HashMap<>(5);
        m.put("list", list);
        m.put("currPage", pageNum);
        m.put("pageSize", pageSize);
        m.put("totalCount", size);
        if (size % pageSize == 0) {
            m.put("totalPage", size / pageSize);
        } else {
            m.put("totalPage", size / pageSize + 1);
        }
        return m;
    }

    /**
     * 缓存检测性质id
     *
     * @param quoteTestItemDTO 表单id 子类id 检测性质id
     * @return result
     */
    @Override
    public boolean saveTestNatureId(QuoteTestItemDTO quoteTestItemDTO) {
        String sheetId = quoteTestItemDTO.getSheetId();
        Long subId = quoteTestItemDTO.getSubId();
        Long userId = SystemUtil.getUserId();
        String gwCgTestNatureIdCacheKey = QuoteUtil.getGwCgTestNatureIdCacheKey(sheetId, subId, userId);
        redisUtils.set(gwCgTestNatureIdCacheKey, quoteTestItemDTO.getTestNatureId());
        return true;
    }

    /**
     * 删除缓存
     */
    private void removeCache(String... keys) {
        for (String key : keys) {
            redisUtils.delete(key);
        }
    }

    /**
     * 获取【其他公共场所】行业id
     */
    private String getGwCgOtherIndustryId() {
        String gwCgOtherIndustryIdKey = QuoteUtil.GW_CG_OTHER_INDUSTRY_ID_KEY;
        String industryId = redisUtils.get(gwCgOtherIndustryIdKey);
        if (StrUtil.isBlank(industryId)) {
            // 从数据库查询
            QuoteIndustryInfo quoteIndustryInfo = new QuoteIndustryInfo();
            quoteIndustryInfo.setIndustryName(QuoteUtil.OTHER_PUBLIC_PLACE_INDUSTRY_NAME);
            quoteIndustryInfo.setProjectId(QuoteUtil.GW_PROJECT_ID);
            QuoteIndustryInfo industryInfo = quoteIndustryInfoMapper.selectQuoteIndustryInfo(quoteIndustryInfo);
            Optional.ofNullable(industryInfo)
                    .map(QuoteIndustryInfo::getId)
                    .orElseThrow(() -> new RuntimeException("未获取到【其他公共场所】行业id"));
            industryId = industryInfo.getId().toString();
            redisUtils.set(gwCgOtherIndustryIdKey, industryId);
        }
        return industryId;
    }
}
