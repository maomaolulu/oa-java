package com.ruoyi.daily.service.asset;


import com.ruoyi.daily.domain.asset.Asset;
import com.ruoyi.daily.domain.asset.dto.*;

import java.util.List;
import java.util.Map;

/**
 * 固定资产
 * @author zx
 * @date 2022/3/15 18:14
 */
public interface AssetService {
    /**
     * 查询列表
     * @return
     */
    List<Map<String,Object>> getLsit(AssetListDto assetListDto);

    /**
     * 查询列表 --- 在移交固定资产中使用
     * @return
     */
    List<Map<String,Object>> getList(AssetListDto assetListDto);


    /**
     *   获取物品信息
     * @return
     */
    List<DutyAssetDto> getDutyAsset(DutyAssetDto dutyAssetDto);

    /**
     * 替换责任人
     * @param replaceDutyDto
     */
    void replaceDuty(ReplaceDutyDto replaceDutyDto);

    /**
     * 离职物品移交
     * @param transferGoodsDTO
     */
    void transferGoods(TransferGoodsDTO transferGoodsDTO);

    /**
     * 保存固定资产
     * @param asset
     */
    void save(Asset asset);

    /**
     * 固定资产详情
     * @param assetListDto
     * @return
     */
    Map<String,Object> getLsitInfo(AssetListDto assetListDto);

    /**
     * 打印标签
     * @param id 固定资产id
     */
    void print(Long id);

    /**
     * 实验室仪器列表
     * @param labDeviceDto
     * @return
     */
    List<Asset> getDeviceList(LabDeviceDto labDeviceDto);

    /**
     * 实验室仪器信息
     * @param labDeviceDto
     * @return 资产信息
     */
    Asset getDeviceInfo(LabDeviceDto labDeviceDto);

    /**
     * 运营2.0仪器设备信息入库
     *
     * @param asset 仪器设备信息
     * @return result
     */
    int add(Asset asset);
}
