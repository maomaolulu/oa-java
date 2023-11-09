package com.ruoyi.daily.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.ruoyi.daily.domain.asset.Asset;
import com.ruoyi.daily.domain.asset.dto.SaveCategoryDetileDTO;
import com.ruoyi.daily.domain.asset.category.Category;
import com.ruoyi.daily.domain.asset.category.dto.CategoryListDTO;
import com.ruoyi.daily.domain.asset.category.dto.EditCategoryDTO;
import com.ruoyi.daily.domain.asset.category.dto.SaveCategoryDTO;
import com.ruoyi.daily.domain.vw.ViewAsset;
import com.ruoyi.daily.domain.vw.ViewCategoryIdAndName;
import com.ruoyi.daily.mapper.category.ViewCategoryListMapper;
import com.ruoyi.daily.mapper.asset.AssetMapper;
import com.ruoyi.daily.mapper.category.CategoryMapper;
import com.ruoyi.daily.service.asset.CategoryService;
import com.ruoyi.daily.utils.FileUtil;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.DataScopeUtil;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Author 郝佳星
 * Date 2022/6/7 16:39
 **/
@Service
public class CategoryServiceImpl implements CategoryService {
    private final ViewCategoryListMapper viewCategoryListMapper;
    private final AssetMapper assetMapper;
    private final CategoryMapper categoryMapper;
    private final RemoteUserService remoteUserService;
    private final DataScopeUtil dataScopeUtil;

    @Autowired
    public CategoryServiceImpl(ViewCategoryListMapper viewCategoryListMapper, AssetMapper assetMapper, CategoryMapper categoryMapper, RemoteUserService remoteUserService, DataScopeUtil dataScopeUtil) {
        this.viewCategoryListMapper = viewCategoryListMapper;
        this.assetMapper = assetMapper;
        this.categoryMapper = categoryMapper;
        this.remoteUserService = remoteUserService;
        this.dataScopeUtil = dataScopeUtil;
    }


    @Override
    public List<Category> getCategoryLsit(CategoryListDTO categoryListDTO) {
        List<Category> categoryList = getCategories(categoryListDTO);
        List<Asset> assetList = new LambdaQueryChainWrapper<>(assetMapper).list();
        Map<Long, Long> collectNum = assetList.stream().filter(a -> a.getCategoryId() != null && a.getState().equals(10)).collect(groupingBy(Asset::getCategoryId, Collectors.counting()));
        Map<Long, Long> collectSum = assetList.stream().filter(a -> a.getCategoryId() != null && !a.getState().equals(6)&& !a.getState().equals(9)).collect(groupingBy(Asset::getCategoryId, Collectors.counting()));
        for (Category category : categoryList) {
            Long num = collectNum.get(category.getId());
            Long sum = collectSum.get(category.getId());
            category.setCategoryNum(num==null?0:num.intValue());
            category.setCategorySum(sum==null?0:sum.intValue());
        }

        return categoryList;
    }

    private List<Category> getCategories(CategoryListDTO categoryListDTO) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(categoryListDTO.getCategoryName()), "c.category_name", categoryListDTO.getCategoryName());
        wrapper.eq(categoryListDTO.getCompanyId() != null, "c.company_id", categoryListDTO.getCompanyId());
        wrapper.orderByDesc("c.create_time");
        SysUser user = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
        String sql = dataScopeUtil.getScopeSql(user, "d", null);
        if (StrUtil.isNotBlank(sql)) {
            wrapper.apply(sql);
        }
        List<Category> categoryList = categoryMapper.getCategoryList(wrapper);
        return categoryList;
    }


    @Override
    public void editCategory(EditCategoryDTO editCategoryDTO) {
        //修改品类
        String userName = SystemUtil.getUserNameCn();
        editCategoryDTO.setUpdateTime(new Date());
        editCategoryDTO.setUpdateBy(userName);
        Category category = new Category();
        BeanUtils.copyProperties(editCategoryDTO, category);
        categoryMapper.updateById(category);
    }


    @Override
    public void saveCategory(SaveCategoryDTO saveCategoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(saveCategoryDTO, category);
        category.setCreateTime(new Date());
        String userName = SystemUtil.getUserNameCn();
        category.setCreateBy(userName);
        categoryMapper.insert(category);
    }

    @Override
    public List<ViewAsset> getCategoryDetil(SaveCategoryDetileDTO saveCategoryDetilDTO) {
        QueryWrapper<Asset> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("a.company_id", saveCategoryDetilDTO.getCompanyId());
        queryWrapper.eq("a.category_id", saveCategoryDetilDTO.getId());
        queryWrapper.orderByDesc("a.create_time");
        List<ViewAsset> asset = assetMapper.getAsset(queryWrapper);
        return asset;
    }

    @Override
    public void download(CategoryListDTO categoryListDTO, HttpServletResponse response) throws IOException{
        // 多线程处理会乱序
        List<Category> all = new CopyOnWriteArrayList<>(getCategories(categoryListDTO)) ;
        List<Asset> assetList = new LambdaQueryChainWrapper<>(assetMapper).list();
        Map<Long, Long> collectNum = assetList.stream().filter(a -> a.getCategoryId() != null && a.getState().equals(10)).collect(groupingBy(Asset::getCategoryId, Collectors.counting()));
        Map<Long, Long> collectSum = assetList.stream().filter(a -> a.getCategoryId() != null && !a.getState().equals(6)&& !a.getState().equals(9)).collect(groupingBy(Asset::getCategoryId, Collectors.counting()));
        for (Category category : all) {
            Long num = collectNum.get(category.getId());
            Long sum = collectSum.get(category.getId());
            category.setCategoryNum(num==null?0:num.intValue());
            category.setCategorySum(sum==null?0:sum.intValue());
        }
        // 原集合
        List<Category> sortedCategories = getCategories(categoryListDTO);
        List<Map<String, Object>> list = new ArrayList<>(all.size());
//        // 并行流处理会乱序
//        all.parallelStream().forEach(category -> {
//            category.setCategoryNum(new LambdaQueryChainWrapper<>(assetMapper).eq(Asset::getState, 2).eq(Asset::getCompanyId, category.getCompanyId()).eq(Asset::getCategoryId, category.getId()).count());
//            category.setCategorySum(new LambdaQueryChainWrapper<>(assetMapper).ne(Asset::getState, 6).eq(Asset::getCompanyId, category.getCompanyId()).eq(Asset::getCategoryId, category.getId()).count());
//        });
        // 按原集合顺序导出
        sortedCategories.stream().forEach(sc->
            all.stream().forEach(nsc->{
                if(sc.getId().equals(nsc.getId())){
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("公司", nsc.getDeptName());
                    map.put("品类名称", nsc.getCategoryName());
                    map.put("资产总数", nsc.getCategorySum());
                    map.put("库存数量", nsc.getCategoryNum());
                    map.put("单位",nsc.getUnit());
                    map.put("创建人", nsc.getCreateBy());
                    map.put("创建时间", DateUtil.format(nsc.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                    map.put("维护人", nsc.getUpdateBy());
                    map.put("维护时间",DateUtil.format(nsc.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
                    list.add(map);
                }
            })
        );

        FileUtil.downloadExcel(list, response,"品类清单"+SystemUtil.getUserNameCn()+DateUtil.format(new Date(),"yyyyMMddHHmmss"));
    }

    @Override
    public List<ViewCategoryIdAndName> getCategoryIdAndNameList(Long companyId) {
        List<ViewCategoryIdAndName> categoryIdAndNameList = viewCategoryListMapper.getCategoryIdAndNameList(companyId);
        return categoryIdAndNameList;
    }
}
