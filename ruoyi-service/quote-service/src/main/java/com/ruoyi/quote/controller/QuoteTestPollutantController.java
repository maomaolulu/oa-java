package com.ruoyi.quote.controller;

import java.util.List;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.quote.domain.entity.QuoteBaseFactor;
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
import com.ruoyi.quote.domain.entity.QuoteTestPollutant;
import com.ruoyi.quote.service.IQuoteTestPollutantService;

/**
 * 检测类型污染物关联Controller
 *
 * @author yrb
 * @date 2022-06-28
 */
@RestController
@RequestMapping("/quote/testPollutant")
public class QuoteTestPollutantController extends BaseController {

    private final IQuoteTestPollutantService quoteTestPollutantService;

    @Autowired
    public QuoteTestPollutantController(IQuoteTestPollutantService quoteTestPollutantService) {
        this.quoteTestPollutantService = quoteTestPollutantService;
    }

    /**
     * 查询检测类型污染物关联列表
     */
    @GetMapping("/list")
    public R list(QuoteTestPollutant quoteTestPollutant) {
        try {
            pageUtil();
            List<QuoteTestPollutant> list = quoteTestPollutantService.selectQuoteTestPollutantList(quoteTestPollutant);
            return resultData(list);
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息：" + e);
            return R.error("信息获取失败!");
        }
    }

    /**
     * 获取检测类型污染物关联详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            return R.data(quoteTestPollutantService.selectQuoteTestPollutantById(id));
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息：" + e);
            return R.error("信息获取失败!");
        }
    }

    /**
     * 新增检测类型污染物关联
     */
    @OperLog(title = "检测类型污染物关联", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody QuoteTestPollutant quoteTestPollutant) {
        try {
            if (quoteTestPollutantService.insertQuoteTestPollutant(quoteTestPollutant) > 0) {
                return R.ok("新增成功！");
            }
            return R.error("新增失败！");
        } catch (Exception e) {
            logger.error("新增失败，异常信息：" + e);
            return R.error("新增失败!");
        }
    }

    /**
     * 修改检测类型污染物关联
     */
    @OperLog(title = "检测类型污染物关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody QuoteTestPollutant quoteTestPollutant) {
        try {
            if (null == quoteTestPollutant.getId()) {
                return R.error("id不能为空！");
            }
            if (quoteTestPollutantService.updateQuoteTestPollutant(quoteTestPollutant) > 0) {
                return R.ok("编辑成功!");
            }
            return R.error("编辑失败！");
        } catch (Exception e) {
            logger.error("编辑失败，异常信息：" + e);
            return R.error("编辑失败!");
        }
    }

    /**
     * 删除检测类型污染物关联
     */
    @OperLog(title = "检测类型污染物关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("id不能为空！");
            }
            if (quoteTestPollutantService.deleteQuoteTestPollutantByIds(ids) > 0) {
                return R.ok("删除成功！");
            }
            return R.error("删除失败！");
        } catch (Exception e) {
            logger.error("删除失败，异常信息：" + e);
            return R.error("删除失败!");
        }
    }

    /**
     * 查询检测类型污染物关联列表(公卫)
     */
    @GetMapping("/getRelationBaseFactor")
    public R getRelationBaseFactor(Long id) {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            pageUtil();
            List<QuoteBaseFactor> list = quoteTestPollutantService.findRelationBaseFactorList(id);
            return resultData(list);
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息：" + e);
            return R.error("信息获取失败!");
        }
    }

    /**
     * 公卫 删除检测类别对应的污染物
     */
    @OperLog(title = "删除检测类别对应的污染物", businessType = BusinessType.INSERT)
    @PostMapping("/delete")
    public R delete(@RequestBody QuoteTestPollutant quoteTestPollutant) {
        try {
            if (quoteTestPollutant.getId() == null) {
                return R.error("关联主键id不能为空");
            }
            if (quoteTestPollutant.getPollutantId() == null) {
                return R.error("污染物id不能为空");
            }
            if (quoteTestPollutantService.deleteRelationPollutant(quoteTestPollutant)) {
                return R.ok("删除关联污染物成功");
            }
            return R.error("删除关联污染物失败");
        } catch (Exception e) {
            logger.error("删除关联污染物失败，异常信息：" + e);
            return R.error("删除关联污染物失败");
        }
    }
}
