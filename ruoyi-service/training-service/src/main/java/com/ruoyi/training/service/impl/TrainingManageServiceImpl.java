package com.ruoyi.training.service.impl;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.training.entity.TraCourseUser;
import com.ruoyi.training.entity.TraManage;
import com.ruoyi.training.entity.dto.TrainingManageDTO;
import com.ruoyi.training.entity.vo.TrainingManageVO;
import com.ruoyi.training.mapper.TraCourseUserMapper;
import com.ruoyi.training.mapper.TraMyExamMapper;
import com.ruoyi.training.mapper.TrainingManageMapper;
import com.ruoyi.training.service.ITrainingManageService;
import com.ruoyi.training.utils.TrainingUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Author yrb
 * @Date 2022/6/2 10:12
 * @Version 1.0
 * @Description 培训管理Service业务处理层
 */
@Service
public class TrainingManageServiceImpl implements ITrainingManageService {
    private final TrainingManageMapper trainingManageMapper;
    private final RemoteUserService remoteUserService;
    private final TraCourseUserMapper traCourseUserMapper;
    private final TraMyExamMapper myExamMapper;


    public TrainingManageServiceImpl(TrainingManageMapper trainingManageMapper,
                                     RemoteUserService remoteUserService, TraCourseUserMapper traCourseUserMapper, TraMyExamMapper myExamMapper) {
        this.trainingManageMapper = trainingManageMapper;
        this.remoteUserService = remoteUserService;
        this.traCourseUserMapper = traCourseUserMapper;
        this.myExamMapper = myExamMapper;
    }

    @Override
    public List<TrainingManageVO> findTrainingManageList(TrainingManageDTO trainingManageDto) {
        SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
        if (null != trainingManageDto.getDeptId()) {
            trainingManageDto.setCompanyId(1);
        } else {
            trainingManageDto.setDeptId(sysUser.getCompanyId());
        }
        int year = Calendar.getInstance().get(Calendar.YEAR);
        trainingManageDto.setTrainYear(year);
        trainingManageDto.setCreateTime(year + "-10-01 00:00:00");
        List<TrainingManageVO> trainingManageList = trainingManageMapper.selectTrainingManageList(trainingManageDto);
        if (null != trainingManageList && trainingManageList.size() > 0) {
            String company = sysUser.getCompany();
            for (TrainingManageVO trainingManageVo : trainingManageList) {
                String deptName = trainingManageVo.getDeptName();
                trainingManageVo.setDepartment(company + "-" + deptName);
                long createTime = trainingManageVo.getCreateTime().getTime();
                long startTime = DateUtil.parse(year - 1 + "-10-01 00:00:00").getTime();
                if (createTime >= startTime) {
                    trainingManageVo.setLimitHours(new BigDecimal(40));
                } else {
                    trainingManageVo.setLimitHours(new BigDecimal(8));
                }
                if (null == trainingManageVo.getRealHours()) {
                    trainingManageVo.setRealHours(new BigDecimal(0));
                }
            }
        }
        return trainingManageList;
    }

    /**
     * 获取培训管理列表
     *
     * @param manage 可传值
     * @return 培训列表
     */
    @Override
    public List<TraManage> selectTraManageList(TraManage manage) {
        //数据临时载体
        HashSet<String> roleKeys = new HashSet<>();
        //搜索当前用户信息
        SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
        //权限数据抽离
        for (SysRole role : sysUser.getRoles()) {
            roleKeys.add(role.getRoleKey());
        }
        List<Long> deptIds = manage.getDeptIds();
        //优维数据特殊处理
        boolean isTrue = roleKeys.add("youwei_admin");
        if (!isTrue) {
            if (StringUtils.isNull(deptIds)) {
                List<String> userPermissions = sysUser.getUserPermissions();
                if (StringUtils.isNotEmpty(userPermissions)) {
                    deptIds = new ArrayList<>();
                    for (String str : userPermissions) {
                        deptIds.add(Long.valueOf(str));
                    }
                }
            }
            //防止空指针报错
            if (deptIds.size() == 0) {
                deptIds.add(sysUser.getDeptId());
            }
            return trainingManageMapper.selectMoreTraManageList(manage.getUserId(), deptIds);
        } else {
            //类型   1：部门主管 ； 2 公司经理
            if (manage.getCompanyId() == null) {
                manage.setCompanyId(SystemUtil.getCompanyId());
            }
            //获取角色表,并判断是否是总经理
            if (!roleKeys.add("training-leader")) {
                if (StringUtils.isNotEmpty(deptIds)) {
                    manage.setDeptId(deptIds.get(0));
                }
            } else {
                manage.setDeptId(SystemUtil.getDeptId());
            }
            return trainingManageMapper.selectTraManageList(manage);
        }
    }

