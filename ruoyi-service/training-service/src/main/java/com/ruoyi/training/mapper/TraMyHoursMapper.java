package com.ruoyi.training.mapper;

import com.ruoyi.training.entity.TraMyHours;
import org.springframework.stereotype.Repository;


/**
 * 我的学时 mapper
 *
 * @author hjy
 */

@Repository
public interface TraMyHoursMapper {
    /**
     * 获取我的学时
     *
     * @param myHours 我的学时相关信息
     * @return 我的学时相关信息
     */
    TraMyHours getMyHours(TraMyHours myHours);
}
