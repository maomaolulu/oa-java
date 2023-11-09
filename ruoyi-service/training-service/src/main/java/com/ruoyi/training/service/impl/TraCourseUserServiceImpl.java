package com.ruoyi.training.service.impl;

import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.file.feign.RemoteFileService;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.training.entity.*;
import com.ruoyi.training.mapper.*;
import com.ruoyi.training.service.ITraCourseUserService;
import com.ruoyi.training.utils.TrainingUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author hjy
 */
@Service
public class TraCourseUserServiceImpl implements ITraCourseUserService {

    private final TraCourseUserMapper traCourseUserMapper;

    private final TraMyExamMapper myExamMapper;

    private final TraCourseInfoMapper courseInfoMapper;

    private final TraTrainCategoryMapper categoryMapper;

    private final TraMyHoursMapper myHoursMapper;

    private final RemoteUserService remoteUserService;

    private final RedisUtils redisUtils;

    private final RemoteFileService fileService;

    public TraCourseUserServiceImpl(TraCourseUserMapper traCourseUserMapper, TraMyExamMapper myExamMapper, TraCourseInfoMapper courseInfoMapper, TraTrainCategoryMapper categoryMapper, TraMyHoursMapper myHoursMapper, RemoteUserService remoteUserService, RedisUtils redisUtils, RemoteFileService fileService) {
        this.traCourseUserMapper = traCourseUserMapper;
        this.myExamMapper = myExamMapper;
        this.courseInfoMapper = courseInfoMapper;
        this.categoryMapper = categoryMapper;
        this.myHoursMapper = myHoursMapper;
        this.remoteUserService = remoteUserService;
        this.redisUtils = redisUtils;
        this.fileService = fileService;
    }


    /**
     * 查询本部门课程信息列表
     *
     * @param traCourseInfo 相关课程信息
     * @return 课程列表，课程类别列表
     */
    @Override
    public List<TraCourseInfo> selectTraCourseInfoList(TraCourseInfo traCourseInfo) {
        //宁波优维按所在部门显示，其他公司显示全部
        Long companyId = SystemUtil.getCompanyId();
        Long deptId = SystemUtil.getDeptId();
        if (Objects.equals(companyId, TrainingUtils.NBYW_COMPANY_ID)) {
            traCourseInfo.setCompanyId(null);
            traCourseInfo.setDeptId(deptId.toString());
        } else {
            //加入公司筛选
            if (traCourseInfo.getCompanyId() == null) {
                traCourseInfo.setCompanyId(companyId);
            }
        }

        //人员id
        Long userId = SystemUtil.getUserId();
        //个人所属部门
        if (traCourseInfo.getDeptId() == null) {
            traCourseInfo.setDeptId(String.valueOf(SystemUtil.getDeptId()));
        }
        List<TraCourseInfo> traCourseInfos = courseInfoMapper.selectNewCourseInfoList(traCourseInfo, userId);
        traCourseInfos.stream().forEach(courseInfo -> {

            String training = fileService.getFileUrls(courseInfo.getTypes(), courseInfo.getCoverUrl());
            courseInfo.setCoverUrl(training);
        });
        return traCourseInfos;
    }

    /**
     * 查询我的课程
     *
     * @param traCourseUser 我的课程信息
     * @return 我饿课程列表
     */
    @Override
    public List<TraCourseUser> selectMyCourseInfoList(TraCourseUser traCourseUser) {
        //筛选个人课程
        if (traCourseUser.getUserId() == null) {
            traCourseUser.setUserId(SystemUtil.getUserId());
        }
        //个人归属部门
        if (traCourseUser.getDeptId() == null) {
            traCourseUser.setDeptId(SystemUtil.getDeptId());
        }
        List<TraCourseUser> traCourseUsers = traCourseUserMapper.selectMyCourseInfoList(traCourseUser);
        traCourseUsers.stream().forEach(courseInfoUser -> {
            TraCourseInfo courseInfo = courseInfoUser.getCourseInfo();
            String training = fileService.getFileUrls(courseInfo.getTypes(), courseInfo.getCoverUrl());
            courseInfo.setCoverUrl(training);
        });
        return traCourseUsers;
    }

