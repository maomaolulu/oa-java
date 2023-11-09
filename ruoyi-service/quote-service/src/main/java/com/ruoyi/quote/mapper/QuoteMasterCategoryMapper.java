package com.ruoyi.quote.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.quote.domain.entity.QuoteMasterCategory;
import org.springframework.stereotype.Repository;

/**
 * 报价主分类Mapper接口
 * 
 * @author yrb
 * @date 2022-04-25
 */
@Repository
public interface QuoteMasterCategoryMapper extends BaseMapper<QuoteMasterCategory>
{
    /**
     * 查询报价主分类
     * 
     * @param id 报价主分类主键
     * @return 报价主分类
     */
    public QuoteMasterCategory selectQuoteMasterCategoryById(Long id);

    /**
     * 查询报价主分类列表
     * 
     * @param quoteMasterCategory 报价主分类
     * @return 报价主分类集合
     */
    List<QuoteMasterCategory> selectQuoteMasterCategoryList(QuoteMasterCategory quoteMasterCategory);

    /**
     * 新增报价主分类
     * 
     * @param quoteMasterCategory 报价主分类
     * @return 结果
     */
    public int insertQuoteMasterCategory(QuoteMasterCategory quoteMasterCategory);

    /**
     * 修改报价主分类
     * 
     * @param quoteMasterCategory 报价主分类
     * @return 结果
     */
    public int updateQuoteMasterCategory(QuoteMasterCategory quoteMasterCategory);

    /**
     * 删除报价主分类
     * 
     * @param id 报价主分类主键
     * @return 结果
     */
    public int deleteQuoteMasterCategoryById(Long id);

    /**
     * 批量删除报价主分类
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteQuoteMasterCategoryByIds(Long[] ids);
}
