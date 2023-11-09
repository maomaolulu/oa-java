package com.ruoyi.quote.mapper;

import java.util.List;

import com.ruoyi.quote.domain.dto.QuoteTestInfoDTO;
import com.ruoyi.quote.domain.entity.QuoteSheetItems;
import com.ruoyi.quote.domain.vo.QuoteTestItemDetailsVO;
import org.springframework.stereotype.Repository;
import com.ruoyi.quote.domain.entity.QuoteTestItem;

/**
 * (环境)子类检测项目Mapper接口
 * 
 * @author yrb
 * @date 2022-06-16
 */
@Repository
public interface QuoteTestItemMapper
{
    /**
     * 查询(环境)子类检测项目
     * 
     * @param id (环境)子类检测项目主键
     * @return (环境)子类检测项目
     */
    QuoteTestItem selectQuoteTestItemById(Long id);

    /**
     * 查询(环境)子类检测项目列表
     * 
     * @param quoteTestItem (环境)子类检测项目
     * @return (环境)子类检测项目集合
     */
    List<QuoteTestItem> selectQuoteTestItemList(QuoteTestItem quoteTestItem);

    /**
     * 新增(环境)子类检测项目
     * 
     * @param quoteTestItem (环境)子类检测项目
     * @return 结果
     */
    int insertQuoteTestItem(QuoteTestItem quoteTestItem);

    /**
     * 修改(环境)子类检测项目
     * 
     * @param quoteTestItem (环境)子类检测项目
     * @return 结果
     */
    int updateQuoteTestItem(QuoteTestItem quoteTestItem);

    /**
     * 删除(环境)子类检测项目
     * 
     * @param id (环境)子类检测项目主键
     * @return 结果
     */
    int deleteQuoteTestItemById(Long id);

    /**
     * 批量删除(环境)子类检测项目
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteQuoteTestItemByIds(Long[] ids);

    /**
     * 通过sheetId删除数据
     *
     * @param quoteTestItem
     * @return 结果
     */
    int updateTempFlag(QuoteTestItem quoteTestItem);

    /**
     * 获取已报价的污染物id
     *
     * @param quoteTestItem
     * @return
     */
    List<Long> selectPollutantIdList(QuoteTestItem quoteTestItem);

    /**
     * 删除用户取消操作的数据
     *
     * @param quoteTestItem 表单id、子类id、点位名称
     * @return
     */
    int deleteQuoteTestItemAddTemp(QuoteTestItem quoteTestItem);

    /**
     * 根据表单id、子类id、点位名称获取检测细项列表
     *
     * @param quoteTestItem 表单id、子类id、点位id或检测类别id
     * @return 结果
     */
    List<QuoteTestItemDetailsVO> selectTestItemByPointId(QuoteTestItem quoteTestItem);

    /**
     * 获取子类已报价的污染物id
     *
     * @param quoteTestItem 表单id、子类id、检测类型id
     * @return
     */
    List<Long> selectPollutantIdListBySub(QuoteTestItem quoteTestItem);

    /**
     * 获取子类已报价的检测类别id
     *
     * @param quoteTestItem 表单id、子类id
     * @return
     */
    List<Long> selectPollutantTypeIdListBySub(QuoteTestItem quoteTestItem);

    /**
     * app端--报价记录--获取检测费明细
     *
     * @param quoteSheetItems 表单id、子类id
     * @return 检测费用明细
     */
    List<QuoteTestItemDetailsVO> selectQuoteTestExpensesDetailsList(QuoteSheetItems quoteSheetItems);

    /**
     * 定时任务 删除两天前的临时数据
     *
     * @param quoteTestItem 临时数据标记    时间（两天前）
     * @return result
     */
    int deleteTempTestItem(QuoteTestItem quoteTestItem);

    /**
     * 查询关联的检测物质信息
     *
     * @param quoteTestInfoDTO 大类id 检测性质id 检测类别id集合
     * @return result
     */
    List<QuoteTestItemDetailsVO> selectTestItemInfo(QuoteTestInfoDTO quoteTestInfoDTO);

    /**
     * 查询其他检测物质信息列表
     *
     * @param quoteTestInfoDTO 大类id 检测性质id 检测类别id集合
     * @return result
     */
    List<QuoteTestItemDetailsVO> selectOtherTestItemInfoList(QuoteTestInfoDTO quoteTestInfoDTO);

    /**
     * 公卫重构 批量添加检测项
     *
     * @param list 检测项集合
     * @return result
     */
    int addTestItemBatch(List<QuoteTestItemDetailsVO> list);
}
