package com.ruoyi.daily.service.impl.asset;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.daily.domain.asset.AaSku;
import com.ruoyi.daily.domain.asset.AaSpu;
import com.ruoyi.daily.domain.asset.AaTranscation;
import com.ruoyi.daily.domain.asset.Asset;
import com.ruoyi.daily.domain.asset.dto.LabDeviceDto;
import com.ruoyi.daily.feign.RemoteDictService;
import com.ruoyi.daily.mapper.asset.AaSkuMapper;
import com.ruoyi.daily.mapper.asset.AaSpuMapper;
import com.ruoyi.daily.mapper.asset.AaTranscationMapper;
import com.ruoyi.daily.mapper.asset.AssetMapper;
import com.ruoyi.daily.service.asset.AaTranscationService;
import com.ruoyi.file.feign.RemoteFileService;
import com.ruoyi.system.domain.SysDictData;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.DataScopeUtil;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ruoyi.common.utils.MapUtil.toMap;

/**
 * 出入库记录
 *
 * @author zx
 * @date 2022-08-08 19:20:42
 */
@Service
@Slf4j
public class AaTranscationServiceImpl implements AaTranscationService {
    private final AaTranscationMapper transcationMapper;
    private final RemoteUserService remoteUserService;
    private final DataScopeUtil dataScopeUtil;
    private final AssetMapper assetMapper;
    private final RemoteDeptService remoteDeptService;
    private final RemoteFileService remoteFileService;
    private final AaSkuMapper aaSkuMapper;
    private final AaSpuMapper aaSpuMapper;
    private final RedisService redisService;


    @Autowired
    public AaTranscationServiceImpl(AaTranscationMapper transcationMapper, RemoteUserService remoteUserService, DataScopeUtil dataScopeUtil,  AssetMapper assetMapper, RemoteDeptService remoteDeptService, RemoteFileService remoteFileService, AaSkuMapper aaSkuMapper, AaSpuMapper aaSpuMapper, RedisService redisService) {
        this.transcationMapper = transcationMapper;
        this.remoteUserService = remoteUserService;
        this.dataScopeUtil = dataScopeUtil;
        this.assetMapper = assetMapper;
        this.remoteDeptService = remoteDeptService;
        this.remoteFileService = remoteFileService;
        this.aaSkuMapper = aaSkuMapper;
        this.aaSpuMapper = aaSpuMapper;
        this.redisService = redisService;
    }

    /**
     * 保存出入库记录
     *
     * @param aaTranscation 出入库记录
     */
    @Override
    public void save(AaTranscation aaTranscation) {
        transcationMapper.insert(aaTranscation);
    }

    /**
     * 查询出入库记录
     *
     * @param transcation
     * @return
     */
    @Override
    public List<AaTranscation> getList(AaTranscation transcation) {
        QueryWrapper<AaTranscation> wrapper = new QueryWrapper<>();
        wrapper.in(transcation.getTransType() != null, "a.trans_type", String.valueOf(transcation.getTransType()).split("", 0));
        wrapper.like(StrUtil.isNotBlank(transcation.getName()), "a.name", transcation.getName());
        wrapper.like(StrUtil.isNotBlank(transcation.getItemSn()), "a.item_sn", transcation.getItemSn());
        wrapper.eq(transcation.getCompanyId() != null, "a.company_id", transcation.getCompanyId());
        wrapper.eq(transcation.getDeptId() != null, "a.dept_id", transcation.getDeptId());
        wrapper.eq(transcation.getItemType() != null, "a.item_type", transcation.getItemType());
        // 领用人
        wrapper.like(StrUtil.isNotBlank(transcation.getApplier()), "a.applier", transcation.getApplier());
        SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
        String sql = dataScopeUtil.getScopeSql(sysUser, "d", null);
        if (StrUtil.isNotBlank(sql)) {
            wrapper.apply(sql);
        }
        return transcationMapper.selectTranscation(wrapper);
    }

