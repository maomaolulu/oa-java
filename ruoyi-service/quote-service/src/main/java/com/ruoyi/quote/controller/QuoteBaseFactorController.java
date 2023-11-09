package com.ruoyi.quote.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.quote.domain.dto.QuoteBaseFactorFilterDTO;
import com.ruoyi.quote.domain.dto.QuoteRelationFactorDTO;
import com.ruoyi.quote.domain.entity.QuoteBaseFactor;
import com.ruoyi.quote.domain.vo.QuoteBaseFactorVO;
import com.ruoyi.quote.service.IQuoteTestItemService;
import com.ruoyi.quote.service.IQuoteTestPollutantService;
import com.ruoyi.quote.utils.QuoteUtil;
import com.ruoyi.system.util.SystemUtil;
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
import com.ruoyi.quote.service.IQuoteBaseFactorService;

/**
 * 危害因素基础信息Controller
 *
 * @author yrb
 * @date 2022-05-06
 */
@RestController
@RequestMapping("/quote/baseFactor")
public class QuoteBaseFactorController extends BaseController {
    private final IQuoteBaseFactorService quoteBaseFactorService;
    private final IQuoteTestPollutantService quoteTestPollutantService;
    private final RedisUtils redisUtils;

    @Autowired
    public QuoteBaseFactorController(IQuoteBaseFactorService quoteBaseFactorService,
                                     IQuoteTestPollutantService quoteTestPollutantService,
                                     RedisUtils redisUtils) {
        this.quoteBaseFactorService = quoteBaseFactorService;
        this.quoteTestPollutantService = quoteTestPollutantService;
        this.redisUtils = redisUtils;
    }

    /**
     * 查询危害因素基础信息列表
     */
    @GetMapping("/list")
    public R list(QuoteBaseFactorVO quoteBaseFactorVO) {
        try {
            Page page = startPageWithTotal();
            List<QuoteBaseFactorVO> list = quoteBaseFactorService.selectQuoteBaseFactorList(quoteBaseFactorVO);
            return R.data(list, page.getTotal());
        } catch (Exception e) {
            logger.error("查询危害因素基础信息列表失败，异常信息：" + e);
            return R.error("查询危害因素基础信息列表失败!");
        }
    }

    /**
     * 查询危害因素基础信息列表
     */
    @GetMapping("/userList")
    public R userList(QuoteBaseFactorVO quoteBaseFactorVO) {
        try {
            Page page = startPageWithTotal();
            List<QuoteBaseFactorVO> list = quoteBaseFactorService.selectQuoteBaseFactorUserList(quoteBaseFactorVO);
            return R.data(list, page.getTotal());
        } catch (Exception e) {
            logger.error("查询危害因素基础信息列表失败，异常信息：" + e);
            return R.error("查询危害因素基础信息列表失败");
        }
    }

    /**
     * 获取危害因素基础信息详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            return R.data(quoteBaseFactorService.selectQuoteBaseFactorById(id));
        } catch (Exception e) {
            logger.error("获取危害因素基础信息详细信息失败，异常信息：" + e);
            return R.error("获取危害因素基础信息详细信息失败！");
        }
    }

    /**
     * 新增危害因素基础信息
     */
    @OperLog(title = "危害因素基础信息", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody QuoteBaseFactor quoteBaseFactor) {
        try {
            if (StrUtil.isBlank(quoteBaseFactor.getFactorName())) {
                return R.error("污染物名称不能为空！");
            }
            if (quoteBaseFactor.getCategoryId() == null) {
                return R.error("大类id不能为空！");
            }
            if (quoteBaseFactor.getSubcategoryId() == null) {
                return R.error("子类id不能为空！");
            }
            if (quoteBaseFactor.getPrice() == null) {
                return R.error("价格不能为空！");
            }
            if (quoteBaseFactorService.insertQuoteBaseFactor(quoteBaseFactor) > 0) {
                return R.ok("新增危害因素基础信息成功.");
            }
            return R.error("新增危害因素基础信息失败!");
        } catch (Exception e) {
            logger.error("新增危害因素基础信息失败，异常信息：" + e);
            return R.error("新增危害因素基础信息失败," + e.getMessage());
        }
    }

    /**
     * 修改危害因素基础信息
     */
    @OperLog(title = "危害因素基础信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody QuoteBaseFactor quoteBaseFactor) {
        try {
            if (null == quoteBaseFactor) {
                return R.error("修改内容不能为空！");
            }
            if (null == quoteBaseFactor.getId()) {
                return R.error("id不能为空！");
            }
            if (quoteBaseFactorService.updateQuoteBaseFactor(quoteBaseFactor) > 0) {
                return R.ok("修改危害因素基础信息成功.");
            }
            return R.error("修改危害因素基础信息失败！");
        } catch (Exception e) {
            logger.error("修改危害因素基础信息失败，异常信息：" + e);
            return R.error("修改危害因素基础信息失败," + e.getMessage());
        }
    }

