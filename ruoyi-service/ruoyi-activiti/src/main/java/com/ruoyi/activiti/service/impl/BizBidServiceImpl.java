package com.ruoyi.activiti.service.impl;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.SysAttachment;
import com.ruoyi.activiti.domain.my_apply.BizBidApply;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.ActReProcdefMapper;
import com.ruoyi.activiti.mapper.BizBidApplyMapper;
import com.ruoyi.activiti.service.BizBidService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author yrb
 * @Date 2023/5/31 15:12
 * @Version 1.0
 * @Description 合同审批
 */
@Service
@Slf4j
public class BizBidServiceImpl implements BizBidService {
    private final BizBidApplyMapper bizBidApplyMapper;
    private final RemoteUserService remoteUserService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final RemoteFileService remoteFileService;
    private final RemoteDeptService remoteDeptService;

    public BizBidServiceImpl(BizBidApplyMapper bizBidApplyMapper,
                             RemoteUserService remoteUserService,
                             ActReProcdefMapper actReProcdefMapper,
                             IBizBusinessService bizBusinessService,
                             RemoteFileService remoteFileService,
                             RemoteDeptService remoteDeptService) {
        this.bizBidApplyMapper = bizBidApplyMapper;
        this.remoteUserService = remoteUserService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.remoteFileService = remoteFileService;
        this.remoteDeptService = remoteDeptService;
    }

    /**
     * 插入招标信息
     *
     * @param bizBidApply 招标信息
     * @return result
     */
    @Override
    public int insert(BizBidApply bizBidApply) {
        try {
            // 抄送人去重
            String cc = bizBidApply.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                bizBidApply.setCc(cc);
            }
            String userName = SystemUtil.getUserName();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            bizBidApply.setTitle(sysUser.getUserName() + "提交的投标审批");
            bizBidApply.setCreateTime(new Date());
            bizBidApply.setCreateBy(userName);
            int insert = bizBidApplyMapper.insert(bizBidApply);
            if (insert == 0) return 0;

            BizBusiness bizBusiness = initBusiness(bizBidApply);
            bizBusiness.setCompanyId(SystemUtil.getCompanyId());
            bizBusiness.setCompanyName(SystemUtil.getCompanyName());
            bizBusiness.setDeptId(SystemUtil.getDeptId());
            bizBusiness.setDeptName(sysUser.getDept().getDeptName());
            bizBusiness.setApplyCode(bizBidApply.getApplyCode());
            int i = bizBusinessService.insertBizBusiness(bizBusiness);
            if (i == 0) {
                throw new RuntimeException("插入通用流程信息失败");
            }

            Map<String, Object> variables = Maps.newHashMap();
            // 这里可以设置各个负责人，key跟模型的代理变量一致
            variables.put("cc", bizBidApply.getCc());
            bizBusinessService.startProcess(bizBusiness, variables);

            // 将上传的临时文件转为有效文件
            int reimburse = remoteFileService.update(bizBidApply.getId(), bizBusiness.getApplyCode());
            if (reimburse == 0) {
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
     * 查询招标审批详情
     *
     * @param id 主键id
     * @return 详情信息
     */
    @Override
    public BizBidApply findDetail(String id) {
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(id);
        BizBidApply bizBidApply = bizBidApplyMapper.selectById(bizBusiness.getTableId());
        // 申请公司名称
        bizBidApply.setComName(remoteDeptService.selectSysDeptByDeptId(bizBidApply.getComId()).getDeptName());
        // 申请部门名称
        bizBidApply.setDeptName(remoteDeptService.selectSysDeptByDeptId(bizBidApply.getDeptId()).getDeptName());
        // 抄送人
        String ccStr = bizBidApply.getCc();
        if (StrUtil.isNotBlank(ccStr)) {
            List<String> ccList = new ArrayList<>();
            for (String cc : ccStr.split(",")) {
                SysUser ccUser = remoteUserService.selectSysUserByUserId(Long.parseLong(cc));
                ccList.add(ccUser.getUserName());
            }
            bizBidApply.setCcName(String.join("、", ccList));
        }
        // 付款附件
        List<SysAttachment> appendix = remoteFileService.getList(Long.parseLong(bizBusiness.getTableId()), "bid-appendix");
        if (appendix == null) {
            appendix = new ArrayList<>();
        }
        bizBidApply.setAttachment(appendix);
        return bizBidApply;
    }

    /**
     * 初始化业务流程
     *
     * @param bizBidApply 审批信息
     * @return result
     */
    private BizBusiness initBusiness(BizBidApply bizBidApply) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "bid");
        example.setOrderByClause("VERSION_ DESC");
        ActReProcdef actReProcdef = actReProcdefMapper.selectByExample(example).get(0);

        BizBusiness business = new BizBusiness();
        business.setTableId(bizBidApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(bizBidApply.getTitle());
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
