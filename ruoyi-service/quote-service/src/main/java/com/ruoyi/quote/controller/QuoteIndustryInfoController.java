package com.ruoyi.quote.controller;

import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.quote.domain.entity.QuotePostInfo;
import com.ruoyi.quote.domain.vo.QuoteIndustryInfoVO;
import com.ruoyi.quote.service.IQuotePostInfoService;
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
import com.ruoyi.quote.domain.entity.QuoteIndustryInfo;
import com.ruoyi.quote.service.IQuoteIndustryInfoService;

/**
 * 行业信息Controller
 *
 * @author yrb
 * @date 2022-04-26
 */
@RestController
@RequestMapping("/quote/industryInfo")
public class QuoteIndustryInfoController extends BaseController {

    private final IQuoteIndustryInfoService quoteIndustryInfoService;
    private final IQuotePostInfoService quotePostInfoService;

    @Autowired
    public QuoteIndustryInfoController(IQuoteIndustryInfoService quoteIndustryInfoService,
                                       IQuotePostInfoService quotePostInfoService) {
        this.quoteIndustryInfoService = quoteIndustryInfoService;
        this.quotePostInfoService = quotePostInfoService;
    }

    /**
     * 查询行业信息列表
     */
    @GetMapping("/list")
    public R list(QuoteIndustryInfo quoteIndustryInfo) {
        try {
            Page page = startPageWithTotal();
            page.setOrderBy("create_time desc");
            if (null == quoteIndustryInfo || null == quoteIndustryInfo.getParentId()) {
                quoteIndustryInfo.setParentId(0L);
            }
            List<QuoteIndustryInfo> list = quoteIndustryInfoService.selectQuoteIndustryInfoList(quoteIndustryInfo);
            return R.data(list, page.getTotal());
        } catch (Exception e) {
            logger.error("查询行业信息列表失败,异常信息：" + e);
            return R.error("查询行业信息列表失败！");
        }
    }

    /**
     * 获取行业信息详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            return R.data(quoteIndustryInfoService.selectQuoteIndustryInfoById(id));
        } catch (Exception e) {
            logger.error("获取行业信息详细信息失败,异常信息：" + e);
            return R.error("获取行业信息详细信息失败！");
        }
    }

    /**
     * 新增行业信息
     */
    @OperLog(title = "行业信息", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody QuoteIndustryInfo quoteIndustryInfo) {
        try {
            if (null == quoteIndustryInfo.getProjectId()) {
                return R.error("所属项目id不能为空！");
            }
            if (null == quoteIndustryInfo.getIndustryName() || StrUtil.isBlank(quoteIndustryInfo.getIndustryName())) {
                return R.error("行业名称不能为空！");
            }
            if (quoteIndustryInfoService.insertQuoteIndustryInfo(quoteIndustryInfo) > 0) {
                return R.ok("新增行业信息成功.");
            }
            return R.error("新增行业信息失败！");
        } catch (Exception e) {
            logger.error("新增行业信息失败,异常信息：" + e);
            return R.error("新增行业信息失败," + e.getMessage());
        }
    }

    /**
     * 修改行业信息
     */
    @OperLog(title = "行业信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody QuoteIndustryInfo quoteIndustryInfo) {
        try {
            if (null == quoteIndustryInfo) {
                return R.error("修改内容不能为空！");
            }
            if (null == quoteIndustryInfo.getId()) {
                return R.error("行业id不能为空！");
            }
            if (null == quoteIndustryInfo.getProjectId()) {
                return R.error("项目id不能为空！");
            }
            if (quoteIndustryInfoService.updateQuoteIndustryInfo(quoteIndustryInfo) > 0) {
                return R.ok("修改行业信息成功.");
            }
            return R.error("修改行业信息失败！");
        } catch (Exception e) {
            logger.error("修改行业信息失败,异常信息：" + e);
            return R.error("修改行业信息失败," + e.getMessage());
        }
    }

    /**
     * 删除行业信息
     */
    @OperLog(title = "行业信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("id不能为空！");
            }
            // 删除时添加校验是否关联岗位
            List<QuotePostInfo> quotePostInfoList = quotePostInfoService.selectQuotePostInfoByIds(ids);
            if (null != quotePostInfoList && quotePostInfoList.size() > 0) {
                return R.error("该行业已关联岗位，无法删除！");
            }
            if (quoteIndustryInfoService.deleteQuoteIndustryInfoByIds(ids) > 0) {
                return R.ok("删除行业信息成功.");
            }
            return R.error("删除行业信息失败！");
        } catch (Exception e) {
            logger.error("删除行业信息失败,异常信息：" + e);
            return R.error("删除行业信息失败！");
        }
    }

    /**
     * 查询行业信息列表(自定义：包含子父类)
     */
    @GetMapping("/getIndusryInfoList")
    public R getIndusryInfoList(QuoteIndustryInfoVO quoteIndustryInfoVO) {
        try {
            pageUtil();
            List<QuoteIndustryInfoVO> quoteIndustryInfoUserList = quoteIndustryInfoService.findQuoteIndustryInfoUserList(quoteIndustryInfoVO);
            return resultData(quoteIndustryInfoUserList);
        } catch (Exception e) {
            logger.error("获取行业列表信息失败，异常信息：" + e);
            return R.error("获取行业列表信息失败!");
        }
    }

    /**
     * （环境、公卫）删除行业大类、子类
     */
    @OperLog(title = "删除行业大类、子类", businessType = BusinessType.DELETE)
    @DeleteMapping("/delete/{ids}")
    public R delete(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("id不能为空！");
            }
            if (quoteIndustryInfoService.deleteIndustryInfo(ids)) {
                return R.ok("行业删除成功!");
            }
            return R.error("行业删除失败！");
        } catch (Exception e) {
            logger.error("行业信息删除失败，异常信息：" + e);
            return R.error("行业信息删除失败,"+e.getMessage());
        }
    }

    /**
     * 查询行业信息列表(职卫、公卫)
     */
    @GetMapping("/getIndusryInfoUserList")
    public R getIndusryInfoUserList(QuoteIndustryInfo quoteIndustryInfo) {
        try {
            if (null == quoteIndustryInfo.getProjectId()) {
                return R.error("所属项目id不能为空!");
            }
            pageUtil();
            List<QuoteIndustryInfoVO> quoteIndustryInfoUserList = quoteIndustryInfoService.findIndustryInfoList(quoteIndustryInfo);
            return resultData(quoteIndustryInfoUserList);
        } catch (Exception e) {
            logger.error("获取行业列表信息失败，异常信息：" + e);
            return R.error("获取行业列表信息失败!");
        }
    }
}
