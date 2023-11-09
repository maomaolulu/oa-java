package com.ruoyi.quote.controller;

import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.quote.domain.dto.SheetItemsDTO;
import com.ruoyi.quote.domain.vo.QuoteTestItemDetailsVO;
import org.apache.commons.lang.StringUtils;
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
import com.ruoyi.quote.domain.entity.QuoteSheetItems;
import com.ruoyi.quote.service.IQuoteSheetItemsService;

/**
 * 报价检测项目Controller
 *
 * @author yrb
 * @date 2022-04-29
 */
@RestController
@RequestMapping("/quote/items")
public class QuoteSheetItemsController extends BaseController {
    private final IQuoteSheetItemsService quoteSheetItemsService;

    @Autowired
    public QuoteSheetItemsController(IQuoteSheetItemsService quoteSheetItemsService) {
        this.quoteSheetItemsService = quoteSheetItemsService;
    }

    /**
     * 查询报价检测项目列表
     */
    @GetMapping("/list")
    public R list(QuoteSheetItems quoteSheetItems) {
        try {
            Page page = startPageWithTotal();
            List<QuoteSheetItems> list = quoteSheetItemsService.selectQuoteSheetItemsList(quoteSheetItems);
            return R.data(list, page.getTotal());
        } catch (Exception e) {
            logger.error("查询报价检测项目列表失败,异常信息：" + e);
            return R.error("查询报价检测项目列表失败！");
        }
    }

    /**
     * app端-报价记录-获取检测费明细
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            return R.data(quoteSheetItemsService.selectQuoteSheetItemsById(id));
        } catch (Exception e) {
            logger.error("获取报价检测项目详细信息失败,异常信息：" + e);
            return R.error("获取报价检测项目详细信息失败！");
        }
    }

    /**
     * 新增报价检测项目
     */
    @OperLog(title = "报价检测项目", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody QuoteSheetItems quoteSheetItems) {
        try {
            if (null == quoteSheetItems) {
                return R.error("新增内容不能为空！");
            }
            if (quoteSheetItemsService.insertQuoteSheetItems(quoteSheetItems) > 0) {
                return R.ok("新增报价检测项目成功.");
            }
            return R.error("新增报价检测项目失败！");
        } catch (Exception e) {
            logger.error("新增报价检测项目失败,异常信息：" + e);
            return R.error("新增报价检测项目失败！");
        }
    }

    /**
     * 新增报价检测项目(自定义)
     */
    @OperLog(title = "新增报价检测项目", businessType = BusinessType.INSERT)
    @PostMapping("/addQuoteSheetItemsBatch")
    public R addQuoteSheetItemsBatch(@RequestBody SheetItemsDTO sheetItemsDTO) {
        try {
            if (null == sheetItemsDTO) {
                return R.error("要添加的检测项目信息不能为空！");
            }
            if (StrUtil.isBlank(sheetItemsDTO.getSheetId())) {
                return R.error("报价单id不能为空！");
            }
            if (sheetItemsDTO.getPostId() == null) {
                return R.error("岗位id不能为空");
            }
            if (quoteSheetItemsService.insertQuoteSheetItemsBatch(sheetItemsDTO)) {
                return R.ok("新增报价检测项目成功.");
            }
            return R.error("新增报价检测项目失败!");
        } catch (Exception e) {
            logger.error("新增报价检测项目失败,异常信息：" + e);
            return R.error("新增报价检测项目失败," + e.getMessage());
        }
    }

    /**
     * 修改报价检测项目
     */
    @OperLog(title = "报价检测项目", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody QuoteSheetItems quoteSheetItems) {
        try {
            if (null == quoteSheetItems) {
                return R.error("修改内容不能为空！");
            }
            if (null == quoteSheetItems.getId()) {
                return R.error("id不能为空！");
            }
            if (quoteSheetItemsService.updateQuoteSheetItems(quoteSheetItems) > 0) {
                return R.ok("修改报价检测项目成功.");
            }
            return R.error("修改报价检测项目失败！");
        } catch (Exception e) {
            logger.error("修改报价检测项目失败,异常信息：" + e);
            return R.error("修改报价检测项目失败！");
        }
    }

    /**
     * 删除报价检测项目
     */
    @OperLog(title = "报价检测项目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("id不能为空！");
            }
            if (quoteSheetItemsService.deleteQuoteSheetItemsByIds(ids) > 0) {
                return R.ok("删除报价检测项目成功.");
            }
            return R.error("删除报价检测项目失败！");
        } catch (Exception e) {
            logger.error("删除报价检测项目失败,异常信息：" + e);
            return R.error("删除报价检测项目失败！");
        }
    }

    /**
     * 获取报价检测项目(导出用)
     */
    @GetMapping(value = "/bySheetId/{sheetId}")
    public R getInfo(@PathVariable("sheetId") String sheetId) {
        try {
            if (null == sheetId) {
                return R.error("报价单id不能为空");
            }
            return R.data(quoteSheetItemsService.findQuoteSheetItemsBySheetId(sheetId));
        } catch (Exception e) {
            logger.error("获取报价检测项目失败,异常信息：" + e);
            return R.error("获取报价检测项目失败！");
        }
    }

    /**
     * 获取报价检测项目(报价单)
     */
    @GetMapping(value = "/getItems")
    public R getItems(String sheetId, String postName) {
        try {
            if (StringUtils.isBlank(sheetId)) {
                return R.error("报价单id不能为空！");
            }
            if (StringUtils.isBlank(postName)) {
                return R.error("岗位名称不能为空！");
            }
            return R.data(quoteSheetItemsService.findQuoteSheetItems(sheetId, postName));
        } catch (Exception e) {
            logger.error("获取报价检测项目失败,异常信息：" + e);
            return R.error("获取报价检测项目失败！");
        }
    }

    /**
     * app端-职卫、环境、公卫-获取检测费用明细 参数（表单id、子类id）
     */
    @GetMapping("/getTestExpensesDetails")
    public R getTestExpensesDetails(QuoteSheetItems quoteSheetItems) {
        try {
            if (StringUtils.isBlank(quoteSheetItems.getSheetId())) {
                return R.error("表单id不能为空！");
            }
            if (null == quoteSheetItems.getSubId()) {
                return R.error("子类id不能为空！");
            }
            pageUtil();
            List<QuoteTestItemDetailsVO> list = quoteSheetItemsService.findQuoteTestExpensesDetailsList(quoteSheetItems);
            return resultData(list);
        } catch (Exception e) {
            logger.error("查询报价检测项目列表失败,异常信息：" + e);
            return R.error("查询报价检测项目列表失败！" + e.getMessage());
        }
    }
}
