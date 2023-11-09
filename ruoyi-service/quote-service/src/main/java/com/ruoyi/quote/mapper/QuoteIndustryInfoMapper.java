package com.ruoyi.quote.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.quote.domain.entity.QuoteIndustryInfo;
import com.ruoyi.quote.domain.vo.QuoteIndustryInfoVO;
import org.springframework.stereotype.Repository;

/**
 * 行业信息Mapper接口
 * 
 * @author yrb
 * @date 2022-04-26
 */
@Repository
public interface QuoteIndustryInfoMapper extends BaseMapper<QuoteIndustryInfo>
{
    /**
     * 查询行业信息
     * 
     * @param id 行业信息主键
     * @return 行业信息
     */
    QuoteIndustryInfo selectQuoteIndustryInfoById(Long id);

    /**
     * 查询行业信息列表
     * 
     * @param quoteIndustryInfo 行业信息
     * @return 行业信息集合
     */
    public List<QuoteIndustryInfo> selectQuoteIndustryInfoList(QuoteIndustryInfo quoteIndustryInfo);

    /**
     * 新增行业信息
     * 
     * @param quoteIndustryInfo 行业信息
     * @return 结果
     */
    public int insertQuoteIndustryInfo(QuoteIndustryInfo quoteIndustryInfo);

    /**
     * 修改行业信息
     * 
     * @param quoteIndustryInfo 行业信息
     * @return 结果
     */
    public int updateQuoteIndustryInfo(QuoteIndustryInfo quoteIndustryInfo);

    /**
     * 删除行业信息
     * 
     * @param id 行业信息主键
     * @return 结果
     */
    public int deleteQuoteIndustryInfoById(Long id);

    /**
     * 批量删除行业信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteQuoteIndustryInfoByIds(Long[] ids);

    /**
     * 查询行业信息
     *
     * @param quoteIndustryInfo 按条件查询
     * @return 行业信息
     */
    QuoteIndustryInfo selectQuoteIndustryInfo(QuoteIndustryInfo quoteIndustryInfo);

    /**
     * 查询行业信息列表(包含子父类)
     *
     * @param quoteIndustryInfoVO 行业信息
     * @return 行业信息集合
     */
    List<QuoteIndustryInfoVO> selectQuoteIndustryInfoUserList(QuoteIndustryInfoVO quoteIndustryInfoVO);

    /**
     * 删除行业信息
     *
     * @param parentId 父类id
     * @return 结果
     */
    int deleteQuoteIndustryInfoByParentId(Long parentId);

    /**
     * 获取行业列表（公卫、职卫）
     *
     * @param quoteIndustryInfo 行业名称（可选） 项目id
     * @return
     */
    List<QuoteIndustryInfoVO> selectIndustryInfoList(QuoteIndustryInfo quoteIndustryInfo);

    /**
     * 查询行业信息列表
     *
     * @param quoteIndustryInfo 行业信息
     * @return 行业信息集合
     */
    List<QuoteIndustryInfo> selectQuoteIndustryInfoEditList(QuoteIndustryInfo quoteIndustryInfo);
}
