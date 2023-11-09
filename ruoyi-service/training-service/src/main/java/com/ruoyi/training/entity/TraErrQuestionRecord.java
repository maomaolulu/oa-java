package com.ruoyi.training.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * 错题记录
 * @author zx
 * @date 2022-06-15 10:31:27
 */
@TableName("tra_err_question_record")
@Data
@Accessors(chain = true)
public class TraErrQuestionRecord implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 试题id
     */
    private BigInteger questionId;
    /**
     * 类别id
     */
    private BigInteger categoryId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 错误答案
     */
    private String answer;
    /**
     * 考试id
     */
    private Long examId;

}
