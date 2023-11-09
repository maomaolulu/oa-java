package com.ruoyi.activiti.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.activiti.domain.fiance.BizAccountsReceivable;
import com.ruoyi.activiti.mapper.BizAccountsReceivableMapper;
import com.ruoyi.activiti.mapper.DistrictsMapper2;
import com.ruoyi.activiti.service.BizAccountsReceivableService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 收款账号
 *
 * @author zh
 * @date 2021-12-15
 */
@Service
@Slf4j
public class BizAccountsReceivableServiceImpl implements BizAccountsReceivableService {

    private final BizAccountsReceivableMapper bizAccountsReceivableMapper;
    private final DistrictsMapper2 districtsMapper2;
    private RemoteUserService remoteUserService;

    @Autowired
    public BizAccountsReceivableServiceImpl(BizAccountsReceivableMapper bizAccountsReceivableMapper, DistrictsMapper2 districtsMapper2, RemoteUserService remoteUserService) {
        this.bizAccountsReceivableMapper = bizAccountsReceivableMapper;
        this.districtsMapper2 = districtsMapper2;
        this.remoteUserService = remoteUserService;
    }

    @Override
    public List<BizAccountsReceivable> selectList(SysUser sysUser, BizAccountsReceivable bizAccountsReceivable) {
        Long userId = SystemUtil.getUserId();
        QueryWrapper<BizAccountsReceivable> bizAccountsReceivableQueryWrapper = new QueryWrapper<>();
        bizAccountsReceivableQueryWrapper.eq("del_flag",0);
        ArrayList<Integer> integers = new ArrayList<>();

       if (bizAccountsReceivable.getTypes()!=null){
           if (bizAccountsReceivable.getTypes()==1){
               integers.add(1);
           }

           if (bizAccountsReceivable.getTypes()==2){
               integers.add(2);
           }
           if (bizAccountsReceivable.getTypes()==3){
               integers.add(3);
           }
           if (bizAccountsReceivable.getTypes()==12){
               integers.add(1);
               integers.add(2);
           }
           if (bizAccountsReceivable.getTypes()==23){
               integers.add(2);
               integers.add(3);
           }
           if (bizAccountsReceivable.getTypes()==123){
               integers.add(1);
               integers.add(2);
               integers.add(3);
           }
       }
        bizAccountsReceivableQueryWrapper.in(bizAccountsReceivable.getTypes()!=null,"account_type",integers);
        bizAccountsReceivableQueryWrapper.eq("belong_user",userId);
         List<BizAccountsReceivable> bizAccountsReceivables = bizAccountsReceivableMapper.selectList(bizAccountsReceivableQueryWrapper);

        for (BizAccountsReceivable acc:bizAccountsReceivables
             ) {
            if(StrUtil.isNotBlank(acc.getCity())){
                acc.setCityName(districtsMapper2.selectById(Integer.valueOf(acc.getCity())).getName());
            }
           if(StrUtil.isNotBlank(acc.getProvince())){
               acc.setProvinceName(districtsMapper2.selectById(Integer.valueOf(acc.getProvince())).getName());
           }

        }

        return  bizAccountsReceivables;
    }

    /**
     * 新增收款账号
     *
     * @param
     * @return
     */
    @Override
    public R insert(BizAccountsReceivable bizAccountsReceivable) {
        try {
            Long userId = SystemUtil.getUserId();

            SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);

            bizAccountsReceivable.setCreateBy(sysUser.getLoginName());
            bizAccountsReceivable.setCreateTime(new Date());
            bizAccountsReceivable.setDelFlag("0");
            bizAccountsReceivable.setBelongUser(userId.toString());

            bizAccountsReceivableMapper.insert(bizAccountsReceivable);

            return R.ok("提交收款账号成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("提交收款账号失败");
        }
    }

    /**
     * 修改收款账号
     *
     * @param
     * @return
     */
    @Override
    public R update(BizAccountsReceivable bizAccountsReceivable) {
        try {
            Long userId = SystemUtil.getUserId();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);

            bizAccountsReceivable.setUpdateBy(sysUser.getLoginName());
            bizAccountsReceivable.setUpdateTime(new Date());

            bizAccountsReceivableMapper.updateById(bizAccountsReceivable);

            return R.ok("提交收款账号成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("提交收款账号失败");
        }
    }


    /**
     * 删除收款账号
     * @param
     * @return
     */
    public R delete(Integer[] ids) {
        for(Integer id: Arrays.asList(ids)){
            BizAccountsReceivable bizAccountsReceivable = new BizAccountsReceivable();
            bizAccountsReceivable.setId(id);
            bizAccountsReceivable.setDelFlag("1");
            bizAccountsReceivableMapper.updateById(bizAccountsReceivable);
        }
      return   R.ok("删除成功");
    }



}