    /**
     * 删除危害因素基础信息
     */
    @OperLog(title = "危害因素基础信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("要删除的危害因素基础信息id不能为空！");
            }
            if (quoteBaseFactorService.deleteQuoteBaseFactorByIds(ids) > 0) {
                return R.ok("删除危害因素基础信息成功.");
            }
            return R.error("删除危害因素基础信息失败！");
        } catch (Exception e) {
            logger.error("删除危害因素基础信息失败，异常信息：" + e);
            return R.error("删除危害因素基础信息失败!");
        }
    }

    /**
     * 删除危害因素基础信息(先删除关联信息)
     */
    @OperLog(title = "危害因素基础信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/delete/{ids}")
    public R delete(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("要删除的危害因素基础信息id不能为空！");
            }
            if (quoteBaseFactorService.deleteBaseFactorByIds(ids)) {
                return R.ok("删除危害因素基础信息成功.");
            }
            return R.error("删除危害因素基础信息失败！");
        } catch (Exception e) {
            logger.error("删除危害因素基础信息失败，异常信息：" + e);
            return R.error("删除危害因素基础信息失败!");
        }
    }

    /**
     * 查询危害因素基础信息列表(环境--过滤已关联的)
     */
    @GetMapping("/getQuoteBaseFactorFilterList")
    public R getQuoteBaseFactorFilterList(QuoteBaseFactorFilterDTO quoteBaseFactorFilterDTO) {
        try {
            Long masterCategoryId = quoteBaseFactorFilterDTO.getMasterCategoryId();
            Long subCategoryId = quoteBaseFactorFilterDTO.getSubCategoryId();
            Long pollutantTypeId = quoteBaseFactorFilterDTO.getPollutantTypeId();
            Long categoryId = quoteBaseFactorFilterDTO.getCategoryId();
            if (null == masterCategoryId) {
                return R.error("行业大类id不能为空！");
            }
            if (null == subCategoryId) {
                return R.error("行业子类id不能为空！");
            }
            if (null == pollutantTypeId) {
                return R.error("检测类别id不能为空！");
            }
            if (null == categoryId) {
                return R.error("污染物大类id不能为空！");
            }

            //获取已关联的污染物id
            Map<String, Object> map = new HashMap<>();
            map.put("masterCategoryId", masterCategoryId);
            map.put("subCategoryId", subCategoryId);
            map.put("pollutantTypeId", pollutantTypeId);
            List<Long> list = quoteBaseFactorService.findRelationPollutantId(map);
            QuoteBaseFactorVO quoteBaseFactorVO = new QuoteBaseFactorVO();
            String factorName = quoteBaseFactorFilterDTO.getFactorName();
            if (StrUtil.isNotBlank(factorName)) {
                quoteBaseFactorVO.setFactorName(factorName);
            }
            quoteBaseFactorVO.setList(list);

            pageUtil();
            List<QuoteBaseFactorVO> quoteBaseFactorFilterList = quoteBaseFactorService.findQuoteBaseFactorFilterList(quoteBaseFactorVO);
            return resultData(quoteBaseFactorFilterList);
        } catch (Exception e) {
            logger.error("获取污染物信息失败，异常信息：" + e);
            return R.error("获取污染物信息失败！");
        }
    }

    /**
     * 查询危害因素基础信息列表(公卫--编辑检测类别--编辑关联危害因素时--过滤已关联的)
     */
    @GetMapping("/getQuoteBaseFactorRelationList")
    public R getQuoteBaseFactorRelationList(QuoteBaseFactorVO quoteBaseFactorVO) {
        try {
            Long id = quoteBaseFactorVO.getId();
            if (null == id) {
                return R.error("id不能为空");
            }
            quoteBaseFactorVO.setList(quoteTestPollutantService.findPollutantIdListById(id));
            pageUtil();
            List<QuoteBaseFactor> list = quoteBaseFactorService.findQuoteBaseFactorFilterRelationList(quoteBaseFactorVO);
            return resultData(list);
        } catch (Exception e) {
            logger.error("获取污染物信息失败，异常信息：" + e);
            return R.error("获取污染物信息失败！");
        }
    }

    /**
     * 查询危害因素基础信息列表(公卫--过滤已报价的污染物)
     */
    @GetMapping("/getQuoteRelationFactorList")
    public R getQuoteRelationFactorList(QuoteRelationFactorDTO quoteRelationFactorDTO) {
        try {
            String sheetId = quoteRelationFactorDTO.getSheetId();
            Long subId = quoteRelationFactorDTO.getSubId();
            Long pollutantTypeId = quoteRelationFactorDTO.getPollutantTypeId();
            Long masterCategoryId = quoteRelationFactorDTO.getMasterCategoryId();
            if (StrUtil.isBlank(sheetId)) {
                return R.error("表单id不能为空！");
            }
            if (subId == null) {
                return R.error("子类id不能为空！");
            }
            if (pollutantTypeId == null) {
                return R.error("检测类别id不能为空！");
            }
            if (masterCategoryId == null) {
                return R.error("大类id不能为空！");
            }
            // 当前子类已报价的污染物id集合
//            List<Long> list = quoteTestItemService.findPollutantIdListBySub(quoteRelationFactorDTO);

            // 检测类别关联主键id
            Long id = quoteBaseFactorService.findQuoteRelationTypeId(quoteRelationFactorDTO);
            QuoteBaseFactorVO quoteBaseFactorVO = new QuoteBaseFactorVO();
            quoteBaseFactorVO.setId(id);
            String idsKey = QuoteUtil.getGwTestItemsIdsKey(sheetId, subId, pollutantTypeId, SystemUtil.getUserId());
            String idsValue = redisUtils.get(idsKey);
            if (StrUtil.isNotBlank(idsValue)) {
                quoteBaseFactorVO.setList(QuoteUtil.getList(idsValue));
            }
            pageUtil();
            List<QuoteBaseFactor> pollutantTypeRelationFactorList = quoteBaseFactorService.findPollutantTypeRelationFactorList(quoteBaseFactorVO);
            return resultData(pollutantTypeRelationFactorList);
        } catch (Exception e) {
            logger.error("获取污染物信息失败，异常信息：" + e);
            return R.error("获取污染物信息失败," + e.getMessage());
        }
    }
}
