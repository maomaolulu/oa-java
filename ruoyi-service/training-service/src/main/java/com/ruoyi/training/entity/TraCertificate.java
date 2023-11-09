package com.ruoyi.training.entity;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.training.entity.vo.CourseUserVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 培训证书实体
 *
 * @author hjy
 * @date 2022/6/21 16:49
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TraCertificate extends BaseEntity {
    /**
     * 证书id
     */
    private String id;
    /**
     * 证书名称
     */
    private String certificateName;
    /**
     * 证书类型  1：新员工   2：老员工
     */
    private Integer certificateType;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 考试id
     */
    private Long examId;
    /**
     * 证书年份
     */
    private Integer trainYear;
    /**
     * 岗位编码
     */
    private String postCode;
    /**
     * 部门id
     */
    private Long deptId;
    /**
     * 已学课程总学时
     */
    private BigDecimal allHour;
    /**
     * 证书起始日期
     */
    private String startDate;
    /**
     * 存放已学课程-数据载体
     */
    private List<CourseUserVO> courseList;

}
