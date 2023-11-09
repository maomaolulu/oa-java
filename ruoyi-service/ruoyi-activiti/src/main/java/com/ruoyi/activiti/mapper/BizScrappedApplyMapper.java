package com.ruoyi.activiti.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.activiti.domain.asset.BizScrappedApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description:
 * @author: zx
 * @date: 2021/11/21 19:08
 */
@Repository
@Mapper
public interface BizScrappedApplyMapper extends BaseMapper<BizScrappedApply> {

    /**
     * 查询报废出库
     *
     * @param id 报废出库ID
     * @return 报废出库
     */
    public BizScrappedApply selectBizScrappedApplyById(Long id);

    /**
     * 获取物品类型名称
     * @param assetType
     * @return
     */
    @Select("SELECT d.dict_label FROM sys_dict_data d WHERE d.dict_code = #{assetType} ")
    String getAssetType(@Param("assetType")String assetType);

    /**
     * 获取物品状态名称
     * @param state
     * @return
     */
    @Select("SELECT d.dict_label FROM sys_dict_data d WHERE d.dict_value = #{state} and d.dict_type = 'goodStatus' ")
    String getState(@Param("state")Integer state);

    /**
     * 查询报废出库列表
     *
     * @param bizScrappedApply 报废出库
     * @return 报废出库集合
     */
    public List<BizScrappedApply> selectBizScrappedApplyList(BizScrappedApply bizScrappedApply);

    /**
     * 新增报废出库
     *
     * @param bizScrappedApply 报废出库
     * @return 结果
     */
    public int insertBizScrappedApply(BizScrappedApply bizScrappedApply);

    /**
     * 修改报废出库
     *
     * @param bizScrappedApply 报废出库
     * @return 结果
     */
    public int updateBizScrappedApply(BizScrappedApply bizScrappedApply);

    /**
     * 删除报废出库
     *
     * @param id 报废出库ID
     * @return 结果
     */
    public int deleteBizScrappedApplyById(Long id);

    /**
     * 批量删除报废出库
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizScrappedApplyByIds(String[] ids);
}
