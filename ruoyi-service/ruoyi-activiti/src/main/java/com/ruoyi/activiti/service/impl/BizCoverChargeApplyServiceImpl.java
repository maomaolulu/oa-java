package com.ruoyi.activiti.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.SysAttachment;
import com.ruoyi.activiti.domain.dto.BizCoverChargeApplyDto;
import com.ruoyi.activiti.domain.fiance.BizCoverChargeApply;
import com.ruoyi.activiti.domain.fiance.BizCoverChargeInfo;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.purchase.BizGoodsInfo;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.ActReProcdefMapper;
import com.ruoyi.activiti.mapper.BizCoverChargeApplyMapper;
import com.ruoyi.activiti.mapper.BizCoverChargeInfoMapper;
import com.ruoyi.activiti.service.BizCoverChargeApplyService;
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
 * 服务费申请业务层处理
 *
 * @author zx
 * @date 2022年3月16日21:51:30
 */
@Service
@Slf4j
public class BizCoverChargeApplyServiceImpl implements BizCoverChargeApplyService {
    private final BizCoverChargeApplyMapper coverChargeApplyMapper;
    private final RemoteUserService remoteUserService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final BizCoverChargeInfoMapper coverChargeInfoMapper;
    private final RemoteFileService remoteFileService;
    private final RemoteDeptService remoteDeptService;
    @Autowired
    private DataScopeUtil dataScopeUtil;


