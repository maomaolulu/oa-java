package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.dto.BizOrSignDTO;
import com.ruoyi.activiti.domain.proc.BizBusiness;

import java.util.List;

/**
 * @author wuYang
 * @date 2022/9/6 17:41
 */
public interface BizOrSignService {

    /**
     * 我的或签
     */
    List<BizBusiness> getOrSign(BizOrSignDTO dto);
}
