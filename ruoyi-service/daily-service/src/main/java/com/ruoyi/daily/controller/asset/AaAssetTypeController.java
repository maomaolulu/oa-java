package com.ruoyi.daily.controller.asset;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.daily.domain.asset.AaAssetType;
import com.ruoyi.daily.service.asset.AaAssetTypeService;
import org.springframework.web.bind.annotation.*;

/**
 * @Author yrb
 * @Date 2023/8/2 15:07
 * @Version 1.0
 * @Description 资产类型
 */
@RestController
@RequestMapping("/asset_type")
public class AaAssetTypeController extends BaseController {
    private final AaAssetTypeService aaAssetTypeService;

    public AaAssetTypeController(AaAssetTypeService aaAssetTypeService) {
        this.aaAssetTypeService = aaAssetTypeService;
    }

    @GetMapping("/list")
    public R list(AaAssetType aaAssetType) {
        try {
            return R.data(aaAssetTypeService.getList(aaAssetType));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return R.error("获取资产类型信息发生异常！");
        }
    }

    @PostMapping("/save")
    @OperLog(title = "保存资产类型信息", businessType = BusinessType.INSERT)
    public R save(@RequestBody AaAssetType aaAssetType) {
        try {
            if (StrUtil.isBlank(aaAssetType.getAssetName())) {
                return R.error("参数错误");
            }
            if (!aaAssetTypeService.checkUnique(aaAssetType)) {
                return R.error("资产类别已存在");
            }
            if (aaAssetTypeService.save(aaAssetType)) {
                return R.ok("成功");
            }
            return R.error("失败");
        } catch (Exception e) {
            logger.error("保存资产类型发生异常，异常信息：" + e.getMessage());
            return R.error("失败");
        }
    }
}
