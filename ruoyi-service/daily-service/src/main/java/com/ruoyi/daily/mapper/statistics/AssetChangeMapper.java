package com.ruoyi.daily.mapper.statistics;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.daily.domain.statistics.AssetChangeVO;
import com.ruoyi.daily.domain.statistics.AssetProportionVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 注：后缀 ByValue = 按估值（value）
 *
 * @author wuYang
 * @date 2022/8/30 9:27
 */
@Repository
public interface AssetChangeMapper {
    /**
     * 按照采购价
     */
    @Select(
            "SELECT   " +
                    "   sum( purchase_price ) AS total,   " +
                    "   DATE_FORMAT( purchase_time, '%Y-%m' ) AS month    " +
                    "FROM   " +
                    "   aa_asset   ${ew.customSqlSegment} " +
                    "GROUP BY   " +
                    "   month    " +
                    "ORDER BY   " +
                    "   month")
    List<AssetChangeVO> getAssetChangeList(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    /**
     * 按照估值价格
     */
    @Select(
            "SELECT   " +
                    "   sum( value ) AS total,   " +
                    "   DATE_FORMAT( purchase_time, '%Y-%m' ) AS month    " +
                    "FROM   " +
                    "   aa_asset   ${ew.customSqlSegment} " +
                    "GROUP BY   " +
                    "   month    " +
                    "ORDER BY   " +
                    "   month")
    List<AssetChangeVO> getAssetChangeListByValue(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    /**
     * 按照采购价
     */
    @Select("SELECT " +
            "sum( purchase_price ) AS purchase_price, " +
            "s.dept_name as company_name " +
            "FROM " +
            "aa_asset a " +
            "LEFT JOIN " +
            "sys_dept s " +
            "on  " +
            "a.company_id = s.dept_id   ${ew.customSqlSegment} "
            )
    List<AssetProportionVO> getProportion(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);
    /**
     * 按照采购价
     */
    @Select("SELECT " +
            "sum( purchase_price ) AS purchase_price, " +
            "s.dept_name as company_name " +
            "FROM " +
            "aa_asset a " +
            "LEFT JOIN " +
            "sys_dept s " +
            "on  " +
            "a.dept_id = s.dept_id   ${ew.customSqlSegment} "
    )
    List<AssetProportionVO> getProportion1(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    /**
     * 按照估值价格
     */
    @Select("SELECT " +
            "sum( value ) AS purchase_price, " +
            "s.dept_name as company_name " +
            "FROM " +
            "aa_asset a " +
            "LEFT JOIN " +
            "sys_dept s " +
            "on " +
            "a.company_id = s.dept_id  ${ew.customSqlSegment} "
           )
    List<AssetProportionVO> getProportionByValue(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);
    /**
     * 按照估值价格
     */
    @Select("SELECT " +
            "sum( value ) AS purchase_price, " +
            "s.dept_name as company_name " +
            "FROM " +
            "aa_asset a " +
            "LEFT JOIN " +
            "sys_dept s " +
            "on " +
            "a.dept_id = s.dept_id  ${ew.customSqlSegment} "
            )
    List<AssetProportionVO> getProportionByValue1(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    /**
     * 算出所有的固定资产
     */
    @Select("SELECT sum(purchase_price) FROM aa_asset p ${ew.customSqlSegment}")
    Long getTotalAssetPrice(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    /**
     * 算出所有的固定资产 按估值价
     */
    @Select("SELECT sum(value) FROM aa_asset p ${ew.customSqlSegment}")
    Long getTotalAssetPriceByValue(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);


    /**
     * 算出所有的流动资产
     */
    @Select("SELECT " +
            " SUM(temp.number * p.price) as total " +
            "FROM " +
            " ( " +
            " SELECT " +
            "  ( sum( IF ( operation = 1, amount, 0 ))- sum( IF ( operation = 2, amount, 0 )) ) AS number, " +
            "  spu_id  " +
            " FROM " +
            "  aa_sku  " +
            " GROUP BY " +
            "  spu_id  " +
            " ) temp " +
            " LEFT JOIN aa_sku k ON temp.spu_id = k.spu_id " +
            " LEFT JOIN aa_spu p ON k.spu_id = p.id ${ew.customSqlSegment} ")
    Long getFlowTotal(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    /**
     * 算出所有的流动资产 按估值价
     */
    @Select("SELECT " +
            " SUM(temp.number * k.value) as total " +
            "FROM " +
            " ( " +
            " SELECT " +
            "  ( sum( IF ( operation = 1, amount, 0 ))- sum( IF ( operation = 2, amount, 0 )) ) AS number, " +
            "  spu_id  " +
            " FROM " +
            "  aa_sku  " +
            " GROUP BY " +
            "  spu_id  " +
            " ) temp " +
            " LEFT JOIN aa_sku k ON temp.spu_id = k.spu_id " +
            " LEFT JOIN aa_spu p ON k.spu_id = p.id ${ew.customSqlSegment} ")
    Long getFlowTotalByValue(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    /**
     * 按估值 流动资产变化
     */
    @Select("SELECT  " +
            "  SUM(temp.number * k.`value`) as total,  " +
            "  DATE_FORMAT(k.create_time,'%Y-%m')as month  " +
            "FROM  " +
            "  (  " +
            "  SELECT  " +
            "    ( sum( IF ( operation = 1, amount, 0 ))- sum( IF ( operation = 2, amount, 0 )) ) AS number,  " +
            "    spu_id   " +
            "  FROM  " +
            "    aa_sku   " +
            "  GROUP BY  " +
            "    spu_id   " +
            "  ) temp  " +
            "  LEFT JOIN aa_sku k ON temp.spu_id = k.spu_id  " +
            "  LEFT JOIN aa_spu p ON k.spu_id = p.id  ${ew.customSqlSegment} " +
            "  GROUP BY month   " +
            "  ORDER BY month")
    List<AssetChangeVO> getFlowListMonthByValue(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    /**
     * 按估值 流动资产变化
     */
    @Select("SELECT  " +
            "  SUM(temp.number *  p.price) as total,  " +
            "  DATE_FORMAT(k.create_time,'%Y-%m')as month  " +
            "FROM  " +
            "  (  " +
            "  SELECT  " +
            "    ( sum( IF ( operation = 1, amount, 0 ))- sum( IF ( operation = 2, amount, 0 )) ) AS number,  " +
            "    spu_id   " +
            "  FROM  " +
            "    aa_sku   " +
            "  GROUP BY  " +
            "    spu_id   " +
            "  ) temp  " +
            "  LEFT JOIN aa_sku k ON temp.spu_id = k.spu_id  " +
            "  LEFT JOIN aa_spu p ON k.spu_id = p.id  ${ew.customSqlSegment} " +
            "  GROUP BY month   " +
            "  ORDER BY month")
    List<AssetChangeVO> getFlowListMonth(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    /**
     * 、
     * 按照采购价
     *
     * @param queryWrapper
     * @return
     */
    @Select("SELECT sum(purchase_price) as total,  " +
            "DATE_FORMAT(create_time,'%Y-%m')as month  " +
            " FROM aa_asset p ${ew.customSqlSegment} GROUP BY month   " +
            " ORDER BY month")
    List<AssetChangeVO> getAssetListMonth(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    /**
     * 按照估值
     *
     * @param queryWrapper
     * @return
     */
    @Select("SELECT sum(value)  as total,  " +
            "DATE_FORMAT(create_time,'%Y-%m')as month  " +
            " FROM aa_asset p ${ew.customSqlSegment} GROUP BY month   " +
            " ORDER BY month")
    List<AssetChangeVO> getAssetListMonthByValue(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);
}
