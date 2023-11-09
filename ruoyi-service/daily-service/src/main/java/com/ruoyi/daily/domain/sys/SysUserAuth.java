package com.ruoyi.daily.domain.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

 /**
 * 权限管理;
 * @author : zx
 * @date : 2022-10-21
 */
@ApiModel(value = "权限管理",description = "")
@TableName("sys_user_auth")
@Data
public class SysUserAuth implements Serializable,Cloneable {
     /**
      * 用户id
      */
     @ApiModelProperty(name = "用户id", notes = "")
     private Long userId;
     /**
      * 角色id
      */
     @ApiModelProperty(name = "角色id", notes = "")
     private Long roleId;
     /**
      * 部门id
      */
     @ApiModelProperty(name = "部门id", notes = "")
     private Long deptId;
     /**
      * 创建人
      */
     @ApiModelProperty(name = "创建人", notes = "")
     private String createBy;
     /**
      * 创建时间
      */
     @ApiModelProperty(name = "创建时间", notes = "")
     private Date createTime;

 }
