package com.ruoyi.activiti.controller.nbcb;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.nbcb.sdk.OpenSDK;
import com.ruoyi.activiti.domain.nbcb.NBCBDto;
import com.ruoyi.activiti.domain.nbcb.NbcbAccountInfo;
import com.ruoyi.activiti.service.nbcb.NbcbSevice;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author yrb
 * @Date 2023/5/15 13:25
 * @Version 1.0
 * @Description 宁波银行财资系统 EASYAPI
 */
@RestController
@RequestMapping("/nbcb")
public class NbcbController extends BaseController {
    private final NbcbSevice nbcbSevice;

    public NbcbController(NbcbSevice nbcbSevice) {
        this.nbcbSevice = nbcbSevice;
    }

    /**
     * 通用下载接口
     *
     * @param nbcbDto 入参
     * @return result
     */
    @GetMapping("/getGeneralDownloadUrl")
    public R getGeneralDownloadUrl(NBCBDto nbcbDto) {
        try {
            // 上送报文
            Map<String, Object> paramMap = Maps.newHashMap();
            logger.info(nbcbDto.getCustId());
            paramMap.put("custId", nbcbDto.getCustId());
            logger.info(nbcbDto.getDownloadNo());
            paramMap.put("downloadNo", nbcbDto.getDownloadNo());
            logger.info(nbcbDto.getTradeType());
            paramMap.put("tradeType", nbcbDto.getTradeType());
            logger.info(paramMap.toString());
            Map<String, Object> data = Maps.newHashMap();
            data.put("Data", paramMap);
            logger.info(data.toString());
            // 返回报文
            String response = OpenSDK.send(nbcbDto.getProductID(), nbcbDto.getServiceID(), JSON.toJSONString(data));
            return R.data(response);
        } catch (Exception e) {
            logger.error("接口调用失败" + e);
            return R.error("失败");
        }
    }

    /**
     * 账户信息查询
     *
     * @param nbcbDto 入参
     * @return result
     */
    @PostMapping("/queryAccount")
    public R queryAccount(@RequestBody NBCBDto nbcbDto) throws Exception {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("custId", nbcbDto.getCustId());
        paramMap.put("pageSize", nbcbDto.getPageSize());
        paramMap.put("currentPage", nbcbDto.getCurrentPage());
        Map<String, Object> data = Maps.newHashMap();
        data.put("Data", paramMap);
        // 返回报文
        String response = OpenSDK.send(nbcbDto.getProductID(), nbcbDto.getServiceID(), JSON.toJSONString(data));
        return R.data(response);
    }

    /**
     * 交易明细查询
     *
     * @param nbcbDto 入参
     * @return result
     */
    @PostMapping("/queryAccDetail")
    public R queryAccDetail(@RequestBody NBCBDto nbcbDto) throws Exception {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("custId", nbcbDto.getCustId());
        paramMap.put("bankAccList", nbcbDto.getBankAccList());
        paramMap.put("pageSize", nbcbDto.getPageSize());
        paramMap.put("currentPage", nbcbDto.getCurrentPage());
        paramMap.put("queryType", nbcbDto.getQueryType());
        Map<String, Object> data = Maps.newHashMap();
        data.put("Data", paramMap);
        // 返回报文
        String response = OpenSDK.send(nbcbDto.getProductID(), nbcbDto.getServiceID(), JSON.toJSONString(data));
        return R.data(response);
    }

    /**
     * 回单查询
     *
     * @param nbcbDto 入参
     * @return result
     */
    @PostMapping("/queryReceipt")
    public R queryReceipt(@RequestBody NBCBDto nbcbDto) throws Exception {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("custId", nbcbDto.getCustId());
        paramMap.put("queryFlag", nbcbDto.getQueryFlag());
        paramMap.put("accountSet", nbcbDto.getAccountSet());
        Map<String, Object> data = Maps.newHashMap();
        data.put("Data", paramMap);
        // 返回报文
        String response = OpenSDK.send(nbcbDto.getProductID(), nbcbDto.getServiceID(), JSON.toJSONString(data));
        return R.data(response);
    }

    /**
     * 批量转账
     *
     * @param nbcbDto 入参
     * @return result
     */
    @PostMapping("/batchTransfer")
    public R batchTransfer(@RequestBody NBCBDto nbcbDto) throws Exception {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("custId", nbcbDto.getCustId());
        paramMap.put("batchSerialNo", nbcbDto.getBatchSerialNo());
        paramMap.put("businessCode", nbcbDto.getBusinessCode());
        paramMap.put("totalAmt", nbcbDto.getTotalAmt());
        paramMap.put("totalNumber", nbcbDto.getTotalNumber());
        paramMap.put("showFlag", nbcbDto.getShowFlag());
        paramMap.put("transferDtls", nbcbDto.getTransferDtls());
        Map<String, Object> data = Maps.newHashMap();
        data.put("Data", paramMap);
        // 返回报文
        String response = OpenSDK.send(nbcbDto.getProductID(), nbcbDto.getServiceID(), JSON.toJSONString(data));
        return R.data(response);
    }

    /**
     * 批量查证
     *
     * @param nbcbDto 入参
     * @return result
     */
    @PostMapping("/queryBatchTransferResult")
    public R queryBatchTransferResult(@RequestBody NBCBDto nbcbDto) throws Exception {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("custId", nbcbDto.getCustId());
        paramMap.put("batchSerialNo", nbcbDto.getBatchSerialNo());
        Map<String, Object> data = Maps.newHashMap();
        data.put("Data", paramMap);
        // 返回报文
        String response = OpenSDK.send(nbcbDto.getProductID(), nbcbDto.getServiceID(), JSON.toJSONString(data));
        return R.data(response);
    }

    @GetMapping("/test")
    public R test(NBCBDto nbcbDto) {
        nbcbSevice.test(nbcbDto);
        return R.ok();
    }

    @PostMapping("/testPaymentOrderApiAdd")
    public R testPaymentOrderApiAdd(@RequestBody NBCBDto nbcbDto) {
        nbcbSevice.testPaymentOrderApiAdd(nbcbDto);
        return R.ok();
    }

    /**
     * 手动推送付款单
     * @param nbcbDto 账号信息
     * @return 推送结果
     */
    @PostMapping("/paymentOrderApiAdd")
    public R paymentOrderApiAdd(@RequestBody NBCBDto nbcbDto) {
        try {
            if (StrUtil.isBlank(nbcbDto.getName())){
                return R.error("收款账户不能为空");
            }
            if (StrUtil.isBlank(nbcbDto.getAccountNum())){
                return R.error("账号不能为空");
            }
            if (StrUtil.isBlank(nbcbDto.getProjectMoney())){
                return R.error("推送金额不能为空");
            }
            if (StrUtil.isBlank(nbcbDto.getBank())){
                return R.error("银行名称不能为空");
            }
            return R.data(nbcbSevice.paymentOrderApiAdd(nbcbDto));
        }catch (Exception e){
            return R.error("推送付款单发生异常");
        }
    }

    @PostMapping("/test")
    public void test(@RequestBody NbcbAccountInfo nbcbAccountInfo){
        nbcbSevice.paymentOrderApiQry(nbcbAccountInfo);
    }

    @GetMapping("/testAutoAudit")
    public void testAutoAudit(String businessKey, Long id){
        nbcbSevice.testAutoAudit(businessKey,id);
    }
}
