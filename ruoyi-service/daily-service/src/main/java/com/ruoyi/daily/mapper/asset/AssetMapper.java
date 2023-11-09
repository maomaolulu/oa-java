package com.ruoyi.daily.mapper.asset;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.daily.domain.asset.Asset;
import com.ruoyi.daily.domain.asset.dto.AaSkuTestDto;
import com.ruoyi.daily.domain.asset.dto.DutyAssetDto;
import com.ruoyi.daily.domain.vw.ViewAsset;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 固定资产
 * @author zx
 * @date 2022/3/15 18:19
 */
@Repository
public interface AssetMapper extends BaseMapper<Asset> {
    /**
     * 固定资产列表
     * @param wrapper
     * @return
     */
    @Select("SELECT a.*,c.dept_name as company_name,ac.category_name,dd.dict_label as asset_type_name,dd2.dict_label as state_name from (" +
            "SELECT a.*," +
            "case when a.pic_path is null or a.pic_path = '' then 0 else 1 end as has_pic " +
            "FROM aa_asset a) a " +
            "left join sys_dept d on d.dept_id = a.dept_id " +
            "left join sys_dept c on c.dept_id = a.company_id " +
            "left join aa_category ac on ac.id = a.category_id " +
            "left join sys_dict_data dd on dd.dict_code = a.asset_type and dd.dict_type = 'assets_type' " +
            "left join sys_dict_data dd2 on dd2.dict_sort = a.state and dd2.dict_type = 'goodStatus' " +
            "${ew.customSqlSegment} ")
    @Override
    List<Map<String, Object>> selectMaps(@Param(Constants.WRAPPER) Wrapper wrapper);

    @Select("select t1.asset_name as asset_type_name ,t2.category_name from aa_asset_type t1 " +
            "left join aa_category_fixed t2 on t1.id = t2.asset_type_id " +
            "${ew.customSqlSegment} ")
    Map<String, String> selectMap(@Param(Constants.WRAPPER) Wrapper wrapper);

    @Select("SELECT * from aa_asset WHERE id = #{id}")
    Asset selectOneById(@Param("id") Long id);

    /**
     * 获取资产信息
     * @param wrapper
     * @return
     */
    @Select("SELECT " +
            "  a.id, " +
            " CONCAT(c.dept_name,'-',d.dept_name) as deptName, " +
            " sd.dict_label as assetTypeName, " +
            " a.asset_sn as assetSn, " +
            " a.asset_type as assetType, " +
            " a.name , " +
            " a.model," +
            " a.state " +
            "FROM " +
            " aa_asset a " +
            " LEFT JOIN sys_dept c ON a.company_id = c.dept_id " +
            " LEFT JOIN sys_dept d ON a.dept_id = d.dept_id " +
            " LEFT JOIN sys_dict_data sd on sd.dict_code = a.asset_type " +
            "  ${ew.customSqlSegment}"
    )
    List<ViewAsset> getAsset(@Param(Constants.WRAPPER) Wrapper wrapper);



    /**
     *   获取物品信息
     * @param wrapper
     */
    @Select("SELECT " +
            "  a.id, " +
            " CONCAT(c.dept_name,'-',d.dept_name) as belongDept, " +
            " sd.dict_label as assetTypeName, " +
            " a.asset_sn as assetSn, " +
            " sd2.dict_label as goodStatus, " +
            " a.name , " +
            " a.model, " +
            " a.unit, " +
            " a.charger, " +
            " a.keeper, " +
            " a.purchase_price as singlePrice," +
            " a.purchase_time " +
            "  " +
            "FROM " +
            " aa_asset a " +
            " LEFT JOIN sys_dept c ON a.company_id = c.dept_id " +
            " LEFT JOIN sys_dept d ON a.dept_id = d.dept_id " +
            " LEFT JOIN sys_dict_data sd on sd.dict_code = a.asset_type " +
            " LEFT JOIN sys_dict_data sd2 on sd2.dict_value = a.state and sd2.dict_type = 'goodStatus' ${ew.customSqlSegment} ")
    List<DutyAssetDto> getDutyAsset(@Param(Constants.WRAPPER) Wrapper wrapper);

    /**
     * 替换责任人
     * @param ids
     * @param newCharger
     */
    @Update("<script>"+
            "update aa_asset set charger = #{newCharger} where id in " +
             "<foreach item='id' index='index' collection='ids' open='(' separator=',' close=')'>"+
             "#{id}"+
             "</foreach>"+
            "</script>")
    void replaceDuty(@Param("ids") List<Long> ids, @Param("newCharger") String newCharger);
    /**
     * 离职物品移交
     * @param id
     * @param newKepper
     * @param operator
     */
    @Update("update aa_asset set keeper = #{newKeeper},update_by = #{operator},update_time = now() where id = #{id} ")
    void transferGoods(@Param("id") Long id, @Param("newKeeper") String newKepper,@Param("operator") String operator);

    /**
     * 校验id是否存在
     * @param id
     * @return
     */
    @Select("select count(*) from aa_asset where label_code = #{id}")
    int countId(@Param("id") String id);

    /**
     * 校验物品编号是否存在
     * @param assetSn
     * @param companyId
     * @return
     */
    @Select("select count(*) from aa_asset where asset_sn = #{assetSn} and company_id = #{companyId}")
    int countAssetSn(@Param("assetSn") String assetSn,@Param("companyId")Long companyId);

    /**
     * 打印标签
     * @param id
     * @return
     */
    @Update("update aa_asset set is_labelled = 1 where id = #{id} ")
    int print(@Param("id") Long id);


    /**
     * 唯一id
     * @param labelCode
     * @return
     */
    @Update("update aa_asset set company_id = #{companyId} where label_code = #{labelCode} ")
    int updateCompanyId(@Param("labelCode") String labelCode,@Param("companyId")Long companyId);


    @Select("select a.id,b.id as id2 from aa_sku a " +
            "left join aa_transcation b on b.identifier = a.id " +
            "where a.id between 1475705412302282754 and 1572432301881626863 ")
    List<AaSkuTestDto> getAaSkuTestInfo();

    @Update("update aa_sku set id = #{newId} where id = #{id} ")
    int updateId(@Param("id") Long id,@Param("newId")Long newId);

    @Update("update aa_transcation set identifier = #{newIdentifier} where identifier = #{identifier} ")
    int updateIdentifier(@Param("identifier") Long identifier,@Param("newIdentifier")Long newIdentifier);

    /**
     * 查询实验室仪器列表
     * @return
     */
    @Select("select a.id,a.name ,a.model ,a.asset_sn,a.state,d.dict_label as state_name,a.precisions,a.inspect_time from aa_device_classify dc " +
            "left join aa_asset a on a.id = dc.asset_id " +
            "left join sys_dict_data d on d.dict_value = a.state ${ew.customSqlSegment} ")
    List<Asset> getDeviceList(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    /**
     * 获取仪器设备分类
     * @param assetId
     * @return
     */
    @Select("select d1.dict_value as device_type,d1.remark as device_type_name,d2.dict_value as dict_key,d2.dict_label as dict_key_name from aa_device_classify a " +
            "left join sys_dict_data d1 on d1.dict_value = a.device_type " +
            "left join sys_dict_data d2 on d2.dict_value = a.dict_key " +
            "where a.asset_id = #{assetId} and d1.dict_type = 'device_type' and d2.dict_type = d1.dict_label ")
    List<Map<String,Object>> getDeviceTypeList(@Param("assetId")Long assetId);

}
