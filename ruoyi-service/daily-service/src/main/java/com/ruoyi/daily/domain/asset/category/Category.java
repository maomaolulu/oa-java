package com.ruoyi.daily.domain.asset.category;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 品类
 * @author zx
 * @date 2022-06-23 09:33:14
 **/
@Data
@TableName("aa_category")
public class Category implements Serializable{
    @TableId(type = IdType.AUTO)
    private Long id ;
    /** 品类名称 */
    private String categoryName ;
    /** 创建人 */
    private String createBy ;
    /** 修改人 */
    private String updateBy;
    /** 创建日期 */
    @JsonFormat(timezone = "GMT+8")
    private Date createTime ;
    /** 修改日期 */
    @JsonFormat(timezone = "GMT+8")
    private Date updateTime ;
    /** 所属公司 */
    private Long companyId ;
    /** 单位 */
    private String unit ;
    @TableField(exist = false)
    /** 所属公司名称 */
    private String deptName;
    @TableField(exist = false)
    /**
     * 资产总数
     */
    private Integer categorySum;
    /** 库存数量 */
    @TableField(exist = false)
    private Integer categoryNum;
}


