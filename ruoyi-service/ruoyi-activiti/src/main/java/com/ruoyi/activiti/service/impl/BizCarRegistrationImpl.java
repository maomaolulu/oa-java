package com.ruoyi.activiti.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.activiti.domain.car.BizCarRegistration;
import com.ruoyi.activiti.domain.car.BizReserveDetail;
import com.ruoyi.activiti.mapper.BizCarRegistrationMapper;
import com.ruoyi.activiti.mapper.BizReserveDetailMapper;
import com.ruoyi.activiti.service.BizCarRegistrationService;
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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 汽车管理
 *
 * @author zh
 * @date 2022-02-21
 */
@Service
@Slf4j
public class BizCarRegistrationImpl implements BizCarRegistrationService {
    private final BizCarRegistrationMapper bizCarRegistrationMapper;
    private final RemoteDeptService remoteDeptService;
    private final BizReserveDetailMapper reserveDetailMapper;

    @Autowired
    private DataScopeUtil dataScopeUtil;
    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    public BizCarRegistrationImpl(BizCarRegistrationMapper bizCarRegistrationMapper, RemoteDeptService remoteDeptService, BizReserveDetailMapper reserveDetailMapper) {
        this.bizCarRegistrationMapper = bizCarRegistrationMapper;
        this.remoteDeptService = remoteDeptService;
        this.reserveDetailMapper = reserveDetailMapper;
    }

    @Override
    public List<BizCarRegistration> listAllPage(BizCarRegistration bizCarRegistration) {
        QueryWrapper<BizCarRegistration> bizCarRegistrationQueryWrapper = new QueryWrapper<>();

        // 用车人
        bizCarRegistrationQueryWrapper.like(StrUtil.isNotBlank(bizCarRegistration.getCarUser()), "ca.car_user", bizCarRegistration.getCarUser());
        //车牌号
        bizCarRegistrationQueryWrapper.like(StrUtil.isNotBlank(bizCarRegistration.getPlateNumber()), "ca.plate_number", bizCarRegistration.getPlateNumber());
        //车辆属性0：公车，1：私车
        bizCarRegistrationQueryWrapper.like(bizCarRegistration.getTypes() != null, "ca.types", bizCarRegistration.getTypes());
        //公司
        bizCarRegistrationQueryWrapper.eq(bizCarRegistration.getCompanyId() != null, "ca.company_id", bizCarRegistration.getCompanyId());
        //部门
        bizCarRegistrationQueryWrapper.eq(bizCarRegistration.getDeptId() != null, "ca.dept_id", bizCarRegistration.getDeptId());
        bizCarRegistrationQueryWrapper.orderByDesc("ca.id");
        List<BizCarRegistration> bizCarRegistrations = bizCarRegistrationMapper.selectAll(bizCarRegistrationQueryWrapper);

        //数据权限
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        String sql = dataScopeUtil.getScopeSql(sysUser, "d", null);
        if (StrUtil.isNotBlank(sql)) {
            bizCarRegistrationQueryWrapper.apply(sql);
        }

        bizCarRegistrations.stream().forEach(carRegistration -> {

            List<BizReserveDetail> reserveDetail = getReserveDetail(carRegistration.getPlateNumber(), "");
            carRegistration.setStatusNow("空闲");
            reserveDetail.stream().forEach(bizReserveDetail -> {
                Date date = new Date();
                if (StrUtil.isNotBlank(bizCarRegistration.getStatusDate())) {
                    date = DateUtil.parse(bizCarRegistration.getStatusDate());
                }
                if (date.compareTo(bizReserveDetail.getStartDate()) == 1
                        && date.compareTo(bizReserveDetail.getEndDate()) == -1
                        || date.compareTo(bizReserveDetail.getStartDate()) == 0
                        || date.compareTo(bizReserveDetail.getEndDate()) == 0) {
                    carRegistration.setStatusNow(getStatusNow(bizReserveDetail.getCarStatus()));
                }
            });

        });
        return bizCarRegistrations;
    }

