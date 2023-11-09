package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.my_apply.BizContractInfo;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @Author yrb
 * @Date 2023/5/22 14:09
 * @Version 1.0
 * @Description 合同评审-合同信息
 */
@Repository
public interface BizContractInfoMapper extends BaseMapper<BizContractInfo> {

    @Select(" select t2.* from biz_contract_info t1 " +
            " left join biz_business t2 on t1.id = t2.table_id " +
            " ${ew.customSqlSegment} ")
    BizBusiness selectOneRecord(@Param(Constants.WRAPPER) QueryWrapper wrapper);
}
