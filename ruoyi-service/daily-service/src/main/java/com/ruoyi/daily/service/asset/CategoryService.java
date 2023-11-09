package com.ruoyi.daily.service.asset;

import com.ruoyi.daily.domain.asset.category.Category;
import com.ruoyi.daily.domain.asset.dto.SaveCategoryDetileDTO;
import com.ruoyi.daily.domain.asset.category.dto.CategoryListDTO;
import com.ruoyi.daily.domain.asset.category.dto.EditCategoryDTO;
import com.ruoyi.daily.domain.asset.category.dto.SaveCategoryDTO;
import com.ruoyi.daily.domain.vw.ViewAsset;
import com.ruoyi.daily.domain.vw.ViewCategory;
import com.ruoyi.daily.domain.vw.ViewCategoryIdAndName;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Author 郝佳星
 * Date 2022/6/7 16:30
 **/
public interface CategoryService {
    /**
     * 查询品类列表
     * @return
     */
    List<Category> getCategoryLsit(CategoryListDTO categoryListDto);


    /**
     * 修改品类清单
     * @return
     */
    void editCategory(EditCategoryDTO editCategory);


    /**
     * 新增品类清单
     * @return
     */
    void saveCategory(SaveCategoryDTO saveCategoryDTO);

    /**
     * 查询详情
     * @return
     */
    List<ViewAsset>  getCategoryDetil(SaveCategoryDetileDTO saveCategoryDetilDTO);


    /**
     * 导出
     * @param categoryListDTO
     * @param response
     * @throws IOException
     */
    void download(CategoryListDTO categoryListDTO, HttpServletResponse response) throws IOException, ExecutionException, InterruptedException;


    /**
     * 品类列表id和名称查询
     * @param companyId
     */
    List<ViewCategoryIdAndName> getCategoryIdAndNameList(Long companyId);


}
