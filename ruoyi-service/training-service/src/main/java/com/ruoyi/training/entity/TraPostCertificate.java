package com.ruoyi.training.entity;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.training.entity.vo.UserPostCertificateVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 证书岗位表
 *
 * @author hjy
 * @date 2022/6/21 13:37
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TraPostCertificate extends BaseEntity {

    /**
     * 岗位id，主键，自增
     */
    private Integer postId;
    /**
     * 岗位所属分公司
     */
    private Long companyId;

    /**
     * 岗位编码 必填
     */
    private String postCode;
    /**
     * 岗位名称 必填
     */
    private String postName;
    /**
     * 岗位顺序
     */
    private Integer postSort;
    /**
     * 状态（0 正常  1停用）
     */
    private Integer status;
    /**
     * 关联数据-岗位所关联的用户
     */
    private List<UserPostCertificateVo> userPostList;
    /**
     * 关联数据-当前公司所有已绑定人员的id，用于筛选已绑定人员
     */
    private String userIds;
    /**
     * 关联数据-用于整理userIds
     */
    private String[] userList;

}
