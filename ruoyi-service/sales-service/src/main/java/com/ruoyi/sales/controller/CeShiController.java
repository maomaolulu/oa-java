package com.ruoyi.sales.controller;

import com.ruoyi.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sales/ceShi")
public class CeShiController {

    @GetMapping("ces")
    public R ces(String str){
        System.err.println(str);

        return R.data("hello");

    }

}
