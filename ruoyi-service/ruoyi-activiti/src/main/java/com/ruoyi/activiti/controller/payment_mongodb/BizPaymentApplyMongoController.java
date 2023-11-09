package com.ruoyi.activiti.controller.payment_mongodb;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.vo.BizBusinessVO;
import com.ruoyi.activiti.domain.vo.DirectoryDTO;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.payment_mongodb.BizPaymentApplyService;
import com.ruoyi.activiti.test.BizBusinessTest;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 付款申请重构
 *
 * @author wuYang
 * @date 2022/10/17 16:25
 */
@Api(tags = {"付款申请重构"})
@RestController
public class BizPaymentApplyMongoController extends BaseController {
    @Resource
    BizPaymentApplyService bizPaymentApplyService;
    @Resource
    RemoteDeptService remoteDeptService;
    @Resource
    RemoteUserService remoteUserService;
    @Resource
    IBizBusinessService businessService;

    /**
     * 查询模板列表
     *
     * @return
     */
    @GetMapping("/templatelist")
    @ApiOperation("查询模板")
    public R getAllList(@RequestParam(required = false) String id, Integer isLive) {
        try {
            if (StringUtils.isBlank(id)) {
                return R.error("查询模板列表失败，请选择对应模板目录");
            }
            return R.data(bizPaymentApplyService.select(id, isLive));
        } catch (Exception e) {
            logger.error("查询模板列表失败{}", e.toString());
            return R.error("查询模板列表失败，请联系管理员");
        }
    }

