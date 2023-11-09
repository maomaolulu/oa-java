package com.ruoyi.quote.controller;

import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.quote.domain.dto.PostExpenseDetailsDTO;
import com.ruoyi.quote.domain.entity.QuotePostItems;
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
import com.ruoyi.quote.service.IQuotePostItemsService;

/**
 * 岗位检测项目Controller
 *
 * @author yrb
 * @date 2022-06-10
 */
@RestController
@RequestMapping("/quote/postItems")
public class QuotePostItemsController extends BaseController {
    private final IQuotePostItemsService quotePostItemsService;

    @Autowired
    public QuotePostItemsController(IQuotePostItemsService quotePostItemsService) {
        this.quotePostItemsService = quotePostItemsService;
    }

    /**
     * 查询岗位检测项目列表
     */
    @GetMapping("/list")
    public R list(QuotePostItems quotePostItems) {
        try {
            pageUtil();
            List<QuotePostItems> list = quotePostItemsService.selectQuotePostItemsList(quotePostItems);
            return resultData(list);
        } catch (Exception e) {
            logger.error("获取检测项目信息失败,异常信息：" + e);
            return R.error("获取检测项目信息失败！");
        }
    }

    /**
     * 获取岗位检测项目详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            return R.data(quotePostItemsService.selectQuotePostItemsById(id));
        } catch (Exception e) {
            logger.error("获取检测项目信息失败,异常信息：" + e);
            return R.error("获取检测项目信息失败！");
        }
    }

    /**
     * 新增岗位检测项目
     */
    @OperLog(title = "岗位检测项目", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody QuotePostItems quotePostItems) {
        try {
            if (null == quotePostItems) {
                return R.error("新增信息不能为空！");
            }
            if (quotePostItemsService.insertQuotePostItems(quotePostItems) > 0) {
                return R.ok("添加成功！");
            }
            return R.error("添加失败！");
        } catch (Exception e) {
            logger.error("新增检测项目信息失败,异常信息：" + e);
            return R.error("新增检测项目信息失败！");
        }
    }

    /**
     * 修改岗位检测项目
     */
    @OperLog(title = "岗位检测项目", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody QuotePostItems quotePostItems) {
        try {
            if (null == quotePostItems) {
                return R.error("新增信息不能为空！");
            }
            if (null == quotePostItems.getId()) {
                return R.error("id不能为空！");
            }
            if (quotePostItemsService.updateQuotePostItems(quotePostItems) > 0) {
                return R.ok("编辑成功！");
            }
            return R.error("编辑失败！");
        } catch (Exception e) {
            logger.error("编辑检测项目信息失败,异常信息：" + e);
            return R.error("编辑检测项目信息失败！");
        }
    }

    /**
     * 删除岗位检测项目
     */
    @OperLog(title = "岗位检测项目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("id不存在！");
            }
            if (quotePostItemsService.deleteQuotePostItemsByIds(ids) > 0) {
                return R.ok("删除成功！");
            }
            return R.error("删除失败！");
        } catch (Exception e) {
            logger.error("检测项目信息删除失败,异常信息：" + e);
            return R.error("检测项目信息删除失败！");
        }
    }

    /**
     * 获取子类对应岗位检测信息列表
     */
    @OperLog(title = "获取子类对应岗位检测信息列表", businessType = BusinessType.OTHER)
    @PostMapping("/getSubPostExpenseDetails")
    public R getSubPostExpenseDetails(@RequestBody PostExpenseDetailsDTO postExpenseDetailsDTO) {
        try {
            if (StrUtil.isBlank(postExpenseDetailsDTO.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (StrUtil.isBlank(postExpenseDetailsDTO.getCompanyName())) {
                return R.error("公司名称不能为空");
            }
            if (postExpenseDetailsDTO.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (postExpenseDetailsDTO.getList() == null || postExpenseDetailsDTO.getList().size() == 0) {
                return R.error("岗位id不能为空");
            }
            return resultData(quotePostItemsService.findSubPostExpenseDetails(postExpenseDetailsDTO));
        } catch (Exception e) {
            logger.error("获取子类对应岗位检测信息列表失败，异常信息：" + e);
            return R.error("获取子类对应岗位检测信息列表失败，" + e.getMessage());
        }
    }

    /**
     * 删除子类对应岗位检测信息
     */
    @OperLog(title = "删除子类对应岗位检测信息列表", businessType = BusinessType.OTHER)
    @PostMapping("/deleteSubPostExpenseDetails")
    public R deleteSubPostExpenseDetails(@RequestBody PostExpenseDetailsDTO postExpenseDetailsDTO) {
        try {
            if (StrUtil.isBlank(postExpenseDetailsDTO.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (postExpenseDetailsDTO.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (postExpenseDetailsDTO.getPostId() == null) {
                return R.error("岗位id不能为空");
            }
            if (quotePostItemsService.deleteSubPostExpenseDetails(postExpenseDetailsDTO)) {
                return R.ok("删除成功");
            }
            return R.error("删除失败");
        } catch (Exception e) {
            logger.error("删除岗位对应检测信息失败，异常信息：" + e);
            return R.error("删除岗位对应检测信息失败," + e.getMessage());
        }
    }
}
