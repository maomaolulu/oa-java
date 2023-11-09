package com.ruoyi.activiti.service.nbcb;

import com.ruoyi.activiti.domain.nbcb.NBCBDto;
import com.ruoyi.activiti.domain.nbcb.NbcbAccountInfo;
import com.ruoyi.activiti.domain.proc.BizBusiness;

/**
 * @Author yrb
 * @Date 2023/6/6 9:48
 * @Version 1.0
 * @Description 宁波银行财资平台自动打款相关接口
 */
public interface NbcbSevice {

    String singleTransfer(NBCBDto nbcbDto);

    String querySingleTransferResult(NBCBDto nbcbDto);

    void test(NBCBDto nbcbDto);

    /**
     * 付款单提交接口
     *
     * @param bizBusiness 申请主要信息
     * @return result
     */
    void paymentOrderApiAdd(BizBusiness bizBusiness);

    /**
     * 自动审批
     */
    void autoAudit();

    /**
     * 测试推送付款单
     * @param nbcbDto 入参
     */
    void testPaymentOrderApiAdd(NBCBDto nbcbDto);

    /**
     * 推送付款单(财务推送)
     * @param nbcbDto 入参
     */
    String paymentOrderApiAdd(NBCBDto nbcbDto);

    void paymentOrderApiQry(NbcbAccountInfo nbcbAccountInfo);

    /**
     * 测试自动审批
     *
     * @param businessKey 流程通用业务类主键id
     */
    void testAutoAudit(String businessKey, Long id);
}
