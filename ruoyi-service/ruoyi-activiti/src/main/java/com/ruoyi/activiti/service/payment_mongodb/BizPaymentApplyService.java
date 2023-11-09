package com.ruoyi.activiti.service.payment_mongodb;

import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.vo.BizBusinessVO;
import com.ruoyi.activiti.domain.vo.DirectoryDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuYang
 * @date 2022/10/17 16:26
 */
public interface BizPaymentApplyService {
    /**
     * 模板插入mongodb
     *
     * @param param json参数
     */
    Integer insert(HashMap<String, Object> param);

    /**
     * 更新模板
     *
     * @param param
     */
    int update(HashMap<String, Object> param);

    /**
     * 返回二级目录
     *
     * @param
     */
    List<HashMap> secondDirector();

    /**
     * 查询所有模板列表
     *
     * @return
     */
    Object select(String id, Integer isLive);

    /**
     * 查看模板是否为旧mob
     */
    Boolean checkTemplateIsOld(String id);

    /**
     * 删除模板
     *
     * @param id
     */
    boolean delete(String id);

    /**
     * 获取单个模板
     *
     * @param id
     */
    HashMap<String, Object> getTemplate(String id);

    /**
     * 启动流程
     */
    int startPaymentProcess(HashMap<String, Object> param);

    /**
     * 获取单个模板
     */
    HashMap<String, Object> selectById(String id);

    /**
     * 查询所有模板目录列表
     */
    List<HashMap> getDirectory(String type);

    /**
     * 查询所有模板目录列表
     */
    List<HashMap> getDirectoryAll(String type);

    /**
     * 增加目录
     */
    void addDirectory(DirectoryDTO directoryDTO);

    /**
     * 删除目录
     */
    int removeDirectory(String id);

    /**
     * 更新目录
     */
    int updateDirectory(DirectoryDTO directoryDTO);

    /**
     * 查询单个目录
     */
    HashMap selectOneDirectory(String id);

    /**
     * 查询配置
     */
    HashMap getConfig();

    /**
     * 激活/禁用模板
     */
    int activation(String templateId);

    /**
     * 获取付款申请列表
     */
    List<BizBusiness> getPaymentApplyList(BizBusinessVO bizBusiness);

    /**
     * 更具目录id 查询模板
     */
    List<HashMap> getTemplateById(String id);

    /**
     * 获取详情
     */
    HashMap getApplyDetail(String id, BizBusiness bizBusiness);

    /**
     * 判断使用有重复
     */
    boolean ifHasDuplicateBill(String bullCode, String billNumber);

    /**
     * 数据到处
     */
    void download(List<BizBusiness> all, HttpServletResponse response) throws IOException;

    /**
     * 获取全部biz_business 按照权限
     */
    List<BizBusiness> getAll(String applyCode, String type, String applyer, Integer companyId, Integer deptId, Boolean hasPurchase);

    /**
     * 变成草稿
     */
    int toDraft(HashMap<String, Object> param);

    /**
     * 四级联动 包含明细
     *
     * @param
     * @return
     */
    List<HashMap> paymentInfo(Boolean flag, String id);

    void insertPaymentNumber();
    /**
     * 导出财务报表
     * @param response
     * @throws Exception
     */
     void exportPaymentApplyData(HttpServletResponse response) throws Exception;
    /**
     * 获取财务申请数据
     */
    Map<String, Map<String, Map<String, BigDecimal>>> getPaymentApplyData(HttpServletResponse response);

    /**
     * 判断用户是否有申请权限
     *
     * @param id 模板id
     * @return result
     */
    boolean permitApply(String id);
}
