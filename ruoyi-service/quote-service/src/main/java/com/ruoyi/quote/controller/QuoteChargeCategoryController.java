package com.ruoyi.quote.controller;

import java.util.List;

import com.github.pagehelper.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.quote.domain.entity.QuoteChargeCategory;
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
import com.ruoyi.quote.service.IQuoteChargeCategoryService;

/**
 * 收费标准详细分类Controller
 *
 * @author yrb
 * @date 2022-05-17
 */
@RestController
@RequestMapping("/quote/charge")
public class QuoteChargeCategoryController extends BaseController {
    private final IQuoteChargeCategoryService quoteChargeCategoryService;

    @Autowired
    public QuoteChargeCategoryController(IQuoteChargeCategoryService quoteChargeCategoryService) {
        this.quoteChargeCategoryService = quoteChargeCategoryService;
    }

    /**
     * 查询收费标准详细分类列表
     */
    @GetMapping("/list")
    public R list(QuoteChargeCategory quoteChargeCategory) {
        try {
            Page page = startPageWithTotal();
            List<QuoteChargeCategory> list = quoteChargeCategoryService.selectQuoteChargeCategoryList(quoteChargeCategory);
            return R.data(list, page.getTotal());
        } catch (Exception e) {
            logger.error("查询收费标准详细分类列表失败,异常信息：" + e);
            return R.error("查询收费标准详细分类列表失败！");
        }
    }

    /**
     * 获取收费标准详细分类详细信息
     */
    @GetMapping(value = "/{categoryId}")
    public R getInfo(@PathVariable("categoryId") Long categoryId) {
        try {
            if (null == categoryId) {
                return R.error("分类详情id不能为空！");
            }
            return R.data(quoteChargeCategoryService.selectQuoteChargeCategoryByCategoryId(categoryId));
        } catch (Exception e) {
            logger.error("获取收费标准详细分类详细信息失败,异常信息：" + e);
            return R.error("获取收费标准详细分类详细信息失败！");
        }
    }

    /**
     * 新增收费标准详细分类
     */
    @OperLog(title = "收费标准详细分类", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody QuoteChargeCategory quoteChargeCategory) {
        try {
            if (null == quoteChargeCategory) {
                return R.error("新增内容不能为空！");
            }
            if (quoteChargeCategoryService.insertQuoteChargeCategory(quoteChargeCategory) > 0) {
                return R.ok("新增收费标准详细分类成功.");
            }
            return R.error("新增收费标准详细分类失败！");
        } catch (Exception e) {
            logger.error("新增收费标准详细分类失败,异常信息：" + e);
            return R.error("新增收费标准详细分类失败！");
        }
    }

    /**
     * 修改收费标准详细分类
     */
    @OperLog(title = "收费标准详细分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody QuoteChargeCategory quoteChargeCategory) {
        try {
            if (null == quoteChargeCategory) {
                return R.error("修改内容不能为空！");
            }
            if (null == quoteChargeCategory.getCategoryId()) {
                return R.error("分类id不能为空！");
            }
            if (quoteChargeCategoryService.updateQuoteChargeCategory(quoteChargeCategory) > 0) {
                return R.ok("修改收费标准详细分类成功.");
            }
            return R.error("修改收费标准详细分类失败！");
        } catch (Exception e) {
            logger.error("修改收费标准详细分类失败,异常信息：" + e);
            return R.error("修改收费标准详细分类失败！");
        }
    }

    /**
     * 删除收费标准详细分类
     */
    @OperLog(title = "收费标准详细分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryIds}")
    public R remove(@PathVariable Long[] categoryIds) {
        try {
            if (null == categoryIds || categoryIds.length < 1) {
                return R.error("分类id不能为空！");
            }
            if (quoteChargeCategoryService.deleteQuoteChargeCategoryByCategoryIds(categoryIds) > 0) {
                return R.ok("删除收费标准详细分类成功.");
            }
            return R.error("删除收费标准详细分类失败！");
        } catch (Exception e) {
            logger.error("删除收费标准详细分类失败,异常信息：" + e);
            return R.error("删除收费标准详细分类失败！");
        }
    }

    /**
     * 查询收费标准详细分类列表tree
     */
    @GetMapping("/getCategoryTree")
    public R getCategoryTree(QuoteChargeCategory quoteChargeCategory) {
        try {
            return R.data(quoteChargeCategoryService.quoteChargeCategoryTree(quoteChargeCategory));
        } catch (Exception e) {
            logger.error("查询收费标准树失败,异常信息：" + e);
            return R.error("查询收费标准树失败！");
        }
    }
}