    private String getStatusNow(String status) {
        switch (status) {
            case "1":
                status = "在用";
                break;
            case "2":
                status = "预约";
                break;
            default:
                break;
        }
        return status;
    }

    @Override
    public List<BizCarRegistration> listIdle(String goDate, String backDate) {
        //数据权限
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
        List<String> unPlateNumber = reserveDetailMapper.getUnavailablePlateNumber(goDate, backDate);
        // 获取可预约车辆信息
        QueryWrapper<BizCarRegistration> wrapper = new QueryWrapper<>();
        //车辆属性0：公车，1：私车
        wrapper.eq("ca.types", 0);
        wrapper.eq("ca.status", "1");
        wrapper.eq("ca.company_id", belongCompany2.get("companyId").toString());
        wrapper.notIn(!unPlateNumber.isEmpty(), "plate_number", unPlateNumber);
        List<BizCarRegistration> bizCarRegistrations = bizCarRegistrationMapper.selectPlateNumber(wrapper);

        return bizCarRegistrations;
    }

    @Override
    public BizCarRegistration save(BizCarRegistration bizCarRegistration) {
        QueryWrapper<BizCarRegistration> wrapper = new QueryWrapper<>();
        wrapper.eq("plate_number", bizCarRegistration.getPlateNumber());
        Integer integer = bizCarRegistrationMapper.selectCount(wrapper);
        if (integer > 0) {
            throw new StatefulException("车牌号已存在");
        }

        bizCarRegistration.setCreateBy(SystemUtil.getUserId());
        bizCarRegistrationMapper.insert(bizCarRegistration);
        return bizCarRegistration;
    }

    @Override
    public BizCarRegistration update(BizCarRegistration bizCarRegistration) {
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        bizCarRegistration.setStatusUpdateUser(sysUser.getUserName());
        bizCarRegistration.setStatusUpdateTime(new Date());
        bizCarRegistration.setUpdateBy(userId);
        bizCarRegistrationMapper.updateById(bizCarRegistration);
        return bizCarRegistration;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long[] ids) {
        try {

            for (Long id : ids
            ) {
                BizCarRegistration bizCarRegistration = new BizCarRegistration();
                bizCarRegistration.setId(id);
                bizCarRegistration.setDelFlag("1");
                bizCarRegistrationMapper.updateById(bizCarRegistration);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    /**
     * 获取最新里程数
     *
     * @param plateNumber
     * @return
     */
    @Override
    public BigDecimal getLatestMileage(String plateNumber) {
        QueryWrapper<BizCarRegistration> wrapper = new QueryWrapper<>();
        wrapper.eq("plate_number", plateNumber);
        BizCarRegistration result = bizCarRegistrationMapper.selectOne(wrapper);
        return result.getLatestMileage();
    }

    /**
     * 获取车辆在用、预约信息
     *
     * @param plateNumber
     * @param status
     * @return
     */
    @Override
    public List<BizReserveDetail> getReserveDetail(String plateNumber, String status) {
        QueryWrapper<BizReserveDetail> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(status), "car_status", status);
        wrapper.eq("plate_number", plateNumber);
        wrapper.eq("del_flag", "0");
        List<BizReserveDetail> reserveDetails = reserveDetailMapper.selectList(wrapper);
        return reserveDetails;
    }

    /**
     * 手动还车
     *
     * @param id
     * @param remark
     */
    @Override
    public void returnCar(Long id, String remark) {
        BizReserveDetail reserveDetail = reserveDetailMapper.selectById(id);
        reserveDetail.setRemark(remark);
        reserveDetail.setDelFlag("1");
        reserveDetail.setUpdateTime(new Date());
        reserveDetail.setUpdateBy(SystemUtil.getUserName());
        reserveDetailMapper.updateById(reserveDetail);
    }
}
