package com.ruoyi.quote.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.MoneyToCNFormat;
import com.ruoyi.quote.domain.dto.QuoteSheetInfoDTO;
import com.ruoyi.quote.domain.dto.SheetItemsDTO;
import com.ruoyi.quote.domain.entity.*;
import com.ruoyi.quote.domain.vo.*;
import com.ruoyi.quote.mapper.*;
import com.ruoyi.quote.utils.QuoteUtil;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.service.IQuoteSheetItemsService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 报价检测项目Service业务层处理
 *
 * @author yrb
 * @date 2022-04-29
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
@Slf4j
public class QuoteSheetItemsServiceImpl implements IQuoteSheetItemsService {
    private final QuoteSheetItemsMapper quoteSheetItemsMapper;
    private final QuoteTestItemMapper quoteTestItemMapper;
    private final QuotePostItemsMapper quotePostItemsMapper;
    private final RedisUtils redisUtils;
    private final QuoteCustomerInfoMapper quoteCustomerInfoMapper;
    private final QuoteExpenseDetailsMapper quoteExpenseDetailsMapper;
    private final QuoteSheetInfoMapper quoteSheetInfoMapper;

    @Autowired
    public QuoteSheetItemsServiceImpl(QuoteSheetItemsMapper quoteSheetItemsMapper,
                                      QuoteTestItemMapper quoteTestItemMapper,
                                      QuotePostItemsMapper quotePostItemsMapper,
                                      RedisUtils redisUtils,
                                      QuoteCustomerInfoMapper quoteCustomerInfoMapper,
                                      QuoteExpenseDetailsMapper quoteExpenseDetailsMapper,
                                      QuoteSheetInfoMapper quoteSheetInfoMapper) {
        this.quoteSheetItemsMapper = quoteSheetItemsMapper;
        this.quoteTestItemMapper = quoteTestItemMapper;
        this.quotePostItemsMapper = quotePostItemsMapper;
        this.redisUtils = redisUtils;
        this.quoteCustomerInfoMapper = quoteCustomerInfoMapper;
        this.quoteExpenseDetailsMapper = quoteExpenseDetailsMapper;
        this.quoteSheetInfoMapper = quoteSheetInfoMapper;
    }

