package com.ruoyi.activiti.service;


import com.ruoyi.activiti.domain.car.BizCarRegistration;
import com.ruoyi.activiti.domain.car.BizReserveDetail;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zh
 * @date 2022-02-21
 * @desc 汽车管理
 */
public interface BizCarRegistrationService {
    /**
     * 车辆管理数据分页
     * @param bizCarRegistration
     * @return
     */
    List<BizCarRegistration> listAllPage(BizCarRegistration bizCarRegistration);

    /**
     * 查询可预约车辆
     * @param goDate
     * @return
     */
    List<BizCarRegistration> listIdle(String goDate,String backDate);

    /**
     * 新增车辆管理
     * @param bizCarRegistration
     * @return
     */
    BizCarRegistration save(BizCarRegistration bizCarRegistration);
    /**
     * 修改车辆管理
     * @param bizCarRegistration
     * @return
     */
    BizCarRegistration update(BizCarRegistration bizCarRegistration);
    /**
     * 删除车辆管理
     * @param ids
     * @return
     */
    void delete(Long [] ids);

    /**
     * 获取最新里程数
     * @param plateNumber
     * @return
     */
    BigDecimal getLatestMileage(String plateNumber);

    /**
     * 获取车辆在用、预约信息
     * @param plateNumber 车牌号
     * @param status 状态
     * @return
     */
    List<BizReserveDetail> getReserveDetail(String plateNumber,String status);

    /**
     * 手动还车
     * @param id
     * @param remark
     */
    void returnCar(Long id, String remark);
}
