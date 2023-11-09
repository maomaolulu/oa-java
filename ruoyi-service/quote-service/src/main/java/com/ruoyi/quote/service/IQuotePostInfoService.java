package com.ruoyi.quote.service;

import java.util.List;

import com.ruoyi.quote.domain.dto.PostHarmFactorDTO;
import com.ruoyi.quote.domain.entity.QuotePostInfo;
import com.ruoyi.quote.domain.vo.QuotePostInfoVO;

/**
 * 岗位信息Service接口
 * 
 * @author yrb
 * @date 2022-04-26
 */
public interface IQuotePostInfoService 
{
    /**
     * 查询岗位信息
     * 
     * @param id 岗位信息主键
     * @return 岗位信息
     */
    QuotePostInfo selectQuotePostInfoById(Long id);

    /**
     * 查询岗位信息列表
     * 
     * @param quotePostInfoVO 岗位信息
     * @return 岗位信息集合
     */
    List<QuotePostInfoVO> selectQuotePostInfoList(QuotePostInfoVO quotePostInfoVO);

    /**
     * 新增岗位信息
     * 
     * @param quotePostInfo 岗位信息
     * @return 结果
     */
    int insertQuotePostInfo(QuotePostInfo quotePostInfo);

    /**
     * 新增岗位信息及危害因素
     *
     * @param postHarmFactorDTO 岗位信息及危害因素集合
     * @return 结果
     */
    boolean insertBatch(PostHarmFactorDTO postHarmFactorDTO);

    /**
     * 修改岗位信息
     * 
     * @param quotePostInfo 岗位信息
     * @return 结果
     */
    int updateQuotePostInfo(QuotePostInfo quotePostInfo);

    /**
     * 修改岗位信息关联危害因素
     *
     * @param postHarmFactorDTO 岗位及危害因素信息
     * @return 结果
     */
    int updateQuotePostInfoWithHarmFactor(PostHarmFactorDTO postHarmFactorDTO);

    /**
     * 批量删除岗位信息
     * 
     * @param ids 需要删除的岗位信息主键集合
     * @return 结果
     */
    int deleteQuotePostInfoByIds(Long[] ids);

    /**
     * 删除岗位信息信息
     * 
     * @param id 岗位信息主键
     * @return 结果
     */
    int deleteQuotePostInfoById(Long id);

    /**
     * 根据行业id查询岗位信息
     *
     * @return 岗位列表
     */
    List<QuotePostInfo> selectQuotePostInfoByIds(Long[] ids);

    /**
     * 查询岗位信息
     *
     * @param quotePostInfo 岗位名称 行业id
     * @return 岗位信息
     */
    QuotePostInfo findQuotePostInfoByIndustryIdAndName(QuotePostInfo quotePostInfo);

    /**
     * 查询岗位信息(筛选后)
     *
     * @param quotePostInfo 岗位
     * @return 岗位信息
     */
    List<QuotePostInfo> findQuotePostInfoUserList(QuotePostInfo quotePostInfo);
}
