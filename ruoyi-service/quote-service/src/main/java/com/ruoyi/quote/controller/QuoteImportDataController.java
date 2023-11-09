package com.ruoyi.quote.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.quote.domain.dto.RelationInfoGwDTO;
import com.ruoyi.quote.domain.dto.RelationInfoHjDTO;
import com.ruoyi.quote.domain.vo.QuoteBaseFactorVO;
import com.ruoyi.quote.mapper.QuoteIndustryInfoMapper;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.quote.domain.dto.RelationInfoZwDTO;
import com.ruoyi.quote.domain.entity.*;
import com.ruoyi.quote.service.*;
import com.ruoyi.quote.utils.QuoteUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @Author yrb
 * @Date 2022/5/20 14:39
 * @Version 1.0
 * @Description 数据导入
 */
@RestController
@RequestMapping("/quote/import")
public class QuoteImportDataController extends BaseController {

    private final IQuoteChargeCategoryService quoteChargeCategoryService;
    private final IQuoteBaseFactorService quoteBaseFactorService;
    private final IQuoteIndustryInfoService quoteIndustryInfoService;
    private final IQuotePostInfoService quotePostInfoService;
    private final IQuoteHarmFactorService quoteHarmFactorService;
    private final QuoteIndustryInfoMapper quoteIndustryInfoMapper;
    private final IQuotePollutantTypeService quotePollutantTypeService;
    private final IQuoteCategoryPollutantService quoteCategoryPollutantService;
    private final IQuoteTestPollutantService quoteTestPollutantService;

    public QuoteImportDataController(IQuoteChargeCategoryService quoteChargeCategoryService,
                                     IQuoteBaseFactorService quoteBaseFactorService,
                                     IQuoteIndustryInfoService quoteIndustryInfoService,
                                     IQuotePostInfoService quotePostInfoService,
                                     IQuoteHarmFactorService quoteHarmFactorService,
                                     QuoteIndustryInfoMapper quoteIndustryInfoMapper,
                                     IQuotePollutantTypeService quotePollutantTypeService,
                                     IQuoteCategoryPollutantService quoteCategoryPollutantService,
                                     IQuoteTestPollutantService quoteTestPollutantService) {
        this.quoteChargeCategoryService = quoteChargeCategoryService;
        this.quoteBaseFactorService = quoteBaseFactorService;
        this.quoteIndustryInfoService = quoteIndustryInfoService;
        this.quotePostInfoService = quotePostInfoService;
        this.quoteHarmFactorService = quoteHarmFactorService;
        this.quoteIndustryInfoMapper = quoteIndustryInfoMapper;
        this.quotePollutantTypeService = quotePollutantTypeService;
        this.quoteCategoryPollutantService = quoteCategoryPollutantService;
        this.quoteTestPollutantService = quoteTestPollutantService;
    }

    /**
     * 职卫 （危害因素基础信息导入）
     */
    @GetMapping("/baseFactor/zw")
    public R baseFactorZw() {
        try {
            String path = "C:\\Users\\MI\\Desktop\\数据导入\\【职业卫生】对应关系表-李岫文0519.xlsx";
            List<QuoteBaseFactor> quoteBaseFactorList = QuoteUtil.importBaseFactorZw(path);
            if (null != quoteBaseFactorList || quoteBaseFactorList.size() > 0) {
                if (quoteBaseFactorService.insertQuoteBaseFactorBatch(quoteBaseFactorList) == 0) {
                    throw new RuntimeException("插入数据失败！");
                }
            }
            return R.ok("数据插入成功.");
        } catch (Exception e) {
            logger.error("数据导入失败,异常信息：" + e);
            return R.error("数据导入失败！");
        }
    }

