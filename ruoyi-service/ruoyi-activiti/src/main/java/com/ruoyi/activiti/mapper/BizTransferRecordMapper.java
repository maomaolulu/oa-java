package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.activiti.domain.purchase.BizTransferRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 采购记录 数据层
 *
 * @author zx
 * @date 2022/4/22 14:39
 */
@Repository
public interface BizTransferRecordMapper extends BaseMapper<BizTransferRecord> {
    /**
     * 更新aa_asset表信息
     *
     * @param goodsId 商品id
     * @param deptId  部门id
     * @param charger 负责人
     * @param keeper  保管人
     */
    @Update("update aa_asset set dept_id = #{deptId},charger = #{charger},keeper = #{keeper},is_transfer = 1 where id = #{goodsId}")
    void updateAssetInfo(@Param("goodsId") Long goodsId, @Param("deptId") Long deptId, @Param("charger") String charger, @Param("keeper") String keeper);

    /**
     * 设为库存
     * @param goodsId
     */
    @Update("update aa_asset set keeper = null , charger = null  where id = #{goodsId}")
    void setStorage(@Param("goodsId") Long goodsId);

    /**
     * 查询移交记录
     *
     * @param queryWrapper
     * @return
     */
    @Select("SELECT " +
            "tr.id,  " +
            "tr.purchase_code, " +
            "tr.applyer, " +
            "tr.create_time, " +
            "tr.handler, " +
            "tr.keeper, " +
            "tr.create_by, " +
            "company.dept_name as company_name, " +
            "d.dept_name, " +
            "dd.dict_label, " +
            "good.`name`, " +
            "good.model, " +
            "u.user_name as purchaser, " +
            "good.purchase_time," +
            "a.asset_sn," +
            "tr.is_checked " +
            "FROM biz_transfer_record tr  " +
            "LEFT JOIN biz_goods_info good on good.id = tr.goods_id " +
            "LEFT JOIN sys_dept company on company.dept_id = tr.company_id " +
            "LEFT JOIN sys_dept d on d.dept_id = tr.dept_id " +
            "LEFT JOIN aa_asset a on a.id = tr.asset_id " +
            "LEFT JOIN sys_user u on u.login_name = good.purchaser " +
            "LEFT JOIN sys_dict_data dd on dd.dict_code = good.item_type ${ew.customSqlSegment} ")
    List<BizTransferRecord> getTransferRecord(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);
}
