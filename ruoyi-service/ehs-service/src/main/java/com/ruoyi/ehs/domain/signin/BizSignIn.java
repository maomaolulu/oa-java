package com.ruoyi.ehs.domain.signin;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author wuYang
 * @date 2022/9/19 15:11
 */
@Data
@Accessors(chain = true)
@TableName("biz_sign_in")
public class BizSignIn {

    @Id
    private Long id;

    private String address;

    private LocalDateTime time;

    private BigDecimal lng;

    private BigDecimal lat;

    private String img;

    private String userName;

    private Long userId;
    private Long deptId;

    private Long companyId;




}
