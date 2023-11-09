package com.ruoyi.training.service.impl;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.training.entity.TraDeptHours;
import com.ruoyi.training.mapper.TraDeptHoursMapper;
import com.ruoyi.training.service.TraDeptHoursService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hjy
 * @description 部门学时接口实现层
 * @date 2022/6/15 15:03
 */
@Service
public class TraDeptHoursServiceImpl implements TraDeptHoursService {

    private final TraDeptHoursMapper hoursMapper;

    private final RemoteUserService remoteUserService;

    public TraDeptHoursServiceImpl(TraDeptHoursMapper hoursMapper, RemoteUserService remoteUserService) {
        this.hoursMapper = hoursMapper;
        this.remoteUserService = remoteUserService;
    }

    /**
     * 获取部门学时列表
     *
     * @param traDeptHours 部门学时信息
     * @return 部门学时信息列表
     */
    @Override
    public List<TraDeptHours> selectTraDeptHoursList(TraDeptHours traDeptHours) {
        //标记
        boolean flag = false;
        //搜索当前用户信息
        SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
        //权限数据抽离
        for (SysRole role : sysUser.getRoles()) {
            if ("youwei_admin".equalsIgnoreCase(role.getRoleKey())) {
                flag = true;
                break;
            }
        }
        List<TraDeptHours> list = null;
        //如果存在特殊角色
        if (flag) {
            List<String> userPermissions = sysUser.getUserPermissions();
            if (StringUtils.isEmpty(userPermissions)) {
                userPermissions.add(String.valueOf(sysUser.getDeptId()));
            }
            list = hoursMapper.selectMoreDeptHoursList(userPermissions);
        } else {
            if (traDeptHours.getCompanyId() == null) {
                traDeptHours.setCompanyId(SystemUtil.getCompanyId());
            }
            list = hoursMapper.selectTraDeptHoursList(traDeptHours);
        }
        return list;
    }

    /**
     * 通过id获取部门学时详情
     *
     * @param id 部门学时id
     * @return 部门学时信息列表
     */
    @Override
    public TraDeptHours selectTraDeptHoursById(Long id) {
        return hoursMapper.selectTraDeptHoursById(id);
    }

    /**
     * 新增部门学时
     *
     * @param traDeptHours 部门学时信息
     * @return 状态
     */
    @Override
    public int insertTraDeptHours(TraDeptHours traDeptHours) {
        traDeptHours.setCreateBy(SystemUtil.getUserName());
        return hoursMapper.insertTraDeptHours(traDeptHours);
    }

    /**
     * 修改部门学时
     *
     * @param traDeptHours 部门学时信息
     * @return 状态
     */
    @Override
    public int updateTraDeptHours(TraDeptHours traDeptHours) {
        traDeptHours.setUpdateBy(SystemUtil.getUserName());
        return hoursMapper.updateTraDeptHours(traDeptHours);
    }

    /**
     * 删除部门学时
     *
     * @param ids 部门学时id集合
     * @return 状态
     */
    @Override
    public int deleteTraDeptHoursByIds(Long[] ids) {
        return hoursMapper.deleteTraDeptHoursByIds(ids);
    }

    /**
     * 检查是否已经创建过部门学时目标
     *
     * @param deptId 部门id
     * @return 学时信息
     */
    @Override
    public TraDeptHours checkedDept(String deptId) {
        return hoursMapper.checkedDept(deptId);
    }
}
