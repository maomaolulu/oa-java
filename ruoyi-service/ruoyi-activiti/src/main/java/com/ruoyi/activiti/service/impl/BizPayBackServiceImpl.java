package com.ruoyi.activiti.service.impl;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.activiti.domain.*;
import com.ruoyi.activiti.domain.fiance.BizContractApply;
import com.ruoyi.activiti.domain.fiance.BizPayBack;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.*;
import com.ruoyi.activiti.service.BizPayBackService;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.util.DataScopeUtil;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 回款管理业务层处理
 *
 * @author zh
 * @date 2021-11-26
 */
@Service
@Slf4j
public class BizPayBackServiceImpl implements BizPayBackService {
    private final BizContractApplyMapper bizContractApplyMapper;
    private final BizPayBackMapper bizPayBackMapper;
    private final RemoteUserService remoteUserService;
    private final RemoteFileService remoteFileService;
    private final RemoteDeptService remoteDeptService;
    @Autowired
    private DataScopeUtil dataScopeUtil;


    @Autowired
    public BizPayBackServiceImpl( BizContractApplyMapper bizContractApplyMapper, BizPayBackMapper bizPayBackMapper, RemoteUserService remoteUserService,  RemoteFileService remoteFileService, RemoteDeptService remoteDeptService) {
        this.bizContractApplyMapper = bizContractApplyMapper;
        this.bizPayBackMapper = bizPayBackMapper;
        this.remoteUserService = remoteUserService;
        this.remoteFileService = remoteFileService;
        this.remoteDeptService = remoteDeptService;
    }


    /**
     * 查询回款管理列表
     *
     * @param dto 回款管理
     * @return 回款管理
     */
    @Override
    public List<BizPayBack> selectBizPayBack(BizPayBack dto) {
        QueryWrapper<BizPayBack> mapper = new QueryWrapper<>();
        mapper.eq("pb.del_flag",0);
        //查询回款管理单条
        mapper.eq(dto.getId()!=null,"pb.id",dto.getId());
        /**回款管理申请编号*/
        mapper.like(StrUtil.isNotBlank(dto.getPayBackCode()),"pb.pay_back_code",dto.getPayBackCode());
        /**申请人*/
        mapper.eq(StrUtil.isNotBlank(dto.getCreateByName()),"u.user_name",dto.getCreateByName());
        /**客户名称*/
        mapper.eq(StrUtil.isNotBlank(dto.getPayee()),"pb.payee",dto.getPayee());
        /**收款类型*/
        mapper.eq(StrUtil.isNotBlank(dto.getReceiveType()),"pb.receive_type",dto.getReceiveType());

        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);

        String sql = dataScopeUtil.getScopeSql(sysUser, "d", null);

        if(StrUtil.isNotBlank(sql)){
            mapper.apply(sql);
        }
        List<BizPayBack> list = bizPayBackMapper.selectAll(mapper);
        for (BizPayBack bizPayBack:list
             ) {
            List<SysAttachment> file = remoteFileService.getList(bizPayBack.getId(), "pay-back-file");

            List<SysAttachment> img = remoteFileService.getList(bizPayBack.getId(), "pay-back-img");
            bizPayBack.setFile(file);
            bizPayBack.setImg(img);
        }

        return  list;
    }


    /**
     * 回款管理新增
     *
     * @param bizPayBack 回款管理
     * @return 结果
     */
    @Override
    public R insertBizPayBack(BizPayBack bizPayBack) {
        try {
            bizPayBack.setDelFlag("0");
            bizPayBack.setCreateBy(SystemUtil.getUserId());
            bizPayBack.setCreateTime(new Date());
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyId1 = belongCompany2.get("companyId").toString();
            bizPayBack.setDeptId(sysUser.getDeptId());
            bizPayBackMapper.insert(bizPayBack);
            BizContractApply bizContractApply = new BizContractApply();
            bizContractApply.setId(bizPayBack.getContractApplyId());
            BigDecimal subtract = bizPayBack.getRemainingMoney().subtract(bizPayBack.getAmount());
            bizContractApply.setRemainingMoney(subtract);
           if(subtract.doubleValue()<0){
               return R.error("回款金额不能大于剩余回款金额");
           }
            bizContractApplyMapper.updateById(bizContractApply);
            int reimburse = remoteFileService.update(bizPayBack.getId(),bizPayBack.getPayBackCode());
            if(reimburse == 0){
                throw new StatefulException("将上传的文件转为有效文件失败");
            }
            return R.ok("提交回款管理成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("提交合同审批业务失败");
        }
    }


    /**
     * 修改回款管理
     *
     * @param
     * @return
     */
    @Override
    public R update(BizPayBack bizPayBack) {
        try {
            Long userId = SystemUtil.getUserId();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);

            bizPayBack.setUpdateBy(sysUser.getLoginName());
            bizPayBack.setUpdateTime(new Date());

            bizPayBackMapper.updateById(bizPayBack);

            return R.ok("修改回款管理成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("修改回款管理失败");
        }
    }


    /**
     * 删除回款管理
     * @param
     * @return
     */
    @Override
    public R delete(Long [] ids) {
        for(Long id: Arrays.asList(ids)){
            BizPayBack bizPayBack = new BizPayBack();
            bizPayBack.setId(id);
            bizPayBack.setDelFlag("1");
            bizPayBackMapper.updateById(bizPayBack);
        }
        return   R.ok("删除成功");
    }
}