    /**
     * 获取培训完成率，考核通过率
     *
     * @param userId 用户id
     * @return 获取培训完成率，考核通过率
     * @author hjy
     * @date 2022/6/20 10:21
     */
    @Override
    public Map<String, Object> getRate(Long userId) {
        //类型   1：部门主管 ； 2 公司经理
        int type = 1;
        //如果没有提供用户id，默认当前登录用户id
        if (userId == null) {
            userId = SystemUtil.getUserId();
        }
        //搜索当前用户信息
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        //获取角色表,并判断是否是总经理
        for (SysRole role : sysUser.getRoles()) {
            //注：根据登录人员的角色判断  总经理查看当前公司的通关率；部门主管只查看该部门的通关率； fengongsi_manager 分公司总经理(审批)
            if ("training-leader".equalsIgnoreCase(role.getRoleKey())) {
                type = 2;
                break;
            }
        }
        Map<String, Object> map;
        if (type == 2) {
            //经理
            map = trainingManageMapper.getRateByCompanyId(sysUser.getCompanyId());
        } else {
            //部门主管
            map = trainingManageMapper.getRateByDeptId(sysUser.getDeptId());
        }
        map.put("company", sysUser.getCompany());
        return map;
    }

    /**
     * 获取员工培训情况
     *
     * @param userId 用户id
     * @return 结果集
     */
    @Override
    public Map<String, Object> getTrainingDetail(Long userId) {
        //当前操作用户id
        if (userId == null) {
            userId = SystemUtil.getUserId();
        }
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
        Map<String, Object> mapInfo = new HashMap<>();
        //用户信息+部门学时信息
        mapInfo.put("userInfo", trainingManageMapper.getDeptHours(userId, map.get("type")));
        //课程信息
        List<TraCourseUser> courseUserList = traCourseUserMapper.selectMyCourseInfoList(new TraCourseUser(userId, nowYear, null));
        mapInfo.put("courseInfo", courseUserList);
        //考试信息
        mapInfo.put("examInfo", myExamMapper.selectMyExamListInfoByUserId(userId, nowYear));
        return mapInfo;
    }

    /**
     * 获取更多考试通过率（公司级别）
     *
     * @param userId 当前登陆人
     * @return 结果
     */
    @Override
    public List<Map<String, Object>> getMoreRate(Long userId) {
        if (userId == null) {
            userId = SystemUtil.getUserId();
        }
        //搜索当前用户信息
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        List<Map<String, Object>> companyInfo = trainingManageMapper.getCompanyId(sysUser.getUserPermissions());
        if (StringUtils.isEmpty(companyInfo)) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("key", sysUser.getCompanyId());
            map.put("value", sysUser.getCompany());
            companyInfo.add(map);
        }
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        for (Map<String, Object> map : companyInfo) {
            Long companyId = Long.valueOf(String.valueOf(map.get("key")));
            String companyName = String.valueOf(map.get("value"));
            Map<String, Object> rateInfo = trainingManageMapper.getRateByCompanyId(companyId);
            rateInfo.put("companyName", companyName);
            maps.add(rateInfo);
        }
        return maps;
    }
}
