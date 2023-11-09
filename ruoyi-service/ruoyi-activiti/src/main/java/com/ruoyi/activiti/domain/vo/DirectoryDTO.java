package com.ruoyi.activiti.domain.vo;

import lombok.Data;

/**
 * @author wuYang
 * @date 2022/10/19 9:45
 */
@Data
public class DirectoryDTO {
    private String id;

    private String parentId;

    private String name;

    private String type;
    private Integer level;
}
