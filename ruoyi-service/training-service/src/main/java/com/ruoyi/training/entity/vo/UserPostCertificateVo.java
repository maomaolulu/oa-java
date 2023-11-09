package com.ruoyi.training.entity.vo;

import lombok.Data;

/**
 * 证书岗位用户封装提
 *
 * @author hjy
 * @date 2022/7/5 18:02
 */
@Data
public class UserPostCertificateVo {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 岗位id
     */
    private Integer postId;
}
