package com.ruoyi.activiti.domain.purchase;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 采购记录
 * @author zx
 * @date 2022/4/21 15:30
 */
@TableName("biz_goods_record")
@Data
public class BizGoodsRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long  id ;
    /** 逻辑删 */
    private String delFlag ;
    /** 创建人 */
    private String createBy ;
    /** 创建时间 */
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;
    /** 物品id */
    private Long goodsId ;
    /** 采购环节 */
    private String link ;
    /** 用户姓名 */
    private String userName ;
    /**
     * 移交目标
     */
    @TableField(exist = false)
    private List<String> users ;
}
