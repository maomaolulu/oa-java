package com.ruoyi.training.service;

import java.util.List;
import com.ruoyi.training.entity.TraTrainCategory;
import com.ruoyi.training.entity.vo.TraTrainCategoryVO;

/**
 * 培训类型Service接口
 * 
 * @author yrb
 * @date 2022-06-06
 */
public interface ITraTrainCategoryService 
{
    /**
     * 查询培训类型
     * 
     * @param id 培训类型主键
     * @return 培训类型
     */
    public TraTrainCategory selectTraTrainCategoryById(Long id);

    /**
     * 查询培训类型列表
     * 
     * @param traTrainCategory 培训类型
     * @return 培训类型集合
     */
    public List<TraTrainCategory> selectTraTrainCategoryList(TraTrainCategory traTrainCategory);

    /**
     * 新增培训类型
     * 
     * @param traTrainCategory 培训类型
     * @return 结果
     */
    public int insertTraTrainCategory(TraTrainCategory traTrainCategory);

    /**
     * 修改培训类型
     * 
     * @param traTrainCategory 培训类型
     * @return 结果
     */
    public int updateTraTrainCategory(TraTrainCategory traTrainCategory);

    /**
     * 批量删除培训类型
     * 
     * @param ids 需要删除的培训类型主键集合
     * @return 结果
     */
    public int deleteTraTrainCategoryByIds(Long[] ids);

    /**
     * 删除培训类型信息
     * 
     * @param id 培训类型主键
     * @return 结果
     */
    public int deleteTraTrainCategoryById(Long id);


    /**
     * 通过多个公司id查询课程分类信息
     *
     * @param companyIds-->公司id集合
     * @return 对应公司的课程分类信息
     */
    List<TraTrainCategoryVO> findTrainCategoryInfoByCompanyIds(List<Long> companyIds);
}
