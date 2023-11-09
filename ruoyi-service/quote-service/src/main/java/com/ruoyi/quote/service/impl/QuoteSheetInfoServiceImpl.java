package com.ruoyi.quote.service.impl;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.quote.domain.entity.*;
import com.ruoyi.quote.mapper.QuotePointInfoMapper;
import com.ruoyi.quote.mapper.QuotePostItemsMapper;
import com.ruoyi.quote.service.IQuoteExpenseDetailsService;
import com.ruoyi.quote.service.IQuoteSheetItemsService;
import com.ruoyi.quote.service.IQuoteTestItemService;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.quote.mapper.QuoteExpenseDetailsMapper;
import com.ruoyi.quote.utils.QuoteUtil;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.mapper.QuoteSheetInfoMapper;
import com.ruoyi.quote.service.IQuoteSheetInfoService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 报价单信息Service业务层处理
 *
 * @author yrb
 * @date 2022-04-29
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class QuoteSheetInfoServiceImpl implements IQuoteSheetInfoService {
    private final QuoteSheetInfoMapper quoteSheetInfoMapper;
    private final RemoteUserService remoteUserService;
    private final QuoteExpenseDetailsMapper quoteExpenseDetailsMapper;
    private final IQuoteExpenseDetailsService quoteExpenseDetailsService;
    private final IQuoteSheetItemsService quoteSheetItemsService;
    private final IQuoteTestItemService quoteTestItemService;
    private final QuotePointInfoMapper quotePointInfoMapper;
    private final QuotePostItemsMapper quotePostItemsMapper;

    @Autowired
    public QuoteSheetInfoServiceImpl(QuoteSheetInfoMapper quoteSheetInfoMapper,
                                     RemoteUserService remoteUserService,
                                     QuoteExpenseDetailsMapper quoteExpenseDetailsMapper,
                                     IQuoteExpenseDetailsService quoteExpenseDetailsService,
                                     IQuoteSheetItemsService quoteSheetItemsService,
                                     IQuoteTestItemService quoteTestItemService,
                                     QuotePointInfoMapper quotePointInfoMapper,
                                     QuotePostItemsMapper quotePostItemsMapper) {
        this.quoteSheetInfoMapper = quoteSheetInfoMapper;
        this.remoteUserService = remoteUserService;
        this.quoteExpenseDetailsMapper = quoteExpenseDetailsMapper;
        this.quoteExpenseDetailsService = quoteExpenseDetailsService;
        this.quoteSheetItemsService = quoteSheetItemsService;
        this.quoteTestItemService = quoteTestItemService;
        this.quotePointInfoMapper = quotePointInfoMapper;
        this.quotePostItemsMapper = quotePostItemsMapper;
    }

    /**
     * 查询报价单信息
     *
     * @param id 报价单信息主键
     * @return 报价单信息
     */
    @Override
    public QuoteSheetInfo selectQuoteSheetInfoById(String id) {
        return quoteSheetInfoMapper.selectQuoteSheetInfoById(id);
    }

    /**
     * 查询报价单信息列表
     *
     * @param quoteSheetInfo 报价单信息
     * @return 报价单信息
     */
    @Override
    public List<QuoteSheetInfo> selectQuoteSheetInfoList(QuoteSheetInfo quoteSheetInfo) {
        // 前端客户名称输入框传入字段customerName，后端对应companyName
        String customerName = quoteSheetInfo.getCustomerName();
        if (StrUtil.isNotBlank(customerName)){
            quoteSheetInfo.setCompanyName(customerName);
            quoteSheetInfo.setCustomerName(null);
        }
        return quoteSheetInfoMapper.selectQuoteSheetInfoUserList(quoteSheetInfo);
    }

    /**
     * 新增报价单信息
     *
     * @param quoteSheetInfo 报价单信息
     * @return 结果
     */
    @Override
    public int insertQuoteSheetInfo(QuoteSheetInfo quoteSheetInfo) throws RuntimeException {
        // 报价人
        SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
        String salesInfo = sysUser.getCompany() + "-" + sysUser.getDept().getDeptName() + "-" + sysUser.getUserName();
        quoteSheetInfo.setSalesInfo(salesInfo);
        // 报价项目
        String masterAbbreviation = quoteSheetInfo.getMasterAbbreviation();
        String sheetInfoId = quoteSheetInfo.getId();
        List<QuoteExpenseDetails> quoteExpenseDetailsList = quoteExpenseDetailsMapper.selectSubAbbBySheetId(sheetInfoId);
        List<String> fullNameList = new ArrayList<>();
        List<String> abbNameList = new ArrayList<>();
        if (CollUtil.isEmpty(quoteExpenseDetailsList)) {
            throw new RuntimeException("报价子类详细信息不存在！！！");
        }
        for (QuoteExpenseDetails quoteExpenseDetails : quoteExpenseDetailsList) {
            fullNameList.add(quoteExpenseDetails.getSubName());
            abbNameList.add(quoteExpenseDetails.getSubName());
        }
        String quoteProject = QuoteUtil.getQuoteProject(masterAbbreviation, abbNameList);
        quoteSheetInfo.setQuoteProject(quoteProject);
        // 生成合同中的项目名称
        String quoteProjectName = QuoteUtil.getFormatString(fullNameList,"、");
        quoteSheetInfo.setProjectName(quoteProjectName);
        // 生成记录编号
        String recordCode = "BJ" + DateUtils.getFormatDateNow() + QuoteUtil.getRandomNumber(10);
        quoteSheetInfo.setRecordCode(recordCode);
        // 获取当前时间
        quoteSheetInfo.setCreateTime(DateUtils.getNowDate());
        int i = quoteSheetInfoMapper.insertQuoteSheetInfo(quoteSheetInfo);
        // 将临时文件设置为永久
        if (i > 0) {
            // 报价费用详细信息设置为永久
            QuoteExpenseDetails quoteExpenseDetails = new QuoteExpenseDetails();
            quoteExpenseDetails.setSheetId(sheetInfoId);
            quoteExpenseDetails.setTempFlag(QuoteUtil.TEMP_FLAG);
            if (quoteExpenseDetailsService.updateTempFlag(quoteExpenseDetails) < 0) {
                throw new RuntimeException("更新检测费用明细临时状态失败！");
            }
            // 职卫 临时文件设置为永久
            QuoteSheetItems quoteSheetItems = new QuoteSheetItems();
            quoteSheetItems.setSheetId(sheetInfoId);
            List<QuoteSheetItems> quoteSheetItemsList = quoteSheetItemsService.selectQuoteSheetItemsList(quoteSheetItems);
            if (CollUtil.isNotEmpty(quoteSheetItemsList)) {
                quoteSheetItems.setTempFlag(QuoteUtil.TEMP_FLAG);
                if (quoteSheetItemsService.updateTempFlag(quoteSheetItems) < 0) {
                    throw new RuntimeException("更新报价单细项临时状态失败！");
                }
            }
            // 环境、公卫 临时文件设置为永久
            QuoteTestItem quoteTestItem = new QuoteTestItem();
            quoteTestItem.setSheetId(sheetInfoId);
            List<QuoteTestItem> quoteTestItemList = quoteTestItemService.selectQuoteTestItemList(quoteTestItem);
            if (CollUtil.isNotEmpty(quoteTestItemList)) {
                quoteTestItem.setTempFlag(QuoteUtil.TEMP_FLAG);
                if (!quoteTestItemService.updateTempFlag(quoteTestItem)) {
                    throw new RuntimeException("更新报价单细项临时状态失败！");
                }
            }
            // 删除点位信息
            QuotePointInfo pointInfo = new QuotePointInfo();
            pointInfo.setSheetId(sheetInfoId);
            List<QuotePointInfo> quotePointInfoList = quotePointInfoMapper.selectQuotePointInfoList(pointInfo);
            if (CollUtil.isNotEmpty(quotePointInfoList)) {
                if (quotePointInfoMapper.deleteQuotePointInfoBySheetId(sheetInfoId) == 0) {
                    throw new RuntimeException("删除点位信息失败");
                }
            }
            // 删除职卫岗位报价临时信息
            QuotePostItems quotePostItems = new QuotePostItems();
            quotePostItems.setSheetId(sheetInfoId);
            List<QuotePostItems> postItemsList = quotePostItemsMapper.selectQuotePostItemsList(quotePostItems);
            if (CollUtil.isNotEmpty(postItemsList)) {
                if (quotePostItemsMapper.deleteQuotePostItems(quotePostItems)==0){
                    throw new RuntimeException("删除岗位报价临时信息失败");
                }
            }
        }
        return i;
    }

    /**
     * 修改报价单信息
     *
     * @param quoteSheetInfo 报价单信息
     * @return 结果
     */
    @Override
    public int updateQuoteSheetInfo(QuoteSheetInfo quoteSheetInfo) {
        quoteSheetInfo.setUpdateTime(DateUtils.getNowDate());
        return quoteSheetInfoMapper.updateQuoteSheetInfo(quoteSheetInfo);
    }

    /**
     * 批量删除报价单信息
     *
     * @param ids 需要删除的报价单信息主键
     * @return 结果
     */
    @Override
    public int deleteQuoteSheetInfoByIds(String[] ids) {
        return quoteSheetInfoMapper.deleteQuoteSheetInfoByIds(ids);
    }

    /**
     * 删除报价单信息信息
     *
     * @param id 报价单信息主键
     * @return 结果
     */
    @Override
    public int deleteQuoteSheetInfoById(String id) {
        return quoteSheetInfoMapper.deleteQuoteSheetInfoById(id);
    }
}
