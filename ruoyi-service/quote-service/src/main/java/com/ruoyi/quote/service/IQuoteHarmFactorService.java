package com.ruoyi.quote.service;

import java.math.BigDecimal;
import java.util.List;

import com.ruoyi.quote.domain.dto.QuoteBaseFactorDTO;
import com.ruoyi.quote.domain.entity.QuoteHarmFactor;
import com.ruoyi.quote.domain.entity.QuoteSheetItems;
import com.ruoyi.quote.domain.vo.QuoteBaseFactorVO;
import com.ruoyi.quote.domain.vo.QuoteHarmFactorVO;

/**
 * 危害因素Service接口
 * 
 * @author yrb
 * @date 2022-04-27
 */
public interface IQuoteHarmFactorService
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
     * @param quoteBaseFactorVO 危害因素
     * @return 危害因素集合
     */
    List<QuoteBaseFactorVO> selectQuoteHarmFactorList(QuoteBaseFactorVO quoteBaseFactorVO);

    /**
     * 查询危害因素列表
     *
     * @param quoteHarmFactor 危害因素
     * @return 危害因素集合
     */
    List<QuoteHarmFactorVO> selectQuoteHarmFactorUserList(QuoteHarmFactor quoteHarmFactor);

    /**
     * 新增危害因素
     * 
     * @param quoteHarmFactor 危害因素
     * @return 结果
     */
    public int insertQuoteHarmFactor(QuoteHarmFactor quoteHarmFactor);

    /**
     * 修改危害因素
     * 
     * @param quoteHarmFactor 危害因素
     * @return 结果
     */
    public int updateQuoteHarmFactor(QuoteHarmFactor quoteHarmFactor);

    /**
     * 批量删除危害因素
     * 
     * @param ids 需要删除的危害因素主键集合
     * @return 结果
     */
    public int deleteQuoteHarmFactorByIds(Long[] ids);

    /**
     * 删除危害因素信息
     * 
     * @param id 危害因素主键
     * @return 结果
     */
    public int deleteQuoteHarmFactorById(Long id);

    /**
     * 查询危害因素列表 通过岗位id
     *
     * @param quoteBaseFactorVO 岗位id 已选危害因素id集合
     * @return 危害因素集合
     */
    List<QuoteBaseFactorVO> findQuoteHarmFactorAppList(QuoteBaseFactorVO quoteBaseFactorVO);

    /**
     * 查询危害因素列表 置顶列表 岗位对应
     *
     * @param quoteHarmFactorVO 表单id 子类id
     * @return 危害因素集合
     */
    List<QuoteBaseFactorVO> findQuoteHarmFactorAppTopList(QuoteHarmFactorVO quoteHarmFactorVO);

    /**
     * 查询危害因素列表 置顶列表 所有
     *
     * @param quoteHarmFactorVO 表单id 子类id
     * @return 危害因素集合
     */
    List<QuoteBaseFactorVO> findQuoteHarmFactorAppTopBaseList(QuoteHarmFactorVO quoteHarmFactorVO);

    /**
     * 查询危害因素列表 置顶列表 所有
     *
     * @param quoteBaseFactorVO 表单id 子类id
     * @return 危害因素集合
     */
    List<QuoteBaseFactorVO> findQuoteHarmFactorAppBaseList(QuoteBaseFactorVO quoteBaseFactorVO);

    /**
     * 更新缓存
     *
     * @param quoteBaseFactorDTO 危害因素信息
     * @return result
     */
    boolean updateCache(QuoteBaseFactorDTO quoteBaseFactorDTO);

    /**
     * 从危害因素基础信息中选择未关联岗位的危害因素
     *
     * @param quoteBaseFactorDTO
     * @return result
     */
    boolean addFactorFromBaseHarmFactor(QuoteBaseFactorDTO quoteBaseFactorDTO);

    /**
     * 获取点位数
     *
     * @param quoteSheetItems 表单id、子类id、岗位id
     * @return result
     */
    BigDecimal findPointNumber(QuoteSheetItems quoteSheetItems);

    /**
     * 恢复缓存 职卫报价
     *
     * @param quoteHarmFactorVO 表单id 子类id
     * @return 危害因素集合
     */
    boolean recoverCache(QuoteHarmFactorVO quoteHarmFactorVO);
}
