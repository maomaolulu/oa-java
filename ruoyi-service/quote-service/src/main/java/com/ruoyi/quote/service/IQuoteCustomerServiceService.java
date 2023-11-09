package com.ruoyi.quote.service;

import java.util.List;
import com.ruoyi.quote.domain.entity.QuoteCustomerService;

/**
 * 客服信息Service接口
 * 
 * @author yrb
 * @date 2022-04-28
 */
public interface IQuoteCustomerServiceService {

    /**
     * 根据公司名称查询客服代表
     *
     * @return 客服信息
     */
    QuoteCustomerService findQuoteCustomerServiceByCompanyName();

    /**
     * 查询客服信息
     * 
     * @param id 客服信息主键
     * @return 客服信息
     */
    public QuoteCustomerService selectQuoteCustomerServiceById(Long id);

    /**
     * 查询客服信息列表
     * 
     * @param quoteCustomerService 客服信息
     * @return 客服信息集合
     */
    public List<QuoteCustomerService> selectQuoteCustomerServiceList(QuoteCustomerService quoteCustomerService);

    /**
     * 新增客服信息
     * 
     * @param quoteCustomerService 客服信息
     * @return 结果
     */
    public int insertQuoteCustomerService(QuoteCustomerService quoteCustomerService);

    /**
     * 修改客服信息
     * 
     * @param quoteCustomerService 客服信息
     * @return 结果
     */
    public int updateQuoteCustomerService(QuoteCustomerService quoteCustomerService);

    /**
     * 批量删除客服信息
     * 
     * @param ids 需要删除的客服信息主键集合
     * @return 结果
     */
    public int deleteQuoteCustomerServiceByIds(Long[] ids);

    /**
     * 删除客服信息信息
     * 
     * @param id 客服信息主键
     * @return 结果
     */
    public int deleteQuoteCustomerServiceById(Long id);
}
