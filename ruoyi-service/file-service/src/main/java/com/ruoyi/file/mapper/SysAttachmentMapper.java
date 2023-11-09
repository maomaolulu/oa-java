package com.ruoyi.file.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.file.domain.SysAttachment;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 附件
 * @author zx
 * @date 2021/12/19 10:06
 */
@Repository
public interface SysAttachmentMapper extends BaseMapper<SysAttachment> {
    /**
     * 获取最新版本信息
     * @return
     */
    @Select("select * from sys_app_version order by id desc limit 1 ")
    Map<String,Object> getLastAppVersion();
}
