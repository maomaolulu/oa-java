package com.ruoyi.daily.controller.asset;

import cn.hutool.core.exceptions.StatefulException;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.utils.EasyExcelUtil;
import com.ruoyi.common.utils.IpUtils;
import com.ruoyi.common.utils.NoModelWriteData;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.daily.domain.asset.AaTranscation;
import com.ruoyi.daily.domain.asset.Asset;
import com.ruoyi.daily.domain.asset.dto.*;
import com.ruoyi.daily.service.asset.AaTranscationService;
import com.ruoyi.daily.service.asset.AssetService;
import com.ruoyi.system.util.SystemUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 固定资产管理
 *
 * @author zx
 * @date 2022/3/15 16:56
 * @menu 固定资产管理
 */
@Api(tags = {"固定资产管理"})
@RestController
@RequestMapping("asset")
public class AssetController extends BaseController {
    private final AssetService assetService;
    private final AaTranscationService transcationService;

    @Autowired
    public AssetController(AssetService assetService,
                           AaTranscationService transcationService) {
        this.assetService = assetService;
        this.transcationService = transcationService;
    }

    /**
     * 查询列表
     *
     * @param assetListDto
     * @return
     */
    @ApiOperation("查询列表")
    @GetMapping("list")
    public R list(AssetListDto assetListDto) {
        try {
            startPage();
            return result(assetService.getLsit(assetListDto));
        } catch (Exception e) {
            logger.error("获取固定资产列表", e);
            return R.error("获取固定资产列表失败");
        }
    }

    /**
     * 查询列表详情
     *
     * @param assetListDto
     * @return
     */
    @GetMapping("list_info")
    public R listInfo(AssetListDto assetListDto) {
        try {
            return R.data(assetService.getLsitInfo(assetListDto));
        } catch (Exception e) {
            logger.error("获取固定资产详情", e);
            return R.error("获取固定资产详情失败");
        }
    }

    /**
     * 打印标签
     *
     * @param id 固定资产id
     * @return
     */
    @GetMapping("label")
    public R print(Long id) {
        try {
            assetService.print(id);
            return R.ok();
        } catch (Exception e) {
            logger.error("更新打印标签状态失败", e);
            return R.error("更新打印标签状态失败");
        }
    }

    /**
     * 评价专用
     *
     * @return
     */
    @GetMapping("list_pj")
    public R listPj(HttpServletRequest request) {
        try {
            System.out.println(IpUtils.getHostIp());
            System.out.println(IpUtils.getHostName());
            System.out.println(IpUtils.getIpAddr(request));
            if (!IpUtils.getIpAddr(request).equals("192.168.0.60")) {
                return R.error("无权访问");
            }
            AssetListDto assetListDto = new AssetListDto();
            assetListDto.setAsset_type(53);
            return R.data(assetService.getLsit(assetListDto));
        } catch (Exception e) {
            logger.error("获取固定资产列表", e);
            return R.error("获取固定资产列表失败");
        }
    }

    /**
     * 导出列表
     *
     * @param assetListDto
     * @return
     */
    @GetMapping("excel")
    public void exportExcel(HttpServletResponse response, AssetListDto assetListDto) {
        try {
            NoModelWriteData noModelWriteData = new NoModelWriteData();
            assetListDto.setExcel(1);
            List<Map<String, Object>> list = assetService.getLsit(assetListDto);
            noModelWriteData.setDataList(list);

            noModelWriteData.setFileName("固定资产");
            String[] headMap = {"所属公司", "责任部门", "资产类别", "物品编号", "物品名称", "规格型号", "品类名称", "物品单位", "物品状态", "是否检定", "准用说明", "责任人", "保管人", "供应商", "当前估值", "采购单价", "采购时间", "备注", "录入人", "录入时间", "维护人", "维护时间"};
            String[] dataStrMap = {"company_name", "dept_name", "asset_type_name", "asset_sn", "name", "model", "category_name", "unit", "state_name", "is_inspected_name", "permit", "charger", "keeper", "dealer", "value", "purchase_price", "purchase_time", "notes", "create_by", "create_time", "update_by", "update_time"};
            noModelWriteData.setHeadMap(headMap);
            noModelWriteData.setDataStrMap(dataStrMap);
            EasyExcelUtil.noModuleWriteByMap(noModelWriteData, response);

        } catch (Exception e) {
            logger.error("导出固定资产列表", e);
        }
    }

    /**
     * 获取责任物品信息
     *
     * @param dutyAssetDto
     * @return
     */
    @GetMapping("dutyAsset")
    public R dutyAsset(DutyAssetDto dutyAssetDto) {
        try {
            pageUtil();
            return result(assetService.getDutyAsset(dutyAssetDto));
        } catch (Exception e) {
            logger.error("获取责任物品列表", e);
            return R.error("获取责任物品列表失败");
        }
    }

    /**
     * 获取保管物品信息
     *
     * @param dutyAssetDto
     * @return
     */
    @GetMapping("keeperAsset")
    public R keeperAsset(DutyAssetDto dutyAssetDto) {
        try {
            pageUtil();
            return result(assetService.getDutyAsset(dutyAssetDto));
        } catch (Exception e) {
            logger.error("获取保管物品列表", e);
            return R.error("获取保管物品列表失败");
        }
    }

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    @GetMapping("getUserInfo")
    public void getUserInfo() {
        System.out.println(SystemUtil.getUserNameCn());
    }

