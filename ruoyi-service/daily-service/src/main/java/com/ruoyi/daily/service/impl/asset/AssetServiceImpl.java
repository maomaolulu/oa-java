package com.ruoyi.daily.service.impl.asset;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.utils.RandomUtil;
import com.ruoyi.daily.domain.asset.AaDeviceClassify;
import com.ruoyi.daily.domain.asset.AaTranscation;
import com.ruoyi.daily.domain.asset.Asset;
import com.ruoyi.daily.domain.asset.dto.*;
import com.ruoyi.daily.mapper.asset.AaDeviceClassifyMapper;
import com.ruoyi.daily.mapper.asset.AssetMapper;
import com.ruoyi.daily.service.asset.AaTranscationService;
import com.ruoyi.daily.service.asset.AssetService;
import com.ruoyi.file.feign.RemoteFileService;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.DataScopeUtil;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 固定资产
 *
 * @author zx
 * @date 2022/3/15 18:15
 */
@Service
@Slf4j
public class AssetServiceImpl implements AssetService {
    private final AssetMapper assetMapper;
    private final DataScopeUtil dataScopeUtil;
    private final RemoteUserService remoteUserService;
    private final RemoteDeptService remoteDeptService;
    private final AaTranscationService transcationService;
    private final RemoteFileService remoteFileService;
    private final AaDeviceClassifyMapper deviceClassifyMapper;

    @Autowired
    public AssetServiceImpl(AssetMapper assetMapper, DataScopeUtil dataScopeUtil, RemoteUserService remoteUserService, RemoteDeptService remoteDeptService, AaTranscationService transcationService, RemoteFileService remoteFileService, AaDeviceClassifyMapper deviceClassifyMapper) {
        this.assetMapper = assetMapper;
        this.dataScopeUtil = dataScopeUtil;
        this.remoteUserService = remoteUserService;
        this.remoteDeptService = remoteDeptService;
        this.transcationService = transcationService;
        this.remoteFileService = remoteFileService;
        this.deviceClassifyMapper = deviceClassifyMapper;
    }

