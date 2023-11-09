package com.ruoyi.activiti.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.ruoyi.activiti.domain.BizProcesskeyDept;
import com.ruoyi.activiti.domain.BizProcesskeyRole;
import com.ruoyi.activiti.domain.dto.BizApplyLimitDTO;
import com.ruoyi.activiti.domain.vo.BizApplyLimitVO;
import com.ruoyi.activiti.mapper.BizProcesskeyDeptMapper;
import com.ruoyi.activiti.mapper.BizProcesskeyRoleMapper;
import com.ruoyi.activiti.service.BizApplyLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Author yrb
 * @Date 2023/4/19 15:09
 * @Version 1.0
 * @Description
 */
@Service
@Slf4j
public class BizApplyLimitServiceImpl implements BizApplyLimitService {
    private final BizProcesskeyDeptMapper bizProcesskeyDeptMapper;
    private final BizProcesskeyRoleMapper bizProcesskeyRoleMapper;

    public BizApplyLimitServiceImpl(BizProcesskeyDeptMapper bizProcesskeyDeptMapper,
                                    BizProcesskeyRoleMapper bizProcesskeyRoleMapper) {
        this.bizProcesskeyDeptMapper = bizProcesskeyDeptMapper;
        this.bizProcesskeyRoleMapper = bizProcesskeyRoleMapper;
    }

    /**
     * 插入限制信息
     *
     * @param bizApplyLimitDTO 限制信息
     * @return result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertLimitInfo(BizApplyLimitDTO bizApplyLimitDTO) {
        // 获取限制信息
        Set<String> processKeySet = new HashSet<>(bizApplyLimitDTO.getProcessKeyList());
        Set<Long> deptIdSet = new HashSet<>(bizApplyLimitDTO.getDeptIdList());
        Set<Integer> roleIdSet = new HashSet<>(bizApplyLimitDTO.getRoleIdList());
        for (String processKey : processKeySet) {
            // 删除原有的限制部门和角色
            bizProcesskeyDeptMapper.deleteByProcessKey(processKey);
            bizProcesskeyRoleMapper.deleteByProcessKey(processKey);
            if (CollUtil.isNotEmpty(deptIdSet)) {
                // 设置限制部门
                List<BizProcesskeyDept> bizProcesskeyDeptList = new ArrayList<>();
                for (Long deptId : deptIdSet) {
                    BizProcesskeyDept bizProcesskeyDept = new BizProcesskeyDept();
                    bizProcesskeyDept.setProcessKey(processKey);
                    bizProcesskeyDept.setDeptId(deptId);
                    bizProcesskeyDeptList.add(bizProcesskeyDept);
                }
                // 批量插入限制部门
                int i = bizProcesskeyDeptMapper.insertList(bizProcesskeyDeptList);
                if (i < 1) {
                    throw new RuntimeException("批量插入限制申请部门失败");
                }
            }
            if (CollUtil.isNotEmpty(roleIdSet)) {
                // 设置限制角色
                List<BizProcesskeyRole> bizProcesskeyRoleList = new ArrayList<>();
                for (Integer roleId : roleIdSet) {
                    BizProcesskeyRole bizProcesskeyRole = new BizProcesskeyRole();
                    bizProcesskeyRole.setProcessKey(processKey);
                    bizProcesskeyRole.setRoleId(roleId);
                    bizProcesskeyRoleList.add(bizProcesskeyRole);
                }
                // 批量插入限制角色
                int i = bizProcesskeyRoleMapper.insertList(bizProcesskeyRoleList);
                if (i < 1) {
                    throw new RuntimeException("批量插入限制申请角色失败");
                }
            }
        }
    }

    /**
     * 通过processKey获取限制信息
     *
     * @param processKey 费用类型唯一标识
     * @return result
     */
    @Override
    public BizApplyLimitVO findLimitInfo(String processKey) {
        List<Long> deptIdList = bizProcesskeyDeptMapper.selectDeptByBizProcesskey(processKey);
        List<Integer> roleIdList = bizProcesskeyRoleMapper.selectRoleByBizProcesskey(processKey);
        BizApplyLimitVO bizApplyLimitVO = new BizApplyLimitVO();
        bizApplyLimitVO.setDeptIdList(deptIdList);
        bizApplyLimitVO.setRoleIdList(roleIdList);
        return bizApplyLimitVO;
    }
}