    /**
     * 新增岗位检测项目
     *
     * @param sheetItemsDTO 岗位对应的检测项目
     * @return 结果
     */
    @Override
    public boolean insertQuoteSheetItemsBatch(SheetItemsDTO sheetItemsDTO) {
        String sheetId = sheetItemsDTO.getSheetId();
        Long postId = sheetItemsDTO.getPostId();
        Long subId = sheetItemsDTO.getSubId();
        QuoteSheetItems sheetItems = new QuoteSheetItems();
        sheetItems.setSheetId(sheetId);
        sheetItems.setSubId(subId);
        sheetItems.setPostId(postId);
        List<QuoteSheetItems> quoteSheetItemsList = quoteSheetItemsMapper.selectQuoteSheetItemsBySubId(sheetItems);
        if (CollUtil.isNotEmpty(quoteSheetItemsList)) {
            if (quoteSheetItemsMapper.deleteQuoteSheetItemsBySheetId(sheetItems) == 0) {
                throw new RuntimeException("删除对应岗位原有检测项目信息失败");
            }
        }
        List<QuoteBaseFactorVO> baseFactorVOList = sheetItemsDTO.getQuoteBaseFactorVOList();
        List<String> factorNameList = new ArrayList<>();
        List<BigDecimal> totalPriceList = new ArrayList<>();
        if (CollUtil.isNotEmpty(baseFactorVOList)) {
            String postName = sheetItemsDTO.getPostName();
            for (QuoteBaseFactorVO quoteBaseFactorVO : baseFactorVOList) {
                QuoteSheetItems quoteSheetItems = new QuoteSheetItems();
                quoteSheetItems.setSheetId(sheetId);
                quoteSheetItems.setPostName(postName);
                quoteSheetItems.setSubId(subId);
                quoteSheetItems.setPostId(postId);
                String factorName = quoteBaseFactorVO.getFactorName();
                quoteSheetItems.setFactorName(factorName);
                factorNameList.add(factorName);
                quoteSheetItems.setLimitRange(quoteBaseFactorVO.getLimitRange());
                quoteSheetItems.setPointNumber(quoteBaseFactorVO.getPointNumber());
                quoteSheetItems.setPrice(quoteBaseFactorVO.getPrice());
                quoteSheetItems.setStandardInfo(quoteBaseFactorVO.getStandardInfo());
                BigDecimal totalPrice = quoteBaseFactorVO.getTotalPrice();
                quoteSheetItems.setTotalPrice(totalPrice);
                totalPriceList.add(totalPrice);
                quoteSheetItems.setCreateTime(DateUtils.getNowDate());
                if (quoteSheetItemsMapper.insertQuoteSheetItems(quoteSheetItems) == 0) {
                    throw new RuntimeException("岗位检测项目信息新增失败！");
                }
            }
            String factorNames = QuoteUtil.getFormatString(factorNameList, "、");
            if (totalPriceList.size() == 0) {
                throw new RuntimeException("获取总价格失败");
            }
            BigDecimal totalPrice = QuoteUtil.getBigDecimalTotal(totalPriceList);
            QuotePostItems quotePostItems = new QuotePostItems();
            quotePostItems.setSheetId(sheetId);
            quotePostItems.setSubId(subId);
            quotePostItems.setPostId(postId);
            List<QuotePostItems> postItemsList = quotePostItemsMapper.selectQuotePostItemsList(quotePostItems);
            if (postItemsList != null && postItemsList.size() != 0) {
                if (quotePostItemsMapper.deleteQuotePostItems(quotePostItems) == 0) {
                    throw new RuntimeException("删除原有岗位检测项目失败");
                }
            }
            quotePostItems.setItemsName(factorNames);
            quotePostItems.setTotalPrice(totalPrice);
            if (quotePostItemsMapper.insertQuotePostItems(quotePostItems) == 0) {
                throw new RuntimeException("插入岗位检测项目失败");
            }
        }
        // 更新缓存
        Long userId = SystemUtil.getUserId();
        if (CollUtil.isNotEmpty(baseFactorVOList)) {
            String testListKey = QuoteUtil.getZwTestListKey(sheetId, subId, postId, userId);
            String listStr = JSONObject.toJSON(baseFactorVOList).toString();
            if (StrUtil.isBlank(listStr)) {
                throw new RuntimeException("通过json获取字符串失败");
            }
            redisUtils.set(testListKey, listStr);
            String testListTempKey = QuoteUtil.getZwTestListTempKey(sheetId, subId, postId, userId);
            redisUtils.set(testListTempKey, listStr);
            List<QuoteBaseFactorVO> baseTestList = new ArrayList<>();
            List<Long> idsList = new ArrayList<>();
            String baseTestListKey = QuoteUtil.getZwBaseTestListKey(sheetId, subId, postId, userId);
            String checkedIdsKey = QuoteUtil.getZwCheckedIdsKey(sheetId, subId, postId, userId);
            for (QuoteBaseFactorVO quoteBaseFactorVO : baseFactorVOList) {
                idsList.add(quoteBaseFactorVO.getId());
                if (quoteBaseFactorVO.getRelationFlag() == null || quoteBaseFactorVO.getRelationFlag() != 1) {
                    baseTestList.add(quoteBaseFactorVO);
                }
            }
            if (baseTestList.size() != 0) {
                String baseListStr = JSONObject.toJSON(baseTestList).toString();
                redisUtils.set(baseTestListKey, baseListStr);
                String baseTestListTempKey = QuoteUtil.getZwBaseTestListTempKey(sheetId, subId, postId, userId);
                redisUtils.set(baseTestListTempKey, baseListStr);
            }
            String checkedIds = QuoteUtil.getFormatString(idsList, ",");
            redisUtils.set(checkedIdsKey, checkedIds);
            String checkedIdsTempKey = QuoteUtil.getZwCheckedIdsTempKey(sheetId, subId, postId, userId);
            redisUtils.set(checkedIdsTempKey, checkedIds);
        }
        return true;
    }

    /**
     * 根据报价单id获取检测项目费用明细
     *
     * @param sheetId 报价单id
     * @return
     */
    @Override
    public List<QuoteSheetItems> findQuoteSheetItemsBySheetId(String sheetId) {
        return quoteSheetItemsMapper.selectQuoteSheetItemsBySheetId(sheetId);
    }

    /**
     * 获取检测项目
     *
     * @param sheetId  报价单
     * @param postName 岗位名称
     * @return 检测项目列表
     */
    @Override
    public List<QuoteSheetItems> findQuoteSheetItems(String sheetId, String postName) {
        return quoteSheetItemsMapper.selectQuoteSheetItems(sheetId, postName);
    }

