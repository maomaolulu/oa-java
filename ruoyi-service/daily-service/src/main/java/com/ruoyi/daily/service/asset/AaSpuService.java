package com.ruoyi.daily.service.asset;


import com.ruoyi.daily.domain.asset.AaSku;
import com.ruoyi.daily.domain.asset.AaSpu;

import java.util.List;

/**
 * 品类管理
 * @author zx
 * @date 2022-08-17 15:22:29
 */
public interface AaSpuService {
    /**
     * 保存品类
     * @param aaSpu
     */
    void save(AaSpu aaSpu);

    /**
     * 编辑品类
     * @param aaSpu
     */
    void update(AaSpu aaSpu);

    /**
     * 获取品类详情
     * @param id
     * @return
     */
    AaSpu getInfo(Long id);

    /**
     * 获取流动资产列表
     * @param aaSpu
     * @return
     */
    List<AaSpu> getList(AaSpu aaSpu);

    /**
     * 耗材直接入库
     * @param aaSku
     */
    void saveSku(AaSku aaSku);

    /**
     * 获取直接入库批次列表
     * @param aaSku
     * @return
     */
    List<AaSku> getSkuList(AaSku aaSku,int isExcel);

    /**
     * 流动资产导出
     * @param aaSpu
     * @return
     */
    List<AaSpu> getListExcel(AaSpu aaSpu);
}
