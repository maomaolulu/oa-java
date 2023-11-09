package com.ruoyi.activiti.service.impl.asset;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.asset.BizConsumablesInventory;
import com.ruoyi.activiti.domain.asset.BizConsumablesInventoryDetail;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.ActReProcdefMapper;
import com.ruoyi.activiti.mapper.asset.BizConsumablesInventoryDetailMapper;
import com.ruoyi.activiti.mapper.asset.BizConsumablesInventoryMapper;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.asset.BizConsumablesInventoryService;
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
 * 耗材盘点
 * @author zx
 * @date 2022/3/29 14:13
 */
@Service
@Slf4j
public class BizConsumablesInventoryServiceImpl implements BizConsumablesInventoryService {
    private final BizConsumablesInventoryMapper consumablesInventoryMapper;
    private final BizConsumablesInventoryDetailMapper consumablesInventoryDetailMapper;
    private final RemoteUserService remoteUserService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final RemoteDeptService remoteDeptService;
    private final RemoteFileService remoteFileService;

    public BizConsumablesInventoryServiceImpl(BizConsumablesInventoryMapper consumablesInventoryMapper, BizConsumablesInventoryDetailMapper consumablesInventoryDetailMapper, RemoteUserService remoteUserService, ActReProcdefMapper actReProcdefMapper, IBizBusinessService bizBusinessService, RemoteDeptService remoteDeptService, RemoteFileService remoteFileService) {
        this.consumablesInventoryMapper = consumablesInventoryMapper;
        this.consumablesInventoryDetailMapper = consumablesInventoryDetailMapper;
        this.remoteUserService = remoteUserService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.remoteDeptService = remoteDeptService;
        this.remoteFileService = remoteFileService;
    }

    /**
     * 新增申请
     *
     * @param consumablesInventory
     * @return
     */
    @Override
    public int insert(BizConsumablesInventory consumablesInventory) {
        try {
            // 抄送人去重
            String cc =  consumablesInventory.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                consumablesInventory.setCc(cc);
            }
            String userName = SystemUtil.getUserName();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyId1 = belongCompany2.get("companyId").toString();
            consumablesInventory.setCreateTime(new Date());
            consumablesInventory.setUpdateTime(new Date());
            consumablesInventory.setCreateBy(userName);
            consumablesInventory.setUpdateBy(userName);
            //申请人部门
            consumablesInventory.setDeptId(sysUser.getDeptId());
            consumablesInventory.setCompanyId(Long.valueOf(companyId1));
            consumablesInventory.setTitle(sysUser.getUserName() + "提交的耗材盘点审批");
            int insert =  consumablesInventoryMapper.insert( consumablesInventory);
            if(consumablesInventory.getDetails().isEmpty()){
                return 2;
            }
            consumablesInventory.getDetails().stream().forEach(bizConsumablesInventoryDetail -> {
                bizConsumablesInventoryDetail.setId(null);
                bizConsumablesInventoryDetail.setCreateBy(userName);
                bizConsumablesInventoryDetail.setCreateTime(new Date());
                bizConsumablesInventoryDetail.setApplyId(consumablesInventory.getId());
                consumablesInventoryDetailMapper.insert(bizConsumablesInventoryDetail);

            });

            // 初始化流程
            BizBusiness business = initBusiness( consumablesInventory);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(consumablesInventory.getApplyCode());
            bizBusinessService.insertBizBusiness(business);

            Map<String, Object> variables = Maps.newHashMap();
            // 这里可以设置各个负责人，key跟模型的代理变量一致
            variables.put("cc",  consumablesInventory.getCc());
            bizBusinessService.startProcess(business, variables);

            // 将上传的临时文件转为有效文件
            int reimburse = remoteFileService.update( consumablesInventory.getId(),  consumablesInventory.getApplyCode());
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
     * 获取详情
     *
     * @param id
     * @return
     */
    @Override
    public BizConsumablesInventory selectById(Long id) {
        BizConsumablesInventory consumablesInventory = consumablesInventoryMapper.selectById(id);
        if(consumablesInventory == null){
            log.error("查询不到id为{}的耗材盘点", id);
            return new BizConsumablesInventory();
        }
        QueryWrapper<BizConsumablesInventoryDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("apply_id",id);
        List<BizConsumablesInventoryDetail> bizConsumablesInventoryDetails = consumablesInventoryDetailMapper.selectList(wrapper);
        if(bizConsumablesInventoryDetails==null||bizConsumablesInventoryDetails.isEmpty()){
            bizConsumablesInventoryDetails = new ArrayList<>();
            log.error("查询不到盘点详情明细");
        }
        consumablesInventory.setDetails(bizConsumablesInventoryDetails);

//        // 查询附件信息
//        // 通用申请凭证
//        List<SysAttachment> certificate = remoteFileService.getList( consumablesInventory.getId(), "universal-certificate");
//        if(certificate==null){
//            certificate = new ArrayList<>();
//        }
//        // 通用申请附件
//        List<SysAttachment> appendix = remoteFileService.getList( consumablesInventory.getId(), "universal-appendix");
//        if(appendix==null){
//            appendix = new ArrayList<>();
//        }
//
//        consumablesInventory.setVouchers(certificate);
//        consumablesInventory.setAttachment(appendix);

        Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2( consumablesInventory.getDeptId());
        //申请公司名称
        consumablesInventory.setCompanyName(belongCompany2.get("companyName").toString());
        //申请部门名称
        consumablesInventory.setDeptName(remoteDeptService.selectSysDeptByDeptId( consumablesInventory.getDeptId()).getDeptName());
        //申请人赋值
        SysUser sysUser2 = remoteUserService.selectSysUserByUsername( consumablesInventory.getCreateBy());
        consumablesInventory.setCreateByName(sysUser2.getUserName());

        String ccStr =  consumablesInventory.getCc();
        if(StrUtil.isNotBlank(ccStr)){
            List<String> ccList = new ArrayList<>();
            for (String cc : ccStr.split(",")) {
                SysUser ccUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
                ccList.add(ccUser.getUserName());
            }
            consumablesInventory.setCcName(String.join("、",ccList));
        }
        return  consumablesInventory;
    }
    /**
     * 初始化业务流程
     *
     * @param  consumablesInventory
     * @return
     */
    private BizBusiness initBusiness(BizConsumablesInventory consumablesInventory) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "hcpd");
        example.setOrderByClause("VERSION_ DESC");
        ActReProcdef actReProcdef = actReProcdefMapper.selectByExample(example).get(0);

        BizBusiness business = new BizBusiness();
        business.setTableId( consumablesInventory.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle( consumablesInventory.getTitle());
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
