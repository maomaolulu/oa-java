package com.ruoyi.activiti.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.activiti.domain.asset.ScrappedDto;
import com.ruoyi.activiti.domain.dto.BizOrSignDTO;
import com.ruoyi.activiti.domain.dto.NeedManageDto;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.service.BizOrSignService;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.MapInfoService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.system.util.SystemUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 流程业务
 *
 * @author ruoyi
 * @date 2020-01-06
 * @menu 流程业务
 */
@RestController
@RequestMapping("business")
@Api(tags = {"我的或签"})
public class BizBusinessController extends BaseController {
    @Autowired
    private IBizBusinessService bizBusinessService;
    private final MapInfoService mapInfoService;

    @Resource
    BizOrSignService bizOrSignService;


    @Autowired
    public BizBusinessController(MapInfoService mapInfoService) {
        this.mapInfoService = mapInfoService;
    }

    /**
     * 查询流程业务详情
     */
    @GetMapping("get/{id}")
    public BizBusiness get(@PathVariable("id") String id) {
        return bizBusinessService.selectBizBusinessById(id);
    }

    /**
     * 查询流程业务详情(流程监控)
     */
    @GetMapping("get_all/{id}")
    public BizBusiness getAll(@PathVariable("id") String id) {
        return bizBusinessService.selectBizBusinessById(id);
    }

    /**
     * 流程监控列表
     *
     * @param bizBusiness
     * @return
     */
    @GetMapping("list_all")
    public R listAll(BizBusiness bizBusiness) {
        startPage();
        List<BizBusiness> bizBusinesses = bizBusinessService.selectBizBusinessListAll2(bizBusiness);
        return result(bizBusinesses);
    }

    /**
     * 我的或签
     */
    @ApiOperation("我的或签")
    @GetMapping("orsign")
    public R getOrSign(BizOrSignDTO dto) {
        try {
            startPage();
            List<BizBusiness> orSign = bizOrSignService.getOrSign(dto);
            List<BizBusiness> info = getInfo(orSign);
            return result(info);
        } catch (Exception e) {
            logger.error("获取失败" + e.getMessage(), e);
            return R.error("获取失败");
        }
    }

    /**
     * 流程统计分析1
     *
     * @param bizBusiness
     * @return
     */
    @PostMapping("list_count1")
    public R listCount1(@RequestBody(required = false) BizBusiness bizBusiness) {
        List<Map<String, Object>> count1 = bizBusinessService.selectCount1(bizBusiness);
        return R.data(count1);
    }

    /**
     * 流程统计分析2
     *
     * @param bizBusiness
     * @return
     */
    @PostMapping("list_count2")
    public R listCount2(@RequestBody(required = false) BizBusiness bizBusiness) {
        List<Map<String, Object>> count2 = bizBusinessService.selectCount2(bizBusiness);
        return R.data(count2);
    }