    /**
     * 查询报价检测项目
     *
     * @param id 报价检测项目主键
     * @return 报价检测项目
     */
    @Override
    public QuoteSheetItems selectQuoteSheetItemsById(Long id) {
        return quoteSheetItemsMapper.selectQuoteSheetItemsById(id);
    }

    /**
     * 查询报价检测项目列表
     *
     * @param quoteSheetItems 报价检测项目
     * @return 报价检测项目
     */
    @Override
    public List<QuoteSheetItems> selectQuoteSheetItemsList(QuoteSheetItems quoteSheetItems) {
        return quoteSheetItemsMapper.selectQuoteSheetItemsList(quoteSheetItems);
    }

    /**
     * 新增报价检测项目
     *
     * @param quoteSheetItems 报价检测项目
     * @return 结果
     */
    @Override
    public int insertQuoteSheetItems(QuoteSheetItems quoteSheetItems) {
        quoteSheetItems.setCreateTime(DateUtils.getNowDate());
        return quoteSheetItemsMapper.insertQuoteSheetItems(quoteSheetItems);
    }

    /**
     * 修改报价检测项目
     *
     * @param quoteSheetItems 报价检测项目
     * @return 结果
     */
    @Override
    public int updateQuoteSheetItems(QuoteSheetItems quoteSheetItems) {
        quoteSheetItems.setUpdateTime(DateUtils.getNowDate());
        return quoteSheetItemsMapper.updateQuoteSheetItems(quoteSheetItems);
    }

    /**
     * 批量删除报价检测项目
     *
     * @param ids 需要删除的报价检测项目主键
     * @return 结果
     */
    @Override
    public int deleteQuoteSheetItemsByIds(Long[] ids) {
        return quoteSheetItemsMapper.deleteQuoteSheetItemsByIds(ids);
    }

    /**
     * 删除报价检测项目信息
     *
     * @param id 报价检测项目主键
     * @return 结果
     */
    @Override
    public int deleteQuoteSheetItemsById(Long id) {
        return quoteSheetItemsMapper.deleteQuoteSheetItemsById(id);
    }

    /**
     * 通过报价单id查询已报价岗位
     *
     * @param sheetId 报价单id
     * @return 岗位名称
     */
    @Override
    public List<String> findPostNameBySheetId(String sheetId, Long subId) {
        return quoteSheetItemsMapper.selectPostNameBySheetId(sheetId, subId);
    }

    /**
     * 设置临时文件为永久文件
     *
     * @return
     */
    @Override
    public int updateTempFlag(QuoteSheetItems quoteSheetItems) {
        return quoteSheetItemsMapper.updateTempFlag(quoteSheetItems);
    }

    /**
     * app端--报价记录--获取检测费明细
     *
     * @param quoteSheetItems 表单id、子类id
     * @return 检测费用明细
     */
    @Override
    public List<QuoteTestItemDetailsVO> findQuoteTestExpensesDetailsList(QuoteSheetItems quoteSheetItems) {
        List<QuoteTestItemDetailsVO> quoteTestIemDetailsVOList = quoteTestItemMapper.selectQuoteTestExpensesDetailsList(quoteSheetItems);
        if (CollUtil.isEmpty(quoteTestIemDetailsVOList)) {
            return quoteSheetItemsMapper.selectQuoteSheetItemsDetailsList(quoteSheetItems);
        }
        return quoteTestIemDetailsVOList;
    }