    /**
     * 查询列表
     *
     * @param assetListDto
     * @return
     */
    @Override
    public List<Map<String, Object>> getLsit(AssetListDto assetListDto) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.like(StrUtil.isNotBlank(assetListDto.getAsset_sn()), "a.asset_sn", assetListDto.getAsset_sn());
        wrapper.like(StrUtil.isNotBlank(assetListDto.getName()), "a.name", assetListDto.getName());
        wrapper.like(StrUtil.isNotBlank(assetListDto.getKeyword()), "a.name", assetListDto.getKeyword());
        wrapper.eq(assetListDto.getAsset_type() != null, "a.asset_type", assetListDto.getAsset_type());
        wrapper.eq(assetListDto.getState() != null, "a.state", assetListDto.getState());
        wrapper.eq(assetListDto.getDept_id() != null, "a.dept_id", assetListDto.getDept_id());
        wrapper.eq(assetListDto.getCompany_id() != null, "a.company_id", assetListDto.getCompany_id());
        wrapper.like(StrUtil.isNotBlank(assetListDto.getCharger()), "a.charger", assetListDto.getCharger());
        wrapper.like(StrUtil.isNotBlank(assetListDto.getKeeper()), "a.keeper", assetListDto.getKeeper());
        wrapper.eq(assetListDto.getIs_labelled() != null, "a.is_labelled", assetListDto.getIs_labelled());
        wrapper.eq(assetListDto.getHas_pic() != null, "a.has_pic", assetListDto.getHas_pic());
        wrapper.eq(StringUtils.isNotBlank(assetListDto.getCategory_id()), "a.category_id", assetListDto.getCategory_id());
        wrapper.like(StringUtils.isNotBlank(assetListDto.getCategory_name()), "ac.category_name", assetListDto.getCategory_name());
        wrapper.ne("state", 9);
        wrapper.between(StrUtil.isNotBlank(assetListDto.getStart_date()) && StrUtil.isNotBlank(assetListDto.getEnd_date()), "a.purchase_time", DateUtil.parse(assetListDto.getStart_date()), DateUtil.parse(assetListDto.getEnd_date()));
        wrapper.orderByDesc("a.create_time");
        Long userId = SystemUtil.getUserId();
        if (userId == null) {
            userId = 1L;
        }
        SysUser user = remoteUserService.selectSysUserByUserId(userId);
        String sql = dataScopeUtil.getScopeSql(user, "d", null);
        if (StrUtil.isNotBlank(sql)) {
            wrapper.apply(sql);
        }
        List<Map<String, Object>> maps = assetMapper.selectMaps(wrapper);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        Map<Long, String> deptMap = remoteDeptService.listEnable2Map();
        maps.stream().forEach(m -> {
            // 如果是仪器设备
            if (m.get("asset_type").toString().equals("53")) {
                List<Map<String, Object>> typeList = assetMapper.getDeviceTypeList((Long) m.get("id"));
                if (!typeList.isEmpty()) {
                    Map<String, Object> map = typeList.get(0);
                    m.put("device_type", map.get("device_type"));
                    m.put("dict_key", map.get("dict_key"));
                    m.put("device_type_name", map.get("device_type_name"));
                    m.put("dict_key_name", map.get("dict_key_name"));
                } else {
                    m.put("device_type", "");
                    m.put("dict_key", "");
                    m.put("device_type_name", "");
                    m.put("dict_key_name", "");
                }
            }
            m.put("dept_name", deptMap.get(Long.valueOf(m.get("dept_id").toString())));
            if (m.containsKey("is_instrument") && Boolean.valueOf(m.get("is_instrument").toString())) {
                m.put("is_instrument", "1");
            } else {
                m.put("is_instrument", "0");
            }
            if (m.containsKey("is_inspected") && Boolean.valueOf(m.get("is_inspected").toString())) {
                m.put("is_inspected", "1");

            } else {
                m.put("is_inspected", "0");

            }
            if (m.containsKey("is_virtual") && Boolean.valueOf(m.get("is_virtual").toString())) {
                m.put("is_virtual", "1");
            } else {
                m.put("is_virtual", "0");
            }
            if (m.containsKey("is_labelled") && Boolean.valueOf(m.get("is_labelled").toString())) {
                m.put("is_labelled", "1");
            } else {
                m.put("is_labelled", "0");
            }
            if (m.containsKey("purchase_time")) {
                m.put("purchase_time", df2.format(m.get("purchase_time")));
            }
            if (m.containsKey("create_time")) {
                m.put("create_time", df.format(m.get("create_time")));
            }
            if (m.containsKey("update_time")) {
                m.put("update_time", df.format(m.get("update_time")));
            }
            if (m.containsKey("arrive_time")) {
                m.put("arrive_time", df2.format(m.get("arrive_time")));
            }
            if (m.containsKey("inspect_time")) {
                m.put("inspect_time", df.format(m.get("inspect_time")));
            }
            if (m.containsKey("next_inspect_time")) {
                m.put("next_inspect_time", df.format(m.get("next_inspect_time")));
            }
            if (m.containsKey("check_time")) {
                m.put("check_time", df2.format(m.get("check_time")));
            }
            if (m.containsKey("expire_time")) {
                m.put("expire_time", df.format(m.get("expire_time")));
            }

            //处理品类
            QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("t1.asset_type", 1);
            queryWrapper.eq("t2.asset_type_id", m.get("asset_type"));
            queryWrapper.eq("t2.id", m.get("category_id"));
            Map<String, String> map = assetMapper.selectMap(queryWrapper);
            if (CollUtil.isNotEmpty(map)) {
                String asset_type_name = map.get("asset_type_name");
                String category_name = map.get("category_name");
                if (StrUtil.isNotBlank(asset_type_name)) {
                    m.put("asset_type_name", asset_type_name);
                }
                if (StrUtil.isNotBlank(category_name)) {
                    m.put("category_name", category_name);
                }
            }
            // 申请编号
            m.putIfAbsent("purchase_code", "");
        });
        if (assetListDto.getExcel() == 1) {
            maps.stream().forEach(m -> {
                if (m.containsKey("is_inspected") && "1".equals(m.get("is_inspected").toString())) {
                    m.put("is_inspected_name", "是");
                } else {
                    m.put("is_inspected_name", "否");
                }
            });
        } else {
            maps.stream().forEach(m -> {
                if (m.containsKey("pic_path") && StrUtil.isNotBlank(m.get("pic_path").toString())) {
                    String pic_path = m.get("pic_path").toString();
                    List<String> pic_path_list = getUrl(pic_path);
                    m.put("pic_path_list", pic_path_list);
                } else {
                    m.put("pic_path_list", new ArrayList<>());
                }
                if (m.containsKey("att_path") && StrUtil.isNotBlank(m.get("att_path").toString())) {
                    String pic_path = m.get("att_path").toString();
                    List<String> pic_path_list = getUrl(pic_path);
                    m.put("att_path_list", pic_path_list);
                } else {
                    m.put("att_path_list", new ArrayList<>());
                }
                if (m.containsKey("cert_path") && StrUtil.isNotBlank(m.get("cert_path").toString())) {
                    String pic_path = m.get("cert_path").toString();
                    List<String> pic_path_list = getUrl(pic_path);
                    m.put("cert_path_list", pic_path_list);
                } else {
                    m.put("cert_path_list", new ArrayList<>());
                }
            });
        }
        return maps;
    }

    /**
     * 查询列表 --- 在移交固定资产中使用
     *
     * @param assetListDto
     * @return
     */
    @Override
    public List<Map<String, Object>> getList(AssetListDto assetListDto) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.like(StrUtil.isNotBlank(assetListDto.getAsset_sn()), "a.asset_sn", assetListDto.getAsset_sn());
        wrapper.like(StrUtil.isNotBlank(assetListDto.getName()), "a.name", assetListDto.getName());
        wrapper.like(StrUtil.isNotBlank(assetListDto.getKeyword()), "a.name", assetListDto.getKeyword());
        wrapper.eq(assetListDto.getAsset_type() != null, "a.asset_type", assetListDto.getAsset_type());
        wrapper.eq(assetListDto.getState() != null, "a.state", assetListDto.getState());
        wrapper.eq(assetListDto.getDept_id() != null, "a.dept_id", assetListDto.getDept_id());
        wrapper.eq(assetListDto.getCompany_id() != null, "a.company_id", assetListDto.getCompany_id());
        wrapper.like(StrUtil.isNotBlank(assetListDto.getCharger()), "a.charger", assetListDto.getCharger());
        wrapper.like(StrUtil.isNotBlank(assetListDto.getKeeper()), "a.keeper", assetListDto.getKeeper());
        wrapper.eq(assetListDto.getIs_labelled() != null, "a.is_labelled", assetListDto.getIs_labelled());
        wrapper.eq(assetListDto.getHas_pic() != null, "a.has_pic", assetListDto.getHas_pic());
        wrapper.eq(assetListDto.getCategory_id() != null, "a.category_id", assetListDto.getCategory_id());
        wrapper.ne("state", 9);
        wrapper.ne("state", 6);
        wrapper.like(StrUtil.isNotBlank(assetListDto.getCategory_name()), "ac.category_name", assetListDto.getCategory_name());
        wrapper.between(StrUtil.isNotBlank(assetListDto.getStart_date()) && StrUtil.isNotBlank(assetListDto.getEnd_date()), "a.purchase_time", DateUtil.parse(assetListDto.getStart_date()), DateUtil.parse(assetListDto.getEnd_date()));
        wrapper.orderByDesc("a.create_time");
        Long userId = SystemUtil.getUserId();
        if (userId == null) {
            userId = 1L;
        }
        SysUser user = remoteUserService.selectSysUserByUserId(userId);
        String sql = dataScopeUtil.getScopeSql(user, "d", null);
        if (StrUtil.isNotBlank(sql)) {
            wrapper.apply(sql);
        }
        List<Map<String, Object>> maps = assetMapper.selectMaps(wrapper);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        Map<Long, String> deptMap = remoteDeptService.listEnable2Map();
        maps.stream().forEach(m -> {
            m.put("dept_name", deptMap.get(Long.valueOf(m.get("dept_id").toString())));
            if (m.containsKey("is_instrument") && Boolean.valueOf(m.get("is_instrument").toString())) {
                m.put("is_instrument", "1");
            } else {
                m.put("is_instrument", "0");
            }
            if (m.containsKey("is_inspected") && Boolean.valueOf(m.get("is_inspected").toString())) {
                m.put("is_inspected", "1");

            } else {
                m.put("is_inspected", "0");

            }
            if (m.containsKey("is_virtual") && Boolean.valueOf(m.get("is_virtual").toString())) {
                m.put("is_virtual", "1");
            } else {
                m.put("is_virtual", "0");
            }
            if (m.containsKey("is_labelled") && Boolean.valueOf(m.get("is_labelled").toString())) {
                m.put("is_labelled", "1");
            } else {
                m.put("is_labelled", "0");
            }
            if (m.containsKey("purchase_time")) {
                m.put("purchase_time", df2.format(m.get("purchase_time")));
            }
            if (m.containsKey("create_time")) {
                m.put("create_time", df.format(m.get("create_time")));
            }
            if (m.containsKey("update_time")) {
                m.put("update_time", df.format(m.get("update_time")));
            }
            if (m.containsKey("arrive_time")) {
                m.put("arrive_time", df2.format(m.get("arrive_time")));
            }
            if (m.containsKey("inspect_time")) {
                m.put("inspect_time", df.format(m.get("inspect_time")));
            }
            if (m.containsKey("next_inspect_time")) {
                m.put("next_inspect_time", df.format(m.get("next_inspect_time")));
            }
            if (m.containsKey("check_time")) {
                m.put("check_time", df2.format(m.get("check_time")));
            }
            if (m.containsKey("expire_time")) {
                m.put("expire_time", df.format(m.get("expire_time")));
            }
            //处理品类
            QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("t1.asset_type", 1);
            queryWrapper.eq("t2.asset_type_id", m.get("asset_type"));
            queryWrapper.eq("t2.id", m.get("category_id"));
            Map<String, String> map = assetMapper.selectMap(queryWrapper);
            if (CollUtil.isNotEmpty(map)) {
                String asset_type_name = map.get("asset_type_name");
                String category_name = map.get("category_name");
                if (StrUtil.isNotBlank(asset_type_name)) {
                    m.put("asset_type_name", asset_type_name);
                }
                if (StrUtil.isNotBlank(category_name)) {
                    m.put("category_name", category_name);
                }
            }
        });
        if (assetListDto.getExcel() == 1) {
            maps.stream().forEach(m -> {
                if (m.containsKey("is_inspected") && "1".equals(m.get("is_inspected").toString())) {
                    m.put("is_inspected_name", "是");
                } else {
                    m.put("is_inspected_name", "否");
                }
            });
        } else {
            maps.stream().forEach(m -> {
                if (m.containsKey("pic_path") && StrUtil.isNotBlank(m.get("pic_path").toString())) {
                    String pic_path = m.get("pic_path").toString();
                    List<String> pic_path_list = getUrl(pic_path);
                    m.put("pic_path_list", pic_path_list);
                } else {
                    m.put("pic_path_list", new ArrayList<>());
                }
                if (m.containsKey("att_path") && StrUtil.isNotBlank(m.get("att_path").toString())) {
                    String pic_path = m.get("att_path").toString();
                    List<String> pic_path_list = getUrl(pic_path);
                    m.put("att_path_list", pic_path_list);
                } else {
                    m.put("att_path_list", new ArrayList<>());
                }
                if (m.containsKey("cert_path") && StrUtil.isNotBlank(m.get("cert_path").toString())) {
                    String pic_path = m.get("cert_path").toString();
                    List<String> pic_path_list = getUrl(pic_path);
                    m.put("cert_path_list", pic_path_list);
                } else {
                    m.put("cert_path_list", new ArrayList<>());
                }
            });
        }
        return maps;
    }

    private List<String> getUrl(String path) {
        String[] split = path.split(";");
        List<String> pic_path_list = new ArrayList<>(split.length);
        for (String s : split) {
            if (StrUtil.isBlank(s)) {
                continue;
            }
            try {
                String url = remoteFileService.getFileUrls("inventory", s.split("inventory/")[1]);
                pic_path_list.add(url);
            } catch (Exception e) {
                log.error(s);
            }
        }
        return pic_path_list;
    }

    /**
     * 获取物品信息
     *
     * @param dutyAssetDto
     * @return SELECT
     * a.id,
     * CONCAT(c.dept_name,'-',d.dept_name) as belong_dept,
     * sd.dict_label as asset_type,
     * a.asset_sn,
     * a.name ,
     * a.model,
     * a.unit,
     * a.charger,
     * a.keeper,
     * a.purchase_price as single_price
     * FROM
     * aa_asset a
     * LEFT JOIN sys_dept c ON a.company_id = c.dept_id
     * LEFT JOIN sys_dept d ON a.dept_id = d.dept_id
     * LEFT JOIN sys_dict_data sd on sd.dict_code = a.asset_type
     * LEFT JOIN sys_dict_data sd2 on sd2.dict_value = a.state and sd2.dict_type = 'goodStatus'
     */
    @Override
    public List<DutyAssetDto> getDutyAsset(DutyAssetDto dutyAssetDto) {
        QueryWrapper<DutyAssetDto> wrapper = new QueryWrapper();
        // 部门
        wrapper.eq(dutyAssetDto.getDeptId() != null, "a.dept_id", dutyAssetDto.getDeptId());
        // 资产类型
        wrapper.eq(dutyAssetDto.getAssetType() != null, "a.asset_type", dutyAssetDto.getAssetType());
        // 物品名称
        wrapper.like(StrUtil.isNotBlank(dutyAssetDto.getName()), "a.name", dutyAssetDto.getName());
        // 责任人
        wrapper.eq(StrUtil.isNotBlank(dutyAssetDto.getCharger()), "a.charger", dutyAssetDto.getCharger());
        // 保管人
        wrapper.eq(StrUtil.isNotBlank(dutyAssetDto.getKeeper()), "a.keeper", dutyAssetDto.getKeeper());
        // 物品编号
        wrapper.like(StrUtil.isNotBlank(dutyAssetDto.getAssetSn()), "a.asset_sn", dutyAssetDto.getAssetSn());
        return assetMapper.getDutyAsset(wrapper);
    }


    /**
     * 替换责任人
     *
     * @param replaceDutyDto
     */
    @Override
    public void replaceDuty(ReplaceDutyDto replaceDutyDto) {
        assetMapper.replaceDuty(replaceDutyDto.getIds(), replaceDutyDto.getNewCharger());
    }

    /**
     * 离职物品移交
     *
     * @param transferGoodsDTO
     */
    @Override
    public void transferGoods(TransferGoodsDTO transferGoodsDTO) {
        String operator = SystemUtil.getUserName();
        List<Long> assetId = transferGoodsDTO.getAssetId();
        assetId.stream().forEach(asset -> {
            assetMapper.transferGoods(asset, transferGoodsDTO.getUserName(), operator);
        });
    }

    /**
     * 保存固定资产
     *
     * @param asset
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Asset asset) {
        String userNameCn = SystemUtil.getUserNameCn();
        Date date = new Date();
        if (asset.getId() == null) {

            Boolean aBoolean = checkSn(asset.getAssetSn(), asset.getCompanyId());
            if (!aBoolean) {
                throw new StatefulException("资产编号");
            }
            asset.setCompanyId(SystemUtil.getCompanyId());
            asset.setLabelCode(getId());
            asset.setCreateBy(userNameCn);
            asset.setUpdateBy(userNameCn);
            asset.setUpdateTime(date);
            asset.setCreateTime(date);
            if (asset.getTransType() != null && asset.getTransType() == 7) {
                asset.setCheckTime(date);
            }
            int insert = assetMapper.insert(asset);
            if (insert != 1) {
                throw new RuntimeException("保存数据失败");
            }
            // 保存入库记录 直接入库
            AaTranscation aaTranscation = new AaTranscation();
            aaTranscation.setCompanyId(asset.getCompanyId());
            aaTranscation.setDeptId(asset.getDeptId());
            aaTranscation.setName(asset.getName());
            aaTranscation.setModel(asset.getModel());
            aaTranscation.setItemType(1);
            aaTranscation.setItemSn(asset.getAssetSn());
            aaTranscation.setAmount(1);
            aaTranscation.setIdentifier(asset.getId());
            aaTranscation.setSpuAmount(0);
            if (asset.getTransType() != null) {
                aaTranscation.setTransType(asset.getTransType());
            } else {
                aaTranscation.setTransType(1);
            }
            aaTranscation.setOperator(asset.getOperator());
            aaTranscation.setCreateTime(date);
            aaTranscation.setCreateBy(SystemUtil.getUserNameCn());
            aaTranscation.setUpdateTime(date);
            aaTranscation.setUpdateBy(SystemUtil.getUserNameCn());
            transcationService.save(aaTranscation);
            // 仪器设备保存分类
            AaDeviceClassify aaDeviceClassify = new AaDeviceClassify();
            aaDeviceClassify.setAssetId(asset.getId());
            aaDeviceClassify.setDeviceType(asset.getDeviceType());
            aaDeviceClassify.setDictKey(asset.getDictKey());
            aaDeviceClassify.setCreateBy(SystemUtil.getUserName());
            aaDeviceClassify.setCreateTime(new Date());
            deviceClassifyMapper.insert(aaDeviceClassify);
            // 将上传的临时文件转为有效文件
            int reimburse = remoteFileService.update(null, asset.getAssetSn());
            if (reimburse == 0) {
                throw new StatefulException("将上传的文件转为有效文件失败");
            }
        } else {
            asset.setCompanyId(null);
            asset.setUpdateBy(userNameCn);
            asset.setUpdateTime(date);
            int update = assetMapper.updateById(asset);
            if (update != 1) {
                throw new RuntimeException("编辑数据失败");
            }
            // 仪器设备保存分类
            AaDeviceClassify aaDeviceClassifyOld = deviceClassifyMapper.selectOne(new QueryWrapper<AaDeviceClassify>().eq("asset_id", asset.getId()));
            if (aaDeviceClassifyOld != null) {
                aaDeviceClassifyOld.setDeviceType(asset.getDeviceType());
                aaDeviceClassifyOld.setDictKey(asset.getDictKey());
                deviceClassifyMapper.updateById(aaDeviceClassifyOld);
            } else {
                AaDeviceClassify aaDeviceClassify = new AaDeviceClassify();
                aaDeviceClassify.setAssetId(asset.getId());
                aaDeviceClassify.setDeviceType(asset.getDeviceType());
                aaDeviceClassify.setDictKey(asset.getDictKey());
                aaDeviceClassify.setCreateBy(SystemUtil.getUserName());
                aaDeviceClassify.setCreateTime(new Date());
                deviceClassifyMapper.insert(aaDeviceClassify);
            }
            // 将上传的临时文件转为有效文件
            int reimburse = remoteFileService.update(null, asset.getAssetSn());
            if (reimburse == 0) {
                throw new StatefulException("将上传的文件转为有效文件失败");
            }
        }
    }

    /**
     * 固定资产详情
     *
     * @param assetListDto
     * @return
     */
    @Override
    public Map<String, Object> getLsitInfo(AssetListDto assetListDto) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq(assetListDto.getId() != null, "a.id", assetListDto.getId());
        wrapper.eq(assetListDto.getLabelCode() != null, "a.label_code", assetListDto.getLabelCode());
        List<Map<String, Object>> maps = assetMapper.selectMaps(wrapper);
        if (maps.isEmpty()) {
            throw new StatefulException("查无详情");
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        Map<Long, String> deptMap = remoteDeptService.listEnable2Map();
        maps.stream().forEach(m -> {
            // 如果是仪器设备
            if (m.get("asset_type").toString().equals("53")) {
                List<Map<String, Object>> typeList = assetMapper.getDeviceTypeList((Long) m.get("id"));
                if (!typeList.isEmpty()) {
                    Map<String, Object> map = typeList.get(0);
                    m.put("device_type", map.get("device_type"));
                    m.put("dict_key", map.get("dict_key"));
                    m.put("device_type_name", map.get("device_type_name"));
                    m.put("dict_key_name", map.get("dict_key_name"));
                } else {
                    m.put("device_type", "");
                    m.put("dict_key", "");
                    m.put("device_type_name", "");
                    m.put("dict_key_name", "");
                }
            }
            m.put("dept_name", deptMap.get(Long.valueOf(m.get("dept_id").toString())));
            if (m.containsKey("is_instrument") && Boolean.valueOf(m.get("is_instrument").toString())) {
                m.put("is_instrument", "1");
            } else {
                m.put("is_instrument", "0");
            }
            if (m.containsKey("is_inspected") && Boolean.valueOf(m.get("is_inspected").toString())) {
                m.put("is_inspected", "1");

            } else {
                m.put("is_inspected", "0");

            }
            if (m.containsKey("is_virtual") && Boolean.valueOf(m.get("is_virtual").toString())) {
                m.put("is_virtual", "1");
            } else {
                m.put("is_virtual", "0");
            }
            if (m.containsKey("is_labelled") && Boolean.valueOf(m.get("is_labelled").toString())) {
                m.put("is_labelled", "1");
            } else {
                m.put("is_labelled", "0");
            }
            if (m.containsKey("purchase_time")) {
                m.put("purchase_time", df2.format(m.get("purchase_time")));
            }
            if (m.containsKey("create_time")) {
                m.put("create_time", df.format(m.get("create_time")));
            }
            if (m.containsKey("update_time")) {
                m.put("update_time", df.format(m.get("update_time")));
            }
            if (m.containsKey("arrive_time")) {
                m.put("arrive_time", df2.format(m.get("arrive_time")));
            }
            if (m.containsKey("inspect_time")) {
                m.put("inspect_time", df2.format(m.get("inspect_time")));
            }
            if (m.containsKey("next_inspect_time")) {
                m.put("next_inspect_time", df2.format(m.get("next_inspect_time")));
            }
            if (m.containsKey("check_time")) {
                m.put("check_time", df2.format(m.get("check_time")));
            }
            if (m.containsKey("expire_time")) {
                m.put("expire_time", df.format(m.get("expire_time")));
            }

            if (m.containsKey("pic_path") && StrUtil.isNotBlank(m.get("pic_path").toString())) {
                String pic_path = m.get("pic_path").toString();
                List<String> pic_path_list = getUrl(pic_path);
                m.put("pic_path_list", pic_path_list);
            } else {
                m.put("pic_path_list", new ArrayList<>());
            }
            if (m.containsKey("att_path") && StrUtil.isNotBlank(m.get("att_path").toString())) {
                String pic_path = m.get("att_path").toString();
                List<String> pic_path_list = getUrl(pic_path);
                m.put("att_path_list", pic_path_list);
            } else {
                m.put("att_path_list", new ArrayList<>());
            }
            if (m.containsKey("cert_path") && StrUtil.isNotBlank(m.get("cert_path").toString())) {
                String pic_path = m.get("cert_path").toString();
                List<String> pic_path_list = getUrl(pic_path);
                m.put("cert_path_list", pic_path_list);
            } else {
                m.put("cert_path_list", new ArrayList<>());
            }
        });

        // 处理品类
        Map<String, Object> map1 = maps.get(0);
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("t1.asset_type", 1);
        queryWrapper.eq("t2.asset_type_id", map1.get("asset_type"));
        queryWrapper.eq("t2.id", map1.get("category_id"));
        Map<String, String> map = assetMapper.selectMap(queryWrapper);
        if (CollUtil.isNotEmpty(map)) {
            String asset_type_name = map.get("asset_type_name");
            String category_name = map.get("category_name");
            if (StrUtil.isNotBlank(asset_type_name)) {
                map1.put("asset_type_name", asset_type_name);
            }
            if (StrUtil.isNotBlank(category_name)) {
                map1.put("category_name", category_name);
            }
        }
        return map1;
    }

    /**
     * 打印标签
     *
     * @param id 固定资产id
     */
    @Override
    public void print(Long id) {
        assetMapper.print(id);
    }

    private String getId() {
        String s = RandomUtil.randomInt(12);
        int i = assetMapper.countId(s);
        if (i > 0) {
            s = getId();
        }
        return s;
    }

    private Boolean checkSn(String assetSn, Long companyId) {
        int i = assetMapper.countAssetSn(assetSn, companyId);
        if (i > 0) {
            return false;
        }
        return true;
    }


    /**
     * 实验室仪器列表
     *
     * @param labDeviceDto 查询信息
     * @return 仪器列表
     */
    @Override
    public List<Asset> getDeviceList(LabDeviceDto labDeviceDto) {
        // d.dict_type = 'goodStatus' and dc.device_type = 2 and a.dept_id = 139
        QueryWrapper<Asset> wrapper = new QueryWrapper<>();
        wrapper.eq("d.dict_type", "goodStatus");
        wrapper.eq("dc.device_type", 2);
        wrapper.eq("a.asset_type", 53);
        wrapper.eq("a.dept_id", labDeviceDto.getDeptId());
        wrapper.like(StrUtil.isNotBlank(labDeviceDto.getName()), "a.name", labDeviceDto.getName());
        wrapper.like(StrUtil.isNotBlank(labDeviceDto.getModel()), "a.model", labDeviceDto.getModel());
        wrapper.like(StrUtil.isNotBlank(labDeviceDto.getAssetSn()), "a.asset_sn", labDeviceDto.getAssetSn());
        wrapper.like(labDeviceDto.getState() != null, "a.state", labDeviceDto.getState());
        List<Asset> deviceList = assetMapper.getDeviceList(wrapper);
        return deviceList;
    }

    /**
     * 实验室仪器信息
     *
     * @param labDeviceDto
     * @return
     */
    @Override
    public Asset getDeviceInfo(LabDeviceDto labDeviceDto) {
        List<Asset> deviceList = getDeviceList(labDeviceDto);
        if (deviceList.isEmpty()) {
            return null;
        }
        return deviceList.get(0);

    }

    /**
     * 运营2.0仪器设备信息入库
     *
     * @param asset 仪器设备信息
     * @return result
     */
    @Override
    public int add(Asset asset) {
        // 责任人
        Long chargeId = asset.getChargeId();
        Long companyId;
        if (chargeId != null) {
            SysUser sysUser = remoteUserService.selectSysUserByUserId(chargeId);
            if (sysUser != null && sysUser.getUserId() != null) {
                companyId = sysUser.getCompanyId();
                asset.setCompanyId(companyId);
                asset.setDeptId(sysUser.getDeptId());
                asset.setCharger(sysUser.getUserName());
            }
        }

        // 运营2.0新增仪器设备信息
        if (asset.getId() == null) {
            Boolean aBoolean = checkSn(asset.getAssetSn(), asset.getCompanyId());
            if (!aBoolean) {
                return 3;
            }
        }
        asset.setId(null);

        // 是否已检定
        asset.setIsInspected(0);
        // 是否为虚拟资产（默认否，不纳入日常管理）
        asset.setIsVirtual(0);
        // 是否打印标签
        asset.setIsLabelled(0);
        // 是否移交
        asset.setIsTransfer(0);

        QueryWrapper<Asset> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("asset_sn", asset.getAssetSn());
        queryWrapper.eq("company_id", asset.getCompanyId());
        queryWrapper.isNotNull("purchase_code");
        List<Asset> assetList = assetMapper.selectList(queryWrapper);

        // 录入人
        SysUser sysUser;
        String userName = null;
        Long userId = asset.getUserId();
        if (asset.getUserId() != null) {
            sysUser = remoteUserService.selectSysUserByUserId(userId);
            if (sysUser != null && sysUser.getUserId() != null) {
                userName = sysUser.getUserName();
                asset.setOperator(userName);
            }
        }
        if (CollUtil.isNotEmpty(assetList)) {
            // 更新
            asset.setId(assetList.get(0).getId());
            asset.setUpdateBy(userName);
            asset.setUpdateTime(new Date());
            return assetMapper.updateById(asset);
        } else {
            // 新增
            asset.setCreateBy(userName);
            asset.setCreateTime(new Date());
            asset.setUpdateBy(userName);
            asset.setUpdateTime(new Date());
            return assetMapper.insert(asset);
        }
    }
}
