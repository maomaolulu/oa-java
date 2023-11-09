package com.ruoyi.activiti.mapper;

import com.ruoyi.activiti.domain.BizAssociateGood;
import com.ruoyi.activiti.domain.purchase.BizGoodsInfo;
import com.ruoyi.common.core.dao.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 采购物品信息Mapper接口
 *
 * @author zx
 * @date 2021-11-16
 */
@Repository
public interface BizGoodsInfoMapper extends BaseMapper<BizGoodsInfo>
{
    /**
     * 查询采购物品信息
     *
     * @param id 采购物品信息ID
     * @return 采购物品信息
     */
    public BizGoodsInfo selectBizGoodsInfoById(Long id);

    /**
     * 查询采购物品信息列表
     *
     * @param bizGoodsInfo 采购物品信息
     * @return 采购物品信息集合
     */
    public List<BizGoodsInfo> selectBizGoodsInfoList(BizGoodsInfo bizGoodsInfo);

    /**
     * 新增采购物品信息
     *
     * @param bizGoodsInfo 采购物品信息
     * @return 结果
     */
    public int insertBizGoodsInfo(BizGoodsInfo bizGoodsInfo);

    /**
     * 修改采购物品信息
     *
     * @param bizGoodsInfo 采购物品信息
     * @return 结果
     */
    public int updateBizGoodsInfo(BizGoodsInfo bizGoodsInfo);

    /**
     * 删除采购物品信息
     *
     * @param id 采购物品信息ID
     * @return 结果
     */
    public int deleteBizGoodsInfoById(Long id);

    /**
     * 批量删除采购物品信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizGoodsInfoByIds(String[] ids);

    /**
     * 查询物品类型
     * @param id
     * @return 资产类型名称
     */
    @Select("select asset_name from aa_asset_type where id = #{id}")
    String getItemTypeName(@Param("id") String id);

    /**
     * 查询本公司库管
     * @param company
     * @return
     */
    @Select("SELECT u.user_name,u.email,r.role_name,d.ancestors,u.cid FROM sys_user u  " +
            "LEFT JOIN sys_user_role ur on u.user_id = ur.user_id " +
            "LEFT JOIN sys_role r on r.role_id = ur.role_id " +
            "LEFT JOIN sys_dept d on d.dept_id = u.dept_id " +
            "WHERE r.role_name like '%库管%' and d.ancestors like #{company} ")
    List<Map<String,Object>> getLibraryTube(@Param("company") String company);

    /**
     * 查询最新采购状态
     * @param id 物品id
     * @return
     */
    @Select("select link from biz_goods_record where goods_id = #{id} order by create_time desc limit 1 ")
    Optional<String> getLink(@Param("id")Long id);

    /**
     * 获取关联采购信息
     * @param id
     * @return
     */
    @Select("select * from biz_associate_good a where a.associate_id = #{id}")
    List<BizAssociateGood> getAssociatedInfo(@Param("id")Long id);
}
