package com.ruoyi.activiti.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.activiti.domain.ActComment;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.mapper.ActCommentMapper;
import com.ruoyi.activiti.service.ActCommentService;
import com.ruoyi.activiti.service.IBizAuditService;
import com.ruoyi.activiti.service.MapInfoService;
import com.ruoyi.activiti.utils.MailService;
import com.ruoyi.activiti.vo.HiTaskVo;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zx
 * @date 2022/2/21 14:57
 */
@Service
public class ActCommentImpl implements ActCommentService {
    private final ActCommentMapper actCommentMapper;
    private final RemoteUserService remoteUserService;
    private final IBizAuditService auditService;
    private final MailService mailService;
    private final MapInfoService mapInfoService;

    @Autowired
    public ActCommentImpl(ActCommentMapper actCommentMapper, RemoteUserService remoteUserService, IBizAuditService auditService, MailService mailService, MapInfoService mapInfoService) {
        this.actCommentMapper = actCommentMapper;
        this.remoteUserService = remoteUserService;
        this.auditService = auditService;
        this.mailService = mailService;
        this.mapInfoService = mapInfoService;
    }

    /**
     * 新增评论
     *
     * @param actComment
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActComment save(ActComment actComment) {
        try {

            actComment.setCreateTime(new Date());
            actComment.setCreateBy(SystemUtil.getUserId().toString());
            int insert = actCommentMapper.insert(actComment);
            ActComment actComment1 = actCommentMapper.selectById(actComment.getId());
            if (insert == 1) {
                SysUser sysUser = remoteUserService.selectSysUserByUserId(actComment.getUserId());
                actComment1.setUserName(sysUser.getUserName());
                QueryWrapper<BizBusiness> wrapper = new QueryWrapper<>();
                wrapper.eq("proc_inst_id", actComment1.getProcInstId());
                BizBusiness business = actCommentMapper.selectBusiness(wrapper);
                SysUser sysUser1 = remoteUserService.selectSysUserByUserId(business.getUserId());
                // 发送邮箱
                String mapInfoMail = mapInfoService.getMapInfoMail(business);
                String txt = "<body>" +
                        "<p>" +
                        "您的申请在" + DateUtil.format(actComment1.getCreateTime(), "yyyy年MM月dd日 HH时mm分") + "被<strong style='color:#2d8ccc;'>" + sysUser.getUserName() + "</strong>评论了。" +
                        "</p>" +
                        "<p>" +
                        "<strong>评论内容：<strong>" + actComment1.getComment() +
                        "</p><br>" + mapInfoMail + "<br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
                if (StrUtil.isNotBlank(sysUser1.getEmail())) {
                    mailService.send(txt, business.getTitle(), sysUser1.getEmail(), sysUser.getUserName(), sysUser1.getCid());
                }
                // 获取已审批的所有相关人员
                HiTaskVo hiTaskVo = new HiTaskVo();
                hiTaskVo.setProcInstId(actComment1.getProcInstId());
                List<HiTaskVo> historyTaskList = auditService.getHistoryTaskList(hiTaskVo);
                Set<Long> users = new HashSet<>();
                for (HiTaskVo taskVo : historyTaskList) {
                    if (taskVo.getAuditorId() == null) {
                        continue;
                    }
                    users.add(taskVo.getAuditorId());
//                    users.add(Long.valueOf(taskVo.getStartUserId()));
                }
                BizBusiness bizBusiness = new BizBusiness();
                bizBusiness.setProcInstId(actComment1.getProcInstId());
//                String title = businessService.selectBizBusinessList(bizBusiness).get(0).getTitle();
                String txt2 = "<body>" +
                        "<p>" +
                        business.getTitle() + "在" + DateUtil.format(actComment1.getCreateTime(), "yyyy年MM月dd日 HH时mm分") + "被<strong style='color:#2d8ccc;'>" + sysUser.getUserName() + "</strong>评论了。" +
                        "</p>" +
                        "<p>" +
                        "<strong>评论内容：<strong>" + actComment1.getComment() +
                        "</p><br>" + mapInfoMail + "<br><h4 style='color:#aaaaaa;'><strong>提示：更多数据，请前往云管家平台查看</strong></h4></body>";
                for (Long user : users) {
                    SysUser user2 = remoteUserService.selectSysUserByUserId(user);
                    if (StrUtil.isNotBlank(user2.getEmail())) {
                        mailService.send(txt2, business.getTitle(), user2.getEmail(), sysUser.getUserName(), user2.getCid());
                        System.out.println("发送邮件给" + user2.getUserName());
                    }
//                    if(null==sysUser1.getWxCode()){
//                        continue;
//                    }
//                    SubscriptionUtil.commentSend(sysUser1.getWxCode(),title, actComment1.getComment(), sysUser.getUserName(), DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                }

            }
            return actComment1;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    /**
     * 删除评论
     *
     * @param id
     * @return
     */
    @Override
    public ActComment delete(Long id) {
        Long userId = SystemUtil.getUserId();
        int result = 0;
        ActComment actComment = actCommentMapper.selectById(id);
        if (userId.equals(actComment.getUserId())) {
            actComment.setDelFlag("1");
            actComment.setUpdateBy(userId.toString());
            actComment.setUpdateTime(new Date());
            result = actCommentMapper.updateById(actComment);
        }
        if (result == 1) {
            SysUser sysUser = remoteUserService.selectSysUserByUserId(actComment.getUserId());
            actComment.setUserName(sysUser.getUserName());
            actComment.setComment("该评论已撤回");
            return actComment;
        }
        return null;
    }

    /**
     * 统计
     *
     * @param procInstId
     * @return
     */
    @Override
    public int count(String procInstId) {
        QueryWrapper<ActComment> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag", "0");
        wrapper.eq("proc_inst_id", procInstId);
        int integer = actCommentMapper.selectCount(wrapper);
        return integer;
    }

    /**
     * 获取评论列表
     *
     * @param procInstId
     * @return
     */
    @Override
    public List<ActComment> list(String procInstId) {
        QueryWrapper<ActComment> wrapper = new QueryWrapper<>();
        wrapper.eq("ac.proc_inst_id", procInstId);
        wrapper.orderByDesc("ac.create_time");
        List<ActComment> list = actCommentMapper.selectComment(wrapper);
        return list;
    }
}
