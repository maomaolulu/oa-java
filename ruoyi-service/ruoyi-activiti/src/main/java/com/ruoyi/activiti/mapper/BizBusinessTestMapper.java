package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.activiti.domain.BizAssociateApply;
import com.ruoyi.activiti.domain.BizAssociateGood;
import com.ruoyi.activiti.domain.fiance.BizPaymentApply;
import com.ruoyi.activiti.domain.fiance.BizPaymentDetail;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.purchase.BizGoodsInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * @author wuYang
 * @date 2022/9/16 10:21
 */
@Repository
public interface BizBusinessTestMapper extends BaseMapper<BizBusiness> {
    @Select("select * from biz_business where dept_id is null")
    List<BizBusiness> getNull();

    @Select("select dept_id from sys_user where user_id = #{user_id}")
    Long getUser(@Param("user_id") Long userId);

    @Select("SELECT name from temp")
    List<String> all();

    //    @Select("SELECT dept_id FROM sys_dept where parent_id = 0 AND dept_id != 115")
//    List<Integer> getAllCompanyId();
    @Select("SELECT dept_id FROM sys_dept where parent_id = 0 and  dept_id != 115")
    List<Integer> getAllCompanyId();

    @Select("SELECT DISTINCT KEY_ as key1,NAME_ as name1 FROM ACT_RE_PROCDEF  WHERE KEY_ LIKE 'payment-%'")
    List<HashMap> getAllProdDef();
    @Select("SELECT config_id,config_key as key1 ,config_value as value1 FROM sys_config WHERE config_key = #{value}")
   HashMap getSysConfig(@Param("value")String value);
    @Update("UPDATE sys_config set config_value = #{value},config_name = #{name} WHERE config_id = #{id}")
    void  update(@Param("value")String value,@Param("id")Integer id,@Param("name")String name);
    @Select("SELECT dept_name from  sys_dept  WHERE dept_id = #{id}")
    String getName(@Param("id")Integer id);
    @Select("SELECT * from biz_business WHERE proc_def_key = 'payment'")
    List<BizBusiness> paymentBiz();
    @Select("SELECT COUNT(*)as num FROM sys_user_role WHERE role_id = #{roleId} AND user_id = #{userId}")
    Integer getRoleNum(@Param("roleId") Integer roleId, @Param("userId")Long userId);
    @Select("SELECT * from biz_business WHERE id = #{id}")
    BizBusiness getBusiness(@Param("id") Long id);
    @Select("SELECT dept_id FROM sys_dept WHERE dept_name = #{name}")
    List<Long> getDeptNum(@Param("name")String name);
    @Select("SELECT * from biz_goods_info WHERE id = #{id}")
    BizGoodsInfo getGood(@Param("id") Long id);

    @Select(" SELECT * FROM biz_associate_good WHERE purchase_key = #{appId}")
    List<BizAssociateGood> good(@Param("appId") Long id);

    @Select(" SELECT * FROM biz_associate_apply WHERE associate_apply = #{appId}")
    List<BizAssociateApply> approve(@Param("appId") Long id);

    @Select(" SELECT * FROM biz_payment_detail WHERE parent_id = #{appId}")
    List<BizPaymentDetail> detail(@Param("appId") Long id);

    @Select(" SELECT * FROM biz_payment_apply WHERE id = #{appId}")
    BizPaymentApply payment(@Param("appId") Long id);

    @Select(" SELECT result FROM biz_business WHERE table_id = #{table_id} and proc_def_key =#{proc_def_key} ")
    HashMap paymentResult(@Param("table_id") String table_id, @Param("proc_def_key") String proc_def_key);

    /**
     * 获取所有parent = id
     *
     * @param id
     * @return
     */
    @Select(" SELECT dept_id,dept_name,parent_id FROM sys_dept WHERE parent_id = #{id}")
    List<HashMap<String, String>> searchCompany(@Param("appId") Long id);

    @Select(" SELECT dept_id,dept_name,parent_id FROM sys_dept WHERE parent_id = #{id}")
    List<Integer> onlyCompany(@Param("id") Long id);

    @Select(" SELECT dept_id,dept_name,parent_id FROM sys_dept WHERE dept_id = #{id}")
    HashMap getParent(@Param("id") Integer id);

    /**
     * 获取所有parent = id
     *
     * @param
     * @return
     */
    @Select(" SELECT dept_id,dept_name,parent_id FROM sys_dept ")
    List<HashMap<String, Object>> searchCompanyAll();

    @Select(" SELECT result FROM biz_business WHERE proc_inst_id =#{proc_inst_id}")
    HashMap approveResult(@Param("proc_inst_id") Long proc_inst_id);

    @Select(" SELECT * FROM biz_business WHERE table_id = #{tableId} ")
    HashMap deletePayment(@Param("tableId") String tableId);

//    @Select(" SELECT * FROM biz_business WHERE table_id = #{tableId} ")
//    HashMap deletePayment(@Param("tableId") String tableId);
    @Select("SELECT user_id FROM sys_user_auth  a LEFT JOIN sys_role r on a.role_id =r.role_id WHERE r.role_name ='付款确认人1' and dept_id = #{dept_id}")
    List<Integer> getUserRole(@Param("dept_id") Integer dept_id);

    @Select("SELECT user_name FROM sys_user WHERE user_id = #{id}")
    String getUserName(@Param("id") Integer id);
    @Update("UPDATE biz_business set auditors = #{name}  where table_id = #{tableId}")
    void updateAuditors(@Param("name")String name,@Param("tableId") String tableId);
}
