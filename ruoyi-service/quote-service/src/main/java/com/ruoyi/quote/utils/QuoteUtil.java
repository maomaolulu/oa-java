package com.ruoyi.quote.utils;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.quote.domain.dto.RelationInfoGwDTO;
import com.ruoyi.quote.domain.dto.RelationInfoHjDTO;
import com.ruoyi.quote.domain.dto.RelationInfoZwDTO;
import com.ruoyi.quote.domain.entity.QuoteBaseFactor;
import com.ruoyi.quote.domain.entity.QuoteTestNature;
import io.swagger.annotations.ApiModelProperty;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @Author yrb
 * @Date 2022/5/12 8:49
 * @Version 1.0
 * @Description 报价相关工具类
 */
public class QuoteUtil {

    @ApiModelProperty(value = "检测性质名称集合")
    public static final String TEST_NATURE_NAMES = "quote.gw.cg.web.nature.names";

    @ApiModelProperty(value = "检测性质id集合")
    public static final String TEST_NATURE_IDS = "quote.gw.cg.web.nature.ids";

    @ApiModelProperty(value = "检测性质id集合")
    public static final String TEST_NATURE_LIST = "quote.gw.cg.web.nature.list";

    @ApiModelProperty(value = "选中标识")
    public static final Integer CHECKED_FLAG = 1;

    @ApiModelProperty(value = "临时文件标识：是")
    public static final Long TEMP_FLAG = 1L;

    @ApiModelProperty(value = "其他检测类型")
    public static final Long OTHER_TEST_ITEM_FLAG = 1L;
    @ApiModelProperty(value = "常规检测类型")
    public static final Long NORMAL_TEST_ITEM_FLAG = 2L;

    @ApiModelProperty(value = "公卫所属项目id")
    public static final Long GW_PROJECT_ID = 4L;

    @ApiModelProperty(value = "环境所属项目id")
    public static final Long HJ_PROJECT_ID = 2L;

    @ApiModelProperty(value = "危害因素类型：分包")
    public static final Integer FACTOR_TYPE_FENBAO = 1;

    @ApiModelProperty(value = "危害因素类型：扩项")
    public static final Integer FACTOR_TYPE_KUOXIANG = 2;

    @ApiModelProperty(value = "收费标准子类：扩项")
    public static final String CHARGE_CATEGORY_KUOXIANG = "扩项";

    @ApiModelProperty(value = "收费标准子类：分包")
    public static final String CHARGE_CATEGORY_FENBAO = "分包";

    @ApiModelProperty(value = "关联标识")
    public static final Integer RELATION_FLAG = 1;

    @ApiModelProperty(value = "职卫收费标准id")
    public static final Long ZW_CATEGORY_ID = 1002L;

    @ApiModelProperty(value = "公卫重构【其他公共场所】行业id缓存key")
    public static final String GW_CG_OTHER_INDUSTRY_ID_KEY = "quote:gw:cg:other:industry:id:forAllUser";

    @ApiModelProperty(value = "其他公共场所")
    public static final String OTHER_PUBLIC_PLACE_INDUSTRY_NAME="其他公共场所";

    /**
     * 获取检测类型名称和id集合
     *
     * @param quoteTestNatureList 检测类型集合
     * @return map
     */
    public static Map<String, String> getNatureNamesAndIds(List<QuoteTestNature> quoteTestNatureList) {
        StringBuilder names = new StringBuilder();
        StringBuilder ids = new StringBuilder();
        for (QuoteTestNature quoteTestNature : quoteTestNatureList) {
            Long id = quoteTestNature.getId();
            ids.append(id).append(",");
            String natureName = quoteTestNature.getNatureName();
            if (natureName.contains("、")) {
                natureName = natureName.replace("、", "");
            }
            names.append(natureName).append("、");
        }
        Map<String, String> map = new HashMap<>();
        map.put(TEST_NATURE_NAMES, names.substring(0, names.length() - 1));
        map.put(TEST_NATURE_IDS, ids.toString());
        return map;
    }

