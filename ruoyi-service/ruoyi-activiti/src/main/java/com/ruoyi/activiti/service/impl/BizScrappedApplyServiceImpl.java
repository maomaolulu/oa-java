package com.ruoyi.activiti.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.asset.Asset;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.asset.BizScrappedApply;
import com.ruoyi.activiti.mapper.ActReProcdefMapper;
import com.ruoyi.activiti.mapper.BizScrappedApplyMapper;
import com.ruoyi.activiti.service.AssetService;
import com.ruoyi.activiti.service.BizScrappedApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteConfigService;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @description: 报废出库申请
 * @author: zx
 * @date: 2021/11/21 19:09
 */
@Service
@Slf4j
public class BizScrappedApplyServiceImpl implements BizScrappedApplyService {
    private final BizScrappedApplyMapper scrappedApplyMapper;
    private final RemoteUserService remoteUserService;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final RemoteConfigService remoteConfigService;
    private final AssetService assetService;
    private final RemoteDeptService remoteDeptService;

    @Autowired
    public BizScrappedApplyServiceImpl(BizScrappedApplyMapper scrappedApplyMapper, RemoteUserService remoteUserService, ActReProcdefMapper actReProcdefMapper, IBizBusinessService bizBusinessService, RemoteConfigService remoteConfigService, AssetService assetService, RemoteDeptService remoteDeptService) {
        this.scrappedApplyMapper = scrappedApplyMapper;
        this.remoteUserService = remoteUserService;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.remoteConfigService = remoteConfigService;
        this.assetService = assetService;
        this.remoteDeptService = remoteDeptService;
    }

    /**
     * 新增报废出库申请
     *
     * @param scrappedApply
     * @return
     */
    @Override
    public R insert(BizScrappedApply scrappedApply) {
        try {
            // 抄送人去重
            String cc = scrappedApply.getCc();
            if(StrUtil.isNotBlank(cc)){
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                scrappedApply.setCc(cc);
            }
            Date date = new Date();
            String timestamp = String.valueOf(date.getTime());
            String today = DateUtil.today();
            today = today.replace("-", "");

            Long userId = SystemUtil.getUserId();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
            String purchaseCode = "BF" + today + timestamp.substring(0, 12);
            scrappedApply.setApplyCode(purchaseCode);
            scrappedApply.setCreateBy(sysUser.getLoginName());
            scrappedApply.setCreateTime(new Date());
            scrappedApply.setDelFlag("0");
            scrappedApply.setDeptId(sysUser.getDeptId().intValue());
            scrappedApply.setTitle(sysUser.getUserName() + "提交的报废申请");
            scrappedApplyMapper.insertBizScrappedApply(scrappedApply);
            // 初始化流程
            BizBusiness business = initBusiness(scrappedApply);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(scrappedApply.getApplyCode());
            bizBusinessService.insertBizBusiness(business);
            // 获取物品采购价
            Asset asset = assetService.getById(scrappedApply.getAssertId());
            if (null == asset.getPurchasePrice()) {
                return R.error("请先维护采购价格");
            }
            // 获取经营参数
            SysConfig config1 = new SysConfig();
            config1.setConfigKey("params200");
            List<SysConfig> list = remoteConfigService.listOperating(config1);
            SysConfig config2 = new SysConfig();
            config2.setConfigKey("params2000");
            List<SysConfig> list2 = remoteConfigService.listOperating(config2);
            if (list.isEmpty() || list2.isEmpty()) {
                return R.error("请先配置经营参数");
            }

            Map<String, Object> variables = Maps.newHashMap();
            // 这里可以设置各个负责人，key跟模型的代理变量一致
            variables.put("cc", scrappedApply.getCc());
            variables.put("params200", Long.valueOf(list.get(0).getConfigValue()) * 100);
            variables.put("params2000", Long.valueOf(list2.get(0).getConfigValue()) * 100);
            variables.put("money", asset.getPurchasePrice());
            bizBusinessService.startProcess(business, variables);
            return R.ok("提交报废出库申请成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error("提交报废出库申请失败");
        }
    }

    /**
     * 查询详情
     *
     * @param tableId
     * @return
     */
    @Override
    public BizScrappedApply selectBizScrappedApplyById(String tableId) {
        BizScrappedApply bizScrappedApply = scrappedApplyMapper.selectBizScrappedApplyById(Long.valueOf(tableId));
        Asset byId = assetService.getById(bizScrappedApply.getAssertId());
        if (byId != null) {
            // 物品类型
            String assetType = byId.getAssetType();
            String assetType1 = scrappedApplyMapper.getAssetType(assetType);
            // 物品状态
            Integer state = byId.getState();
            String state1 = scrappedApplyMapper.getState(state);
            byId.setAssetTypeName(assetType1);
            byId.setStateName(state1);
            byId.setCompanyName(remoteDeptService.getBelongCompany2(byId.getDeptId()).get("companyName").toString());
            byId.setDeptName(remoteDeptService.selectSysDeptByDeptId(byId.getDeptId()).getDeptName());
            bizScrappedApply.setAsset(byId);
        }
        bizScrappedApply.setApplyTime(DateUtil.format(bizScrappedApply.getCreateTime(), "yyyy-MM-dd HH:mm"));
        Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(bizScrappedApply.getDeptId());
        bizScrappedApply.setCompanyName(belongCompany2.get("companyName").toString());
        String cc = bizScrappedApply.getCc();
        if(StrUtil.isNotBlank(cc)){

            String[] split = cc.split(",");
            List<String> ccList = new ArrayList<>();
            for (String s : split) {
                SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.valueOf(s));
                ccList.add(sysUser.getUserName());
            }
            bizScrappedApply.setCcName(String.join("、",ccList));
        }
        //打印人赋值
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        bizScrappedApply.setPdfName(sysUser.getUserName());
        return bizScrappedApply;
    }

    @Override
    public void test(Long deptId) {

        remoteDeptService.getBelongCompany2(deptId).get("companyName").toString();
    }

    /**
     * 初始化业务流程
     *
     * @param scrappedApply
     * @return
     * @author zmr
     */
    private BizBusiness initBusiness(BizScrappedApply scrappedApply) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isBlank(scrappedApply.getProcessKey())){
            scrappedApply.setProcessKey("scrapped");
        }
        criteria.andEqualTo("key", scrappedApply.getProcessKey());
        example.setOrderByClause("VERSION_ DESC");
        List<ActReProcdef> actReProcdefs = actReProcdefMapper.selectByExample(example);
        ActReProcdef actReProcdef = new ActReProcdef();
        if (!actReProcdefs.isEmpty()) {
            actReProcdef = actReProcdefs.get(0);
        }
        BizBusiness business = new BizBusiness();
        business.setTableId(scrappedApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(scrappedApply.getTitle());
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
