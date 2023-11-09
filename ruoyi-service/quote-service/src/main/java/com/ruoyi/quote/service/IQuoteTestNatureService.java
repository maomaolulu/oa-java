package com.ruoyi.quote.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.quote.domain.entity.QuoteTestNature;

/**
 * 检测性质Service接口
 * 
 * @author yrb
 * @date 2022-09-13
 */
public interface IQuoteTestNatureService extends IService<QuoteTestNature>
{
    /**
     * 查询检测性质
     * 
     * @param id 检测性质主键
     * @return 检测性质
     */
    QuoteTestNature selectQuoteTestNatureById(Long id);

    /**
     * 查询检测性质列表
     * 
     * @param quoteTestNature 检测性质
     * @return 检测性质集合
     */
    List<QuoteTestNature> selectQuoteTestNatureList(QuoteTestNature quoteTestNature);

    /**
     * 新增检测性质
     * 
     * @param quoteTestNature 检测性质
     * @return 结果
     */
    int insertQuoteTestNature(QuoteTestNature quoteTestNature);

    /**
     * 修改检测性质
     * 
     * @param quoteTestNature 检测性质
     * @return 结果
     */
    int updateQuoteTestNature(QuoteTestNature quoteTestNature);

    /**
     * 批量删除检测性质
     * 
     * @param ids 需要删除的检测性质主键集合
     * @return 结果
     */
    int deleteQuoteTestNatureByIds(Long[] ids);

    /**
     * 删除检测性质信息
     * 
     * @param id 检测性质主键
     * @return 结果
     */
    int deleteQuoteTestNatureById(Long id);

    /**
     * 标记选中的检测性质
     *
     * @param natureIds 检测性质id集合
     * @return result
     */
    List<QuoteTestNature> findQuoteTestNatureList(String natureIds);
}
