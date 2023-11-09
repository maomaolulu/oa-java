package com.ruoyi.quote.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.quote.domain.dto.QuoteRelationFactorDTO;
import com.ruoyi.quote.domain.dto.QuoteTestInfoDTO;
import com.ruoyi.quote.domain.dto.QuoteTestItemAddDTO;
import com.ruoyi.quote.domain.dto.QuoteTestItemDTO;
import com.ruoyi.quote.domain.entity.QuoteTestItem;
import com.ruoyi.quote.domain.vo.QuoteTestItemDetailsVO;
import com.ruoyi.quote.domain.vo.QuoteTestItemEditVO;
import com.ruoyi.quote.domain.vo.QuoteTestItemInfoVO;
import com.ruoyi.quote.domain.vo.QuoteTestItemTypeVO;

/**
 * (环境)子类检测项目Service接口
 * 
 * @author yrb
 * @date 2022-06-16
 */
public interface IQuoteTestItemService
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
     * 批量删除(环境)子类检测项目
     * 
     * @param ids 需要删除的(环境)子类检测项目主键集合
     * @return 结果
     */
    int deleteQuoteTestItemByIds(Long[] ids);

    /**
     * 删除(环境)子类检测项目信息
     * 
     * @param id (环境)子类检测项目主键
     * @return 结果
     */
    int deleteQuoteTestItemById(Long id);

    /**
     * （环境）批量添加检测项目
     *
     * @param quoteTestItemAddDTO
     * @return 结果
     */
    boolean addBatchTestItem(QuoteTestItemAddDTO quoteTestItemAddDTO);

    /**
     * （环境）批量编辑检测项目
     *
     * @param quoteTestItemDTOList
     * @return 结果
     */
    boolean editBatchTestItem(List<QuoteTestItemDTO> quoteTestItemDTOList);

    /**
     * 通过sheetId删除数据
     *
     * @param quoteTestItem
     * @return 结果
     */
    boolean updateTempFlag(QuoteTestItem quoteTestItem);

    /**
     * 删除用户取消操作的数据
     *
     * @param quoteTestItem 表单id、子类id、点位id
     * @return 结果
     */
    boolean deleteQuoteTestItemAddTemp(QuoteTestItem quoteTestItem);

    /**
     * 根据表单id、子类id、点位id获取检测细项列表
     *
     * @param quoteTestItem 表单id、子类id、点位id或检测类别id
     * @return 结果
     */
    List<QuoteTestItemDetailsVO> findTestItemByPointId(QuoteTestItem quoteTestItem);

    /**
     * 获取子类已报价的污染物id
     *
     * @param quoteRelationFactorDTO 表单id、子类id、检测类别id
     * @return result
     */
    List<Long> findPollutantIdListBySub(QuoteRelationFactorDTO quoteRelationFactorDTO);

    /**
     * 获取子类已报价的检测类别id
     *
     * @param quoteTestItem 表单id、子类id
     * @return result
     */
    List<Long> findPollutantTypeIdListBySub(QuoteTestItem quoteTestItem);

    /**
     * 环境、公卫-web端-编辑-获取数据
     *
     * @param quoteTestItem 表单id、子类id、点位id
     * @return 结果
     */
    QuoteTestItemEditVO findTestItemEdit(QuoteTestItem quoteTestItem);

    /**
     * 环境、公卫--删除点位和报价信息
     *
     * @param quoteTestItem 报价单id、子类id、点位id、检测类型id
     * @return 结果
     */
    boolean deleteQuoteTestItem(QuoteTestItem quoteTestItem);

    /**
     * 通过表单id、子类id、检测类别获取检测项
     *
     * @param quoteTestItem 表单id、子类id、检测类别id
     * @return result
     */
    List<QuoteTestItemTypeVO> findQuoteTestItem(QuoteTestItem quoteTestItem);

    /**
     * 公卫 子类取消报价
     *
     * @param quoteTestItem 表单id、子类id
     * @return result
     */
    boolean cancelOperrationToGW(QuoteTestItem quoteTestItem);

    /**
     * 公卫--删除检测类别
     *
     * @param quoteTestItem 报价单id、子类id、检测类型id
     * @return 结果
     */
    boolean deleteQuoteTestItemToGW(QuoteTestItem quoteTestItem);

    /**
     * 职卫、环境、公卫 行业改变 删除原有报价信息
     *
     * @param quoteTestItem 表单id
     * @return result
     */
    boolean deleteQuoteTestItemRelationInfo(QuoteTestItem quoteTestItem);

    /**
     * app端-环境-删除单个检测项目
     *
     * @param quoteTestItem 报价信息
     * @return result
     */
    boolean deleteHjTestItem(QuoteTestItem quoteTestItem);

    /**
     * app端-公卫-删除单个检测项目
     *
     * @param quoteTestItem 报价信息
     * @return result
     */
    boolean deleteGwTestItem(QuoteTestItem quoteTestItem);

    /**
     * 定时任务 删除两天前的临时数据
     *
     * @param quoteTestItem 临时数据标记    时间（两天前）
     * @return result
     */
    int deleteTempTestItem(QuoteTestItem quoteTestItem);

    /**
     * 查询已报价的检测信息
     *
     * @param quoteTestItem 表单id 子类id
     * @return result
     */
    QuoteTestItemInfoVO findTestItemInfo(QuoteTestItem quoteTestItem);

    /**
     * 获取已选择的其他检测项  缓存一般检测项
     *
     * @param quoteTestItemDTO 报价项集合
     * @return result
     */
    List<QuoteTestItemDetailsVO> findOtherTestItem(QuoteTestItemDTO quoteTestItemDTO);

    /**
     * 查询关联的检测物质信息
     *
     * @param quoteTestInfoDTO 大类id 检测性质id 检测类别id集合
     * @return result
     */
    List<QuoteTestItemDetailsVO> findTestItemInfo(QuoteTestInfoDTO quoteTestInfoDTO);

    /**
     * 提交已选择的其他检测项
     *
     * @param quoteTestItemDTO 表单id 子类id ...
     * @return result
     */
    boolean commitOtherTestItem(QuoteTestItemDTO quoteTestItemDTO);

    /**
     * 删除常规检测项
     *
     * @param quoteTestItemDTO 表单id 子类id 检测项信息
     * @return result
     */
    boolean removeNormalTestItem(QuoteTestItemDTO quoteTestItemDTO);

    /**
     * 公卫（重构） 返回上一步
     *
     * @param quoteTestItem 表单id 子类id
     * @return result
     */
    boolean revertPreviousStep(QuoteTestItem quoteTestItem);

    /**
     * 批量添加检测项
     *
     * @param quoteTestItemDTO 表单id 子类id 检测项信息
     * @return result
     */
    boolean addTestItemBatch(QuoteTestItemDTO quoteTestItemDTO);

    /**
     * 获取其他检测物质信息列表
     *
     * @param quoteTestInfoDTO 大类id 检测性质id 检测类别id集合
     * @return result
     */
    List<QuoteTestItemDetailsVO> findOtherTestItemInfoList(QuoteTestInfoDTO quoteTestInfoDTO);

    /**
     * 获取其他检测物质信息列表（web端）
     *
     * @param quoteTestInfoDTO 大类id 检测性质id 检测类别id集合
     * @return result
     */
    Map<String, Object> findOtherTestItemInfoListForWeb(QuoteTestInfoDTO quoteTestInfoDTO);

    /**
     * 缓存检测性质id
     *
     * @param quoteTestItemDTO 表单id 子类id 检测性质id
     * @return result
     */
    boolean saveTestNatureId(QuoteTestItemDTO quoteTestItemDTO);
}
