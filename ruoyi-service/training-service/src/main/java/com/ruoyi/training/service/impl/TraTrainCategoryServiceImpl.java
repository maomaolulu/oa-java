package com.ruoyi.training.service.impl;

import cn.hutool.core.exceptions.StatefulException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.file.feign.RemoteFileService;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.training.entity.TraTrainCategory;
import com.ruoyi.training.entity.vo.TraTrainCategoryVO;
import com.ruoyi.training.mapper.TraTrainCategoryMapper;
import com.ruoyi.training.service.ITraTrainCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 培训类型Service业务层处理
 *
 * @author yrb
 * @date 2022-06-06
 */
@Service
public class TraTrainCategoryServiceImpl implements ITraTrainCategoryService {
    private final TraTrainCategoryMapper traTrainCategoryMapper;

    private final RemoteFileService remoteFileService;

    private final RemoteFileService fileService;

    private final RemoteUserService remoteUserService;

    @Autowired
    public TraTrainCategoryServiceImpl(TraTrainCategoryMapper traTrainCategoryMapper, RemoteFileService fileService, RemoteFileService remoteFileService, RemoteUserService remoteUserService) {
        this.traTrainCategoryMapper = traTrainCategoryMapper;
        this.fileService = fileService;
        this.remoteFileService = remoteFileService;
        this.remoteUserService = remoteUserService;
    }

    /**
     * 查询培训类型
     *
     * @param id 培训类型主键
     * @return 培训类型
     */
    @Override
    public TraTrainCategory selectTraTrainCategoryById(Long id) {
        return traTrainCategoryMapper.selectTraTrainCategoryById(id);
    }

    /**
     * 查询培训类型列表
     *
     * @param traTrainCategory 培训类型
     * @return 培训类型
     */
    @Override
    public List<TraTrainCategory> selectTraTrainCategoryList(TraTrainCategory traTrainCategory) {
        //标记
        boolean flag = false;
        //搜索当前用户信息
        SysUser sysUser = remoteUserService.selectSysUserByUserId(SystemUtil.getUserId());
        //权限数据抽离
        for (SysRole role : sysUser.getRoles()) {
            if ("youwei_admin".equalsIgnoreCase(role.getRoleKey())) {
                flag = true;
                break;
            }
        }
        List<TraTrainCategory> list = null;
        //如果存在特殊角色
        if (flag) {
            List<String> userPermissions = sysUser.getUserPermissions();
            if (traTrainCategory.getCompanyId() == null) {
                if (StringUtils.isEmpty(userPermissions)) {
                    userPermissions.add(String.valueOf(SystemUtil.getCompanyId()));
                }
            } else {
                userPermissions.clear();
                userPermissions.add(String.valueOf(traTrainCategory.getCompanyId()));
            }
            list = traTrainCategoryMapper.selectMoreTrainCategoryList(userPermissions);
        } else {
            if (traTrainCategory.getCompanyId() == null) {
                traTrainCategory.setCompanyId(SystemUtil.getCompanyId());
            }
            list = traTrainCategoryMapper.selectTraTrainCategoryList(traTrainCategory);
        }
        //更新图片信息
        list.forEach(category -> {
            String training = fileService.getFileUrls(category.getTypes(), category.getCoverUrl());
//            category.setCoverUrl(training);
            category.setTempUrl(training);
        });
        return list;
    }

    /**
     * 新增培训类型
     *
     * @param traTrainCategory 培训类型
     * @return 结果
     */
    @Override
    public int insertTraTrainCategory(TraTrainCategory traTrainCategory) {
        traTrainCategory.setCreateTime(DateUtils.getNowDate());
        traTrainCategory.setCreateBy(SystemUtil.getUserName());
        if (traTrainCategory.getCompanyId() == null) {
            traTrainCategory.setCompanyId(SystemUtil.getCompanyId());
        }
        int status = remoteFileService.update(null, traTrainCategory.getTempId());
        if (status == 0) {
            throw new StatefulException("将上传的文件转为有效文件失败！");
        }
        return traTrainCategoryMapper.insertTraTrainCategory(traTrainCategory);
    }

    /**
     * 修改培训类型
     *
     * @param traTrainCategory 培训类型
     * @return 结果
     */
    @Override
    public int updateTraTrainCategory(TraTrainCategory traTrainCategory) {
        traTrainCategory.setUpdateTime(DateUtils.getNowDate());
        if (traTrainCategory.getCompanyId() == null) {
            traTrainCategory.setCompanyId(SystemUtil.getCompanyId());
        }
        TraTrainCategory category = traTrainCategoryMapper.selectTraTrainCategoryById(traTrainCategory.getId());
        //封面改变
        if (!category.getCoverUrl().equals(traTrainCategory.getCoverUrl())) {
            //持久化新封面
            int status = remoteFileService.update(null, traTrainCategory.getTempId());
            if (status == 0) {
                throw new StatefulException("将上传的文件转为有效文件失败！");
            }
            //删除旧的封面
            remoteFileService.delete(category.getTypes(), category.getCoverUrl());
        }
        return traTrainCategoryMapper.updateTraTrainCategory(traTrainCategory);
    }

    /**
     * 批量删除培训类型
     *
     * @param ids 需要删除的培训类型主键
     * @return 结果
     */
    @Override
    public int deleteTraTrainCategoryByIds(Long[] ids) {
        return traTrainCategoryMapper.deleteTraTrainCategoryByIds(ids);
    }

    /**
     * 删除培训类型信息
     *
     * @param id 培训类型主键
     * @return 结果
     */
    @Override
    public int deleteTraTrainCategoryById(Long id) {
        return traTrainCategoryMapper.deleteTraTrainCategoryById(id);
    }

    /**
     * 通过多个公司id查询课程分类信息
     *
     * @param companyIds -->公司id集合
     * @return 对应公司的课程分类信息
     */
    @Override
    public List<TraTrainCategoryVO> findTrainCategoryInfoByCompanyIds(List<Long> companyIds) {
        return traTrainCategoryMapper.selectTrainCategoryInfoByCompanyIds(companyIds);
    }
}
