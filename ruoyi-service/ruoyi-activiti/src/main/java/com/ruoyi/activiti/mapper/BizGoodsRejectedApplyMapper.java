package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.purchase.BizGoodsRejectedApply;
import com.ruoyi.activiti.domain.dto.CapitalGoodsDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * lx
 * 22/01/14
 */

@Repository
public interface BizGoodsRejectedApplyMapper extends BaseMapper<BizGoodsRejectedApply> {


    /**
     * 获取固定资产退货列表
     * @param wrapper
     * @return
     */
    @Select(" SELECT " +
            "aa.id," +
            "pur.purchase_code," +
            "d.dept_name," +
            "u.user_name AS applicant," +
            "da.dict_label AS goosType," +
            "aa.name," +
            "aa.model," +
            "good.actual_amount AS amount," +
            "aa.dealer," +
            "u2.user_name AS purchaser," +
            "good.purchase_time ," +
            "aa.order_id," +
            "aa.asset_sn," +
            "aa.operator," +
            "aa.unit," +
            "aa.create_time," +
            "d2.dept_name as company_name " +
            " FROM aa_asset aa " +
            " inner JOIN biz_goods_info good ON aa.order_id = good.id " +
            " LEFT JOIN biz_purchase_apply pur ON good.purchase_id = pur.id " +
            " LEFT JOIN sys_dept d ON d.dept_id = aa.dept_id " +
            "  LEFT JOIN sys_dept d2 ON d2.dept_id = aa.company_id  " +
            " LEFT JOIN sys_dict_data da ON da.dict_code = aa.asset_type " +
            " LEFT JOIN sys_user u ON u.login_name = good.create_by " +
            " LEFT JOIN sys_user u2 ON u2.login_name = good.purchaser " +
            " ${ew.customSqlSegment} ")
    List<CapitalGoodsDto> selectAssetList(@Param(Constants.WRAPPER) QueryWrapper wrapper);


    /**
     * 获取流动资产退货列表
     * @param wrapper
     * @return
     */
    @Select(" SELECT " +
            "aas.id," +
            "pur.purchase_code," +
            "d.dept_name," +
            "u.user_name AS applicant," +
            "da.dict_label AS goosType," +
            "ap.name," +
            "ap.model," +
            "good.actual_amount AS amount," +
            "aas.dealer," +
            "u2.user_name AS purchaser," +
            "good.purchase_time ," +
            "aas.order_id," +
            "ap.spu_sn," +
            "aas.operator," +
            "ap.unit," +
            "aas.create_time," +
            "d2.dept_name as company_name" +
            "  FROM aa_sku aas  " +
            "  inner JOIN biz_goods_info good ON aas.order_id = good.id  " +
            "  LEFT JOIN aa_spu ap ON aas.spu_id = ap.id  " +
            "  LEFT JOIN biz_purchase_apply pur ON good.purchase_id = pur.id  " +
            "  LEFT JOIN sys_dept d ON d.dept_id = aas.dept_id  " +
            "  LEFT JOIN sys_dept d2 ON d2.dept_id = ap.company_id  " +
            "  LEFT JOIN sys_dict_data da ON da.dict_code = ap.spu_type  " +
            "  LEFT JOIN sys_user u ON u.login_name = good.create_by  " +
            "  LEFT JOIN sys_user u2 ON u2.login_name = good.purchaser  " +
            " ${ew.customSqlSegment}  ")
    List<CapitalGoodsDto> selectSpuList(@Param(Constants.WRAPPER) QueryWrapper wrapper);


}
