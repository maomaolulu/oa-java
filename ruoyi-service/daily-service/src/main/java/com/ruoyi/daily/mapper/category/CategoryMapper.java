package com.ruoyi.daily.mapper.category;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.daily.domain.asset.category.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zx
 * Date 2022/6/8 9:12
 **/
@Repository
public interface CategoryMapper extends BaseMapper<Category> {
    /**
     * 查询品类列表
     * @param wrapper
     * @return
     */
    @Select("select c.* ,d.dept_name  from aa_category c left join sys_dept d on d.dept_id = c.company_id ${ew.customSqlSegment} ")
    List<Category> getCategoryList(@Param(Constants.WRAPPER) Wrapper wrapper);
}
