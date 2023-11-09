package com.ruoyi.daily.mapper.asset;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.daily.domain.asset.AaSpu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 品类管理
 * @author zx
 * @date 2022-08-17 15:21:16
 */
@Repository
public interface AaSpuMapper extends BaseMapper<AaSpu> {
    @Select("select a.id, a.company_id, a.spu_sn, a.spu_type, a.name, a.model, a.hazard_type, " +
            "a.storage_cond, a.is_inspected, a.price,price as average_price, a.unit, a.period, a.short_limit, a.notes, a.create_by, " +
            "a.create_time, a.update_by, a.update_time, a.storage_num,IF(a.is_inspected = 1, '是', '否') as is_inspected_name from aa_spu a " +
            "left join sys_dept d on d.dept_id = a.company_id " +
            " ${ew.customSqlSegment} ")
    List<AaSpu> selectSpuList(@Param(Constants.WRAPPER) QueryWrapper wrapper);

    /**
     * 获取带确认数量
     * @param spuId
     * @return
     */
    @Select("select sum(amount) as number from biz_claim c where c.status = 0 and c.spu_id = #{spuId}")
    Optional<Integer> getNumber(@Param("spuId") Long spuId);

    /**
     * 获取领用申请未通过数量
     * @param spuId
     * @return
     */
    @Select("select sum(cnum.number)as number from (select  distinct cg.id, cg.claim_num as number,cg.goods_id  from biz_business b " +
            "left join biz_claim_apply ca on ca.id = b.table_id " +
            "left join biz_claim_goods cg on cg.claim_id = ca.id " +
            "where b.proc_def_key = 'claim' and b.status = 1 and b.result = 1 and b.del_flag = false and cg.goods_id = #{spuId}) as cnum ")
    Optional<Integer> getNumberApply(@Param("spuId") Long spuId);

}
