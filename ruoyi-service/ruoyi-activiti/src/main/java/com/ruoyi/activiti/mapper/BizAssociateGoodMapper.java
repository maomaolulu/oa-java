package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.activiti.domain.BizAssociateGood;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * 采购物品关联付款报销
 * @author zx
 * @date 2022-06-28 11:55:41
 */
@Repository
public interface BizAssociateGoodMapper extends BaseMapper<BizAssociateGood> {
    /**
     * 流程撤销回滚财务状态
     * @param types 付款1报销2
     * @param tableId 付款报销id
     */
    @Update("update biz_goods_info good left join biz_associate_good ag on good.id = ag.good_id " +
            "set good.finance_status  = 0 where ag.types = #{types} and ag.apply_id = #{tableId}")
    void changeGoodFinanceStatus(@Param("types")int types,@Param("tableId") String tableId);
}
