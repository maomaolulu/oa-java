package com.ruoyi.activiti.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.SysAttachment;
import com.ruoyi.activiti.domain.dto.BizReviewApplyDto;
import com.ruoyi.activiti.domain.fiance.BizReviewApply;
import com.ruoyi.activiti.domain.fiance.BizReviewInfo;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.purchase.BizGoodsInfo;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.ActReProcdefMapper;
import com.ruoyi.activiti.mapper.BizReviewApplyMapper;
import com.ruoyi.activiti.mapper.BizReviewInfoMapper;
import com.ruoyi.activiti.service.BizReviewApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.DataScopeUtil;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 审批服务费申请业务层处理
 *
 * @author zh
 * @date 2021-11-20
 */
@Service
@Slf4j
public class BizReviewApplyServiceImpl implements BizReviewApplyService {
    private final BizReviewApplyMapper bizReviewApplyMapper;
    private final RemoteUserService remoteUserService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final BizReviewInfoMapper bizReviewInfoMapper;
    private final RemoteFileService remoteFileService;
    private final RemoteDeptService remoteDeptService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private DataScopeUtil dataScopeUtil;
    @Autowired
    private TaskService taskService;


    @Autowired
    public BizReviewApplyServiceImpl(BizReviewApplyMapper bizReviewApplyMapper, RemoteUserService remoteUserService, ActReProcdefMapper actReProcdefMapper, IBizBusinessService bizBusinessService, BizReviewInfoMapper bizReviewInfoMapper, RemoteFileService remoteFileService, RemoteDeptService remoteDeptService) {
        this.bizReviewApplyMapper = bizReviewApplyMapper;
        this.remoteUserService = remoteUserService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.bizReviewInfoMapper = bizReviewInfoMapper;
        this.remoteFileService = remoteFileService;
        this.remoteDeptService = remoteDeptService;
    }

    /**
     * 查询评审服务申请
     *
     * @param id 评审服务申请ID
     * @return 评审服务申请
     */
    @Override
    public BizReviewApplyDto selectBizReviewApplyById(Long id) {
        BizReviewApplyDto bizReviewApplyDto = new BizReviewApplyDto();
        bizReviewApplyDto.setId(id);
        bizReviewApplyDto.setIsDetails("1");
        bizReviewApplyDto.setOneStatus(1);
        List<BizReviewApplyDto> bizReviewApplyDtos = this.selectBizReviewApply(bizReviewApplyDto);
        if (bizReviewApplyDtos != null && bizReviewApplyDtos.size() > 0) {
            return bizReviewApplyDtos.get(0);
        } else {
            return new BizReviewApplyDto();
        }

    }

