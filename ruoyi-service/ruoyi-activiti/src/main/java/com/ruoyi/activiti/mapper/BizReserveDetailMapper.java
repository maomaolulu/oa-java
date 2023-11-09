package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.activiti.domain.car.BizReserveDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 车辆使用明细
 * @author zx
 * @date 2022/3/14 17:33
 */
@Repository
public interface BizReserveDetailMapper extends BaseMapper<BizReserveDetail> {
    /**
     * 获取不可用车牌号
     * @param goDate
     * @param backDate
     * @return
     */
    @Select("SELECT  a.plate_number from biz_reserve_detail a WHERE a.del_flag = 0 and ( ( #{goDate} >= a.start_date and #{goDate} <= a.end_date) " +
            "or " +
            "(#{backDate} >= a.start_date and #{backDate} <= a.end_date) " +
            "or " +
            "(#{goDate} <= a.start_date and #{backDate} >= a.end_date) ) ")
    List<String> getUnavailablePlateNumber(@Param("goDate") String goDate,@Param("backDate") String backDate);
}