    /**
     * 替换责任人
     */
    @PutMapping("replaceDuty")
    @OperLog(title = "替换责任人", businessType = BusinessType.UPDATE)
    public R replaceDuty(@RequestBody ReplaceDutyDto replaceDutyDto) {
        try {
            assetService.replaceDuty(replaceDutyDto);
            return R.ok("操作成功");
        } catch (Exception e) {
            logger.error("替换责任人", e);
            return R.error("替换责任人失败");
        }
    }

    /**
     * 离职物品移交
     */
    @PutMapping("trans")
    @OperLog(title = "离职物品移交", businessType = BusinessType.UPDATE)
    public R transfer(@RequestBody TransferGoodsDTO transferGoodsDTO) {
        try {
            assetService.transferGoods(transferGoodsDTO);
            return R.ok();
        } catch (Exception e) {
            logger.error("离职物品移交失败", e);
            return R.error("离职物品移交失败");
        }
    }

    /**
     * 保存固定资产
     *
     * @param asset
     * @return
     */
    @PostMapping("info")
    @OperLog(title = "保存固定资产", businessType = BusinessType.INSERT)
    public R save(@RequestBody Asset asset) {
        try {
            assetService.save(asset);
            return R.ok("保存成功");
        } catch (StatefulException e) {
            return R.error("资产编号已存在");
        } catch (Exception e) {
            logger.error("保存固定资产", e);
            return R.error("保存固定资产失败");
        }
    }

    /**
     * 保存出入库记录
     *
     * @param transcation
     * @return
     */
    @PostMapping("transcation")
    @OperLog(title = "保存出入库记录", businessType = BusinessType.INSERT)
    public R save(@RequestBody AaTranscation transcation) {
        try {
            transcationService.save(transcation);
            return R.ok("保存成功");
        } catch (Exception e) {
            logger.error("保存出入库记录", e);
            return R.error("保存出入库记录失败");
        }
    }

    /**
     * 出入库记录查询
     *
     * @param transcation
     * @return
     */
    @GetMapping("transcation")
    public R getList(AaTranscation transcation) {
        try {
            pageUtil();
            return resultData(transcationService.getList(transcation));
        } catch (Exception e) {
            logger.error("查询出入库记录", e);
            return R.error("查询出入库记录失败");
        }
    }

    /**
     * 出入库记录详情
     *
     * @param id
     * @return
     */
    @GetMapping("asset_info")
    public R getAssetsInfo(Long id) {
        try {
            return R.data(transcationService.getAssetsInfo(id));
        } catch (StatefulException e) {
            logger.error("查无此批次信息" + id);
            return R.error("查无此批次信息");
        } catch (Exception e) {
            logger.error("查询出入库记录详情", e);
            return R.error("查询出入库记录详情失败");

        }
    }

    /**
     * 实验室仪器列表-分页
     */
    @GetMapping("lab_device_page")
    public R getDeviceListPage(LabDeviceDto labDeviceDto) {
        String dataBelong = labDeviceDto.getDataBelong();
        if (StringUtils.isNotEmpty(dataBelong)) {
            if ("杭州安联".equals(dataBelong)) {
                labDeviceDto.setDeptId(139L);
            } else if ("宁波安联".equals(dataBelong)) {
                labDeviceDto.setDeptId(159L);
            } else if ("嘉兴安联".equals(dataBelong)) {
                labDeviceDto.setDeptId(124L);
            } else {
                //上海量远
                labDeviceDto.setDeptId(167L);
            }
        } else {
            //兼容、默认 杭州实验室数据
            labDeviceDto.setDeptId(139L);
        }
        pageUtil();
        return resultData(assetService.getDeviceList(labDeviceDto));
    }

    /**
     * 实验室仪器列表-不分页
     */
    @GetMapping("lab_device")
    public R getDeviceList(LabDeviceDto labDeviceDto) {
        String dataBelong = labDeviceDto.getDataBelong();
        if (StringUtils.isNotEmpty(dataBelong)) {
            if ("杭州安联".equals(dataBelong)) {
                labDeviceDto.setDeptId(139L);
            } else if ("宁波安联".equals(dataBelong)) {
                labDeviceDto.setDeptId(159L);
            } else if ("嘉兴安联".equals(dataBelong)) {
                labDeviceDto.setDeptId(124L);
            } else {
                //上海量远
                labDeviceDto.setDeptId(167L);
            }
        } else {
            //兼容、默认 杭州实验室数据
            labDeviceDto.setDeptId(139L);
        }
        return R.data(assetService.getDeviceList(labDeviceDto));
    }

    /**
     * 仪器信息-详情
     */
    @GetMapping("lab_device_info")
    public R getDeviceInfo(LabDeviceDto labDeviceDto) {
        try {
            return R.data(assetService.getDeviceInfo(labDeviceDto));
        } catch (Exception e) {
            logger.error("查询实验室仪器信息", e);
            return R.error("查询实验室仪器信息失败");
        }
    }

    /**
     * 运营2.0仪器设备信息入库
     *
     * @param asset 入库信息
     * @return result
     */
    @ApiOperation("设备入库")
    @PostMapping("/add")
    public int add(HttpServletRequest request, @RequestBody Asset asset) {
        String authCode = "d537fa8d-3ec2-48a4-916e-fa834f7f2922";
        if (!authCode.equals(request.getHeader("authCode"))){
            return 2;
        }
        return assetService.add(asset);
    }

}
