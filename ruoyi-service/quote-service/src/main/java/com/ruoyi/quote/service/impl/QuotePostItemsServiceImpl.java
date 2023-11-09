package com.ruoyi.quote.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.quote.domain.dto.PostExpenseDetailsDTO;
import com.ruoyi.quote.domain.entity.QuotePostItems;
import com.ruoyi.quote.domain.entity.QuoteSheetItems;
import com.ruoyi.quote.mapper.QuotePostInfoMapper;
import com.ruoyi.quote.mapper.QuoteSheetItemsMapper;
import com.ruoyi.quote.utils.QuoteUtil;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.mapper.QuotePostItemsMapper;
import com.ruoyi.quote.service.IQuotePostItemsService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 岗位检测项目Service业务层处理
 *
 * @author yrb
 * @date 2022-06-10
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class QuotePostItemsServiceImpl implements IQuotePostItemsService {
    private final QuotePostItemsMapper quotePostItemsMapper;
    private final QuotePostInfoMapper quotePostInfoMapper;
    private final QuoteSheetItemsMapper quoteSheetItemsMapper;
    private final RedisUtils redisUtils;

    @Autowired
    public QuotePostItemsServiceImpl(QuotePostItemsMapper quotePostItemsMapper,
                                     QuotePostInfoMapper quotePostInfoMapper,
                                     QuoteSheetItemsMapper quoteSheetItemsMapper,
                                     RedisUtils redisUtils) {
        this.quotePostItemsMapper = quotePostItemsMapper;
        this.quotePostInfoMapper = quotePostInfoMapper;
        this.quoteSheetItemsMapper = quoteSheetItemsMapper;
        this.redisUtils = redisUtils;
    }

    /**
     * 查询岗位检测项目
     *
     * @param id 岗位检测项目主键
     * @return 岗位检测项目
     */
    @Override
    public QuotePostItems selectQuotePostItemsById(Long id) {
        return quotePostItemsMapper.selectQuotePostItemsById(id);
    }

    /**
     * 查询岗位检测项目列表
     *
     * @param quotePostItems 岗位检测项目
     * @return 岗位检测项目
     */
    @Override
    public List<QuotePostItems> selectQuotePostItemsList(QuotePostItems quotePostItems) {
        return quotePostItemsMapper.selectQuotePostItemsList(quotePostItems);
    }

    /**
     * 新增岗位检测项目
     *
     * @param quotePostItems 岗位检测项目
     * @return 结果
     */
    @Override
    public int insertQuotePostItems(QuotePostItems quotePostItems) {
        return quotePostItemsMapper.insertQuotePostItems(quotePostItems);
    }

    /**
     * 修改岗位检测项目
     *
     * @param quotePostItems 岗位检测项目
     * @return 结果
     */
    @Override
    public int updateQuotePostItems(QuotePostItems quotePostItems) {
        return quotePostItemsMapper.updateQuotePostItems(quotePostItems);
    }

    /**
     * 批量删除岗位检测项目
     *
     * @param ids 需要删除的岗位检测项目主键
     * @return 结果
     */
    @Override
    public int deleteQuotePostItemsByIds(Long[] ids) {
        return quotePostItemsMapper.deleteQuotePostItemsByIds(ids);
    }

    /**
     * 删除岗位检测项目信息
     *
     * @param id 岗位检测项目主键
     * @return 结果
     */
    @Override
    public int deleteQuotePostItemsById(Long id) {
        return quotePostItemsMapper.deleteQuotePostItemsById(id);
    }

    /**
     * 删除岗位检测项目信息
     *
     * @param sheetId 表单id
     * @return
     */
    @Override
    public int deleteQuotePostItemsBySheetId(String sheetId) {
        return quotePostItemsMapper.deleteQuotePostItemsBySheetId(sheetId);
    }

    /**
     * 子类对应岗位检测费用信息
     *
     * @param postExpenseDetailsDTO 表单id 公司名称 子类id 岗位id 所选岗位id集合
     * @return result
     */
    @Override
    public List<QuotePostItems> findSubPostExpenseDetails(PostExpenseDetailsDTO postExpenseDetailsDTO) {
        String sheetId = postExpenseDetailsDTO.getSheetId();
        Long subId = postExpenseDetailsDTO.getSubId();
        List<Long> list = postExpenseDetailsDTO.getList();
        // 固定顺序 升序
        Collections.sort(list);
        // 缓存已选的岗位
        String postCheckedIdsKey = QuoteUtil.getZwPostCheckedIdsKey(sheetId, subId, SystemUtil.getUserId());
        redisUtils.set(postCheckedIdsKey, QuoteUtil.getFormatString(list, ","));
        String companyName = postExpenseDetailsDTO.getCompanyName();
        QuotePostItems quotePostItems = new QuotePostItems();
        quotePostItems.setSheetId(sheetId);
        quotePostItems.setSubId(subId);
        List<QuotePostItems> quotePostItemsList = quotePostItemsMapper.selectQuotePostItemsList(quotePostItems);
        if (quotePostItemsList == null || quotePostItemsList.size() == 0) {
            List<QuotePostItems> postItemsList = new ArrayList<>();
            for (Long postId : list) {
                QuotePostItems postItems = new QuotePostItems();
                postItems.setSheetId(sheetId);
                postItems.setSubId(subId);
                postItems.setPostId(postId);
                postItems.setCompanyName(companyName);
                // 此处充当岗位名称用
                postItems.setItemsName(quotePostInfoMapper.selectQuotePostInfoById(postId).getPostName());
                postItems.setTotalPrice(BigDecimal.ZERO);
                postItemsList.add(postItems);
            }
            return postItemsList;
        } else {
            List<Long> postIds = new ArrayList<>();
            List<QuotePostItems> newList = new ArrayList<>();
            for (QuotePostItems postItems : quotePostItemsList) {
                Long postId = postItems.getPostId();
                postIds.add(postId);
                if (!list.contains(postId)) {
                    QuotePostItems items = new QuotePostItems();
                    items.setSheetId(sheetId);
                    items.setSubId(subId);
                    items.setPostId(postId);
                    List<QuotePostItems> postItemsList = quotePostItemsMapper.selectQuotePostItemsList(items);
                    if (postItemsList != null && postItemsList.size() != 0) {
                        if (quotePostItemsMapper.deleteQuotePostItems(items) == 0) {
                            throw new RuntimeException("删除岗位对应检测信息失败");
                        }
                    }
                    QuoteSheetItems quoteSheetItems = new QuoteSheetItems();
                    quoteSheetItems.setSheetId(sheetId);
                    quoteSheetItems.setSubId(subId);
                    quoteSheetItems.setPostId(postId);
                    List<QuoteSheetItems> sheetItems = quoteSheetItemsMapper.selectQuoteSheetItemsList(quoteSheetItems);
                    if (sheetItems != null && sheetItems.size() != 0) {
                        if (quoteSheetItemsMapper.deleteQuoteSheetItems(quoteSheetItems) == 0) {
                            throw new RuntimeException("删除岗位对应检测详细信息失败");
                        }
                    }
                    continue;
                }
                postItems.setItemsName(quotePostInfoMapper.selectQuotePostInfoById(postId).getPostName());
                newList.add(postItems);
            }
            for (Long postId : list) {
                if (!postIds.contains(postId)) {
                    // 原有岗位不包含现在的所选岗位，新增岗位
                    QuotePostItems qpi = new QuotePostItems();
                    qpi.setSheetId(sheetId);
                    qpi.setSubId(subId);
                    qpi.setPostId(postId);
                    qpi.setCompanyName(companyName);
                    // 此处充当岗位名称用
                    qpi.setItemsName(quotePostInfoMapper.selectQuotePostInfoById(postId).getPostName());
                    qpi.setTotalPrice(BigDecimal.ZERO);
                    newList.add(qpi);
                }
            }
            // 固定顺序
            List<QuotePostItems> result = new ArrayList<>();
            Map<Long, QuotePostItems> itemsMap = newList.stream().collect(Collectors.toMap(QuotePostItems::getPostId, quotePostItem -> quotePostItem));
            for (Long id : list) {
                result.add(itemsMap.get(id));
            }
            return result;
        }
    }

    /**
     * 删除子类对应的岗位检测信息
     *
     * @param postExpenseDetailsDTO 表单id 子类id 岗位id
     * @return
     */
    @Override
    public boolean deleteSubPostExpenseDetails(PostExpenseDetailsDTO postExpenseDetailsDTO) {
        String sheetId = postExpenseDetailsDTO.getSheetId();
        Long subId = postExpenseDetailsDTO.getSubId();
        Long postId = postExpenseDetailsDTO.getPostId();
        // 查询并删除岗位检测信息
        QuotePostItems postItems = new QuotePostItems();
        postItems.setSheetId(sheetId);
        postItems.setSubId(subId);
        postItems.setPostId(postId);
        List<QuotePostItems> postItemsList = quotePostItemsMapper.selectQuotePostItemsList(postItems);
        if (CollUtil.isNotEmpty(postItemsList)) {
            throw new RuntimeException("子类对应岗位检测信息为空，请联系管理员");
        }
        if (quotePostItemsMapper.deleteQuotePostItems(postItems) == 0) {
            return false;
        }
        // 查询并删除检测细项信息
        QuoteSheetItems quoteSheetItems = new QuoteSheetItems();
        quoteSheetItems.setSheetId(sheetId);
        quoteSheetItems.setSubId(subId);
        quoteSheetItems.setPostId(postId);
        List<QuoteSheetItems> quoteSheetItemsList = quoteSheetItemsMapper.selectQuoteSheetItemsList(quoteSheetItems);
        if (CollUtil.isEmpty(quoteSheetItemsList)) {
            throw new RuntimeException("未获取到岗位对应检测信息，请联系管理员");
        }
        if (quoteSheetItemsMapper.deleteQuoteSheetItems(quoteSheetItems) == 0) {
            throw new RuntimeException("删除岗位对应检测信息失败");
        }
        // 清除置顶列表
        Long userId = SystemUtil.getUserId();
        redisUtils.delete(QuoteUtil.getZwTestListKey(sheetId, subId, postId, userId));
        redisUtils.delete(QuoteUtil.getZwTestListTempKey(sheetId, subId, postId, userId));
        redisUtils.delete(QuoteUtil.getZwBaseTestListKey(sheetId, subId, postId, userId));
        redisUtils.delete(QuoteUtil.getZwBaseTestListTempKey(sheetId, subId, postId, userId));
        redisUtils.delete(QuoteUtil.getZwCheckedIdsKey(sheetId, subId, postId, userId));
        redisUtils.delete(QuoteUtil.getZwCheckedIdsTempKey(sheetId, subId, postId, userId));
        return true;
    }
}
