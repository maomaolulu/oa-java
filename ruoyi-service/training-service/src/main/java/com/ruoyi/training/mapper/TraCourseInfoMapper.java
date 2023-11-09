package com.ruoyi.training.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.training.entity.TraCourseInfo;
import com.ruoyi.training.entity.dto.TraCourseInfoDTO;
import com.ruoyi.training.entity.vo.CourseInfoVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程信息Mapper接口
 *
 * @author yrb
 * @date 2022-05-30
 */
@Repository
public interface TraCourseInfoMapper {
    /**
     *
     */
    @Select("select * from tra_course_info where company_id=#{companyId } and issue_flag=1 ")
    List<TraCourseInfo> listByCompanyId(Long companyId);

    /**
     *
     */
    @Select("select * from tra_course_info ${ew.customSqlSegment}  ")
    List<TraCourseInfo> listByCompanyIds(@Param(Constants.WRAPPER) QueryWrapper wrapper);
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
     * @param traCourseInfo 课程信息
     * @return 课程信息集合
     */
    public List<TraCourseInfo> selectTraCourseInfoList(TraCourseInfo traCourseInfo);

    /**
     * 新增课程信息
     *
     * @param traCourseInfo 课程信息
     * @return 结果
     */
    public int insertTraCourseInfo(TraCourseInfo traCourseInfo);

    /**
     * 修改课程信息
     *
     * @param traCourseInfo 课程信息
     * @return 结果
     */
    public int updateTraCourseInfo(TraCourseInfo traCourseInfo);

    /**
     * 删除课程信息
     *
     * @param id 课程信息主键
     * @return 结果
     */
    public int deleteTraCourseInfoById(Long id);

    /**
     * 批量删除课程信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTraCourseInfoByIds(Long[] ids);

    /**
     * 查询全部课程 区分选修必修 是否属于我的课程
     *
     * @param traCourseInfoDTO 课程信息
     * @return 课程信息集合
     */
    List<CourseInfoVO> selectTraCourseInfoCustomList(TraCourseInfoDTO traCourseInfoDTO);

    /**
     * 查询我的课表
     *
     * @param traCourseInfoDTO 课程信息
     * @return 我的课表
     */
    List<CourseInfoVO> selectMyCourseList(TraCourseInfoDTO traCourseInfoDTO);

    /**
     * 查询课程信息列表
     *
     * @param traCourseInfoDTO 课程信息
     * @return 课程信息集合
     */
    List<CourseInfoVO> selectTraCourseInfoUserList(TraCourseInfoDTO traCourseInfoDTO);

    /**
     * 查询本部门课程列表信息
     *
     * @param traCourseInfo 课程信息
     * @param userId        用户id
     * @return 本部门课程列表
     */
    List<TraCourseInfo> selectNewCourseInfoList(@Param("traCourseInfo") TraCourseInfo traCourseInfo, @Param("userId") Long userId);

    /**
     * 查询更多课程信息
     *
     * @param info 查询条件
     * @param ids  自定义部门集合
     * @return 更多课程信息集合
     */
    List<CourseInfoVO> selectMoreCourseInfoList(@Param("info") TraCourseInfoDTO info, @Param("ids") List<String> ids);
}
