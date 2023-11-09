package com.ruoyi.activiti.service.impl;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.SysAttachment;
import com.ruoyi.activiti.domain.dto.BizBusinessFeeApplyDto;
import com.ruoyi.activiti.domain.fiance.BizBusinessFeeApply;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.purchase.BizPurchaseApply;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.ActReProcdefMapper;
import com.ruoyi.activiti.mapper.BizBusinessFeeApplyMapper;
import com.ruoyi.activiti.service.BizBusinessFeeApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.DataScopeUtil;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 业务费申请
 *
 * @author zh
 * @date 2021-12-17
 */
@Service
@Slf4j
public class BizBusinessFeeApplyServiceImpl implements BizBusinessFeeApplyService {

    private final BizBusinessFeeApplyMapper bizBusinessFeeApplyMapper;
    private final IBizBusinessService bizBusinessService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final RemoteFileService remoteFileService;
    private final RemoteDeptService remoteDeptService;
    @Autowired
    private DataScopeUtil dataScopeUtil;
    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    public BizBusinessFeeApplyServiceImpl(BizBusinessFeeApplyMapper bizBusinessFeeApplyMapper, IBizBusinessService bizBusinessService, ActReProcdefMapper actReProcdefMapper, RemoteFileService remoteFileService, RemoteDeptService remoteDeptService, RemoteUserService remoteUserService, DataScopeUtil dataScopeUtil) {
        this.bizBusinessFeeApplyMapper = bizBusinessFeeApplyMapper;
        this.bizBusinessService = bizBusinessService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.remoteFileService = remoteFileService;
        this.remoteDeptService = remoteDeptService;
        this.remoteUserService = remoteUserService;

        this.dataScopeUtil = dataScopeUtil;
    }

    /**
     * 查询业务费申请
     *
     * @param id 业务费申请ID
     * @return 业务费申请
     */
    @Override
    public BizBusinessFeeApply selectBizBusinessFeeApplyById(Integer id) {
        return bizBusinessFeeApplyMapper.selectById(id);
    }

