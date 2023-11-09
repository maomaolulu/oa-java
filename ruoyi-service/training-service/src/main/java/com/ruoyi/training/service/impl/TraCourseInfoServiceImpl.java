package com.ruoyi.training.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.file.feign.RemoteFileService;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.training.entity.TraCourseInfo;
import com.ruoyi.training.entity.dto.TraCourseInfoDTO;
import com.ruoyi.training.entity.vo.CourseInfoVO;
import com.ruoyi.training.utils.TrainingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.training.mapper.TraCourseInfoMapper;
import com.ruoyi.training.service.ITraCourseInfoService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 课程信息Service业务层处理
 *
 * @author yrb
 * @date 2022-05-30
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class TraCourseInfoServiceImpl implements ITraCourseInfoService {
    private final TraCourseInfoMapper traCourseInfoMapper;
    private final RemoteUserService remoteUserService;
    private final RemoteDeptService remoteDeptService;
    private final RemoteFileService remoteFileService;

    @Autowired
    public TraCourseInfoServiceImpl(TraCourseInfoMapper traCourseInfoMapper,
                                    RemoteUserService remoteUserService,
                                    RemoteDeptService remoteDeptService,
                                    RemoteFileService remoteFileService) {
        this.traCourseInfoMapper = traCourseInfoMapper;
        this.remoteUserService = remoteUserService;
        this.remoteDeptService = remoteDeptService;
        this.remoteFileService = remoteFileService;
    }

    @Override
    public List<TraCourseInfo> companyCourseInfoList(Long companyId) {
        List<TraCourseInfo> traCourseInfos = traCourseInfoMapper.listByCompanyId(companyId);


        return traCourseInfos;
    }

    @Override
    public Map<Long, List<TraCourseInfo>> companyCourseInfoLists(List<Long> companyIds) {

        List<TraCourseInfo> traCourseInfos = traCourseInfoMapper.listByCompanyIds(
                new QueryWrapper<TraCourseInfo>()
                        .eq("issue_flag",1)
                        .in("company_id",companyIds)
        );
        Map<Long, List<TraCourseInfo>> map = traCourseInfos.stream().collect(Collectors.groupingBy(TraCourseInfo::getCompanyId));

        return map;
    }
    /**
     * 查询课程信息
     *
     * @param id 课程信息主键
     * @return 课程信息
     */
    @Override
    public TraCourseInfo selectTraCourseInfoById(Long id) {
        return traCourseInfoMapper.selectTraCourseInfoById(id);
    }

    /**
     * 查询课程信息列表
     *
     * @param traCourseInfoDTO 课程信息
     * @return 课程信息
     */
    @Override
    public List<CourseInfoVO> selectTraCourseInfoUserList(TraCourseInfoDTO traCourseInfoDTO) {
        // 按一个或多个类别查找
        String trainIds = traCourseInfoDTO.getTrainIds();
        if (StrUtil.isNotBlank(trainIds)) {
            traCourseInfoDTO.setTrainIdList(Arrays.asList(trainIds.split(",")));
        }
        //数据临时载体
        HashSet<String> roleKeys = new HashSet<>();
        //搜索当前用户信息
        SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
        //权限数据抽离
        for (SysRole role : sysUser.getRoles()) {
            roleKeys.add(role.getRoleKey());
        }
        //优维数据特殊处理
        boolean isTrue = roleKeys.add("youwei_admin");
        if (traCourseInfoDTO.getIssueFlag() != null) {
            if (traCourseInfoDTO.getIssueFlag() == 1) {
                // 此处不为空即可 拼接xml中条件(market != null)
                traCourseInfoDTO.setMarket(2);
                // 拼接xml中(issue_flag != 0)
                traCourseInfoDTO.setIssueFlag(0);
            }
        }
        if (!isTrue) {
            List<String> userPermissions = sysUser.getUserPermissions();
            if (traCourseInfoDTO.getCompanyId() == null) {
                return traCourseInfoMapper.selectMoreCourseInfoList(traCourseInfoDTO, userPermissions);
            } else {
                return traCourseInfoMapper.selectTraCourseInfoUserList(traCourseInfoDTO);
            }
        } else {
            if (traCourseInfoDTO.getCompanyId() == null) {
                traCourseInfoDTO.setCompanyId(SystemUtil.getCompanyId());
            }
            return traCourseInfoMapper.selectTraCourseInfoUserList(traCourseInfoDTO);
        }
    }

    /**
     * 新增课程信息
     *
     * @param traCourseInfo 课程信息
     * @return 结果
     */
    @Override
    public Map<String, Object> insertTraCourseInfo(TraCourseInfo traCourseInfo) {
        traCourseInfo.setCreateTime(DateUtils.getNowDate());
        traCourseInfo.setLastModifier(SystemUtil.getUserNameCn());
        traCourseInfo.setUpdateTime(DateUtils.getNowDate());
        if (traCourseInfo.getCompanyId() == null) {
            traCourseInfo.setCompanyId(SystemUtil.getCompanyId());
        }
        int i = traCourseInfoMapper.insertTraCourseInfo(traCourseInfo);
        String md5 = traCourseInfo.getMd5();
        if (StrUtil.isNotBlank(md5) && i > 0) {
            if (remoteFileService.update(null, md5) < 0) {
                throw new RuntimeException("更新临时文件为永久文件出错！");
            }
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("id", traCourseInfo.getId());
        map.put("result", i);
        return map;
    }

    /**
     * 修改课程信息
     *
     * @param traCourseInfo 课程信息
     * @return 结果
     */
    @Override
    public int updateTraCourseInfo(TraCourseInfo traCourseInfo) {
        traCourseInfo.setLastModifier(SystemUtil.getUserNameCn());
        traCourseInfo.setUpdateTime(DateUtils.getNowDate());
        int i = traCourseInfoMapper.updateTraCourseInfo(traCourseInfo);
        String md5 = traCourseInfo.getMd5();
        if (StrUtil.isNotBlank(md5) && i > 0) {
            if (remoteFileService.update(null, md5) < 0) {
                throw new RuntimeException("更新临时文件为永久文件出错！");
            }
        }
        return i;
    }

    /**
     * 批量删除课程信息
     *
     * @param ids 需要删除的课程信息主键
     * @return 结果
     */
    @Override
    public int deleteTraCourseInfoByIds(Long[] ids) {
        return traCourseInfoMapper.deleteTraCourseInfoByIds(ids);
    }

    /**
     * 删除课程信息信息
     *
     * @param id 课程信息主键
     * @return 结果
     */
    @Override
    public int deleteTraCourseInfoById(Long id) {
        return traCourseInfoMapper.deleteTraCourseInfoById(id);
    }

    /**
     * 查询全部课程 区分选修必修 是否属于我的课程
     *
     * @param traCourseInfoDTO 课程信息
     * @return 课程信息集合
     */
    @Override
    public List<CourseInfoVO> findTraCourseInfoList(TraCourseInfoDTO traCourseInfoDTO) {
        // 获取部门id对应的父类id
        Long userId = SystemUtil.getUserId();
        traCourseInfoDTO.setUserId(userId);
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        Long deptId = sysUser.getDeptId();
        SysDept sysDept = remoteDeptService.selectSysDeptByDeptId(deptId);
        String ancestors = sysDept.getAncestors() + deptId;
        ancestors = TrainingUtils.getRegexpAncestor(ancestors);
        traCourseInfoDTO.setAncestors(ancestors);
        traCourseInfoDTO.setIssueFlag(1);
        traCourseInfoDTO.setTrainYear(Calendar.getInstance().get(Calendar.YEAR));
        return traCourseInfoMapper.selectTraCourseInfoCustomList(traCourseInfoDTO);
    }

    /**
     * 查询我的课表
     *
     * @param traCourseInfoDTO 课程信息
     * @return 课程信息集合
     */
    @Override
    public List<CourseInfoVO> findMyCourseList(TraCourseInfoDTO traCourseInfoDTO) {
        // 获取部门id对应的父类id
        Long userId = SystemUtil.getUserId();
        traCourseInfoDTO.setUserId(userId);
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        Long deptId = sysUser.getDeptId();
        SysDept sysDept = remoteDeptService.selectSysDeptByDeptId(deptId);
        String ancestors = sysDept.getAncestors() + deptId;
        ancestors = TrainingUtils.getRegexpAncestor(ancestors);
        traCourseInfoDTO.setAncestors(ancestors);
        traCourseInfoDTO.setIssueFlag(1);
        traCourseInfoDTO.setTrainYear(Calendar.getInstance().get(Calendar.YEAR));
        traCourseInfoDTO.setCourseType(1);
        return traCourseInfoMapper.selectMyCourseList(traCourseInfoDTO);
    }

    /**
     * 下架课程
     *
     * @param traCourseInfo 课程id
     * @return result
     */
    @Override
    public boolean changeCourseMarketState(TraCourseInfo traCourseInfo) {
        return traCourseInfoMapper.updateTraCourseInfo(traCourseInfo) == 1;
    }
}