    /**
     * 拼接报价项目
     *
     * @param master  主类集合
     * @param subList 子类简称集合
     * @return 所有报价项目
     */
    public static String getQuoteProject(String master, List<String> subList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String sub : subList) {
            stringBuilder.append(master).append("-").append(sub).append(",");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    /**
     * 获取in查询字段
     *
     * @param subList 字符串数组
     * @return in 条件查询字段
     */
    public static String getQueryFiled(List<String> subList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String sub : subList) {
            stringBuilder.append("'").append(sub).append("'").append(",");
        }
        return "(" + stringBuilder.substring(0, stringBuilder.length() - 1) + ")";
    }

    /**
     * 计算BigDecimal总和
     *
     * @return 和
     */
    public static BigDecimal getBigDecimalTotal(List<BigDecimal> list) {
        BigDecimal sum = new BigDecimal("0.0");
        for (BigDecimal num : list) {
            sum = sum.add(num);
        }
        return sum;
    }

    /**
     * 职卫--基础危害因素信息导入
     *
     * @param path 文件路径
     * @return 集合
     * @throws Exception 异常信息
     */
    public static List<QuoteBaseFactor> importBaseFactorZw(String path) throws Exception {
        FileInputStream in = new FileInputStream(path);
        XSSFWorkbook sheets = new XSSFWorkbook(in);
        int numberOfSheets = sheets.getNumberOfSheets();
        if (numberOfSheets < 1) {
            return null;
        }
        List<QuoteBaseFactor> quoteBaseFactorList = new ArrayList<>();
        XSSFSheet sheetAt = sheets.getSheetAt(2);
        int rowNum = sheetAt.getLastRowNum();
        if (rowNum < 1) {
            return null;
        }
        Map<Integer, Object> map = new HashMap<>();
        for (int j = 1; j <= rowNum; j++) {
            XSSFRow row = sheetAt.getRow(j);
            short cellNum = row.getLastCellNum();
            if (cellNum < 1) {
                continue;
            }
            List<Object> list = new ArrayList<>();
            for (int k = 1; k < cellNum; k++) {
                XSSFCell cell = row.getCell(k);
                if (null == cell || CellType.BLANK == cell.getCellType()) {
                    if (null == map.get(k)) {
                        XSSFRow row1 = sheetAt.getRow(j - 1);
                        XSSFCell cell1 = row1.getCell(k);
                        if (null == cell || CellType.BLANK == cell.getCellType()) {
                            list.add(null);
                            continue;
                        }
                        if (CellType.NUMERIC == cell1.getCellType()) {
                            map.put(k, cell1.getNumericCellValue());
                        } else {
                            map.put(k, cell1.getStringCellValue());
                        }
                    }
                    list.add(map.get(k));
                } else {
                    if (CellType.NUMERIC == cell.getCellType()) {
                        list.add(cell.getNumericCellValue());
                        map.put(k, cell.getNumericCellValue());
                    } else {
                        list.add(cell.getStringCellValue());
                        map.put(k, cell.getStringCellValue());
                    }
                }
            }
            QuoteBaseFactor quoteBaseFactor = new QuoteBaseFactor();
            quoteBaseFactor.setFactorName(list.get(0).toString());
            String standardInfo = list.get(1).toString();
            if ("无".equals(standardInfo)) {
                quoteBaseFactor.setStandardInfo(null);
            } else {
                quoteBaseFactor.setStandardInfo(list.get(1).toString());
            }
            if (null != list.get(2)) {
                quoteBaseFactor.setLimitRange(list.get(2).toString());
            }
            quoteBaseFactor.setPrice(new BigDecimal(list.get(3).toString()));
            quoteBaseFactor.setCategoryId(1002L);
            quoteBaseFactor.setSubcategoryId(1003L);
            quoteBaseFactor.setCreateTime(new Date());
            quoteBaseFactor.setUpdateTime(new Date());
            quoteBaseFactorList.add(quoteBaseFactor);
        }
        return quoteBaseFactorList;
    }

