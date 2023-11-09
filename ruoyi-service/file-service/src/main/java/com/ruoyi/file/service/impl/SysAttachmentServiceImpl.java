package com.ruoyi.file.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.file.domain.SysAppVersion;
import com.ruoyi.file.mapper.SysAppVersionMapper;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.file.domain.SysAttachment;
import com.ruoyi.file.mapper.SysAttachmentMapper;
import com.ruoyi.file.service.SysAttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zx
 * @date 2021/12/19 10:07
 */
@Service
@Slf4j
public class SysAttachmentServiceImpl implements SysAttachmentService {
    @Autowired
    public RedisTemplate redisTemplate;
    @Resource(name = "stringRedisTemplate")
    private ValueOperations<String, String> valueOperations;
    private final SysAttachmentMapper attachmentMapper;
    private final SysAppVersionMapper sysAppVersionMapper;

    @Autowired
    public SysAttachmentServiceImpl(SysAttachmentMapper attachmentMapper, SysAppVersionMapper sysAppVersionMapper) {
        this.attachmentMapper = attachmentMapper;
        this.sysAppVersionMapper = sysAppVersionMapper;
    }

    /**
     * 保存附件信息
     *
     * @param attachment
     * @return
     */
    @Override
    public int save(SysAttachment attachment) {
        String userName = SystemUtil.getUserName();
        attachment.setCreatedBy(userName);
        attachment.setUpdatedBy(userName);
        attachment.setCreatedTime(new Date());
        attachment.setUpdatedTime(new Date());
        int insert = attachmentMapper.insert(attachment);
        return insert;
    }

    /**
     * 临时文件转有效文件
     *
     * @param tempId
     * @param parentId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Long parentId, String tempId) {
        try {
            String userName = SystemUtil.getUserName();
            QueryWrapper<SysAttachment> wrapper = new QueryWrapper<>();
            if (StrUtil.isBlank(tempId)) {
                wrapper.eq("parent_id", parentId);
            } else {

                wrapper.eq("temp_id", tempId);
            }
            wrapper.eq("del_flag", "2");

            List<SysAttachment> sysAttachments = attachmentMapper.selectList(wrapper);
            if (sysAttachments.isEmpty()) {
                return 1;
            }
            sysAttachments.stream().forEach(attachment -> {
                attachment.setUpdatedBy(userName);
                attachment.setUpdatedTime(new Date());
                attachment.setDelFlag("0");
                attachment.setParentId(parentId);
                attachmentMapper.updateById(attachment);
            });
            return 1;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }

    }

    /**
     * 临时文件转有效文件
     *
     * @param tempId
     * @param parentId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(String parentId, String tempId) {
        try {
            String userName = SystemUtil.getUserName();
            QueryWrapper<SysAttachment> wrapper = new QueryWrapper<>();
            if (StrUtil.isBlank(tempId)) {
                wrapper.eq("m_parent_id", parentId);
            } else {

                wrapper.eq("temp_id", tempId);
            }
            wrapper.eq("del_flag", "2");

            List<SysAttachment> sysAttachments = attachmentMapper.selectList(wrapper);
            if (sysAttachments.isEmpty()) {
                return 1;
            }
            sysAttachments.stream().forEach(attachment -> {
                attachment.setUpdatedBy(userName);
                attachment.setUpdatedTime(new Date());
                attachment.setDelFlag("0");
                attachment.setMParentId(parentId);
                attachmentMapper.updateById(attachment);
            });
            return 1;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }

    }

    /**
     * 临时文件转有效文件
     *
     * @param ids
     * @param types
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByIds( List<Long> ids,String types) {
        try {
            String userName = SystemUtil.getUserName();
            QueryWrapper<SysAttachment> wrapper = new QueryWrapper<>();
            wrapper.in("id", ids);

            wrapper.eq("del_flag", "2");

            List<SysAttachment> sysAttachments = attachmentMapper.selectList(wrapper);
            if (sysAttachments.isEmpty()) {
                return 1;
            }

            sysAttachments.stream().forEach(attachment -> {
                attachment.setUpdatedBy(userName);
                attachment.setUpdatedTime(new Date());
                attachment.setDelFlag("0");
                attachmentMapper.updateById(attachment);
            });
            return 1;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }

    }
    /**
     * 根据ids获取附件列表
     *
     * @param ids
     * @param types
     * @return
     */
    @Override
    public List<SysAttachment> getListByIds( List<Long> ids,String types) {
        QueryWrapper<SysAttachment> wrapper = new QueryWrapper<>();
        wrapper.eq("types", types);
        wrapper.in("id", ids);
        wrapper.eq("del_flag", "0");
        List<SysAttachment> sysAttachments = attachmentMapper.selectList(wrapper);
        return sysAttachments;
    }
    /**
     * 获取附件列表
     *
     * @param types
     * @param parentId
     * @return
     */
    @Override
    public List<SysAttachment> getListMongo(String types, String parentId) {
        QueryWrapper<SysAttachment> wrapper = new QueryWrapper<>();
        wrapper.eq("types", types);
        wrapper.eq("m_parent_id", parentId);
        wrapper.eq("del_flag", "0");
        List<SysAttachment> sysAttachments = attachmentMapper.selectList(wrapper);
        return sysAttachments;
    }
    /**
     * 获取附件列表
     *
     * @param types
     * @param parentId
     * @return
     */
    @Override
    public List<SysAttachment> getList(String types, Long parentId) {
        QueryWrapper<SysAttachment> wrapper = new QueryWrapper<>();
        wrapper.eq("types", types);
        wrapper.eq("parent_id", parentId);
        wrapper.eq("del_flag", "0");
        List<SysAttachment> sysAttachments = attachmentMapper.selectList(wrapper);
        return sysAttachments;
    }

