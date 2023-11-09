package com.ruoyi.quote.mapper;

import java.util.List;

import com.ruoyi.quote.domain.entity.QuoteSheetItems;
import com.ruoyi.quote.domain.vo.QuoteTestItemDetailsVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 报价检测项目Mapper接口
 *
 * @author yrb
 * @date 2022-04-29
 */
@Repository
public interface QuoteSheetItemsMapper {
    /**
     * 通过报价单id删除检测项目
     *
     * @param quoteSheetItems 报价单id、岗位名称、子类id
     */
    int deleteQuoteSheetItemsBySheetId(QuoteSheetItems quoteSheetItems);

    /**
     * 查询报价检测项目
     *
     * @param id 报价检测项目主键
     * @return 报价检测项目
     */
    public QuoteSheetItems selectQuoteSheetItemsById(Long id);

    /**
     * 根据报价单id获取检测项目费用明细
     *
     * @param sheetId 报价单id
     * @return
     */
    List<QuoteSheetItems> selectQuoteSheetItemsBySheetId(String sheetId);

    /**
     * 获取检测项目
     *
     * @param quoteSheetItems 报价单id 岗位名称
     * @return 检测项目列表
     */
    List<QuoteSheetItems> selectQuoteSheetItemsBySubId(QuoteSheetItems quoteSheetItems);

    /**
     * 获取检测项目
     *
     * @param sheetId  报价单
     * @param postName 岗位名称
     * @return 检测项目列表
     */
    List<QuoteSheetItems> selectQuoteSheetItems(@Param("sheetId") String sheetId, @Param("postName") String postName);

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
     * 删除报价检测项目
     *
     * @param id 报价检测项目主键
     * @return 结果
     */
    public int deleteQuoteSheetItemsById(Long id);

    /**
     * 批量删除报价检测项目
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteQuoteSheetItemsByIds(Long[] ids);

    /**
     * 通过表单id获取岗位名称
     *
     * @param sheetId 表单id
     * @return 岗位名称
     */
    List<String> selectPostNameBySheetId(@Param("sheetId")String sheetId,@Param("subId")Long subId);

    /**
     * 设置临时文件为永久文件
     *
     * @return
     */
    int updateTempFlag(QuoteSheetItems quoteSheetItems);

    /**
     * 查询报价检测项目列表
     *
     * @param quoteSheetItems 报价检测项目
     * @return 报价检测项目集合
     */
    List<QuoteTestItemDetailsVO> selectQuoteSheetItemsDetailsList(QuoteSheetItems quoteSheetItems);

    /**
     * 删除检测信息
     *
     * @param quoteSheetItems 表单id
     * @return result
     */
    int deleteQuoteSheetItems(QuoteSheetItems quoteSheetItems);

    /**
     * 定时任务 删除两天前的临时数据
     *
     * @param quoteSheetItems 临时数据标记    时间（两天前）
     * @return result
     */
    int deleteTempSheetItem(QuoteSheetItems quoteSheetItems);
}
