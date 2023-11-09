package com.ruoyi.daily.controller.asset;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.daily.domain.asset.category.Category;
import com.ruoyi.daily.domain.asset.dto.SaveCategoryDetileDTO;
import com.ruoyi.daily.domain.asset.category.dto.CategoryListDTO;
import com.ruoyi.daily.domain.asset.category.dto.EditCategoryDTO;
import com.ruoyi.daily.domain.asset.category.dto.SaveCategoryDTO;
import com.ruoyi.daily.domain.vw.ViewCategory;
import com.ruoyi.daily.service.asset.CategoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 固定资产管理品类清单
 * @author zx
 * Date 2022/6/7 16:29
 **/
@RestController
@RequestMapping("category")
public class CategoryController extends BaseController {

    private final CategoryService categoryService;
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    /**
     * 品类清单列表
     * @param categoryListDto
     * @return
     */
    @GetMapping("list")
    public R categoryList(CategoryListDTO categoryListDto){
        try {
            pageUtil();
            return resultData(categoryService.getCategoryLsit(categoryListDto));
        }catch (Exception e){
            logger.error("获取品类列表失败",e);
            return R.error("获取品类列表失败");
        }
    }


    /**
     * 编辑品类清单
     */
    @PutMapping("info")
    @OperLog(title = "编辑品类清单",businessType = BusinessType.UPDATE)
    public R editCategory(@RequestBody EditCategoryDTO editCategory)
    {
        try {
            categoryService.editCategory(editCategory);
            return R.ok();
        }catch (Exception e){
            logger.error("修改品类清单失败",e);
            return R.error("修改品类清单失败");
        }
    }


    /**
     *  新增品类
     */
    @PostMapping ("info")
    @OperLog(title = "新增品类",businessType = BusinessType.INSERT)
    public R saveCategory(@RequestBody SaveCategoryDTO saveCategoryDTO)
    {
        try {
            categoryService.saveCategory(saveCategoryDTO);
            return R.ok();
        }catch (Exception e){
            logger.error("新增品类失败",e);
            return R.error("新增品类失败");
        }
    }

    /**
     *  详情集合接口
     */
    @GetMapping("detail")
    public R getCategoryDetil(SaveCategoryDetileDTO saveCategoryDetilDTO)
    {
        try {
            pageUtil();
            return resultData(categoryService.getCategoryDetil(saveCategoryDetilDTO));
        }catch (Exception e){
            logger.error("查询详情失败",e);
            return R.error("查询详情失败");
        }
    }


    /**
     *  品类列表id和名称查询
     */
    @GetMapping("getCategoryIdAndNameList")
    public R getCategoryIdAndNameList(Long companyId)
    {
        try {
            return R.data(categoryService.getCategoryIdAndNameList(companyId));
        }catch (Exception e){
            logger.error("查询失败",e);
            return R.error("查询失败");
        }
    }


    /**
     * 导出
     * @param response
     * @param categoryListDto
     */
    @ApiOperation("导出数据")
    @GetMapping(value = "download")
    public void download(HttpServletResponse response, CategoryListDTO categoryListDto){
        try {
            categoryService.download(categoryListDto, response);
        }catch  (Exception e) {
            logger.error("导出数据失败", e);
        }
    }




}
