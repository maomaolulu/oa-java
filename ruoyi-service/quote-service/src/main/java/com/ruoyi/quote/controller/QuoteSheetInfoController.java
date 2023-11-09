package com.ruoyi.quote.controller;

import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.quote.domain.dto.QuoteSheetInfoDTO;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.quote.service.*;
import com.ruoyi.system.feign.RemoteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.quote.domain.entity.QuoteSheetInfo;

/**
 * 报价单信息Controller
 *
 * @author yrb
 * @date 2022-04-29
 */
@RestController
@RequestMapping("/quote/info")
@RefreshScope
public class QuoteSheetInfoController extends BaseController {
    private final IQuoteSheetInfoService quoteSheetInfoService;
    private final IQuoteSheetItemsService quoteSheetItemsService;
    private final RemoteUserService remoteUserService;

    @Value("${python.url.request}")
    private String requestUrl;

    @Autowired
    public QuoteSheetInfoController(IQuoteSheetInfoService quoteSheetInfoService,
                                    IQuoteSheetItemsService quoteSheetItemsService,
                                    RemoteUserService remoteUserService) {
        this.quoteSheetInfoService = quoteSheetInfoService;
        this.quoteSheetItemsService = quoteSheetItemsService;
        this.remoteUserService = remoteUserService;
    }

    /**
     * 查询报价单信息列表
     */
    @GetMapping("/list")
    public R list(QuoteSheetInfo quoteSheetInfo) {
        try {
            Page page = startPageWithTotal();
            page.setOrderBy("create_time desc");
            List<QuoteSheetInfo> list = quoteSheetInfoService.selectQuoteSheetInfoList(quoteSheetInfo);
            return R.data(list, page.getTotal());
        } catch (Exception e) {
            logger.error("查询报价单信息列表失败,异常信息：" + e);
            return R.error("查询报价单信息列表失败！");
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/getUserInfo")
    public R getUserInfo() {
        try {
            return R.data(remoteUserService.selectSysUserByUserId(SystemUtil.getUserId()));
        } catch (Exception e) {
            logger.error("获取当前用户信息失败,异常信息：" + e);
            return R.error("获取当前用户信息失败！");
        }
    }

    /**
     * 获取报价单信息详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") String id) {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            return R.data(quoteSheetInfoService.selectQuoteSheetInfoById(id));
        } catch (Exception e) {
            logger.error("获取报价单信息详细信息失败,异常信息：" + e);
            return R.error("获取报价单信息详细信息失败！");
        }
    }

    /**
     * 新增报价单信息
     */
    @OperLog(title = "报价单信息", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody QuoteSheetInfo quoteSheetInfo) {
        try {
            if (StrUtil.isBlank(quoteSheetInfo.getId())) {
                return R.error("报价单id不能为空");
            }
            if (quoteSheetInfo.getMasterCategoryId() == null) {
                return R.error("主类id不能为空");
            }
            if (quoteSheetInfoService.insertQuoteSheetInfo(quoteSheetInfo) > 0) {
                return R.ok("新增报价单信息成功.");
            }
            return R.error("新增报价单信息失败！");
        } catch (Exception e) {
            logger.error("新增报价单信息失败,异常信息：" + e);
            return R.error("新增报价单信息失败！");
        }
    }

    /**
     * 修改报价单信息
     */
    @OperLog(title = "报价单信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody QuoteSheetInfo quoteSheetInfo) {
        try {
            if (null == quoteSheetInfo) {
                return R.error("修改内容不能为空！");
            }
            if (null == quoteSheetInfo.getId()) {
                return R.error("id不能为空！");
            }
            if (quoteSheetInfoService.updateQuoteSheetInfo(quoteSheetInfo) > 0) {
                return R.ok("修改报价单信息成功.");
            }
            return R.error("修改报价单信息失败！");
        } catch (Exception e) {
            logger.error("修改报价单信息失败,异常信息：" + e);
            return R.error("修改报价单信息失败！");
        }
    }

    /**
     * 删除报价单信息
     */
    @OperLog(title = "报价单信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable String[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("id不能为空！");
            }
            if (quoteSheetInfoService.deleteQuoteSheetInfoByIds(ids) > 0) {
                return R.ok("删除报价单信息成功.");
            }
            return R.error("删除报价单信息失败！");
        } catch (Exception e) {
            logger.error("删除报价单信息失败,异常信息：" + e);
            return R.error("删除报价单信息失败！");
        }
    }

    /**
     * 导出合同
     */
    @OperLog(title = "导出合同", businessType = BusinessType.EXPORT)
    @PostMapping("/exportContract")
    public R exportContract(@RequestBody QuoteSheetInfoDTO quoteSheetInfoDTO) {
        try {
            if (null == quoteSheetInfoDTO.getCustomerId()) {
                return R.error("客户id为空，报价信息不完善！");
            }
            if (StrUtil.isBlank(quoteSheetInfoDTO.getCompanyName())) {
                return R.error("公司名称为空，报价信息不完善！");
            }
            if (StrUtil.isBlank(quoteSheetInfoDTO.getProjectName())) {
                return R.error("项目名称不能为空");
            }
            if (quoteSheetInfoDTO.getDiscountPrice() == null || quoteSheetInfoDTO.getDiscountPrice().doubleValue() == 0.0) {
                return R.error("优惠后价格（含税）不能为空");
            }
            if (quoteSheetInfoDTO.getType() == null) {
                return R.error("导出类型不能为空");
            }
            quoteSheetInfoDTO.setRequestUrl(requestUrl);
            return R.data(quoteSheetItemsService.getContractDownloadUrl(quoteSheetInfoDTO));
        } catch (Exception e) {
            logger.error("导出合同失败,异常信息：" + e);
            return R.error("导出合同失败," + e.getMessage());
        }
    }

    /**
     * 导出报价单
     */
    @OperLog(title = "导出报价单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public R export(@RequestBody QuoteSheetInfoDTO quoteSheetInfoDTO) {
        try {
            if (StrUtil.isBlank(requestUrl)) {
                return R.error("获取python请求地址失败");
            }
            if (quoteSheetInfoDTO.getType() == null) {
                return R.error("下载类型不能为空");
            }
            quoteSheetInfoDTO.setRequestUrl(requestUrl);
            return R.data(quoteSheetItemsService.getSheetInfoDownloadUrl(quoteSheetInfoDTO));
        } catch (Exception e) {
            logger.error("获取下载地址失败,异常信息：" + e);
            return R.error("获取下载地址失败," + e.getMessage());
        }
    }
}
