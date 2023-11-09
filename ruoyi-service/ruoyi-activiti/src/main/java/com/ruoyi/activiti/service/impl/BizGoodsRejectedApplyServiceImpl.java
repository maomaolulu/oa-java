package com.ruoyi.activiti.service.impl;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.*;
import com.ruoyi.activiti.domain.asset.AaSku;
import com.ruoyi.activiti.domain.asset.AaSpu;
import com.ruoyi.activiti.domain.asset.Asset;
import com.ruoyi.activiti.domain.dto.CapitalGoodsDto;
import com.ruoyi.activiti.domain.dto.GoodsRejectedApplyDto;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.purchase.BizGoodsRejectedApply;
import com.ruoyi.activiti.domain.purchase.BizGoodsRejectedDetail;
import com.ruoyi.activiti.domain.purchase.BizPurchaseApply;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.*;
import com.ruoyi.activiti.service.BizGoodsRejectedApplyService;
import com.ruoyi.activiti.service.BizGoodsRejectedDetailService;
import com.ruoyi.activiti.service.BizPurchaseListService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.system.util.DataScopeUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * lx
 * 22/01/14
 */
@Service
@Slf4j
public class BizGoodsRejectedApplyServiceImpl implements BizGoodsRejectedApplyService {
    @Autowired
    private BizGoodsRejectedApplyMapper goodsRejectedApplyMapper;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private BizGoodsRejectedDetailMapper goodsRejectedDetailMapper;
    @Autowired
    private BizGoodsRejectedDetailService goodsRejectedDetailService;
    @Autowired
    private AaSpuMapper aaSpuMapper;
    @Autowired
    private AaSkuMapper aaSkuMapper;//2
    @Autowired
    private RemoteFileService remoteFileService;
    @Autowired
    private AssetMapper assetMapper;//1
    @Autowired
    private BizPurchaseListService purchaseListService;
    @Autowired
    private BizBusinessMapper bizBusinessMapper;
    @Autowired
    private ActReProcdefMapper actReProcdefMapper;
    @Autowired
    private DataScopeUtil dataScopeUtil;
    @Autowired
    private IBizBusinessService businessService;

    private final RemoteDeptService remoteDeptService;

    @Autowired
    public BizGoodsRejectedApplyServiceImpl(RemoteDeptService remoteDeptService) {
        this.remoteDeptService = remoteDeptService;
    }


    /**
     * 按条件查询固定资产退货列表
     *
     * @param capitalGoodsDto
     * @return
     */
    @Override
    public List<CapitalGoodsDto> getAssetList(CapitalGoodsDto capitalGoodsDto) {
        QueryWrapper<CapitalGoodsDto> capitalGoodsDtoQueryWrapper = new QueryWrapper<>();
        capitalGoodsDtoQueryWrapper.like(StringUtils.isNoneBlank(capitalGoodsDto.getPurchaseCode()), "pur.purchase_code", capitalGoodsDto.getPurchaseCode())
                .like(StringUtils.isNoneBlank(capitalGoodsDto.getName()), "aa.name", capitalGoodsDto.getName())
                .like(StringUtils.isNoneBlank(capitalGoodsDto.getAssetSn()), "aa.asset_sn", capitalGoodsDto.getAssetSn())
                .like(StringUtils.isNoneBlank(capitalGoodsDto.getApplicant()), "u.user_name", capitalGoodsDto.getApplicant())
                .like(capitalGoodsDto.getDeptId() != null, "aa.dept_id", capitalGoodsDto.getDeptId())
                .eq(capitalGoodsDto.getCompanyId() != null, "aa.company_id", capitalGoodsDto.getCompanyId())
                .like(StringUtils.isNoneBlank(capitalGoodsDto.getOperator()), "aa.operator", capitalGoodsDto.getOperator())
                .ne("aa.state", 9);

        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        String sql = dataScopeUtil.getScopeSql(sysUser, "d", null);

        if (StrUtil.isNotBlank(sql)) {
            capitalGoodsDtoQueryWrapper.apply(sql);
        }
        return goodsRejectedApplyMapper.selectAssetList(capitalGoodsDtoQueryWrapper);
    }

