package com.ruoyi.daily.service.sys;

import com.ruoyi.daily.domain.sys.SysHelpFaqInfo;
import com.ruoyi.daily.domain.sys.dto.SysHelpFaqAddDTO;
import com.ruoyi.daily.domain.sys.dto.SysHelpFaqInfoDTO;

/**
 * 帮助中心详情服务
 * Created by WuYang on 2022/8/18 10:08
 */
public interface SysHelpFaqInfoService {

    /**
     * 新增一条帮助中心条目
     */
    void add(SysHelpFaqInfo dto);
    /**
     * 编辑中新增条目
     */
    void editAdd(SysHelpFaqInfoDTO dto);
}
