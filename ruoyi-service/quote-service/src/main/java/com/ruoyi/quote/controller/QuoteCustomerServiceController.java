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
import com.ruoyi.quote.domain.entity.QuoteCustomerService;
import com.ruoyi.quote.service.IQuoteCustomerServiceService;

/**
 * 客服信息Controller
 *
 * @author yrb
 * @date 2022-04-28
 */
@RestController
@RequestMapping("/quote/customerService")
public class QuoteCustomerServiceController extends BaseController {
    private final IQuoteCustomerServiceService quoteCustomerServiceService;

    @Autowired
    public QuoteCustomerServiceController(IQuoteCustomerServiceService quoteCustomerServiceService) {
        this.quoteCustomerServiceService = quoteCustomerServiceService;
    }

    /**
     * 查询客服信息列表
     */
    @GetMapping("/list")
    public R list(QuoteCustomerService quoteCustomerService) {
        try {
            Page page = startPageWithTotal();
            List<QuoteCustomerService> list = quoteCustomerServiceService.selectQuoteCustomerServiceList(quoteCustomerService);
            return R.data(list, page.getTotal());
        } catch (Exception e) {
            logger.error("查询客服信息列表失败,异常信息：" + e);
            return R.error("查询客服信息列表失败！");
        }
    }

    /**
     * 获取客服信息详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            return R.data(quoteCustomerServiceService.selectQuoteCustomerServiceById(id));
        } catch (Exception e) {
            logger.error("获取客服信息详细信息失败,异常信息：" + e);
            return R.error("获取客服信息详细信息失败！");
        }
    }

    /**
     * 新增客服信息
     */
    @OperLog(title = "客服信息", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody QuoteCustomerService quoteCustomerService) {
        try {
            if (null == quoteCustomerService) {
                return R.error("新增客服信息不能为空！");
            }
            if (quoteCustomerServiceService.insertQuoteCustomerService(quoteCustomerService) > 0) {
                return R.ok("新增客服信息成功.");
            }
            return R.error("新增客服信息失败！");
        } catch (Exception e) {
            logger.error("新增客服信息失败,异常信息：" + e);
            return R.error("新增客服信息失败," + e.getMessage());
        }
    }

    /**
     * 修改客服信息
     */
    @OperLog(title = "客服信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody QuoteCustomerService quoteCustomerService) {
        try {
            if (null == quoteCustomerService) {
                return R.error("请输入要编辑的客服信息！");
            }
            if (null == quoteCustomerService.getId()) {
                return R.error("客服id不能为空！！");
            }
            if (quoteCustomerServiceService.updateQuoteCustomerService(quoteCustomerService) > 0) {
                return R.ok("修改客服信息成功.");
            }
            return R.error("修改客服信息失败！");
        } catch (Exception e) {
            logger.error("修改客服信息失败,异常信息：" + e);
            return R.error("修改客服信息失败！");
        }
    }

    /**
     * 删除客服信息
     */
    @OperLog(title = "客服信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("要删除的客户id不能为空！");
            }
            if (quoteCustomerServiceService.deleteQuoteCustomerServiceByIds(ids) > 0) {
                return R.ok("客服信息删除成功.");
            }
            return R.error("删除客服信息失败");
        } catch (Exception e) {
            logger.error("删除客服信息失败,异常信息：" + e);
            return R.error("删除客服信息失败！");
        }
    }

    /**
     * 获取客服代表
     *
     * @return 当前用户客服代表
     */
    @GetMapping("/getCustomerServiceInfo")
    public R getCustomerServiceInfo() {
        try {
            return R.data(quoteCustomerServiceService.findQuoteCustomerServiceByCompanyName());
        } catch (Exception e) {
            logger.error("获取客服代表信息失败,异常信息：" + e);
            return R.error("获取客服代表信息失败！");
        }
    }
}
