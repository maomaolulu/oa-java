package com.ruoyi.ehs.domain.signin.dto;

import lombok.Data;

/**
 * @author wuYang
 * @date 2022/9/19 16:33
 */
@Data
public class BizSignInDTO {

    private String userName;

    private Long companyId;

    private Long deptId;

    private String startTime;

    private String endTime;

}
