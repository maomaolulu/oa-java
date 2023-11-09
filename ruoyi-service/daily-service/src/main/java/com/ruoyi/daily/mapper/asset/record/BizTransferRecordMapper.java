package com.ruoyi.daily.mapper.asset.record;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.daily.domain.asset.record.BizTransferRecord;
import com.ruoyi.daily.domain.asset.record.vo.BizTransferRecordVO;
import com.ruoyi.daily.domain.asset.transfer.PurchaseTransferVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by WuYang on 2022/8/22 17:35
 */
@Repository
public interface BizTransferRecordMapper extends BaseMapper<BizTransferRecord> {
        @Select("SELECT " +
                "tr.id,  " +
                "tr.purchase_code, " +
                "tr.applyer, " +
                "tr.create_time, " +
                "tr.handler, " +
                "tr.keeper, " +
                "tr.original_state,"+
                "tr.current_state,"+
                "tr.type,"+
                "tr.old_keeper,"+
                "tr.old_dept,"+
                "company.dept_name as company_name, " +
                "d.dept_name, " +
                "dd.dict_label, " +
                "a.name, " +
                "a.model, " +
                "u.user_name as purchaser, " +
                "good.purchase_time," +
                "a.asset_sn," +
                "tr.is_checked ," +
                "ac.category_name " +
                "FROM biz_transfer_record tr  " +
                "LEFT JOIN biz_goods_info good on good.id = tr.goods_id " +
                "LEFT JOIN sys_dept company on company.dept_id = tr.company_id " +
                "LEFT JOIN sys_dept d on d.dept_id = tr.dept_id " +
                "LEFT JOIN aa_asset a on a.id = tr.asset_id " +
                "LEFT JOIN aa_category ac on a.category_id = ac.id " +
                "LEFT JOIN sys_user u on u.login_name = good.purchaser " +
                "LEFT JOIN sys_dict_data dd on dd.dict_code = good.item_type ${ew.customSqlSegment}"

        )
        List<BizTransferRecordVO> transfer(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

        @Select("SELECT " +
                "  pa.purchase_code, " +
                "  a.asset_sn, " +
                "  a.name, " +
                "  a.model, " +
                "  a.keeper, " +
                "  d.dept_name, " +
                "  g.purchase_date,  " +
                "  br.id," +
                " br.handler, "+
                " br.create_time, "+
                " u.user_name as create_by "+
                "FROM " +
                "  biz_transfer_record br " +
                "  LEFT JOIN biz_goods_info g ON br.goods_id = g.id " +
                "  LEFT JOIN biz_purchase_apply pa ON g.purchase_id = pa.id " +
                "  LEFT JOIN sys_user u ON u.login_name = pa.create_by " +
                "  LEFT JOIN aa_asset a on a.id = br.asset_id " +
                "  LEFT JOIN sys_dept d on d.dept_id = a.dept_id ${ew.customSqlSegment}")
        List<PurchaseTransferVO> getUserUnCheckedList(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);
        @Update("UPDATE biz_goods_info SET is_transfer = #{is_transfer} WHERE id = #{id}")
        void updateGoodInfoById(@Param("id")Long id ,@Param("is_transfer")Integer state);
}
