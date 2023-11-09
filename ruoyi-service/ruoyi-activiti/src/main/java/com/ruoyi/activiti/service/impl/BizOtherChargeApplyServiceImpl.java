package com.ruoyi.activiti.service.impl;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.SysAttachment;
import com.ruoyi.activiti.domain.dto.BizOtherChargeApplyDto;
import com.ruoyi.activiti.domain.fiance.BizOtherChargeApply;
import com.ruoyi.activiti.domain.fiance.BizOtherChargeInfo;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.purchase.BizGoodsInfo;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.ActReProcdefMapper;
import com.ruoyi.activiti.mapper.BizOtherChargeApplyMapper;
import com.ruoyi.activiti.mapper.BizOtherChargeInfoMapper;
import com.ruoyi.activiti.service.BizOtherChargeApplyService;
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
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 其他费用申请业务层处理
 *
 * @author zx
 * @date 2022-3-17 21:40:51
 */
@Service
@Slf4j
public class BizOtherChargeApplyServiceImpl implements BizOtherChargeApplyService {
    private final BizOtherChargeApplyMapper otherChargeApplyMapper;
    private final BizOtherChargeInfoMapper otherChargeInfoMapper;
    private final RemoteUserService remoteUserService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final RemoteFileService remoteFileService;
    private final RemoteDeptService remoteDeptService;
    @Autowired
    private DataScopeUtil dataScopeUtil;


    @Autowired
    public BizOtherChargeApplyServiceImpl(BizOtherChargeApplyMapper otherChargeApplyMapper, RemoteUserService remoteUserService, ActReProcdefMapper actReProcdefMapper, IBizBusinessService bizBusinessService, BizOtherChargeInfoMapper otherChargeInfoMapper, RemoteFileService remoteFileService, RemoteDeptService remoteDeptService) {
        this.otherChargeApplyMapper = otherChargeApplyMapper;
        this.remoteUserService = remoteUserService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.otherChargeInfoMapper = otherChargeInfoMapper;
        this.remoteFileService = remoteFileService;
        this.remoteDeptService = remoteDeptService;
    }

    /**
     * 查询其他费用申请
     *
     * @param id 其他费用申请ID
     * @return 其他费用申请
     */
    @Override
    public BizOtherChargeApplyDto selectBizOtherChargeApplyById(Long id) {
        BizOtherChargeApplyDto otherChargeApplyDto = new BizOtherChargeApplyDto();
        otherChargeApplyDto.setId(id);
        otherChargeApplyDto.setIsDetails("1");
        otherChargeApplyDto.setOneStatus(1);
        List<BizOtherChargeApplyDto> otherChargeApplyDtos = this.selectBizOtherChargeApply(otherChargeApplyDto);
        if (otherChargeApplyDtos != null && otherChargeApplyDtos.size() > 0) {
            return otherChargeApplyDtos.get(0);
        } else {
            return new BizOtherChargeApplyDto();
        }

    }

    /**
     * 查询其他费用申请列表
     *
     * @param dto 其他费用申请
     * @return 其他费用申请
     */
    @Override
    public List<BizOtherChargeApplyDto> selectBizOtherChargeApply(BizOtherChargeApplyDto dto) {
        QueryWrapper<BizOtherChargeApply> otherChargeApplyQueryWrapper = new QueryWrapper<>();
        otherChargeApplyQueryWrapper.eq("bu.proc_def_key", "other-charge");
        otherChargeApplyQueryWrapper.eq("bu.del_flag", 0);
        otherChargeApplyQueryWrapper.eq("ra.del_flag", 0);
        //查询单条
        otherChargeApplyQueryWrapper.eq(dto.getId() != null, "ra.id", dto.getId());
        //查询审批状态
        otherChargeApplyQueryWrapper.eq(dto.getResult() != null, "bu.result", dto.getResult());
        //查询审批状态
        otherChargeApplyQueryWrapper.eq(dto.getStatus() != null, "bu.status", dto.getStatus());
        /**其他费用申请编号*/
        if(StrUtil.isBlank(dto.getOtherCode())){
            dto.setOtherCode(dto.getReviewCode());
        }
        otherChargeApplyQueryWrapper.like(StrUtil.isNotBlank(dto.getOtherCode()), "ra.other_code", dto.getOtherCode());
        /**申请人*/
        otherChargeApplyQueryWrapper.eq(StrUtil.isNotBlank(dto.getUserName()), "u.user_name", dto.getUserName());
        /**申请时间*/
        String createTime1 = dto.getCreateTime1();
        String createTime2 = dto.getCreateTime2();

        otherChargeApplyQueryWrapper.between(StrUtil.isNotBlank(createTime1) && StrUtil.isNotBlank(createTime2), "ra.create_time", createTime1, createTime2);
        otherChargeApplyQueryWrapper.orderByDesc("ra.id");
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        if (dto.getOneStatus() == null) {
            String sql = dataScopeUtil.getScopeSql(sysUser, "d", "u");

            if (StrUtil.isNotBlank(sql) && StrUtil.isBlank(dto.getIsDetails())) {
                otherChargeApplyQueryWrapper.apply(sql);
            }
        }

        List<BizOtherChargeApplyDto> list = otherChargeApplyMapper.selectAll(otherChargeApplyQueryWrapper);

        return list;
    }

