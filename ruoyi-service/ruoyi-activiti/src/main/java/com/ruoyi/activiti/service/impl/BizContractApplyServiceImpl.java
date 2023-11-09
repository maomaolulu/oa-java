package com.ruoyi.activiti.service.impl;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.*;
import com.ruoyi.activiti.domain.dto.BizContractApplyDto;
import com.ruoyi.activiti.domain.fiance.BizContractApply;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.fiance.BizReviewApply;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.*;
import com.ruoyi.activiti.service.BizContractApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.feign.RemoteConfigService;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.util.DataScopeUtil;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 合同审批业务层处理
 *
 * @author zh
 * @date 2021-11-24
 */
@Service
@Slf4j
public class BizContractApplyServiceImpl implements BizContractApplyService {
    private final BizContractApplyMapper bizContractApplyMapper;
    private final RemoteUserService remoteUserService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final RemoteFileService remoteFileService;
    private final RemoteDeptService remoteDeptService;
    @Autowired
    private DataScopeUtil dataScopeUtil;
    @Resource
    RemoteConfigService remoteConfigService;


    @Autowired
    public BizContractApplyServiceImpl( BizContractApplyMapper bizContractApplyMapper, RemoteUserService remoteUserService, ActReProcdefMapper actReProcdefMapper, IBizBusinessService bizBusinessService, RemoteFileService remoteFileService, RemoteDeptService remoteDeptService) {
        this.bizContractApplyMapper = bizContractApplyMapper;
        this.remoteUserService = remoteUserService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.remoteFileService = remoteFileService;
        this.remoteDeptService = remoteDeptService;
    }


