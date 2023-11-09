package com.ruoyi.activiti.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author zx
 * @date 2022/1/13 10:05
 */
@Data
public class SealApplyDto implements Serializable {
    /**
     * 用印文件名称
     */
    private String document;
    /**
     * 用印人
     */
    private String sealUser;
    /**
     * 用印部门
     */
    private Long userDept;
    /**
     * 加盖何种印章
     */
    private String sealType;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
}
