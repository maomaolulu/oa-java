package com.ruoyi.ehs.service;

import com.ruoyi.ehs.domain.signin.BizSignIn;
import com.ruoyi.ehs.domain.signin.dto.BizSignInAddDTO;
import com.ruoyi.ehs.domain.signin.dto.BizSignInDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 打卡签到
 * @author wuYang
 * @date 2022/9/19 15:42
 */
public interface BizSignInService {

    /**
     * 新增
     */
    void  add(BizSignInAddDTO bizSignIn);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 查询
     */
    List<BizSignIn> selectPage(BizSignInDTO dto, LocalDateTime start,LocalDateTime end);

    /**
     * 更新
     */
    void update();

}
