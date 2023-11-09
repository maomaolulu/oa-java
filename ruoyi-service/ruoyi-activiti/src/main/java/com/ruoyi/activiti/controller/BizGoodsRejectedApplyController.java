package com.ruoyi.activiti.controller;


import cn.hutool.core.date.DateUtil;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.purchase.BizGoodsRejectedApply;
import com.ruoyi.activiti.domain.dto.CapitalGoodsDto;
import com.ruoyi.activiti.service.BizGoodsRejectedApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @menu 退货相关
 */
@RestController
@RequestMapping("goods_rejected")
public class BizGoodsRejectedApplyController extends BaseController {
    @Autowired
    private BizGoodsRejectedApplyService goodsRejectedApplyService;
    @Autowired
    private IBizBusinessService businessService;

    /**
     * 获取固定资产退货列表
     * @param dto
     * @return
     */
    @GetMapping("selectAssetList")
    public R selectAssetList(CapitalGoodsDto dto){
        startPage();
        return result(goodsRejectedApplyService.getAssetList(dto));
    }

    /**
     * 获取流动资产退货列表
     * @param dto
     * @return
     */
    @GetMapping("getSpuList")
    public R getSpuList(CapitalGoodsDto dto){
        startPage();
        return result(goodsRejectedApplyService.getSpuList(dto));
    }

    /**
     * 获取付款编号
     *
     * @return
     */
    @GetMapping("code")
    public R getCode() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String today = DateUtil.today();
        today = today.replace("-", "");
        return R.data("TH" + today + timestamp);
    }


    /**
     * 点击退货申请获取物品信息--固定资产asset
     * @param idList
     * @return
     */
    @GetMapping("assetList")
    public R getAssetList(List<Long> idList) {
        try {
            return R.data(goodsRejectedApplyService.getAsset(idList));
        } catch (Exception e) {
            logger.error("获取关联审批单下拉列表失败", e);
            return R.error("获取关联审批单下拉列表失败");
        }
    }

    /**
     * 点击退货申请获取物品信息--流动资产sku
     * @param idList
     * @return
     */
    @GetMapping("spuList")
    public R getSpuList(List<Long> idList) {
        try {
            return R.data(goodsRejectedApplyService.getSpu(idList));
        } catch (Exception e) {
            logger.error("获取关联审批单下拉列表失败", e);
            return R.error("获取关联审批单下拉列表失败");
        }
    }

    /**
     * 点击提交退货申请
     * @param goodsRejectedApply
     * @return
     */
    @OperLog(title = "退货申请", businessType = BusinessType.UPDATE)
    @PostMapping("save")
    public R addSave(@RequestBody BizGoodsRejectedApply goodsRejectedApply){
        try {
            int insert = goodsRejectedApplyService.insert(goodsRejectedApply);
            if(insert==0){
                return R.error("提交退货申请失败");
            }
            if (insert==3){
                return R.error("退货明细不能为空");
            }
            return R.ok("提交申请成功");
        }catch (Exception e) {
            logger.error("提交申请失败", e);
            return R.error("提交申请失败");
        }

    }

    /**
     * 通过流程ID获取详情
     * @param businessKey
     * @return
     */
    @GetMapping("info/{businessKey}")
    public R info(@PathVariable("businessKey") String businessKey){
        try {
            try {
                BizBusiness business = businessService.selectBizBusinessById(businessKey);
                if (null == business) {
                    return R.error("获取流程信息失败");
                }
                BizGoodsRejectedApply goodsRejectedApply = goodsRejectedApplyService.getInfo(Long.valueOf(business.getTableId()));
                goodsRejectedApply.setTitle(business.getTitle());
                return R.data(goodsRejectedApply);
            } catch (Exception e) {
                logger.error("获取详情失败", e);
                return R.error("获取详情失败");
            }
        }catch (Exception e) {
            logger.error("获取详情失败", e);
            return R.error("获取详情失败");
        }
    }
}