    /**
     * 新增我的课程
     *
     * @param traCourseUser 我的课程信息表
     * @return 状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertMyCourse(TraCourseUser traCourseUser) {
        //当前操作用户id
        if (traCourseUser.getUserId() == null) {
            traCourseUser.setUserId(SystemUtil.getUserId());
        }
        //当前年份
        int nowYear = Calendar.getInstance().get(Calendar.YEAR);
        //指定用户信息
        SysUser sysUser = remoteUserService.selectSysUserByUserId(traCourseUser.getUserId());
        //判断新老员工
        Map<String, Integer> map = TrainingUtils.judgeStaffType(sysUser);
        //满足新员工，且在当前年9月份之后入职
        if (map.get("type") == 1 && map.get("status") == 1) {
            nowYear++;
        }
        //查找当前年考试是否存在
        TraMyExam traMyExam = myExamMapper.findExam(traCourseUser.getUserId(), nowYear);
        if (traMyExam != null) {
            //判断考试是否结束
            if (traMyExam.getStatus() != 2) {
                //将课程加入到我的考试列表中
                myExamMapper.addCourse(traMyExam.getId(), traCourseUser.getCourseId());
            }
        } else {
            TraMyExam myExam = new TraMyExam();
            myExam.setUserId(traCourseUser.getUserId());
            myExam.setStatus(0);
            myExam.setExamType(0);
            myExam.setTrainYear(nowYear);
            if (map.get("type") == 1) {
                myExam.setExamName("新上岗专业技术人员能力培训");
            } else {
                myExam.setExamName("专业技术人员年度继续教育培训");
            }
            myExamMapper.insertMyExam(myExam);
            //将课程加入到我的考试列表中
            myExamMapper.addCourse(myExam.getId(), traCourseUser.getCourseId());
        }
        traCourseUser.setTrainYear(nowYear);
        traCourseUser.setJoinType(0);
        traCourseUser.setSchedule("0");
        return traCourseUserMapper.insertMyCourse(traCourseUser);
    }

    /**
     * 删除我的课程
     *
     * @param courseId 课程id
     * @return 状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int deleteMyCourse(Long courseId) {
        //当前操作用户id
        Long userId = SystemUtil.getUserId();
        //当前年份
        int nowYear = Calendar.getInstance().get(Calendar.YEAR);
        //指定用户信息
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        //判断新老员工
        Map<String, Integer> map = TrainingUtils.judgeStaffType(sysUser);
        //满足新员工，且在当前年9月份之后入职
        if (map.get("type") == 1 && map.get("status") == 1) {
            nowYear++;
        }
        TraMyExam traMyExam = myExamMapper.findExam(userId, nowYear);
        myExamMapper.deleteCourse(traMyExam.getId(), courseId);
        int num = myExamMapper.selectMyExamCourseCount(traMyExam.getId());
        if (num == 0) {
            myExamMapper.deleteMyExamById(traMyExam.getId());
        }
        return traCourseUserMapper.deleteMyCourse(userId, nowYear, courseId);
    }


    /**
     * @param type 1：我的课程类型列表；0：本部门课程类型列表-变更为0：本公司全部课程类型列表
     * @return 我的课程类型列表或本部门全部课程类型列表
     * @description 查询课程类型列表
     */
    @Override
    public List<TraTrainCategory> getTypeList(Integer type) {
        List<TraTrainCategory> list;
        if (type == 1) {
            //当前操作用户id
            Long userId = SystemUtil.getUserId();
            //查询我的课程类型列表
            list = categoryMapper.getUserTypeList(userId);
        } else {
            //当前登录用户所在部门
            Long companyId = SystemUtil.getCompanyId();
            //查询本部门课程类型列表
            list = categoryMapper.getDeptTypeList(companyId);
        }
        return list;
    }

    /**
     * 更新我的课程进度以及状态
     *
     * @param traCourseUser 我的课程信息
     * @return 状态
     */
    @Override
    public int updateTraCourseUser(TraCourseUser traCourseUser) {
        //如果没有提供用户id，默认当前登录用户id
        if (traCourseUser.getUserId() == null) {
            traCourseUser.setUserId(SystemUtil.getUserId());
        }
        //状态为已完成的时候  把完成时间加上
        if (traCourseUser.getStatus() == 2) {
            redisUtils.delete("training-video" + traCourseUser.getCourseId() + ":" + SystemUtil.getUserId());
            //老员工及满足条件的新员工按照正常观看时间记录
            String time = DateUtils.getTime();
            //获取指定用户信息用于判断是否是老员工
            SysUser sysUser = remoteUserService.selectSysUserByUserId(traCourseUser.getUserId());
            Map<String, Integer> map = TrainingUtils.judgeStaffType(sysUser);
            if (map.get("type") == 1 && map.get("status") == 1) {
                //10月份包括之后的把完成时间记录到下一年里面（2023-01-01 00:00:00）
                int year = Calendar.getInstance().get(Calendar.YEAR) + 1;
                time = year + "-01-01 00:00:00";
            }
            traCourseUser.setFinishTime(time);
        }
        return traCourseUserMapper.updateTraCourseUser(traCourseUser);
    }

    /**
     * 获取我的学时
     *
     * @param myHours 我的学时相关信息
     * @return 我的学时相关信息
     */
    @Override
    public TraMyHours getMyHours(TraMyHours myHours) {
        //如果没有提供用户id，默认当前登录用户id
        if (myHours.getUserId() == null) {
            myHours.setUserId(SystemUtil.getUserId());
        }
        //如果没有设置年份，默认取当年的年份
        if (myHours.getYear() == null) {
            myHours.setYear(Calendar.getInstance().get(Calendar.YEAR));
        }
        //获取指定用户信息用于判断是否是老员工
        SysUser sysUser = remoteUserService.selectSysUserByUserId(myHours.getUserId());

        Map<String, Integer> map = TrainingUtils.judgeStaffType(sysUser);
        myHours.setUserType(map.get("type"));

        return myHoursMapper.getMyHours(myHours);
    }
}
