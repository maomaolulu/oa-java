package com.ruoyi.daily.controller.statistics;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.daily.domain.statistics.*;
import com.ruoyi.daily.service.statistics.AssetChangeService;
import com.ruoyi.daily.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * 统计分析
 * @author wuYang
 * @date 2022/8/30 10:41
 */
@Api(tags = {"统计分析"})
@RestController
public class StatisticsController extends BaseController {
    @Resource
    AssetChangeService assetChangeService;


    @ApiOperation("固定资产变化分析")
    @GetMapping("asset")
    public Result<List<AssetChangeVO>> assetChangeList(AssetChangeDTO dto) {
        try {
            LocalDate startTime = null;
            LocalDate endTime = null;
            String start = dto.getStartTime();
            if (StringUtils.isNotBlank(start)) {
                 startTime = DateUtil.parse(start).toLocalDateTime().toLocalDate();
            }
            String end = dto.getEndTime();
            if (StringUtils.isNotBlank(end)) {
                 endTime = DateUtil.parse(end).toLocalDateTime().toLocalDate();
            }
            List<AssetChangeVO> assetChange = assetChangeService.getAssetChange(dto, startTime, endTime);
            return Result.data(assetChange);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取失败");
            return Result.error("获取失败");
        }
    }

    @ApiOperation("固定资产占比变化")
    @GetMapping("proportion")
    public Result< List<AssetProportionVO>>  assetProportionList(AssetProportionDTO dto) {
        try {
            LocalDate startTime = null;
            LocalDate endTime = null;
            String start = dto.getStartTime();
            if (StringUtils.isNotBlank(start)) {
                startTime = DateUtil.parse(start).toLocalDateTime().toLocalDate();
            }
            String end = dto.getEndTime();
            if (StringUtils.isNotBlank(end)) {
                endTime = DateUtil.parse(end).toLocalDateTime().toLocalDate();
            }
            List<AssetProportionVO> assetChange = assetChangeService.getAssetProportion(dto, startTime, endTime);
            return Result.data(assetChange);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取失败");
            return Result.error("获取失败");
        }
    }

    @ApiOperation("流动固定资产占比分析")
    @GetMapping("contrast")
    public Result<AssetContrastVO>  contrastList(AssetContrastDTO dto) {
        try {
            LocalDate startTime = null;
            LocalDate endTime = null;
            String start = dto.getStartTime();
            if (StringUtils.isNotBlank(start)) {
                startTime = DateUtil.parse(start).toLocalDateTime().toLocalDate();
            }
            String end = dto.getEndTime();
            if (StringUtils.isNotBlank(end)) {
                endTime = DateUtil.parse(end).toLocalDateTime().toLocalDate();
            }
            AssetContrastVO contrast = assetChangeService.getContrast(dto, startTime, endTime);
            return Result.data(contrast);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取失败");
            return Result.error("获取失败");
        }
    }
    @ApiOperation("流动固定资产月份")
    @GetMapping("contrast/month")
    public Result<TotalListVO> contrastListByMonth(AssetContrastDTO dto) {
        try {
            LocalDate startTime = null;
            LocalDate endTime = null;
            String start = dto.getStartTime();
            if (StringUtils.isNotBlank(start)) {
                startTime = DateUtil.parse(start).toLocalDateTime().toLocalDate();
            }
            String end = dto.getEndTime();
            if (StringUtils.isNotBlank(end)) {
                endTime = DateUtil.parse(end).toLocalDateTime().toLocalDate();
            }
            TotalListVO changeList = assetChangeService.getChangeList(dto, startTime, endTime);
            return Result.data(changeList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取失败");
            return Result.error("获取失败");
        }
    }


}
