package com.ruoyi.quote.controller;

import java.util.List;

import com.ruoyi.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.quote.domain.entity.QuoteTestNature;
import com.ruoyi.quote.service.IQuoteTestNatureService;

/**
 * 检测性质Controller
 *
 * @author yrb
 * @date 2022-09-13
 */
@RestController
@RequestMapping("/quote/nature")
public class QuoteTestNatureController extends BaseController {

    private final IQuoteTestNatureService quoteTestNatureService;

    @Autowired
    public QuoteTestNatureController(IQuoteTestNatureService quoteTestNatureService) {
        this.quoteTestNatureService = quoteTestNatureService;
    }

    /**
     * 查询检测性质列表
     */
    @GetMapping("/list")
    public R list(QuoteTestNature quoteTestNature) {
        try {
            pageUtil();
            List<QuoteTestNature> list = quoteTestNatureService.selectQuoteTestNatureList(quoteTestNature);
            return resultData(list);
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息：" + e);
            return R.error("信息获取失败!");
        }
    }

    /**
     * 获取已选中的检测性质列表
     */
    @GetMapping("/getCheckedlist")
    public R getCheckedlist(String natureIds) {
        try {
            List<QuoteTestNature> list = quoteTestNatureService.findQuoteTestNatureList(natureIds);
            return resultData(list);
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息：" + e);
            return R.error("信息获取失败!");
        }
    }
}
