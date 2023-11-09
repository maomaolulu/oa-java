package com.ruoyi.training.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author : http://www.chiner.pro
 * @date : 2022-6-7
 * @desc : 分数记录表
 */
@TableName("tra_score_record")
@Data
@Accessors(chain = true)
public class TraScoreRecord implements Serializable {
    /** id */
    @TableId(type = IdType.AUTO)
    private BigInteger id ;
    /** 创建人 */
    private String createBy ;
    /** 创建时间 */
    private Date createTime ;
    /** 姓名 */
    private String userName ;
    /** 用户id */
    private Integer userId ;
    /** 分数 */
    private BigDecimal score ;
    /** 考试id */
    private Long examId ;

}