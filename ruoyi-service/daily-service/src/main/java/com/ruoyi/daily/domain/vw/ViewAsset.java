package com.ruoyi.daily.domain.vw;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Author 郝佳星
 * Date 2022/6/8 11:15
 **/
@TableName("view_asset")
@Data
public class ViewAsset {
    /** 所属部门名称 */
    private String deptName;

    private String assetTypeName;

    private Integer assetType;

    private String assetSn;

    private String name;

    private String model;

    private Integer state;
}