    @Autowired
    public BizCoverChargeApplyServiceImpl(BizCoverChargeApplyMapper coverChargeApplyMapper, RemoteUserService remoteUserService, ActReProcdefMapper actReProcdefMapper, IBizBusinessService bizBusinessService, BizCoverChargeInfoMapper coverChargeInfoMapper, RemoteFileService remoteFileService, RemoteDeptService remoteDeptService) {
        this.coverChargeApplyMapper = coverChargeApplyMapper;
        this.remoteUserService = remoteUserService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.coverChargeInfoMapper = coverChargeInfoMapper;
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
    public BizCoverChargeApplyDto selectBizCoverChargeApplyById(Long id) {
        BizCoverChargeApplyDto coverChargeApplyDto = new BizCoverChargeApplyDto();
        coverChargeApplyDto.setId(id);
        coverChargeApplyDto.setIsDetails("1");
        coverChargeApplyDto.setOneStatus(1);
        List<BizCoverChargeApplyDto> coverChargeApplyDtos = this.selectBizCoverChargeApply(coverChargeApplyDto);
        if (coverChargeApplyDtos != null && coverChargeApplyDtos.size() > 0) {
            return coverChargeApplyDtos.get(0);
        } else {
            return new BizCoverChargeApplyDto();
        }

    }

    /**
     * 查询评审服务申请列表
     *
     * @param dto 评审服务申请
     * @return 评审服务申请
     */
    @Override
    public List<BizCoverChargeApplyDto> selectBizCoverChargeApply(BizCoverChargeApplyDto dto) {
        QueryWrapper<BizCoverChargeApply> wrapper = new QueryWrapper<>();
        wrapper.eq("bu.proc_def_key", "cover");
        wrapper.eq("bu.del_flag", 0);
        wrapper.eq("ra.del_flag", 0);
        //查询单条
        wrapper.eq(dto.getId() != null, "ra.id", dto.getId());
        //查询审批状态
        wrapper.eq(dto.getResult() != null, "bu.result", dto.getResult());
        //查询审批状态
        wrapper.eq(dto.getStatus() != null, "bu.status", dto.getStatus());
        /**服务申请编号*/
        if(StrUtil.isBlank(dto.getCoverCode())){
            dto.setCoverCode(dto.getReviewCode());
        }
        wrapper.like(StrUtil.isNotBlank(dto.getCoverCode()), "ra.cover_code", dto.getCoverCode());
        /**申请人*/
        wrapper.eq(StrUtil.isNotBlank(dto.getUserName()), "u.user_name", dto.getUserName());
        /**申请时间*/
        String createTime1 = dto.getCreateTime1();
        String createTime2 = dto.getCreateTime2();

        wrapper.between(StrUtil.isNotBlank(createTime1) && StrUtil.isNotBlank(createTime2), "ra.create_time", createTime1, createTime2);
        wrapper.orderByDesc("ra.id");
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        if (dto.getOneStatus() == null) {
            String sql = dataScopeUtil.getScopeSql(sysUser, "d", "u");

            if (StrUtil.isNotBlank(sql) && StrUtil.isBlank(dto.getIsDetails())) {
                wrapper.apply(sql);
            }
        }

        List<BizCoverChargeApplyDto> list = coverChargeApplyMapper.selectAll(wrapper);

        return list;
    }

    /**
     * 评审服务费申请新增
     *
     * @param coverChargeApply 评审、服务费新增
     * @return 结果
     */
    @Override
    public R insertBizCoverChargeApply(BizCoverChargeApply coverChargeApply) {
        try {
            // 抄送人去重
            String cc = coverChargeApply.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                coverChargeApply.setCc(cc);
            }
            coverChargeApply.setDelFlag("0");
            coverChargeApply.setCreateBy(SystemUtil.getUserName());
            coverChargeApply.setCreateTime(new Date());
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyId1 = belongCompany2.get("companyId").toString();
            coverChargeApply.setDeptId(sysUser.getDeptId());
            coverChargeApply.setCompanyId(Long.valueOf(companyId1));
//            String purchaseCode = "CG" + UUID.fastUUID().toString().substring(0, 8);
//            coverChargeApply.setReviewCode(purchaseCode);
            coverChargeApply.setUserId(SystemUtil.getUserId());


            coverChargeApply.setTitle(sysUser.getUserName() + "提交的服务费申请");
            coverChargeApplyMapper.insert(coverChargeApply);
            List<BizCoverChargeInfo> coverChargeInfos = coverChargeApply.getCoverChargeInfos();
//            if (bizGoodsInfos.isEmpty()) {
//                return R.error("请添加物品");
//            }
            //款项用途（1评审费、2服务费、3其他费用）
            for (BizCoverChargeInfo coverChargeInfo : coverChargeInfos) {

                if (StrUtil.isBlank(coverChargeInfo.getProjectCode())) {
                    return R.error("项目编号不能为空");
                } else if (StrUtil.isBlank(coverChargeInfo.getInspectedUnit())) {
                    return R.error("受检单位不能为空");
                } else if (coverChargeInfo.getAmountDate() == null) {
                    return R.error("收款日期不能为空");
                } else if (coverChargeInfo.getPaymentAmount() == null) {
                    return R.error("支付金额不能为空");
                } else if (coverChargeInfo.getContractAmount() == null) {
                    return R.error("合同金额不能为空");
                }
                coverChargeInfo.setReviewAmount(coverChargeInfo.getPaymentAmount());
                coverChargeInfo.setDelFlag("0");
                coverChargeInfo.setCreateBy(SystemUtil.getUserName());
                coverChargeInfo.setCreateTime(new Date());
                coverChargeInfo.setApplyId(coverChargeApply.getId());
                coverChargeInfoMapper.insert(coverChargeInfo);
            }

            // 初始化流程
            BizBusiness business = initBusiness(coverChargeApply);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(coverChargeApply.getCoverCode());
            bizBusinessService.insertBizBusiness(business);

            Map<String, Object> variables = Maps.newHashMap();
            variables.put("cc", coverChargeApply.getCc());

            bizBusinessService.startProcess(business, variables);
            int reimburse = remoteFileService.update(Long.valueOf(coverChargeApply.getId()), coverChargeApply.getCoverCode());
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
    public List<BizCoverChargeInfo> selectBizCoverChargeInfoList(Long applyId) {
        QueryWrapper<BizCoverChargeInfo> bizReviewInfoQueryWrapper = new QueryWrapper<>();
        bizReviewInfoQueryWrapper.eq("apply_id", applyId);
        bizReviewInfoQueryWrapper.eq("del_flag", 0);
        List<BizCoverChargeInfo> coverChargeInfos = coverChargeInfoMapper.selectList(bizReviewInfoQueryWrapper);
        if (coverChargeInfos != null && coverChargeInfos.size() > 0) {
            for (BizCoverChargeInfo coverChargeInfo : coverChargeInfos
            ) {
                if (coverChargeInfo.getAmountDate() != null) {
                    coverChargeInfo.setPdfAmountDate(DateUtil.format(coverChargeInfo.getAmountDate(), "yyyy-MM-dd "));
                    System.err.println(coverChargeInfo.getPdfAmountDate());
                }

            }
        }
        return coverChargeInfos;
    }

    @Override
    public BizCoverChargeApplyDto getPurchase(BizCoverChargeApplyDto coverChargeApplyDto) {
        // 抄送人赋值
        if (StrUtil.isNotBlank(coverChargeApplyDto.getCc())) {
            String[] split = coverChargeApplyDto.getCc().split(",");
            List<String> ccList = new ArrayList<>();
            for (String cc : split) {
                SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
                if (sysUser == null) {
                    ccList.add("");

                } else {

                    ccList.add(sysUser.getUserName());
                }
            }
            coverChargeApplyDto.setCcName(String.join(",", ccList));
        }


        BizGoodsInfo bizGoodsInfo = new BizGoodsInfo();
        bizGoodsInfo.setPurchaseId(coverChargeApplyDto.getId());
        List<BizCoverChargeInfo> coverChargeInfos = this.selectBizCoverChargeInfoList(coverChargeApplyDto.getId());
        List<SysAttachment> sysAttachments = remoteFileService.getList(coverChargeApplyDto.getId(), "cover");
        if (coverChargeInfos == null) {
            coverChargeInfos = new ArrayList<>();
        }
        if (sysAttachments == null) {
            sysAttachments = new ArrayList<>();
        }
        coverChargeApplyDto.setCoverChargeInfos(coverChargeInfos);
        coverChargeApplyDto.setSysAttachments(sysAttachments);
        //打印人赋值
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        coverChargeApplyDto.setPdfName(sysUser.getUserName());
        return coverChargeApplyDto;
    }

    @Override
    public BizCoverChargeApplyDto selectOne(Long tableId) {

        BizCoverChargeApplyDto coverChargeApplyDto = this.selectBizCoverChargeApplyById(tableId);
//            coverChargeApplyDto.setTitle(business.getTitle());
        return getPurchase(coverChargeApplyDto);

    }

    /**
     * 初始化业务流程
     *
     * @param coverChargeApply
     * @return
     * @author zmr
     */
    private BizBusiness initBusiness(BizCoverChargeApply coverChargeApply) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "cover");
        example.setOrderByClause("VERSION_ DESC");
        List<ActReProcdef> actReProcdefs = actReProcdefMapper.selectByExample(example);
        ActReProcdef actReProcdef = new ActReProcdef();
        if (!actReProcdefs.isEmpty()) {
            actReProcdef = actReProcdefs.get(0);
        }
        BizBusiness business = new BizBusiness();
        business.setTableId(coverChargeApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(coverChargeApply.getTitle());
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