    /**
     * 环境 （污染物基础信息导入）
     */
    @GetMapping("/baseFactor/hj")
    public R baseFactorHj() {
        try {
            String path = "C:\\Users\\MI\\Desktop\\环境\\【收费标准】\\收费标准数据库2022.4.21\\环保检测收费标准2022.4.21.xlsx";
            Map<String, Long> map = new HashMap<>();
            map.put("环境空气", 1004L);
            map.put("废气（有组织、无组织）", 1010L);
            map.put("水质（废水、地下水、地表水）", 1011L);
            map.put("降水", 1007L);
            map.put("地下水", 1012L);
            map.put("土壤（沉积物）", 1013L);
            map.put("固废（危废）", 1014L);
            map.put("环境噪声", 1015L);
            for (int i = 0; i < map.size(); i++) {
                List<QuoteBaseFactor> quoteBaseFactorList = QuoteUtil.importBaseFactorHj(path, i, map);
                if (null != quoteBaseFactorList || quoteBaseFactorList.size() > 0) {
                    if (quoteBaseFactorService.insertQuoteBaseFactorBatch(quoteBaseFactorList) == 0) {
                        throw new RuntimeException("数据插入失败！");
                    }
                }
            }
            return R.ok("数据插入成功!");
        } catch (Exception e) {
            logger.error("数据导入失败,异常信息：" + e);
            return R.error("数据导入失败！");
        }
    }

    /**
     * 公卫 （污染物基础信息导入）
     */
    @GetMapping("/baseFactor/gw")
    public R baseFactorGw() {
        try {
            String path = "C:\\Users\\MI\\Desktop\\环境\\【收费标准】\\收费标准数据库2022.4.21\\公共场所检测收费标准2022.4.21.xlsx";
            Map<String, Long> map = new HashMap<>();
            map.put("公共场所", 1005L);
            map.put("生活饮用水", 1006L);
            map.put("一次性使用卫生用品和学校卫生", 1008L);
            map.put("洁净区域和医疗卫生", 1009L);
            for (int i = 0; i < 4; i++) {
                List<QuoteBaseFactor> quoteBaseFactorList = QuoteUtil.importBaseFactorGw(path, i, map);
                if (null != quoteBaseFactorList || quoteBaseFactorList.size() > 0) {
                    if (quoteBaseFactorService.insertQuoteBaseFactorBatch(quoteBaseFactorList) == 0) {
                        throw new RuntimeException("数据插入失败！");
                    }
                }
            }
            return R.ok("数据插入成功!");
        } catch (Exception e) {
            logger.error("数据导入失败,异常信息：" + e);
            return R.error("数据导入失败！");
        }
    }

