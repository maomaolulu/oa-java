package com.ruoyi.quote.mapper;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.quote.domain.entity.QuoteTestNature;

/**
 * 检测性质Mapper接口
 * 
 * @author yrb
 * @date 2022-09-13
 */
@Repository
public interface QuoteTestNatureMapper extends BaseMapper<QuoteTestNature>
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
     * 删除检测性质
     * 
     * @param id 检测性质主键
     * @return 结果
     */
    int deleteQuoteTestNatureById(Long id);

    /**
     * 批量删除检测性质
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteQuoteTestNatureByIds(Long[] ids);
}
