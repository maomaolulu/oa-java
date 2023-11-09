package com.ruoyi.quote.controller;

import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.quote.domain.dto.*;
import com.ruoyi.quote.domain.vo.QuoteTestItemDetailsVO;
import com.ruoyi.quote.domain.vo.QuoteTestItemEditVO;
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
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.quote.domain.entity.QuoteTestItem;
import com.ruoyi.quote.service.IQuoteTestItemService;

/**
 * (环境)子类检测项目Controller
 *
 * @author yrb
 * @date 2022-06-16
 */
@RestController
@RequestMapping("/quote/testItem")
public class QuoteTestItemController extends BaseController {

    private final IQuoteTestItemService quoteTestItemService;
    private final RedisUtils redisUtils;

    @Autowired
    public QuoteTestItemController(IQuoteTestItemService quoteTestItemService,
                                   RedisUtils redisUtils) {
        this.quoteTestItemService = quoteTestItemService;
        this.redisUtils = redisUtils;
    }

    /**
     * 查询(环境)子类检测项目列表
     */
    @GetMapping("/list")
    public R list(QuoteTestItem quoteTestItem) {
        try {
            pageUtil();
            List<QuoteTestItem> list = quoteTestItemService.selectQuoteTestItemList(quoteTestItem);
            return resultData(list);
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息：" + e);
            return R.error("信息获取失败!");
        }
    }

    /**
     * 获取(环境)子类检测项目详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("id不能为空！");
            }
            return R.data(quoteTestItemService.selectQuoteTestItemById(id));
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息：" + e);
            return R.error("信息获取失败!");
        }
    }

    /**
     * 新增(环境)子类检测项目
     */
    @OperLog(title = "(环境)子类检测项目", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody QuoteTestItem quoteTestItem) {
        try {
            if (quoteTestItemService.insertQuoteTestItem(quoteTestItem) > 0) {
                return R.ok("新增成功！");
            }
            return R.error("新增失败！");
        } catch (Exception e) {
            logger.error("新增失败，异常信息：" + e);
            return R.error("新增失败!");
        }
    }

    /**
     * 修改(环境)子类检测项目
     */
    @OperLog(title = "(环境)子类检测项目", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody QuoteTestItem quoteTestItem) {
        try {
            if (null == quoteTestItem.getId()) {
                return R.error("id不能为空！");
            }
            if (quoteTestItemService.updateQuoteTestItem(quoteTestItem) > 0) {
                return R.ok("编辑成功!");
            }
            return R.error("编辑失败！");
        } catch (Exception e) {
            logger.error("编辑失败，异常信息：" + e);
            return R.error("编辑失败!");
        }
    }

