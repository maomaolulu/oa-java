package com.ruoyi.daily.service.asset;

import com.ruoyi.daily.domain.asset.AaTranscation;
import com.ruoyi.daily.domain.asset.Asset;
import com.ruoyi.daily.domain.asset.dto.LabDeviceDto;

import java.util.List;
import java.util.Map;

/**
 * 出入库记录
 * @author zx
 * @date 2022-08-08 15:11:40
 */
public interface AaTranscationService {
    /**
     * 保存出入库记录
     * @param aaTranscation 出入库记录
     */
    void save(AaTranscation aaTranscation);

    /**
     * 查出出入库记录
     * @param transcation
     * @return
     */
    List<AaTranscation> getList(AaTranscation transcation);

    /**
     * 查询固定资产详情
     * @param id
     * @return
     */
    Map<String,Object> getAssetsInfo(Long id);


}
