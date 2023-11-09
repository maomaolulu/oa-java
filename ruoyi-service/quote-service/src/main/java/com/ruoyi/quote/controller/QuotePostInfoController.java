package com.ruoyi.quote.controller;

import java.util.List;

import com.github.pagehelper.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.quote.domain.dto.PostHarmFactorDTO;
import com.ruoyi.quote.domain.dto.QuotePostInfoDTO;
import com.ruoyi.quote.domain.vo.QuotePostInfoVO;
import com.ruoyi.quote.service.IQuoteSheetItemsService;
import com.ruoyi.quote.utils.QuoteUtil;
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
import com.ruoyi.quote.domain.entity.QuotePostInfo;
import com.ruoyi.quote.service.IQuotePostInfoService;

/**
 * 岗位信息Controller
 *
 * @author yrb
 * @date 2022-04-26
 */
@RestController
@RequestMapping("/quote/postInfo")
public class QuotePostInfoController extends BaseController {
    private final IQuotePostInfoService quotePostInfoService;
    private final IQuoteSheetItemsService quoteSheetItemsService;

    @Autowired
    public QuotePostInfoController(IQuotePostInfoService quotePostInfoService,
                                   IQuoteSheetItemsService quoteSheetItemsService) {
        this.quotePostInfoService = quotePostInfoService;
        this.quoteSheetItemsService = quoteSheetItemsService;
    }

    /**
     * 查询岗位信息列表
     */
    @GetMapping("/list")
    public R list(QuotePostInfoVO quotePostInfoVO) {
        try (Page<Object> page = startPageWithTotal()) {
            List<QuotePostInfoVO> list = quotePostInfoService.selectQuotePostInfoList(quotePostInfoVO);
            return R.data(list, page.getTotal());
        } catch (Exception e) {
            logger.error("查询岗位信息列表失败,异常信息：" + e);
            return R.error("查询岗位信息列表失败！");
        }
    }

    /**
     * 获取岗位信息详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            return R.data(quotePostInfoService.selectQuotePostInfoById(id));
        } catch (Exception e) {
            logger.error("获取岗位信息详细信息失败,异常信息：" + e);
            return R.error("获取岗位信息详细信息失败！");
        }
    }

    /**
     * 新增岗位信息
     */
    @OperLog(title = "岗位信息", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody QuotePostInfo quotePostInfo) {
        try {
            if (null == quotePostInfo) {
                return R.error("新增内容不能为空！");
            }
            if (quotePostInfoService.insertQuotePostInfo(quotePostInfo) > 0) {
                return R.ok("新增岗位信息成功.");
            }
            return R.error("新增岗位信息失败");
        } catch (Exception e) {
            logger.error("新增岗位信息失败,异常信息：" + e);
            return R.error("新增岗位信息失败！");
        }
    }

    /**
     * 新增岗位关联危害因素
     */
    @OperLog(title = "岗位信息", businessType = BusinessType.INSERT)
    @PostMapping("/addPostWithHarmFactor")
    public R addPostWithHarmFactor(@RequestBody PostHarmFactorDTO postHarmFactorDTO) {
        try {
            if (null == postHarmFactorDTO) {
                return R.error("新增内容不能为空！");
            }
            if (quotePostInfoService.insertBatch(postHarmFactorDTO)) {
                return R.ok("新增岗位信息成功");
            }
            return R.error("新增岗位信息失败！");
        } catch (Exception e) {
            logger.error("新增岗位信息失败,异常信息：" + e);
            return R.error("新增岗位信息失败," + e.getMessage());
        }
    }

    /**
     * 修改岗位信息
     */
    @OperLog(title = "岗位信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody QuotePostInfo quotePostInfo) {
        try {
            if (null == quotePostInfo) {
                return R.error("修改内容不能为空！");
            }
            if (null == quotePostInfo.getId()) {
                return R.error("id不能为空！");
            }
            if (quotePostInfoService.updateQuotePostInfo(quotePostInfo) > 0) {
                return R.ok("修改岗位信息成功.");
            }
            return R.error("修改岗位信息失败！");
        } catch (Exception e) {
            logger.error("修改岗位信息失败,异常信息：" + e);
            return R.error("修改岗位信息失败！");
        }
    }

    /**
     * 修改岗位信息
     */
    @OperLog(title = "岗位信息", businessType = BusinessType.UPDATE)
    @PutMapping("/editPostWithHarmFactor")
    public R editPostWithHarmFactor(@RequestBody PostHarmFactorDTO postHarmFactorDTO) {
        try {
            if (null == postHarmFactorDTO) {
                return R.error("修改内容不能为空！");
            }
            QuotePostInfo quotePostInfo = postHarmFactorDTO.getQuotePostInfo();
            if (null == quotePostInfo) {
                return R.error("岗位信息不能为空！");
            }
            if (null == quotePostInfo.getId()) {
                return R.error("岗位id不能为空！");
            }
            if (quotePostInfoService.updateQuotePostInfoWithHarmFactor(postHarmFactorDTO) > 0) {
                return R.ok("修改岗位信息成功.");
            }
            return R.error("修改岗位信息失败！");
        } catch (Exception e) {
            logger.error("修改岗位信息失败,异常信息：" + e);
            return R.error("修改岗位信息失败！");
        }
    }

    /**
     * 删除岗位信息
     */
    @OperLog(title = "岗位信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("id不能为空！");
            }
            if (quotePostInfoService.deleteQuotePostInfoByIds(ids) > 0) {
                return R.ok("删除岗位信息成功.");
            }
            return R.error("删除岗位信息失败！");
        } catch (Exception e) {
            logger.error("删除岗位信息失败,异常信息：" + e);
            return R.error("删除岗位信息失败！");
        }
    }

    /**
     * 查询岗位信息列表(自定义)
     */
    @GetMapping("/getPostInfo")
    public R getPostInfo(QuotePostInfoDTO quotePostInfoDTO) {
        try (Page<Object> page = startPageWithTotal()) {
            if (null == quotePostInfoDTO || null == quotePostInfoDTO.getSheetId()) {
                return R.error("表单id不能为空！");
            }
            QuotePostInfo quotePostInfo = quotePostInfoDTO.getQuotePostInfo();
            List<String> postNamelist = quoteSheetItemsService.findPostNameBySheetId(quotePostInfoDTO.getSheetId(), quotePostInfoDTO.getSubId());
            if (null != postNamelist && postNamelist.size() > 0) {
                String postName = QuoteUtil.getQueryFiled(postNamelist);
                if (null == quotePostInfo) {
                    quotePostInfo = new QuotePostInfo();
                }
                quotePostInfo.setPostName(postName);
            }
            List<QuotePostInfo> list = quotePostInfoService.findQuotePostInfoUserList(quotePostInfo);
            return R.data(list, page.getTotal());
        } catch (Exception e) {
            logger.error("查询岗位信息列表失败,异常信息：" + e);
            return R.error("查询岗位信息列表失败！");
        }
    }
}
