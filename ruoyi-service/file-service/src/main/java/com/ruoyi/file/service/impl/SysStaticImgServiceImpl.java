package com.ruoyi.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.file.domain.SysAttachment;
import com.ruoyi.file.domain.SysStaticImg;
import com.ruoyi.file.mapper.SysStaticImgMapper;
import com.ruoyi.file.service.SysStaticImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zx
 * @date 2022/1/7 17:52
 */
@Service
public class SysStaticImgServiceImpl implements SysStaticImgService {
    private final SysStaticImgMapper sysStaticImgMapper;
    @Autowired
    public SysStaticImgServiceImpl(SysStaticImgMapper sysStaticImgMapper) {
        this.sysStaticImgMapper = sysStaticImgMapper;
    }

    @Override
    public int insert(SysStaticImg sysStaticImg) {
        return sysStaticImgMapper.insert(sysStaticImg);
    }

    /**
     * 获取静态文件列表
     *
     * @return
     */
    @Override
    public List<SysStaticImg> getList() {
        return sysStaticImgMapper.selectList(null);
    }

    /**
     * 删除静态文件
     *
     * @param types
     * @param path
     */
    @Override
    public void delete(String types, String path) {
        QueryWrapper<SysStaticImg> wrapper = new QueryWrapper<>();
        wrapper.eq("types", types);
        wrapper.eq("path", path);
        sysStaticImgMapper.delete(wrapper);
    }
}
