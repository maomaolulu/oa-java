package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.asset.ScrappedDto;
import com.ruoyi.activiti.domain.dto.NeedManageDto;
import com.ruoyi.activiti.domain.proc.BizBusiness;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>File：IBizBusinessService.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2020 2020年1月6日 下午3:38:40</p>
 * <p>Company: zmrit.com </p>
 * @author zmr
 * @version 1.0
 */
public interface IBizBusinessService
{
    /**
     * 查询流程业务
     *
     * @param id 流程业务ID
     * @return 流程业务
     */
    public BizBusiness selectBizBusinessById(String id);

    /**
     * 查询流程业务（审批时专用）
     *
     * @param id 流程业务ID
     * @return 流程业务
     */
    public BizBusiness selectBizBusinessById2(String id);

    /**
     * 查询流程业务列表
     *
     * @param bizBusiness 流程业务
     * @return 流程业务集合
     */
    public List<BizBusiness> selectBizBusinessList(BizBusiness bizBusiness);



    /**
     * 查询流程业务列表
     *
     * @param bizBusiness 流程业务
     * @return 流程业务集合
     */
    public List<BizBusiness> selectBizBusinessListForRelate(BizBusiness bizBusiness);

    /**
     * 查询流程业务列表(流程图小程序)
     *
     * @param bizBusiness 流程业务
     * @return 流程业务集合
     */
    public List<BizBusiness> selectBizBusinessListAll(BizBusiness bizBusiness);

    /**
     * 查询流程业务列表(流程监控)
     *
     * @param bizBusiness 流程业务
     * @return 流程业务集合
     */
    public List<BizBusiness> selectBizBusinessListAll2(BizBusiness bizBusiness);

    /**
     * 新增流程业务
     *
     * @param bizBusiness 流程业务
     * @return 结果
     */
    public int insertBizBusiness(BizBusiness bizBusiness);

    /**
     * 修改流程业务
     *
     * @param bizBusiness 流程业务
     * @return 结果
     */
    public int updateBizBusiness(BizBusiness bizBusiness);

    /**
     * 批量删除流程业务
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBizBusinessByIds(String ids);

    /**
     * logic remove 逻辑删除
     *
     * @param ids
     * @return
     * @author zmr
     */
    public int deleteBizBusinessLogic(String ids);

    /**
     * 删除流程业务信息
     *
     * @param id 流程业务ID
     * @return 结果
     */
    public int deleteBizBusinessById(Long id);

    /**
     * start 启动流程
     *
     * @param business 业务对象，必须包含id,title,userId,procDefId属性
     * @param variables 启动流程需要的变量
     * @author zmr
     */
    Set<String> startProcess(BizBusiness business, Map<String, Object> variables);

    /**
     * check 检查负责人
     *
     * @param business 业务对象，必须包含id,procInstId属性
     * @param result 审批结果
     * @param currentUserId 当前操作用户 可能是发起人或者任务处理人
     * @return
     * @author zmr
     */


    Set<String> setAuditor(BizBusiness business, int result, long currentUserId);

    public Set<String> setInstanceUsers(BizBusiness business, int result, long currentUserId);

    /**
     * 获取业务信息
     * @param businessKey
     */
//    void getBusiness(String businessKey,String operator);

    /**
     * 给抄送人发邮件
     * @param businessKey
     * @param ccList
     */
    void ccEmail(String businessKey, List<String> ccList, String operator);

    /**
     * 流程统计分析1
     * @param bizBusiness
     * @return
     */
    List<Map<String, Object>> selectCount1(BizBusiness bizBusiness);

    /**
     * 流程统计分析2
     * @param bizBusiness
     * @return
     */
    List<Map<String, Object>> selectCount2(BizBusiness bizBusiness);

    /**
     * 查询需求
     * @param needManageDto
     * @return
     */
    List<NeedManageDto> getNeed(NeedManageDto needManageDto);

    /**
     * 临时抄送转正式
     * @param business
     */
    void changeCC(BizBusiness business);

    /**
     * 查询我的转交
     * @param bizBusiness
     * @return
     */
    List<BizBusiness> selectMyReassignment(BizBusiness bizBusiness);

    /**
     * 通过资产id 查询流程
     *
     * @param id 资产id
     * @return 流程业务
     */
    BizBusiness selectBizBusinessByAssetId(Long id);
    /**
     * 通过物品id 查询流程
     *
     * @param goodId 物品id
     * @return 流程业务
     */
    BizBusiness selectBizBusinessByGoodId(Long goodId);

    /**
     * 报废申请列表
     * @param scrappedDto
     * @return
     */
    List<BizBusiness> selectScrappedList(ScrappedDto scrappedDto);

    void getBusiness2(BizBusiness business);
}