    /**
     * 导出
     *
     * @return
     */
    @GetMapping("/exportPaymentApplyData")
    public void exportPaymentApplyData(HttpServletResponse response) {
        try {
            bizPaymentApplyService.exportPaymentApplyData(response);
        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    /**
     * 判断老模板
     *
     * @return
     */
    @GetMapping("/check/template/old")
    @ApiOperation("判断老模板")
    public R checkTemplateOld(String id) {
        try {
            return R.data(bizPaymentApplyService.checkTemplateIsOld(id));
        } catch (Exception e) {
            logger.error("判断老模板失败", e);
            return R.error("判断老模板失败，请联系管理员");
        }
    }

    /**
     * 二级目录
     *
     * @return
     */
    @GetMapping("/secondDir")
    @ApiOperation("二级目录")
    public R secondDir() {
        try {
            return R.data(bizPaymentApplyService.secondDirector());
        } catch (Exception e) {
            logger.error("判断老模板失败", e);
            return R.error("判断老模板失败，请联系管理员");
        }
    }

    /**
     * 新增模板
     *
     * @param param
     * @return
     */
    @PostMapping("/template")
    @ApiOperation("新增")
    @OperLog(title = "新增模板", businessType = BusinessType.INSERT)
    public R add(@RequestBody HashMap<String, Object> param) {
        try {
            Integer insert = bizPaymentApplyService.insert(param);
            if (insert == 0) {
                return R.error("模板name重复 去编辑已存在模板");
            }
            return R.ok("新增完成");
        } catch (Exception e) {
            logger.error("新增模板失败，请联系管理员{}", e.toString());
            return R.error("新增模板失败，请联系管理员");
        }
    }

    /**
     * 更新维护目录
     *
     * @param param
     * @return
     */
    @PutMapping("/template")
    @ApiOperation("更新")
    @OperLog(title = "更新维护模板", businessType = BusinessType.UPDATE)
    public R update(@RequestBody HashMap<String, Object> param) {
        try {
            int i = bizPaymentApplyService.update(param);
            if (i == 0) {
                return R.error(250, "版本正在使用，不能修改");
            }
            return R.ok("更新完成");
        } catch (Exception e) {
            logger.error("更新完成失败，请联系管理员{}", e.toString());
            return R.error("更新完成失败，请联系管理员");
        }
    }

    /**
     * 删除模板
     *
     * @param id
     * @retur
     */
    @GetMapping("/template_delete")
    @ApiOperation("删除")
    @OperLog(title = "删除模板", businessType = BusinessType.DELETE)
    public R delete(String id) {
        try {
            return R.data(bizPaymentApplyService.delete(id) ? "已完成删除" : "模板使用中无法删除");
        } catch (Exception e) {
            logger.error("删除模板失败，请联系管理员{}", e.toString());
            return R.error("删除模板失败,请联系管理员");
        }
    }

    /**
     * 获取模板
     *
     * @param id
     * @return
     */
    @GetMapping("/template")
    @ApiOperation("获取模板")
    public R getOneTemplate(@RequestParam("id") String id) {
        try {
            return R.data(bizPaymentApplyService.getTemplate(id));
        } catch (Exception e) {
            logger.error("获取模板失败，请联系管理员{}", e.toString());
            return R.error("获取模板失败，请联系管理员");
        }
    }

    /**
     * 变成草稿
     *
     * @param param
     * @return
     */
    @PostMapping("/draft")
    @ApiOperation("变成草稿")
    @OperLog(title = "变成草稿", businessType = BusinessType.INSERT)
    public R toDraft(@RequestBody HashMap<String, Object> param) {
        bizPaymentApplyService.toDraft(param);
        return R.ok();
    }

    /**
     * 四级精确到明细
     *
     * @param
     * @return
     */
    @GetMapping("/detail")
    @ApiOperation("四级明细")
    public R detail(@RequestParam("flag") Boolean flag, @RequestParam("id") String id) {
        try {
            List<HashMap> hashMaps = bizPaymentApplyService.paymentInfo(flag, id);
            return R.data(hashMaps);
        } catch (Exception e) {
            logger.error("明细失败", e);
            return R.error(e.getMessage());
        }
    }

    /**
     * 开启流程（付款申请）
     *
     * @param param
     * @return
     */
    @PostMapping("/start")
    @ApiOperation("开启流程")
    @OperLog(title = "付款申请", businessType = BusinessType.INSERT)
    public R startProcess(@RequestBody HashMap<String, Object> param) {
        try {
            SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUser.getDeptId());
            String companyName = belongCompany2.get("companyName").toString();

            int insert = bizPaymentApplyService.startPaymentProcess(param);
            if (insert == 0) {
                return R.error("提交申请失败");
            }
            if (insert == 2) {
                return R.error(companyName + "缺少经营参数");
            }
            if (insert == 3) {
                return R.error("付款明细不能为空");
            }
            if (insert == -2) {
                return R.error("发票重复");
            }
            if (insert == 4) {
                return R.ok("已存入草稿");
            }
            if (insert == -666) {
                return R.error(503, "预算余额不足，无法发起");
            }
            if (insert == -111) {
                return R.error("提交失败，请联系管理员");
            }
//            if (insert == -999) {
//                return R.error("你的角色无法发起申请");
//            }
            return R.ok("提交申请成功");
        } catch (Exception e) {
            logger.error("提交申请失败", e);
            return R.error("提交申请失败");
        }
    }

    /**
     * 财务申请（新）判断是否有权限申请
     */
    @GetMapping("/allowApply")
    @ApiOperation("判断是否有权限申请")
    public R allowApply(@RequestParam("id") String id) {
        try {
            if (StrUtil.isBlank(id)){
                return R.error("id不能为空！");
            }
            if (bizPaymentApplyService.permitApply(id)){
                return R.ok();
            }
            return R.error("你的角色无法发起申请");
        } catch (Exception e) {
            logger.error("财务申请（新）判断用户是否有申请权限发生异常---", e);
            return R.error("判断用户申请权限发生异常！");
        }
    }

    /**
     * 获取付款维护目录
     *
     * @param type
     * @return
     */
    @GetMapping("/directoryList")
    @ApiOperation("获取目录")
    public R getDirectorys(String type) {
        try {
            return R.data(bizPaymentApplyService.getDirectory(type));
        } catch (Exception e) {
            logger.error("获取目录失败，请联系管理员{}", e.toString());
            return R.error("获取目录失败，请联系管理员");
        }
    }

    /**
     * 获取目录
     *
     * @param type
     * @return
     */
    @GetMapping("/directoryListAll")
    @ApiOperation("获取目录 ")
    public R getDirectoryAlls(String type) {
        try {
            return R.data(bizPaymentApplyService.getDirectoryAll(type));
        } catch (Exception e) {
            logger.error("获取目录失败，请联系管理员{}", e.toString());
            return R.error("获取目录失败，请联系管理员");
        }
    }

    /**
     * 新增目录
     *
     * @param directoryDTO
     * @return
     */
    @PostMapping("/directory")
    @ApiOperation("新增目录")
    @OperLog(title = "新增维护目录", businessType = BusinessType.INSERT)
    public R addDirectory(@RequestBody DirectoryDTO directoryDTO) {
        try {
            bizPaymentApplyService.addDirectory(directoryDTO);
            return R.ok("添加成功");
        } catch (Exception e) {
            logger.error("新增维护目录失败，请联系管理员:{}", e.toString());
            return R.error("新增维护目录失败，请联系管理员");
        }
    }

    /**
     * 通过id删除目录
     *
     * @param id
     * @return
     */
    @GetMapping("/directory_delete")
    @ApiOperation("删除目录")
    @OperLog(title = "删除维护目录", businessType = BusinessType.DELETE)
    public R deleteDirectory(@RequestParam("id") String id) {
        try {
            int i = bizPaymentApplyService.removeDirectory(id);
            if (i == 0) {
                return R.error(500, "有子集目录模板存在，无法删除");
            }
            return R.ok("删除成功");
        } catch (Exception e) {
            logger.error("删除目录失败，请联系管理员{}", e.toString());
            return R.error("删除目录失败，请联系管理员");
        }
    }

    /**
     * 更新目录
     *
     * @param directoryDTO
     * @return
     */
    @PutMapping("/directory")
    @ApiOperation("更新目录")
    @OperLog(title = "更新维护目录", businessType = BusinessType.UPDATE)
    public R UpdateDirectory(@RequestBody DirectoryDTO directoryDTO) {
        try {
            int i = bizPaymentApplyService.updateDirectory(directoryDTO);
            if (i == 0) {
                return R.ok("先转移附属类别");
            } else {
                return R.ok("更新成功");
            }

        } catch (Exception e) {
            logger.error("更新目录失败，请联系管理员{}", e.toString());
            return R.error("更新目录失败，请联系管理员");
        }
    }

    /**
     * 查询获取单个目录(通过id)
     *
     * @param id
     * @return
     */
    @GetMapping("/directory")
    @ApiOperation("获取单个目录")
    public R getDirectory(String id) {
        try {
            HashMap hashMap = bizPaymentApplyService.selectOneDirectory(id);
            return R.data(hashMap);
        } catch (Exception e) {
            logger.error("获取单个目录失败，请联系管理员{}", e.toString());
            return R.error("获取单个目录失败，请联系管理员");
        }
    }

    /**
     * 获取所有配置列表（基类）
     *
     * @return
     */
    @GetMapping("/confiig")
    @ApiOperation("获取所有配置列表")
    public R getAllConfiguration() {
        try {
            HashMap hashMap = bizPaymentApplyService.getConfig();
            return R.data(hashMap);
        } catch (Exception e) {
            logger.error("获取单个目录失败，请联系管理员{}", e.toString());
            return R.error("获取单个目录失败，请联系管理员");
        }
    }

    /**
     * 激活或者禁用模板
     *
     * @return
     */
    @GetMapping("/active")
    @ApiOperation("激活或者禁用")
    public R active(String id) {
        try {
            int activation = bizPaymentApplyService.activation(id);
            if (activation == 0) {
                return R.ok("激活成功");
            }
            return R.ok("禁用成功");
        } catch (Exception e) {
            logger.error("激活失败，请联系管理员{}", e.toString());
            return R.error("激活失败，请联系管理员");
        }
    }

    /**
     * 获取模板通过目录id
     *
     * @return
     */
    @GetMapping("/getTemplateById")
    @ApiOperation("获取模板通过目录id")
    public R getTemplateById(String id) {
        try {
            return R.data(bizPaymentApplyService.getTemplateById(id));
        } catch (Exception e) {
            logger.error("获取模板通过目录id失败，请联系管理员{}", e.toString());
            return R.error("获取模板通过目录id失败，请联系管理员");
        }
    }

    /**
     * 获取付款申请列表
     *
     * @return
     */
    @GetMapping("/getPaymentApplyList")
    @ApiOperation("获取流程详情")
    public R getPaymentApplyList(BizBusinessVO bizBusiness) {
        try {
            startPage();
            if (StringUtils.isNotBlank(bizBusiness.getStart())) {
                bizBusiness.setStartTime(DateUtil.parseDate(bizBusiness.getStart()).toLocalDateTime().toLocalDate());
            }
            if (StringUtils.isNotBlank(bizBusiness.getEnd())) {
                bizBusiness.setEndTime(DateUtil.parseDate(bizBusiness.getEnd()).toLocalDateTime().toLocalDate());
            }
            List<BizBusiness> paymentApplyList = bizPaymentApplyService.getPaymentApplyList(bizBusiness);
            return result(paymentApplyList);
        } catch (Exception e) {
            logger.error("获取流程详情失败，请联系管理员{}", e.toString());
            return R.error("获取流程详情失败，请联系管理员");
        }
    }

    /**
     * 获取详情
     *
     * @param businessKey
     * @return
     */
    @GetMapping("biz/{businessKey}")
    @ApiOperation("获取详情")
    public R biz2(@PathVariable("businessKey") String businessKey) {
        try {
            BizBusiness business = businessService.selectBizBusinessById(businessKey);
            if (null == business) {
                return R.error("获取流程信息失败");
            }
            return R.data(bizPaymentApplyService.getApplyDetail(business.getTableId(), business));
        } catch (Exception e) {
            logger.error("获取详情失败{}", e.toString());
            return R.error("获取详情失败");
        }
    }

    /**
     * 判断发票是否重复
     *
     * @param
     * @return
     */
    @GetMapping("checkDup")
    @ApiOperation("判断发票是否重复")
    public R check(String bullCode, String billNumber) {
        try {
            return R.data(bizPaymentApplyService.ifHasDuplicateBill(bullCode, billNumber));
        } catch (Exception e) {
            logger.error("获取详情失败{}", e.toString());
            return R.error("获取详情失败");
        }
    }

    /**
     * 导出
     *
     * @param response
     * @param
     * @throws IOException
     */
    @ApiOperation("导出数据")
    @GetMapping(value = "/payment/download")
    public void download(HttpServletResponse response, BizBusinessVO bizBusinessVO) throws IOException {
        //todo 是否要父级类别
        List<BizBusiness> paymentApplyList = bizPaymentApplyService.getPaymentApplyList(bizBusinessVO);
        bizPaymentApplyService.download(paymentApplyList, response);
    }

    @Resource
    BizBusinessTest bizBusinessTest;

    @GetMapping("/gene")
    @ApiOperation("生成")
    public void gene() {
        bizBusinessTest.gene();
//        bizPaymentApplyService.insertPaymentNumber();
    }

    @ApiOperation("获取所有")
    @GetMapping("/getall")
    public R getAll(@RequestParam(required = false) String applyCode,
                    @RequestParam(required = false) String type,
                    @RequestParam(required = false) String applyer,
                    @RequestParam(required = false) Integer companyId,
                    @RequestParam(required = false) Integer deptId,
                    @RequestParam(required = false) Boolean hasPurchase) {
        startPage();
        List<BizBusiness> all = bizPaymentApplyService.getAll(applyCode, type, applyer, companyId, deptId, hasPurchase);
        if (all == null) {
            all = new ArrayList<>();
        }
        return result(all);
    }
}
