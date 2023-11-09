package com.ruoyi.daily.mapper.category;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.daily.domain.vw.ViewCategory;
import com.ruoyi.daily.domain.vw.ViewCategoryIdAndName;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author 郝佳星
 * Date 2022/6/7 17:25
 **/
@Repository
public interface ViewCategoryListMapper extends BaseMapper<ViewCategory> {


    /**
     * 获取品类列表
     * @param wrapper
     * @return 品类列表
     */
    @Select("SELECT " +
            " DISTINCT " +
            "  c.id, " +
            "  (select count(1) from aa_asset  where  state=2   and category_id =c.id )  as categoryNum  ," +
            "  (select count(1)  from aa_asset  where state<>6 and category_id =c.id) as categorySum  ," +
            " d.dept_name as deptName, " +
            "c.category_name as  categoryName," +
            "c.company_id as  companyId," +
            " c.unit, " +
            " c.update_by as updateBy, " +
            " c.create_by as createBy, " +
            " c.update_time as updateTime, " +
            " c.create_time as createTime" +
            " FROM " +
            " aa_category c " +
            " LEFT JOIN sys_dept d ON c.company_id = d.dept_id " +
            " LEFT JOIN aa_asset a ON a.category_id = c.id " +
            " ${ew.customSqlSegment} ")
    List<ViewCategory> getCategoryList(@Param(Constants.WRAPPER) Wrapper wrapper);


    /**
     *   品类列表id和名称查询
     */
    @Select("SELECT " +
            "  c.id, " +
            " c.category_name" +
            " FROM " +
            " aa_category c  where c.company_id=#{companyId}"
            )
    List<ViewCategoryIdAndName> getCategoryIdAndNameList(@Param("companyId") Long companyId);



    /**
     * 离职物品移交
     * @param id
     * @param newKepper
     * @param operator
     */
    @Update("update aa_asset set keeper = #{newKeeper},update_by = #{operator},update_time = now() where id = #{id} ")
    void transferGoods(@Param("id") Long id, @Param("newKeeper") String newKepper,@Param("operator") String operator);
}
