package com.ruoyi.activiti.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author zx
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
//    private String userName = SystemUtil.getUserName();
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
//        this.strictInsertFill(metaObject, "createBy", String.class, userName);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
//        this.strictInsertFill(metaObject, "updateBy", String.class, userName);
    }
}
