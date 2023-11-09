package com.ruoyi.quote.controller;

import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.quote.domain.dto.QuoteBaseFactorDTO;
import com.ruoyi.quote.domain.entity.QuoteSheetItems;
import com.ruoyi.quote.domain.vo.QuoteBaseFactorVO;
import com.ruoyi.quote.domain.vo.QuoteHarmFactorVO;
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
import com.ruoyi.quote.domain.entity.QuoteHarmFactor;
import com.ruoyi.quote.service.IQuoteHarmFactorService;

/**
 * 危害因素Controller
 *
 * @author yrb
 * @date 2022-04-27
 */
@RestController
@RequestMapping("/quote/factor")
public class QuoteHarmFactorController extends BaseController {
    private final IQuoteHarmFactorService quoteHarmFactorService;

    @Autowired
    public QuoteHarmFactorController(IQuoteHarmFactorService quoteHarmFactorService) {
        this.quoteHarmFactorService = quoteHarmFactorService;
    }

    /**
     * 查询危害因素列表
     */
    @GetMapping("/list")
    public R list(QuoteBaseFactorVO quoteHarmFactorVO) {
        try {
            Page page = startPageWithTotal();
            List<QuoteBaseFactorVO> list = quoteHarmFactorService.selectQuoteHarmFactorList(quoteHarmFactorVO);
            return R.data(list, page.getTotal());
        } catch (Exception e) {
            logger.error("查询危害因素列表失败,异常信息：" + e);
            return R.error("查询危害因素列表失败," + e.getMessage());
        }
    }

    /**
     * 查询危害因素列表 置顶列表 岗位对应的危害因素
     */
    @GetMapping("/app/top/list")
    public R appTopList(QuoteHarmFactorVO quoteHarmFactorVO) {
        try {
            List<QuoteBaseFactorVO> list = quoteHarmFactorService.findQuoteHarmFactorAppTopList(quoteHarmFactorVO);
            return R.data(list);
        } catch (Exception e) {
            logger.error("查询危害因素列表失败,异常信息：" + e);
            return R.error("查询危害因素列表失败," + e.getMessage());
        }
    }

    /**
     * 查询危害因素列表 通过岗位id
     */
    @GetMapping("/app/list")
    public R appList(QuoteBaseFactorVO quoteBaseFactorVO) {
        try {
            Page page = startPageWithTotal();
            List<QuoteBaseFactorVO> list = quoteHarmFactorService.findQuoteHarmFactorAppList(quoteBaseFactorVO);
            return R.data(list, page.getTotal());
        } catch (Exception e) {
            logger.error("查询危害因素列表失败,异常信息：" + e);
            return R.error("查询危害因素列表失败," + e.getMessage());
        }
    }

    /**
     * 查询危害因素列表 置顶列表 岗位对应的危害因素
     */
    @GetMapping("/app/top/base/list")
    public R appTopBaseList(QuoteHarmFactorVO quoteHarmFactorVO) {
        try {
            List<QuoteBaseFactorVO> list = quoteHarmFactorService.findQuoteHarmFactorAppTopBaseList(quoteHarmFactorVO);
            return R.data(list);
        } catch (Exception e) {
            logger.error("查询危害因素列表失败,异常信息：" + e);
            return R.error("查询危害因素列表失败," + e.getMessage());
        }
    }