    /**
     * 查询合同审批申请列表
     *
     * @param dto 合同审批
     * @return 合同审批
     */
    @Override
    public List<BizContractApplyDto> selectBizContractApply(BizContractApplyDto dto) {
        QueryWrapper<BizReviewApply> bizReviewApplyQueryWrapper = new QueryWrapper<>();
        bizReviewApplyQueryWrapper.eq("bu.proc_def_key","contract_ys");
        bizReviewApplyQueryWrapper.eq("bu.del_flag",0);

        bizReviewApplyQueryWrapper.eq("ca.del_flag",0);
        //回款查询
        bizReviewApplyQueryWrapper.gt(dto.getIsPayBack()!=null&&dto.getIsPayBack().equals("1"),"ca.remaining_money",0);
        //查询合同审批单条
        bizReviewApplyQueryWrapper.eq(dto.getId()!=null,"ca.id",dto.getId());
        //查询审批状态
        bizReviewApplyQueryWrapper.eq(dto.getResult()!=null,"bu.result",dto.getResult());
        //查询审批状态
        bizReviewApplyQueryWrapper.eq(dto.getStatus()!=null,"bu.status",dto.getStatus());
        /**合同编号*/
        bizReviewApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getContractCode()),"ca.contract_code",dto.getContractCode());
        bizReviewApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getApplyCode()),"ca.apply_code",dto.getApplyCode());
        /**申请人*/
        bizReviewApplyQueryWrapper.eq(StrUtil.isNotBlank(dto.getCreateByName()),"u.user_name",dto.getCreateByName());
        /** 跟进人查询 */
        bizReviewApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getFollowerName()),"u2.user_name",dto.getFollowerName());
        /** 客户名称 */
        bizReviewApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getCustomerName()),"ca.customer_name",dto.getCustomerName());
        /**申请时间*/
        String createTime1 = dto.getCreateTime1();
        String createTime2 = dto.getCreateTime2();

        bizReviewApplyQueryWrapper.between(StrUtil.isNotBlank(createTime1)&&StrUtil.isNotBlank(createTime2),"ca.create_time",createTime1,createTime2);
        bizReviewApplyQueryWrapper.orderByDesc("ca.id");
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        if(dto.getOneStatus()==null){
            String sql = dataScopeUtil.getScopeSql(sysUser, "d", "u");

            if(StrUtil.isNotBlank(sql)){
                bizReviewApplyQueryWrapper.apply(sql);
            }
        }

        List<BizContractApplyDto> list = bizContractApplyMapper.selectAll(bizReviewApplyQueryWrapper);

        return  list;
    }


    /**
     * 合同审批新增
     *
     * @param bizContractApply 合同审批
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R insertBizContractApply(BizContractApply bizContractApply) {
        try {
            // 抄送人去重
            String cc = bizContractApply.getCc();
            if(StrUtil.isNotBlank(cc)){
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                bizContractApply.setCc(cc);
            }
            bizContractApply.setDelFlag("0");
            bizContractApply.setCreateBy(SystemUtil.getUserId());
            bizContractApply.setCreateTime(new Date());
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyId1 = belongCompany2.get("companyId").toString();
            bizContractApply.setDeptId(sysUser.getDeptId());
            bizContractApply.setRemainingMoney(bizContractApply.getContractAmount());
            bizContractApply.setCompanyId(Long.valueOf(companyId1));
            bizContractApply.setTitle(sysUser.getUserName()+"提交的合同审批申请");

            bizContractApplyMapper.insert(bizContractApply);

            // 初始化流程
            BizBusiness business = initBusiness(bizContractApply);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(bizContractApply.getApplyCode());
            bizBusinessService.insertBizBusiness(business);

            Map<String, Object> variables = Maps.newHashMap();
            SysConfig config3 = new SysConfig();
            config3.setConfigKey("contract_ys-g1");
            List<SysConfig> list3 = remoteConfigService.listOperating(config3);
            variables.put("g1", Long.valueOf(list3.get(0).getConfigValue()));
            variables.put("cc", bizContractApply.getCc());
            variables.put("money", bizContractApply.getContractAmount());

            bizBusinessService.startProcess(business, variables);
            int reimburse = remoteFileService.update(Long.valueOf(bizContractApply.getId()),bizContractApply.getApplyCode());
            if(reimburse == 0){
                throw new StatefulException("将上传的文件转为有效文件失败");
            }
            return R.ok("提交合同审批业务成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("提交合同审批业务失败");
        }
    }

    @Override
    public BizContractApplyDto getPurchase(BizContractApplyDto dto) {
        // 抄送人赋值
        if (StrUtil.isEmpty(dto.getCc())) {
            dto.setCcName("");
        } else {
            String[] split = dto.getCc().split(",");
            List<String> ccList = new ArrayList<>();
            for (String cc : split) {
                SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
                if (sysUser == null) {
                    ccList.add("");
                } else {
                    ccList.add(sysUser.getUserName());
                }
            }
            dto.setCcName(String.join(",", ccList));
        }
        List<SysAttachment> sysAttachmentsFile = remoteFileService.getList(dto.getId(), "contract-file");
        List<SysAttachment> sysAttachmentsImg = remoteFileService.getList(dto.getId(), "contract-img");
        if(sysAttachmentsFile==null){
            sysAttachmentsFile=new ArrayList<>();
        }
        if(sysAttachmentsImg==null){
            sysAttachmentsImg=new ArrayList<>();
        }
        dto.setSysAttachmentsFile(sysAttachmentsFile);
        dto.setSysAttachmentsImg(sysAttachmentsImg);
        return dto;
    }

    @Override
    public BizContractApplyDto selectOne(Long tableId) {


            BizContractApplyDto dto = new BizContractApplyDto();
            dto.setId(tableId);
            dto.setOneStatus(1);
         List<BizContractApplyDto> bizContractApplyDtos = this.selectBizContractApply(dto);
        BizContractApplyDto bizContractApplyDto1 = new BizContractApplyDto();
        if(bizContractApplyDtos!=null&&bizContractApplyDtos.size()>0){
            bizContractApplyDto1=bizContractApplyDtos.get(0);
         }
//            bizContractApplyDto.setTitle(business.getTitle());
        return getPurchase(bizContractApplyDto1);

    }


    /**
     * 初始化业务流程
     *
     * @param bizContractApply
     * @return
     * @author zmr
     */
    private BizBusiness initBusiness(BizContractApply bizContractApply) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "contract_ys");
        example.setOrderByClause("VERSION_ DESC");
        List<ActReProcdef> actReProcdefs = actReProcdefMapper.selectByExample(example);
        ActReProcdef actReProcdef = new ActReProcdef();
        if (!actReProcdefs.isEmpty()) {
            actReProcdef = actReProcdefs.get(0);
        }
        BizBusiness business = new BizBusiness();
        business.setTableId(bizContractApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(bizContractApply.getTitle());
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
