package com.ruoyi.activiti.domain.dto;

import lombok.Data;

/**
 * Author 郝佳星
 * Date 2022/6/10 12:03
 **/
@Data
public class CancelUseCarDTO {
    //申请id
    private Long id;

    /**申请人名称 */
    private String createByName;

    /**取消用车1  提前还车2  延迟还车3*/
    private Integer state;

}