    /**
     * 环境--基础危害因素信息导入
     *
     * @param path 文件路径
     * @return 集合
     * @throws Exception 异常信息
     */
    public static List<QuoteBaseFactor> importBaseFactorHj(String path, int sheetIndex, Map<String, Long> subcategoryIdMap) throws Exception {
        FileInputStream in = new FileInputStream(path);
        XSSFWorkbook sheets = new XSSFWorkbook(in);
        int numberOfSheets = sheets.getNumberOfSheets();
        if (numberOfSheets < 1) {
            return null;
        }
        List<QuoteBaseFactor> quoteBaseFactorList = new ArrayList<>();
        XSSFSheet sheetAt = sheets.getSheetAt(sheetIndex);
        int rowNum = sheetAt.getLastRowNum();
        if (rowNum < 1) {
            return null;
        }
        String sheetName = sheets.getSheetName(sheetIndex);
        Long subcategoryId = subcategoryIdMap.get(sheetName);
        Map<Integer, Object> map = new HashMap<>();
        for (int j = 1; j <= rowNum; j++) {
            XSSFRow row = sheetAt.getRow(j);
            short cellNum = row.getLastCellNum();
            if (cellNum < 1) {
                continue;
            }
            List<Object> list = new ArrayList<>();
            for (int k = 2; k < cellNum; k++) {
                XSSFCell cell = row.getCell(k);
                if (null == cell || CellType.BLANK == cell.getCellType()) {
                    if (null == map.get(k)) {
                        XSSFRow row1 = sheetAt.getRow(j - 1);
                        XSSFCell cell1 = row1.getCell(k);
                        if (null == cell || CellType.BLANK == cell.getCellType()) {
                            list.add(null);
                            continue;
                        }
                        if (CellType.NUMERIC == cell1.getCellType()) {
                            map.put(k, cell1.getNumericCellValue());
                        } else {
                            map.put(k, cell1.getStringCellValue());
                        }
                    }
                    list.add(map.get(k));
                } else {
                    if (CellType.NUMERIC == cell.getCellType()) {
                        list.add(cell.getNumericCellValue());
                        map.put(k, cell.getNumericCellValue());
                    } else {
                        list.add(cell.getStringCellValue());
                        map.put(k, cell.getStringCellValue());
                    }
                }
            }
            QuoteBaseFactor quoteBaseFactor = new QuoteBaseFactor();
            quoteBaseFactor.setFactorName(list.get(0).toString());
            quoteBaseFactor.setStandardInfo(list.get(1).toString());
            if (null != list.get(2)) {
                quoteBaseFactor.setLimitRange(list.get(2).toString());
            }
            // 采样费
            BigDecimal samplePrice = new BigDecimal(list.get(3).toString());
            quoteBaseFactor.setSamplePrice(samplePrice);
            // 分析费
            BigDecimal analysePrice = new BigDecimal(list.get(4).toString());
            quoteBaseFactor.setAnalysePrice(analysePrice);
            // 计算单价 (采样费 + 分析费)
            BigDecimal price = samplePrice.add(analysePrice);
            quoteBaseFactor.setPrice(price);
            quoteBaseFactor.setCategoryId(1001L);
            quoteBaseFactor.setSubcategoryId(subcategoryId);
            quoteBaseFactor.setCreateTime(new Date());
            quoteBaseFactor.setUpdateTime(new Date());
            quoteBaseFactorList.add(quoteBaseFactor);
        }
        return quoteBaseFactorList;
    }

