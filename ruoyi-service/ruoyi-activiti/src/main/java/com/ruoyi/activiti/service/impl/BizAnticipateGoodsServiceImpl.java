package com.ruoyi.activiti.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.activiti.domain.purchase.BizAnticipateGoods;
import com.ruoyi.activiti.mapper.BizAnticipateGoodsMapper;
import com.ruoyi.activiti.service.BizAnticipateGoodsService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 采购预提交
 *
 * @author zh
 * @date 2021-02-10
 */
@Service
@Slf4j
public class BizAnticipateGoodsServiceImpl implements BizAnticipateGoodsService {
    private final BizAnticipateGoodsMapper bizAnticipateGoodsMapper;

    @Autowired
    public BizAnticipateGoodsServiceImpl( BizAnticipateGoodsMapper bizAnticipateGoodsMapper) {
        this.bizAnticipateGoodsMapper = bizAnticipateGoodsMapper;

    }


    @Override
    public List<BizAnticipateGoods> listAll(BizAnticipateGoods bizAnticipateGoods) {
        //        SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
        QueryWrapper<BizAnticipateGoods> bizAnticipateGoodsQueryWrapper = new QueryWrapper<>();
        //物品名称
        bizAnticipateGoodsQueryWrapper.like(StrUtil.isNotBlank(bizAnticipateGoods.getName()),"name",bizAnticipateGoods.getName());

        bizAnticipateGoodsQueryWrapper.eq("create_by",SystemUtil.getUserId());
        bizAnticipateGoodsQueryWrapper.eq("del_flag",0);
        //登记编号
        bizAnticipateGoodsQueryWrapper.like(StrUtil.isNotBlank(bizAnticipateGoods.getGoodsType()),"goods_type",bizAnticipateGoods.getGoodsType());
        //0:固定资产，1.耗材
        bizAnticipateGoodsQueryWrapper.eq("asset_type",bizAnticipateGoods.getAssetType());
        List<BizAnticipateGoods> bizAnticipateGoods1 = bizAnticipateGoodsMapper.listAll(bizAnticipateGoodsQueryWrapper);
        return bizAnticipateGoods1;
    }

    @Override
    public BizAnticipateGoods updateById(BizAnticipateGoods bizAnticipateGoods) {
        bizAnticipateGoodsMapper.updateById(bizAnticipateGoods);
        return bizAnticipateGoods;
    }
    @Override
    public BizAnticipateGoods saveBizAnticipateGoods(List<BizAnticipateGoods> bizAnticipateGoodsList) {
        for (BizAnticipateGoods bizAnticipateGoods:bizAnticipateGoodsList
             ) {
            bizAnticipateGoods.setCreateBy(SystemUtil.getUserId());
            bizAnticipateGoods.setCreateTime(new Date());
            bizAnticipateGoods.setDelFlag("0");
            bizAnticipateGoodsMapper.insert(bizAnticipateGoods);
        }

        return new BizAnticipateGoods();
    }
    /**
     * 删除
     * */
    @Override
    public  String deleteBizAnticipateGoods( Long[] ids ){
        for (Long l:ids
             ) {
            BizAnticipateGoods bizAnticipateGoods = new BizAnticipateGoods();
            bizAnticipateGoods.setDelFlag("1");
            bizAnticipateGoods.setId(l);
            bizAnticipateGoodsMapper.updateById(bizAnticipateGoods);
        }
        return null;
    }
}