    /**
     * 删除(环境)子类检测项目
     */
    @OperLog(title = "(环境)子类检测项目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("id不能为空！");
            }
            if (quoteTestItemService.deleteQuoteTestItemByIds(ids) > 0) {
                return R.ok("删除成功！");
            }
            return R.error("删除失败！");
        } catch (Exception e) {
            logger.error("删除失败，异常信息：" + e);
            return R.error("删除失败!");
        }
    }

    /**
     * 批量新增(环境)子类检测项目
     */
    @OperLog(title = "批量新增(环境)子类检测项目", businessType = BusinessType.INSERT)
    @PostMapping("/addBatch")
    public R addBatch(@RequestBody QuoteTestItemAddDTO quoteTestItemAddDTO) {
        try {
            QuoteTestItem quoteTestItem = quoteTestItemAddDTO.getQuoteTestItem();
            if (null == quoteTestItem) {
                return R.error("检测条目信息不能为空！");
            }
            if (StrUtil.isBlank(quoteTestItem.getSheetId())) {
                return R.error("表单id不能为空！");
            }
            if (null == quoteTestItem.getSubId()) {
                return R.error("子类id不能为空！");
            }
            if (null == quoteTestItem.getPollutantTypeId()) {
                return R.error("检测类别id不能为空！");
            }
            if (quoteTestItemService.addBatchTestItem(quoteTestItemAddDTO)) {
                return R.ok("新增成功！");
            }
            return R.error("新增失败！");
        } catch (Exception e) {
            logger.error("新增失败，异常信息：" + e);
            return R.error("新增失败!");
        }
    }

    /**
     * 批量修改(环境)子类检测项目
     */
    @OperLog(title = "批量修改(环境)子类检测项目", businessType = BusinessType.UPDATE)
    @PutMapping("/editBatch")
    public R editBatch(@RequestBody QuoteTestItemEditDTO quoteTestItemEditDTO) {
        try {
            List<QuoteTestItemDTO> quoteTestItemDTOList = quoteTestItemEditDTO.getQuoteTestItemDTOList();
            if (null == quoteTestItemDTOList || quoteTestItemDTOList.size() < 1) {
                return R.error("数据为空！");
            }
            if (quoteTestItemService.editBatchTestItem(quoteTestItemDTOList)) {
                return R.ok("编辑成功!");
            }
            return R.error("编辑失败！");
        } catch (Exception e) {
            logger.error("编辑失败，异常信息：" + e);
            return R.error("编辑失败," + e.getMessage());
        }
    }

    /**
     * 用户取消操作删除临时数据（环境--改变检测类别下拉框，删除检测项目）
     */
    @OperLog(title = "删除临时数据", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public R delete(@RequestBody QuoteTestItem quoteTestItem) {
        try {
            if (null == quoteTestItem.getPointId()) {
                return R.error("点位id不能为空!");
            }
            if (quoteTestItemService.deleteQuoteTestItemAddTemp(quoteTestItem)) {
                return R.ok("临时数据删除成功！");
            }
            return R.error("临时数据删除失败！");
        } catch (Exception e) {
            logger.error("临时数据删除失败，异常信息：" + e);
            return R.error("临时数据删除失败！");
        }
    }

    /**
     * 根据【表单id、子类id、点位id或检测类别id】获取检测细项列表
     */
    @GetMapping("/getTestItemByPointId")
    public R getTestItemByPointId(QuoteTestItem quoteTestItem) {
        try {
            if (StrUtil.isBlank(quoteTestItem.getSheetId())) {
                return R.error("表单id不能为空！");
            }
            if (null == quoteTestItem.getSubId()) {
                return R.error("子类id不能为空！");
            }
            if (null == quoteTestItem.getPointId() && null == quoteTestItem.getPollutantTypeId()) {
                return R.error("点位id或检测类别id不能为空！");
            }
            List<QuoteTestItemDetailsVO> list = quoteTestItemService.findTestItemByPointId(quoteTestItem);
            return R.data(list);
        } catch (Exception e) {
            logger.error("获取检测细项信息失败，异常信息：" + e);
            return R.error("获取检测细项信息失败!");
        }
    }

    /**
     * 环境、公卫-web端-编辑-获取数据
     */
    @GetMapping("/getTestItemEdit")
    public R getTestItemEdit(QuoteTestItem quoteTestItem) {
        try {
            if (StrUtil.isBlank(quoteTestItem.getSheetId())) {
                return R.error("表单id不能为空！");
            }
            if (null == quoteTestItem.getSubId()) {
                return R.error("子类id不能为空！");
            }
            if (quoteTestItem.getPointId() == null && quoteTestItem.getPollutantTypeId() == null) {
                return R.error("环境请传点位id，公卫请传检测类别id，不能同时为空");
            }
            if (quoteTestItem.getPointId() != null && quoteTestItem.getPollutantTypeId() != null) {
                return R.error("环境请传点位id，公卫请传检测类别id，不能同时存在");
            }
            QuoteTestItemEditVO quoteTestItemEditVO = quoteTestItemService.findTestItemEdit(quoteTestItem);
            return R.data(quoteTestItemEditVO);
        } catch (Exception e) {
            logger.error("获取检测细项信息失败，异常信息：" + e);
            return R.error("获取检测细项信息失败!");
        }
    }

    /**
     * 删除点位信息、子类检测项目
     */
    @OperLog(title = "删除点位信息、子类检测项目", businessType = BusinessType.UPDATE)
    @PostMapping("/deleteQuoteInfo")
    public R deleteQuoteInfo(@RequestBody QuoteTestItem quoteTestItem) {
        try {
            if (StrUtil.isBlank(quoteTestItem.getSheetId())) {
                return R.error("表单id不能为空！");
            }
            if (null == quoteTestItem.getSubId()) {
                return R.error("子类id不能为空！");
            }
            if (quoteTestItemService.deleteQuoteTestItem(quoteTestItem)) {
                return R.ok("删除成功！");
            }
            return R.error("删除失败！");
        } catch (Exception e) {
            logger.error("删除失败，异常信息：" + e);
            return R.error("删除失败!");
        }
    }

    /**
     * 公卫过滤已选检测类别 添加检测类别 保存到redis
     *
     * @param quotePollutantTypeRedisDTO oldId检测类别替换前的id id检测类别要替换的id subId子类id
     * @return result
     */
    @PutMapping("/addPollutantType")
    public R addPollutantType(@RequestBody QuotePollutantTypeRedisDTO quotePollutantTypeRedisDTO) {
        try {
            Long oldId = quotePollutantTypeRedisDTO.getOldId();
            Long id = quotePollutantTypeRedisDTO.getId();
            Long subId = quotePollutantTypeRedisDTO.getSubId();
            String sheetId = quotePollutantTypeRedisDTO.getSheetId();
            if (id == null) {
                return R.error("检测类别id不能为空");
            }
            if (subId == null) {
                return R.error("子类id不能为空");
            }
            if (StrUtil.isBlank(sheetId)) {
                return R.error("表单id不能为空");
            }
            String pollutantTypeId = QuoteUtil.getGwPollutantTypeKey(subId, SystemUtil.getUserId(), sheetId);
            String ids = redisUtils.get(pollutantTypeId);
            if (oldId == null) {
                // 新增
                if (StrUtil.isNotBlank(ids)) {
                    ids += "," + id;
                    redisUtils.set(pollutantTypeId, ids);
                    return R.ok("添加成功");
                }
                redisUtils.set(pollutantTypeId, id);
                return R.ok("添加成功");
            } else {
                // 替换
                List<Long> list = QuoteUtil.getList(ids);
                list.add(id);
                list.remove(oldId);
                ids = QuoteUtil.getFormatString(list, ",");
                redisUtils.set(pollutantTypeId, ids);
                return R.ok("替换成功");
            }
        } catch (Exception e) {
            logger.error("添加检测类别到缓存失败");
            return R.error("添加检测类别到缓存失败");
        }
    }

    /**
     * 公共  获取子类检测项目
     *
     * @param quoteTestItem 表单id、子类id
     * @return result
     */
    @GetMapping("/getQuoteTestItem")
    public R getQuoteTestItem(QuoteTestItem quoteTestItem) {
        try {
            if (StrUtil.isBlank(quoteTestItem.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteTestItem.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            return R.data(quoteTestItemService.findQuoteTestItem(quoteTestItem));
        } catch (Exception e) {
            logger.error("获取检测信息失败，异常信息：" + e);
            return R.error("获取检测信息失败," + e.getMessage());
        }
    }

    /**
     * 公卫 子类取消报价
     *
     * @param quoteTestItem 表单id、子类id
     */
    @PostMapping("/cancel/gw")
    public R cancelOperrationToGW(@RequestBody QuoteTestItem quoteTestItem) {
        try {
            if (StrUtil.isBlank(quoteTestItem.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteTestItem.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (quoteTestItemService.cancelOperrationToGW(quoteTestItem)) {
                return R.ok("取消报价成功");
            }
            return R.error("取消报价失败");
        } catch (Exception e) {
            logger.error("取消子类报价操作失败，异常信息：" + e);
            return R.error("取消子类报价操作失败," + e.getMessage());
        }
    }

    /**
     * 公卫 删除子类检测项目
     */
    @OperLog(title = "删除子类检测项目", businessType = BusinessType.DELETE)
    @PostMapping("/deleteQuoteInfoByPollutantType")
    public R deleteQuoteInfoByPollutantType(@RequestBody QuoteTestItem quoteTestItem) {
        try {
            if (StrUtil.isBlank(quoteTestItem.getSheetId())) {
                return R.error("表单id不能为空！");
            }
            if (null == quoteTestItem.getSubId()) {
                return R.error("子类id不能为空！");
            }
            if (quoteTestItem.getPollutantTypeId() == null && quoteTestItem.getPointId() == null) {
                return R.error("检测类别id和检测点位id不能同时为空");
            }
            if (quoteTestItemService.deleteQuoteTestItemToGW(quoteTestItem)) {
                return R.ok("当前检测类别关联信息删除成功");
            }
            return R.error("当前检测类别关联信删除失败");
        } catch (Exception e) {
            logger.error("当前检测类别关联信删除失败，异常信息：" + e);
            return R.error("当前检测类别关联信删除失败," + e.getMessage());
        }
    }

    /**
     * 行业改变 删除关联报价信息
     */
    @OperLog(title = "行业改变 删除关联报价信息", businessType = BusinessType.DELETE)
    @PostMapping("/deleteQuoteTestItemRelationInfo")
    public R deleteQuoteTestItemRelationInfo(@RequestBody QuoteTestItem quoteTestItem) {
        try {
            if (StrUtil.isBlank(quoteTestItem.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteTestItem.getId() == null) {
                return R.error("行业id不能为空");
            }
            if (quoteTestItemService.deleteQuoteTestItemRelationInfo(quoteTestItem)) {
                return R.ok("删除成功");
            }
            return R.error("删除失败");
        } catch (Exception e) {
            logger.error("删除失败，异常信息：" + e);
            return R.error("删除失败,发生未知错误！");
        }
    }

    /**
     * app端-环境-删除单个检测项目
     *
     * @param quoteTestItem 报价信息
     * @return result
     */
    @OperLog(title = "环境-单个删除报价项", businessType = BusinessType.DELETE)
    @PostMapping("/hj/delete/testItem")
    public R deleteHjTestItem(@RequestBody QuoteTestItem quoteTestItem) {
        try {
            if (quoteTestItem.getId() == null) {
                return R.error("要删除的检测项主键id不能为空");
            }
            if (quoteTestItem.getPollutantId() == null) {
                return R.error("污染物id不能为空");
            }
            if (StrUtil.isBlank(quoteTestItem.getSheetId())) {
                return R.error("报价单id不能为空");
            }
            if (quoteTestItem.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (quoteTestItem.getPointId() == null) {
                return R.error("点位id不能为空");
            }
            if (quoteTestItemService.deleteHjTestItem(quoteTestItem)) {
                return R.ok("报价项删除成功");
            }
            return R.error("报价项删除失败");
        } catch (Exception e) {
            logger.error("删除报价项失败，异常信息：" + e);
            return R.error("删除报价项失败," + e.getMessage());
        }
    }

    /**
     * app端-公卫-删除单个检测项目
     *
     * @param quoteTestItem 报价信息
     * @return result
     */
    @OperLog(title = "公卫-单个删除报价项", businessType = BusinessType.DELETE)
    @PostMapping("/gw/delete/testItem")
    public R deleteGwTestItem(@RequestBody QuoteTestItem quoteTestItem) {
        try {
            if (quoteTestItem.getId() == null) {
                return R.error("要删除的检测项id不能为空");
            }
            if (StrUtil.isBlank(quoteTestItem.getSheetId())) {
                return R.error("报价单id不能为空");
            }
            if (quoteTestItem.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (quoteTestItem.getPollutantTypeId() == null) {
                return R.error("检测类型id不能为空");
            }
            if (quoteTestItemService.deleteGwTestItem(quoteTestItem)) {
                return R.ok("报价项删除成功");
            }
            return R.error("报价项删除失败");
        } catch (Exception e) {
            logger.error("删除报价项失败，异常信息：" + e);
            return R.error("删除报价项失败," + e.getMessage());
        }
    }

    /**
     * 公卫（重构） 进到公卫子类报价界面 获取检测信息相关列表
     */
    @PostMapping("/getTestItemInfo")
    public R getTestItemInfo(@RequestBody QuoteTestItem quoteTestItem) {
        try {
            if (StrUtil.isBlank(quoteTestItem.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteTestItem.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (quoteTestItem.getId() == null) {
                return R.error("行业大类id不能为空");
            }
            return R.data(quoteTestItemService.findTestItemInfo(quoteTestItem));
        } catch (Exception e) {
            logger.error("发生异常，异常信息:" + e);
            return R.error("发生异常," + e.getMessage());
        }
    }

    /**
     * 查询关联检测物质信息
     */
    @PostMapping("/getQuoteTestInfo")
    public R getQuoteTestInfo(@RequestBody QuoteTestInfoDTO quoteTestInfoDTO) {
        try {
            if (StrUtil.isBlank(quoteTestInfoDTO.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteTestInfoDTO.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (quoteTestInfoDTO.getMasterCategoryId() == null) {
                return R.error("大类id不能为空");
            }
            if (quoteTestInfoDTO.getNatureId() == null) {
                return R.error("检测性质id不能为空");
            }
            if (CollUtil.isEmpty(quoteTestInfoDTO.getList())) {
                return R.error("检测性质id集合不能为空");
            }
            return R.data(quoteTestItemService.findTestItemInfo(quoteTestInfoDTO));
        } catch (Exception e) {
            logger.error("发生异常，异常信息：" + e);
            return R.error("发生异常");
        }
    }

    /**
     * 获取其他检测物质信息（已选）
     */
    @PostMapping("/getOtherTestItem")
    public R getOtherTestItem(@RequestBody QuoteTestItemDTO quoteTestItemDTO) {
        try {
            if (StrUtil.isBlank(quoteTestItemDTO.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteTestItemDTO.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            return R.data(quoteTestItemService.findOtherTestItem(quoteTestItemDTO));
        } catch (Exception e) {
            logger.error("发生异常，异常信息：" + e);
            return R.error("发生异常");
        }
    }

    /**
     * 选择其他检测项并提交
     */
    @OperLog(title = "提交其他检测项", businessType = BusinessType.UPDATE)
    @PostMapping("/commitOtherTestItem")
    public R commitOtherTestItem(@RequestBody QuoteTestItemDTO quoteTestItemDTO) {
        try {
            if (StrUtil.isBlank(quoteTestItemDTO.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteTestItemDTO.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (quoteTestItemService.commitOtherTestItem(quoteTestItemDTO)) {
                return R.ok("success");
            }
            return R.error("fail");
        } catch (Exception e) {
            logger.error("提交其他检测项失败，异常信息：" + e);
            return R.error("提交其他检测项失败");
        }
    }

    /**
     * 删除其他检测项 单条
     */
    @OperLog(title = "删除其他检测项", businessType = BusinessType.UPDATE)
    @PostMapping("/removeNormalTestItem")
    public R removeNormalTestItem(@RequestBody QuoteTestItemDTO quoteTestItemDTO) {
        try {
            if (StrUtil.isBlank(quoteTestItemDTO.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteTestItemDTO.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (CollUtil.isEmpty(quoteTestItemDTO.getQuoteTestItemDetailsVOList())) {
                return R.error("请传入要移除的检测项信息");
            }
            if (quoteTestItemDTO.getQuoteTestItemDetailsVOList().size() != 1) {
                return R.error("一次只能移除一条检测项信息");
            }
            if (quoteTestItemService.removeNormalTestItem(quoteTestItemDTO)) {
                return R.ok("success");
            }
            return R.error("fail");
        } catch (Exception e) {
            logger.error("发生异常，异常信息：" + e);
            return R.error("发生异常");
        }
    }

    /**
     * 报价界面返回上一步
     */
    @OperLog(title = "报价界面返回上一步", businessType = BusinessType.UPDATE)
    @PostMapping("/revertPreviousStep")
    public R revertPreviousStep(@RequestBody QuoteTestItem quoteTestItem) {
        try {
            if (StrUtil.isBlank(quoteTestItem.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteTestItem.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (quoteTestItemService.revertPreviousStep(quoteTestItem)) {
                return R.ok("success");
            }
            return R.error("fail");
        } catch (Exception e) {
            logger.error("公卫报价返回上一步发生异常，异常信息：" + e);
            return R.error("返回上一步时发生错误");
        }
    }

    /**
     * 批量添加检测项
     */
    @OperLog(title = "批量添加检测项", businessType = BusinessType.INSERT)
    @PostMapping("/addTestItemBatch")
    public R addTestItemBatch(@RequestBody QuoteTestItemDTO quoteTestItemDTO) {
        try {
            if (StrUtil.isBlank(quoteTestItemDTO.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteTestItemDTO.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (CollUtil.isEmpty(quoteTestItemDTO.getQuoteTestItemDetailsVOList())) {
                return R.error("请先添加检测物质！");
            }
            if (quoteTestItemService.addTestItemBatch(quoteTestItemDTO)) {
                return R.ok("添加成功");
            }
            return R.error("添加失败");
        } catch (Exception e) {
            logger.error("公卫添加报价项发生异常，异常信息：" + e);
            return R.error("添加失败，" + e.getMessage());
        }
    }

    /**
     * 获取其他检测物质信息列表
     */
    @PostMapping("/getOtherTestItemInfoList")
    public R getOtherTestItemInfoList(@RequestBody QuoteTestInfoDTO quoteTestInfoDTO) {
        try {
            if (StrUtil.isBlank(quoteTestInfoDTO.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteTestInfoDTO.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (CollUtil.isEmpty(quoteTestInfoDTO.getList())) {
                return R.error("检测类型id集合不能为空");
            }
            pageUtil();
            return resultData(quoteTestItemService.findOtherTestItemInfoList(quoteTestInfoDTO));
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息：" + e);
            return R.error("信息获取失败!");
        }
    }

    /**
     * 获取其他检测物质信息列表 (web端)
     */
    @PostMapping("/getOtherTestItemInfoListForWeb")
    public R getOtherTestItemInfoListForWeb(@RequestBody QuoteTestInfoDTO quoteTestInfoDTO) {
        try {
            if (StrUtil.isBlank(quoteTestInfoDTO.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteTestInfoDTO.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (CollUtil.isEmpty(quoteTestInfoDTO.getList())) {
                return R.error("检测类型id集合不能为空");
            }
            return R.data(quoteTestItemService.findOtherTestItemInfoListForWeb(quoteTestInfoDTO));
        } catch (Exception e) {
            logger.error("信息获取失败，异常信息：" + e);
            return R.error("信息获取失败!");
        }
    }

    /**
     * 缓存检测性质
     */
    @OperLog(title = "缓存检测性质", businessType = BusinessType.INSERT)
    @PostMapping("/saveTestNatureId")
    public R saveTestNatureId(@RequestBody QuoteTestItemDTO quoteTestItemDTO) {
        try {
            if (StrUtil.isBlank(quoteTestItemDTO.getSheetId())) {
                return R.error("表单id不能为空");
            }
            if (quoteTestItemDTO.getSubId() == null) {
                return R.error("子类id不能为空");
            }
            if (quoteTestItemDTO.getTestNatureId() == null) {
                return R.error("检测性质id不能为空");
            }
            if (quoteTestItemService.saveTestNatureId(quoteTestItemDTO)) {
                return R.ok("success");
            }
            return R.error("fail");
        } catch (Exception e) {
            logger.error("发生异常，异常信息：---->" + e);
            return R.error("未知错误");
        }
    }
}
