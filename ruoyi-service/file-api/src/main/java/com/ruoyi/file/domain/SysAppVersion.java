package com.ruoyi.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * app信息
 * @author zx
 * @date 2022-08-12 14:54:10
 */
@Data
@TableName("sys_app_version")
public class SysAppVersion implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 版本号
     */
    private String version;
    /**
     * 版本名称
     */
    private String versionName;
    /**
     * 文件名称
     */
    private String wgt;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 文件类型 1差量包2安卓安装包3苹果安装包
     */
    private String type;

}
