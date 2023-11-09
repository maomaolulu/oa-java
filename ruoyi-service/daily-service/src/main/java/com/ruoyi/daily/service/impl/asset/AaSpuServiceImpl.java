package com.ruoyi.daily.service.impl.asset;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.daily.domain.asset.AaSku;
import com.ruoyi.daily.domain.asset.AaSpu;
import com.ruoyi.daily.domain.asset.AaTranscation;
import com.ruoyi.daily.mapper.asset.AaSkuMapper;
import com.ruoyi.daily.mapper.asset.AaSpuMapper;
import com.ruoyi.daily.mapper.asset.AaTranscationMapper;
import com.ruoyi.daily.service.asset.AaSpuService;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysDictData;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.daily.feign.RemoteDictService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.DataScopeUtil;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 品类管理
 * @author zx
 * @date 2022-08-17 15:21:16
 */
@Service
public class AaSpuServiceImpl implements AaSpuService{
    private final AaSpuMapper aaSpuMapper;
    private final AaSkuMapper aaSkuMapper;
    private final RemoteDeptService remoteDeptService;
    private final RemoteDictService remoteDictService;
    private final DataScopeUtil dataScopeUtil;
    private final RemoteUserService remoteUserService;
    private final RedisService redisUtils;
    private final AaTranscationMapper aaTranscationMapper;
    @Autowired
    public AaSpuServiceImpl(AaSpuMapper aaSpuMapper, AaSkuMapper aaSkuMapper, RemoteDeptService remoteDeptService, RemoteDictService remoteDictService, DataScopeUtil dataScopeUtil, RemoteUserService remoteUserService, RedisService redisUtils, AaTranscationMapper aaTranscationMapper) {
        this.aaSpuMapper = aaSpuMapper;
        this.aaSkuMapper = aaSkuMapper;
        this.remoteDeptService = remoteDeptService;
        this.remoteDictService = remoteDictService;
        this.dataScopeUtil = dataScopeUtil;
        this.remoteUserService = remoteUserService;
        this.redisUtils = redisUtils;
        this.aaTranscationMapper = aaTranscationMapper;
    }

    /**
     * 保存品类
     *
     * @param aaSpu
     */
    @Override
    public void save(AaSpu aaSpu) {

        aaSpu.setCreateTime(new Date());
        aaSpu.setCreateBy(SystemUtil.getUserNameCn());
        aaSpu.setStorageNum(0);
        aaSpuMapper.insert(aaSpu);
        aaSpu.setSpuSn("HC"+String.format("%05d", aaSpu.getId()));
        aaSpuMapper.updateById(aaSpu);
    }

    /**
     * 编辑品类
     *
     * @param aaSpu
     */
    @Override
    public void update(AaSpu aaSpu) {
        aaSpu.setUpdateTime(new Date());
        aaSpu.setUpdateBy(SystemUtil.getUserNameCn());
        aaSpuMapper.updateById(aaSpu);
    }

    /**
     * 获取品类详情
     *
     * @param id
     * @return
     */
    @Override
    public AaSpu getInfo(Long id) {
        AaSpu aaSpu = aaSpuMapper.selectById(id);
        SysDept sysDept = remoteDeptService.selectSysDeptByDeptId(aaSpu.getCompanyId());
        aaSpu.setCompanyName(sysDept.getDeptName());
        List<SysDictData> assetsType = remoteDictService.getType("assets_type");

        for (SysDictData sysDictData : assetsType) {
            if(sysDictData.getDictCode().equals(aaSpu.getSpuType())){
                aaSpu.setSpuTypeName(sysDictData.getDictLabel());
            }
        }
        return aaSpu;
    }

    /**
     * 获取流动资产列表
     *
     * @param aaSpu
     * @return
     */
    @Override
    public List<AaSpu> getList(AaSpu aaSpu) {
        List<AaSpu> aaSpus = getAaSpus(aaSpu);
        for (AaSpu spu : aaSpus) {
            // 当前库存显示为 实际库存-直接领用待确认数量-领用申请未通过数量
            Optional<Integer> number = aaSpuMapper.getNumber(spu.getId());
            Integer num = 0;
            if(number.isPresent()){
                num = number.get();
            }

            Optional<Integer> numberApply = aaSpuMapper.getNumberApply(spu.getId());
            Integer numApply = 0;
            if(numberApply.isPresent()){
                numApply = numberApply.get();
            }
            spu.setStorageNum(spu.getStorageNum()-num-numApply);
            // 数量小于0显示0
            if(spu.getStorageNum()<0){
                spu.setStorageNum(0);
            }
        }
        return aaSpus;
    }

    /**
     * 流动资产导出
     *
     * @param aaSpu
     * @return
     */
    @Override
    public List<AaSpu> getListExcel(AaSpu aaSpu) {
        List<AaSpu> aaSpus = getAaSpus(aaSpu);
        return aaSpus;
    }

