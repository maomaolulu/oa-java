package com.ruoyi.quote.controller;

import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.quote.domain.entity.QuotePointInfo;
import com.ruoyi.quote.service.IQuotePointInfoService;

/**
 * (环境)点位信息Controller
 *
 * @author yrb
 * @date 2022-06-17
 */
@RestController
@RequestMapping("/quote/pointInfo")
public class QuotePointInfoController extends BaseController {

    private final IQuotePointInfoService quotePointInfoService;

    @Autowired
    public QuotePointInfoController(IQuotePointInfoService quotePointInfoService) {
        this.quotePointInfoService = quotePointInfoService;
    }

    /**
     * 查询点位信息列表
     */
    @GetMapping("/list")
    public R list(QuotePointInfo quotePointInfo) {
        try {
            pageUtil();
            List<QuotePointInfo> list = quotePointInfoService.selectQuotePointInfoList(quotePointInfo);
            return resultData(list);
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息：" + e);
            return R.error("信息获取失败!");
        }
    }

    /**
     * 获取点位信息详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            return R.data(quotePointInfoService.selectQuotePointInfoById(id));
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息：" + e);
            return R.error("信息获取失败!");
        }
    }

    /**
     * 新增点位信息
     */
    @OperLog(title = "点位信息", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody QuotePointInfo quotePointInfo) {
        try {
            if (quotePointInfoService.insertQuotePointInfo(quotePointInfo) > 0) {
                return R.ok("新增成功！");
            }
            return R.error("新增失败！");
        } catch (Exception e) {
            logger.error("新增失败，异常信息：" + e);
            return R.error("新增失败!");
        }
    }

    /**
     * 修改点位信息
     */
    @OperLog(title = "点位信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody QuotePointInfo quotePointInfo) {
        try {
            if (null == quotePointInfo.getId()) {
                return R.error("id不能为空！");
            }
            if (quotePointInfoService.updateQuotePointInfo(quotePointInfo) > 0) {
                return R.ok("编辑成功!");
            }
            return R.error("编辑失败！");
        } catch (Exception e) {
            logger.error("编辑失败，异常信息：" + e);
            return R.error("编辑失败!");
        }
    }

    /**
     * 删除点位信息
     */
    @OperLog(title = "点位信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("id不能为空！");
            }
            if (quotePointInfoService.deleteQuotePointInfoByIds(ids) > 0) {
                return R.ok("删除成功！");
            }
            return R.error("删除失败！");
        } catch (Exception e) {
            logger.error("删除失败，异常信息：" + e);
            return R.error("删除失败!");
        }
    }

    /**
     * 删除点位信息（删除关联的检测项信息）
     */
    @OperLog(title = "点位信息", businessType = BusinessType.INSERT)
    @PostMapping("/delete")
    public R delete(@RequestBody QuotePointInfo quotePointInfo) {
        try {
            if (null == quotePointInfo.getId()) {
                return R.error("点位主键id不能为空！");
            }
            if (StrUtil.isBlank(quotePointInfo.getSheetId())) {
                return R.error("表单id不能为空！");
            }
            if (null == quotePointInfo.getSubId()) {
                return R.error("子类id不能为空！");
            }
            if (quotePointInfoService.deleteQuotePointInfoRelationTestItem(quotePointInfo)) {
                return R.ok("删除成功！");
            }
            return R.error("删除失败！");
        } catch (Exception e) {
            logger.error("删除失败，异常信息：" + e);
            return R.error("删除失败!");
        }
    }

    /**
     * 新增点位信息
     */
    @OperLog(title = "新增点位信息", businessType = BusinessType.INSERT)
    @PostMapping("/addPointInfo")
    public R addPointInfo(@RequestBody QuotePointInfo quotePointInfo) {
        try {
            QuotePointInfo pointInfo = quotePointInfoService.insertQuotePointInfoReturnResult(quotePointInfo);
            if (null != pointInfo) {
                return R.data(pointInfo);
            }
            return R.error("新增失败！");
        } catch (Exception e) {
            logger.error("新增失败，异常信息：" + e);
            return R.error("新增失败!");
        }
    }
}
