package com.ruoyi.daily.controller.asset;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.daily.domain.asset.AaCategoryFixed;
import com.ruoyi.daily.service.asset.AaCategoryFixedService;
import org.springframework.web.bind.annotation.*;

/**
 * @Author yrb
 * @Date 2023/8/2 17:08
 * @Version 1.0
 * @Description 固定资产品类
 */
@RestController
@RequestMapping("/category_fixed")
public class AaCategoryFixedController extends BaseController {
    private final AaCategoryFixedService aaCategoryFixedService;

    public AaCategoryFixedController(AaCategoryFixedService aaCategoryFixedService) {
        this.aaCategoryFixedService = aaCategoryFixedService;
    }

    @PostMapping("/save")
    @OperLog(title = "保存固定资产品类信息", businessType = BusinessType.INSERT)
    public R save(@RequestBody AaCategoryFixed aaCategoryFixed) {
        try {
            if (StrUtil.isBlank(aaCategoryFixed.getCategoryName())) {
                return R.error("品类名称不能为空");
            }
            if (aaCategoryFixed.getAssetTypeId() == null) {
                return R.error("资产类型id不能为空");
            }
            if (!aaCategoryFixedService.checkUnique(aaCategoryFixed)){
                return R.error("品类已存在");
            }
            if (aaCategoryFixedService.save(aaCategoryFixed)) {
                return R.ok("成功");
            }
            return R.error("失败");

        } catch (Exception e) {
            logger.error("新增固定资产品类信息异常：" + e);
            return R.error("失败");
        }
    }

    @GetMapping("/list")
    public R list(AaCategoryFixed aaCategoryFixed){
        try{
            startPage();
            return result(aaCategoryFixedService.getInfo(aaCategoryFixed));
        }catch (Exception e){
            logger.error("获取固定资产品类信息异常:"+e.getMessage());
            return R.error("获取固定资产品类信息异常");
        }
    }
}