    /**
     * 查询业务费申请列表
     *
     * @param
     * @return
     */
    @Override
    public List<BizBusinessFeeApplyDto> selectBizBusinessFeeApplyListAll(BizBusinessFeeApplyDto dto) {
        QueryWrapper<BizPurchaseApply> bizPurchaseApplyQueryWrapper = new QueryWrapper<>();
        bizPurchaseApplyQueryWrapper.eq("bu.proc_def_key","businessMoney");
        if(dto.getOneStatus()==null){
            bizPurchaseApplyQueryWrapper.eq("bu.del_flag",0);
            bizPurchaseApplyQueryWrapper.eq("bua.del_flag",0);
        }
//        bizPurchaseApplyQueryWrapper.eq("bu.del_flag",0);
//        bizPurchaseApplyQueryWrapper.eq("bua.del_flag",0);
        //查询单条数据
        bizPurchaseApplyQueryWrapper.eq(dto.getId()!=null,"bua.id",dto.getId());
        //查询审批状态
        bizPurchaseApplyQueryWrapper.eq(dto.getResult()!=null,"bu.result",dto.getResult());
        //查询审批状态
        bizPurchaseApplyQueryWrapper.eq(dto.getStatus()!=null,"bu.status",dto.getStatus());
        /**采购申请编号/付款单号*/
        bizPurchaseApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getApplyCode()),"bua.apply_code",dto.getApplyCode());
        /**申请人*/
        bizPurchaseApplyQueryWrapper.eq(StrUtil.isNotBlank(dto.getApplyUserName()),"u2.user_name",dto.getApplyUserName());
        /**申请时间*/
        String createTime1 = dto.getCreateTime1();
        String createTime2 = dto.getCreateTime2();
        bizPurchaseApplyQueryWrapper.orderByDesc("bua.id");
        bizPurchaseApplyQueryWrapper.between(StrUtil.isNotBlank(createTime1)&&StrUtil.isNotBlank(createTime2),"bua.create_time",createTime1,createTime2);

        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);

        String sql = dataScopeUtil.getScopeSql(sysUser, "d", "u ");
        if(dto.getOneStatus()==null){
            if(StrUtil.isNotBlank(sql)){
                bizPurchaseApplyQueryWrapper.apply(sql);
            }
        }
        List<BizBusinessFeeApplyDto> list = bizBusinessFeeApplyMapper.selectAll(bizPurchaseApplyQueryWrapper);

        return  list;
    }
    /**
     * 新增业务费申请
     *
     * @param bizBusinessFeeApply 新增业务费申请)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public R insertBizPurchaseApply(BizBusinessFeeApply bizBusinessFeeApply) {
        try {
            // 抄送人去重
            String cc = bizBusinessFeeApply.getCc();
            if(StrUtil.isNotBlank(cc)){
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                bizBusinessFeeApply.setCc(cc);
            }
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());


            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyId1 = belongCompany2.get("companyId").toString();
            bizBusinessFeeApply.setCompanyId(Integer.valueOf(companyId1));

            bizBusinessFeeApply.setDeptId(sysUser.getDeptId().intValue());
            bizBusinessFeeApply.setDelFlag("0");
            bizBusinessFeeApply.setCreateBy(SystemUtil.getUserName());
            bizBusinessFeeApply.setCreateTime(new Date());
            bizBusinessFeeApply.setUserId(SystemUtil.getUserId().intValue());

            System.err.println(bizBusinessFeeApply.toString());
            bizBusinessFeeApply.setTitle(sysUser.getUserName()+"提交的业务费申请");
            bizBusinessFeeApplyMapper.insert(bizBusinessFeeApply);

            // 初始化流程
            BizBusiness business = initBusiness(bizBusinessFeeApply);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(bizBusinessFeeApply.getApplyCode());
            bizBusinessService.insertBizBusiness(business);

            Map<String, Object> variables = Maps.newHashMap();
//            // 这里可以设置各个负责人，key跟模型的代理变量一致
            variables.put("cc",bizBusinessFeeApply.getCc());

            bizBusinessService.startProcess(business, variables);

            int reimburse = remoteFileService.update(Long.valueOf(bizBusinessFeeApply.getId()),bizBusinessFeeApply.getApplyCode());
            if(reimburse == 0){
                throw new StatefulException("将上传的文件转为有效文件失败");
            }
            return R.ok("业务申请费用成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("业务申请费用失败");
        }
    }

    /**
     * 修改业务费用申请
     *
     * @param bizBusinessFeeApply 修改业务费用申请
     * @return 结果
     */
    @Override
    public BizBusinessFeeApply updateBizBusinessFeeApply(BizBusinessFeeApply bizBusinessFeeApply) {
         bizBusinessFeeApplyMapper.updateById(bizBusinessFeeApply);
         return bizBusinessFeeApply;

    }

    /**
     * 删除业务费用申请
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public void deleteBizBusinessFeeApplyByIds(Integer[] ids) {
        for (Integer s:ids
             ) {
             BizBusinessFeeApply bizBusinessFeeApply = new BizBusinessFeeApply();
            bizBusinessFeeApply.setId(s);
            bizBusinessFeeApply.setDelFlag("1");
            bizBusinessFeeApplyMapper.updateById(bizBusinessFeeApply);
        }
    }

    @Override
    public BizBusinessFeeApplyDto getPurchase(BizBusinessFeeApplyDto bizBusinessFeeApply) {
        // 抄送人赋值
        if (StrUtil.isEmpty(bizBusinessFeeApply.getCc())) {
            bizBusinessFeeApply.setCcName("");
        } else {
        String[] split = bizBusinessFeeApply.getCc().split(",");
        List<String> ccList = new ArrayList<>();
        for (String cc : split) {
            SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
            if(sysUser==null){
                ccList.add("");
            }else{

                ccList.add(sysUser.getUserName());
            }
        }
        bizBusinessFeeApply.setCcName(String.join(",", ccList));
        }


        return bizBusinessFeeApply;
    }

    @Override
    public BizBusinessFeeApplyDto selectOne(String businessKey) {
        BizBusiness business = bizBusinessService.selectBizBusinessById(businessKey);

            BizBusinessFeeApplyDto bizBusinessFeeApplyDto = new BizBusinessFeeApplyDto();
            bizBusinessFeeApplyDto.setId(Integer.valueOf(business.getTableId()));
            List<SysAttachment> sysAttachments = remoteFileService.getList(Long.valueOf(business.getTableId()), "business-fee");
            if(sysAttachments==null){
                sysAttachments=new ArrayList<>();
            }
            bizBusinessFeeApplyDto.setOneStatus("1");
            BizBusinessFeeApplyDto bizBusinessFeeApplyDtos = new BizBusinessFeeApplyDto();
             List<BizBusinessFeeApplyDto> bizBusinessFeeApplyDtos1 = this.selectBizBusinessFeeApplyListAll(bizBusinessFeeApplyDto);
             if(bizBusinessFeeApplyDtos1!=null&&bizBusinessFeeApplyDtos1.size()>0){
                  bizBusinessFeeApplyDtos = this.selectBizBusinessFeeApplyListAll(bizBusinessFeeApplyDto).get(0);
             }


            bizBusinessFeeApplyDtos.setSysAttachments(sysAttachments);
            bizBusinessFeeApplyDtos.setTitle(business.getTitle());
            //隶属部门赋值
            if(bizBusinessFeeApplyDtos.getApplyDeptId()!=null){
                Map<String, Object> belongCompany = remoteDeptService.getBelongCompany2(bizBusinessFeeApplyDtos.getApplyDeptId());
                //隶属公司名称
                bizBusinessFeeApplyDtos.setApplyCompanyName(belongCompany.get("companyName").toString());
                bizBusinessFeeApplyDtos.setCompanyId(Integer.valueOf(belongCompany.get("companyId").toString()));
                //隶属部门名称
                bizBusinessFeeApplyDtos.setApplyDeptName(remoteDeptService.selectSysDeptByDeptId(bizBusinessFeeApplyDtos.getApplyDeptId()).getDeptName());
            }
            //打印人赋值
            Long userId = SystemUtil.getUserId();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
            bizBusinessFeeApplyDtos.setPdfName(sysUser.getUserName());
            return getPurchase(bizBusinessFeeApplyDtos) ;

    }


    /**
     * 初始化业务流程
     *
     * @param bizBusinessFeeApply
     * @return
     * @author zmr
     */
    private BizBusiness initBusiness(BizBusinessFeeApply bizBusinessFeeApply) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "businessMoney");
        example.setOrderByClause("VERSION_ DESC");
        List<ActReProcdef> actReProcdefs = actReProcdefMapper.selectByExample(example);
        ActReProcdef actReProcdef = new ActReProcdef();
        if (!actReProcdefs.isEmpty()) {
            actReProcdef = actReProcdefs.get(0);
        }
        BizBusiness business = new BizBusiness();
        business.setTableId(bizBusinessFeeApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(bizBusinessFeeApply.getTitle());
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
