package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.purchase.BizPurchaseApply;
import com.ruoyi.activiti.domain.dto.BizPurchaseListDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 采购申请(新)Mapper接口
 * 
 * @author zh
 * @date 2021-12-10
 */
@Repository
public interface BizPurchaseListMapper extends BaseMapper<BizPurchaseApply>
{
    /**
     * 查询采购申请(新)待入库
     * 
     * @param   wrapper
     * @return 采购申请(新)
     */
    @Select(" select pur.id,pur.purchase_code,pur.dept_id,pur.create_by,pur.create_time,good.id goods_id,good.item_type,good.name,good.model,good.amount, " +
            "good.actual_amount,good.warehousing_amount,good.unit,good.price,good.purchase_price,good.payment_method,good.supplier,good.purchaser,good.purchase_time,good.is_acceptance,good.single_price, " +
            "good.expect_date,good.remark,d.dept_name,good.is_purchase,good.is_received,good.is_storage,bu.status bustatus,bu.result, " +
            " pur.title,good.arrival_date,good.usages,d2.dept_name usagesName,u.user_name createByName,u2.user_name purchaserName,good.good_type,good.spu_id,good.old, " +
            " good.apply_name,good.apply_model,good.apply_unit,good.apply_remark,bu.proc_inst_id, " +
            "good.is_invalid,good.average_quote,good.payer,good.pay_time FROM  biz_business bu  " +
            "left join biz_purchase_apply  pur  on pur.id=bu.table_id " +
            "left join biz_goods_info  good on pur.id=good.purchase_id " +
            "left join sys_dept d on d.dept_id = pur.dept_id " +
            "left join sys_dept d2 on d2.dept_id = good.usages " +
            "left join sys_user u on u.login_name = pur.create_by " +
            "left join sys_user u2 on u2.login_name = good.purchaser " +
            " ${ew.customSqlSegment}  ")
     List<BizPurchaseListDto> selectBizPurchaseListAll(@Param(Constants.WRAPPER) QueryWrapper wrapper);
    /**
     * 查询采购申请(新)待移交
     *
     * @param   wrapper
     * @return 采购申请(新)
     */
    @Select(" select pur.id,pur.purchase_code,pur.dept_id,pur.create_by,pur.create_time,good.id goods_id,good.item_type,good.name,good.model,good.amount, " +
            "good.actual_amount,good.warehousing_amount,good.unit,good.price,good.purchase_price,good.payment_method,good.supplier,good.purchaser,good.purchase_time,good.is_acceptance,good.single_price, " +
            "good.expect_date,good.remark,d.dept_name,good.is_purchase,good.is_received,good.is_storage,bu.status bustatus,bu.result, " +
            " pur.title,good.arrival_date,good.usages,d2.dept_name usagesName,u.user_name createByName,u2.user_name purchaserName,good.good_type,good.spu_id,good.old, " +
            " good.apply_name,good.apply_model,good.apply_unit,good.apply_remark,bu.proc_inst_id,aa.operator, " +
            "aa.create_time as storage_time,good.is_invalid,good.average_quote,aa.id as asset_id,aa.asset_sn FROM  biz_business bu  " +
            "left join biz_purchase_apply  pur  on pur.id=bu.table_id " +
            "left join biz_goods_info  good on pur.id=good.purchase_id " +
            "left join sys_dept d on d.dept_id = pur.dept_id " +
            "left join sys_dept d2 on d2.dept_id = good.usages " +
            "left join sys_user u on u.login_name = pur.create_by " +
            "left join sys_user u2 on u2.login_name = good.purchaser " +
            "left join aa_asset aa on aa.order_id = good.id " +
            " ${ew.customSqlSegment}  ")
    List<BizPurchaseListDto> selectBizPurchaseListAllTransfer(@Param(Constants.WRAPPER) QueryWrapper wrapper);
    /**
     * 固定资产采购查询页面
     *
     * @param   wrapper
     * @return 固定资产采购查询页面
     */
    @Select(" select DISTINCT bu.id,bu.proc_inst_id,pur.create_time,u.user_name createByName,bu.title,bu.status,bu.result,pur.purchase_code FROM  biz_business bu  " +
            "            left join biz_purchase_apply  pur  on pur.id=bu.table_id " +
            "            left join biz_goods_info  good on pur.id=good.purchase_id  " +
            "            left join sys_dept d on d.dept_id = pur.dept_id  " +
            "            left join sys_dept d2 on d2.dept_id = good.usages " +
            "            left join sys_user u on u.login_name = pur.create_by  " +
            "            left join sys_user u2 on u2.login_name = good.purchaser  " +
            "            ${ew.customSqlSegment}  ")
     List<BizPurchaseListDto> selectGoodsListFixed(@Param(Constants.WRAPPER) QueryWrapper wrapper);
    /**
     * 查询采购申请信息 用于退货
     *
     * @param  goodId
     * @return 查询采购申请信息 用于退货
     */
    @Select(" select pur.* from biz_purchase_apply pur " +
            " left join biz_goods_info  good on pur.id=good.purchase_id  " +
            " where good.id=#{ goodId }  limit 1 ")
     BizPurchaseApply selectByGoodsId(Long goodId);

    /**
     * 资产采购数据导出
     * @return
     */
    @Select("select bb.apply_code,bb.dept_name ,case when bb.status = 1 then '审批中' else '通过' end, " +
            "bb.apply_time,sd.dept_name as belong_dept_name,bb.applyer,pa.remark as apply_remark,sdd.dict_label,g.name,g.model, " +
            "       g.unit,g.amount,g.remark,g.average_quote as '供应商报价均价' " +
            "from biz_business bb " +
            "left join biz_purchase_apply pa on pa.id = bb.table_id " +
            "left join biz_goods_info g on g.purchase_id = pa.id " +
            "left join sys_dept sd on g.ascription_dept = sd.dept_id " +
            "left join sys_dict_data sdd on sdd.dict_code = g.item_type " +
            " " +
            " " +
            "where bb.proc_def_key = 'purchase' and (bb.status = 1 and  bb.result = 1 or bb.status = 2 and bb.result = 2) and bb.company_id = 115 " +
            "  and bb.apply_time between '2022-10-01 00:00:00' and '2022-12-31 23:59:59' and sdd.dict_type = 'assets_type'")
    List<Map<String, Object>> exportAssetPurchaseExcel();
}