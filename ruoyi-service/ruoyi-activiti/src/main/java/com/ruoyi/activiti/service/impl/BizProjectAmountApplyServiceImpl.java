package com.ruoyi.activiti.service.impl;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.consts.UrlConstants;
import com.ruoyi.activiti.domain.SysAttachment;
import com.ruoyi.activiti.domain.dto.BizProjectAmountApplyDto;
import com.ruoyi.activiti.domain.my_apply.BizProjectAmountApply;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.ActReProcdefMapper;
import com.ruoyi.activiti.mapper.BizProjectAmountApplyMapper;
import com.ruoyi.activiti.service.BizProjectAmountApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteConfigService;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.DataScopeUtil;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 项目金额调整申请
 *
 * @author zh
 * @date 2023/03/23
 */
@Service
@Slf4j
public class BizProjectAmountApplyServiceImpl implements BizProjectAmountApplyService {
    private final RemoteUserService remoteUserService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final RemoteDeptService remoteDeptService;
    private final RemoteFileService remoteFileService;
    private final BizProjectAmountApplyMapper bizProjectAmountApplyMapper;
    private final DataScopeUtil dataScopeUtil;
    private final RemoteConfigService remoteConfigService;

    public BizProjectAmountApplyServiceImpl(RemoteUserService remoteUserService,
                                            ActReProcdefMapper actReProcdefMapper,
                                            IBizBusinessService bizBusinessService,
                                            RemoteDeptService remoteDeptService,
                                            RemoteFileService remoteFileService,
                                            BizProjectAmountApplyMapper bizProjectAmountApplyMapper,
                                            DataScopeUtil dataScopeUtil, RemoteConfigService remoteConfigService) {
        this.remoteUserService = remoteUserService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.remoteDeptService = remoteDeptService;
        this.remoteFileService = remoteFileService;
        this.bizProjectAmountApplyMapper = bizProjectAmountApplyMapper;
        this.dataScopeUtil = dataScopeUtil;
        this.remoteConfigService = remoteConfigService;
    }

    /**
     * 新增申请
     *
     * @param parameter
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(BizProjectAmountApply parameter) {
        try {
            // 抄送人去重
            String cc = parameter.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                parameter.setCc(cc);
            }
            String userName = SystemUtil.getUserName();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            parameter.setCreateTime(new Date());
            parameter.setUpdateTime(new Date());
            parameter.setCreateBy(userName);
            parameter.setUpdateBy(userName);
            // 申请人部门
            parameter.setDeptId(sysUser.getDeptId());
            parameter.setTitle(sysUser.getUserName() + "提交的项目金额修改申请");
            if (bizProjectAmountApplyMapper.insert(parameter) == 0) return 0;

            // 初始化流程
            BizBusiness business = initBusiness(parameter);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(parameter.getApplyCode());
            if (bizBusinessService.insertBizBusiness(business) == 0) {
                throw new RuntimeException("插入流程信息失败");
            }

            Map<String, Object> variables = Maps.newHashMap();
            // 这里可以设置各个负责人，key跟模型的代理变量一致
            variables.put("cc", parameter.getCc());
            bizBusinessService.startProcess(business, variables);

            // 将上传的临时文件转为有效文件
            if (remoteFileService.update(parameter.getId(), parameter.getApplyCode()) == 0) {
                throw new StatefulException("将上传的文件转为有效文件失败");
            }
            return 1;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    /**
     * 获取详情
     *
     * @param id
     * @return
     */
    @Override
    public BizProjectAmountApply selectById(Long id) {
        BizProjectAmountApply bizProjectAmountApply = bizProjectAmountApplyMapper.selectById(id);

        // 查询图片信息
        List<SysAttachment> certificate = remoteFileService.getList(bizProjectAmountApply.getId(), "xmxg-certificate");
        if (certificate == null) {
            certificate = new ArrayList<>();
        }
        bizProjectAmountApply.setVouchers(certificate);
        // 查询附件信息
        List<SysAttachment> appendix = remoteFileService.getList(bizProjectAmountApply.getId(), "xmxg-appendix");
        if (appendix == null) {
            appendix = new ArrayList<>();
        }

        bizProjectAmountApply.setAttachment(appendix);

        String ccStr = bizProjectAmountApply.getCc();
        if (StrUtil.isNotBlank(ccStr)) {
            List<String> ccList = new ArrayList<>();
            for (String cc : ccStr.split(",")) {
                SysUser ccUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
                ccList.add(ccUser.getUserName());
            }
            bizProjectAmountApply.setCcName(String.join("、", ccList));
        }
//        Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(salaryAdjustment.getDeptId());
//        //申请公司名称
//        salaryAdjustment.setCompanyName(belongCompany2.get("companyName").toString());
//        //申请部门名称
        bizProjectAmountApply.setDeptName(remoteDeptService.selectSysDeptByDeptId(bizProjectAmountApply.getDeptId()).getDeptName());

        return bizProjectAmountApply;
    }

    /**
     * 判断该项目是否处于审批状态
     *
     * @param identifier 项目编号
     * @return result
     */
    @Override
    public boolean isProcessing(String identifier) {
        QueryWrapper<BizProjectAmountApply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("t1.identifier", identifier);
        queryWrapper.eq("t2.`status`", 1);
        queryWrapper.eq("t2.proc_def_key", "pr_amount");
        if (bizProjectAmountApplyMapper.count(queryWrapper) > 0) {
            return true;
        }
        return false;
    }

