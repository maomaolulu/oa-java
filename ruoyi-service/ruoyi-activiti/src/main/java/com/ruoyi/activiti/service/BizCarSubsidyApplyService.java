package com.ruoyi.activiti.service;


import com.ruoyi.activiti.domain.car.BizCarSubsidyApply;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author zh
 * @date 2022-02-24
 * @desc 还车补贴申请
 */
public interface BizCarSubsidyApplyService {
    /**
     * 还车补贴申请数据分页
     * @param bizCarSubsidyApply
     * @return
     */
    List<BizCarSubsidyApply> listAllPage(BizCarSubsidyApply bizCarSubsidyApply);

    /**
     * 新增还车补贴
     * @param bizCarSubsidyApply
     * @return
     */
    BizCarSubsidyApply save(BizCarSubsidyApply bizCarSubsidyApply);
    /**
     * 修改还车补贴
     * @param bizCarSubsidyApply
     * @return
     */
    BizCarSubsidyApply update(BizCarSubsidyApply bizCarSubsidyApply);
    /**
     * 删除还车补贴
     * @param ids
     * @return
     */
    void delete(Long [] ids);

    /**
     * 查询详情
     * @param tableId
     * @return
     */
    BizCarSubsidyApply selectOne(Long tableId);
    /**
     * 导出
     * @param all
     * @param response
     * @throws IOException
     */
    void download(List<BizCarSubsidyApply> all, HttpServletResponse response) throws IOException ;

    /**
     * 导出特定格式的数据
     * @param all
     * @param response
     * @throws IOException
     */
    void export(List<BizCarSubsidyApply> all, HttpServletResponse response) throws IOException ;
}
