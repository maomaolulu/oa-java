package com.ruoyi.activiti.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.car.BizCarApply;
import com.ruoyi.activiti.domain.car.BizCarDelay;
import com.ruoyi.activiti.domain.car.BizCarInfo;
import com.ruoyi.activiti.domain.car.BizReserveDetail;
import com.ruoyi.activiti.domain.dto.CancelUseCarDTO;
import com.ruoyi.activiti.domain.dto.CheckDelayCarDTO;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.mapper.*;
import com.ruoyi.activiti.service.BizCarApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.activiti.utils.MailService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.DataScopeUtil;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 用车申请
 *
 * @author zh
 * @date 2022-02-22
 */
@Service
@Slf4j
public class BizCarApplyImpl implements BizCarApplyService {
    private final BizCarApplyMapper bizCarApplyMapper;
    private final BizCarInfoMapper bizCarInfoMapper;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final RemoteDeptService remoteDeptService;
    private final BizBusinessMapper bizBusinessMapper;
    private final BizReserveDetailMapper reserveDetailMapper;
    private final BizCarDelayMapper bizCarDelayMapper;
    private final MailService mailService;

    @Autowired
    private DataScopeUtil dataScopeUtil;
    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    public BizCarApplyImpl(BizCarApplyMapper bizCarApplyMapper, BizCarInfoMapper bizCarInfoMapper, ActReProcdefMapper actReProcdefMapper, IBizBusinessService bizBusinessService, RemoteDeptService remoteDeptService, BizBusinessMapper bizBusinessMapper, BizReserveDetailMapper reserveDetailMapper, BizCarDelayMapper bizCarDelayMapper, MailService mailService) {
        this.bizCarApplyMapper = bizCarApplyMapper;
        this.bizCarInfoMapper = bizCarInfoMapper;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.remoteDeptService = remoteDeptService;
        this.bizBusinessMapper = bizBusinessMapper;
        this.reserveDetailMapper = reserveDetailMapper;
        this.bizCarDelayMapper = bizCarDelayMapper;
        this.mailService = mailService;
    }

