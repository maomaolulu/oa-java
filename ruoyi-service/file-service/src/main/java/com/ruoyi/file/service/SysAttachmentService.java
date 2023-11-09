package com.ruoyi.file.service;

import com.ruoyi.file.domain.SysAppVersion;
import com.ruoyi.file.domain.SysAttachment;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zx
 * @date 2021/12/19 10:07
 */
public interface SysAttachmentService {
    /**
     * 保存附件信息
     * @param attachment
     * @return
     */
    int save(SysAttachment attachment);

    /**
     * 临时文件转有效文件
     * @param parentId
     * @param tempId
     * @return
     */
    int update(Long parentId,String tempId);
    int update(String parentId,String tempId);
    /**
     * 临时文件转有效文件
     *
     * @param types
     * @param ids
     * @return
     */
    int updateByIds(List<Long> ids,String types );

    /**
     * 根据ids获取附件列表
     *
     * @param types
     * @param ids
     * @return
     */
    List<SysAttachment> getListByIds(List<Long> ids,String types);

    /**
     * 获取附件列表
     * @param types
     * @param parentId
     * @return
     */
    List<SysAttachment> getList(String types, Long parentId);
    /**
     * 获取附件列表
     * @param types
     * @param parentId
     * @return
     */
    List<SysAttachment> getListMongo(String types, String parentId);

    /**
     * 获取附件列表
     * @param types
     * @param tempId
     * @return
     */
    List<SysAttachment> getListByTempId(String types, String tempId);

    /**
     * 删除附件
     * @param types
     * @param path
     */
    void delete(String types, String path);

    /**
     * 获取临时文件
     * @return
     */
    List<SysAttachment> getTempList();

    /**
     * 获取app最新版本
     * @return
     */
    SysAppVersion getLastAppVersion(int type);
    /**
     * 获取最新app安装包
     * @return
     */
    SysAppVersion getLastApp(int type);

    /**
     * 保存计数器
     */
    Long counter();
}
