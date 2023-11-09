package com.ruoyi.quote.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.mapper.QuoteCustomerInfoMapper;
import com.ruoyi.quote.domain.entity.QuoteCustomerInfo;
import com.ruoyi.quote.service.IQuoteCustomerInfoService;

/**
 * 客户信息Service业务层处理
 *
 * @author yrb
 * @date 2022-04-27
 */
@Service
public class QuoteCustomerInfoServiceImpl implements IQuoteCustomerInfoService {

    private final QuoteCustomerInfoMapper quoteCustomerInfoMapper;

    @Autowired
    public QuoteCustomerInfoServiceImpl(QuoteCustomerInfoMapper quoteCustomerInfoMapper) {
        this.quoteCustomerInfoMapper = quoteCustomerInfoMapper;
    }

    /**
     * 查询客户信息
     *
     * @param id 客户信息主键
     * @return 客户信息
     */
    @Override
    public QuoteCustomerInfo selectQuoteCustomerInfoById(Long id) {
        return quoteCustomerInfoMapper.selectQuoteCustomerInfoById(id);
    }

    /**
     * 查询客户信息列表
     *
     * @param quoteCustomerInfo 客户信息
     * @return 客户信息
     */
    @Override
    public List<QuoteCustomerInfo> selectQuoteCustomerInfoList(QuoteCustomerInfo quoteCustomerInfo) {
        return quoteCustomerInfoMapper.selectQuoteCustomerInfoList(quoteCustomerInfo);
    }

    /**
     * 新增客户信息
     *
     * @param quoteCustomerInfo 客户信息
     * @return 结果
     */
    @Override
    public int insertQuoteCustomerInfo(QuoteCustomerInfo quoteCustomerInfo) {
        quoteCustomerInfo.setCreateTime(DateUtils.getNowDate());
        // 添加业务员信息
        quoteCustomerInfo.setSalesmanId(SystemUtil.getUserId());
        quoteCustomerInfo.setSalesman(SystemUtil.getUserNameCn());
        return quoteCustomerInfoMapper.insertQuoteCustomerInfo(quoteCustomerInfo);
    }


    /**
     * 修改客户信息
     *
     * @param quoteCustomerInfo 客户信息
     * @return 结果
     */
    @Override
    public int updateQuoteCustomerInfo(QuoteCustomerInfo quoteCustomerInfo) {
        quoteCustomerInfo.setUpdateTime(DateUtils.getNowDate());
        return quoteCustomerInfoMapper.updateQuoteCustomerInfo(quoteCustomerInfo);
    }

    /**
     * 批量删除客户信息
     *
     * @param ids 需要删除的客户信息主键
     * @return 结果
     */
    @Override
    public int deleteQuoteCustomerInfoByIds(Long[] ids) {
        return quoteCustomerInfoMapper.deleteQuoteCustomerInfoByIds(ids);
    }

    /**
     * 删除客户信息信息
     *
     * @param id 客户信息主键
     * @return 结果
     */
    @Override
    public int deleteQuoteCustomerInfoById(Long id) {
        return quoteCustomerInfoMapper.deleteQuoteCustomerInfoById(id);
    }
}
