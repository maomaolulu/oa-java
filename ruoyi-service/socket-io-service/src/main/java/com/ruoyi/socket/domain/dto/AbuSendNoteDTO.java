package com.ruoyi.socket.domain.dto;

import lombok.Data;

/**
 * 发送留言
 *
 * @Author yrb
 * @Date 2023/4/7 13:59
 * @Version 1.0
 * @Description
 */
@Data
public class AbuSendNoteDTO {
    /**
     * 项目编号
     */
    private String projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目负责人手机号码
     */
    private String masterPhone;

    /**
     * 留言信息
     */
    private String note;

    /**
     * 市场人员手机号码
     */
    private String busiPhone;

    /**
     * 业务员（市场人员）
     */
    private String salesman;

    /**
     * 项目录入人
     */
    private String writer;
}
