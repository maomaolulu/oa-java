package com.ruoyi.quote.mapper;

import java.util.List;

import com.ruoyi.quote.domain.dto.QuotePollutantTypeDTO;
import com.ruoyi.quote.domain.entity.QuotePollutantType;
import org.springframework.stereotype.Repository;

/**
 * 污染物类别Mapper接口
 *
 * @author yrb
 * @date 2022-06-15
 */
@Repository
public interface QuotePollutantTypeMapper {
    /**
     * 查询污染物类别
     *
     * @param id 污染物类别主键
     * @return 污染物类别
     */
    public QuotePollutantType selectQuotePollutantTypeById(Long id);

    /**
     * 查询污染物类别列表
     *
     * @param quotePollutantType 污染物类别
     * @return 污染物类别集合
     */
    public List<QuotePollutantType> selectQuotePollutantTypeList(QuotePollutantType quotePollutantType);

    /**
     * 查询污染物类别列表(编辑时使用)
     *
     * @param quotePollutantType 污染物类别
     * @return 污染物类别集合
     */
    public List<QuotePollutantType> selectQuotePollutantTypeListForEdit(QuotePollutantType quotePollutantType);

    /**
     * 新增污染物类别
     *
     * @param quotePollutantType 污染物类别
     * @return 结果
     */
    public int insertQuotePollutantType(QuotePollutantType quotePollutantType);

    /**
     * 修改污染物类别
     *
     * @param quotePollutantType 污染物类别
     * @return 结果
     */
    public int updateQuotePollutantType(QuotePollutantType quotePollutantType);

    /**
     * 删除污染物类别
     *
     * @param id 污染物类别主键
     * @return 结果
     */
    public int deleteQuotePollutantTypeById(Long id);

    /**
     * 批量删除污染物类别
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteQuotePollutantTypeByIds(Long[] ids);

    /**
     * 获取检测类型列表（过滤当前子类已报价的）
     *
     * @param quotePollutantTypeDTO 大类id 过滤list
     * @return
     */
    List<QuotePollutantType> selectQuotePollutantTypeFilterList(QuotePollutantTypeDTO quotePollutantTypeDTO);

    /**
     * 查询检测类型信息 （数据导入使用）
     *
     * @param quotePollutantType 检测类型名称 所属项目
     * @return result
     */
    QuotePollutantType selectQuotePollutantType(QuotePollutantType quotePollutantType);

    /**
     * 通过表单id从检测项目中查找检测类型
     *
     * @param list
     * @return result
     */
    List<QuotePollutantType> selectPollutantTypeInTestItem(List list);
}
