package com.ruoyi.training.mapper;

import com.ruoyi.training.entity.TraTrainCategory;
import com.ruoyi.training.entity.vo.TraTrainCategoryVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 培训类型Mapper接口
 *
 * @author yrb
 * @date 2022-06-06
 */
@Repository
public interface TraTrainCategoryMapper {
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
     * 删除培训类型
     *
     * @param id 培训类型主键
     * @return 结果
     */
    public int deleteTraTrainCategoryById(Long id);

    /**
     * 批量删除培训类型
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTraTrainCategoryByIds(Long[] ids);

    /**
     * 查询我的课程类型列表
     *
     * @param userId 用户id
     * @return 我的课程类型列表（全部，不区分年份）
     * @author hjy
     * @date 2022/6/15 9:57
     */
    List<TraTrainCategory> getUserTypeList(Long userId);

    /**
     * 查询本部门课程类型列表-变更：查询本公司课程类型列表
     *
     * @param companyId 公司id
     * @return 本部门课程类型列表
     * @author hjy
     * @date 2022/6/15 9:57
     */
    List<TraTrainCategory> getDeptTypeList(Long companyId);

    /**
     * 查询培训类型列表(自定义考试)
     *
     * @param examId 考试信息id
     * @return 培训类型集合
     */
    List<TraTrainCategory> selectTraTrainCategoryUserList(Long examId);

    /**
     * 获取培训类别信息-优维，范围扩大
     *
     * @param ids 自定义部门信息
     * @return 类别列表
     */
    List<TraTrainCategory> selectMoreTrainCategoryList(@Param("ids") List<String> ids);

    /**
     * 通过多个公司id查询课程分类信息
     *
     * @param companyIds-->公司id集合
     * @return 对应公司的课程分类信息
     */
    List<TraTrainCategoryVO> selectTrainCategoryInfoByCompanyIds(@Param("companyIds") List<Long> companyIds);
}
