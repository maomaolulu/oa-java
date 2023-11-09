package com.ruoyi.quote.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.quote.domain.entity.QuoteCustomerInfo;
import org.springframework.stereotype.Repository;

/**
 * 客户信息Mapper接口
 * 
 * @author yrb
 * @date 2022-04-27
 */
@Repository
public interface QuoteCustomerInfoMapper extends BaseMapper<QuoteCustomerInfo>
{
    /**
     * 查询客户信息
     * 
     * @param id 客户信息主键
     * @return 客户信息
     */
    public QuoteCustomerInfo selectQuoteCustomerInfoById(Long id);

    /**
     * 查询客户信息列表
     * 
     * @param quoteCustomerInfo 客户信息
     * @return 客户信息集合
     */
    public List<QuoteCustomerInfo> selectQuoteCustomerInfoList(QuoteCustomerInfo quoteCustomerInfo);

    /**
     * 新增客户信息
     * 
     * @param quoteCustomerInfo 客户信息
     * @return 结果
     */
    public int insertQuoteCustomerInfo(QuoteCustomerInfo quoteCustomerInfo);

    /**
     * 修改客户信息
     * 
     * @param quoteCustomerInfo 客户信息
     * @return 结果
     */
    public int updateQuoteCustomerInfo(QuoteCustomerInfo quoteCustomerInfo);

    /**
     * 删除客户信息
     * 
     * @param id 客户信息主键
     * @return 结果
     */
    public int deleteQuoteCustomerInfoById(Long id);

    /**
     * 批量删除客户信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteQuoteCustomerInfoByIds(Long[] ids);
}
