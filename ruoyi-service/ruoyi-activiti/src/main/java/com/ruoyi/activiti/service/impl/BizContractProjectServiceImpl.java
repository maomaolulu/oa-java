package com.ruoyi.activiti.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.consts.UrlConstants;
import com.ruoyi.activiti.domain.my_apply.BizContractProject;
import com.ruoyi.activiti.domain.my_apply.BizCpType;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.mapper.ActReProcdefMapper;
import com.ruoyi.activiti.mapper.BizBusinessTestMapper;
import com.ruoyi.activiti.mapper.BizContractProjectMapper;
import com.ruoyi.activiti.mapper.BizCpTypeMapper;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.IBizContractProjectService;
import com.ruoyi.activiti.service.YunYingCommonService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.activiti.utils.CodeUtil;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteConfigService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @Author yrb
 * @Date 2023/6/13 15:17
 * @Version 1.0
 * @Description 合同项目信息修改
 */
@Service
@Slf4j
public class BizContractProjectServiceImpl implements IBizContractProjectService {
    private final BizCpTypeMapper bizCpTypeMapper;
    private final RemoteUserService remoteUserService;
    private final BizContractProjectMapper bizContractProjectMapper;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final BizBusinessTestMapper bizBusinessTestMapper;
    private final RemoteConfigService remoteConfigService;
    private final YunYingCommonService yunYingCommonService;

    public BizContractProjectServiceImpl(BizCpTypeMapper bizCpTypeMapper,
                                         RemoteUserService remoteUserService,
                                         BizContractProjectMapper bizContractProjectMapper,
                                         ActReProcdefMapper actReProcdefMapper,
                                         IBizBusinessService bizBusinessService,
                                         BizBusinessTestMapper bizBusinessTestMapper,
                                         RemoteConfigService remoteConfigService,
                                         YunYingCommonService yunYingCommonService) {
        this.bizCpTypeMapper = bizCpTypeMapper;
        this.remoteUserService = remoteUserService;
        this.bizContractProjectMapper = bizContractProjectMapper;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.bizBusinessTestMapper = bizBusinessTestMapper;
        this.remoteConfigService = remoteConfigService;
        this.yunYingCommonService = yunYingCommonService;
    }

    /**
     * 插入合同项目信息
     *
     * @param bizContractProject 合同项目信息
     * @return result
     */
    @Override
    public int insert(BizContractProject bizContractProject) {
        try {
            if (!isUsing(bizContractProject.getIdentifierNew())) {
                return -1;
            }
            // 判断角色或部门是否可以发起申请
            Long comId = SystemUtil.getCompanyId();
            Long deptId = SystemUtil.getDeptId();
            Long userId = SystemUtil.getUserId();
            List<Long> list = new ArrayList<>();
            if (115 == comId) {
                // 杭州安联
                List<Long> temp = bizBusinessTestMapper.getDeptNum("客户服务部");
                if (!temp.contains(deptId)) {
                    return -999;
                }
            } else if (118 == comId) {
                // 嘉兴安联
                List<Long> temp = bizBusinessTestMapper.getDeptNum("客户服务部");
                if (!temp.contains(deptId)) {
                    return -999;
                }
            } else if (119 == comId) {
                // 宁波安联 刘益梅
                list.add(701L);
                // 陈雷
                list.add(1161L);
                if (!list.contains(userId)) {
                    return -999;
                }
            } else if (350 == comId) {
                // 亿达检测 吕珊
                list.add(794L);
                if (!list.contains(userId)) {
                    return -999;
                }
            } else if (161 == comId) {
                // 上海量远 邹雨薇
                list.add(719L);
                if (!list.contains(userId)) {
                    return -999;
                }
            } else {
                return -999;
            }

            // 抄送人去重
            String cc = bizContractProject.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                bizContractProject.setCc(cc);
            }
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            String userName = sysUser.getUserName();
            bizContractProject.setTitle(userName + "提交的合同项目修改申请");
            bizContractProject.setCreateTime(new Date());
            bizContractProject.setCreateBy(userName);
            bizContractProject.setApplyCode(CodeUtil.getCode("HTXM"));
            String applier = SystemUtil.getCompanyName() + "/" + sysUser.getDept().getDeptName() + "/" + userName;
            bizContractProject.setApplier(applier);
            bizContractProjectMapper.insert(bizContractProject);

            BizBusiness bizBusiness = initBusiness(bizContractProject);
            bizBusiness.setCompanyId(SystemUtil.getCompanyId());
            bizBusiness.setCompanyName(SystemUtil.getCompanyName());
            bizBusiness.setDeptId(SystemUtil.getDeptId());
            bizBusiness.setDeptName(sysUser.getDept().getDeptName());
            bizBusiness.setApplyCode(bizContractProject.getApplyCode());
            int i = bizBusinessService.insertBizBusiness(bizBusiness);
            if (i == 0) {
                throw new RuntimeException("插入通用流程信息失败");
            }

            Map<String, Object> variables = Maps.newHashMap();
            // 这里可以设置各个负责人，key跟模型的代理变量一致
            variables.put("cc", bizContractProject.getCc());
            bizBusinessService.startProcess(bizBusiness, variables);

            return 1;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    /**
     * 初始化业务流程
     *
     * @param bizContractProject 审批信息
     * @return result
     */
    private BizBusiness initBusiness(BizContractProject bizContractProject) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "contract-project");
        example.setOrderByClause("VERSION_ DESC");
        ActReProcdef actReProcdef = actReProcdefMapper.selectByExample(example).get(0);

