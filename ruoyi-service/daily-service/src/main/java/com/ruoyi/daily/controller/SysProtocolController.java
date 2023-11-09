package com.ruoyi.daily.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.daily.service.SysProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zx
 * @date 2022/1/12 13:51
 * @menu 系统协议
 */
@RestController
@RequestMapping("protocol")
public class SysProtocolController extends BaseController {
    private final SysProtocolService protocolService;
    @Autowired
    public SysProtocolController(SysProtocolService protocolService) {
        this.protocolService = protocolService;
    }

    /**
     * 查询协议
     * @param type 1用户协议2隐私协议
     */
    @GetMapping("info")
    public R get(String type)
    {
        try {
            return R.data(protocolService.get(type));
        }catch (Exception e){
            logger.error("获取协议失败",e);
            return R.error("获取协议失败");
        }
    }
}
