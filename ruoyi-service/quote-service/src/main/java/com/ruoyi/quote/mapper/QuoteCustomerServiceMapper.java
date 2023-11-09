package com.ruoyi.quote.mapper;

import java.util.List;
import com.ruoyi.quote.domain.entity.QuoteCustomerService;
import org.springframework.stereotype.Repository;

/**
 * 客服信息Mapper接口
 * 
 * @author yrb
 * @date 2022-04-28
 */
@Repository
public interface QuoteCustomerServiceMapper {
    /**
     * 通过公司名称查找客服信息
     *
     * @param companyName 公司名称
     * @return 客服信息
     */
    QuoteCustomerService selectQuoteCustomerServiceByCompanyName(String companyName);

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
     * 删除客服信息
     * 
     * @param id 客服信息主键
     * @return 结果
     */
    public int deleteQuoteCustomerServiceById(Long id);

    /**
     * 批量删除客服信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteQuoteCustomerServiceByIds(Long[] ids);
}
