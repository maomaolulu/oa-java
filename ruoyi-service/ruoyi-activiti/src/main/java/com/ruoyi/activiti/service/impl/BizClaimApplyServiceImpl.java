package com.ruoyi.activiti.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.asset.AaSku;
import com.ruoyi.activiti.domain.asset.AaSpu;
import com.ruoyi.activiti.domain.asset.BizClaimApply;
import com.ruoyi.activiti.domain.asset.BizClaimGoods;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.mapper.*;
import com.ruoyi.activiti.service.BizClaimApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.activiti.vo.SkuCheckVo;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zx
 * @date 2021/11/30 18:55
 */
@Service
@Slf4j
public class BizClaimApplyServiceImpl implements BizClaimApplyService {
    private final BizClaimApplyMapper claimApplyMapper;
    private final RemoteUserService remoteUserService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final BizClaimGoodsMapper claimGoodsMapper;
    private final AaSkuMapper aaSkuMapper;
    private final AaSpuMapper aaSpuMapper;
    private final RemoteDeptService remoteDeptService;

    @Autowired
    public BizClaimApplyServiceImpl(BizClaimApplyMapper claimApplyMapper, RemoteUserService remoteUserService, ActReProcdefMapper actReProcdefMapper, IBizBusinessService bizBusinessService, BizClaimGoodsMapper claimGoodsMapper, AaSkuMapper aaSkuMapper, AaSpuMapper aaSpuMapper, RemoteDeptService remoteDeptService) {
        this.claimApplyMapper = claimApplyMapper;
        this.remoteUserService = remoteUserService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.claimGoodsMapper = claimGoodsMapper;
        this.aaSkuMapper = aaSkuMapper;
        this.aaSpuMapper = aaSpuMapper;
        this.remoteDeptService = remoteDeptService;
    }

    /**
     * 新增领用申请
     *
     * @param claimApply
     * @return
     */
    @Override
    public R insert(BizClaimApply claimApply) {
        try {
            // 抄送人去重
            String cc = claimApply.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                claimApply.setCc(cc);
            }
            Date date = new Date();
            String timestamp = String.valueOf(date.getTime());
            String today = DateUtil.today();
            today = today.replace("-", "");
            Long userId = SystemUtil.getUserId();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
            String purchaseCode = "SL" + today + timestamp.substring(0, 12);
            claimApply.setClaimCode(purchaseCode);
            claimApply.setCreateBy(sysUser.getLoginName());
            claimApply.setApplyer(sysUser.getUserName());
            claimApply.setCreateTime(new Date());
            claimApply.setDelFlag("0");
            claimApply.setDeptId(sysUser.getDeptId().intValue());
            claimApply.setTitle(sysUser.getUserName() + "提交的领用申请");
            claimApplyMapper.insertBizClaimApply(claimApply);
            Long applyId = claimApply.getId();
            claimApply.getGoods().stream().forEach(claimGoods -> {
                claimGoods.setClaimId(applyId);
                claimGoods.setDelFlag("0");
                claimGoods.setCreateBy(sysUser.getLoginName());
                claimGoods.setCreateTime(new Date());
                claimGoodsMapper.insert(claimGoods);
            });
            // 初始化流程
            BizBusiness business = initBusiness(claimApply);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(claimApply.getClaimCode());
            bizBusinessService.insertBizBusiness(business);

            Map<String, Object> variables = Maps.newHashMap();
            // 这里可以设置各个负责人，key跟模型的代理变量一致
            variables.put("cc", claimApply.getCc());

            bizBusinessService.startProcess(business, variables);
            return R.ok("提交领用申请成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("提交领用申请失败");
        }
    }

