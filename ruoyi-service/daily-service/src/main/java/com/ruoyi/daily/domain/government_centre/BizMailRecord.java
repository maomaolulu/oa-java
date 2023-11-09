package com.ruoyi.daily.domain.government_centre;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 邮件记录
 * @author zx
 * @date 2022/3/24 15:08
 */
@Data
@TableName("biz_mail_record")
public class BizMailRecord implements Serializable {
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
    /** 收件邮箱 */
    private String sendTo ;
    /** 主题 */
    private String subject ;
    /** 内容 */
    private String content ;
    /**
     * 开始时间
     */
    @TableField(exist = false)
    private String startDate;
    /**
     * 结束时间
     */
    @TableField(exist = false)
    private String endDate;
    /**
     * 发送状态
     */
    private String sendStatus;
}
