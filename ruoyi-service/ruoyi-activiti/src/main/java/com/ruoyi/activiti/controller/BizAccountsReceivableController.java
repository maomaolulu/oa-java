package com.ruoyi.activiti.controller;


import com.ruoyi.activiti.domain.fiance.BizAccountsReceivable;
import com.ruoyi.activiti.service.BizAccountsReceivableService;
import com.ruoyi.common.annotation.LoginUser;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.system.domain.SysUser;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 * 收款账号
 *
 * @author zh
 * @date 2021-12-15
 * @menu 收款账号
 */

@RestController
@RequestMapping("accounts_receivable")
public class BizAccountsReceivableController extends BaseController {


    private final BizAccountsReceivableService bizAccountsReceivableService;
    @Autowired
    public BizAccountsReceivableController( BizAccountsReceivableService bizAccountsReceivableService) {

        this.bizAccountsReceivableService = bizAccountsReceivableService;
    }

    /**
     * 查询本人全部或特定账户类型收款账号数据
     * @param sysUser
     * @param bizAccountsReceivable
     * @return
     */
    @GetMapping("biz/info")
    @ApiOperation(value = "根据当前用户查询")
    public R biz(@LoginUser SysUser sysUser, BizAccountsReceivable bizAccountsReceivable){
        return R.ok("ok",bizAccountsReceivableService.selectList(sysUser,bizAccountsReceivable));
    }

    /**
     * 新增收款账号
     */
    @OperLog(title = "新增收款账号", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增收款账号", notes = "")
    @PostMapping("save")
    public R addSave(@RequestBody BizAccountsReceivable bizAccountsReceivable)
    {
        return bizAccountsReceivableService.insert(bizAccountsReceivable);
    }

    /**
     * 修改收款账号
     */
    @OperLog(title = "修改收款账号", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改收款账号", notes = "")
    @PostMapping("update")
    public R update(@RequestBody BizAccountsReceivable bizAccountsReceivable)
    {
        return bizAccountsReceivableService.update(bizAccountsReceivable);
    }
    /**
     * 删除收款账号
     */
    @OperLog(title = "删除收款账号", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除收款账号", notes = "")
    @PostMapping("delete")
    public R remove(@RequestBody Integer[] ids)
    {
        return bizAccountsReceivableService.delete(ids);
    }


}
