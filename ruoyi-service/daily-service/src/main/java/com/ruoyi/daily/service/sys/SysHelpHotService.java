package com.ruoyi.daily.service.sys;

import com.ruoyi.daily.domain.sys.SysHelpHot;
import com.ruoyi.daily.domain.sys.SysHelpVersion;
import com.ruoyi.daily.domain.sys.dto.SysHelpHotDTO;
import com.ruoyi.daily.domain.sys.dto.SysHelpVersionDTO;

import java.util.List;

/**
 * 热点问题服务
 * Created by WuYang on 2022/8/18 10:08
 */
public interface SysHelpHotService {
    /**
     * 查询版本信息
     */
    List<SysHelpHot> getLists();

    /**
     * 通过id查版本信息
     * @param id 版本id
     */
    SysHelpHot get(Long id);

    /**
     * 删除版本信息
     * @param id 版本id
     */
    void delete(Long id);

    /**
     * 更新版本内容
     * @param version 版本内容
     */
    void update(SysHelpHotDTO version);

    /**
     * 新增版本
     * @param version 版本内容
     */
    void add(SysHelpHotDTO version);
}
