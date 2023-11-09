package com.ruoyi.quote.controller;

import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.quote.domain.dto.QuotePollutantTypeAddDTO;
import com.ruoyi.quote.domain.dto.QuotePollutantTypeDTO;
import com.ruoyi.quote.domain.dto.QuoteTestPollutantDTO;
import com.ruoyi.quote.domain.entity.QuoteCategoryPollutant;
import com.ruoyi.quote.domain.entity.QuotePollutantType;
import com.ruoyi.quote.domain.vo.QuoteTestItemVO;
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
import com.ruoyi.quote.service.IQuotePollutantTypeService;

/**
 * 污染物类别Controller
 *
 * @author yrb
 * @date 2022-06-15
 */
@RestController
@RequestMapping("/quote/type")
public class QuotePollutantTypeController extends BaseController {
    private final IQuotePollutantTypeService quotePollutantTypeService;
    private final RedisUtils redisUtils;

    @Autowired
    public QuotePollutantTypeController(IQuotePollutantTypeService quotePollutantTypeService,
                                        RedisUtils redisUtils) {
        this.quotePollutantTypeService = quotePollutantTypeService;
        this.redisUtils = redisUtils;
    }

    /**
     * 查询污染物类别列表
     */
    @GetMapping("/list")
    public R list(QuotePollutantType quotePollutantType) {
        try {
            pageUtil();
            List<QuotePollutantType> list = quotePollutantTypeService.selectQuotePollutantTypeList(quotePollutantType);
            return resultData(list);
        } catch (Exception e) {
            logger.error("获取污染物类别信息失败，异常信息：" + e);
            return R.error("获取污染物类别信息失败！");
        }
    }

    /**
     * 获取污染物类别详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            return R.data(quotePollutantTypeService.selectQuotePollutantTypeById(id));
        } catch (Exception e) {
            logger.error("获取污染物类别信息失败，异常信息：" + e);
            return R.error("获取污染物类别信息失败！");
        }
    }

    /**
     * 新增污染物类别
     */
    @OperLog(title = "污染物类别", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody QuotePollutantType quotePollutantType) {
        try {
            if (StrUtil.isBlank(quotePollutantType.getPollutantName())) {
                return R.error("污染物名称为空，请检查！");
            }
            if (null == quotePollutantType.getProjectId()) {
                return R.error("项目所属id不能为空！");
            }
            if (quotePollutantTypeService.insertQuotePollutantType(quotePollutantType) > 0) {
                return R.ok("污染物类别信息添加成功！");
            }
            return R.error("污染物类别信息添加失败！");
        } catch (Exception e) {
            logger.error("污染物类别信息添加失败，异常信息：" + e);
            return R.error("污染物类别信息添加失败！");
        }
    }

    /**
     * 修改污染物类别
     */
    @OperLog(title = "污染物类别", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody QuotePollutantType quotePollutantType) {
        try {
            if (null == quotePollutantType.getId()) {
                return R.error("污染物类别信息id为空，请检查！");
            }
            if (quotePollutantTypeService.updateQuotePollutantType(quotePollutantType) > 0) {
                return R.ok("污染物类别信息编辑成功！");
            }
            return R.error("污染物类别信息编辑失败！");
        } catch (Exception e) {
            logger.error("污染物类别信息编辑失败，异常信息：" + e);
            return R.error("污染物类别信息编辑失败！");
        }
    }

    /**
     * 删除污染物类别
     */
    @OperLog(title = "污染物类别", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("id不能为空！");
            }
            if (quotePollutantTypeService.deleteQuotePollutantTypeByIds(ids) > 0) {
                return R.ok("删除污染物类别信息成功.");
            }
            return R.error("删除污染物类别信息失败！");
        } catch (Exception e) {
            logger.error("删除污染物类别信息失败，异常信息：" + e);
            return R.error("删除污染物类别信息失败！");
        }
    }

    /**
     * 新增检测类别 （公卫-web端参数设置）
     */
    @OperLog(title = "新增检测类别", businessType = BusinessType.UPDATE)
    @PutMapping("/addPollutantType")
    public R addPollutantType(@RequestBody QuotePollutantTypeAddDTO quotePollutantTypeAddDTO) {
        try {
            if (StrUtil.isBlank(quotePollutantTypeAddDTO.getPollutantName())) {
                return R.error("检测类别名称不能为空！");
            }
            if (null == quotePollutantTypeAddDTO.getIndustryId()) {
                return R.error("行业id不能为空！");
            }
            if (null == quotePollutantTypeAddDTO.getProjectId()) {
                return R.error("项目id不能为空！");
            }
            if (quotePollutantTypeService.addPollutantType(quotePollutantTypeAddDTO)) {
                return R.ok("新增成功！");
            }
            return R.error("新增失败！");
        } catch (Exception e) {
            logger.error("新增失败！");
            return R.error("新增失败," + e.getMessage());
        }
    }

    @OperLog(title = "污染物关联检测类别、行业类别", businessType = BusinessType.INSERT)
    @PostMapping("/addRelationCaregoryAndPollutant")
    public R addRelationCaregoryAndPollutant(@RequestBody QuoteTestPollutantDTO quoteTestPollutantDTO) {
        try {
            Long id = quoteTestPollutantDTO.getId();
            if (null == id) {
                return R.error("关联表主键id不能为空！");
            }
            List<Long> ids = quoteTestPollutantDTO.getIds();
            if (null == ids || ids.size() == 0) {
                return R.error("污染物id不能为空！");
            }
            if (quotePollutantTypeService.addRelationCaregoryAndPollutant(id, ids)) {
                return R.ok("添加成功！");
            }
            return R.error("添加失败！");
        } catch (Exception e) {
            logger.error("添加失败，异常信息：" + e);
            return R.error("添加失败！");
        }
    }

    /**
     * 查询污染物类别列表（公卫）
     * （A点位使用检测列表中的类别1，B点位则要去除检测类别中的类别1）
     */
    @GetMapping("/getPollutantTypeList")
    public R getPollutantTypeList(QuoteTestItemVO quoteTestItemVO) {
        try {
            // 获取子类已报价列表
            //List<Long> list = quoteTestItemService.findPollutantTypeIdListBySub(quoteTestItemVO);
            QuotePollutantTypeDTO quotePollutantTypeDTO = new QuotePollutantTypeDTO();
            quotePollutantTypeDTO.setMasterCategoryId(quoteTestItemVO.getMasterCategoryId());
            String typeKey = QuoteUtil.getGwPollutantTypeKey(quoteTestItemVO.getSubId(), SystemUtil.getUserId(), quoteTestItemVO.getSheetId());
            String ids = redisUtils.get(typeKey);
            if (StrUtil.isNotBlank(ids)) {
                List<Long> list = QuoteUtil.getList(ids);
                quotePollutantTypeDTO.setList(list);
            }
            pageUtil();
            List<QuotePollutantType> quotePollutantTypeList = quotePollutantTypeService.findQuotePollutantTypeFilterList(quotePollutantTypeDTO);
            return resultData(quotePollutantTypeList);
        } catch (Exception e) {
            logger.error("获取污染物类别信息失败，异常信息：" + e);
            return R.error("获取污染物类别信息失败," + e.getMessage());
        }
    }
}
