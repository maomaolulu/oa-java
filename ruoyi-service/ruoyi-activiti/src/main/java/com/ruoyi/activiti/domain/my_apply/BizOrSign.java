package com.ruoyi.activiti.domain.my_apply;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

/**
 * @author wuYang
 * @date 2022/9/6 16:55
 */
@TableName("biz_or_sign")
@Data
public class BizOrSign {

    @ApiModelProperty("id")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("流程id")
    private String processKey;

    @ApiModelProperty("userId")
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BizOrSign bizOrSign = (BizOrSign) o;
        return processKey.equals(bizOrSign.getProcessKey()) && Objects.equals(userId, bizOrSign.getUserId());
    }

    @Override
    public int hashCode() {
        int result = processKey != null ? processKey.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }
}