    private List<AaSpu> getAaSpus(AaSpu aaSpu) {
        QueryWrapper<AaSpu> wrapper = new QueryWrapper<>();
        wrapper.eq(aaSpu.getSpuType()!=null,"a.spu_type", aaSpu.getSpuType());
        wrapper.like(StrUtil.isNotBlank(aaSpu.getName()),"a.name", aaSpu.getName());
        wrapper.like(StrUtil.isNotBlank(aaSpu.getSpuSn()),"a.spu_sn", aaSpu.getSpuSn());
        wrapper.eq(aaSpu.getCompanyId()!=null,"a.company_id", aaSpu.getCompanyId());
        SysUser user = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
        String sql = dataScopeUtil.getScopeSql(user, "d", null);
        if(StrUtil.isNotBlank(sql)){
            sql=sql.substring(0,sql.lastIndexOf(")"));
            sql += " OR a.company_id = "+SystemUtil.getCompanyId()+")";
            wrapper.apply(sql);
        }
        List<AaSpu> aaSpus = aaSpuMapper.selectSpuList(wrapper);
        List<SysDictData> assetsType = remoteDictService.getType("assets_type");
        Map<Long, String> deptMap = remoteDeptService.listEnable2Map();

        Map<Long, String> maps = assetsType.stream().collect(Collectors.toMap(SysDictData::getDictCode, SysDictData::getDictLabel));
        for (AaSpu spu : aaSpus) {
            spu.setSpuTypeName(maps.get(spu.getSpuType().longValue()));
            spu.setCompanyName(deptMap.get(spu.getCompanyId()));
        }
        return aaSpus;
    }
    public static void main(String[] args) {

        String fileName="(89989897)";

        String newFileName=fileName.substring(0,fileName.lastIndexOf(")"));

        System.out.println("最后一个文件名为=====>"+newFileName);

    }

    /**
     * 耗材直接入库
     *
     * @param aaSku
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public void saveSku(AaSku aaSku) {
        AaSpu aaSpu = aaSpuMapper.selectById(aaSku.getSpuId());
        Date date = new Date();
        aaSku.setCreateTime(date);
        aaSku.setUpdateTime(date);
        String userNameCn = SystemUtil.getUserNameCn();
        aaSku.setCreateBy(userNameCn);
        aaSku.setUpdateBy(userNameCn);
        aaSku.setOperation(1);
        aaSku.setCompanyId(SystemUtil.getCompanyId());
        aaSkuMapper.insert(aaSku);
        // 保存出入库记录
        AaTranscation aaTranscation = new AaTranscation();
        aaTranscation.setModel(aaSpu.getModel());
        aaTranscation.setName(aaSpu.getName());
        aaTranscation.setCreateTime(date);
        aaTranscation.setCreateBy(SystemUtil.getUserNameCn());
        aaTranscation.setAmount(aaSku.getAmount().intValue());
        aaTranscation.setUnit(aaSku.getUnit());
        // 原spu_id 2021-12-28 李小龙更改为sku_id
        aaTranscation.setIdentifier(aaSku.getId());
        aaTranscation.setItemSn(aaSpu.getSpuSn());
        aaTranscation.setTransType(1);
        if(aaSku.getTransTypeName()!=null){
            aaTranscation.setTransType(Integer.valueOf(aaSku.getTransTypeName()));
        }
        aaTranscation.setCompanyId(aaSpu.getCompanyId());
        // 申请人部门
        aaTranscation.setDeptId(SystemUtil.getDeptId());
        aaTranscation.setItemType(2);
        // 经办人
        aaTranscation.setOperator(SystemUtil.getUserNameCn());
        //  入库求和减去出库求和
        aaTranscation.setSpuAmount(aaSpu.getStorageNum());
        aaTranscationMapper.insert(aaTranscation);
        // 更新当前库存
        aaSpu.setStorageNum(aaSpu.getStorageNum()+aaSku.getAmount().intValue());
        // 更新采购均价,原来为0则直接赋值
        if(aaSpu.getPrice().compareTo(BigDecimal.ZERO)==0){
            aaSpu.setPrice(aaSku.getPurchasePrice());
        }else {
            aaSpu.setPrice((aaSpu.getPrice().add(aaSku.getPurchasePrice())).divide(new BigDecimal(2)));
        }
        aaSpuMapper.updateById(aaSpu);

    }

    /**
     * 获取直接入库批次列表
     *
     * @param aaSku
     * @return
     */
    @Override
    public List<AaSku> getSkuList(AaSku aaSku,int isExcel) {
        QueryWrapper<AaSku> wrapper = new QueryWrapper<>();
        wrapper.eq("k.operation",1);
        wrapper.like(StrUtil.isNotBlank(aaSku.getSpuTypeName()),"p.name",aaSku.getSpuTypeName());
        wrapper.like(StrUtil.isNotBlank(aaSku.getName()),"k.name",aaSku.getName());
        wrapper.between(StrUtil.isNotBlank(aaSku.getStartDate())&&StrUtil.isNotBlank(aaSku.getEndDate()),"k.purchase_time",aaSku.getStartDate()+" 00:00:00",aaSku.getEndDate()+" 23:59:59");
        SysUser user = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
        String sql = dataScopeUtil.getScopeSql(user, "d", null);
        if(StrUtil.isNotBlank(sql)){
            wrapper.apply(sql);
        }
        List<AaSku> skuList;
        if(isExcel ==1){
            skuList = aaSkuMapper.getSkuListExcel(wrapper);
        }else {
            skuList = aaSkuMapper.getSkuList(wrapper);
        }
        skuList.stream().forEach(k->{
            k.setSpuTypeName(redisUtils.getCacheObject("dict:"+k.getSpuType()));
        });
        return skuList;
    }

}
