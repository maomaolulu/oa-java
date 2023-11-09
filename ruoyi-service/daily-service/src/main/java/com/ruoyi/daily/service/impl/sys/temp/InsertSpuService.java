package com.ruoyi.daily.service.impl.sys.temp;

import com.ruoyi.daily.domain.asset.AaSpu;
import com.ruoyi.daily.domain.asset.dto.AaSkuTestDto;
import com.ruoyi.daily.domain.sys.temp.InsertSpu;
import com.ruoyi.daily.mapper.asset.AaSpuMapper;
import com.ruoyi.daily.mapper.asset.AssetMapper;
import com.ruoyi.daily.mapper.sys.temp.InsertSpuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by WuYang on 2022/8/29 11:58
 */
@Service
public class InsertSpuService {

    @Resource
    InsertSpuMapper insertSpuMapper;

    @Resource
    AssetMapper assetMapper;


    public void insertSpu() {
        // 统计入库和出库的差 ----> 根据spu_id 分组
        List<InsertSpu> list = insertSpuMapper.getList();
        // 遍历更新库存数值
        for (InsertSpu e : list) {
            System.out.println(e.getSpuId()+"________"+e.getNumber());

            Integer number = e.getNumber() < 0 ?0:e.getNumber();
            insertSpuMapper.updateStorageNumById(e.getSpuId(),number);
        }
        // 单独处理当前库存可能为NULL情况
        List<AaSpu> aaSpus = insertSpuMapper.selectNull();
        aaSpus.forEach(e ->{
            e.setStorageNum(0);
          insertSpuMapper.updateStorageNumById(e.getId(),0);
        });

    }
    @Transactional(rollbackFor = Exception.class)
    public void updateSkuId(){
        List<AaSkuTestDto> aaSkuTestInfo = assetMapper.getAaSkuTestInfo();
        Long a = 1090L;
        for (AaSkuTestDto aaSkuTestDto : aaSkuTestInfo) {
            System.out.println(aaSkuTestDto.toString());
            System.out.println(a);
            assetMapper.updateId(aaSkuTestDto.getId(),a);
            assetMapper.updateIdentifier(aaSkuTestDto.getId(),a);
            a++;


        }
    }


}