    /**
     * 公卫--基础危害因素信息导入
     *
     * @param path 文件路径
     * @return 集合
     * @throws Exception 异常信息
     */
    public static List<QuoteBaseFactor> importBaseFactorGw(String path, int sheetIndex, Map<String, Long> subcategoryIdMap) throws Exception {
        FileInputStream in = new FileInputStream(path);
        XSSFWorkbook sheets = new XSSFWorkbook(in);
        int numberOfSheets = sheets.getNumberOfSheets();
        if (numberOfSheets < 1) {
            return null;
        }
        List<QuoteBaseFactor> quoteBaseFactorList = new ArrayList<>();
        XSSFSheet sheetAt = sheets.getSheetAt(sheetIndex);
        int rowNum = sheetAt.getLastRowNum();
        if (rowNum < 1) {
            return null;
        }
        String sheetName = sheets.getSheetName(sheetIndex);
        Long subcategoryId = subcategoryIdMap.get(sheetName);
        Map<Integer, Object> map = new HashMap<>();
        for (int j = 1; j <= rowNum; j++) {
            XSSFRow row = sheetAt.getRow(j);
            short cellNum = row.getLastCellNum();
            if (cellNum < 1) {
                continue;
            }
            List<Object> list = new ArrayList<>();
            for (int k = 2; k < cellNum; k++) {
                XSSFCell cell = row.getCell(k);
                if (null == cell || CellType.BLANK == cell.getCellType()) {
                    if (null == map.get(k)) {
                        XSSFRow row1 = sheetAt.getRow(j - 1);
                        XSSFCell cell1 = row1.getCell(k);
                        if (null == cell || CellType.BLANK == cell.getCellType()) {
                            list.add(null);
                            continue;
                        }
                        if (CellType.NUMERIC == cell1.getCellType()) {
                            map.put(k, cell1.getNumericCellValue());
                        } else {
                            map.put(k, cell1.getStringCellValue());
                        }
                    }
                    list.add(map.get(k));
                } else {
                    if (CellType.NUMERIC == cell.getCellType()) {
                        list.add(cell.getNumericCellValue());
                        map.put(k, cell.getNumericCellValue());
                    } else {
                        list.add(cell.getStringCellValue());
                        map.put(k, cell.getStringCellValue());
                    }
                }
            }
            QuoteBaseFactor quoteBaseFactor = new QuoteBaseFactor();
            quoteBaseFactor.setFactorName(list.get(0).toString());
            quoteBaseFactor.setStandardInfo(list.get(1).toString());
            if (null != list.get(2)) {
                quoteBaseFactor.setLimitRange(list.get(2).toString());
            }
            quoteBaseFactor.setPrice(new BigDecimal(list.get(3).toString()));
            quoteBaseFactor.setCategoryId(1000L);
            quoteBaseFactor.setSubcategoryId(subcategoryId);
            quoteBaseFactor.setCreateTime(new Date());
            quoteBaseFactor.setUpdateTime(new Date());
            quoteBaseFactorList.add(quoteBaseFactor);
        }
        return quoteBaseFactorList;
    }

    /**
     * （职卫）导入行业、岗位、危害因素关联信息
     *
     * @param path 文件路径
     * @return 关联信息集合
     * @throws Exception 异常信息
     */
    public static List<RelationInfoZwDTO> importRelationInfoZw(String path) throws Exception {
        FileInputStream in = new FileInputStream(path);
        XSSFWorkbook sheets = new XSSFWorkbook(in);
        int numberOfSheets = sheets.getNumberOfSheets();
        if (numberOfSheets < 1) {
            return null;
        }
        XSSFSheet sheetAt = sheets.getSheetAt(0);
        int rowNum = sheetAt.getLastRowNum();
        if (rowNum < 1) {
            return null;
        }
        List<RelationInfoZwDTO> relationInfoDTOList = new ArrayList<>();
        for (int j = 2; j <= rowNum; j++) {
            XSSFRow row = sheetAt.getRow(j);
            short cellNum = row.getLastCellNum();
            if (cellNum < 1) {
                continue;
            }
            RelationInfoZwDTO relationInfoZwDTO = new RelationInfoZwDTO();
            for (int k = 1; k < 6; k++) {
                switch (k) {
                    case 1:
                        relationInfoZwDTO.setCode(row.getCell(k).getStringCellValue());
                        break;
                    case 2:
                        relationInfoZwDTO.setIndustryName(row.getCell(k).getStringCellValue());
                        break;
                    case 4:
                        relationInfoZwDTO.setPostName(row.getCell(k).getStringCellValue());
                        break;
                    case 5:
                        relationInfoZwDTO.setFactorName(row.getCell(k).getStringCellValue());
                        break;
                }
            }
            relationInfoDTOList.add(relationInfoZwDTO);
        }
        return relationInfoDTOList;
    }

