package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.fiance.BizAccountsReceivable;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.SysUser;

import java.util.List;
/**
 * @author zh
 * @date 2021/12/15
 */
public interface BizAccountsReceivableService {
    /**
     * 查询本人全部或特定账户类型收款账号数据
     * @param sysUser
     * @param bizAccountsReceivable
     * @return
     */
    List<BizAccountsReceivable> selectList(SysUser sysUser, BizAccountsReceivable bizAccountsReceivable);

    /**
     * 新增收款账号
     * @param bizAccountsReceivable
     * @return
     */
    R insert(BizAccountsReceivable bizAccountsReceivable);

    /**
     * 修改收款账号
     * @param bizAccountsReceivable
     * @return
     */
    R update(BizAccountsReceivable bizAccountsReceivable);

    /**
     * 删除收款账号
     * @param
     * @return
     */
     R delete(Integer[] ids) ;
}
