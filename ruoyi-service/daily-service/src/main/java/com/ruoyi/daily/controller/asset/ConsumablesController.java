package com.ruoyi.daily.controller.asset;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.daily.domain.asset.AaSku;
import com.ruoyi.daily.domain.asset.AaSpu;
import com.ruoyi.daily.service.asset.AaSpuService;
import com.ruoyi.common.utils.EasyExcelUtil;
import com.ruoyi.common.utils.SimpleWriteData;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 耗材管理
 * @author zx
 * @date 2022-08-17 15:08:02
 */
@RestController
@RequestMapping("consumables")
public class ConsumablesController extends BaseController {
    private final AaSpuService aaSpuService;
    @Autowired
    public ConsumablesController(AaSpuService aaSpuService) {
        this.aaSpuService = aaSpuService;
    }

    /**
     * 品类录入
     * @param aaSpu
     * @return
     */
    @PostMapping("info")
    @OperLog(title = "流动资产品类新增",businessType = BusinessType.INSERT)
    public R save(@RequestBody AaSpu aaSpu){
        try {
            aaSpuService.save(aaSpu);
            return R.ok();
        }catch (Exception e){
            logger.error("流动资产品类新增失败",e);
            return R.error("保存失败");
        }
    }
    /**
     * 品类详情
     * @param id
     * @return
     */
    @GetMapping("info")
    public R getInfo(Long id){
        try {
            return R.data(aaSpuService.getInfo(id));
        }catch (Exception e){
            logger.error("获取品类详情失败",e);
            return R.error("获取品类详情失败");
        }
    }
    /**
     * 品类编辑
     * @param aaSpu
     * @return
     */
    @PutMapping("info")
    @OperLog(title = "流动资产品类编辑",businessType = BusinessType.UPDATE)
    public R update(@RequestBody AaSpu aaSpu){
        try {
            aaSpuService.update(aaSpu);
            return R.ok();
        }catch (Exception e){
            logger.error("流动资产品类编辑失败",e);
            return R.error("保存失败");
        }
    }

    /**
     * 流动资产列表
     * @param aaSpu
     * @return
     */
    @GetMapping("list")
    public R getList(AaSpu aaSpu){
        try {
            pageUtil();
            return resultData(aaSpuService.getList(aaSpu));
        }catch (Exception e){
            logger.error("获取流动资产列表失败",e);
            return R.error("获取流动资产列表失败");
        }
    }

    /**
     * 流动资产列表导出
     * @param aaSpu
     * @return
     */
    @GetMapping("list_excel")
    public void getList(HttpServletResponse response,AaSpu aaSpu){
        try {
            SimpleWriteData simpleWriteData = new SimpleWriteData();
            simpleWriteData.setFileName("品类列表_"+ SystemUtil.getUserNameCn()+"_"+ DateUtil.format(new Date(),"yyyyMMddHHmmss"));
            simpleWriteData.setDataList(aaSpuService.getListExcel(aaSpu));
            EasyExcelUtil.simpleWrite(simpleWriteData,AaSpu.class,response);
        }catch (Exception e){
            logger.error("导出流动资产列表失败",e);
        }
    }

    /**
     * 直接入库
     * @param aaSku
     * @return
     */
    @PostMapping("sku")
    @OperLog(title = "耗材直接入库",businessType = BusinessType.INSERT)
    public R saveSku(@RequestBody AaSku aaSku){
        try {
            aaSpuService.saveSku(aaSku);
            return R.ok();
        }catch (Exception e){
            logger.error("耗材直接入库失败",e);
            return R.error("保存失败");
        }
    }

    /**
     * 直接入库批次列表
     * @param aaSku
     * @return
     */
    @GetMapping("list_sku")
    public R getList(AaSku aaSku){
        try {
            pageUtil();
            return resultData(aaSpuService.getSkuList(aaSku,0));
        }catch (Exception e){
            logger.error("获取批次列表失败",e);
            return R.error("获取批次列表失败");
        }
    }
    /**
     * 直接入库批次列表导出
     * @param aaSku
     * @return
     */
    @GetMapping("list_sku_excel")
    public void getList(HttpServletResponse response, AaSku aaSku){
        try {
            SimpleWriteData simpleWriteData = new SimpleWriteData();
            simpleWriteData.setFileName("流动资产批次信息");
            simpleWriteData.setDataList(aaSpuService.getSkuList(aaSku,1));
            EasyExcelUtil.simpleWrite(simpleWriteData,AaSku.class,response);
        }catch (Exception e){
            logger.error("导出批次列表导出失败",e);
        }
    }

}
