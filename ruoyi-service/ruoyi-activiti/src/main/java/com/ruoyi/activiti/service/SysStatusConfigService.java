package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.SysStatusConfig;

/**
 * @author zh
 * @date 2021-12-31
 * @desc 静态文件管理表
 */
public interface SysStatusConfigService {

    SysStatusConfig selectFielPathName();
    /**
     * 修改样式
     * */
    SysStatusConfig updateById(SysStatusConfig sysStatusConfig);

}