    /**
     * 获取下载地址
     *
     * @param quoteSheetInfoDTO 报价单id、python请求地址、python下载地址
     * @return url
     */
    @Override
    public String getSheetInfoDownloadUrl(QuoteSheetInfoDTO quoteSheetInfoDTO) {
        // 报价单id
        String id = quoteSheetInfoDTO.getId();
        // 获取报价单信息
        QuoteSheetInfo sheetInfo = new QuoteSheetInfo();
        sheetInfo.setId(id);
        List<QuoteSheetInfo> quoteSheetInfoList = quoteSheetInfoMapper.selectQuoteSheetInfoUserList(sheetInfo);
        if (CollUtil.isEmpty(quoteSheetInfoList)) {
            throw new RuntimeException("报价单信息不存在，请联系管理员");
        }
        QuoteSheetInfoVO quoteSheetInfoVo = new QuoteSheetInfoVO();
        // 1 报价单基本信息
        QuoteSheetInfo info = quoteSheetInfoList.get(0);
        quoteSheetInfoVo.setQuoteSheetInfo(info);
        // 2 获取客户信息
        QuoteCustomerInfo quoteCustomerInfo = quoteCustomerInfoMapper.selectQuoteCustomerInfoById(info.getCustomerId());
        QuoteCustomerInfoVO quoteCustomerInfoVO = new QuoteCustomerInfoVO();
        BeanUtils.copyProperties(quoteCustomerInfo, quoteCustomerInfoVO);
        quoteCustomerInfoVO.setFullAddress(quoteCustomerInfo.getProvince() + quoteCustomerInfo.getCity() + quoteCustomerInfo.getCounty() + quoteCustomerInfo.getAddress());
        quoteSheetInfoVo.setQuoteCustomerInfoVO(quoteCustomerInfoVO);
        // 3 获取报价项目费用详情
        QuoteExpenseDetails quoteExpenseDetails = new QuoteExpenseDetails();
        quoteExpenseDetails.setSheetId(id);
        List<QuoteExpenseDetails> quoteExpenseDetailsList = quoteExpenseDetailsMapper.selectQuoteExpenseDetailsList(quoteExpenseDetails);
        if (CollUtil.isEmpty(quoteExpenseDetailsList)) {
            throw new RuntimeException("未获取到报价收费详细信息，请联系管理员");
        }
        quoteSheetInfoVo.setQuoteExpenseDetailsList(quoteExpenseDetailsList);
        // 子类检测费用list转map<子类id，子类对应检测明细>
        Map<Long, QuoteExpenseDetails> detailsMap = quoteExpenseDetailsList.stream().collect(Collectors.toMap(QuoteExpenseDetails::getSubId, qed -> qed));
        // 4 优惠后费用总计（含税） 数字转中文
        quoteSheetInfoVo.setDiscountPriceCN(MoneyToCNFormat.formatToCN(info.getDiscountPrice().doubleValue()));
        // 5 优惠后费用总计（不含税） 数字转中文
        quoteSheetInfoVo.setExcludeTaxesPriceCN(MoneyToCNFormat.formatToCN(info.getExcludeTaxesPrice().doubleValue()));
        List<QuoteSubDetaillsVO> quoteSubDetaillsVOList = new ArrayList<>();
        // 6.1 获取项目检测费用明细(按子类划分)-职卫
        QuoteSheetItems quoteSheetItems = new QuoteSheetItems();
        quoteSheetItems.setSheetId(id);
        List<QuoteSheetItems> quoteSheetItemsList = quoteSheetItemsMapper.selectQuoteSheetItemsList(quoteSheetItems);
        if (CollUtil.isNotEmpty(quoteSheetItemsList)) {
            // 职卫 检测项list转map<子类id，对应list>
            Map<Long, List<QuoteSheetItems>> sheetItemsMap = quoteSheetItemsList.stream().collect(Collectors.groupingBy(QuoteSheetItems::getSubId));
            for (Long subId : sheetItemsMap.keySet()) {
                QuoteSubDetaillsVO quoteSubDetaillsVO = new QuoteSubDetaillsVO();
                QuoteExpenseDetails expenseDetails = detailsMap.get(subId);
                quoteSubDetaillsVO.setSubFullName(expenseDetails.getSubName());
                quoteSubDetaillsVO.setSubTestExpense(expenseDetails.getTestExpense());
                quoteSubDetaillsVO.setQuoteSheetItemsList(sheetItemsMap.get(subId));
                quoteSubDetaillsVOList.add(quoteSubDetaillsVO);
            }
        } else {
            // 6.2 获取项目检测费用明细(按子类划分)-环境、公卫
            QuoteTestItem quoteTestItem = new QuoteTestItem();
            quoteTestItem.setSheetId(id);
            List<QuoteTestItem> quoteTestItemList = quoteTestItemMapper.selectQuoteTestItemList(quoteTestItem);
            // 环境、公卫 检测项list转map<子类id，对应list>
            if (CollUtil.isNotEmpty(quoteTestItemList)) {
                Map<Long, List<QuoteTestItem>> testItemsMap = quoteTestItemList.stream().collect(Collectors.groupingBy(QuoteTestItem::getSubId));
                for (Long subId : testItemsMap.keySet()) {
                    QuoteSubDetaillsVO quoteSubDetaillsVO = new QuoteSubDetaillsVO();
                    QuoteExpenseDetails expenseDetails = detailsMap.get(subId);
                    quoteSubDetaillsVO.setSubFullName(expenseDetails.getSubName());
                    quoteSubDetaillsVO.setSubTestExpense(expenseDetails.getTestExpense());
                    quoteSubDetaillsVO.setQuoteTestItemList(testItemsMap.get(subId));
                    quoteSubDetaillsVOList.add(quoteSubDetaillsVO);
                }
            }
        }
        quoteSheetInfoVo.setQuoteSubDetaillsVOList(quoteSubDetaillsVOList);
        // 请求python接口下载报价单
        Integer type = quoteSheetInfoDTO.getType();
        Long masterCategoryId = info.getMasterCategoryId();
        String suffix;
        if (masterCategoryId == 1 && type == 1) {
            suffix = "utility/price/zhiwei/word";
        } else if (masterCategoryId == 1 && type == 2) {
            suffix = "utility/price/zhiwei/pdf";
        } else if (masterCategoryId == 2 && type == 1) {
            suffix = "utility/price/huanjing/word";
        } else if (masterCategoryId == 2 && type == 2) {
            suffix = "utility/price/huanjing/pdf";
        } else if (masterCategoryId == 3 && type == 1) {
            suffix = "utility/price/huanjing/word";
        } else if (masterCategoryId == 3 && type == 2) {
            suffix = "utility/price/huanjing/pdf";
        } else if (masterCategoryId == 4 && type == 1) {
            suffix = "utility/price/gongwei/word";
        } else if (masterCategoryId == 4 && type == 2) {
            suffix = "utility/price/gongwei/pdf";
        } else {
            throw new RuntimeException("下载类型输入错误");
        }
        String result = HttpUtil.post(quoteSheetInfoDTO.getRequestUrl() + suffix, JSONObject.toJSONString(R.data(quoteSheetInfoVo)));
        if (!result.endsWith(".pdf") && !result.endsWith(".docx")) {
            throw new RuntimeException("调用Python接口出现错误");
        }
        return result;
    }