    /**
     * 查询项目修改列表
     *
     * @param dto 条件信息
     * @return result
     */
    @Override
    public List<BizProjectAmountApplyDto> findBizProjectAmountApplyList(BizProjectAmountApplyDto dto) {

        QueryWrapper<BizProjectAmountApplyDto> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bu.proc_def_key", "pr_amount");
        queryWrapper.eq("bu.del_flag", 0);
        queryWrapper.eq("ra.del_flag", 0);
        // 查询单条
        queryWrapper.eq(dto.getId() != null, "ra.id", dto.getId());
        // 查询审批状态
        queryWrapper.eq(dto.getResult() != null, "bu.result", dto.getResult());
        // 查询审批状态
        queryWrapper.eq(dto.getStatus() != null, "bu.status", dto.getStatus());
        // 项目编号
        queryWrapper.eq(StrUtil.isNotBlank(dto.getIdentifier()), "ra.identifier", dto.getIdentifier());
        // 申请人
        queryWrapper.eq(StrUtil.isNotBlank(dto.getUserName()), "bu.applyer", dto.getUserName());
        // 审批结果
        queryWrapper.like(StringUtils.isNotBlank(dto.getUpResult()), "ra.up_result", dto.getUpResult());
        // 申请时间
        String createTime1 = dto.getCreateTime1();
        if (StrUtil.isNotBlank(createTime1)) {
            createTime1 = createTime1 + " 00:00:00";
        }
        String createTime2 = dto.getCreateTime2();
        if (StrUtil.isNotBlank(createTime2)) {
            createTime2 = createTime2 + " 23:59:59";
        }

        queryWrapper.between(StrUtil.isNotBlank(createTime1) && StrUtil.isNotBlank(createTime2), "ra.create_time", createTime1, createTime2);
        queryWrapper.orderByDesc("ra.id");
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        if (dto.getOneStatus() == null) {
            String sql = dataScopeUtil.getScopeSql(sysUser, "d", "u");

            if (StrUtil.isNotBlank(sql) && StrUtil.isBlank(dto.getIsDetails())) {
                queryWrapper.apply(sql);
            }
        }
        return bizProjectAmountApplyMapper.selectAll(queryWrapper);
    }

    @Override
    public String updateAgain(BizProjectAmountApplyDto bizProjectAmountApplyDto) {
        String returnMsg = "";
        BizProjectAmountApply bizProjectAmountApply = bizProjectAmountApplyMapper.selectById(bizProjectAmountApplyDto.getId());

        HashMap<String, Object> paramMap = new HashMap<>();
        //项目编号
        paramMap.put("identifier", bizProjectAmountApply.getIdentifier());
        //项目金额
        paramMap.put("totalMoney", bizProjectAmountApply.getNewTotalMoney());
        /** 业务费 */
        paramMap.put("commission", bizProjectAmountApply.getNewCommission());
        /** 评审费 */
        paramMap.put("evaluationFee", bizProjectAmountApply.getNewEvaluationFee());
        /** 服务费  */
        paramMap.put("serviceCharge", bizProjectAmountApply.getNewServiceCharge());
        /** 分包费  */
        paramMap.put("subprojectFee", bizProjectAmountApply.getNewSubprojectFee());
        /** 虚拟税费 */
        paramMap.put("virtualTax", bizProjectAmountApply.getNewVirtualTax());
        /** 其他支出 */
        paramMap.put("otherExpenses", bizProjectAmountApply.getNewOtherExpenses());
        cn.hutool.json.JSONObject josmmap = JSONUtil.parseObj(paramMap);
        String msg = null;
        try {
            SysConfig configUrl = remoteConfigService.findConfigUrl();
            if ("test".equals(configUrl.getConfigValue())) {
                msg = HttpUtil.post(UrlConstants.JPUPDATE_TEST, josmmap.toString());
            } else {
                msg = HttpUtil.post(UrlConstants.JPUPDATE_ONLINE, josmmap.toString());
            }
            JSONObject jsonObject = JSON.parseObject(msg);
            Object o = jsonObject.get("code");
            if ("200".equals(o.toString())) {
                bizProjectAmountApply.setUpResult("修改项目金额成功");
                returnMsg = "同步项目金额成功";
            } else {
                bizProjectAmountApply.setUpResult("修改项目金额失败");
                returnMsg = "同步项目金额失败";
            }
        } catch (Exception e) {
            log.error("修改项目金额失败" + msg);
            bizProjectAmountApply.setUpResult("修改项目金额失败");

        }
        bizProjectAmountApply.setUpResultInfo(msg);
        bizProjectAmountApplyMapper.updateById(bizProjectAmountApply);

        return returnMsg;
    }

    /**
     * 初始化业务流程
     *
     * @param bizProjectAmountApply
     * @return
     */
    private BizBusiness initBusiness(BizProjectAmountApply bizProjectAmountApply) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "pr_amount");
        example.setOrderByClause("VERSION_ DESC");
        ActReProcdef actReProcdef = actReProcdefMapper.selectByExample(example).get(0);

        BizBusiness business = new BizBusiness();
        business.setTableId(bizProjectAmountApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(bizProjectAmountApply.getTitle());
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
}
