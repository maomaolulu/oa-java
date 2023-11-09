package com.ruoyi.daily.controller;

import com.ruoyi.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author yrb
 * @Date 2023/5/10 13:54
 * @Version 1.0
 * @Description
 */
@RestController
@RequestMapping("/ping")
public class PingController {
    @GetMapping()
    public R ping(){
        return R.ok();
    }
}
