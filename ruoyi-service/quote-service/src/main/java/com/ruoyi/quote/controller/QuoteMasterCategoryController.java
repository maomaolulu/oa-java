package com.ruoyi.quote.controller;

import java.util.List;

import com.github.pagehelper.Page;
import com.ruoyi.common.core.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.quote.domain.entity.QuoteMasterCategory;
import com.ruoyi.quote.service.IQuoteMasterCategoryService;

/**
 * 报价主分类Controller
 *
 * @author yrb
 * @date 2022-04-25
 */
@Slf4j
@RestController
@RequestMapping("/quote/category")
public class QuoteMasterCategoryController extends BaseController {
    private final IQuoteMasterCategoryService quoteMasterCategoryService;

    @Autowired
    public QuoteMasterCategoryController(IQuoteMasterCategoryService quoteMasterCategoryService) {
        this.quoteMasterCategoryService = quoteMasterCategoryService;
    }

    /**
     * 查询报价主分类列表
     */
    @GetMapping("/list")
    public R list(QuoteMasterCategory quoteMasterCategory) {
        try {
            Page page = startPageWithTotal();
            List<QuoteMasterCategory> list = quoteMasterCategoryService.selectQuoteMasterCategoryList(quoteMasterCategory);
            return R.data(list, page.getTotal());
        } catch (Exception e) {
            logger.error("查询报价主分类列表失败,异常信息：" + e);
            return R.error("查询报价主分类列表失败！");
        }
    }
}
