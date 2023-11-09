package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.BizAssociateApply;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 管理审批单
 * @author zx
 * @date 2022/1/6 14:08
 */
@Repository
public interface BizAssociateApplyMapper extends BaseMapper<BizAssociateApply> {

    @Select("SELECT baa.associate_title as associateTitle, " +
            "baa.associate_types as  associateTypes, " +
            "baa.associate_apply as associateApply, " +
            "baa.apply_id as applyId " +
            "FROM biz_associate_apply baa ${ew.customSqlSegment} ")
    List<Map<String,Object>> getAssociate(@Param(Constants.WRAPPER) QueryWrapper wrapper);

    /**
     * 修改采购物品财务状态(关联报销付款)
     * @param id
     * @param status
     * @return
     */
    @Update("update biz_goods_info set finance_status = #{status} where id = #{id} ")
    int updateFinanceStatus(@Param("id")Long id,@Param("status")int status);

    /**
     * 修改采购物品财务状态(已报销)
     * @param types 1付款 2报销
     * @param applyId 报销付款id
     * @param status 状态
     * @return
     */
    @Update("update biz_goods_info good  " +
            "left join biz_associate_good ag on ag.good_id = good.id " +
            "set good.finance_status = #{status} " +
            "where ag.types = #{types} and ag.apply_id = #{applyId}")
    int updateFinanceStatusReimburse(@Param("types")Integer types,@Param("applyId")Long applyId,@Param("status")int status);
    /**
     * 修改采购物品财务状态(已付款)
     * @param types 1付款 2报销
     * @param applyId 报销付款id
     * @param status 状态
     * @param payer 付款人
     * @param payTime 付款时间
     * @return
     */
    @Update("update biz_goods_info good  " +
            "left join biz_associate_good ag on ag.good_id = good.id " +
            "set good.finance_status = #{status},good.payer = #{payer} ,good.pay_time = #{payTime} " +
            "where ag.types = #{types} and ag.apply_id = #{applyId}")
    int updateFinanceStatusPayment(@Param("types")Integer types,@Param("applyId")Long applyId,@Param("status")int status,@Param("payer")String payer,@Param("payTime") Date payTime);
    /**
     * 修改采购物品财务状态(已付款)
     * @param id 1付款 2报销
     * @param status 状态
     * @param payer 付款人
     * @param payTime 付款时间
     * @return
     */
    @Update("update biz_goods_info good " +
            "set good.finance_status = #{status},good.payer = #{payer} ,good.pay_time = #{payTime} " +
            "where id = #{id}")
    int updateFinanceStatusPaymentMongo(@Param("id")Long id,@Param("status")int status,@Param("payer")String payer,@Param("payTime") Date payTime);

}