    /**
     * 获取合同下载地址
     *
     * @param quoteSheetInfoDTO 客户id、项目名称...
     * @return url
     */
    @Override
    public String getContractDownloadUrl(QuoteSheetInfoDTO quoteSheetInfoDTO) {
        QuoteSheetInfoContractVO quoteSheetInfoContractVo = new QuoteSheetInfoContractVO();
        quoteSheetInfoContractVo.setProjectName(quoteSheetInfoDTO.getProjectName());
        String companyName = quoteSheetInfoDTO.getCompanyName();
        quoteSheetInfoContractVo.setCompanyName(companyName);
        QuoteCustomerInfo quoteCustomerInfo = quoteCustomerInfoMapper.selectQuoteCustomerInfoById(quoteSheetInfoDTO.getCustomerId());
        if (quoteCustomerInfo == null) {
            throw new RuntimeException("客户信息获取失败");
        }
        String entrustingParty = companyName + " (" + quoteCustomerInfo.getCustomerName() + ")";
        quoteSheetInfoContractVo.setEntrustingParty(entrustingParty);
        quoteSheetInfoContractVo.setCustomerAddress(quoteCustomerInfo.getProvince() + quoteCustomerInfo.getCity()
                + quoteCustomerInfo.getCounty() + quoteCustomerInfo.getAddress());
        BigDecimal discountPrice = quoteSheetInfoDTO.getDiscountPrice();
        String formatToCN = MoneyToCNFormat.formatToCN(discountPrice.doubleValue());
        quoteSheetInfoContractVo.setDiscountPrice(discountPrice);
        quoteSheetInfoContractVo.setFormatToCN(formatToCN);
        // 获取下载地址
        Integer type = quoteSheetInfoDTO.getType();
        String suffix;
        if (type == 1) {
            suffix = "utility/contract/word";
        } else if (type == 2) {
            suffix = "utility/contract/pdf";
        } else {
            throw new RuntimeException("导出类型输入错误");
        }
        String result = HttpUtil.post(quoteSheetInfoDTO.getRequestUrl() + suffix, JSONObject.toJSONString(R.data(quoteSheetInfoContractVo)));
        if (!result.endsWith(".pdf") && !result.endsWith(".docx")) {
            throw new RuntimeException("调用Python接口出现错误");
        }
        return result;
    }

    /**
     * 定时任务 删除两天前的临时数据
     *
     * @param quoteSheetItems 临时数据标记    时间（两天前）
     * @return result
     */
    @Override
    public int deleteTempSheetItem(QuoteSheetItems quoteSheetItems) {
        return quoteSheetItemsMapper.deleteTempSheetItem(quoteSheetItems);
    }
}
