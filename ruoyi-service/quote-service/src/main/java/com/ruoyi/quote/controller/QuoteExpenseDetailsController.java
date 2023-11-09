package com.ruoyi.quote.controller;


import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.quote.domain.dto.QuoteExpenseDetailsDTO;
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
import com.ruoyi.quote.domain.entity.QuoteExpenseDetails;
import com.ruoyi.quote.service.IQuoteExpenseDetailsService;

import java.util.List;

/**
 * 检测费用明细Controller
 *
 * @author yrb
 * @date 2022-04-29
 */
@RestController
@RequestMapping("/quote/details")
public class QuoteExpenseDetailsController extends BaseController {
    private final IQuoteExpenseDetailsService quoteExpenseDetailsService;

    @Autowired
    public QuoteExpenseDetailsController(IQuoteExpenseDetailsService quoteExpenseDetailsService) {
        this.quoteExpenseDetailsService = quoteExpenseDetailsService;
    }

    /**
     * 查询检测费用明细列表
     */
    @GetMapping("/list")
    public R list(QuoteExpenseDetails quoteExpenseDetails) {
        try {
            Page page = startPageWithTotal();
            List<QuoteExpenseDetails> list = quoteExpenseDetailsService.selectQuoteExpenseDetailsList(quoteExpenseDetails);
            return R.data(list, page.getTotal());
        } catch (Exception e) {
            logger.error("查询检测费用明细列表失败,异常信息：" + e);
            return R.error("查询检测费用明细列表失败！");
        }
    }

    /**
     * 获取检测费用明细详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            return R.data(quoteExpenseDetailsService.selectQuoteExpenseDetailsById(id));
        } catch (Exception e) {
            logger.error("获取检测费用明细详细信息失败,异常信息：" + e);
            return R.error("获取检测费用明细详细信息失败！");
        }
    }

    /**
     * 新增检测费用明细
     */
    @OperLog(title = "检测费用明细", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody QuoteExpenseDetails quoteExpenseDetails) {
        try {
            if (StrUtil.isBlank(quoteExpenseDetails.getSheetId())) {
                return R.error("报价单id不能为空!");
            }
            if (null == quoteExpenseDetails.getSubId()) {
                return R.error("子类id不能为空！");
            }
            if (quoteExpenseDetailsService.insertQuoteExpenseDetails(quoteExpenseDetails) > 0) {
                return R.ok("新增检测费用明细成功.");
            }
            return R.error("新增检测费用明细失败！");
        } catch (Exception e) {
            logger.error("新增检测费用明细失败,异常信息：" + e);
            return R.error("新增检测费用明细失败！");
        }
    }

    /**
     * 新增检测费用明细(批量)
     */
    @OperLog(title = "批量新增检测费用明细", businessType = BusinessType.INSERT)
    @PostMapping("/addBatch")
    public R addBatch(@RequestBody List<QuoteExpenseDetails> quoteExpenseDetails) {
        try {
            if (null == quoteExpenseDetails || quoteExpenseDetails.size() < 1) {
                return R.error("新增信息不能为空！");
            }
            for (QuoteExpenseDetails quoteExpenseDetail : quoteExpenseDetails) {
                if (StrUtil.isBlank(quoteExpenseDetail.getCompanyName())) {
                    return R.error("公司名称不能为空");
                }
                if (StrUtil.isBlank(quoteExpenseDetail.getSubName())) {
                    return R.error("子类报价全称不能为空");
                }
                if (StrUtil.isBlank(quoteExpenseDetail.getSubAbb())) {
                    return R.error("子类报价简称不能为空");
                }
                if (StrUtil.isBlank(quoteExpenseDetail.getSheetId())) {
                    return R.error("报价单id不能为空");
                }
                if (null == quoteExpenseDetail.getSubId()) {
                    return R.error("子类id不能为空！");
                }
                if (quoteExpenseDetailsService.insertQuoteExpenseDetails(quoteExpenseDetail) < 1) {
                    return R.error("新增检测费用明细失败！");
                }
            }
            return R.ok("新增检测费用明细成功");
        } catch (Exception e) {
            logger.error("新增检测费用明细失败,异常信息：" + e);
            return R.error("新增检测费用明细失败！" + e.getMessage());
        }
    }