    /**
     * 其他费用申请新增
     *
     * @param otherChargeApply 评审、服务费新增
     * @return 结果
     */
    @Override
    public R insertBizOtherChargeApply(BizOtherChargeApply otherChargeApply) {
        try {
            // 抄送人去重
            String cc = otherChargeApply.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                otherChargeApply.setCc(cc);
            }
            otherChargeApply.setDelFlag("0");
            otherChargeApply.setCreateBy(SystemUtil.getUserName());
            otherChargeApply.setCreateTime(new Date());
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyId1 = belongCompany2.get("companyId").toString();
            otherChargeApply.setDeptId(sysUser.getDeptId());
            otherChargeApply.setCompanyId(Long.valueOf(companyId1));
//            String purchaseCode = "CG" + UUID.fastUUID().toString().substring(0, 8);
//            otherChargeApply.setReviewCode(purchaseCode);
            otherChargeApply.setUserId(SystemUtil.getUserId());


            otherChargeApply.setTitle(sysUser.getUserName() + "提交的其他费用申请");
            otherChargeApplyMapper.insert(otherChargeApply);
            List<BizOtherChargeInfo> otherChargeInfos = otherChargeApply.getOtherChargeInfos();
//            if (bizGoodsInfos.isEmpty()) {
//                return R.error("请添加物品");
//            }
            for (BizOtherChargeInfo otherChargeInfo : otherChargeInfos) {
                if (otherChargeInfo.getPaymentAmount() == null) {
                    return R.error("支付金额不能为空");
                } else if (StrUtil.isBlank(otherChargeInfo.getAmountDetails())) {
                    return R.error("费用明细不能为空");
                }
                otherChargeInfo.setReviewAmount(otherChargeInfo.getPaymentAmount());
                otherChargeInfo.setDelFlag("0");
                otherChargeInfo.setCreateBy(SystemUtil.getUserName());
                otherChargeInfo.setCreateTime(new Date());
                otherChargeInfo.setApplyId(otherChargeApply.getId());
                otherChargeInfoMapper.insert(otherChargeInfo);
            }

            // 初始化流程
            BizBusiness business = initBusiness(otherChargeApply);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(otherChargeApply.getOtherCode());
            bizBusinessService.insertBizBusiness(business);

            Map<String, Object> variables = Maps.newHashMap();
            variables.put("cc", otherChargeApply.getCc());

            bizBusinessService.startProcess(business, variables);
            int reimburse = remoteFileService.update(Long.valueOf(otherChargeApply.getId()), otherChargeApply.getOtherCode());
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
     * 其他费用费信息
     *
     * @param applyId 其他费用费ID
     * @return 其他费用费信息
     */
    @Override
    public List<BizOtherChargeInfo> selectBizOtherChargeInfoList(Long applyId) {
        QueryWrapper<BizOtherChargeInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("apply_id", applyId);
        wrapper.eq("del_flag", 0);
        List<BizOtherChargeInfo> otherChargeInfos = otherChargeInfoMapper.selectList(wrapper);
        if (otherChargeInfos != null && otherChargeInfos.size() > 0) {
            for (BizOtherChargeInfo otherChargeInfo : otherChargeInfos) {

            }
        }
        return otherChargeInfos;
    }

    @Override
    public BizOtherChargeApplyDto getPurchase(BizOtherChargeApplyDto otherChargeApplyDto) {
        // 抄送人赋值
        if (StrUtil.isNotBlank(otherChargeApplyDto.getCc())) {
            String[] split = otherChargeApplyDto.getCc().split(",");
            List<String> ccList = new ArrayList<>();
            for (String cc : split) {
                SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
                if (sysUser == null) {
                    ccList.add("");

                } else {

                    ccList.add(sysUser.getUserName());
                }
            }
            otherChargeApplyDto.setCcName(String.join(",", ccList));
        }


        BizGoodsInfo bizGoodsInfo = new BizGoodsInfo();
        bizGoodsInfo.setPurchaseId(otherChargeApplyDto.getId());
        List<BizOtherChargeInfo> otherChargeInfos = this.selectBizOtherChargeInfoList(otherChargeApplyDto.getId());
        List<SysAttachment> sysAttachments = remoteFileService.getList(otherChargeApplyDto.getId(), "other-charge");
        if (otherChargeInfos == null) {
            otherChargeInfos = new ArrayList<>();
        }
        if (sysAttachments == null) {
            sysAttachments = new ArrayList<>();
        }
        otherChargeApplyDto.setOtherChargeInfos(otherChargeInfos);
        otherChargeApplyDto.setSysAttachments(sysAttachments);
        //打印人赋值
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        otherChargeApplyDto.setPdfName(sysUser.getUserName());
        return otherChargeApplyDto;
    }

    @Override
    public BizOtherChargeApplyDto selectOne(Long tableId) {

        BizOtherChargeApplyDto otherChargeApplyDto = this.selectBizOtherChargeApplyById(tableId);
//            otherChargeApplyDto.setTitle(business.getTitle());
        return getPurchase(otherChargeApplyDto);

    }

    /**
     * 初始化业务流程
     *
     * @param otherChargeApply
     * @return
     * @author zmr
     */
    private BizBusiness initBusiness(BizOtherChargeApply otherChargeApply) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "other-charge");
        example.setOrderByClause("VERSION_ DESC");
        List<ActReProcdef> actReProcdefs = actReProcdefMapper.selectByExample(example);
        ActReProcdef actReProcdef = new ActReProcdef();
        if (!actReProcdefs.isEmpty()) {
            actReProcdef = actReProcdefs.get(0);
        }
        BizBusiness business = new BizBusiness();
        business.setTableId(otherChargeApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(otherChargeApply.getTitle());
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
