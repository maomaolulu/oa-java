package com.ruoyi.system.feign;

import com.ruoyi.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.ruoyi.common.constant.ServiceNameConstants;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.feign.factory.RemoteDeptFallbackFactory;

import java.util.List;
import java.util.Map;

/**
 * 用户 Feign服务层
 * 
 * @author zmr
 * @date 2019-05-20
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteDeptFallbackFactory.class)
public interface RemoteDeptService
{
    @GetMapping("dept/get/{deptId}")
    SysDept selectSysDeptByDeptId(@PathVariable("deptId") long deptId);

    @GetMapping("dept/list/audit")
    List<SysDept> listAudit(@RequestBody List<Long> roleIds);

    /**
     * 查询部门所在公司
     */
    @GetMapping("dept/belong_company2/{deptId}")
    Map<String, Object> getBelongCompany2(@PathVariable("deptId") long deptId);

    /**
     * 查询部门所属一级部门
     */
    @GetMapping("dept/first_dept/{deptId}")
    Map<String, Object> getFirstDept(@PathVariable("deptId") long deptId);

    /**
     * 查询部门所在公司
     */
    @GetMapping("dept/belong_company3/{deptId}")
    String getBelongCompany3(@PathVariable("deptId") long deptId);

    /**
     * 无权限过滤查询所有可用部门Map
     * @return
     */
    @GetMapping("dept/list/enable2_map")
    Map<Long,String> listEnable2Map();
}