    /**
     * 查询详情
     *
     * @param tableId
     * @return
     */
    @Override
    public BizClaimApply selectBizClaimApplyById(String tableId) {
        BizClaimApply claimApply = claimApplyMapper.selectByPrimaryKey(tableId);
        Example example = new Example(BizClaimGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("claimId", claimApply.getId());
        List<BizClaimGoods> bizClaimGoods = claimGoodsMapper.selectByExample(example);
        bizClaimGoods.stream().forEach(bizClaimGoods1 -> {
            AaSpu aaSpu = aaSpuMapper.selectByPrimaryKey(bizClaimGoods1.getGoodsId());
            bizClaimGoods1.setAaSpu(aaSpu);
        });
        claimApply.setGoods(bizClaimGoods);
        Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(claimApply.getDeptId());
        claimApply.setCompanyName(belongCompany2.get("companyName").toString());
        String ccStr = claimApply.getCc();
        if (StrUtil.isNotBlank(ccStr)) {
            List<String> ccList = new ArrayList<>();
            for (String cc : ccStr.split(",")) {
                SysUser ccUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
                ccList.add(ccUser.getUserName());
            }
            claimApply.setCcName(String.join("、", ccList));
        }

        return claimApply;
    }

    /**
     * 批量删除
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteClaimByIds(String ids) {
        return claimApplyMapper.deleteLogic(Convert.toStrArray(ids));
    }

    /**
     * 检查库存
     *
     * @param skuCheckVos
     * @return
     */
    @Override
    public String checkSku(List<SkuCheckVo> skuCheckVos) {
        String resultStr = "";
        List<String> resultList = new ArrayList<>();
        for (SkuCheckVo skuCheckVo : skuCheckVos) {
//            Example example = new Example(AaSku.class);
//            Example.Criteria criteria = example.createCriteria();
//            criteria.andEqualTo("spuId", skuCheckVo.getSpuId());
//            criteria.andEqualTo("operation", 1);
            QueryWrapper<AaSku> wrapper = new QueryWrapper<>();
            wrapper.eq("spu_id", skuCheckVo.getSpuId());
            wrapper.eq("operation", 1);
            List<AaSku> aaSkus = aaSkuMapper.selectList(wrapper);

//            Example example2 = new Example(AaSku.class);
//            Example.Criteria criteria2 = example2.createCriteria();
//            criteria2.andEqualTo("spuId", skuCheckVo.getSpuId());
//            criteria2.andEqualTo("operation", 2);
            QueryWrapper<AaSku> wrapper2 = new QueryWrapper<>();
            wrapper2.eq("spu_id", skuCheckVo.getSpuId());
            wrapper2.eq("operation", 2);
            List<AaSku> aaSkus2 = aaSkuMapper.selectList(wrapper2);

            long sum = aaSkus.stream().collect(Collectors.summarizingLong(value -> value.getAmount())).getSum();
            long sum2 = aaSkus2.stream().collect(Collectors.summarizingLong(value -> value.getAmount())).getSum();
            long result = sum - sum2;
            if (result < skuCheckVo.getAmount()) {
                resultList.add(skuCheckVo.getName());
            }
        }
        if (!resultList.isEmpty()) {
            resultStr = String.join("、", resultList);
        }
        return resultStr;
    }

    /**
     * 初始化业务流程
     *
     * @param claimApply
     * @return
     * @author zmr
     */
    private BizBusiness initBusiness(BizClaimApply claimApply) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "claim");
        example.setOrderByClause("VERSION_ DESC");
        List<ActReProcdef> actReProcdefs = actReProcdefMapper.selectByExample(example);
        ActReProcdef actReProcdef = new ActReProcdef();
        if (!actReProcdefs.isEmpty()) {
            actReProcdef = actReProcdefs.get(0);
        }
        BizBusiness business = new BizBusiness();
        business.setTableId(claimApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(claimApply.getTitle());
        business.setProcName(actReProcdef.getName());
        long userId = SystemUtil.getUserId();
        business.setUserId(userId);
        SysUser user = remoteUserService.selectSysUserByUserId(userId);
        business.setApplyer(user.getUserName());
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        return business;
    }
}
