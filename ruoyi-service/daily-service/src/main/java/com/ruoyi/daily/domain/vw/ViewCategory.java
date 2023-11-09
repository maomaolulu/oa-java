package com.ruoyi.daily.domain.vw;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.daily.domain.asset.dto.DutyAssetDto;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Author 郝佳星
 * Date 2022/6/7 16:56
 **/
@TableName("view_category")
@Data
public class ViewCategory implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 品类名称 */
    private String categoryName ;
    /** 创建人 */
    private String createBy ;
    /** 修改人 */
    private String updateBy ;
    /** 创建日期 */
    private Date createTime ;
    /** 修改日期 */
    private Date updateTime ;
    /** 所属公司 */
    private BigInteger companyId ;
    /** 所属公司名称 */
    private String deptName;

    /** 资产总数 */
    @TableField(exist = false)
    private Integer categorySum;

    /** 库存数量 */
    @TableField(exist = false)
    private Integer categoryNum;

    @TableField(exist = false)
    private Integer state;

    @TableField(exist = false)
    private Long aid;

    /** 单位 */
    private String unit;

//    @TableField(exist = false)
//    private List<ViewAsset> assetList;

}
