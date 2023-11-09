package com.ruoyi.activiti.domain.nbcb;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author yrb
 * @Date 2023/5/15 14:16
 * @Version 1.0
 * @Description 参数
 */
@Data
public class NBCBDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户号
     */
    private String custId;

    /**
     * 下载编号
     */
    private String downloadNo;

    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 产品号
     */
    private String productID;

    /**
     * 服务号
     */
    private String serviceID;

    /**
     * 每页条数
     */
    private String pageSize;

    /**
     * 当前页数
     */
    private String currentPage;

    /**
     * 账号
     */
    private List<String> bankAccList;

    /**
     * 开始日期 yyyy-MM-dd
     */
    private String beginDate;

    /**
     * 结束日期 yyyy-MM-dd
     */
    private String endDate;

    /**
     * 统计编号
     */
    private List<String> subBankAccList;

    /**
     * 账户标识
     */
    private String bankAccSign;

    /**
     * 起始金额 单位：元
     */
    private String beginAmt;

    /**
     * 终止金额 单位：元
     */
    private String endAmt;

    /**
     * 收支方向 0-收入 1-支出
     */
    private String cdSign;

    /**
     * 查询方式 0-分页查询，1-下载文件查询
     */
    private String queryType;

    /**
     * 查询标识
     */
    private String queryFlag;

    /**
     * 交易流水号
     */
    private String serialNo;

    /**
     * 账号集合
     */
    private List<String> accountSet;

    /**
     * 大批量下载标识
     */
    private String batchDownloadSign;

    /**
     * 批次流水号  8位日期+8位数字
     */
    private String batchSerialNo;

    /**
     * 业务类型
     */
    private String businessCode;

    /**
     * 总金额 单位：元
     */
    private BigDecimal totalAmt;

    /**
     * 总笔数
     */
    private Integer totalNumber;

    /**
     * 审核人是否能查
     * 看转账清单
     */
    private String showFlag;

    /**
     * 是否预约 0或空：否   1：是
     */
    private String appointmentFlag;

    /**
     * 预约时间 yyyy-MM-dd HH:mm:ss
     */
    private String appointmentTime;

    /**
     * 明细列表
     */
    private List<EasyBatchTransferDtlReqBean> transferDtls;

    /**
     * 单位编码
     */
    private String corpCode;

    /**
     * 付款单号
     */
    private String payBillNo;

    /**
     * 供应商名称(收款人姓名)
     */
    private String supplyName;

    /**
     * 供应商账号(收款人账号)
     */
    private String supplyAcc;

    /**
     * 供应商开户行名(收款人开户行名)
     */
    private String supplyBankName;

    /**
     * 付款单金额
     */
    private String amt;

    /**
     * 供应商开户行行号
     */
    private String supplyBankNo;

    /**
     * 付款账号
     */
    private String payAcc;

    /**
     * 申请编号(付款编号)
     */
    private String applyCode;

    /**
     * 业务key
     */
    private String businessKey;

    /**
     * 收款账号
     */
    private String accountNum;

    /**
     * 收款人
     */
    private String name;

    /**
     * 项目金额
     */
    private String projectMoney;

    /**
     * 银行名称
     */
    private String bank;

    /**
     * 银行分支
     */
    private String branchBank;
}
