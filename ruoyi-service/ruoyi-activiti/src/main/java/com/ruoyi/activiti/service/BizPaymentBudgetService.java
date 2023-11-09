package com.ruoyi.activiti.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

/**
 * 预算
 * @author wuYang
 * @date 2022/12/9 10:21
 */

public interface BizPaymentBudgetService {
    /**
     * 上传
     */
    int upload(MultipartFile file,String id,Boolean flag);

    /**
     * 下载
     */
    void download(String id,Boolean flag,HttpServletResponse response)  ;

    /**
     *  统计导出
     */
    void  out(String id,Boolean flag,HttpServletResponse response);

    /**
     * 生成草稿预算模板
     */
    int budgetInsert(HashMap map);
    /**
     * 发布
     */
    int budgetPublish(String id);

    /**
     * 修改
     */
    void budgetUpdate(HashMap map);
    /**
     * 禁用
     */
    int budgetDisable(String id);
    /**
     * 逻辑删除
     */
    void budgetDelete(String id);
    /**
     * 查询全部预算模板
     */
    List<HashMap> budgetSelectAll(String budgetName, Integer state);
    /**
     * 查询单个
     */
    HashMap budgetSelect(String id);

    /**
     * 针对第一次选公司
     */
    List<HashMap> firstChoice(HashMap map);
    /**
     * 针对前端回传公司代码变成为对象
     */
    List<HashMap> handelDeptIdToObject(HashMap map);

    /**
     * 保存
     */
    int savePlan(HashMap map);

    /**
     * 针对前端回传明细代码变成为对象 可能是模板也有可能是明细
     */
    List<HashMap> handeDetailIdToObject(HashMap map);

    /**
     * 查询
     */
    List<HashMap> selectForPlan(String id);

    /**
     * 查询
     */
    List<HashMap> selectForLook(String id);


    /**
     * 修改预算名字
     */
    int updateName(String id,String name);

    /**
     * 每月自动结账转到下一个月
     */
     void delete(List<String> ids, String id );
    /**
     * 查询当月该部门的余额
     */
    String selectMoney(HashMap map);

    /**
     * 手动调剂
     */
    int resetMoney(HashMap map);

    /**
     * 修改已经发布的预算金额
     * @return 是否更新成功
     */
    int updateBudgetMoney(HashMap hashMap);

}