    @Override
    public List<BizCarApply> listAllPage(BizCarApply bizCarApply) {
        QueryWrapper<BizCarApply> bizCarApplyQueryWrapper = new QueryWrapper<>();
        bizCarApplyQueryWrapper.eq("bu.del_flag", "0");
        bizCarApplyQueryWrapper.eq("ca.del_flag", "0");
        bizCarApplyQueryWrapper.eq("bu.proc_def_key", "carApply");
        bizCarApplyQueryWrapper.eq(bizCarApply.getId() != null, "ca.id", bizCarApply.getId());
        //公司
        bizCarApplyQueryWrapper.eq(bizCarApply.getCompanyId() != null, "ca.company_id", bizCarApply.getCompanyId());
        //部门
        bizCarApplyQueryWrapper.eq(bizCarApply.getDeptId() != null, "ca.dept_id", bizCarApply.getDeptId());
        bizCarApplyQueryWrapper.eq(bizCarApply.getDeptUseId() != null, "ca.dept_use_id", bizCarApply.getDeptUseId());
        /**申请人*/
        bizCarApplyQueryWrapper.eq(StrUtil.isNotBlank(bizCarApply.getCreateByName()), "u.user_name", bizCarApply.getCreateByName());
        /**用车申请编号*/
        bizCarApplyQueryWrapper.like(StrUtil.isNotBlank(bizCarApply.getCarCode()), "ca.car_code", bizCarApply.getCarCode());
        bizCarApplyQueryWrapper.like(StrUtil.isNotBlank(bizCarApply.getPlateNumber()), "ca.plate_number", bizCarApply.getPlateNumber());
        /**申请时间*/
        String createTime1 = bizCarApply.getCreateTime1();
        String createTime2 = bizCarApply.getCreateTime2();
        bizCarApplyQueryWrapper.between(StrUtil.isNotBlank(createTime1) && StrUtil.isNotBlank(createTime2), "ca.create_time", createTime1, createTime2);
        // 审批结果
        Integer result = bizCarApply.getResult();
        bizCarApplyQueryWrapper.eq(result != null, "bu.result", result);
        bizCarApplyQueryWrapper.orderByDesc("ca.id");
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        List<Long> roleIds = sysUser.getRoleIds();
//        String targetSql1 = bizCarApplyQueryWrapper.getTargetSql();
        if (bizCarApply.getOneStatus() == null) {
            String sql;
            // 是车辆管理员
            if (roleIds.lastIndexOf(112L) != -1 || roleIds.lastIndexOf(1L) != -1) {
                List<SysRole> roles = sysUser.getRoles();
                for (SysRole role : roles) {
                    if (role.getRoleId() == 112 || role.getRoleId() == 1) {
                        role.setDataScope("6");
                    }
                }
                sql = dataScopeUtil.getScopeSql(sysUser, "d2", null);
            } else {
                sql = dataScopeUtil.getScopeSql(sysUser, "d", null);
            }
            if (StrUtil.isNotBlank(sql)) {
                bizCarApplyQueryWrapper.apply(sql);
            }
        }
//        String targetSql = bizCarApplyQueryWrapper.getTargetSql();
        List<BizCarApply> bizCarApplys = bizCarApplyMapper.selectAll(bizCarApplyQueryWrapper);
        for (BizCarApply bizCarApply1 : bizCarApplys) {
            if (bizCarApply1.getDeptUseId() != null) {
                //隶属部门名称
                bizCarApply1.setDeptUseName(remoteDeptService.selectSysDeptByDeptId(bizCarApply1.getDeptUseId()).getDeptName());
            }
        }

        // 导出数据处理
        if (StrUtil.isNotBlank(bizCarApply.getExport())) {
            for (BizCarApply bizCarApply1 : bizCarApplys) {
                // 车辆属性
                String carTypes = bizCarApply1.getCarTypes();
                if (StrUtil.isNotBlank(carTypes)) {
                    switch (carTypes) {
                        case "0":
                            bizCarApply1.setCarType("公车");
                            break;
                        case "2":
                            bizCarApply1.setCarType("租车");
                            break;
                        default:
                            bizCarApply1.setCarType("私车");
                    }
                }
                // 审批结果
                result = bizCarApply1.getResult();
                if (result != null) {
                    switch (result) {
                        case 1:
                            bizCarApply1.setResultCn("审批中");
                            break;
                        case 2:
                            bizCarApply1.setResultCn("通过");
                            break;
                        case 3:
                            bizCarApply1.setResultCn("驳回");
                            break;
                        case 4:
                            bizCarApply1.setResultCn("撤销");
                            break;
                        default:
                            bizCarApply1.setResultCn("中止");
                    }
                }
                // 司机预约
                String isDriver = bizCarApply1.getIsDriver();
                if (StrUtil.isNotBlank(isDriver)) {
                    bizCarApply1.setIsDriver("0".equals(isDriver) ? "不需要" : "需要");
                }
            }
        }

        return bizCarApplys;
    }

    @Override
    public BizCarApply selectOne(Long tableId) {
        BizCarApply bizCarApply = new BizCarApply();
        bizCarApply.setId(tableId);
        bizCarApply.setOneStatus(1);
        List<BizCarApply> bizCarApplies = listAllPage(bizCarApply);

        BizCarApply bizCarApply1 = new BizCarApply();
        if (bizCarApplies != null && bizCarApplies.size() > 0) {
            bizCarApply1 = bizCarApplies.get(0);
            QueryWrapper<BizCarInfo> bizCarInfoQueryWrapper = new QueryWrapper<>();
            bizCarInfoQueryWrapper.eq("car_apply_id", bizCarApply1.getId());
            List<BizCarInfo> bizCarInfos = bizCarInfoMapper.selectList(bizCarInfoQueryWrapper);
            bizCarApply1.setBizCarInfos(bizCarInfos);

            // 抄送人赋值
            if (StrUtil.isNotBlank(bizCarApply1.getCc())) {
                String[] split = bizCarApply1.getCc().split(",");
                List<String> ccList = new ArrayList<>();
                for (String cc : split) {
                    SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
                    if (sysUser == null) {
                        ccList.add("");

                    } else {

                        ccList.add(sysUser.getUserName());
                    }
                }
                bizCarApply1.setCcName(String.join(",", ccList));
            }
        }

        bizCarApply1.setDeptUseName(remoteDeptService.selectSysDeptByDeptId(bizCarApply1.getDeptUseId()).getDeptName());
        bizCarApply1.setCompanyUseName(remoteDeptService.selectSysDeptByDeptId(bizCarApply1.getCompanyUseId()).getDeptName());

        return bizCarApply1;
    }

