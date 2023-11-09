package com.ruoyi.activiti.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.activiti.domain.asset.AaSku;
import com.ruoyi.activiti.domain.asset.AaSpu;
import com.ruoyi.activiti.domain.asset.Asset;
import com.ruoyi.activiti.domain.purchase.BizGoodsRejectedDetail;
import com.ruoyi.activiti.mapper.AaSkuMapper;
import com.ruoyi.activiti.mapper.AaSpuMapper;
import com.ruoyi.activiti.mapper.BizGoodsRejectedDetailMapper;
import com.ruoyi.activiti.service.AssetService;
import com.ruoyi.activiti.service.BizGoodsRejectedDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ruoyi.common.utils.MapUtil.toMap;


/**
 * lx
 * 22/01/14
 */
@Service
public class BizGoodsRejectedDetailServiceImpl extends ServiceImpl<BizGoodsRejectedDetailMapper, BizGoodsRejectedDetail> implements BizGoodsRejectedDetailService {
    private final AssetService assetService;
    private final AaSkuMapper aaSkuMapper;
    private final AaSpuMapper aaSpuMapper;

    @Autowired
    public BizGoodsRejectedDetailServiceImpl(AssetService assetService, AaSkuMapper aaSkuMapper, AaSpuMapper aaSpuMapper) {
        this.assetService = assetService;
        this.aaSkuMapper = aaSkuMapper;
        this.aaSpuMapper = aaSpuMapper;
    }


    /**
     * 通过退货信息ID获取具体信息列表
     *
     * @param goodsRejectedId
     * @return
     */
    @Override
    public List<BizGoodsRejectedDetail> getListByGoodsId(Long goodsRejectedId) {
        List<BizGoodsRejectedDetail> list = baseMapper.selectList(
                new QueryWrapper<BizGoodsRejectedDetail>()
                        .eq("goods_rejected_id", goodsRejectedId));
        list.stream().forEach(detail -> {
            Long assertId = detail.getAssertId();
            if (1 == detail.getItemType()) {
                Asset byId = assetService.getById(assertId);
                detail.setAsset(byId);
                detail.setInfoMap(toMap(byId));
            }
            if (2 == detail.getItemType()) {
                AaSku aaSku = aaSkuMapper.selectById(assertId);
                AaSpu aaSpu = aaSpuMapper.selectByPrimaryKey(aaSku.getSpuId());
                aaSku.setAaSpu(aaSpu);
                detail.setAaSku(aaSku);
                detail.setInfoMap(toMap(aaSku));
            }
        });
        return list;
    }


}
