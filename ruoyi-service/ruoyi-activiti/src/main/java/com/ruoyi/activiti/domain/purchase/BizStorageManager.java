package com.ruoyi.activiti.domain.purchase;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 耗材库管管理
 * @author zx
 * @date 2022/3/30 15:43
 */
@Data
@TableName("biz_storage_manager")
public class BizStorageManager implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id ;
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
    /** 公司 */
    private Long companyId ;
    /** 分类 */
    private String types ;
    /** 库管 */
    private Long userId ;
    /**
     * 库管名字
     */
    private String userName;
}
