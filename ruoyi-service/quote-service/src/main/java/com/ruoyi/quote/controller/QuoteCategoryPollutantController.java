package com.ruoyi.quote.controller;

import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.quote.domain.dto.QuoteCategoryItemDTO;
import com.ruoyi.quote.domain.dto.QuoteCategoryPollutantDTO;
import com.ruoyi.quote.domain.dto.QuotePollutantTypeDTO;
import com.ruoyi.quote.domain.entity.QuoteBaseFactor;
import com.ruoyi.quote.domain.entity.QuoteCategoryPollutant;
import com.ruoyi.quote.domain.entity.QuotePollutantType;
import com.ruoyi.quote.domain.vo.QuoteIndustryInfoVO;
import com.ruoyi.quote.domain.vo.QuotePollutantTypeVO;
import com.ruoyi.quote.domain.vo.QuoteTestTypeVO;
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
import com.ruoyi.quote.service.IQuoteCategoryPollutantService;

/**
 * 行业类别污染物类别关联Controller
 *
 * @author yrb
 * @date 2022-06-15
 */
@RestController
@RequestMapping("/quote/pollutant")
public class QuoteCategoryPollutantController extends BaseController {
    private final IQuoteCategoryPollutantService quoteCategoryPollutantService;

    @Autowired
    public QuoteCategoryPollutantController(IQuoteCategoryPollutantService quoteCategoryPollutantService) {
        this.quoteCategoryPollutantService = quoteCategoryPollutantService;
    }

    /**
     * 查询行业类别污染物类别关联列表
     */
    @GetMapping("/list")
    public R list(QuoteCategoryPollutant quoteCategoryPollutant) {
        try {
            pageUtil();
            List<QuoteCategoryPollutant> list = quoteCategoryPollutantService.selectQuoteCategoryPollutantList(quoteCategoryPollutant);
            return resultData(list);
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息：" + e);
            return R.error("信息获取失败!");
        }
    }

    /**
     * 获取行业类别污染物类别关联详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            return R.data(quoteCategoryPollutantService.selectQuoteCategoryPollutantById(id));
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息：" + e);
            return R.error("信息获取失败!");
        }
    }

    /**
     * 新增行业类别污染物类别关联
     */
    @OperLog(title = "行业类别污染物类别关联", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody QuoteCategoryPollutant quoteCategoryPollutant) {
        try {
            if (null == quoteCategoryPollutant.getPollutantId()) {
                return R.error("污染物id为空，请检查！");
            }
            if (null == quoteCategoryPollutant.getMasterCategoryId()) {
                return R.error("行业大类id为空，请检查！");
            }
            if (null == quoteCategoryPollutant.getSubCategoryId()) {
                return R.error("行业子类id为空，请检查！");
            }
            if (null == quoteCategoryPollutant.getPollutantTypeId()) {
                return R.error("污染物类别id为空，请检查！");
            }
            if (quoteCategoryPollutantService.insertQuoteCategoryPollutant(quoteCategoryPollutant) > 0) {
                return R.ok("新增成功！");
            }
            return R.error("新增失败！");
        } catch (Exception e) {
            logger.error("新增失败，异常信息：" + e);
            return R.error("新增失败!");
        }
    }

    /**
     * 修改行业类别污染物类别关联
     */
    @OperLog(title = "行业类别污染物类别关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody QuoteCategoryPollutant quoteCategoryPollutant) {
        try {
            if (null == quoteCategoryPollutant.getId()) {
                return R.error("id不能为空！");
            }
            if (quoteCategoryPollutantService.updateQuoteCategoryPollutant(quoteCategoryPollutant) > 0) {
                return R.ok("编辑成功!");
            }
            return R.error("编辑失败！");
        } catch (Exception e) {
            logger.error("编辑失败，异常信息：" + e);
            return R.error("编辑失败!");
        }
    }

