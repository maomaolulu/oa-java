package com.ruoyi.activiti.service.impl;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.proc.BizNode;
import com.ruoyi.activiti.mapper.BizNodeMapper;
import com.ruoyi.activiti.service.IBizNodeService;
import com.ruoyi.activiti.vo.ProcessNodeVo;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteRoleService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 节点Service业务层处理
 *
 * @author ruoyi
 * @date 2020-01-07
 */
@Service
@Slf4j
public class BizNodeServiceImpl implements IBizNodeService {
    @Autowired
    private BizNodeMapper bizNodeMapper;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private RemoteDeptService remoteDeptService;
    @Autowired
    private RemoteRoleService remoteRoleService;
    @Value("${customer_service}")
    private String customerCompany;

    /**
     * 查询节点
     *
     * @param id 节点ID
     * @return 节点
     */
    @Override
    public BizNode selectBizNodeById(Long id) {
        return bizNodeMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询节点列表
     *
     * @param bizNode 节点
     * @return 节点
     */
    @Override
    public List<BizNode> selectBizNodeList(BizNode bizNode) {
        return bizNodeMapper.select(bizNode);
    }

    /**
     * 新增节点
     *
     * @param bizNode 节点
     * @return 结果
     */
    @Override
    public int insertBizNode(BizNode bizNode) {
        return bizNodeMapper.insertSelective(bizNode);
    }

    /* (non-Javadoc)
     * @see com.ruoyi.activiti.service.IBizNodeService#setAuditors(java.lang.String)
     */
    @Override
    public ProcessNodeVo setAuditors(ProcessNodeVo node) {
        List<BizNode> bizNodes = selectBizNodeList(new BizNode().setNodeId(node.getNodeId()));
        List<Long> roleIds = Lists.newArrayList();
        List<Long> userIds = Lists.newArrayList();
        List<Long> deptIds = Lists.newArrayList();
        for (BizNode bizNode : bizNodes) {
            // 设置关联角色
            if (bizNode.getType().equals(ActivitiConstant.NODE_ROLE)) {
                roleIds.add(bizNode.getAuditor());
            }
            // 设置关联部门
            else if (bizNode.getType().equals(ActivitiConstant.NODE_DEPARTMENT)) {
                deptIds.add(bizNode.getAuditor());
            }
            // 设置关联用户
            else if (bizNode.getType().equals(ActivitiConstant.NODE_USER)) {
                userIds.add(bizNode.getAuditor());
            } else if (bizNode.getType().equals(ActivitiConstant.NODE_DEP_HEADER)) {
                // 是否设置操作人负责人
                node.setDeptHeader(true);
            } else if (bizNode.getType().equals(ActivitiConstant.NODE_FIRST_DEP_HEADER)) {
                // 是否设置操作人负责人
                node.setFirstDeptHeader(true);
            } else if (bizNode.getType().equals(ActivitiConstant.NODE_APPLICANT)) {
                node.setApplicant(true);
            }
        }
        // 设置关联角色
        node.setRoleIds(roleIds);
        // 设置关联部门
        node.setDeptIds(deptIds);
        // 设置关联用户
        node.setUserIds(userIds);
        // 设置是否会签
        node.setCountersigned(false);
        return node;
    }

    /* (non-Javadoc)
     * @see com.ruoyi.activiti.service.IBizNodeService#updateBizNode(com.ruoyi.activiti.vo.ProcessNodeVo)
     */
    @Override
    public int updateBizNode(ProcessNodeVo node) {
        // 删除所有节点信息
        bizNodeMapper.delete(new BizNode().setNodeId(node.getNodeId()));
        List<BizNode> bizNodes = Lists.newArrayList();
        List<Long> roleIds = node.getRoleIds();
        if (null != roleIds && roleIds.size() > 0) {
            for (Long roleId : roleIds) {
                bizNodes.add(new BizNode().setNodeId(node.getNodeId()).setType(ActivitiConstant.NODE_ROLE)
                        .setAuditor(roleId));
            }
        }
        List<Long> deptIds = node.getDeptIds();
        if (null != deptIds && deptIds.size() > 0) {
            for (Long deptId : node.getDeptIds()) {
                bizNodes.add(new BizNode().setNodeId(node.getNodeId()).setType(ActivitiConstant.NODE_DEPARTMENT)
                        .setAuditor(deptId));
            }
        }
        List<Long> userIds = node.getUserIds();
        if (null != userIds && userIds.size() > 0) {
            for (Long userId : userIds) {
                bizNodes.add(new BizNode().setNodeId(node.getNodeId()).setType(ActivitiConstant.NODE_USER)
                        .setAuditor(userId));
            }
        }
        if (null != node.getDeptHeader() && node.getDeptHeader()) {
            bizNodes.add(new BizNode().setNodeId(node.getNodeId()).setType(ActivitiConstant.NODE_DEP_HEADER));
        }
        if (null != node.getFirstDeptHeader() && node.getFirstDeptHeader()) {
            bizNodes.add(new BizNode().setNodeId(node.getNodeId()).setType(ActivitiConstant.NODE_FIRST_DEP_HEADER));
        }
        if (null != node.getApplicant() && node.getApplicant()) {
            bizNodes.add(new BizNode().setNodeId(node.getNodeId()).setType(ActivitiConstant.NODE_APPLICANT));
        }
        return bizNodes.isEmpty() ? 0 : bizNodeMapper.insertList(bizNodes);
    }

    /* (non-Javadoc)
     * @see com.ruoyi.activiti.service.IBizNodeService#getAuditors(java.lang.String)
     */
    @Override
    public Set<String> getAuditors(String nodeId, long userId, String procDefKey) {
        log.error("节点id:{}，用户id:{}，procDefKey:{}，当前登录人:{}", nodeId, userId, procDefKey, SystemUtil.getUserNameCn());
        // 申请人，只有下一节点审批人有其公司的审批权限或与其同公司才会进行任务委派
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
//        Long deptId = sysUser.getDept().getParentId();
        Long deptId = sysUser.getDept().getDeptId();
        // TODO 优化查询次数可以将同类型审核人一次性查询得到
        List<BizNode> bizNodes = selectBizNodeList(new BizNode().setNodeId(nodeId));
        Set<Long> auditors = Sets.newHashSet();
        Set<Long> roleIds = Sets.newHashSet();
        Set<Long> roleIds2 = Sets.newHashSet();
        Set<Long> deptIds = Sets.newHashSet();
        if (null != bizNodes && bizNodes.size() > 0) {
            for (BizNode node : bizNodes) {
                if (node.getType().equals(ActivitiConstant.NODE_USER)) {
                    // 如果是用户类型直接塞到审核人结合
                    auditors.add(node.getAuditor());
                } else if (node.getType().equals(ActivitiConstant.NODE_ROLE)) {
                    Long roleId = node.getAuditor();
                    boolean flag = isBizProjectAmountApply(procDefKey, deptId, roleId);
                    if (flag) {
                        // 项目金额修改流程处理公司负责人角色
                        auditors.add(6L);
                    } else {
                        SysRole sysRole = remoteRoleService.selectSysRoleByRoleId(roleId);
                        if (sysRole == null) {
                            continue;
                        }
                        log.error(sysRole.getRoleName());
                        Set<Long> auditors1 = bizNodeMapper.getAuditors(deptId, sysRole.getRoleId());
                        auditors.addAll(auditors1);
                        if ("副总裁".equals(sysRole.getRoleName())) {
                            String companyId = remoteDeptService.getBelongCompany2(deptId).get("companyId").toString();
                            String ehs = "115;118;119;161;172;168;184;350;356;";
                            if (ehs.contains(companyId + ";")) {
                                auditors.add(5L);
                            }
                            String fac = "131;228;231;179;224;225;227;230;229;227;226;232;338;";
                            if (fac.contains(companyId + ";")) {
                                auditors.add(500L);
                            }
                            String tsc = "340;183;281;";
                            if (tsc.contains(companyId + ";")) {
                                auditors.add(549L);
                            }
                            continue;
                        } else if ("研发部负责人".equals(sysRole.getRoleName())) {
                            Set<Long> roleIdSet = Sets.newHashSet();
                            roleIdSet.add(roleId);
                            Set<Long> longs = remoteUserService.selectUserIdsHasRoles(StrUtil.join(",", roleIdSet.toArray()));
                            // 只能有一个研发部负责人
                            if (longs.size() > 1) {
                                log.error("配置了多个研发部负责人");
                            } else if (longs.size() == 1) {
                                auditors.addAll(longs);
                            }
                        }
                        if (",132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,,149,150,151,129,130,131,".contains("," + roleId + ",")) {
                            continue;
                        }

                        if (StrUtil.isBlank(sysRole.getRoleDept())) {
                            // 查询所有拥有有当前角色编号的用户
                            // 角色没有审批权限则按个人审批权限过滤
                            roleIds.add(node.getAuditor());
                        } else {
                            // 角色审批权限与个人审批权限并集
                            roleIds.add(node.getAuditor());
                            // 有设置角色自定义审批权限才会过滤
                            // 过滤有申请人部门审批权限的角色
                            List<String> strings = Arrays.asList(sysRole.getRoleDept().split(";"));
                            if (strings.contains(deptId.toString())) {
                                roleIds2.add(node.getAuditor());
                            }
                        }
                    }
                } else if (node.getType().equals(ActivitiConstant.NODE_DEPARTMENT)) {
                    // 查询所有此部门的用户
                    deptIds.add(node.getAuditor());
                } else if (node.getType().equals(ActivitiConstant.NODE_DEP_HEADER)) {
                    // 有客户服务部的公司，由客户服务部节点无需部门主管审批
                    if ("kehu".equals(nodeId) && customerCompany.contains(";" + sysUser.getCompanyId().toString() + ";") && "review".equals(procDefKey)) {
                        continue;
                    }
                    SysUser user = remoteUserService.selectSysUserByUserId(userId);
                    SysDept dept = remoteDeptService.selectSysDeptByDeptId(user.getDeptId());
                    // 查询所有拥有当前用户部门的负责人
                    auditors.add(dept.getLeaderId());
                } else if (node.getType().equals(ActivitiConstant.NODE_FIRST_DEP_HEADER)) {
                    SysUser user = remoteUserService.selectSysUserByUserId(userId);
                    Map<String, Object> firstDept = remoteDeptService.getFirstDept(user.getDeptId());
                    if (firstDept.containsKey("leaderId") && firstDept.get("leaderId") != null) {
                        // 查询所有拥有当前用户部门的负责人
                        auditors.add(Long.valueOf(firstDept.get("leaderId").toString()));
                    }
                } else if (node.getType().equals(ActivitiConstant.NODE_APPLICANT)) {
                    auditors.add(userId);
                }
            }
        }
        if (roleIds.size() > 0) {
            Set<Long> longs = remoteUserService.selectUserIdsHasRoles(StrUtil.join(",", roleIds.toArray()));
//            auditors.addAll(longs);
            // 过滤有申请人所在部门审批权限的用户
            for (Long aLong : longs) {
                SysUser sysUserAudit = remoteUserService.selectSysUserByUserId(aLong);
//                Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUserAudit.getDeptId());
//                Long companyId = Long.valueOf(belongCompany2.get("companyId").toString());

                if (StrUtil.isNotEmpty(sysUserAudit.getOtherDeptId())) {
                    List<String> strings = Arrays.asList(sysUserAudit.getOtherDeptId().split(";"));
                    if (strings.contains(deptId.toString())) {
                        auditors.add(aLong);
                    }
                }
//                if (deptId.equals(companyId)) {
//                    auditors.add(aLong);
//                }

            }
        }
        // 绑定有审批权限角色的用户直接委派
        if (roleIds2.size() > 0) {
            Set<Long> longs = remoteUserService.selectUserIdsHasRoles(StrUtil.join(",", roleIds2.toArray()));
            auditors.addAll(longs);
        }
        if (deptIds.size() > 0) {
            auditors.addAll(remoteUserService.selectUserIdsInDepts(StrUtil.join(",", deptIds.toArray())));
        }
        Set<String> collect = auditors.stream().filter(f -> f != null).map(m -> m.toString()).collect(Collectors.toSet());
        // 已离职人员去掉
        Iterator<String> iterator = collect.iterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            SysUser user = remoteUserService.selectSysUserByUserId(Long.valueOf(s));
            if ("1".equals(user.getStatus())) {
                iterator.remove();
            }

        }
//        for (String s : collect) {
//            SysUser user = remoteUserService.selectSysUserByUserId(Long.valueOf(s));
//            if("1".equals(user.getStatus())){
//                collect.remove(s);
//            }
//        }
        if (collect.isEmpty()) {
//            throw new StatefulException(292,"无下个节点审批人，请联系管理员");
            log.error("无下个节点审批人，请联系管理员,节点id:{}，用户id:{}，procDefKey:{}，当前登录人:{}", nodeId, userId, procDefKey, SystemUtil.getUserNameCn());
        }
        return collect;
    }

    /**
     * 项目金额修改处理公司总经理审批人
     *
     * @param procDefKey 流程唯一标识
     * @param deptId     部门id
     * @param roleId     角色id
     * @return 结果
     */
    private boolean isBizProjectAmountApply(String procDefKey, Long deptId, Long roleId) {
        if ("pr_amount".equals(procDefKey) && 151L == roleId) {
            SysDept sysDept = remoteDeptService.selectSysDeptByDeptId(deptId);
            if (StrUtil.isNotBlank(sysDept.getAncestors()) && sysDept.getAncestors().contains("115")) {
                return true;
            }
        }
        return false;
    }
}
