package com.ruoyi.quote.service;

import java.util.List;

import com.ruoyi.quote.domain.dto.QuotePollutantTypeAddDTO;
import com.ruoyi.quote.domain.dto.QuotePollutantTypeDTO;
import com.ruoyi.quote.domain.entity.QuotePollutantType;

/**
 * 污染物类别Service接口
 *
 * @author yrb
 * @date 2022-06-15
 */
public interface IQuotePollutantTypeService {
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
     * 批量删除污染物类别
     *
     * @param ids 需要删除的污染物类别主键集合
     * @return 结果
     */
    public int deleteQuotePollutantTypeByIds(Long[] ids);

    /**
     * 删除污染物类别信息
     *
     * @param id 污染物类别主键
     * @return 结果
     */
    public int deleteQuotePollutantTypeById(Long id);

    /**
     * 添加检测类别 （公卫--web端参数设置）
     *
     * @param quotePollutantTypeAddDTO 参数
     * @return 结果
     */
    boolean addPollutantType(QuotePollutantTypeAddDTO quotePollutantTypeAddDTO);

    /**
     * 污染物关联检测类别、行业类别
     *
     * @param id  关联关系主键id
     * @param ids 污染物结合
     * @return result
     */
    boolean addRelationCaregoryAndPollutant(Long id, List<Long> ids);

    /**
     * 获取检测类型列表（过滤当前子类已报价的）
     *
     * @param quotePollutantTypeDTO 大类id 过滤list
     * @return result
     */
    List<QuotePollutantType> findQuotePollutantTypeFilterList(QuotePollutantTypeDTO quotePollutantTypeDTO);

    /**
     * 查询检测类别信息 （数据导入时使用）
     *
     * @param quotePollutantType 检测类型名称 所属项目id
     * @return result
     */
    QuotePollutantType findQuotePollutantType(QuotePollutantType quotePollutantType);
}
