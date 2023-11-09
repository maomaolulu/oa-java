package com.ruoyi.training.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.training.entity.TraCourseInfo;
import com.ruoyi.training.entity.dto.TraCourseInfoDTO;
import com.ruoyi.training.entity.vo.CourseInfoVO;

/**
 * 课程信息Service接口
 * 
 * @author yrb
 * @date 2022-05-30
 */
public interface ITraCourseInfoService 
{
    /**
     * 查询公司关联课程
     * @return 课程信息
     */
    List<TraCourseInfo> companyCourseInfoList(Long companyId);

    /**
     * 批量查询公司关联课程
     * @return 课程信息
     */
    Map<Long, List<TraCourseInfo>> companyCourseInfoLists(List<Long> companyIds);
    /**
     * 查询课程信息
     *
     * @param id 课程信息主键
     * @return 课程信息
     */
    public TraCourseInfo selectTraCourseInfoById(Long id);

    /**
     * 查询课程信息列表
     * 
     * @param traCourseInfoDTO 课程信息
     * @return 课程信息集合
     */
    List<CourseInfoVO> selectTraCourseInfoUserList(TraCourseInfoDTO traCourseInfoDTO);

    /**
     * 新增课程信息
     * 
     * @param traCourseInfo 课程信息
     * @return 结果
     */
    public Map<String,Object> insertTraCourseInfo(TraCourseInfo traCourseInfo);

    /**
     * 修改课程信息
     * 
     * @param traCourseInfo 课程信息
     * @return 结果
     */
    public int updateTraCourseInfo(TraCourseInfo traCourseInfo);

    /**
     * 批量删除课程信息
     * 
     * @param ids 需要删除的课程信息主键集合
     * @return 结果
     */
    public int deleteTraCourseInfoByIds(Long[] ids);

    /**
     * 删除课程信息信息
     * 
     * @param id 课程信息主键
     * @return 结果
     */
    public int deleteTraCourseInfoById(Long id);

    /**
     * 查询全部课程 区分选修必修 是否属于我的课程
     *
     * @param traCourseInfoDTO 课程信息
     * @return 课程信息集合
     */
    List<CourseInfoVO> findTraCourseInfoList(TraCourseInfoDTO traCourseInfoDTO);

    /**
     * 查询我的课表
     *
     * @param traCourseInfoDTO 课程信息
     * @return 课程信息集合
     */
    List<CourseInfoVO> findMyCourseList(TraCourseInfoDTO traCourseInfoDTO);

    /**
     * 改变课程上下架状态（上下架课程）
     *
     * @param traCourseInfo 课程id
     * @return result
     */
    boolean changeCourseMarketState(TraCourseInfo traCourseInfo);
}