        BizBusiness business = new BizBusiness();
        business.setTableId(bizContractProject.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(bizContractProject.getTitle());
        business.setProcName(actReProcdef.getName());
        long userId = SystemUtil.getUserId();
        business.setUserId(userId);

        SysUser user = remoteUserService.selectSysUserByUserId(userId);
        business.setApplyer(user.getUserName());
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        return business;
    }

    /**
     * 获取合同、项目类型信息
     *
     * @return tree
     */
    @Override
    public List<BizCpType> getTree() {
        List<BizCpType> result = new ArrayList<>();
        List<BizCpType> list = bizCpTypeMapper.selectList(new QueryWrapper<>());
        Map<Long, BizCpType> map = list.stream().collect(Collectors.toMap(BizCpType::getId, BizCpType -> BizCpType));
        list.forEach(type -> {
            if (type.getParentId() == 0) {
                result.add(type);
            } else {
                BizCpType bizCpType = map.get(type.getParentId());
                bizCpType.getChildren().add(type);
            }
        });
        return result;
    }

    /**
     * 查询合同项目修改审批详情
     *
     * @param id 业务类主键id
     * @return 详情信息
     */
    @Override
    public BizContractProject findDetail(String id) {
        // 通用信息
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(id);
        // 业务类
        BizContractProject bizContractProject = bizContractProjectMapper.selectById(bizBusiness.getTableId());
        // 抄送人
        String ccStr = bizContractProject.getCc();
        if (StrUtil.isNotBlank(ccStr)) {
            List<String> ccList = new ArrayList<>();
            for (String cc : ccStr.split(",")) {
                SysUser ccUser = remoteUserService.selectSysUserByUserId(Long.parseLong(cc));
                ccList.add(ccUser.getUserName());
            }
            bizContractProject.setCcName(String.join("、", ccList));
        }
        return bizContractProject;
    }

    /**
     * 获取合同项目信息
     *
     * @param identifier 项目编号
     * @return result
     */
    @Override
    public List<BizContractProject> findInfo(String identifier) {
        List<BizContractProject> list = new ArrayList<>();
        String configValue = remoteConfigService.findConfigUrl().getConfigValue();
        // 获取token
        String token = yunYingCommonService.getToken("contract-project");
        if (StrUtil.isBlank(token)) {
            log.error("用户" + SystemUtil.getUserNameCn() + "未获取到token" + ",项目编号" + identifier);
            return list;
        }
        // 通过项目编号获取项目信息
        HashMap<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("identifier", identifier);
        String response;
        if ("test".equals(configValue)) {
            response = HttpUtil.createGet(UrlConstants.YY_CP_SELECT_TEST)
                    .header("token", token)
                    .form(paramMap)
                    .execute().body();
        } else {
            response = HttpUtil.createGet(UrlConstants.YY_CP_SELECT_ONLINE)
                    .header("token", token)
                    .form(paramMap)
                    .execute().body();
        }
        Map<String, Object> map = JSON.parseObject(response);
        Integer code = (Integer) map.get("code");
        if (200 != code) {
            log.error("项目编号" + identifier + ",对应的数据不存在");
            return list;
        }
        list = JSON.parseArray(String.valueOf(map.get("data")), BizContractProject.class);
        return list;
    }

    /**
     *
     */
    @Override
    public void test(String identifier) {
        isUsing(identifier);
    }

    /**
     * 获取合同类型、项目类型
     *
     * @return result
     */
    @Override
    public List<Map> getType() {
        // 获取token
        String token = yunYingCommonService.getToken("contract-project");
        String configValue = remoteConfigService.findConfigUrl().getConfigValue();
        String response;
        if ("test".equals(configValue)) {
            response = HttpUtil.createGet(UrlConstants.YY_CP_TYPE_TEST)
                    .header("token", token)
                    .form(Maps.newHashMap())
                    .execute().body();
        } else {
            response = HttpUtil.createGet(UrlConstants.YY_CP_TYPE_ONLINE)
                    .header("token", token)
                    .form(Maps.newHashMap())
                    .execute().body();
        }
        Map<String, Object> map = JSON.parseObject(response);
        return JSON.parseArray(String.valueOf(map.get("data")), Map.class);
    }

    /**
     * 判断项目编号是否被占用
     *
     * @param identifier 项目编号
     * @return result
     */
    private boolean isUsing(String identifier) {
        // 获取token
        String token = yunYingCommonService.getToken("contract-project");
        SysConfig sysConfig = remoteConfigService.findConfigUrl();
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("identifier", identifier);
        String response;
        if ("test".equals(sysConfig.getConfigValue())) {
            response = HttpUtil.createGet(UrlConstants.YY_CP_USING_TEST)
                    .header("token", token)
                    .form(paramMap)
                    .execute().body();
        } else {
            response = HttpUtil.createGet(UrlConstants.YY_CP_USING_ONLINE)
                    .header("token", token)
                    .form(paramMap)
                    .execute().body();
        }
        Map<String, Object> map = JSON.parseObject(response);
        Integer code = (Integer) map.get("code");
        if (200 != code) {
            return false;
        }
        return true;
    }
}
