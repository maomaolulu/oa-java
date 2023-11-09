package com.ruoyi.quote.controller;

import java.util.List;

import com.github.pagehelper.Page;
import com.ruoyi.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.quote.domain.entity.QuoteSubCategory;
import com.ruoyi.quote.service.IQuoteSubCategoryService;

/**
 * 报价分类子类Controller
 *
 * @author yrb
 * @date 2022-04-26
 */
@RestController
@RequestMapping("/quote/subCategory")
public class QuoteSubCategoryController extends BaseController {
    private final IQuoteSubCategoryService quoteSubCategoryService;

    @Autowired
    public QuoteSubCategoryController(IQuoteSubCategoryService quoteSubCategoryService) {
        this.quoteSubCategoryService = quoteSubCategoryService;
    }

    /**
     * 根据父类id--查询报价分类子类列表
     */
    @GetMapping("/list")
    public R list(QuoteSubCategory quoteSubCategory) {
        try {
            Page page = startPageWithTotal();
            List<QuoteSubCategory> list = quoteSubCategoryService.selectQuoteSubCategoryList(quoteSubCategory);
            return R.data(list, page.getTotal());
        } catch (Exception e) {
            logger.error("查询报价分类子类列表失败,异常信息：" + e);
            return R.error("查询报价分类子类列表失败！" + e.getMessage());
        }
    }

    /**
     * 获取子类详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            return R.data(quoteSubCategoryService.selectQuoteSubCategoryById(id));
        } catch (Exception e) {
            logger.error("获取行业信息详细信息失败,异常信息：" + e);
            return R.error("获取行业信息详细信息失败！");
        }
    }
}
