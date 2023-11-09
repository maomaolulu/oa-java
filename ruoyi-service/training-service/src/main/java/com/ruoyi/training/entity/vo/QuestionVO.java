package com.ruoyi.training.entity.vo;

import com.ruoyi.file.domain.SysAttachment;
import com.ruoyi.training.entity.TraOptionsInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.List;

/**
 * @Author: zx
 * @CreateTime: 2022-06-01  21:00
 * @Description: 试题vo
 *
 */
@Data
@Accessors(chain = true)
public class QuestionVO {
    /**
     * 试题id
     */
    private BigInteger id;
    /**
     * 试题内容
     */
    private String content;
    /**
     * 课程信息
     */
    private List<CourseVO> courseInfo;
    /**
     * 课程类别
     */
    private String categoryName;
    /**
     * 最后操作者
     */
    private String updateBy;
    /**
     * 最后操作时间
     */
    private String updateTime;
    /**
     * 答案
     */
    private String answer;
    /**
     * 选项
     */
    private List<TraOptionsInfo> optionInfos;
    /**
     * 图片列表
     */
    private List<SysAttachment> imgList;
    /**
     * 题目类型
     * 1:单选题
     * 2:多选题
     * 3:判断题
     */
    private Integer type;

    /**
     * 部门id（公司id）
     */
    private BigInteger deptId;
    /**
     * 部门名称（公司名称）
     */
    private String deptName;

}