    /**
     * （职卫）导入行业、岗位、危害因素关联信息
     *
     * @return 导入结果
     */
    @GetMapping("/relationInfo/zw")
    public R relationInfoZw() {
        try {
            String path = "C:\\Users\\MI\\Desktop\\数据导入\\【职业卫生】对应关系表-李岫文0519.xlsx";
            List<RelationInfoZwDTO> relationInfoDTOList = QuoteUtil.importRelationInfoZw(path);
            if (null == relationInfoDTOList && relationInfoDTOList.size() < 1) {
                return R.error("表格内容为空，数据导入失败！");
            }

            for (RelationInfoZwDTO relationInfoDTO : relationInfoDTOList) {
                // 查询是否存在行业信息
                QuoteIndustryInfo quoteIndustryInfo = new QuoteIndustryInfo();
                quoteIndustryInfo.setProjectId(1l);
                quoteIndustryInfo.setIndustryCode(relationInfoDTO.getCode());
                quoteIndustryInfo.setIndustryName(relationInfoDTO.getIndustryName());
                QuoteIndustryInfo quoteIndustryInfoByCodeAndName = quoteIndustryInfoService.findQuoteIndustryInfo(quoteIndustryInfo);
                if (null == quoteIndustryInfoByCodeAndName) {
                    // 不存在该行业  新增
                    quoteIndustryInfo.setCreateTime(new Date());
                    quoteIndustryInfo.setUpdateTime(new Date());
                    if (quoteIndustryInfoService.insertQuoteIndustryInfo(quoteIndustryInfo) < 0) {
                        return R.error("关联行业、岗位、危害因素信息导入失败");
                    }
                    quoteIndustryInfoByCodeAndName = new QuoteIndustryInfo();
                    quoteIndustryInfoByCodeAndName.setId(quoteIndustryInfo.getId());
                }
                QuotePostInfo quotePostInfo = new QuotePostInfo();
                quotePostInfo.setIndustryId(quoteIndustryInfoByCodeAndName.getId());
                quotePostInfo.setPostName(relationInfoDTO.getPostName());
                QuotePostInfo quotePostInfoByIndustryIdAndName = quotePostInfoService.findQuotePostInfoByIndustryIdAndName(quotePostInfo);
                if (null == quotePostInfoByIndustryIdAndName) {
                    // 不存在该岗位  新增
                    quotePostInfo.setCreator(SystemUtil.getUserNameCn());
                    quotePostInfo.setCreateTime(new Date());
                    quotePostInfo.setUpdateTime(new Date());
                    if (quotePostInfoService.insertQuotePostInfo(quotePostInfo) < 0) {
                        return R.error("关联行业、岗位、危害因素信息导入失败");
                    }
                    quotePostInfoByIndustryIdAndName = new QuotePostInfo();
                    quotePostInfoByIndustryIdAndName.setId(quotePostInfo.getId());
                }
                // 关联危害因素
                QuoteBaseFactorVO quoteBaseFactorVO = new QuoteBaseFactorVO();
                quoteBaseFactorVO.setCategoryId(1002L);
                quoteBaseFactorVO.setFactorName(relationInfoDTO.getFactorName());
                QuoteBaseFactor baseFactor = quoteBaseFactorService.findQuoteBaseFactor(quoteBaseFactorVO);
                if (null != baseFactor) {
                    // 危害因素关联岗位信息
                    QuoteHarmFactor quoteHarmFactor = new QuoteHarmFactor();
                    quoteHarmFactor.setBaseId(baseFactor.getId());
                    quoteHarmFactor.setPostId(quotePostInfoByIndustryIdAndName.getId());
                    quoteHarmFactor.setCreateTime(new Date());
                    quoteHarmFactor.setUpdateTime(new Date());
                    // 插入数据
                    if (quoteHarmFactorService.insertQuoteHarmFactor(quoteHarmFactor) < 0) {
                        return R.error("关联行业、岗位、危害因素信息导入失败");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("关联行业、岗位、危害因素信息导入失败" + e);
            return R.error("关联行业、岗位、危害因素信息导入失败");
        }
        return R.ok("数据插入成功！");
    }

    /**
     * （环境）导入行业大类、行业子类、污染物类别、污染物关联信息
     *
     * @return 导入结果
     */
    @GetMapping("/relationInfo/hj")
    public R relationInfoHj() {
        try {
            String path = "C:\\Users\\MI\\Desktop\\环境\\【业务取数逻辑】\\环境\\【环境】对应关系表 -杜思思6.16修改.xlsx";
            List<RelationInfoHjDTO> relationInfoHjDTOList = QuoteUtil.importRelationInfoHj(path);
            if (null == relationInfoHjDTOList && relationInfoHjDTOList.size() < 1) {
                return R.error("表格内容为空或数据获取失败！");
            }
            for (RelationInfoHjDTO relationInfoHjDTO : relationInfoHjDTOList) {
                // 查询是否存在行业大类信息
                QuoteIndustryInfo quoteIndustryInfo = new QuoteIndustryInfo();
                quoteIndustryInfo.setParentId(0L);
                if (StrUtil.isBlank(relationInfoHjDTO.getIndustryName())) continue;
                quoteIndustryInfo.setIndustryName(relationInfoHjDTO.getIndustryName());
                quoteIndustryInfo.setProjectId(2L);
                QuoteIndustryInfo industryInfo = quoteIndustryInfoService.findQuoteIndustryInfo(quoteIndustryInfo);
                if (null == industryInfo) {
                    // 为空则插入行业大类信息
                    quoteIndustryInfo.setCreator(SystemUtil.getUserNameCn());
                    quoteIndustryInfo.setCreateTime(new Date());
                    quoteIndustryInfo.setUpdateTime(new Date());
                    if (quoteIndustryInfoMapper.insertQuoteIndustryInfo(quoteIndustryInfo) == 0) {
                        return R.error("行业大类插入失败！");
                    }
                    industryInfo = new QuoteIndustryInfo();
                    industryInfo.setId(quoteIndustryInfo.getId());
                }
                // 查询是否存在行业子类信息
                QuoteIndustryInfo qii = new QuoteIndustryInfo();
                qii.setParentId(industryInfo.getId());
                qii.setIndustryName(relationInfoHjDTO.getSubIndustry());
                qii.setProjectId(2L);
                QuoteIndustryInfo ii = quoteIndustryInfoService.findQuoteIndustryInfo(qii);
                if (null == ii) {
                    // 为空则插入行业子类信息
                    quoteIndustryInfo.setCreator(SystemUtil.getUserNameCn());
                    quoteIndustryInfo.setCreateTime(new Date());
                    quoteIndustryInfo.setUpdateTime(new Date());
                    if (quoteIndustryInfoService.insertQuoteIndustryInfo(qii) == 0) {
                        return R.error("行业子类插入失败！");
                    }
                    ii = new QuoteIndustryInfo();
                    ii.setId(qii.getId());
                }
                // 获取污染物类别id
                String pollutantTypeName = relationInfoHjDTO.getPollutantTypeName();
                QuotePollutantType pollutantType = new QuotePollutantType();
                pollutantType.setPollutantName(pollutantTypeName);
                pollutantType.setProjectId(2L);
                QuotePollutantType quotePollutantType = quotePollutantTypeService.findQuotePollutantType(pollutantType);
                // 获取收费标准对应id
                String chargeCategory = relationInfoHjDTO.getChargeCategory();
                QuoteChargeCategory quoteChargeCategory = quoteChargeCategoryService.findQuoteChargeCategoryByCategoryName(chargeCategory);
                // 获取污染物id
                String pollutantName = relationInfoHjDTO.getPollutantName();
                String standInfo = relationInfoHjDTO.getStandInfo();
                QuoteBaseFactor quoteBaseFactor = new QuoteBaseFactor();
                quoteBaseFactor.setCategoryId(1001L);
                quoteBaseFactor.setSubcategoryId(quoteChargeCategory.getCategoryId());
                quoteBaseFactor.setFactorName(pollutantName);
                quoteBaseFactor.setStandardInfo(standInfo);
                if (pollutantName.contains("分包") || pollutantName.contains("无") || pollutantName.contains("本次扩"))
                    continue;
                QuoteBaseFactor baseFactor = quoteBaseFactorService.findQuoteBaseFactorImport(quoteBaseFactor);
                if (null == baseFactor) continue;
                // 插入数据 判断该关联关系是否存在
                QuoteCategoryPollutant quoteCategoryPollutant = new QuoteCategoryPollutant();
                quoteCategoryPollutant.setMasterCategoryId(industryInfo.getId());
                quoteCategoryPollutant.setSubCategoryId(ii.getId());
                quoteCategoryPollutant.setPollutantTypeId(quotePollutantType.getId());
                quoteCategoryPollutant.setPollutantId(baseFactor.getId());
                quoteCategoryPollutant.setProjectId(2l);
                QuoteCategoryPollutant categoryPollutant = quoteCategoryPollutantService.findQuoteCategoryPollutant(quoteCategoryPollutant);
                if (null == categoryPollutant) {
                    quoteCategoryPollutant.setCreator(SystemUtil.getUserNameCn());
                    quoteCategoryPollutant.setCreateTime(new Date());
                    quoteCategoryPollutant.setUpdateTime(new Date());
                    if (quoteCategoryPollutantService.insertQuoteCategoryPollutant(quoteCategoryPollutant) == 0) {
                        return R.error("关联关系插入失败！");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("行业大类、行业子类、检测类别、污染物关联信息导入失败" + e);
            return R.error("行业大类、行业子类、检测类别、污染物关联信息导入失败");
        }
        return R.ok("数据插入成功！");
    }

    /**
     * （公卫）导入行业类别、污染物类别、污染物关联信息
     *
     * @return 导入结果
     */
    @GetMapping("/relationInfo/gw")
    public R relationInfoGw() {
        try {
            String path = "C:\\Users\\MI\\Desktop\\环境\\【业务取数逻辑】\\公卫\\【公共场所】对应关系表-需更改6.30.xlsx";
            List<RelationInfoGwDTO> relationInfoGwDTOList = QuoteUtil.importRelationInfoGw(path);
            if (null == relationInfoGwDTOList && relationInfoGwDTOList.size() < 1) {
                return R.error("表格内容为空或数据获取失败！");
            }
            for (RelationInfoGwDTO relationInfoGwDTO : relationInfoGwDTOList) {
                // 查询是否存在行业大类信息
                QuoteIndustryInfo quoteIndustryInfo = new QuoteIndustryInfo();
                quoteIndustryInfo.setParentId(0L);
                if (StrUtil.isBlank(relationInfoGwDTO.getIndustryName())) continue;
                quoteIndustryInfo.setIndustryName(relationInfoGwDTO.getIndustryName());
                quoteIndustryInfo.setProjectId(4L);
                QuoteIndustryInfo industryInfo = quoteIndustryInfoService.findQuoteIndustryInfo(quoteIndustryInfo);
                if (null == industryInfo) {
                    // 为空则插入行业大类信息
                    quoteIndustryInfo.setCreator(SystemUtil.getUserNameCn());
                    quoteIndustryInfo.setCreateTime(new Date());
                    quoteIndustryInfo.setUpdateTime(new Date());
                    if (quoteIndustryInfoMapper.insertQuoteIndustryInfo(quoteIndustryInfo) == 0) {
                        return R.error("行业大类插入失败！");
                    }
                    industryInfo = new QuoteIndustryInfo();
                    industryInfo.setId(quoteIndustryInfo.getId());
                }
                // 获取检测类别id
                String pollutantTypeName = relationInfoGwDTO.getPollutantTypeName();
                QuotePollutantType pollutantType = new QuotePollutantType();
                pollutantType.setPollutantName(pollutantTypeName);
                pollutantType.setProjectId(4L);
                QuotePollutantType quotePollutantType = quotePollutantTypeService.findQuotePollutantType(pollutantType);
                // 获取收费标准对应id
                String chargeCategory = relationInfoGwDTO.getChargeCategory();
                QuoteChargeCategory quoteChargeCategory = quoteChargeCategoryService.findQuoteChargeCategoryByCategoryName(chargeCategory);
                // 获取污染物id
                String pollutantName = relationInfoGwDTO.getPollutantName();
                if (StrUtil.isBlank(pollutantName)) continue;
                String standInfo = relationInfoGwDTO.getStandInfo();
                String limitRange = relationInfoGwDTO.getLimitRange();
                QuoteBaseFactor quoteBaseFactor = new QuoteBaseFactor();
                quoteBaseFactor.setCategoryId(1000L);
                quoteBaseFactor.setSubcategoryId(quoteChargeCategory.getCategoryId());
                quoteBaseFactor.setFactorName(pollutantName);
                quoteBaseFactor.setStandardInfo(standInfo);
                quoteBaseFactor.setLimitRange(limitRange);
                QuoteBaseFactor baseFactor = quoteBaseFactorService.findQuoteBaseFactorImport(quoteBaseFactor);
                if (null == baseFactor) continue;
                // 插入数据 判断该关联关系是否存在
                QuoteCategoryPollutant quoteCategoryPollutant = new QuoteCategoryPollutant();
                quoteCategoryPollutant.setMasterCategoryId(industryInfo.getId());
                quoteCategoryPollutant.setPollutantTypeId(quotePollutantType.getId());
                quoteCategoryPollutant.setProjectId(4l);
                QuoteCategoryPollutant categoryPollutant = quoteCategoryPollutantService.findQuoteCategoryPollutant(quoteCategoryPollutant);
                if (null == categoryPollutant) {
                    quoteCategoryPollutant.setCreator(SystemUtil.getUserNameCn());
                    quoteCategoryPollutant.setCreateTime(new Date());
                    quoteCategoryPollutant.setUpdateTime(new Date());
                    if (quoteCategoryPollutantService.insertQuoteCategoryPollutant(quoteCategoryPollutant) == 0) {
                        return R.error("关联关系插入失败！");
                    }
                    categoryPollutant = new QuoteCategoryPollutant();
                    categoryPollutant.setId(quoteCategoryPollutant.getId());
                }
                // 查询污染物关联表中是否有记录
                QuoteTestPollutant quoteTestPollutant = new QuoteTestPollutant();
                quoteTestPollutant.setId(categoryPollutant.getId());
                quoteTestPollutant.setPollutantId(baseFactor.getId());
                QuoteTestPollutant testPollutant = quoteTestPollutantService.findQuoteTestPollutant(quoteTestPollutant);
                if (testPollutant == null) {
                    quoteTestPollutant.setCreator(SystemUtil.getUserNameCn());
                    quoteTestPollutant.setCreateTime(new Date());
                    if (quoteTestPollutantService.insertQuoteTestPollutant(quoteTestPollutant) == 0) {
                        return R.error("插入关联id、污染物id失败！");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("行业类别、检测类别、污染物关联信息导入失败" + e);
            return R.error("行业类别、检测类别、污染物关联信息导入失败" + e.getMessage());
        }
        return R.ok("数据插入成功！");
    }

    /**
     * （公卫重构）导入行业类别、污染物类别、污染物关联信息
     *
     * @return 导入结果
     */
    @GetMapping("/relationInfo/gw/new")
    public R relationInfoGwNew() {
        try {
            String path = "C:\\Users\\MI\\Desktop\\【公共场所】对应关系表V3.0 20220901(1).xlsx";
            List<RelationInfoGwDTO> relationInfoGwDTOList = QuoteUtil.importRelationInfoGwNew(path);
            if (CollUtil.isEmpty(relationInfoGwDTOList)) {
                return R.error("表格内容为空或数据获取失败！");
            }
            for (RelationInfoGwDTO relationInfoGwDTO : relationInfoGwDTOList) {
                // 查询是否存在行业大类信息
                QuoteIndustryInfo quoteIndustryInfo = new QuoteIndustryInfo();
                quoteIndustryInfo.setParentId(0L);
                quoteIndustryInfo.setIndustryName(relationInfoGwDTO.getIndustryName());
                quoteIndustryInfo.setProjectId(4L);
                QuoteIndustryInfo industryInfo = quoteIndustryInfoService.findQuoteIndustryInfo(quoteIndustryInfo);
                if (industryInfo == null) {
                    // 为空则插入行业大类信息
                    quoteIndustryInfo.setCreator("test");
                    quoteIndustryInfo.setCreateTime(new Date());
                    quoteIndustryInfo.setUpdateTime(new Date());
                    if (quoteIndustryInfoMapper.insertQuoteIndustryInfo(quoteIndustryInfo) == 0) {
                        return R.error("行业大类插入失败！");
                    }
                    industryInfo = new QuoteIndustryInfo();
                    industryInfo.setId(quoteIndustryInfo.getId());
                }
                // 获取检测类别id
                String pollutantTypeName = relationInfoGwDTO.getPollutantTypeName();
                QuotePollutantType pollutantType = new QuotePollutantType();
                pollutantType.setPollutantName(pollutantTypeName);
                pollutantType.setProjectId(4L);
                QuotePollutantType quotePollutantType = quotePollutantTypeService.findQuotePollutantType(pollutantType);
                if (quotePollutantType == null) continue;
                // 获取收费标准对应id
                String chargeCategory = relationInfoGwDTO.getChargeCategory();
                QuoteChargeCategory quoteChargeCategory = quoteChargeCategoryService.findQuoteChargeCategoryByCategoryName(chargeCategory);
                if (quoteChargeCategory == null) continue;
                // 获取污染物id
                String pollutantName = relationInfoGwDTO.getPollutantName();
                String standInfo = relationInfoGwDTO.getStandInfo();
                String limitRange = relationInfoGwDTO.getLimitRange();
                QuoteBaseFactor quoteBaseFactor = new QuoteBaseFactor();
                quoteBaseFactor.setCategoryId(1000L);
                quoteBaseFactor.setSubcategoryId(quoteChargeCategory.getCategoryId());
                quoteBaseFactor.setFactorName(pollutantName);
                quoteBaseFactor.setStandardInfo(standInfo);
                if (StrUtil.isNotBlank(limitRange)) {
                    quoteBaseFactor.setLimitRange(limitRange);
                }
                // 插入数据 判断该关联关系是否存在
                QuoteCategoryPollutant quoteCategoryPollutant = new QuoteCategoryPollutant();
                quoteCategoryPollutant.setMasterCategoryId(industryInfo.getId());
                quoteCategoryPollutant.setPollutantTypeId(quotePollutantType.getId());
                quoteCategoryPollutant.setProjectId(4L);
                QuoteCategoryPollutant categoryPollutant = quoteCategoryPollutantService.findQuoteCategoryPollutant(quoteCategoryPollutant);
                if (categoryPollutant == null) {
                    quoteCategoryPollutant.setCreator("test");
                    quoteCategoryPollutant.setCreateTime(new Date());
                    quoteCategoryPollutant.setUpdateTime(new Date());
                    if (quoteCategoryPollutantService.insertQuoteCategoryPollutant(quoteCategoryPollutant) == 0) {
                        return R.error("关联关系插入失败！");
                    }
                    categoryPollutant = new QuoteCategoryPollutant();
                    categoryPollutant.setId(quoteCategoryPollutant.getId());
                }
                List<QuoteBaseFactor> baseFactorList = quoteBaseFactorService.findQuoteBaseFactorImportGwNew(quoteBaseFactor);
                if (CollUtil.isEmpty(baseFactorList)) continue;
                for (QuoteBaseFactor baseFactor : baseFactorList) {
                    // 查询污染物关联表中是否有记录
                    QuoteTestPollutant quoteTestPollutant = new QuoteTestPollutant();
                    quoteTestPollutant.setId(categoryPollutant.getId());
                    quoteTestPollutant.setPollutantId(baseFactor.getId());
                    QuoteTestPollutant testPollutant = quoteTestPollutantService.findQuoteTestPollutant(quoteTestPollutant);
                    if (testPollutant == null) {
                        quoteTestPollutant.setCreator("test");
                        quoteTestPollutant.setCreateTime(new Date());
                        if (quoteTestPollutantService.insertQuoteTestPollutant(quoteTestPollutant) == 0) {
                            return R.error("插入关联id、污染物id失败！");
                        }
                    }
                }
            }
        } catch (
                Exception e) {
            logger.error("行业类别、检测类别、污染物关联信息导入失败" + e);
            return R.error("行业类别、检测类别、污染物关联信息导入失败" + e.getMessage());
        }
        return R.ok("数据插入成功！");
    }
}