    /**
     * 删除行业类别污染物类别关联
     */
    @OperLog(title = "行业类别污染物类别关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("id不能为空！");
            }
            if (quoteCategoryPollutantService.deleteQuoteCategoryPollutantByIds(ids) > 0) {
                return R.ok("删除成功！");
            }
            return R.error("删除失败！");
        } catch (Exception e) {
            logger.error("删除失败，异常信息：" + e);
            return R.error("删除失败!");
        }
    }

    /**
     * 根据主类id和子类id获取检测类别（不同点位取值相同）(环境)
     */
    @GetMapping("/getRelationPollutantType")
    public R getRelationPollutantType(QuoteCategoryPollutant quoteCategoryPollutant) {
        try {
            List<QuotePollutantType> list = quoteCategoryPollutantService.selectRelationPollutantType(quoteCategoryPollutant);
            return R.data(list);
        } catch (Exception e) {
            logger.error("检测类型信息获取失败，异常信息：" + e);
            return R.error("检测类型信息获取失败!");
        }
    }

    /**
     * 根据主类id、子类id、检测类型id 获取污染物信息
     */
    @PostMapping("/getPollutantInfo")
    public R getPollutantInfo(@RequestBody QuoteCategoryItemDTO quoteCategoryItemDTO) {
        try {
            List<QuoteBaseFactor> list = quoteCategoryPollutantService.selectRelationPollutant(quoteCategoryItemDTO);
            return R.data(list);
        } catch (Exception e) {
            logger.error("污染物信息获取失败，异常信息：" + e);
            return R.error("污染物信息获取失败!");
        }
    }

    /**
     * 通过行业大类、行业子类获取检测类型
     */
    @GetMapping("/getTestType")
    public R getTestType(QuoteIndustryInfoVO quoteIndustryInfoVO) {
        try {
            pageUtil();
            List<QuoteTestTypeVO> testTypeList = quoteCategoryPollutantService.findTestTypeList(quoteIndustryInfoVO);
            return resultData(testTypeList);
        } catch (Exception e) {
            logger.error("获取检测类别列表失败，异常信息：" + e);
            return R.error("获取检测类别列表失败!");
        }
    }

    /**
     * 通过行业大类id、行业子类id、检测类型id获取污染物
     */
    @GetMapping("/getRelationPollutantByTypes")
    public R getRelationPollutantByTypes(QuoteCategoryPollutant quoteCategoryPollutant) {
        try {
            if (null == quoteCategoryPollutant.getMasterCategoryId()) {
                return R.error("行业大类不能为空！");
            }
            if (null == quoteCategoryPollutant.getSubCategoryId()) {
                return R.error("行业子类不能为空！");
            }
            if (null == quoteCategoryPollutant.getPollutantTypeId()) {
                return R.error("污染物类别id不能为空！");
            }
            pageUtil();
            List<QuoteBaseFactor> list = quoteCategoryPollutantService.findRelationPollutantByTypes(quoteCategoryPollutant);
            return resultData(list);
        } catch (Exception e) {
            logger.error("获取污染物信息失败，异常信息：" + e);
            return R.error("获取污染物信息失败!");
        }
    }

    /**
     * 通过行业大类id、行业子类id、检测类型id关联污染物
     */
    @OperLog(title = "行业类别污染物类别关联", businessType = BusinessType.UPDATE)
    @PutMapping("/addReation")
    public R addRelation(@RequestBody QuoteCategoryItemDTO quoteCategoryItemDTO) {
        try {
            QuoteCategoryPollutant quoteCategoryPollutant = quoteCategoryItemDTO.getQuoteCategoryPollutant();
            if (null == quoteCategoryPollutant) {
                return R.error("类别id不能为空！");
            }
            if (null == quoteCategoryPollutant.getMasterCategoryId()) {
                return R.error("行业大类id不能为空！");
            }
            if (null == quoteCategoryPollutant.getSubCategoryId()) {
                return R.error("行业子类id不能为空！");
            }
            if (null == quoteCategoryPollutant.getPollutantTypeId()) {
                return R.error("检测类别id不能为空！");
            }
            List<Long> ids = quoteCategoryItemDTO.getIds();
            if (null == ids || ids.size() == 0) {
                return R.error("污染物id不能为空！");
            }
            if (quoteCategoryPollutantService.addRelation(quoteCategoryPollutant, ids)) {
                return R.ok("污染物添加关联成功！");
            }
            return R.error("污染物添加关联失败!");
        } catch (Exception e) {
            logger.error("污染物添加关联失败，异常信息：" + e);
            return R.error("污染物添加关联失败," + e.getMessage());
        }
    }

    /**
     * 删除关联污染物（环境-单个删除-非主键id删除）
     */
    @OperLog(title = "删除关联污染物", businessType = BusinessType.INSERT)
    @PostMapping("/deletePollutant")
    public R deletePollutant(@RequestBody QuoteCategoryPollutant quoteCategoryPollutant) {
        try {
            if (null == quoteCategoryPollutant.getMasterCategoryId()) {
                return R.error("行业大类id不能为空！");
            }
            if (null == quoteCategoryPollutant.getSubCategoryId()) {
                return R.error("行业子类id不能为空！");
            }
            if (null == quoteCategoryPollutant.getPollutantTypeId()) {
                return R.error("检测类别id不能为空！");
            }
            if (null == quoteCategoryPollutant.getPollutantId()) {
                return R.error("污染物id不能为空！");
            }
            if (quoteCategoryPollutantService.deleteRelationPollutant(quoteCategoryPollutant)) {
                return R.ok("关联污染物信息删除成功！");
            }
            return R.error("关联污染物信息删除失败！");
        } catch (Exception e) {
            logger.error("关联污染物信息删除失败，异常信息：" + e);
            return R.error("关联污染物信息删除失败！");
        }
    }

    /**
     * 删除、批量删除检测类别(环境-删除检测类别)
     */
    @OperLog(title = "删除、批量删除检测类别", businessType = BusinessType.DELETE)
    @PostMapping("/deleteTestType")
    public R deleteTestType(@RequestBody QuoteCategoryPollutantDTO quoteCategoryPollutantDTO) {
        try {
            List<QuoteCategoryPollutant> list = quoteCategoryPollutantDTO.getQuoteCategoryPollutantList();
            if (null == list || list.size() == 0) {
                return R.error("检测类别信息不能为空！");
            }
            if (quoteCategoryPollutantService.deleteTestType(list)) {
                return R.ok("关联污染物信息删除成功！");
            }
            return R.error("关联污染物信息删除失败！");
        } catch (Exception e) {
            logger.error("关联污染物信息删除失败，异常信息：" + e);
            return R.error("关联污染物信息删除失败," + e.getMessage());
        }
    }

    /**
     * 查询检测类别列表  （公卫--web端参数配置）
     */
    @GetMapping("/getPollutantType")
    public R getPollutantType(QuotePollutantTypeVO quotePollutantTypeVO) {
        try {
            pageUtil();
            List<QuotePollutantTypeVO> list = quoteCategoryPollutantService.findPollutantTypeList(quotePollutantTypeVO);
            return resultData(list);
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息：" + e);
            return R.error("信息获取失败!");
        }
    }

    /**
     * 编辑检测类型(公卫--参数设置)
     */
    @PostMapping("/editPollutantType")
    public R editPollutantType(@RequestBody QuotePollutantTypeDTO quotePollutantType) {
        try {
            if (null == quotePollutantType.getId()) {
                return R.error("关联主键id不能为空！");
            }
            if (StrUtil.isBlank(quotePollutantType.getPollutantName())) {
                return R.error("新的检测类型名称不能为空！");
            }
            if (quoteCategoryPollutantService.editPollutantType(quotePollutantType)) {
                return R.ok("编辑成功！");
            }
            return R.error("编辑失败！");
        } catch (Exception e) {
            logger.error("编辑失败，异常信息：" + e);
            return R.error("编辑失败," + e.getMessage());
        }
    }

    /**
     * 删除行业类别污染物类别关联
     */
    @OperLog(title = "行业类别污染物类别关联", businessType = BusinessType.DELETE)
    @DeleteMapping("delete/{ids}")
    public R delete(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("id不能为空！");
            }
            if (quoteCategoryPollutantService.deleteQuoteCategoryPollutantByIds(ids) > 0) {
                return R.ok("删除成功！");
            }
            return R.error("删除失败！");
        } catch (Exception e) {
            logger.error("删除失败，异常信息：" + e);
            return R.error("删除失败!");
        }
    }

    /**
     * 批量删除行业类别污染物类别关联(关联删除关联的污染物) （公卫--删除、批量删除检测列表）
     */
    @OperLog(title = "行业类别污染物类别关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/deleteCategoryPollutant/{ids}")
    public R deleteCategoryPollutant(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("id不能为空！");
            }
            if (quoteCategoryPollutantService.deleteCategoryPollutantByIds(ids)) {
                return R.ok("删除成功！");
            }
            return R.error("删除失败！");
        } catch (Exception e) {
            logger.error("删除失败，异常信息：" + e);
            return R.error("删除失败," + e.getMessage());
        }
    }

    /**
     * 公卫-获取检测类别-根据大类id和检测性质id
     */
    @GetMapping("/getRelationPollutantTypeGw")
    public R getRelationPollutantTypeGw(QuoteCategoryPollutant quoteCategoryPollutant) {
        try {
            if (quoteCategoryPollutant.getMasterCategoryId() == null) {
                return R.error("大类id不能为空");
            }
            // 用户所选的检测性质id通过natureIds传过来
            if (StrUtil.isBlank(quoteCategoryPollutant.getNatureIds())) {
                return R.error("检测性质id不能为空");
            }
            return resultData(quoteCategoryPollutantService.findRelationPollutantTypeGw(quoteCategoryPollutant));
        } catch (Exception e) {
            logger.error("获取检测类型发生异常，异常信息：" + e);
            return R.error("获取检测类型发生异常");
        }
    }
}
