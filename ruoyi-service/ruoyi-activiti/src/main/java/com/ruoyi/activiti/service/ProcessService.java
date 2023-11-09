package com.ruoyi.activiti.service;

import com.ruoyi.common.core.domain.R;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 流程信息
 *
 * @author zx
 * @date 2022/1/11 16:38
 */
public interface ProcessService {
    /**
     * 获取完整流程列表
     *
     * @param name
     * @param tableId
     * @param userId
     * @param money
     * @return
     */
    Map<String, Object> getProcessAll(String name, String tableId, Long userId, BigDecimal money, String variable, String procInstId);

    /**
     * 流程部署
     *
     * @return
     */
    R upload(MultipartFile file);

    /**
     * 查看流程图
     *
     * @param did
     * @param ext
     * @param httpServletResponse
     */
    void show(String did, String ext, HttpServletResponse httpServletResponse) throws IOException;

    /**
     * 挂起、激活流程实例
     *
     * @param state
     * @param processId
     * @return
     */
    R updateState(String state, String processId);

    /**
     * 实时高亮流程图
     *
     * @param procInstId
     * @param response
     */
    void getHighlightImg(String procInstId, HttpServletResponse response);

    /**
     * pdf导出流程
     *
     * @param business
     * @param tableId
     * @return
     */
    List<Map<String, Object>> getPdfProcessAll(String business, String tableId);

    /**
     * 流程撤回
     *
     * @param id     procInstId
     * @param reason 撤回原因
     */
    void revokeProcess(String id, String reason);

    /**
     * 同步其他系统审批状态
     *
     * @param procDefKey 流程默认key
     * @param status     审批状态
     * @param tableId     业务主键ID
     */
    void syncAuditStatus(String procDefKey, Integer status, String tableId);
}
