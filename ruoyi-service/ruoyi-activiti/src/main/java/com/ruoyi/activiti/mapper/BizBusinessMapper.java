package com.ruoyi.activiti.mapper;

import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.common.core.dao.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>File：ActBusinessMapper.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2020 2020年1月6日 下午3:38:12</p>
 * <p>Company: zmrit.com </p>
 * @author zmr
 * @version 1.0
 */
@Repository
public interface BizBusinessMapper extends BaseMapper<BizBusiness>
{


    /**
     * 保存抄送临时记录
     * @param ccId
     * @param procInstId
     * @param createBy
     * @return
     */
    @Select("insert into biz_act_cc (cc_id,proc_inst_id,del_flag,create_by,create_time,is_read) values (#{ccId},#{procInstId},'2',#{createBy},sysdate(),'0') ")
    void insertCC(@Param("ccId")String ccId,@Param("procInstId")String procInstId,@Param("createBy")String createBy);

    @Select("select count(0) from biz_act_cc where cc_id = #{ccId} and proc_inst_id = #{procInstId} ")
    int checkCC(@Param("ccId")String ccId,@Param("procInstId")String procInstId);

    @Select("SELECT ba.`comment` FROM `ACT_HI_TASKINST` aht  " +
            "LEFT JOIN biz_audit ba on aht.ID_ = ba.task_id " +
            "WHERE aht.PROC_INST_ID_ = #{procInstId} and ba.result is not null " +
            "ORDER BY aht.ID_ desc limit 1")
    String getComment(@Param("procInstId")String procInstId);

    /**
     * 临时抄送转正式
     * @param procInstId
     * @return
     */
    @Update("update biz_act_cc set del_flag = '0' WHERE proc_inst_id = #{procInstId} ")
    int changeCC(@Param("procInstId") String procInstId);

    /**
     * 删除临时抄送
     * @param ccId
     * @param procInstId
     * @return
     */
    @Delete("delete from biz_act_cc where cc_id=#{ccId} and proc_inst_id = #{procInstId} and del_flag = '2' ")
    int deleteCC(@Param("ccId") String ccId,@Param("procInstId")String procInstId);

    /**
     * 获取抄送人
     * @param procInstId 流程实例ID
     * @return
     */
    @Select("select * from biz_act_cc where  proc_inst_id = #{procInstId} and del_flag = '0' ")
    List<Map<String,Object>> getCC(@Param("procInstId")String procInstId);

    /**
     * 查询未读抄送信息数量
     * @param userId
     */
    @Select("select count(0) from biz_act_cc where is_read = '0' and del_flag = '0' and cc_id = #{userId} ")
    int getUnReadCount(@Param("userId")String userId);

    /**
     * 抄送已读
     * @param processInstanceId 流程实例ID
     * @param userId        用户ID
     */
    @Update("update biz_act_cc set is_read = '1' WHERE proc_inst_id = #{procInstId} and cc_id = #{userId} ")
    int readCc(@Param("procInstId")String processInstanceId,@Param("userId")String userId);

    /**
     * 根据物品名称查询流程唯一id
     * @param goodsName
     * @return
     */
    @Select("select distinct b.proc_inst_id from biz_goods_info g " +
            "left join biz_business b on b.table_id = g.purchase_id " +
            "where b.proc_def_key = 'purchase' and g.name like  '%' #{goodsName} '%' ")
    List<String> selectInstIdByGoodName(@Param("goodsName")String goodsName);

    @Select("select bb.* from biz_business bb " +
            "left join biz_scrapped_apply sa on sa.id = bb.table_id " +
            "where bb.status =2 and bb.del_flag = false and bb.result = 2 and bb.proc_def_key = 'scrapped' and sa.assert_id = #{id} ")
    List<BizBusiness> selectBizBusinessByAssetId(@Param("id") Long id);

    @Select("select bb.* from biz_business bb " +
            "left join biz_goods_info g on g.purchase_id = bb.table_id " +
            "where bb.status =2 and bb.del_flag = false and bb.result = 2 and bb.proc_def_key = 'purchase' and g.id = #{id} ")
    List<BizBusiness> selectBizBusinessByGoodId(@Param("id") Long id);

    /**
     * 报废申请列表
     * @param applyCode
     * @param assetSn
     * @param goodsName
     * @return
     */
    @Select({"<script> select aa.state as status,b.*,aa.asset_sn,aa.name as goods_name from biz_business b " +
            "left join biz_scrapped_apply sa on sa.id = b.table_id " +
            "left join aa_asset aa on sa.assert_id = aa.id " +
            "where b.proc_def_key = 'scrapped' and b.del_flag = false and b.user_id = #{userId} " +
            "<when test='applyCode!=null and applyCode!=\"\" '>" +
            "and sa.apply_code like concat('%',#{applyCode},'%') ",
            "</when>",
            "<when test='assetSn!=null and assetSn!=\"\" '>" +
                    "and aa.asset_sn like concat('%',#{assetSn},'%') ",
            "</when>",
            "<when test='goodsName!=null and goodsName!=\"\" '>" +
                    "and aa.name like concat('%',#{goodsName},'%') ",
            "</when>",
            "</script>"
            })
    List<BizBusiness> selectScrappedList(@Param("applyCode") String applyCode,@Param("assetSn") String assetSn,@Param("goodsName") String goodsName,@Param("userId")Long userId);

    /**
     * 将上传的临时文件转为有效文件
     */
    @Update("update sys_attachment set parent_id = #{parentId} , del_flag = 0  where temp_id ={tempId}")
    void changeFileState(@Param("parentId") String parentId,@Param("tempId") String tempId);

    /**
     * 查询该权限下的所有id
     * @param name
     * @return
     */
    @Select("SELECT DISTINCT " +
            " a.user_id  " +
            "FROM " +
            " sys_role r " +
            " LEFT JOIN sys_user_auth a ON r.role_id = a.role_id " +
            "WHERE " +
            " a.dept_id = #{deptId}  " +
            " AND r.role_name = #{name} ")
    List<Long>  getUserIdByName(@Param("deptId")Long deptId,@Param("name")String name);
}
