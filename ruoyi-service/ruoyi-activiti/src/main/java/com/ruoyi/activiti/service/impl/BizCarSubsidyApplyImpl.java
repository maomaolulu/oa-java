package com.ruoyi.activiti.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.BizAssociateApply;
import com.ruoyi.activiti.domain.SysAttachment;
import com.ruoyi.activiti.domain.car.BizCarOil;
import com.ruoyi.activiti.domain.car.BizCarRegistration;
import com.ruoyi.activiti.domain.car.BizCarSubsidyApply;
import com.ruoyi.activiti.domain.car.BizReserveDetail;
import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.feign.RemoteFileService;
import com.ruoyi.activiti.mapper.*;
import com.ruoyi.activiti.service.BizCarSubsidyApplyService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.CollectUtil;
import com.ruoyi.activiti.utils.FileUtil;
import com.ruoyi.activiti.vo.CarSubsidyApplyVO;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.DataScopeUtil;
import com.ruoyi.system.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 还车补贴申请
 *
 * @author zh
 * @date 2022-02-24
 */
@Service
@Slf4j
public class BizCarSubsidyApplyImpl implements BizCarSubsidyApplyService {
    private final BizCarSubsidyApplyMapper bizCarSubsidyApplyMapper;
    private final ActReProcdefMapper actReProcdefMapper;
    private final IBizBusinessService bizBusinessService;
    private final RemoteDeptService remoteDeptService;
    private final BizAssociateApplyMapper associateApplyMapper;
    private final RemoteFileService remoteFileService;
    private final BizCarOilMapper bizCarOilMapper;
    private final BizCarRegistrationMapper carRegistrationMapper;
    private final BizReserveDetailMapper reserveDetailMapper;
    @Autowired
    private BizAuditMapper auditMapper;
    @Autowired
    private DataScopeUtil dataScopeUtil;
    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    public BizCarSubsidyApplyImpl(BizCarSubsidyApplyMapper bizCarSubsidyApplyMapper, ActReProcdefMapper actReProcdefMapper, IBizBusinessService bizBusinessService, RemoteDeptService remoteDeptService, BizAssociateApplyMapper associateApplyMapper, RemoteFileService remoteFileService, BizCarOilMapper bizCarOilMapper, BizCarRegistrationMapper carRegistrationMapper, BizReserveDetailMapper reserveDetailMapper) {
        this.bizCarSubsidyApplyMapper = bizCarSubsidyApplyMapper;
        this.actReProcdefMapper = actReProcdefMapper;
        this.bizBusinessService = bizBusinessService;
        this.remoteDeptService = remoteDeptService;
        this.associateApplyMapper = associateApplyMapper;
        this.remoteFileService = remoteFileService;
        this.bizCarOilMapper = bizCarOilMapper;
        this.carRegistrationMapper = carRegistrationMapper;
        this.reserveDetailMapper = reserveDetailMapper;
    }

