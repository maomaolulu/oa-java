package com.ruoyi.quote.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.mapper.QuoteCustomerServiceMapper;
import com.ruoyi.quote.domain.entity.QuoteCustomerService;
import com.ruoyi.quote.service.IQuoteCustomerServiceService;

/**
 * 客服信息Service业务层处理
 *
 * @author yrb
 * @date 2022-04-28
 */
@Service
public class QuoteCustomerServiceServiceImpl implements IQuoteCustomerServiceService {
    private final QuoteCustomerServiceMapper quoteCustomerServiceMapper;
    private final RemoteUserService remoteUserService;

    @Autowired
    public QuoteCustomerServiceServiceImpl(QuoteCustomerServiceMapper quoteCustomerServiceMapper,
                                           RemoteUserService remoteUserService) {
        this.quoteCustomerServiceMapper = quoteCustomerServiceMapper;
        this.remoteUserService = remoteUserService;
    }

    /**
     * 根据公司名称查询客服代表
     *
     * @return 客服信息
     */
    @Override
    public QuoteCustomerService findQuoteCustomerServiceByCompanyName() {
        SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
        return quoteCustomerServiceMapper.selectQuoteCustomerServiceByCompanyName(sysUser.getCompany());
    }

    /**
     * 查询客服信息
     *
     * @param id 客服信息主键
     * @return 客服信息
     */
    @Override
    public QuoteCustomerService selectQuoteCustomerServiceById(Long id) {
        return quoteCustomerServiceMapper.selectQuoteCustomerServiceById(id);
    }

    /**
     * 查询客服信息列表
     *
     * @param quoteCustomerService 客服信息
     * @return 客服信息
     */
    @Override
    public List<QuoteCustomerService> selectQuoteCustomerServiceList(QuoteCustomerService quoteCustomerService) {
        return quoteCustomerServiceMapper.selectQuoteCustomerServiceList(quoteCustomerService);
    }

    /**
     * 新增客服信息
     *
     * @param quoteCustomerService 客服信息
     * @return 结果
     */
    @Override
    public int insertQuoteCustomerService(QuoteCustomerService quoteCustomerService) {
        // 校验一个公司只能有一个客服代表
        QuoteCustomerService qcs = quoteCustomerServiceMapper.selectQuoteCustomerServiceByCompanyName(quoteCustomerService.getCompanyName());
        if (null != qcs) {
            throw new RuntimeException(quoteCustomerService.getCompanyName() + "客服代表已存在，请勿重复添加！");
        }
        quoteCustomerService.setCreateTime(DateUtils.getNowDate());
        return quoteCustomerServiceMapper.insertQuoteCustomerService(quoteCustomerService);
    }

    /**
     * 修改客服信息
     *
     * @param quoteCustomerService 客服信息
     * @return 结果
     */
    @Override
    public int updateQuoteCustomerService(QuoteCustomerService quoteCustomerService) {
        quoteCustomerService.setUpdateTime(DateUtils.getNowDate());
        return quoteCustomerServiceMapper.updateQuoteCustomerService(quoteCustomerService);
    }

    /**
     * 批量删除客服信息
     *
     * @param ids 需要删除的客服信息主键
     * @return 结果
     */
    @Override
    public int deleteQuoteCustomerServiceByIds(Long[] ids) {
        return quoteCustomerServiceMapper.deleteQuoteCustomerServiceByIds(ids);
    }

    /**
     * 删除客服信息信息
     *
     * @param id 客服信息主键
     * @return 结果
     */
    @Override
    public int deleteQuoteCustomerServiceById(Long id) {
        return quoteCustomerServiceMapper.deleteQuoteCustomerServiceById(id);
    }
}