    /**
     * 修改检测费用明细
     */
    @OperLog(title = "检测费用明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody QuoteExpenseDetails quoteExpenseDetails) {
        try {
            if (null == quoteExpenseDetails) {
                return R.error("修改信息不能为空！");
            }
            if (null == quoteExpenseDetails.getId()) {
                return R.error("明细id不能为空！");
            }
            if (quoteExpenseDetailsService.updateQuoteExpenseDetails(quoteExpenseDetails) > 0) {
                return R.ok("修改检测费用明细成功.");
            }
            return R.error("修改检测费用明细失败！");
        } catch (Exception e) {
            logger.error("修改检测费用明细失败,异常信息：" + e);
            return R.error("修改检测费用明细失败！");
        }
    }

    /**
     * 删除检测费用明细
     */
    @OperLog(title = "检测费用明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("要删除的费用明细id不能为空！");
            }
            if (quoteExpenseDetailsService.deleteQuoteExpenseDetailsByIds(ids) > 0) {
                return R.ok("检测费用明细信息删除成功.");
            }
            return R.error("检测费用明细信息删除失败！");
        } catch (Exception e) {
            logger.error("检测费用明细信息删除失败,异常信息：" + e);
            return R.error("检测费用明细信息删除失败！");
        }
    }

    /**
     * 获取子类检测费用明细（职卫、环境、公卫）
     */
    @OperLog(title = "获取子类检测费用明细", businessType = BusinessType.OTHER)
    @PostMapping("/getSubQuoteExpenseDetails")
    public R getSubQuoteExpenseDetails(@RequestBody QuoteExpenseDetailsDTO quoteExpenseDetailsDTO) {
        try {
            if (StrUtil.isBlank(quoteExpenseDetailsDTO.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (StrUtil.isBlank(quoteExpenseDetailsDTO.getCompanyName())) {
                return R.error("公司名称不能为空");
            }
            List<Long> ids = quoteExpenseDetailsDTO.getList();
            if (ids == null || ids.size() == 0) {
                return R.error("请选择报价子类");
            }
            List<QuoteExpenseDetails> list = quoteExpenseDetailsService.findSubQuoteExpenseDetails(quoteExpenseDetailsDTO);
            return R.data(list);
        } catch (Exception e) {
            logger.error("新增检测费用明细失败,异常信息：" + e);
            return R.error("新增检测费用明细失败！" + e.getMessage());
        }
    }

    /**
     * 删除子类检测费用明细（职卫、环境、公卫）
     */
    @OperLog(title = "删除子类检测费用明细", businessType = BusinessType.DELETE)
    @PostMapping("/deleteQuoteExpenseDetailsBySubId")
    public R deleteQuoteExpenseDetailsBySubId(@RequestBody QuoteExpenseDetails quoteExpenseDetails) {
        try {
            if (StrUtil.isBlank(quoteExpenseDetails.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteExpenseDetails.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (quoteExpenseDetailsService.deleteQuoteExpenseDetailsBySubId(quoteExpenseDetails)) {
                return R.ok("子类检测费用信息删除成功");
            }
            return R.error("子类检测费用信息删除失败");
        } catch (Exception e) {
            logger.error("删除子类检测费用明细失败,异常信息：" + e);
            return R.error("删除子类检测费用明细失败！" + e.getMessage());
        }
    }

    /**
     * 获取单个子类检测费用明细 app端
     */
    @OperLog(title = "获取单个子类检测费用明细", businessType = BusinessType.OTHER)
    @PostMapping("/getSubExpenseDetails")
    public R getSubExpenseDetails(@RequestBody QuoteExpenseDetails quoteExpenseDetails) {
        try {
            if (StrUtil.isBlank(quoteExpenseDetails.getSheetId())) {
                return R.error("报价单id不能为空");
            }
            if (quoteExpenseDetails.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            return R.data(quoteExpenseDetailsService.findSubExpenseDetails(quoteExpenseDetails));
        } catch (Exception e) {
            logger.error("获取子类检测费用明细失败,异常信息：" + e);
            return R.error("发生异常，获取子类检测费用明细失败");
        }
    }
}
