package com.ruoyi.system.controller;

import com.ruoyi.common.auth.annotation.HasPermissions;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.service.ISysDeptService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 部门管理
 *
 * @author zmr
 * @date 2019-05-20
 * @menu 部门管理
 */
@RestController
@RequestMapping("dept")
public class SysDeptController extends BaseController
{
    @Autowired
    private ISysDeptService sysDeptService;

    /**
     * 查询部门
     */
    @GetMapping("get/{deptId}")
    public SysDept get(@PathVariable("deptId") Long deptId)
    {
        return sysDeptService.selectDeptById(deptId);
    }

    /**
     * 查询部门列表
     */
    @GetMapping("list")
    @ApiOperation(value = "查询部门列表", notes = "查询部门列表")
    public R list(SysDept sysDept)
    {
        startPage();
        return result(sysDeptService.selectDeptList(sysDept));
    }

    /**
     * 查询部门列表(验证候选人权限)
     */
    @GetMapping("list/audit")
    @ApiOperation(value = "查询部门列表", notes = "(验证候选人权限)")
    public List<SysDept> listAudit(List<Long> roleIds)
    {
        return sysDeptService.selectDeptAudit(roleIds);
    }

    /**
     * 查询角色自定义权限部门
     */
    @PostMapping("list_role")
    public R listRole(@RequestBody String[] list)
    {
        List<String> strings = Arrays.asList(list);
        return R.data(sysDeptService.listRole(strings));
    }

    /**
     * 查询所有可用部门
     */
    @GetMapping("list/enable")
    public R listEnable(SysDept sysDept)
    {
        sysDept.setStatus("0");
        return result(sysDeptService.selectDeptList(sysDept));
    }

    /**
     * 查询部门管理数据,若只有一个部门则把父级也查出来
     */
    @GetMapping("list/enable_parent")
    public R listEnableWithParent(SysDept sysDept)
    {
        sysDept.setStatus("0");
        return result(sysDeptService.selectDeptListWithParent(sysDept));
    }

    /**
     * 查询部门所在公司
     */
    @GetMapping("belong_company")
    public R getBelongCompany(Long deptId)
    {
        return R.ok("ok",sysDeptService.getBelongCompany(deptId));
    }

    @GetMapping("belong_company2/{deptId}")
    public Map<String, Object> getBelongCompany2(@PathVariable("deptId") Long deptId)
    {
        return sysDeptService.getBelongCompany(deptId);
    }

    /**
     * 获取部门所属的一级部门
     * @param deptId
     * @return
     */
    @GetMapping("first_dept/{deptId}")
    public Map<String, Object> getFirstDept(@PathVariable("deptId") Long deptId)
    {
        return sysDeptService.getFirstDept(deptId);
    }

    /**
     * 查询部门加公司全称
     * @param deptId
     * @return
     */
    @GetMapping("belong_company3/{deptId}")
    public String getBelongCompany3(@PathVariable("deptId") Long deptId)
    {
        return sysDeptService.getDeptNameAll(deptId);
    }

    /**
     * 查询所有可用部门(不过滤权限)
     */
    @GetMapping("list/enable2")
    public R listEnable2(SysDept sysDept) {
        sysDept.setStatus("0");
        return result(sysDeptService.selectDeptList2(sysDept));
    }
    /**
     * 查询所有可用部门(不过滤权限)Map
     */
    @GetMapping("list/enable2_map")
    public Map<Long,String> listEnable2Map(SysDept sysDept) {
        sysDept.setStatus("0");
        List<SysDept> sysDepts = sysDeptService.selectDeptListMap(sysDept);
        Map<Long, String> maps = new HashMap<>(sysDepts.size());
        for (SysDept dept : sysDepts) {
            maps.put(dept.getDeptId(),dept.getDeptName());
        }
        return maps;
    }

    /**
     * PC端抄送专用
     */
    @GetMapping("list/enable3")
    public R listEnable3(SysDept sysDept) {
        sysDept.setStatus("0");
        return result(sysDeptService.selectDeptList3(sysDept));
    }

    /**
     * 新增保存部门
     */
    @PostMapping("save")
    @HasPermissions("system:dept:add")
    @OperLog(title = "新增保存部门", businessType = BusinessType.INSERT)
    public R addSave(@RequestBody SysDept sysDept)
    {
        return toAjax(sysDeptService.insertDept(sysDept));
    }

    /**
     * 修改保存部门
     */
    @PostMapping("update")
    @HasPermissions("system:dept:edit")
    @OperLog(title = "修改保存部门", businessType = BusinessType.UPDATE)
    public R editSave(@RequestBody SysDept sysDept)
    {
        return toAjax(sysDeptService.updateDept(sysDept));
    }

    /**
     * 删除部门
     */
    @HasPermissions("system:dept:remove")
    @PostMapping("remove/{deptId}")
    @OperLog(title = "删除部门", businessType = BusinessType.DELETE)
    public R remove(@PathVariable("deptId") Long deptId)
    {
        return toAjax(sysDeptService.deleteDeptById(deptId));
    }

    /**
     * 加载角色部门（数据权限）列表树
     */
    @ApiOperation(value = "加载角色部门（数据权限）列表树", notes = "加载角色部门（数据权限）列表树")
    @ApiImplicitParams({@ApiImplicitParam(name = "roleId", value = "角色id", required = true)})
    @GetMapping("/role/{roleId}")
    public Set<String> deptTreeData(@PathVariable("roleId") Long roleId)
    {
        if (null == roleId || roleId <= 0){
            return null;
        }
        return sysDeptService.roleDeptIds(roleId);
    }

    /**
     * 获取树形结构
     * @return
     */
    @GetMapping("/tree_all")
    public R deptTreeData()
    {
        try {
            return R.data(sysDeptService.getTreeAll());
        }catch (Exception e){
            logger.error("获取树形部门数据失败",e);
            return R.error("获取数据失败");
        }
    }
    /**
     * 获取树形结构(有权限)
     * @return
     */
    @GetMapping("/tree_all_role")
    public R deptTreeDataRole()
    {
        try {
            return R.data(sysDeptService.getTreeAllRole(new SysDept()));
        }catch (Exception e){
            logger.error("获取树形部门数据(有权限)失败",e);
            return R.error("获取数据失败");
        }
    }

    /**
     * 查询公司列表
     */
    @GetMapping("/list/getCompany")
    @ApiOperation(value = "查询公司列表", notes = "查询公司列表")
    public R getCompany(SysDept sysDept)
    {
        return result(sysDeptService.selectCompanyList(sysDept));
    }
}
