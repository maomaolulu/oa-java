package com.ruoyi.training.entity;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 课程信息对象 tra_course_info
 *
 * @author yrb
 * @date 2022-06-10
 */
@Data
public class TraCourseInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键-课程id
     */
    private Long id;

    /**
     * 课程名称
     */
    @Excel(name = "课程名称")
    private String courseName;

    /**
     * 学时
     */
    @Excel(name = "学时")
    private BigDecimal classHour;

    /**
     * 培训类型id
     */
    @Excel(name = "培训类型id")
    private Long trainId;

    /**
     * 部门id（多个id用逗号隔开）
     */
    @Excel(name = "部门id", readConverterExp = "多=个id用逗号隔开")
    private String deptId;

    /**
     * 公司id-新需求，课程归属与全公司，选中的部门为必修课
     */
    private Long companyId;
    /**
     * 是否发布：1发布 0未发布
     */
    @Excel(name = "是否发布：1发布 0未发布")
    private Integer issueFlag;

    /**
     * 视频文件md5
     */
    @Excel(name = "视频文件md5")
    private String md5;

    /**
     * 是否推荐：1推荐 0不推荐
     * 变更-部门必修： 1：所在部门必修； 0：所在部门不必修
     */
    @Excel(name = "是否推荐：1推荐 0不推荐")
    private Integer recommend;

    /**
     * 最后操作者
     */
    @Excel(name = "最后操作者")
    private String lastModifier;

    /**
     * 关联数据-是否选择  0：已选择  1：未选择
     * 注：此属性用于查询本部门全部课程的时候辨别哪些课程已被登录用户加入了,不存数据库
     * hjy
     */
    private Integer checked;

    /**
     * 关联数据-课程类别
     * 注：用于查询本部门全部课程时匹配课程类别
     * hjy
     */
    private String categoryName;
    /**
     * 封面图片链接
     * hjy
     */
    private String coverUrl;
    /**
     * 封面图片文件夹
     */
    private String types;
    /**
     * 标签颜色
     */
    private String cssClass;


}