    /**
     * （环境）导入行业大类、行业子类、污染物类别、污染物关联信息
     *
     * @param path 文件路径
     * @return 关联信息集合
     * @throws Exception 异常信息
     */
    public static List<RelationInfoHjDTO> importRelationInfoHj(String path) throws Exception {
        FileInputStream in = new FileInputStream(path);
        XSSFWorkbook sheets = new XSSFWorkbook(in);
        int numberOfSheets = sheets.getNumberOfSheets();
        if (numberOfSheets < 1) {
            return null;
        }
        XSSFSheet sheetAt = sheets.getSheetAt(0);
        int rowNum = sheetAt.getLastRowNum();
        if (rowNum < 1) {
            return null;
        }
        List<RelationInfoHjDTO> relationInfoHjDTOList = new ArrayList<>();
        for (int j = 2; j <= rowNum; j++) {
            XSSFRow row = sheetAt.getRow(j);
            if (null == row) continue;
            short cellNum = row.getLastCellNum();
            if (cellNum < 1) {
                continue;
            }
            XSSFCell cell = row.getCell(0);
            if (null == cell) continue;
            String chargeCategory = row.getCell(5).getStringCellValue();
            if ("无".equals(chargeCategory)) continue;
            RelationInfoHjDTO relationInfoHjDTO = new RelationInfoHjDTO();
            // 收费标准
            relationInfoHjDTO.setChargeCategory(chargeCategory);
            // 行业大类
            String industryName = row.getCell(0).getStringCellValue();
            industryName = Pattern.compile("[\\d]").matcher(industryName.substring(industryName.indexOf("、") + 1)).replaceAll("");
            relationInfoHjDTO.setIndustryName(industryName);
            // 行业子类
            String subIndustry = Pattern.compile("[\\d]").matcher(row.getCell(1).getStringCellValue()).replaceAll("");
            relationInfoHjDTO.setSubIndustry(subIndustry);
            // 污染物类别
            relationInfoHjDTO.setPollutantTypeName(row.getCell(2).getStringCellValue());
            // 污染物名称
            relationInfoHjDTO.setPollutantName(row.getCell(6).getStringCellValue());
            // 检测标准
            relationInfoHjDTO.setStandInfo(row.getCell(7).getStringCellValue());
            relationInfoHjDTOList.add(relationInfoHjDTO);
        }
        return relationInfoHjDTOList;
    }

