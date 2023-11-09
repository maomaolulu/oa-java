package com.ruoyi.training.controller;

import java.util.Arrays;
import java.util.List;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.training.entity.TraTrainCategory;
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
import com.ruoyi.training.service.ITraTrainCategoryService;

/**
 * 培训类型Controller
 *
 * @author yrb
 * @date 2022-06-06
 */
@RestController
@RequestMapping("/training/category")
public class TraTrainCategoryController extends BaseController {
    private final ITraTrainCategoryService traTrainCategoryService;

    @Autowired
    public TraTrainCategoryController(ITraTrainCategoryService traTrainCategoryService) {
        this.traTrainCategoryService = traTrainCategoryService;
    }

    /**
     * 查询培训类型列表
     */
    @GetMapping("/list")
    public R list(TraTrainCategory traTrainCategory) {
        try {
            pageUtil();
            List<TraTrainCategory> list = traTrainCategoryService.selectTraTrainCategoryList(traTrainCategory);
            return resultData(list);
        } catch (Exception e) {
            logger.error("查询培训类型信息失败，异常信息：" + e);
            return R.error("查询培训类型信息失败." );
        }
    }

    /**
     * 获取培训类型详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("培训类型id不能为空！");
            }
            return R.data(traTrainCategoryService.selectTraTrainCategoryById(id));
        } catch (Exception e) {
            logger.error("获取培训类型信息失败，异常信息：" + e);
            return R.error("获取培训类型信息失败." );
        }
    }

    /**
     * 新增培训类型
     */
    @OperLog(title = "培训类型", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody TraTrainCategory traTrainCategory) {
        try {
            if (null == traTrainCategory) {
                R.error("要新增的培训类型信息不能为空！");
            }
            if (traTrainCategoryService.insertTraTrainCategory(traTrainCategory) > 0) {
                return R.ok("培训类型信息新增成功！");
            }
            return R.error("培训类型信息新增失败！");
        } catch (Exception e) {
            logger.error("获取培训类型信息失败，异常信息：" + e);
            return R.error("获取培训类型信息失败." );
        }
    }

    /**
     * 修改培训类型
     */
    @OperLog(title = "培训类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody TraTrainCategory traTrainCategory) {
        try {
            if (null == traTrainCategory || null == traTrainCategory.getId()) {
                return R.error("要修改的培训类型信息或者培训类型信息id不能为空！");
            }
            if (traTrainCategoryService.updateTraTrainCategory(traTrainCategory) > 0) {
                return R.ok("培训类型信息修改成功！");
            }
            return R.error("培训类型信息修改失败！");
        } catch (Exception e) {
            logger.error("修改培训类型信息失败，异常信息：" + e);
            return R.error("修改培训类型信息失败." );
        }
    }

    /**
     * 删除培训类型
     */
    @OperLog(title = "培训类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("要删除的培训类型id不能为空！");
            }
            if (traTrainCategoryService.deleteTraTrainCategoryByIds(ids) > 0) {
                return R.ok("培训类型信息删除成功！");
            }
            return R.error("培训类型信息删除失败！");
        } catch (Exception e) {
            logger.error("删除培训类型信息失败，异常信息：" + e);
            return R.error("删除培训类型信息失败." );
        }
    }

    /**
     * 通过公司id获取课程分类信息
     */
    @GetMapping("/getTrainCategoryList/{ids}")
    public R getTrainCategoryList(@PathVariable Long[] ids){
        try{
            if (null == ids || ids.length < 1) {
                return R.error("要删除的培训类型id不能为空！");
            }
            return R.data(traTrainCategoryService.findTrainCategoryInfoByCompanyIds(Arrays.asList(ids)));
        }catch(Exception e){
            logger.error("发生异常");
            return R.error("发生异常");
        }
    }
}