    /**
     * 查询评审服务申请列表
     *
     * @param dto 评审服务申请
     * @return 评审服务申请
     */
    @Override
    public List<BizReviewApplyDto> selectBizReviewApply(BizReviewApplyDto dto) {
        QueryWrapper<BizReviewApply> bizReviewApplyQueryWrapper = new QueryWrapper<>();
        bizReviewApplyQueryWrapper.eq("bu.proc_def_key", "review");
        bizReviewApplyQueryWrapper.eq("bu.del_flag", 0);
        bizReviewApplyQueryWrapper.eq("ra.del_flag", 0);
        //查询单条
        bizReviewApplyQueryWrapper.eq(dto.getId() != null, "ra.id", dto.getId());
        //查询审批状态
        bizReviewApplyQueryWrapper.eq(dto.getResult() != null, "bu.result", dto.getResult());
        //查询审批状态
        bizReviewApplyQueryWrapper.eq(dto.getStatus() != null, "bu.status", dto.getStatus());
        /**评审服务申请编号*/
        bizReviewApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getReviewCode()), "ra.review_code", dto.getReviewCode());
        /**申请人*/
        bizReviewApplyQueryWrapper.eq(StrUtil.isNotBlank(dto.getUserName()), "u.user_name", dto.getUserName());
        /**申请时间*/
        String createTime1 = dto.getCreateTime1();
        String createTime2 = dto.getCreateTime2();

        bizReviewApplyQueryWrapper.between(StrUtil.isNotBlank(createTime1) && StrUtil.isNotBlank(createTime2), "ra.create_time", createTime1, createTime2);
        bizReviewApplyQueryWrapper.orderByDesc("ra.id");
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        if (dto.getOneStatus() == null) {
            String sql = dataScopeUtil.getScopeSql(sysUser, "d", "u");

            if (StrUtil.isNotBlank(sql) && StrUtil.isBlank(dto.getIsDetails())) {
                bizReviewApplyQueryWrapper.apply(sql);
            }
        }

        List<BizReviewApplyDto> list = bizReviewApplyMapper.selectAll(bizReviewApplyQueryWrapper);

        return list;
    }

    /**
     * 评审服务费申请新增
     *
     * @param bizReviewApply 评审、服务费新增
     * @return 结果
     */
    @Override
    public R insertBizReviewApply(BizReviewApply bizReviewApply) {
        try {
            // 抄送人去重
            String cc = bizReviewApply.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                bizReviewApply.setCc(cc);
            }
            bizReviewApply.setDelFlag("0");
            bizReviewApply.setCreateBy(SystemUtil.getUserName());
            bizReviewApply.setCreateTime(new Date());
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyId1 = belongCompany2.get("companyId").toString();
            bizReviewApply.setDeptId(sysUser.getDeptId());
            bizReviewApply.setCompanyId(Long.valueOf(companyId1));
//            String purchaseCode = "CG" + UUID.fastUUID().toString().substring(0, 8);
//            bizReviewApply.setReviewCode(purchaseCode);
            bizReviewApply.setUserId(SystemUtil.getUserId());


            bizReviewApply.setTitle(sysUser.getUserName() + "提交的评审服务费申请");
            bizReviewApplyMapper.insert(bizReviewApply);
            List<BizReviewInfo> bizReviewInfos = bizReviewApply.getBizReviewInfos();
//            if (bizGoodsInfos.isEmpty()) {
//                return R.error("请添加物品");
//            }
            //款项用途（1评审费、2服务费、3其他费用）
            String types = bizReviewApply.getTypes();
            for (BizReviewInfo bizReviewInfo : bizReviewInfos
            ) {
                if (types.equals("1")) {
                    if (StrUtil.isBlank(bizReviewInfo.getProjectCode())) {
                        return R.error("项目编号不能为空");
                    } else if (StrUtil.isBlank(bizReviewInfo.getInspectedUnit())) {
                        return R.error("客户名称不能为空");
                    } else if (bizReviewInfo.getReviewDate() == null) {
                        return R.error("评审日期不能为空");
                    } else if (bizReviewInfo.getReviewAmount() == null) {
                        return R.error("评审费金额不能为空");
                    } else if (bizReviewInfo.getExpertNum() == null) {
                        return R.error("专家人数不能为空");
                    }
//                    else if (StrUtil.isBlank(bizReviewInfo.getReviewTeacher())){
//                        return R.error("评审老师不能为空");
//                    }
                }
                if (types.equals("2")) {
                    if (StrUtil.isBlank(bizReviewInfo.getProjectCode())) {
                        return R.error("项目编号不能为空");
                    } else if (StrUtil.isBlank(bizReviewInfo.getInspectedUnit())) {
                        return R.error("受检单位不能为空");
                    } else if (bizReviewInfo.getAmountDate() == null) {
                        return R.error("收款日期不能为空");
                    } else if (bizReviewInfo.getReviewAmount() == null) {
                        return R.error("支付金额不能为空");
                    } else if (bizReviewInfo.getContractAmount() == null) {
                        return R.error("合同金额不能为空");
                    }
                }
                if (types.equals("3")) {
                    if (bizReviewInfo.getReviewAmount() == null) {
                        return R.error("支付金额不能为空");
                    } else if (StrUtil.isBlank(bizReviewInfo.getAmountDetails())) {
                        return R.error("费用明细不能为空");
                    }
                }
                bizReviewInfo.setDelFlag("0");
                bizReviewInfo.setCreateBy(SystemUtil.getUserName());
                bizReviewInfo.setCreateTime(new Date());
                bizReviewInfo.setApplyId(bizReviewApply.getId());
                bizReviewInfoMapper.insert(bizReviewInfo);
            }

            // 初始化流程
            BizBusiness business = initBusiness(bizReviewApply);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(bizReviewApply.getReviewCode());
            bizBusinessService.insertBizBusiness(business);

            Map<String, Object> variables = Maps.newHashMap();
            variables.put("cc", bizReviewApply.getCc());
            variables.put("isCancel", 0);

            bizBusinessService.startProcess(business, variables);
            int reimburse = remoteFileService.update(Long.valueOf(bizReviewApply.getId()), bizReviewApply.getReviewCode());
            if (reimburse == 0) {
                throw new StatefulException("将上传的文件转为有效文件失败");
            }
            return R.ok("提交审批费申请成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("提交审批费申请失败");
        }
    }


    /**
     * 评审服务费信息
     *
     * @param applyId 评审服务费ID
     * @return 评审服务费信息
     */
    @Override
    public List<BizReviewInfo> selectBizReviewInfoList(Long applyId) {
        QueryWrapper<BizReviewInfo> bizReviewInfoQueryWrapper = new QueryWrapper<>();
        bizReviewInfoQueryWrapper.eq("apply_id", applyId);
//        bizReviewInfoQueryWrapper.eq("del_flag",0);
        List<BizReviewInfo> bizReviewInfos = bizReviewInfoMapper.selectList(bizReviewInfoQueryWrapper);
        if (bizReviewInfos != null && bizReviewInfos.size() > 0) {
            for (BizReviewInfo bizReviewInfo : bizReviewInfos) {
                if (bizReviewInfo.getAmountDate() != null) {
                    bizReviewInfo.setPdfAmountDate(DateUtil.format(bizReviewInfo.getAmountDate(), "yyyy-MM-dd "));
                    System.err.println(bizReviewInfo.getPdfAmountDate());
                }
                if (bizReviewInfo.getReviewDate() != null) {
                    bizReviewInfo.setPdfReviewDate(DateUtil.format(bizReviewInfo.getReviewDate(), "yyyy-MM-dd HH:mm:ss"));
                }
            }
        }
        return bizReviewInfos;
    }

    @Override
    public BizReviewApplyDto getPurchase(BizReviewApplyDto bizReviewApplyDto) {
        // 抄送人赋值
        if (StrUtil.isNotBlank(bizReviewApplyDto.getCc())) {
            String[] split = bizReviewApplyDto.getCc().split(",");
            List<String> ccList = new ArrayList<>();
            for (String cc : split) {
                SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
                if (sysUser == null) {
                    ccList.add("");

                } else {

                    ccList.add(sysUser.getUserName());
                }
            }
            bizReviewApplyDto.setCcName(String.join(",", ccList));
        }


        BizGoodsInfo bizGoodsInfo = new BizGoodsInfo();
        bizGoodsInfo.setPurchaseId(bizReviewApplyDto.getId());
        List<BizReviewInfo> bizReviewInfos = this.selectBizReviewInfoList(bizReviewApplyDto.getId());
        List<SysAttachment> sysAttachments = remoteFileService.getList(bizReviewApplyDto.getId(), "review");
        if (bizReviewInfos == null) {
            bizReviewInfos = new ArrayList<>();
        }
        if (sysAttachments == null) {
            sysAttachments = new ArrayList<>();
        }
        bizReviewApplyDto.setBizReviewInfos(bizReviewInfos);
        bizReviewApplyDto.setSysAttachments(sysAttachments);
        //打印人赋值
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        bizReviewApplyDto.setPdfName(sysUser.getUserName());
        return bizReviewApplyDto;
    }

    @Override
    public BizReviewApplyDto selectOne(Long tableId) {

        BizReviewApplyDto bizReviewApplyDto = this.selectBizReviewApplyById(tableId);
//            bizReviewApplyDto.setTitle(business.getTitle());
        return getPurchase(bizReviewApplyDto);

    }

    /**
     * 编辑专家人数实际评审日期
     *
     * @param bizReviewInfos
     * @return
     */
    @Override
    public void editInfo(List<BizReviewInfo> bizReviewInfos, String taskId) {
        boolean flag = false;
        for (BizReviewInfo bizReviewInfo : bizReviewInfos) {
            //若有项目取消则修改流程变量is_cancel
            if ("1".equals(bizReviewInfo.getDelFlag())) {
                flag = true;
            }
            bizReviewInfoMapper.updateById(bizReviewInfo);
        }
        if (flag) {
            List<Task> taskResultList = taskService.createTaskQuery().taskId(taskId).list();
            //当前executionId
            String currentExecutionId = taskResultList.get(0).getExecutionId();
            //保存变量
            runtimeService.setVariable(currentExecutionId, "isCancel", 1);
        }
    }

    /**
     * 初始化业务流程
     *
     * @param bizReviewApply
     * @return
     * @author zmr
     */
    private BizBusiness initBusiness(BizReviewApply bizReviewApply) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "review");
        example.setOrderByClause("VERSION_ DESC");
        List<ActReProcdef> actReProcdefs = actReProcdefMapper.selectByExample(example);
        ActReProcdef actReProcdef = new ActReProcdef();
        if (!actReProcdefs.isEmpty()) {
            actReProcdef = actReProcdefs.get(0);
        }
        BizBusiness business = new BizBusiness();
        business.setTableId(bizReviewApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(bizReviewApply.getTitle());
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