    /**
     * （公卫）导入行业类别、污染物类别、污染物关联信息
     *
     * @param path 文件路径
     * @return 关联信息集合
     * @throws Exception 异常信息
     */
    public static List<RelationInfoGwDTO> importRelationInfoGw(String path) throws Exception {
        FileInputStream in = new FileInputStream(path);
        XSSFWorkbook sheets = new XSSFWorkbook(in);
        int numberOfSheets = sheets.getNumberOfSheets();
        if (numberOfSheets < 1) {
            return null;
        }
        XSSFSheet sheetAt = sheets.getSheetAt(0);
        int rowNum = sheetAt.getLastRowNum();
        if (rowNum < 1) {
            return null;
        }
        List<RelationInfoGwDTO> relationInfoGwDTOList = new ArrayList<>();
        for (int j = 2; j <= rowNum; j++) {
            XSSFRow row = sheetAt.getRow(j);
            short cellNum = row.getLastCellNum();
            if (cellNum < 1) {
                continue;
            }
            RelationInfoGwDTO relationInfoGwDTO = new RelationInfoGwDTO();
            // 行业类别
            String industryName = row.getCell(1).getStringCellValue();
            relationInfoGwDTO.setIndustryName(industryName);
            // 检测类别（污染物类别）
            String pollutantTypeName = row.getCell(2).getStringCellValue();
            relationInfoGwDTO.setPollutantTypeName(pollutantTypeName);
            // 收费标准类别
            String chargeCategory = row.getCell(3).getStringCellValue();
            relationInfoGwDTO.setChargeCategory(chargeCategory);
            // 项目名称（污染物名称）
            String pollutantName = row.getCell(4).getStringCellValue();
            relationInfoGwDTO.setPollutantName(pollutantName);
            // 检测标准及编号
            String standInfo = row.getCell(5).getStringCellValue();
            relationInfoGwDTO.setStandInfo(standInfo);
            // 限制范围
            String limitRange = row.getCell(6).getStringCellValue();
            relationInfoGwDTO.setLimitRange(limitRange);
            relationInfoGwDTOList.add(relationInfoGwDTO);
        }
        return relationInfoGwDTOList;
    }

    /**
     * （公卫-其他公共场所）导入行业类别、污染物类别、污染物关联信息
     *
     * @param path 文件路径
     * @return 关联信息集合
     * @throws Exception 异常信息
     */
    public static List<RelationInfoGwDTO> importRelationInfoGwNew(String path) throws Exception {
        FileInputStream in = new FileInputStream(path);
        XSSFWorkbook sheets = new XSSFWorkbook(in);
        int numberOfSheets = sheets.getNumberOfSheets();
        if (numberOfSheets < 1) {
            return null;
        }
        XSSFSheet sheetAt = sheets.getSheetAt(0);
        int rowNum = sheetAt.getLastRowNum();
        if (rowNum < 1) {
            return null;
        }
        List<RelationInfoGwDTO> relationInfoGwDTOList = new ArrayList<>();
        for (int j = 2; j <= rowNum; j++) {
            XSSFRow row = sheetAt.getRow(j);
            short cellNum = row.getLastCellNum();
            if (cellNum < 1) {
                continue;
            }
            String industryName = row.getCell(1).getStringCellValue();
            if (StrUtil.isBlank(industryName) || !"其他公共场所".equals(industryName)) continue;
            RelationInfoGwDTO relationInfoGwDTO = new RelationInfoGwDTO();
            // 行业类别
            relationInfoGwDTO.setIndustryName(industryName);
            // 检测类别（污染物类别）
            String pollutantTypeName = row.getCell(3).getStringCellValue();
            if (StrUtil.isBlank(pollutantTypeName))continue;
            relationInfoGwDTO.setPollutantTypeName(pollutantTypeName);
            // 收费标准类别
            String chargeCategory = row.getCell(4).getStringCellValue();
            if (StrUtil.isBlank(chargeCategory))continue;
            relationInfoGwDTO.setChargeCategory(chargeCategory);
            // 项目名称（污染物名称）
            String pollutantName = row.getCell(6).getStringCellValue();
            if (StrUtil.isBlank(pollutantName))continue;
            relationInfoGwDTO.setPollutantName(pollutantName);
            // 检测标准及编号
            String standInfo = row.getCell(7).getStringCellValue();
            if (StrUtil.isBlank(standInfo))continue;
            relationInfoGwDTO.setStandInfo(standInfo);
            // 限制范围
            String limitRange = row.getCell(8).getStringCellValue();
            relationInfoGwDTO.setLimitRange(limitRange);
            relationInfoGwDTOList.add(relationInfoGwDTO);
        }
        return relationInfoGwDTOList;
    }

    /**
     * 获取随机数
     *
     * @param length
     */
    public static String getRandomNumber(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }

    /**
     * 字符串转list
     *
     * @param s 逗号分割的字符串
     * @return list
     */
    public static List<Long> getList(String s) {
        String[] list = s.split(",");
        List<Long> ll = new ArrayList<>();
        for (String ss : list) {
            ll.add(new Long(ss));
        }
        return ll;
    }

    /**
     * 获取公卫检测类型Redis缓存key
     *
     * @param subId   子类id
     * @param userId  用户id
     * @param sheetId 表单id
     * @return result
     */
    public static String getGwPollutantTypeKey(Long subId, Long userId, String sheetId) {
        return "quote:gw:type:" + subId + ":" + userId + ":" + sheetId;
    }

    /**
     * 获取环境子类已报价的污染物id Redis缓存
     *
     * @param sheetId 表单id
     * @param subId   子类id
     * @param pointId 点位id
     * @param userId  用户id
     * @return result
     */
    public static String getHjTestItemsIdsKey(String sheetId, Long subId, Long pointId, Long userId) {
        return "quote:hj:items:ids:" + sheetId + ":" + subId + ":" + pointId + ":" + userId;
    }

    /**
     * 获取公卫子类已报价的污染物id Redis缓存
     *
     * @param sheetId         表单id
     * @param subId           子类id
     * @param pollutantTypeId 检测类型id
     * @param userId          用户id
     * @return result
     */
    public static String getGwTestItemsIdsKey(String sheetId, Long subId, Long pollutantTypeId, Long userId) {
        return "quote:gw:items:ids:" + sheetId + ":" + subId + ":" + pollutantTypeId + ":" + userId;
    }

    /**
     * 获取公卫检测类型Redis缓存key
     *
     * @param subId   子类id
     * @param userId  用户id
     * @param sheetId 表单id
     * @return result
     */
    public static String getGwOrHjTestListKey(Long subId, Long userId, String sheetId) {
        return "quote:hjorgw:list:" + subId + ":" + userId + ":" + sheetId;
    }

    /**
     * 职卫选中的所有危害因素
     *
     * @param sheetId 表单id
     * @param subId   子类id
     * @param postId  岗位id
     * @param userId  用户id
     * @return result
     */
    public static String getZwTestListKey(String sheetId, Long subId, Long postId, Long userId) {
        return "quote:zw:list:" + sheetId + ":" + subId + ":" + postId + ":" + userId;
    }

    /**
     * 职卫基础信息表所选的检测因素列表
     *
     * @param sheetId 表单id
     * @param subId   子类id
     * @param postId  岗位id
     * @param userId  用户id
     * @return result
     */
    public static String getZwBaseTestListKey(String sheetId, Long subId, Long postId, Long userId) {
        return "quote:zw:base:list:" + sheetId + ":" + subId + ":" + postId + ":" + userId;
    }

    /**
     * 职卫 岗位关联的危害因素
     *
     * @param sheetId 表单id
     * @param postId  岗位id
     * @param subId   子类id
     * @param userId  用户id
     * @return result
     */
    public static String getZwCheckedIdsKey(String sheetId, Long subId, Long postId, Long userId) {
        return "quote:zw:checked:ids:" + sheetId + ":" + subId + ":" + postId + ":" + userId;
    }

    /**
     * 职卫 岗位关联的危害因素
     *
     * @param sheetId 表单id
     * @param subId   子类id
     * @param postId  岗位id
     * @param userId  用户id
     * @return result
     */
    public static String getZwPostIdsKey(String sheetId, Long subId, Long postId, Long userId) {
        return "quote:zw:post:ids:" + sheetId + ":" + subId + ":" + postId + ":" + userId;
    }

    /**
     * 职卫选中的所有危害因素 临时
     *
     * @param sheetId 表单id
     * @param subId   子类id
     * @param postId  岗位id
     * @param userId  用户id
     * @return result
     */
    public static String getZwTestListTempKey(String sheetId, Long subId, Long postId, Long userId) {
        return "quote:zw:temp:list:" + sheetId + ":" + subId + ":" + postId + ":" + userId;
    }