    @Override
    public List<BizCarSubsidyApply> listAllPage(BizCarSubsidyApply bizCarSubsidyApply) {
        QueryWrapper<BizCarSubsidyApply> bizCarSubsidyApplyQueryWrapper = new QueryWrapper<>();
        bizCarSubsidyApplyQueryWrapper.eq("bu.del_flag", "0");
        bizCarSubsidyApplyQueryWrapper.eq("ca.del_flag", "0");
        bizCarSubsidyApplyQueryWrapper.eq("bu.proc_def_key", "carSubsidyApply");
        bizCarSubsidyApplyQueryWrapper.eq(bizCarSubsidyApply.getId() != null, "ca.id", bizCarSubsidyApply.getId());
        // 部门
        bizCarSubsidyApplyQueryWrapper.eq(bizCarSubsidyApply.getBelongDeptId() != null, "ca.belong_dept_id", bizCarSubsidyApply.getBelongDeptId());
        // 公司
        bizCarSubsidyApplyQueryWrapper.eq(bizCarSubsidyApply.getBelongCompanyId() != null, "ca.belong_company_id", bizCarSubsidyApply.getBelongCompanyId());
        /**申请人*/
        bizCarSubsidyApplyQueryWrapper.like(StrUtil.isNotBlank(bizCarSubsidyApply.getCreateByName()), "u.user_name", bizCarSubsidyApply.getCreateByName());
        /**用车申请编号*/
        bizCarSubsidyApplyQueryWrapper.like(StrUtil.isNotBlank(bizCarSubsidyApply.getSubsidyCode()), "ca.subsidy_code", bizCarSubsidyApply.getSubsidyCode());
        /**车辆属性*/
        bizCarSubsidyApplyQueryWrapper.eq(bizCarSubsidyApply.getCarTypes() != null, "ca.car_types", bizCarSubsidyApply.getCarTypes());
        /**牌照号*/
        bizCarSubsidyApplyQueryWrapper.like(StrUtil.isNotBlank(bizCarSubsidyApply.getPlateNumber()), "ca.plate_number", bizCarSubsidyApply.getPlateNumber());
        /**审批状态*/
        bizCarSubsidyApplyQueryWrapper.eq(bizCarSubsidyApply.getResult() != null, "bu.result", bizCarSubsidyApply.getResult());
        /**审批结果*/
        bizCarSubsidyApplyQueryWrapper.eq(bizCarSubsidyApply.getStatus() != null, "bu.status", bizCarSubsidyApply.getStatus());
        /**申请时间*/
        String createTime1 = bizCarSubsidyApply.getCreateTime1();
        String createTime2 = bizCarSubsidyApply.getCreateTime2();

        bizCarSubsidyApplyQueryWrapper.between(StrUtil.isNotBlank(createTime1) && StrUtil.isNotBlank(createTime2), "ca.create_time", createTime1, createTime2);
        // 实际用车日期
        String useCarTime1 = bizCarSubsidyApply.getStillCarDate1();
        String useCarTime2 = bizCarSubsidyApply.getStillCarDate2();
        bizCarSubsidyApplyQueryWrapper.between(StrUtil.isNotBlank(useCarTime1) && StrUtil.isNotBlank(useCarTime2), "ca.use_car_date", useCarTime1, useCarTime2 + " 23:59:59");
        bizCarSubsidyApplyQueryWrapper.orderByDesc("ca.id");
        //数据权限
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        List<Long> roleIds = sysUser.getRoleIds();

        if (bizCarSubsidyApply.getOneStatus() == null) {
            String sql = "";
            // 是车辆管理员
            if (roleIds.lastIndexOf(112L) != -1 || roleIds.lastIndexOf(1L) != -1) {
                List<SysRole> roles = sysUser.getRoles();
                for (SysRole role : roles) {
                    if (role.getRoleId() == 112 || role.getRoleId() == 1) {
                        role.setDataScope("6");

                    }
                }
                sql = dataScopeUtil.getScopeSql(sysUser, "d2", null);

            } else {


                sql = dataScopeUtil.getScopeSql(sysUser, "d", null);
            }
            if (StrUtil.isNotBlank(sql)) {
                bizCarSubsidyApplyQueryWrapper.apply(sql);
            }


        }
        List<BizCarSubsidyApply> bizCarSubsidyApplys = bizCarSubsidyApplyMapper.selectAll(bizCarSubsidyApplyQueryWrapper);
        // 2022-9-7 修改 wy------start
        Map<Long, String> map = remoteDeptService.listEnable2Map();
        List<CarSubsidyApplyVO> list = auditMapper.getList();
        Map<String, List<CarSubsidyApplyVO>> collect = list.stream().collect(Collectors.groupingBy(CarSubsidyApplyVO::getId));
        for (BizCarSubsidyApply bizCarSubsidyApply1 : bizCarSubsidyApplys
        ) {
            if (bizCarSubsidyApply1.getBelongDeptId() != null) {

                //隶属公司名称
                bizCarSubsidyApply1.setCompanyUseName(map.get(bizCarSubsidyApply1.getBelongCompanyId()));
                //隶属部门名称
                bizCarSubsidyApply1.setDeptUseName(map.get(bizCarSubsidyApply1.getBelongDeptId()));
            }
            //取审批完成时间
            List<CarSubsidyApplyVO> carSubsidyApplyVOS = collect.get(bizCarSubsidyApply1.getProcInstId().toString());
            if (carSubsidyApplyVOS != null) {
                List<CarSubsidyApplyVO> collect1 = carSubsidyApplyVOS.stream().sorted(new Comparator<CarSubsidyApplyVO>() {
                    @Override
                    public int compare(CarSubsidyApplyVO o1, CarSubsidyApplyVO o2) {
                        return o2.getTime().compareTo(o1.getTime());
                    }
                }).collect(Collectors.toList());
                LocalDateTime time = collect1.get(0).getTime();
                if (time != null) {
                    bizCarSubsidyApply1.setEndTime(DateUtil.format(time, "yyyy-MM-dd HH:mm:ss"));
                }
            }
            // 2022-9-7 修改 wy------end


            //属性
            if (bizCarSubsidyApply1.getCarTypes() != null) {
                if (bizCarSubsidyApply1.getCarTypes() == 0) {
                    bizCarSubsidyApply1.setCarTypesName("公车");
                } else if (bizCarSubsidyApply1.getCarTypes() == 1) {
                    bizCarSubsidyApply1.setCarTypesName("私车");
                } else if (bizCarSubsidyApply1.getCarTypes() == 2) {
                    bizCarSubsidyApply1.setCarTypesName("租车");
                }
            }
        }

        return bizCarSubsidyApplys;
    }