    /**
     * 查询固定资产详情
     *
     * @param id
     * @return
     */
    @Override
    public Map<String,Object> getAssetsInfo(Long id) {
        AaTranscation aaTranscation = transcationMapper.selectById(id);
        Map<String, Object> map = new HashMap<>(12);
        Map<Long, String> deptMap = remoteDeptService.listEnable2Map();
        if(aaTranscation.getItemType()==1){

            Asset asset = assetMapper.selectById(aaTranscation.getIdentifier());
            if (asset == null) {
                return null;
            }
            asset.setTransType(aaTranscation.getTransType());
            asset.setAssetTypeName(redisService.getCacheObject("dict:"+asset.getAssetType()).toString());
            asset.setStateName(redisService.getCacheObject("dict:goodStatus:"+asset.getState()).toString());
            asset.setDeptName(remoteDeptService.selectSysDeptByDeptId(asset.getDeptId()).getDeptName());
            asset.setCompanyName(deptMap.get(asset.getCompanyId()));
            if(asset.getIsInspected()==null){
                asset.setIsInspected(0);
            }
            asset.setIsInspectedName(asset.getIsInspected() == 1?"是":"否");
            asset.setTransTypeName(getTransType(asset.getTransType()));
            asset.setPicPathList(getUrl(asset.getPicPath()));
            asset.setCertPathList(getUrl(asset.getCertPath()));
            asset.setAttPathList(getUrl(asset.getAttPath()));
            map = toMap(asset);
            map.put("amount",1);
        }else if(aaTranscation.getItemType()==2){
            AaSku aaSku = aaSkuMapper.selectById(aaTranscation.getIdentifier());
            if(aaSku==null){
                throw new StatefulException("查无批次");
            }
            AaSpu aaSpu = aaSpuMapper.selectById(aaSku.getSpuId());
            aaSku.setPurchasePriceSingle(aaSku.getPurchasePrice()!=null?aaSku.getPurchasePrice():BigDecimal.ZERO);
            aaSku.setCompanyName(deptMap.get(aaSpu.getCompanyId()));
            aaSku.setAveragePrice(aaSpu.getPrice()!=null?aaSpu.getPrice():BigDecimal.ZERO);
//            aaSku.setSpuAmount(aaTranscation.getSpuAmount()!=null?aaTranscation.getSpuAmount():0);
            aaSku.setTransTypeName(redisService.getCacheObject("dict:io_storage_type:"+aaTranscation.getTransType()).toString());
            aaSku.setSpuSn(aaSpu.getSpuSn());
            aaSku.setSpuAmount(aaTranscation.getSpuAmount());
            map = toMap(aaSku);
            Map<String, Object> map1 = toMap(aaSpu);
            map1.putAll(map);
            map.putAll(map1);
            map.put("spuTypeName",redisService.getCacheObject("dict:"+aaSpu.getSpuType()).toString());
            map.put("hazardType",aaSpu.getHazardType());
            map.put("storageCond",aaSpu.getStorageCond());
            map.put("isInspected",aaSpu.getIsInspected());
            map.put("name",aaTranscation.getName());
        }else {
            throw new StatefulException("资产类型错误");
        }
        map.put("applier",aaTranscation.getApplier());
        map.put("spuAmount",aaTranscation.getSpuAmount());
        map.put("deptName",deptMap.get(aaTranscation.getDeptId()));
        map.put("operator",aaTranscation.getOperator());
        map.put("model",aaTranscation.getModel());
        map.put("unit",aaTranscation.getUnit());
        map.put("createTime", DateUtil.format(aaTranscation.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));

        return map;
    }



    private List<String> getUrl(String path) {
        if(StrUtil.isBlank(path)){
            return new ArrayList<>();
        }
        String[] split = path.split(";");
        List<String> pic_path_list = new ArrayList<>(split.length);
        for (String s : split) {
            if(StrUtil.isBlank(s)){
                continue;
            }
            String url = remoteFileService.getFileUrls("inventory", s.split("inventory/")[1]);
            pic_path_list.add(url);
        }
        return pic_path_list;
    }
    private String getTransType(Integer transType){

        // 1直接入库,2采购入库,3直接出库,4领用出库,5报废出库,6退换货出库,7盘盈入库,8盘亏出库
        switch (transType){
            case 1:
                return  "直接入库";
            case 2:
                return "采购入库";
            case 3:
                return  "直接出库";
            case 4:
                return "领用出库";
            case 5:
                return  "报废出库";
            case 6:
                return "退货出库";
            case 7:
                return  "盘盈入库";
            case 8:
                return "盘亏出库";
            default:
                return "";
        }

    }

}
