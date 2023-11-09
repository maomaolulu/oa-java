package com.ruoyi.quote.service;

import java.util.List;
import com.ruoyi.quote.domain.entity.QuotePointInfo;

/**
 * 点位信息Service接口
 * 
 * @author yrb
 * @date 2022-06-17
 */
public interface IQuotePointInfoService 
{
    /**
     * 查询点位信息
     * 
     * @param id 点位信息主键
     * @return 点位信息
     */
    QuotePointInfo selectQuotePointInfoById(Long id);

    /**
     * 查询点位信息列表
     * 
     * @param quotePointInfo 点位信息
     * @return 点位信息集合
     */
    List<QuotePointInfo> selectQuotePointInfoList(QuotePointInfo quotePointInfo);

    /**
     * 新增点位信息
     * 
     * @param quotePointInfo 点位信息
     * @return 结果
     */
    int insertQuotePointInfo(QuotePointInfo quotePointInfo);

    /**
     * 修改点位信息
     * 
     * @param quotePointInfo 点位信息
     * @return 结果
     */
    int updateQuotePointInfo(QuotePointInfo quotePointInfo);

    /**
     * 批量删除点位信息
     * 
     * @param ids 需要删除的点位信息主键集合
     * @return 结果
     */
    int deleteQuotePointInfoByIds(Long[] ids);

    /**
     * 删除点位信息信息
     * 
     * @param id 点位信息主键
     * @return 结果
     */
    int deleteQuotePointInfoById(Long id);

    /**
     * 删除点位信息及关联的检测项目信息
     *
     * @param quotePointInfo 点位主键id、表单id、子类id
     * @return 结果
     */
    boolean deleteQuotePointInfoRelationTestItem(QuotePointInfo quotePointInfo);

    /**
     * 新增点位信息 返回带主键id的实体类
     *
     * @param quotePointInfo 点位信息
     * @return 结果
     */
    QuotePointInfo insertQuotePointInfoReturnResult(QuotePointInfo quotePointInfo);
}
