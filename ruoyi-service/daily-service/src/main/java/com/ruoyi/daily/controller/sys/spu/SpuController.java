package com.ruoyi.daily.controller.sys.spu;

import com.ruoyi.daily.service.impl.sys.temp.InsertSpuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.ResultMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by WuYang on 2022/8/29 14:00
 */
@Api(tags = {"设置当前库存"})
@RequestMapping("/spu")
@RestController
public class SpuController {

    @Resource
    InsertSpuService insertSpuService;
    @ApiOperation("插入")
    @GetMapping("/insert")
    public void insert() {
        insertSpuService.insertSpu();
    }

    @GetMapping("/update")
    public void update() {
        insertSpuService.updateSkuId();
    }
}
