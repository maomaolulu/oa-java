package com.ruoyi.quote.service;

import java.util.List;

import com.ruoyi.quote.domain.dto.QuoteSheetInfoDTO;
import com.ruoyi.quote.domain.dto.SheetItemsDTO;
import com.ruoyi.quote.domain.entity.QuoteSheetItems;
import com.ruoyi.quote.domain.vo.QuoteTestItemDetailsVO;

/**
 * 报价检测项目Service接口
 * 
 * @author yrb
 * @date 2022-04-29
 */
public interface IQuoteSheetItemsService {

    /**
     * 新增岗位检测项目
     *
     * @param sheetItemsDTO 岗位对应的检测项目
     * @return 结果
     */
    boolean insertQuoteSheetItemsBatch(SheetItemsDTO sheetItemsDTO);

    /**
     * 根据报价单id获取检测项目费用明细
     *
     * @param sheetId 报价单id
     * @return
     */
    List<QuoteSheetItems> findQuoteSheetItemsBySheetId(String sheetId);

    /**
     * 根据报价单id获取检测项目费用明细
     *
     * @param sheetId 报价单id
     * @param postName 岗位名称
     * @return 检测项目
     */
    List<QuoteSheetItems> findQuoteSheetItems(String sheetId,String postName);

    /**
     * 查询报价检测项目
     * 
     * @param id 报价检测项目主键
     * @return 报价检测项目
     */
    public QuoteSheetItems selectQuoteSheetItemsById(Long id);

    /**
     * 查询报价检测项目列表
     * 
     * @param quoteSheetItems 报价检测项目
     * @return 报价检测项目集合
     */
    public List<QuoteSheetItems> selectQuoteSheetItemsList(QuoteSheetItems quoteSheetItems);

    /**
     * 新增报价检测项目
     * 
     * @param quoteSheetItems 报价检测项目
     * @return 结果
     */
    public int insertQuoteSheetItems(QuoteSheetItems quoteSheetItems);

    /**
     * 修改报价检测项目
     * 
     * @param quoteSheetItems 报价检测项目
     * @return 结果
     */
    public int updateQuoteSheetItems(QuoteSheetItems quoteSheetItems);

    /**
     * 批量删除报价检测项目
     * 
     * @param ids 需要删除的报价检测项目主键集合
     * @return 结果
     */
    public int deleteQuoteSheetItemsByIds(Long[] ids);

    /**
     * 删除报价检测项目信息
     * 
     * @param id 报价检测项目主键
     * @return 结果
     */
    public int deleteQuoteSheetItemsById(Long id);

    /**
     * 通过报价单id查询已报价岗位
     *
     * @param sheetId 报价单id
     * @return 岗位名称
     */
    List<String> findPostNameBySheetId(String sheetId,Long subId);

    /**
     * 设置临时文件为永久文件
     *
     * @return 1成功 0失败
     */
    int updateTempFlag(QuoteSheetItems quoteSheetItems);

    /**
     * app端--报价记录--获取检测费明细
     *
     * @param quoteSheetItems 表单id、子类id
     * @return 检测费用明细
     */
    List<QuoteTestItemDetailsVO> findQuoteTestExpensesDetailsList(QuoteSheetItems quoteSheetItems);

    /**
     * 获取报价单下载地址
     *
     * @param quoteSheetInfoDTO 报价单id、python请求地址、python下载地址
     * @return url
     */
    String getSheetInfoDownloadUrl(QuoteSheetInfoDTO quoteSheetInfoDTO);

    /**
     * 获取合同下载地址
     *
     * @param quoteSheetInfoDTO
     * @return url
     */
    String getContractDownloadUrl(QuoteSheetInfoDTO quoteSheetInfoDTO);

    /**
     * 定时任务 删除两天前的临时数据
     *
     * @param quoteSheetItems 临时数据标记    时间（两天前）
     * @return result
     */
    int deleteTempSheetItem(QuoteSheetItems quoteSheetItems);
}
