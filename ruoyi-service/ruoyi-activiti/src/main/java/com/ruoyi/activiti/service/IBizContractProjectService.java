package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.my_apply.BizContractProject;
import com.ruoyi.activiti.domain.my_apply.BizCpType;

import java.util.List;
import java.util.Map;

/**
 * @Author yrb
 * @Date 2023/6/13 15:14
 * @Version 1.0
 * @Description 合同项目信息修改
 */
public interface IBizContractProjectService {
    /**
     * 插入合同项目信息
     * @param bizContractProject 合同项目信息
     * @return result
     */
    int insert(BizContractProject bizContractProject);

    /**
     * 获取合同、项目类型信息
     * @return tree
     */
    List<BizCpType> getTree();

    /**
     * 查询合同项目修改审批详情
     * @param id 业务类主键id
     * @return 详情信息
     */
    BizContractProject findDetail(String id);

    /**
     * 获取合同项目信息
     * @param identifier 项目编号
     * @return result
     */
    List<BizContractProject> findInfo(String identifier);

    void test(String identifier);

    /**
     * 获取合同类型、项目类型
     * @return result
     */
    List<Map> getType();
 }