    /**
     * 查询危害因素列表 去除已选的和岗位对应额
     */
    @GetMapping("/app/base/list")
    public R appBaseList(QuoteBaseFactorVO quoteBaseFactorVO) {
        try {
            if (StrUtil.isBlank(quoteBaseFactorVO.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteBaseFactorVO.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            Page page = startPageWithTotal();
            List<QuoteBaseFactorVO> list = quoteHarmFactorService.findQuoteHarmFactorAppBaseList(quoteBaseFactorVO);
            return R.data(list, page.getTotal());
        } catch (Exception e) {
            logger.error("查询危害因素列表失败,异常信息：" + e);
            return R.error("查询危害因素列表失败," + e.getMessage());
        }
    }

    /**
     * 查询危害因素列表
     */
    @GetMapping("/userList")
    public R userList(QuoteHarmFactor quoteHarmFactor) {
        try {
            Page page = startPageWithTotal();
            List<QuoteHarmFactorVO> list = quoteHarmFactorService.selectQuoteHarmFactorUserList(quoteHarmFactor);
            return R.data(list, page.getTotal());
        } catch (Exception e) {
            logger.error("查询危害因素列表失败,异常信息：" + e);
            return R.error("查询危害因素列表失败！");
        }
    }

    /**
     * 获取危害因素详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            return R.data(quoteHarmFactorService.selectQuoteHarmFactorById(id));
        } catch (Exception e) {
            logger.error("获取危害因素详细信息失败,异常信息：" + e);
            return R.error("获取危害因素详细信息失败！");
        }
    }

    /**
     * 新增危害因素
     */
    @OperLog(title = "危害因素", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody QuoteHarmFactor quoteHarmFactor) {
        try {
            if (null == quoteHarmFactor) {
                return R.error("新增内容不能为空！");
            }
            if (quoteHarmFactorService.insertQuoteHarmFactor(quoteHarmFactor) > 0) {
                return R.ok("新增危害因素成功.");
            }
            return R.error("新增危害因素失败！");
        } catch (Exception e) {
            logger.error("新增危害因素失败,异常信息：" + e);
            return R.error("新增危害因素失败！");
        }
    }

    /**
     * 修改危害因素
     */
    @OperLog(title = "危害因素", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody QuoteHarmFactor quoteHarmFactor) {
        try {
            if (null == quoteHarmFactor) {
                return R.error("修改内容不能为空！");
            }
            if (null == quoteHarmFactor.getId()) {
                return R.error("危害因素id不能为空！");
            }
            if (quoteHarmFactorService.updateQuoteHarmFactor(quoteHarmFactor) > 0) {
                return R.ok("修改危害因素成功.");
            }
            return R.error("修改危害因素失败！");
        } catch (Exception e) {
            logger.error("修改危害因素失败,异常信息：" + e);
            return R.error("修改危害因素失败！");
        }
    }

    /**
     * 删除危害因素
     */
    @OperLog(title = "危害因素", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("id不能为空！");
            }
            if (quoteHarmFactorService.deleteQuoteHarmFactorByIds(ids) > 0) {
                return R.ok("删除危害因素成功.");
            }
            return R.error("删除危害因素失败!");
        } catch (Exception e) {
            logger.error("删除危害因素失败,异常信息：" + e);
            return R.error("删除危害因素失败！");
        }
    }

    /**
     * 更新缓存 添加其他危害因素
     */
    @OperLog(title = "更新缓存", businessType = BusinessType.INSERT)
    @PostMapping("/updateCache")
    public R updateCache(@RequestBody QuoteBaseFactorDTO quoteBaseFactorDTO) {
        try {
            if (StrUtil.isBlank(quoteBaseFactorDTO.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteBaseFactorDTO.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (quoteHarmFactorService.updateCache(quoteBaseFactorDTO)) {
                return R.ok("缓存信息更新成功");
            }
            return R.error("缓存信息更新失败");
        } catch (Exception e) {
            logger.error("缓存信息更新失败,异常信息：" + e);
            return R.error("缓存信息更新失败," + e.getMessage());
        }
    }

    /**
     * 从职卫基础危害信息中添加危害因素
     */
    @OperLog(title = "从职卫基础危害信息中添加危害因素", businessType = BusinessType.INSERT)
    @PostMapping("/addFactorFromBaseHarmFactor")
    public R addFactorFromBaseHarmFactor(@RequestBody QuoteBaseFactorDTO quoteBaseFactorDTO) {
        try {
            if (StrUtil.isBlank(quoteBaseFactorDTO.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteBaseFactorDTO.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (quoteHarmFactorService.addFactorFromBaseHarmFactor(quoteBaseFactorDTO)) {
                return R.ok("缓存信息更新成功");
            }
            return R.error("缓存信息更新失败");
        } catch (Exception e) {
            logger.error("缓存信息更新失败,异常信息：" + e);
            return R.error("缓存信息更新失败," + e.getMessage());
        }
    }

    /**
     * 获取点位数
     */
    @PostMapping("/getPointNumber")
    public R getPointNumber(@RequestBody QuoteSheetItems quoteSheetItems) {
        try {
            if (StrUtil.isBlank(quoteSheetItems.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteSheetItems.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (quoteSheetItems.getPostId()==null){
                return R.error("岗位id不能为空");
            }
            return R.data(quoteHarmFactorService.findPointNumber(quoteSheetItems));
        } catch (Exception e) {
            logger.error("获取点位数失败," + e);
            return R.error("获取点位数失败," + e.getMessage());
        }
    }

    /**
     * 上一步
     */
    @OperLog(title = "上一步", businessType = BusinessType.OTHER)
    @PostMapping("/previousStep")
    public R previousStep(QuoteHarmFactorVO quoteHarmFactorVO) {
        try {
            if (StrUtil.isBlank(quoteHarmFactorVO.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteHarmFactorVO.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (quoteHarmFactorService.recoverCache(quoteHarmFactorVO)) {
                return R.ok("成功");
            }
            return R.error("返回上一步失败");
        } catch (Exception e) {
            logger.error("恢复原有数据失败," + e);
            return R.error("恢复原有数据失败," + e.getMessage());
        }
    }
}
