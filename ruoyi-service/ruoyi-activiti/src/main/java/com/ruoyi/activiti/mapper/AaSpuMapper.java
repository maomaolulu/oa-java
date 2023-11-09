package com.ruoyi.activiti.mapper;

import com.ruoyi.activiti.domain.asset.AaSpu;
import com.ruoyi.common.core.dao.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zx
 * @date 2021/12/3 11:43
 */
@Repository
public interface AaSpuMapper extends BaseMapper<AaSpu> {
    @Select(" select * from aa_spu where name=#{name } and company_id=#{companyId } ")
    List<AaSpu> listByName(@Param("name") String name,@Param("companyId") Long companyId);
}