    /**
     * 查询流程业务列表（我的申请）
     */
    @ApiOperation(value = "我的申请", notes = "我的申请")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "sortField", value = "排序列"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向 \"desc\" 或者 \"asc\"")})
    @GetMapping("list/my")
    public R list(BizBusiness bizBusiness) {
        startPage();
        List<BizBusiness> bizBusinesses = bizBusinessService.selectBizBusinessList(bizBusiness);
        List<BizBusiness> info = getInfo(bizBusinesses);
        if (CollUtil.isNotEmpty(info)) {
            if (StrUtil.isNotBlank(bizBusiness.getFunction())) {
                return result(info);
            }
            info.forEach(a -> {
                String userName = SystemUtil.getUserNameCn();
                int length = userName.length();
                String title = a.getTitle();
                if (StrUtil.isNotBlank(title)) {
                    String newTitle = title.substring(length + 3);
                    a.setTitle(newTitle);
                }
                // 付款申请新显示金额 其他的设置为null
                String procDefKey = a.getProcDefKey();
                if (StrUtil.isBlank(procDefKey) || !procDefKey.startsWith("payment-")) {
                    a.setTotalPrice(null);
                }
            });
        }
        return result(info);
    }

    /**
     * 查询流程业务列表（我的申请）
     */
    @ApiOperation(value = "我的申请", notes = "我的申请")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "sortField", value = "排序列"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向 \"desc\" 或者 \"asc\"")})
    @GetMapping("list/my/relate")
    public R listForRelate(BizBusiness bizBusiness) {
        startPage();
        List<BizBusiness> bizBusinesses = bizBusinessService.selectBizBusinessListForRelate(bizBusiness);
        return result(getInfo(bizBusinesses));
    }

    /**
     * 查询报废申请列表（报废出库）
     */
    @ApiOperation(value = "报废出库", notes = "报废出库")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "sortField", value = "排序列"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向 \"desc\" 或者 \"asc\"")})
    @GetMapping("list/scrapped")
    public R list(ScrappedDto scrappedDto) {
        pageUtil();
        List<BizBusiness> bizBusinesses = bizBusinessService.selectScrappedList(scrappedDto);
        return result(getInfo(bizBusinesses));
    }

    /**
     * task 我的转交
     *
     * @param bizBusiness
     * @return
     * @author zx
     */
    @GetMapping("reassignment")
    public R myReassignment(BizBusiness bizBusiness) {
        try {
            pageUtil();
            return resultData(getInfo(bizBusinessService.selectMyReassignment(bizBusiness)));
        } catch (Exception e) {
            logger.error("查询我的转交", e);
            return R.error("查询我的转交失败");
        }
    }

    @GetMapping("list/my/test")
    public void listTest() {
        List<BizBusiness> bizBusinesses = bizBusinessService.selectBizBusinessList(new BizBusiness());
        getInfo(bizBusinesses);
    }

    private List<BizBusiness> getInfo(List<BizBusiness> businessesList) {
        for (BizBusiness bizBusiness : businessesList) {

            Map<String, Object> mapInfo = mapInfoService.getMapInfo(bizBusiness);
            bizBusiness.setPaymentMap((Map<String, Object>) mapInfo.get("paymentMap"));
            bizBusiness.setReimburseMap((Map<String, Object>) mapInfo.get("reimburseMap"));
            bizBusiness.setDetailMap((List<Map<String, Object>>) mapInfo.get("list"));
//            bizBusiness.setProjectMoney(String.valueOf((Integer) mapInfo.get("totalPrice")));
            bizBusiness.setApplyCode(mapInfo.get("applyCode").toString());
            Object totalPrice = mapInfo.get("totalPrice");
            if (totalPrice == null) {
                bizBusiness.setTotalPrice(BigDecimal.valueOf(0));
            } else {
                bizBusiness.setTotalPrice(new BigDecimal(mapInfo.get("totalPrice").toString()));
            }
        }
        return businessesList;
    }


    /**
     * 新增保存流程业务
     */
    @PostMapping("save")
    public R addSave(@RequestBody BizBusiness bizBusiness) {
        bizBusiness.setUserId(getCurrentUserId());
        return toAjax(bizBusinessService.insertBizBusiness(bizBusiness));
    }

    /**
     * 修改保存流程业务
     */
    @PostMapping("update")
    public R editSave(@RequestBody BizBusiness bizBusiness) {
        return toAjax(bizBusinessService.updateBizBusiness(bizBusiness));
    }

    /**
     * 删除流程业务
     */
    @PostMapping("remove")
    @OperLog(title = "删除流程业务", businessType = BusinessType.DELETE)
    public R remove(String ids) {
        return toAjax(bizBusinessService.deleteBizBusinessLogic(ids));
    }

    /**
     * 查询需求管理
     */

    @GetMapping("need")
    public R getNeed(NeedManageDto needManageDto) {
        startPage();
        List<NeedManageDto> needs = bizBusinessService.getNeed(needManageDto);
        return resultData(needs);
    }

}
