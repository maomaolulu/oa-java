package com.ruoyi.quote.mapper;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.ruoyi.quote.domain.entity.QuotePointInfo;

/**
 * 点位信息Mapper接口
 * 
 * @author yrb
 * @date 2022-06-17
 */
@Repository
public interface QuotePointInfoMapper
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
     * 删除点位信息
     * 
     * @param id 点位信息主键
     * @return 结果
     */
    int deleteQuotePointInfoById(Long id);

    /**
     * 批量删除点位信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteQuotePointInfoByIds(Long[] ids);

    /**
     * 通过表单id删除检测点位信息
     *
     * @param sheetId 表单id
     */
    int deleteQuotePointInfoBySheetId(String sheetId);

    /**
     * 删除点位信息
     *
     * @param quotePointInfo 表单id、子类id、点位id、检测类别id
     */
    int deleteQuotePointInfo(QuotePointInfo quotePointInfo);

    /**
     * 删除点位信息临时数据
     *
     * @param quotePointInfo 创建时间（2天前）
     * @return
     */
    int deleteTempPointInfo(QuotePointInfo quotePointInfo);
}