    /**
     * 按条件查询流动资产退货列表
     *
     * @param capitalGoodsDto
     * @return
     */
    @Override
    public List<CapitalGoodsDto> getSpuList(CapitalGoodsDto capitalGoodsDto) {
        QueryWrapper<CapitalGoodsDto> capitalGoodsDtoQueryWrapper = new QueryWrapper<>();
        capitalGoodsDtoQueryWrapper.like(StringUtils.isNoneBlank(capitalGoodsDto.getPurchaseCode()), "pur.purchase_code", capitalGoodsDto.getPurchaseCode())
                .like(StringUtils.isNoneBlank(capitalGoodsDto.getName()), "ap.name", capitalGoodsDto.getName())
                .like(StringUtils.isNoneBlank(capitalGoodsDto.getSpuSn()), "ap.spu_sn", capitalGoodsDto.getSpuSn())
                .like(StringUtils.isNoneBlank(capitalGoodsDto.getApplicant()), "u.user_name", capitalGoodsDto.getApplicant())
                .like(StringUtils.isNoneBlank(capitalGoodsDto.getOperator()), "aas.operator", capitalGoodsDto.getOperator())
                .apply(" aas.order_id is not null and aas.order_id <> '' ").orderByDesc("aas.create_time");
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        String sql = dataScopeUtil.getScopeSql(sysUser, "d", null);

        if (StrUtil.isNotBlank(sql)) {
            capitalGoodsDtoQueryWrapper.apply(sql);
        }

        return goodsRejectedApplyMapper.selectSpuList(capitalGoodsDtoQueryWrapper);
    }


    /**
     * 点击退货申请获取物品信息--固定资产asset
     *
     * @param idList
     * @return
     */
    @Override
    public List<GoodsRejectedApplyDto> getAsset(List<Long> idList) {
        List<GoodsRejectedApplyDto> goodsRejectedApplyDtoList = new ArrayList<>();
        for (Long id : idList) {
            GoodsRejectedApplyDto goodsRejectedApplyDto = new GoodsRejectedApplyDto();
            Asset asset = assetMapper.selectById(id);
            goodsRejectedApplyDto.setAmount(asset.getPurchaseAmount());
            goodsRejectedApplyDto.setItemSn(asset.getAssetSn());
            goodsRejectedApplyDto.setModel(asset.getModel());
            goodsRejectedApplyDto.setName(asset.getName());
            goodsRejectedApplyDto.setUnit(asset.getUnit());
            BizPurchaseApply bizPurchaseApply = purchaseListService.selectByGoodId(asset.getOrderId());
            goodsRejectedApplyDto.setPurchaseCode(bizPurchaseApply.getPurchaseCode());
            goodsRejectedApplyDtoList.add(goodsRejectedApplyDto);
        }
        return goodsRejectedApplyDtoList;
    }

    /**
     * 点击退货申请获取物品信息--流动资产sku
     *
     * @param idList
     * @return
     */
    @Override
    public List<GoodsRejectedApplyDto> getSpu(List<Long> idList) {
        List<GoodsRejectedApplyDto> goodsRejectedApplyDtoList = new ArrayList<>();
        for (Long id : idList) {
            GoodsRejectedApplyDto goodsRejectedApplyDto = new GoodsRejectedApplyDto();
            AaSku sku = aaSkuMapper.selectById(id);
            AaSpu spu = aaSpuMapper.selectByPrimaryKey(sku.getSpuId());
            goodsRejectedApplyDto.setUnit(spu.getUnit());
            goodsRejectedApplyDto.setName(spu.getName());
            goodsRejectedApplyDto.setModel(spu.getModel());
            goodsRejectedApplyDto.setItemSn(spu.getSpuSn());
            BizPurchaseApply bizPurchaseApply = purchaseListService.selectByGoodId(Long.valueOf(sku.getOrderId()));
            goodsRejectedApplyDto.setPurchaseCode(bizPurchaseApply.getPurchaseCode());
            goodsRejectedApplyDtoList.add(goodsRejectedApplyDto);
        }
        return goodsRejectedApplyDtoList;
    }

