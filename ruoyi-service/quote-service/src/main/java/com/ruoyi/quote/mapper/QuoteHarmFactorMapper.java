package com.ruoyi.quote.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.quote.domain.entity.QuoteHarmFactor;
import com.ruoyi.quote.domain.vo.QuoteBaseFactorVO;
import com.ruoyi.quote.domain.vo.QuoteHarmFactorVO;
import org.springframework.stereotype.Repository;

/**
 * 危害因素Mapper接口
 * 
 * @author yrb
 * @date 2022-04-27
 */
@Repository
public interface QuoteHarmFactorMapper extends BaseMapper<QuoteHarmFactor>
{
    /**
     * 查询危害因素
     * 
     * @param id 危害因素主键
     * @return 危害因素
     */
    public QuoteHarmFactor selectQuoteHarmFactorById(Long id);

    /**
     * 查询危害因素列表
     * 
     * @param quoteHarmFactor 危害因素
     * @return 危害因素集合
     */
    public List<QuoteHarmFactor> selectQuoteHarmFactorList(QuoteHarmFactor quoteHarmFactor);

    /**
     * 新增危害因素
     * 
     * @param quoteHarmFactor 危害因素
     * @return 结果
     */
    public int insertQuoteHarmFactor(QuoteHarmFactor quoteHarmFactor);

    /**
     * 批量增加危害因素
     * @param list 危害因素集合
     * @return 结果
     */
    int insertBatch(List<QuoteHarmFactor> list);

    /**
     * 修改危害因素
     * 
     * @param quoteHarmFactor 危害因素
     * @return 结果
     */
    public int updateQuoteHarmFactor(QuoteHarmFactor quoteHarmFactor);

    /**
     * 删除危害因素
     * 
     * @param id 危害因素主键
     * @return 结果
     */
    public int deleteQuoteHarmFactorById(Long id);

    /**
     * 批量删除危害因素
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteQuoteHarmFactorByIds(Long[] ids);

    /**
     * 批量删除危害因素
     *
     * @param ids 危害因素基础信息id集合
     * @return 结果
     */
    int deleteQuoteHarmFactorByBaseIds(Long[] ids);

    /**
     * 统计关联的危害因素个数
     *
     * @param ids 危害因素基础信息id集合
     * @return result
     */
    int countByBaseId(Long[] ids);

    /**
     * 通过id和postId删除关联危害因素
     *
     * @param quoteHarmFactor id postId
     * @return reslut
     */
    int deleteQuoteHarmFactorByIdAndPostId(QuoteHarmFactor quoteHarmFactor);

    /**
     * 查询危害因素列表
     *
     * @param quoteHarmFactor 危害因素
     * @return 危害因素集合
     */
    List<QuoteHarmFactorVO> selectQuoteHarmFactorUserList(QuoteHarmFactor quoteHarmFactor);

    /**
     * 查询危害因素列表（关联基础信息）
     *
     * @param quoteHarmFactorVO 岗位id 危害因素id
     * @return 危害因素集合
     */
    List<QuoteBaseFactorVO> selectQuoteHarmFactorRelationBaseFactorList(QuoteBaseFactorVO quoteHarmFactorVO);
}
