package com.ruoyi.quote.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.quote.domain.dto.QuoteExpenseDetailsDTO;
import com.ruoyi.quote.domain.entity.QuoteSheetItems;
import com.ruoyi.quote.domain.entity.QuoteSubCategory;
import com.ruoyi.quote.domain.entity.QuoteTestItem;
import com.ruoyi.quote.mapper.QuoteSheetItemsMapper;
import com.ruoyi.quote.mapper.QuoteSubCategoryMapper;
import com.ruoyi.quote.mapper.QuoteTestItemMapper;
import com.ruoyi.quote.utils.QuoteUtil;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.mapper.QuoteExpenseDetailsMapper;
import com.ruoyi.quote.domain.entity.QuoteExpenseDetails;
import com.ruoyi.quote.service.IQuoteExpenseDetailsService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 检测费用明细Service业务层处理
 *
 * @author yrb
 * @date 2022-04-29
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class QuoteExpenseDetailsServiceImpl implements IQuoteExpenseDetailsService {
    private final QuoteExpenseDetailsMapper quoteExpenseDetailsMapper;
    private final QuoteSubCategoryMapper quoteSubCategoryMapper;
    private final QuoteSheetItemsMapper quoteSheetItemsMapper;
    private final QuoteTestItemMapper quoteTestItemMapper;
    private final RedisUtils redisUtils;

    @Autowired
    public QuoteExpenseDetailsServiceImpl(QuoteExpenseDetailsMapper quoteExpenseDetailsMapper,
                                          QuoteSubCategoryMapper quoteSubCategoryMapper,
                                          QuoteSheetItemsMapper quoteSheetItemsMapper,
                                          QuoteTestItemMapper quoteTestItemMapper,
                                          RedisUtils redisUtils) {
        this.quoteExpenseDetailsMapper = quoteExpenseDetailsMapper;
        this.quoteSubCategoryMapper = quoteSubCategoryMapper;
        this.quoteTestItemMapper = quoteTestItemMapper;
        this.quoteSheetItemsMapper = quoteSheetItemsMapper;
        this.redisUtils = redisUtils;
    }

    /**
     * 查询检测费用明细
     *
     * @param id 检测费用明细主键
     * @return 检测费用明细
     */
    @Override
    public QuoteExpenseDetails selectQuoteExpenseDetailsById(Long id) {
        return quoteExpenseDetailsMapper.selectQuoteExpenseDetailsById(id);
    }

    /**
     * 查询检测费用明细列表
     *
     * @param quoteExpenseDetails 检测费用明细
     * @return 检测费用明细
     */
    @Override
    public List<QuoteExpenseDetails> selectQuoteExpenseDetailsList(QuoteExpenseDetails quoteExpenseDetails) {
        return quoteExpenseDetailsMapper.selectQuoteExpenseDetailsList(quoteExpenseDetails);
    }

    /**
     * 新增检测费用明细
     *
     * @param quoteExpenseDetails 检测费用明细
     * @return 结果
     */
    @Override
    public int insertQuoteExpenseDetails(QuoteExpenseDetails quoteExpenseDetails) {
        QuoteExpenseDetails expenseDetails = new QuoteExpenseDetails();
        expenseDetails.setSheetId(quoteExpenseDetails.getSheetId());
        expenseDetails.setSubId(quoteExpenseDetails.getSubId());
        List<QuoteExpenseDetails> quoteExpenseDetailsList = quoteExpenseDetailsMapper.selectQuoteExpenseDetailsList(expenseDetails);
        if (CollUtil.isNotEmpty(quoteExpenseDetailsList)) {
            if (quoteExpenseDetailsMapper.deleteQuoteExpenseDetails(expenseDetails) == 0) {
                throw new RuntimeException("删除原有的检测费用明细失败");
            }
        }
        quoteExpenseDetails.setCreateTime(DateUtils.getNowDate());
        return quoteExpenseDetailsMapper.insertQuoteExpenseDetails(quoteExpenseDetails);
    }

    /**
     * 修改检测费用明细
     *
     * @param quoteExpenseDetails 检测费用明细
     * @return 结果
     */
    @Override
    public int updateQuoteExpenseDetails(QuoteExpenseDetails quoteExpenseDetails) {
        quoteExpenseDetails.setUpdateTime(DateUtils.getNowDate());
        return quoteExpenseDetailsMapper.updateQuoteExpenseDetails(quoteExpenseDetails);
    }

    /**
     * 批量删除检测费用明细
     *
     * @param ids 需要删除的检测费用明细主键
     * @return 结果
     */
    @Override
    public int deleteQuoteExpenseDetailsByIds(Long[] ids) {
        return quoteExpenseDetailsMapper.deleteQuoteExpenseDetailsByIds(ids);
    }

    /**
     * 删除检测费用明细信息
     *
     * @param id 检测费用明细主键
     * @return 结果
     */
    @Override
    public int deleteQuoteExpenseDetailsById(Long id) {
        return quoteExpenseDetailsMapper.deleteQuoteExpenseDetailsById(id);
    }

    /**
     * 设置临时文件为永久文件
     *
     * @return
     */
    @Override
    public int updateTempFlag(QuoteExpenseDetails quoteExpenseDetails) {
        return quoteExpenseDetailsMapper.updateTempFlag(quoteExpenseDetails);
    }

    /**
     * 获取子类检测费用明细
     *
     * @param quoteExpenseDetailsDTO 表单id、子类全称、子类简称...
     * @return result
     */
    @Override
    public List<QuoteExpenseDetails> findSubQuoteExpenseDetails(QuoteExpenseDetailsDTO quoteExpenseDetailsDTO) {
        String sheetId = quoteExpenseDetailsDTO.getSheetId();
        String companyName = quoteExpenseDetailsDTO.getCompanyName();
        List<Long> ids = quoteExpenseDetailsDTO.getList();
        // 固定升序
        Collections.sort(ids);
        QuoteExpenseDetails quoteExpenseDetails = new QuoteExpenseDetails();
        quoteExpenseDetails.setSheetId(sheetId);
        List<QuoteExpenseDetails> list = quoteExpenseDetailsMapper.selectQuoteExpenseDetailsList(quoteExpenseDetails);
        if (CollUtil.isEmpty(list)) {
            List<QuoteExpenseDetails> quoteExpenseDetailsList = new ArrayList<>();
            for (Long id : ids) {
                QuoteExpenseDetails expenseDetails = new QuoteExpenseDetails();
                expenseDetails.setSheetId(sheetId);
                expenseDetails.setCompanyName(companyName);
                expenseDetails.setSubId(id);
                expenseDetails.setTotal(BigDecimal.ZERO);
                QuoteSubCategory quoteSubCategory = quoteSubCategoryMapper.selectQuoteSubCategoryById(id);
                expenseDetails.setSubName(quoteSubCategory.getFullCategory());
                expenseDetails.setSubAbb(quoteSubCategory.getAbbreviationCategory());
                quoteExpenseDetailsList.add(expenseDetails);
            }
            return quoteExpenseDetailsList;
        } else {
            List<Long> subIds = new ArrayList<>();
            List<QuoteExpenseDetails> newList = new ArrayList<>();
            for (QuoteExpenseDetails qed : list) {
                Long subId = qed.getSubId();
                subIds.add(subId);
                if (!ids.contains(subId)) {
                    // 新的子类不包含原来的子类，删除原来的子类检测费用明细
                    QuoteExpenseDetails details = new QuoteExpenseDetails();
                    details.setSheetId(sheetId);
                    details.setSubId(subId);
                    if (quoteExpenseDetailsMapper.deleteQuoteExpenseDetails(details) == 0) {
                        throw new RuntimeException("删除原有子类检测费用明细信息失败");
                    }
                    QuoteSheetItems quoteSheetItems = new QuoteSheetItems();
                    quoteSheetItems.setSheetId(sheetId);
                    quoteSheetItems.setSubId(subId);
                    List<QuoteSheetItems> quoteSheetItemsList = quoteSheetItemsMapper.selectQuoteSheetItemsList(quoteSheetItems);
                    if (quoteSheetItemsList != null && quoteSheetItemsList.size() != 0) {
                        if (quoteSheetItemsMapper.deleteQuoteSheetItems(quoteSheetItems) == 0) {
                            throw new RuntimeException("删除原有子类检测费用明细信息失败");
                        }
                    }
                    QuoteTestItem quoteTestItem = new QuoteTestItem();
                    quoteTestItem.setSheetId(sheetId);
                    quoteTestItem.setSubId(subId);
                    List<QuoteTestItem> quoteTestItemList = quoteTestItemMapper.selectQuoteTestItemList(quoteTestItem);
                    if (quoteTestItemList != null && quoteTestItemList.size() != 0) {
                        if (quoteTestItemMapper.deleteQuoteTestItemAddTemp(quoteTestItem) == 0) {
                            throw new RuntimeException("删除原有子类检测费用明细信息失败");
                        }
                    }
                    // 清除置顶列表
                    Long userId = SystemUtil.getUserId();
                    String postCheckedIdsKey = QuoteUtil.getZwPostCheckedIdsKey(sheetId, subId, userId);
                    String idsValue = redisUtils.get(postCheckedIdsKey);
                    if (StrUtil.isNotBlank(idsValue)) {
                        List<Long> postIds = QuoteUtil.getList(idsValue);
                        for (Long postId : postIds) {
                            redisUtils.delete(QuoteUtil.getZwTestListKey(sheetId, subId, postId, userId));
                            redisUtils.delete(QuoteUtil.getZwTestListTempKey(sheetId, subId, postId, userId));
                            redisUtils.delete(QuoteUtil.getZwBaseTestListKey(sheetId, subId, postId, userId));
                            redisUtils.delete(QuoteUtil.getZwCheckedIdsKey(sheetId, subId, postId, userId));
                            redisUtils.delete(QuoteUtil.getZwBaseTestListTempKey(sheetId, subId, postId, userId));
                            redisUtils.delete(QuoteUtil.getZwCheckedIdsTempKey(sheetId, subId, postId, userId));
                        }
                        redisUtils.delete(postCheckedIdsKey);
                    }
                    continue;
                }
                newList.add(qed);
            }
            for (Long id : ids) {
                if (!subIds.contains(id)) {
                    // 原有子类不包含现在的所选子类，新增子类
                    QuoteExpenseDetails expenseDetails = new QuoteExpenseDetails();
                    expenseDetails.setSheetId(sheetId);
                    expenseDetails.setCompanyName(companyName);
                    expenseDetails.setSubId(id);
                    expenseDetails.setTotal(BigDecimal.ZERO);
                    QuoteSubCategory quoteSubCategory = quoteSubCategoryMapper.selectQuoteSubCategoryById(id);
                    expenseDetails.setSubName(quoteSubCategory.getFullCategory());
                    expenseDetails.setSubAbb(quoteSubCategory.getAbbreviationCategory());
                    newList.add(expenseDetails);
                }
            }
            // 固定顺序
            List<QuoteExpenseDetails> result = new ArrayList<>();
            Map<Long, QuoteExpenseDetails> detailsMap = newList.stream().collect(Collectors.toMap(QuoteExpenseDetails::getSubId, quoteExpenseDetail -> quoteExpenseDetail));
            for (Long id : ids) {
                result.add(detailsMap.get(id));
            }
            return result;
        }
    }

    /**
     * 删除子类报价费用检测明细
     *
     * @param quoteExpenseDetails 表单id 子类id
     * @return result
     */
    @Override
    public boolean deleteQuoteExpenseDetailsBySubId(QuoteExpenseDetails quoteExpenseDetails) {
        String sheetId = quoteExpenseDetails.getSheetId();
        Long subId = quoteExpenseDetails.getSubId();
        QuoteExpenseDetails expenseDetails = new QuoteExpenseDetails();
        expenseDetails.setSheetId(sheetId);
        expenseDetails.setSubId(subId);
        List<QuoteExpenseDetails> quoteExpenseDetailsList = quoteExpenseDetailsMapper.selectQuoteExpenseDetailsList(quoteExpenseDetails);
        if (CollUtil.isNotEmpty(quoteExpenseDetailsList)) {
            if (quoteExpenseDetailsMapper.deleteQuoteExpenseDetails(expenseDetails) == 0) {
                return false;
            }
        }
        QuoteSheetItems quoteSheetItems = new QuoteSheetItems();
        quoteSheetItems.setSheetId(sheetId);
        quoteSheetItems.setSubId(subId);
        List<QuoteSheetItems> quoteSheetItemsList = quoteSheetItemsMapper.selectQuoteSheetItemsList(quoteSheetItems);
        if (CollUtil.isNotEmpty(quoteSheetItemsList)) {
            if (quoteSheetItemsMapper.deleteQuoteSheetItems(quoteSheetItems) == 0) {
                throw new RuntimeException("删除检测细项信息失败");
            }
        }
        QuoteTestItem quoteTestItem = new QuoteTestItem();
        quoteTestItem.setSheetId(sheetId);
        quoteTestItem.setSubId(subId);
        List<QuoteTestItem> quoteTestItemList = quoteTestItemMapper.selectQuoteTestItemList(quoteTestItem);
        if (CollUtil.isNotEmpty(quoteTestItemList)) {
            if (quoteTestItemMapper.deleteQuoteTestItemAddTemp(quoteTestItem) == 0) {
                throw new RuntimeException("删除检测细项信息失败");
            }
        }
        return true;
    }

    /**
     * 获取子类检测费用明细
     *
     * @param quoteExpenseDetails 表单id、子类id
     * @return result
     */
    @Override
    public QuoteExpenseDetails findSubExpenseDetails(QuoteExpenseDetails quoteExpenseDetails) {
        return quoteExpenseDetailsMapper.selectSubExpenseDetails(quoteExpenseDetails);
    }
}