    /**
     * 点击提交退货申请
     *
     * @param goodsRejectedApply
     * @return
     */
    @Override
    public int insert(BizGoodsRejectedApply goodsRejectedApply) {

        try {
            // 抄送人去重
            String cc = goodsRejectedApply.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                goodsRejectedApply.setCc(cc);
            }

            String userName = SystemUtil.getUserName();
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            goodsRejectedApply.setCreateBy(userName);
            goodsRejectedApply.setUpdateBy(userName);
            goodsRejectedApply.setUpdateTime(new Date());
            goodsRejectedApply.setCreateTime(new Date());
            goodsRejectedApply.setDelFlag(0);
            goodsRejectedApply.setDeptId(sysUser.getDeptId());
            goodsRejectedApply.setTitle(sysUser.getUserName() + "提交的退货申请");
            int insert = goodsRejectedApplyMapper.insert(goodsRejectedApply);
            List<BizGoodsRejectedDetail> goodsRejectedDetailList = goodsRejectedApply.getGoodsRejectedDetailList();
            if (goodsRejectedDetailList.isEmpty()) {
                return 3;
            }
            for (BizGoodsRejectedDetail goodsRejectedDetail : goodsRejectedDetailList) {
                goodsRejectedDetail.setGoodsRejectedId(goodsRejectedApply.getId());
                goodsRejectedDetailMapper.insert(goodsRejectedDetail);
            }
            // 初始化流程
            BizBusiness business = initBusiness(goodsRejectedApply);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(goodsRejectedApply.getApplyCode());
            bizBusinessMapper.insert(business);
            Map<String, Object> variables = Maps.newHashMap();
            // 这里可以设置各个负责人，key跟模型的代理变量一致
            variables.put("cc", goodsRejectedApply.getCc());
            businessService.startProcess(business, variables);

            // 将上传的临时文件转为有效文件
            int reimburse = remoteFileService.update(goodsRejectedApply.getId(), goodsRejectedApply.getApplyCode());
            if (reimburse == 0) {
                throw new StatefulException("将上传的文件转为有效文件失败");
            }
            return 1;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    public static void main(String[] args) {
        BizGoodsRejectedApply bizGoodsRejectedApply = new BizGoodsRejectedApply();
        List<BizGoodsRejectedDetail> rejectedDetails = new ArrayList<>();
        rejectedDetails.add(new BizGoodsRejectedDetail());
        bizGoodsRejectedApply.setGoodsRejectedDetailList(rejectedDetails);
        System.out.println(bizGoodsRejectedApply);
    }

    /**
     * 通过流程ID获取详情
     *
     * @param id
     * @return
     */
    @Override
    public BizGoodsRejectedApply getInfo(Long id) {
        BizGoodsRejectedApply goodsRejectedApply = goodsRejectedApplyMapper.selectById(id);
        Long deptId = goodsRejectedApply.getDeptId();
        Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(deptId);
        goodsRejectedApply.setCompanyName(belongCompany2.get("companyName").toString());
        List<BizGoodsRejectedDetail> rejectedDetails = goodsRejectedDetailService.getListByGoodsId(id);
        goodsRejectedApply.setGoodsRejectedDetailList(rejectedDetails);
        // 查询附件信息
        // 退货凭证
        List<SysAttachment> certificate = remoteFileService.getList(goodsRejectedApply.getId(), "goods-rejected-img");
        if (certificate == null) {
            certificate = new ArrayList<>();
        }
        // 退货附件
        List<SysAttachment> appendix = remoteFileService.getList(goodsRejectedApply.getId(), "goods-rejected-file");
        if (appendix == null) {
            appendix = new ArrayList<>();
        }
        goodsRejectedApply.setVouchers(certificate);
        goodsRejectedApply.setAttachment(appendix);
        String ccStr = goodsRejectedApply.getCc();
        if (StrUtil.isNotBlank(ccStr)) {
            List<String> ccList = new ArrayList<>();
            for (String cc : ccStr.split(",")) {
                SysUser ccUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
                ccList.add(ccUser.getUserName());
            }
            goodsRejectedApply.setCcName(String.join("、", ccList));
        }
        return goodsRejectedApply;
    }


    /**
     * 初始化业务流程
     *
     * @param goodsRejectedApply
     * @return
     * @author zmr
     */
    private BizBusiness initBusiness(BizGoodsRejectedApply goodsRejectedApply) {
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "goodsRejected");
        example.setOrderByClause("VERSION_ DESC");
        List<ActReProcdef> actReProcdefs = actReProcdefMapper.selectByExample(example);
        ActReProcdef actReProcdef = new ActReProcdef();
        if (!actReProcdefs.isEmpty()) {
            actReProcdef = actReProcdefs.get(0);
        }
        BizBusiness business = new BizBusiness();
        business.setTableId(goodsRejectedApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(goodsRejectedApply.getTitle());
        business.setProcName(actReProcdef.getName());
//        long userId = SystemUtil.getUserId();
        business.setUserId(userId);
        SysUser user = remoteUserService.selectSysUserByUserId(userId);
        business.setApplyer(user.getUserName());
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        business.setDelFlag(false);
        return business;
    }

}