    @Override
    public BizCarApply save(BizCarApply bizCarApply) {
        try {
            // 抄送人去重
            String cc = bizCarApply.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                bizCarApply.setCc(cc);
            }
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyId1 = belongCompany2.get("companyId").toString();
            bizCarApply.setDeptId(sysUser.getDeptId());
            bizCarApply.setCompanyId(Long.valueOf(companyId1));
            bizCarApply.setCreateBy(SystemUtil.getUserId());
            bizCarApply.setCreateTime(new Date());
            bizCarApply.setTitle(sysUser.getUserName() + "提交的用车申请");
            Date goBackDate = bizCarApply.getGoBackDate();
            String formatDate = DateUtil.formatDate(goBackDate);
            Date date = DateUtil.parse(formatDate + " 23:59:59");
            bizCarApply.setGoBackDate(date);
            //所属公司id
//            Map<String, Object> belongCompany3 = remoteDeptService.getBelongCompany2(bizCarApply.getDeptUseId());
//            String belongCompanyId = belongCompany3.get("companyId").toString();
//            bizCarApply.setCompanyUseId(Long.valueOf(belongCompanyId));

            bizCarApplyMapper.insert(bizCarApply);
//            List<BizCarInfo> bizCarInfos = bizCarApply.getBizCarInfos();
//            for (BizCarInfo b : bizCarInfos
//            ) {
//                b.setCarApplyId(bizCarApply.getId());
//                b.setCreateBy(SystemUtil.getUserId());
//                bizCarInfoMapper.insert(b);
//            }

            // 初始化流程
            BizBusiness business = initBusiness(bizCarApply);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(bizCarApply.getCarCode());
            bizBusinessService.insertBizBusiness(business);
            Map<String, Object> variables = Maps.newHashMap();
            variables.put("cc", bizCarApply.getCc());
            variables.put("deptId", bizCarApply.getCompanyUseId());
            bizBusinessService.startProcess(business, variables);

            // 记录车辆该时间段为预约中
            BizReserveDetail reserveDetail = new BizReserveDetail();
            reserveDetail.setApplyId(bizCarApply.getId());
            reserveDetail.setStartDate(bizCarApply.getGoToDate());
            reserveDetail.setEndDate(bizCarApply.getGoBackDate());
            reserveDetail.setPlateNumber(bizCarApply.getPlateNumber());
            reserveDetail.setCarUser(bizCarApply.getCarUser());
            reserveDetail.setCarStatus("2");
            reserveDetail.setCreateBy(sysUser.getLoginName());
            reserveDetail.setCreateTime(new Date());
            reserveDetail.setDelFlag("0");
            reserveDetailMapper.insert(reserveDetail);
//            QueryWrapper<BizCarRegistration> wrapper1 = new QueryWrapper<>();
//            wrapper1.eq("plate_number",bizCarApply.getPlateNumber());
//            BizCarRegistration bizCarRegistration = carRegistrationMapper.selectOne(wrapper1);
//            bizCarRegistration.setCarStatus("2");
//            carRegistrationMapper.updateById(bizCarRegistration);

            return bizCarApply;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    public BizCarApply update(BizCarApply bizCarApply) {
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        bizCarApply.setUpdateBy(sysUser.getUserId());
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long[] ids) {
        try {

            for (Long id : ids
            ) {
                BizCarApply bizCarApply = new BizCarApply();
                bizCarApply.setId(id);
                bizCarApply.setDelFlag("1");
                bizCarApplyMapper.updateById(bizCarApply);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    /**
     * 初始化业务流程
     *
     * @param bizCarApply
     * @return
     * @author zmr
     */
    private BizBusiness initBusiness(BizCarApply bizCarApply) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "carApply");
        example.setOrderByClause("VERSION_ DESC");
        List<ActReProcdef> actReProcdefs = actReProcdefMapper.selectByExample(example);
        ActReProcdef actReProcdef = new ActReProcdef();
        if (!actReProcdefs.isEmpty()) {
            actReProcdef = actReProcdefs.get(0);
        }
        BizBusiness business = new BizBusiness();
        business.setTableId(bizCarApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(bizCarApply.getTitle());
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


    @Override
    public List<BizCarApply> getUnpaidCarList(BizCarApply bizCarApply) {
        QueryWrapper<BizCarApply> bizCarApplyQueryWrapper = new QueryWrapper<>();
        bizCarApplyQueryWrapper.eq("bu.del_flag", "0");
        bizCarApplyQueryWrapper.eq("ca.del_flag", "0");
        //用车申请
        bizCarApplyQueryWrapper.eq("bu.proc_def_key", "carApply");
        //不是撤销的和驳回的
        bizCarApplyQueryWrapper.notIn("bu.result", "3,4");

        //车牌号
        bizCarApplyQueryWrapper.like(StringUtils.isNotBlank(bizCarApply.getPlateNumber()), "ca..plant_number", bizCarApply.getPlateNumber());
        //部门
        //bizCarApplyQueryWrapper.eq(bizCarApply.getDeptId() != null, "ca.dept_id", bizCarApply.getDeptId());
        bizCarApplyQueryWrapper.eq(bizCarApply.getDeptUseId() != null, "ca.dept_use_id", bizCarApply.getDeptUseId());
        /**申请人*/
        bizCarApplyQueryWrapper.eq(StrUtil.isNotBlank(bizCarApply.getCreateByName()), "u.user_name", bizCarApply.getCreateByName());
        /**用车申请编号*/
        bizCarApplyQueryWrapper.like(StrUtil.isNotBlank(bizCarApply.getCarCode()), "ca.car_code", bizCarApply.getCarCode());
        /**申请时间*/
        String createTime1 = bizCarApply.getCreateTime1();
        String createTime2 = bizCarApply.getCreateTime2();
        bizCarApplyQueryWrapper.isNull("su.relation_code");

        bizCarApplyQueryWrapper.between(StrUtil.isNotBlank(createTime1) && StrUtil.isNotBlank(createTime2), "ca.create_time", createTime1, createTime2);
        bizCarApplyQueryWrapper.orderByDesc("ca.id");
        List<BizCarApply> bizCarApplys = bizCarApplyMapper.getUnpaidCarList(bizCarApplyQueryWrapper);
        for (BizCarApply bizCarApply1 : bizCarApplys) {
            if (bizCarApply1.getDeptUseId() != null) {
                if ((remoteDeptService.selectSysDeptByDeptId(bizCarApply1.getDeptUseId()) != null)) {
                    //隶属部门名称
                    if (StringUtils.isNotBlank(remoteDeptService.selectSysDeptByDeptId(bizCarApply1.getDeptUseId()).getDeptName())) {
                        bizCarApply1.setDeptUseName(remoteDeptService.selectSysDeptByDeptId(bizCarApply1.getDeptUseId()).getDeptName());
                    }
                }
            }
        }
        //数据权限
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        String sql = dataScopeUtil.getScopeSql(sysUser, "d", null);
        if (bizCarApply.getOneStatus() == null) {
            if (StrUtil.isNotBlank(sql)) {
                bizCarApplyQueryWrapper.apply(sql);
            }
        }
        return bizCarApplys;
    }

    @Override
    public R cancelUseCar(CancelUseCarDTO cancelUseCarDTO) {
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        BizCarApply bizCarApply = bizCarApplyMapper.selectById(cancelUseCarDTO.getId());
        if (!sysUser.getUserName().equals(cancelUseCarDTO.getCreateByName())) {
            return R.error("只支持申请人本人取消用车!");
        }
        //取消用车
        if ("1".equals(cancelUseCarDTO.getState())) {
            //修改当前端车辆的状态为空闲
            UpdateWrapper<BizReserveDetail> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("apply_id", cancelUseCarDTO.getId());
            BizReserveDetail bizReserveDetail = new BizReserveDetail();
            bizReserveDetail.setCarStatus("");
            bizReserveDetail.setUpdateTime(new Date());
            bizReserveDetail.setUpdateBy(sysUser.getLoginName());
            reserveDetailMapper.update(bizReserveDetail, updateWrapper);
            return R.ok("取消成功");
        }

        //提前还车
        if ("2".equals(cancelUseCarDTO.getState())) {
            return R.ok("跳转还车页面");
        }

        //延迟还车
        //修改当前端车辆的状态为空闲
        QueryWrapper<BizBusiness> queryWrapper = new QueryWrapper<>();
        Example example = new Example(BizBusiness.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tableId", cancelUseCarDTO.getId());
        criteria.andEqualTo("procDefKey", "carApply");
        BizBusiness bizBusiness = bizBusinessMapper.selectOneByExample(example);
        if (bizBusiness.getResult() == 1) {
            return R.error("该申请还在审批中不能申请延迟还车");
        }

        if (!sysUser.getUserName().equals(cancelUseCarDTO.getCreateByName())) {
            return R.error("只支持申请人本人延迟还车!");
        }

        QueryWrapper<BizCarDelay> delayQueryWrapper = new QueryWrapper<>();
        delayQueryWrapper.eq("aply_relation", cancelUseCarDTO.getId());
        BizCarDelay bizCarDelay = bizCarDelayMapper.selectOne(delayQueryWrapper);
        //是不是第一次延迟申请  是的话新增  不是的话修改
        if (bizCarDelay != null) {
            if ("0".equals(bizCarDelay.getState())) {
                return R.error("上一次延迟还车还未审批成功！");
            }
            if (bizCarDelay.getTime() >= 3) {
                return R.error("延迟还车次数最多不得超过3次！");
            }
            UpdateWrapper<BizCarDelay> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", bizCarDelay.getId());
            BizCarDelay updateBizCarDelay = new BizCarDelay();
            updateBizCarDelay.setCreateBy(sysUser.getLoginName());
            updateBizCarDelay.setTime(bizCarDelay.getTime() + 1);
            updateBizCarDelay.setCreateTime(new Date());
            updateBizCarDelay.setCreateById(sysUser.getUserId());

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(bizCarApply.getGoBackDate());
            calendar.add(Calendar.HOUR_OF_DAY, 6);
            updateBizCarDelay.setDelayTime(calendar.getTime());
            updateBizCarDelay.setState("0");
            bizCarDelayMapper.update(updateBizCarDelay, updateWrapper);
        } else {
            BizCarDelay bizCarDelay1 = new BizCarDelay();
            bizCarDelay1.setCreateBy(sysUser.getLoginName());
            bizCarDelay1.setTime(1);
            bizCarDelay1.setCreateTime(new Date());
            bizCarDelay1.setCreateById(sysUser.getUserId());
            bizCarDelay1.setAplyRelation(cancelUseCarDTO.getId());
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(bizCarApply.getGoBackDate());
            calendar.add(Calendar.HOUR_OF_DAY, 6);
            bizCarDelay1.setDelayTime(calendar.getTime());
            bizCarDelay1.setState("0");
            bizCarDelayMapper.insert(bizCarDelay1);
        }
        return R.ok();
    }


    @Override
    public List<BizCarApply> getCarCheckList(BizCarApply bizCarApply) {
        //数据权限
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);

        QueryWrapper<BizCarApply> bizCarApplyQueryWrapper = new QueryWrapper<>();
        bizCarApplyQueryWrapper.eq("bu.del_flag", "0");
        bizCarApplyQueryWrapper.eq("ca.del_flag", "0");
        //用车申请
        bizCarApplyQueryWrapper.eq("bu.proc_def_key", "carApply");
        //不是撤销的和驳回的
        bizCarApplyQueryWrapper.notIn("bu.result", "3,4");

        //正在延迟审核中的
        bizCarApplyQueryWrapper.eq("de.state", "0");

        //部门主管
        bizCarApplyQueryWrapper.eq("d.leader", sysUser.getLeader());

        //车牌号
        bizCarApplyQueryWrapper.like(StringUtils.isNotBlank(bizCarApply.getPlateNumber()), "ca.plant_number", bizCarApply.getPlateNumber());
        //部门
        //bizCarApplyQueryWrapper.eq(bizCarApply.getDeptId() != null, "ca.dept_id", bizCarApply.getDeptId());
        bizCarApplyQueryWrapper.eq(bizCarApply.getDeptUseId() != null, "ca.dept_use_id", bizCarApply.getDeptUseId());
        /**申请人*/
        bizCarApplyQueryWrapper.eq(StrUtil.isNotBlank(bizCarApply.getCreateByName()), "u.user_name", bizCarApply.getCreateByName());
        /**用车申请编号*/
        bizCarApplyQueryWrapper.like(StrUtil.isNotBlank(bizCarApply.getCarCode()), "ca.car_code", bizCarApply.getCarCode());
        /**申请时间*/
        String createTime1 = bizCarApply.getCreateTime1();
        String createTime2 = bizCarApply.getCreateTime2();
        bizCarApplyQueryWrapper.isNull("su.relation_code");

        bizCarApplyQueryWrapper.between(StrUtil.isNotBlank(createTime1) && StrUtil.isNotBlank(createTime2), "ca.create_time", createTime1, createTime2);
        bizCarApplyQueryWrapper.orderByDesc("ca.id");
        List<BizCarApply> bizCarApplys = bizCarApplyMapper.getCarCheckList(bizCarApplyQueryWrapper);
        for (BizCarApply bizCarApply1 : bizCarApplys) {
            if (bizCarApply1.getDeptUseId() != null) {
                //隶属部门名称
                bizCarApply1.setDeptUseName(remoteDeptService.selectSysDeptByDeptId(bizCarApply1.getDeptUseId()).getDeptName());
            }
        }

        String sql = dataScopeUtil.getScopeSql(sysUser, "d", null);
        if (bizCarApply.getOneStatus() == null) {
            if (StrUtil.isNotBlank(sql)) {
                bizCarApplyQueryWrapper.apply(sql);
            }
        }
        return bizCarApplys;
    }

    @Override
    @Transactional
    public R checkCarDelay(CheckDelayCarDTO checkDelayCarDTO) {
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        BizCarDelay bizCarDelay = bizCarDelayMapper.selectById(checkDelayCarDTO.getDelayId());
        BizCarApply bizCarApply = bizCarApplyMapper.selectById(checkDelayCarDTO.getId());
        UpdateWrapper<BizCarDelay> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", checkDelayCarDTO.getDelayId());
        BizCarDelay updateBizCarDelay = new BizCarDelay();
        updateBizCarDelay.setUpdateBy(sysUser.getLoginName());
        updateBizCarDelay.setState(checkDelayCarDTO.getState());
        updateBizCarDelay.setApprovalTime(new Date());
        updateBizCarDelay.setRemark(checkDelayCarDTO.getRemark());
        updateBizCarDelay.setUpdateById(sysUser.getUserId());
        bizCarDelayMapper.update(updateBizCarDelay, updateWrapper);

        UpdateWrapper<BizCarApply> carApplyUpdateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", checkDelayCarDTO.getId());
        BizCarApply updateBizCarApply = new BizCarApply();
        updateBizCarApply.setGoBackDate(bizCarDelay.getDelayTime());
        updateBizCarApply.setUpdateBy(sysUser.getUserId());
        updateBizCarApply.setUpdateTime(new Date());
        bizCarApplyMapper.update(updateBizCarApply, carApplyUpdateWrapper);


        SysUser sysUser1 = remoteUserService.selectSysUserByUserId(bizCarDelay.getCreateById());
        String txt =
                "<body>" +
                        "<p>" +
                        "延迟还车提醒" + "<strong style='color:#2d8ccc;'>" +
                        "</p>" +
                        "<p>" +
                        "<span>" + "安联云管家" + "<strong style='color:#545454; text-align=left'>" + "</span>" + "<span>" + "详情" + "<strong style='color:#7093db; text-align=right'>" + "</span>" +
                        "</p>" +
                        "<p>" +
                        "</p>" +
                        "<p>" +
                        "</p>" +
                        "<p>" +
                        sysUser1.getUserName() + DateUtil.format(bizCarDelay.getCreateTime(), "yyyy年MM月dd日 HH时mm分") + "提交的延迟还车已经通过<strong style='color:#2d8ccc;'>" +
                        "</p>" +
                        "<p>" +
                        "</p>" +
                        "<p>" +
                        "</p>" +
                        "<p>" +
                        "</p>" +
                        "<p>" +
                        "车牌号:" + bizCarApply.getPlateNumber() + "<strong style='color:#000000;'>" +
                        "</p>" +
                        "<p>" +
                        "用车人:" + bizCarApply.getCarUser() + "<strong style='color:#000000;'>" +
                        "</p>" +
                        "<p>" +
                        "预计回程时间变更为:" + bizCarDelay.getDelayTime() + "<strong style='color:#000000;'>" +
                        "</p>" +
                        "</p>" +
                        "<p>" +
                        "</p>" +
                        "<p>" +
                        "</p>" +
                        "<p>" +
                        "</p>" +
                        "<p>" +
                        "<h4>" +
                        "<strong style='text-align='>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
        if (StrUtil.isNotBlank(sysUser1.getEmail())) {
            mailService.send(txt, "延迟还车提醒", sysUser1.getEmail(), sysUser.getUserName(), sysUser1.getCid());
        }

        return R.ok("审核成功");
    }


}