    /**
     * 获取附件列表
     *
     * @param types
     * @param tempId
     * @return
     */
    @Override
    public List<SysAttachment> getListByTempId(String types, String tempId) {
        QueryWrapper<SysAttachment> wrapper = new QueryWrapper<>();
        wrapper.eq("types", types);
        wrapper.eq("temp_id", tempId);
        wrapper.eq("del_flag", "0");
        List<SysAttachment> sysAttachments = attachmentMapper.selectList(wrapper);
        return sysAttachments;
    }

    /**
     * 删除附件
     *
     * @param types
     * @param url
     */
    @Override
    public void delete(String types, String url) {
        QueryWrapper<SysAttachment> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(types), "types", types);
        wrapper.eq(StrUtil.isNotBlank(url), "url", url);
        attachmentMapper.delete(wrapper);
    }

    /**
     * 获取临时文件
     *
     * @return
     */
    @Override
    public List<SysAttachment> getTempList() {

        return attachmentMapper.selectList(new QueryWrapper<SysAttachment>().eq("del_flag", "2"));
    }

    /**
     * 获取app最新版本
     *
     * @return
     */
    @Override
    public SysAppVersion getLastAppVersion(int type) {
        QueryWrapper<SysAppVersion> wrapper = new QueryWrapper<>();
        if (type == 2) {
            wrapper.ne("type", 3);
        }
        if (type == 3) {
            wrapper.ne("type", 2);
        }
        wrapper.orderByDesc("id");
        wrapper.last("limit 1");
        List<SysAppVersion> sysAppVersions = sysAppVersionMapper.selectList(wrapper);
        if (!sysAppVersions.isEmpty()) {
            return sysAppVersions.get(0);
        } else {
            throw new RuntimeException("获取信息失败");
        }

    }

    /**
     * 获取最新app安装包
     *
     * @param type
     * @return
     */
    @Override
    public SysAppVersion getLastApp(int type) {
        QueryWrapper<SysAppVersion> wrapper = new QueryWrapper<>();
        wrapper.eq("type", type);
        wrapper.orderByDesc("id");
        wrapper.last("limit 1");
        List<SysAppVersion> sysAppVersions = sysAppVersionMapper.selectList(wrapper);
        if (!sysAppVersions.isEmpty()) {
            return sysAppVersions.get(0);
        } else {
            return null;
        }
    }

    /**
     * 保存计数器
     */
    @Override
    public Long counter() {

        if (valueOperations.get("report_click") == null) {
            valueOperations.set("report_click", "0");

        }
        long reportClick = valueOperations.increment("report_click",1);
        log.info("当前报告下载次数：{}", reportClick);
        return reportClick;
    }
}