    @Override
    public BizCarSubsidyApply selectOne(Long tableId) {
        BizCarSubsidyApply bizCarSubsidyApply = new BizCarSubsidyApply();
        bizCarSubsidyApply.setId(tableId);
        bizCarSubsidyApply.setOneStatus(1);
        List<BizCarSubsidyApply> bizCarSubsidyApplys = listAllPage(bizCarSubsidyApply);

        BizCarSubsidyApply bizCarSubsidyApply1 = new BizCarSubsidyApply();
        if (bizCarSubsidyApplys != null && bizCarSubsidyApplys.size() > 0) {
            bizCarSubsidyApply1 = bizCarSubsidyApplys.get(0);
            // 抄送人赋值
            if (StrUtil.isNotBlank(bizCarSubsidyApply1.getCc())) {
                String[] split = bizCarSubsidyApply1.getCc().split(",");
                List<String> ccList = new ArrayList<>();
                for (String cc : split) {
                    SysUser sysUser = remoteUserService.selectSysUserByUserId(Long.valueOf(cc));
                    if (sysUser == null) {
                        ccList.add("");

                    } else {

                        ccList.add(sysUser.getUserName());
                    }
                }
                bizCarSubsidyApply1.setCcName(String.join(",", ccList));
            }

            // 获取关联审批单
            QueryWrapper<BizAssociateApply> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("baa.types", "carSubsidyApply");
            wrapper1.eq("baa.apply_id", bizCarSubsidyApply1.getId());
            List<Map<String, Object>> associate = associateApplyMapper.getAssociate(wrapper1);
            for (Map<String, Object> map : associate) {
                List<BizBusiness> business = bizBusinessService.selectBizBusinessListAll(new BizBusiness().setId(Long.valueOf(map.get("associateApply").toString())).setProcDefKey(map.get("associateTypes").toString()));
                if (business.isEmpty()) {
                    map.put("result", "");
                    map.put("auditors", "");
                } else {
                    map.put("result", business.get(0).getResult());
                    map.put("auditors", business.get(0).getAuditors());
                }
            }
            bizCarSubsidyApply1.setAssociateApply(associate);
//            // 导航路线截图
//            List<SysAttachment> navigationImgs = remoteFileService.getList(bizCarSubsidyApply1.getId(), "navigation-imgs");
//            if (navigationImgs == null) {
//                navigationImgs = new ArrayList<>();
//            }
            // 出车前、出车后车内外卫生照片
            List<SysAttachment> hygieneImgs = remoteFileService.getList(bizCarSubsidyApply1.getId(), "hygiene-imgs");
            if (hygieneImgs == null) {
                hygieneImgs = new ArrayList<>();
            }
            // 出车前、出车后里程照
            List<SysAttachment> mileageImgs = remoteFileService.getList(bizCarSubsidyApply1.getId(), "mileage-imgs");
            if (mileageImgs == null) {
                mileageImgs = new ArrayList<>();
            }
//            bizCarSubsidyApply1.setNavigationImgs(navigationImgs);
            bizCarSubsidyApply1.setHygieneImgs(hygieneImgs);
            bizCarSubsidyApply1.setMileageImgs(mileageImgs);
        }
        QueryWrapper<BizCarOil> wrapper = new QueryWrapper<>();
        wrapper.eq("apply_id", bizCarSubsidyApply1.getId());
        List<BizCarOil> bizCarOils = bizCarOilMapper.selectList(wrapper);
        if (bizCarOils.isEmpty()) {
            bizCarOils = new ArrayList<>();
        }
        bizCarSubsidyApply1.setOils(bizCarOils);
        return bizCarSubsidyApply1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BizCarSubsidyApply save(BizCarSubsidyApply bizCarSubsidyApply) {
        try {
            final BigDecimal bm = bizCarSubsidyApply.getBackMileage();
            log.info(bizCarSubsidyApply.getSubsidyCode() + "还车时里程" + bm + "（公里）");
            // 抄送人去重
            String cc = bizCarSubsidyApply.getCc();
            if (StrUtil.isNotBlank(cc)) {
                cc = String.join(",", CollectUtil.twoClear(cc.split(",")));
                bizCarSubsidyApply.setCc(cc);
            }
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyId1 = belongCompany2.get("companyId").toString();
            bizCarSubsidyApply.setDeptId(sysUser.getDeptId());
            bizCarSubsidyApply.setCompanyId(Long.valueOf(companyId1));
            bizCarSubsidyApply.setCreateBy(SystemUtil.getUserId());
            bizCarSubsidyApply.setTitle(sysUser.getUserName() + "提交的还车补贴申请");
            bizCarSubsidyApply.setStillCarDate(new Date());

            Map<String, Object> belongCompany3 = remoteDeptService.getBelongCompany2(bizCarSubsidyApply.getBelongDeptId());
            String belongCompanyId = belongCompany3.get("companyId").toString();
            bizCarSubsidyApply.setBelongCompanyId(Long.valueOf(belongCompanyId));


//            // 导航路线截图
//            List<SysAttachment> navigationImgs = remoteFileService.getList(bizCarSubsidyApply.getId(), "navigation-imgs");
//            if (navigationImgs == null) {
//                throw new StatefulException("导航路线截图不能为空");
//            }
//            // 出车前、出车后车内外卫生照片
//            List<SysAttachment> hygieneImgs = remoteFileService.getList(bizCarSubsidyApply.getId(), "hygiene-imgs");
//            if (hygieneImgs == null) {
//                throw new StatefulException("出车前、出车后车内外卫生照片不能为空");
//            }
//            // 出车前、出车后里程照
//            List<SysAttachment> mileageImgs = remoteFileService.getList(bizCarSubsidyApply.getId(), "mileage-imgs");
//            if (mileageImgs == null) {
//                throw new StatefulException("出车前、出车后里程照不能为空");
//            }
            bizCarSubsidyApplyMapper.insert(bizCarSubsidyApply);

            // 关联多个审批单
            List<Map<String, Object>> associateApply = bizCarSubsidyApply.getAssociateApply();
            if (associateApply == null) {
                throw new StatefulException("请关联用车申请");
//                associateApply = new ArrayList<>();
            }
            //审批单号
            List<String> strings = new ArrayList<>();
            // 去重
            associateApply = associateApply.stream().distinct().collect(Collectors.toList());
            associateApply.stream().forEach(associate -> {
                BizAssociateApply associateApply1 = new BizAssociateApply();
                associateApply1.setApplyId(bizCarSubsidyApply.getId());
                associateApply1.setTypes("carSubsidyApply");
                associateApply1.setAssociateApply(Long.valueOf(associate.get("associateApply").toString()));
                associateApply1.setAssociateTitle(associate.get("associateTitle").toString());
                associateApply1.setAssociateTypes(associate.get("associateTypes").toString());
                associateApply1.setCreateTime(new Date());
                associateApply1.setCreateBy(SystemUtil.getUserId().toString());
                strings.add((associate.get("applyCode").toString()));
                associateApplyMapper.insert(associateApply1);
            });
            // 保存加油明细
            bizCarSubsidyApply.getOils().stream().forEach(bizCarOil -> {
                bizCarOil.setApplyId(bizCarSubsidyApply.getId());
                bizCarOilMapper.insert(bizCarOil);
            });
            //审批单号
            String join = String.join(",", strings);
            bizCarSubsidyApply.setRelationCode(join);
            bizCarSubsidyApplyMapper.updateById(bizCarSubsidyApply);
            // 初始化流程
            BizBusiness business = initBusiness(bizCarSubsidyApply);
            business.setCompanyId(SystemUtil.getCompanyId());
            business.setCompanyName(SystemUtil.getCompanyName());
            business.setDeptId(SystemUtil.getDeptId());
            business.setDeptName(sysUser.getDept().getDeptName());
            business.setApplyCode(bizCarSubsidyApply.getSubsidyCode());
            bizBusinessService.insertBizBusiness(business);
            Map<String, Object> variables = Maps.newHashMap();
            variables.put("cc", bizCarSubsidyApply.getCc());
            bizBusinessService.startProcess(business, variables);
            // 如果是公车
            if (bizCarSubsidyApply.getCarTypes() == 0 && !associateApply.isEmpty()) {
                // 车辆变为空闲,没有关联用车申请则不需要变更
                String associateApply1 = associateApply.get(0).get("associateApply").toString();
                BizBusiness business1 = bizBusinessService.selectBizBusinessById2(associateApply1);
                QueryWrapper<BizReserveDetail> wrapper1 = new QueryWrapper<>();
                wrapper1.eq("apply_id", business1.getTableId());
                BizReserveDetail reserveDetail = reserveDetailMapper.selectOne(wrapper1);
                reserveDetail.setDelFlag("1");
                reserveDetail.setUpdateTime(new Date());
                reserveDetail.setUpdateBy(sysUser.getLoginName());
                reserveDetail.setRemark("还车补贴申请");
                reserveDetailMapper.updateById(reserveDetail);
                // 更新最新里程数
                QueryWrapper<BizCarRegistration> wrapper = new QueryWrapper<>();
                wrapper.eq("plate_number", reserveDetail.getPlateNumber());
                BizCarRegistration bizCarRegistration = carRegistrationMapper.selectOne(wrapper);
                // 还车时里程（公里）大于0更新最新里程数
                if (bm.compareTo(BigDecimal.ZERO) == 1) {
                    bizCarRegistration.setLatestMileage(bm);
                }
                carRegistrationMapper.updateById(bizCarRegistration);
            }
            // 将上传的临时文件转为有效文件
            int reimburse = remoteFileService.update(bizCarSubsidyApply.getId(), bizCarSubsidyApply.getSubsidyCode());
            if (reimburse == 0) {
                throw new StatefulException("将上传的文件转为有效文件失败");
            }
            return bizCarSubsidyApply;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    @Override
    public BizCarSubsidyApply update(BizCarSubsidyApply bizCarSubsidyApply) {
        Long userId = SystemUtil.getUserId();
        SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
        bizCarSubsidyApply.setUpdateBy(sysUser.getUserName());
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long[] ids) {
        try {

            for (Long id : ids
            ) {
                BizCarSubsidyApply bizCarSubsidyApply = new BizCarSubsidyApply();
                bizCarSubsidyApply.setId(id);
                bizCarSubsidyApply.setDelFlag("1");
                bizCarSubsidyApplyMapper.updateById(bizCarSubsidyApply);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    /**
     * 初始化业务流程
     *
     * @param bizCarSubsidyApply
     * @return
     * @author zmr
     */
    private BizBusiness initBusiness(BizCarSubsidyApply bizCarSubsidyApply) {
        // 查出最新发布的流程定义信息
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", "carSubsidyApply");
        example.setOrderByClause("VERSION_ DESC");
        List<ActReProcdef> actReProcdefs = actReProcdefMapper.selectByExample(example);
        ActReProcdef actReProcdef = new ActReProcdef();
        if (!actReProcdefs.isEmpty()) {
            actReProcdef = actReProcdefs.get(0);
        }
        BizBusiness business = new BizBusiness();
        business.setTableId(bizCarSubsidyApply.getId().toString());
        business.setProcDefId(actReProcdef.getId());
        business.setTitle(bizCarSubsidyApply.getTitle());
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

    @Override
    public void download(List<BizCarSubsidyApply> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();

        for (int i = 0; i < all.size(); i++) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("审批编号 ", all.get(i).getSubsidyCode());
            map.put("标题", all.get(i).getTitle());
            map.put("审批状态", getStatus(all.get(i).getStatus()));
            map.put("审批结果", getResult(all.get(i).getResult()));
            map.put("发起时间", DateUtil.format(all.get(i).getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            map.put("审批结束时间时间", all.get(i).getEndTime());
            map.put("发起人姓名", all.get(i).getCreateByName());
            map.put("发起人部门", all.get(i).getDeptName());
            map.put("关联审批单(用车申请）", all.get(i).getUpdateTime());
            map.put("所属公司", all.get(i).getCompanyUseName());
            map.put("所属部门", all.get(i).getDeptUseName());
            map.put("牌照号", all.get(i).getPlateNumber());
            map.put("车辆属性", all.get(i).getCarTypesName());
            map.put("实际用车日期", all.get(i).getUseCarDate());
            map.put("实际还车日期", all.get(i).getStillCarDate());
            map.put("出车前里程(公里)", all.get(i).getGoMileage());
            map.put("还车时里程(公里)", all.get(i).getBackMileage());
            map.put("行驶里程数", all.get(i).getTravelMileage());
            map.put("申请补贴里程数", all.get(i).getApplyMileage());
            map.put("加油里程", all.get(i).getFuelMileage());
            map.put("加油金额", all.get(i).getFuelMoney());
            map.put("本次出行加油金额", all.get(i).getMoney());
            map.put("本次出行加油金额(大写)", all.get(i).getCapitalizeMoney());
            map.put("备注", all.get(i).getRemark());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response, 0);
    }

    @Override
    public void export(List<BizCarSubsidyApply> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();

        for (int i = 0; i < all.size(); i++) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("申请人", all.get(i).getCreateByName());
            map.put("所属公司",all.get(i).getCompanyUseName());
            map.put("所属部门", all.get(i).getDeptName());
            map.put("审批编号 ", all.get(i).getSubsidyCode());
            map.put("标题", all.get(i).getTitle());
            map.put("关联审批单号", all.get(i).getRelationCode());
            map.put("车辆属性", all.get(i).getCarTypesName());
            map.put("牌照号", all.get(i).getPlateNumber());
            map.put("实际用车日期", all.get(i).getUseCarDate());
            map.put("实际还车日期", all.get(i).getStillCarDate());
            map.put("出车前里程(公里)", all.get(i).getGoMileage());
            map.put("还车时里程(公里)", all.get(i).getBackMileage());
            map.put("行驶里程数", all.get(i).getTravelMileage());
            map.put("申请补贴里程数", all.get(i).getApplyMileage());
            map.put("加油里程", all.get(i).getFuelMileage());
            map.put("加油金额", all.get(i).getFuelMoney());
            map.put("本次出行加油金额", all.get(i).getMoney());
            map.put("车辆情况备注", all.get(i).getRemark());
//            map.put("创建人", all.get(i).getCreateBy());
            map.put("创建时间", all.get(i).getCreateTime());
            map.put("更新人", all.get(i).getUpdateBy());
            map.put("更新时间", all.get(i).getUpdateTime());
            map.put("大写总金额", all.get(i).getCapitalizeMoney());
            map.put("最近里程数", all.get(i).getLatestMileage());
            map.put("目的地", all.get(i).getDestination());
            map.put("车辆情况1完好2其他", all.get(i).getVehicleCondition());
            map.put("备注", all.get(i).getRemark());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response, 0);
    }

    private String getResult(Integer resultNum) {
        String result = "";
        switch (resultNum) {
            case 1:
                result = "审批中";
                break;
            case 2:
                result = "通过";
                break;
            case 3:
                result = "驳回";
                break;
            case 4:
                result = "撤销";
                break;
            default:
                break;
        }
        return result;
    }

    private String getStatus(Integer statusNum) {
        String status = "";
        switch (statusNum) {
            case 1:
                status = "审批中";
                break;
            case 2:
                status = "结束";
                break;
            case 3:
                status = "撤销";
                break;
            case 4:
                status = "中止";
                break;
            default:
                break;
        }
        return status;
    }
}
