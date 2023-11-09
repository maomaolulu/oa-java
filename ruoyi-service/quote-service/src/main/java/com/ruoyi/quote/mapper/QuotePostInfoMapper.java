package com.ruoyi.quote.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.quote.domain.entity.QuotePostInfo;
import com.ruoyi.quote.domain.vo.QuotePostInfoVO;
import org.springframework.stereotype.Repository;

/**
 * 岗位信息Mapper接口
 * 
 * @author yrb
 * @date 2022-04-26
 */
@Repository
public interface QuotePostInfoMapper extends BaseMapper<QuotePostInfo>
{
    /**
     * 查询岗位信息
     * 
     * @param id 岗位信息主键
     * @return 岗位信息
     */
    public QuotePostInfo selectQuotePostInfoById(Long id);

    /**
     * 查询岗位信息列表
     * 
     * @param quotePostInfo 岗位信息
     * @return 岗位信息集合
     */
    public List<QuotePostInfo> selectQuotePostInfoList(QuotePostInfo quotePostInfo);

    /**
     * 新增岗位信息
     * 
     * @param quotePostInfo 岗位信息
     * @return 结果
     */
    public int insertQuotePostInfo(QuotePostInfo quotePostInfo);

    /**
     * 修改岗位信息
     * 
     * @param quotePostInfo 岗位信息
     * @return 结果
     */
    public int updateQuotePostInfo(QuotePostInfo quotePostInfo);

    /**
     * 删除岗位信息
     * 
     * @param id 岗位信息主键
     * @return 结果
     */
    public int deleteQuotePostInfoById(Long id);

    /**
     * 批量删除岗位信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteQuotePostInfoByIds(Long[] ids);

    /**
     * 根据行业id查询岗位信息
     *
     * @param ids 需要查询的行业id集合
     * @return 结果
     */
    List<QuotePostInfo> selectQuotePostInfoByIds(Long[] ids);

    /**
     * 查询岗位信息
     *
     * @param quotePostInfo 岗位名称 行业id
     * @return 岗位信息
     */
    QuotePostInfo selectQuotePostInfoByIndustryIdAndName(QuotePostInfo quotePostInfo);

    /**
     * 查询岗位信息(筛选后)
     *
     * @param quotePostInfo 岗位实体类
     * @return 岗位信息
     */
    List<QuotePostInfo> selectQuotePostInfoUserList(QuotePostInfo quotePostInfo);

    /**
     * 查询岗位信息关联行业信息
     *
     * @param quotePostInfoVO 行业名称 岗位名称
     * @return
     */
    List<QuotePostInfoVO> selectPostInfoUserList(QuotePostInfoVO quotePostInfoVO);

    /**
     * 查询岗位信息
     *
     * @param id 岗位信息主键
     * @return 岗位信息
     */
    QuotePostInfoVO selectPostInfoById(Long id);
}
