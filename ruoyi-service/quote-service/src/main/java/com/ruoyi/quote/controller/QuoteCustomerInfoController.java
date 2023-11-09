package com.ruoyi.quote.controller;

import java.util.List;

import com.github.pagehelper.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.quote.domain.entity.QuoteCustomerInfo;
import com.ruoyi.quote.service.IQuoteCustomerInfoService;

/**
 * 客户信息Controller
 *
 * @author yrb
 * @date 2022-04-27
 */
@RestController
@RequestMapping("/quote/customerInfo")
public class QuoteCustomerInfoController extends BaseController {
    private final IQuoteCustomerInfoService quoteCustomerInfoService;

    @Autowired
    public QuoteCustomerInfoController(IQuoteCustomerInfoService quoteCustomerInfoService) {
        this.quoteCustomerInfoService = quoteCustomerInfoService;
    }

    /**
     * 查询客户信息列表
     */
    @GetMapping("/list")
    public R list(QuoteCustomerInfo quoteCustomerInfo) {
        try {
            Page page = startPageWithTotal();
            page.setOrderBy("create_time desc");
            List<QuoteCustomerInfo> list = quoteCustomerInfoService.selectQuoteCustomerInfoList(quoteCustomerInfo);
            return R.data(list, page.getTotal());
        } catch (Exception e) {
            logger.error("查询客户信息列表失败,异常信息：" + e);
            return R.error("查询客户信息列表失败！");
        }
    }

    /**
     * 获取客户详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("客户信息id不能为空!");
            }
            return R.data(quoteCustomerInfoService.selectQuoteCustomerInfoById(id));
        } catch (Exception e) {
            logger.error("获取客户详细信息失败,异常信息：" + e);
            return R.error("获取客户详细信息失败！");
        }
    }

    /**
     * 新增客户信息
     */
    @OperLog(title = "客户信息", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody QuoteCustomerInfo quoteCustomerInfo) {
        try {
            if (null == quoteCustomerInfo) {
                return R.error("客户录入信息不能为空!");
            }
            if (null == quoteCustomerInfo.getCompanyName() || "".equals(quoteCustomerInfo.getCompanyName())) {
                return R.error("客户所属公司不能为空!");
            }
            if (quoteCustomerInfoService.insertQuoteCustomerInfo(quoteCustomerInfo) > 0) {
                return R.ok("新增客户信息成功");
            }
            return R.error("新增客户信息失败");
        } catch (Exception e) {
            logger.error("新增客户信息失败,异常信息：" + e);
            return R.error("新增客户信息失败！");
        }
    }

    /**
     * 修改客户信息
     */
    @OperLog(title = "客户信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody QuoteCustomerInfo quoteCustomerInfo) {
        try {
            if (null == quoteCustomerInfo) {
                return R.error("要修改的客户信息不能为空！");
            }
            if (null == quoteCustomerInfo.getId()) {
                return R.error("客户id不能为空！");
            }
            if (quoteCustomerInfoService.updateQuoteCustomerInfo(quoteCustomerInfo) > 0) {
                return R.ok("修改客户信息成功");
            }
            return R.ok("修改客户信息失败！");
        } catch (Exception e) {
            logger.error("修改客户信息失败,异常信息：" + e);
            return R.error("修改客户信息失败！");
        }
    }

    /**
     * 删除客户信息
     */
    @OperLog(title = "客户信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("要删除的客户id不能为空！");
            }
            if (quoteCustomerInfoService.deleteQuoteCustomerInfoByIds(ids) > 0) {
                return R.ok("删除客户信息成功.");
            }
            return R.error("删除客户信息失败！");
        } catch (Exception e) {
            logger.error("删除客户信息失败,异常信息：" + e);
            return R.error("删除客户信息失败！");
        }
    }
}
