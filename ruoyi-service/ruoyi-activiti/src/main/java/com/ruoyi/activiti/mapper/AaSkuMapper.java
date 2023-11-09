package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.activiti.domain.asset.AaSku;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author zx
 * @date 2021/12/1 11:03
 */
@Repository
public interface AaSkuMapper extends BaseMapper<AaSku> {
    /**
     * 查询原有库存
     * @param spuId
     * @return
     */
    @Select("SELECT case when (SELECT SUM(a.amount) FROM aa_sku a WHERE operation = 1 and spu_id =  #{spuId}) is null then 0 else (SELECT SUM(a.amount) FROM aa_sku a WHERE operation = 1 and spu_id =  #{spuId}) end - case when (SELECT SUM(a.amount) FROM aa_sku a WHERE operation = 2 and spu_id =  #{spuId} ) is null then 0 else (SELECT SUM(a.amount) FROM aa_sku a WHERE operation = 2 and spu_id =  #{spuId} ) end as spu_amount FROM dual")
    Integer countSkuAmount(Long spuId);

}
