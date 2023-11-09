package com.ruoyi.file.service;

import com.ruoyi.file.domain.SysStaticImg;

import java.util.List;

/**
 * @author zx
 * @date 2022/1/7 17:52
 */
public interface SysStaticImgService {
    /**
     * 新增
     * @param sysStaticImg
     * @return
     */
    int insert(SysStaticImg sysStaticImg);

    /**
     * 获取静态文件列表
     * @return
     */
    List<SysStaticImg> getList();

    /**
     * 删除静态文件
     * @param types
     * @param path
     */
    void delete(String types, String path);
}