    /**
     * 职卫基础信息表所选的检测因素列表 临时
     *
     * @param sheetId 表单id
     * @param subId   子类id
     * @param postId  岗位id
     * @param userId  用户id
     * @return result
     */
    public static String getZwBaseTestListTempKey(String sheetId, Long subId, Long postId, Long userId) {
        return "quote:zw:base:temp:list:" + sheetId + ":" + subId + ":" + postId + ":" + userId;
    }

    /**
     * 职卫 岗位关联的危害因素 临时
     *
     * @param sheetId 表单id
     * @param subId   子类id
     * @param postId  岗位id
     * @param userId  用户id
     * @return result
     */
    public static String getZwCheckedIdsTempKey(String sheetId, Long subId, Long postId, Long userId) {
        return "quote:zw:checked:temp:ids:" + sheetId + ":" + subId + ":" + postId + ":" + userId;
    }

    /**
     * 职卫 子类已选的岗位
     *
     * @param sheetId 表单id
     * @param subId   子类id
     * @param userId  用户id
     * @return result
     */
    public static String getZwPostCheckedIdsKey(String sheetId, Long subId, Long userId) {
        return "quote:zw:post:checked::ids:" + sheetId + ":" + subId + ":" + userId;
    }

    /**
     * 行业id
     *
     * @param sheetId 表单id
     * @param userId  用户id
     * @return result
     */
    public static String getIndustryIdKey(String sheetId, Long userId) {
        return "quote:industry:id:" + sheetId + ":" + userId;
    }

    /**
     * 公卫重构 报价项
     *
     * @param sheetId 表单id
     * @param subId   子类id
     * @param userId  用户id
     * @return 报价项目集合
     */
    public static String getGwCgNormalTestItemKey(String sheetId, Long subId, Long userId) {
        return "quote:gw:cg:normal:item:" + sheetId + ":" + subId + ":" + userId;
    }

    /**
     * 公卫重构 报价项（其他）
     *
     * @param sheetId 表单id
     * @param subId   子类id
     * @param userId  用户id
     * @return 报价项目集合
     */
    public static String getGwCgOtherTestItemKey(String sheetId, Long subId, Long userId) {
        return "quote:gw:cg:other:item:" + sheetId + ":" + subId + ":" + userId;
    }

    /**
     * 公卫重构 报价项（其他） id集合
     *
     * @param sheetId 表单id
     * @param subId   子类id
     * @param userId  用户id
     * @return id集合
     */
    public static String getGwCgOtherTestItemIdsKey(String sheetId, Long subId, Long userId) {
        return "quote:gw:cg:other:item:ids:" + sheetId + ":" + subId + ":" + userId;
    }

    /**
     * 公卫重构 获取其他检测项集合 web端
     *
     * @param sheetId 表单id
     * @param subId   子类id
     * @param userId  用户id
     * @return 报价项目集合
     */
    public static String getGwCgOtherTestItemForWebKey(String sheetId, Long subId, Long userId) {
        return "quote:gw:cg:other:item:list:web:" + sheetId + ":" + subId + ":" + userId;
    }

    /**
     * 公卫重构 缓存检测性质id
     *
     * @param sheetId 表单id
     * @param subId 子类id
     * @param userId 用户id
     * @return result
     */
    public static String getGwCgTestNatureIdCacheKey(String sheetId, Long subId, Long userId) {
        return "quote:gw:cg:test:nature:id:cache:" + sheetId + ":" + subId + ":" + userId;
    }

    /**
     * Long类型的list转换成format分割的字符串
     *
     * @param list   list集合
     * @param format 分隔符
     * @return 字符串
     */
    public static String getFormatString(Collection<?> list, String format) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object l : list) {
            stringBuilder.append(l).append(format);
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }
}
