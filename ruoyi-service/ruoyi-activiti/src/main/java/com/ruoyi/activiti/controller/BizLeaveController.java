package com.ruoyi.activiti.controller;

import com.google.common.collect.Maps;
import com.ruoyi.activiti.consts.ActivitiConstant;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.domain.proc.BizLeave;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.service.IBizLeaveService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.common.utils.RandomUtil;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 请假 提供者
 *
 * @author ruoyi
 * @date 2020-01-07
 */
@RestController
@RequestMapping("leave")
public class BizLeaveController extends BaseController {
    @Autowired
    private IBizLeaveService leaveService;

    @Autowired
    private IBizBusinessService bizBusinessService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private RedisUtils redis;

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    /**
     * 查询${tableComment}
     */
    @GetMapping("get/{id}")
    public BizLeave get(@PathVariable("id") String id) {
        return leaveService.selectBizLeaveById(id);
    }

    @GetMapping("biz/{businessKey}")
    public R biz(@PathVariable("businessKey") String businessKey) {

        List<Task> taskResultList = taskService.createTaskQuery().processInstanceId("").list();

////当前executionId
//        String currentExecutionId = taskResultList.get(0).getExecutionId();
////当前签署总数
//        String currentnum = StringUtils.defaultString(runtimeService.getVariable(currentExecutionId, "num").toString(), "0");
//        System.out.println("当前签署总数"+currentnum);
////签署数+1
//        runtimeService.setVariable(currentExecutionId, "num",Integer.parseInt(currentnum) + 1);


        BizBusiness business = bizBusinessService.selectBizBusinessById(businessKey);
        redis.set("cc" + business.getProcInstId(), "hello_world");
        System.out.println(redis.get("cc" + business.getProcInstId()));
        System.out.println(business.getProcInstId());
        runtimeService.deleteProcessInstance(business.getProcInstId(),"会签减签");
        if (null != business) {
            BizLeave leave = leaveService.selectBizLeaveById(business.getTableId());
            return R.data(leave);
        }
        return R.error("no record");
    }

    /**
     * 查询请假列表
     */
    @GetMapping("list")
    public R list(BizLeave leave) {
        startPage();
        return result(leaveService.selectBizLeaveList(leave));
    }

    /**
     * 新增保存请假
     */
    @PostMapping("save")
    public R addSave(@RequestBody BizLeave leave) {
        int index = leaveService.insertBizLeave(leave);
        if (index == 1) {
            BizBusiness business = initBusiness(leave);
            bizBusinessService.insertBizBusiness(business);
            Map<String, Object> variables = Maps.newHashMap();
            // 这里可以设置各个负责人，key跟模型的代理变量一致
            // variables.put("pm", 1l);
            // variables.put("sup", 1l);
            // variables.put("gm", 1l);
            // variables.put("hr", 1l);
            variables.put("duration", leave.getDuration());
            variables.put("num", 0);
            bizBusinessService.startProcess(business, variables);
            redis.set("cc:" + business.getProcInstId(), RandomUtil.randomStr(6));
        }
        return toAjax(index);
    }

    /**
     * @param leave
     * @return
     * @author zmr
     */
    private BizBusiness initBusiness(BizLeave leave) {
        BizBusiness business = new BizBusiness();
        business.setTableId(leave.getId().toString());
        business.setProcDefId(leave.getProcDefId());
        business.setTitle(leave.getTitle());
        business.setProcName(leave.getProcName());
        long userId = getCurrentUserId();
        business.setUserId(userId);
        SysUser user = remoteUserService.selectSysUserByUserId(userId);
        business.setApplyer(user.getUserName());
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        return business;
    }

    /**
     * 修改保存请假
     */
    @PostMapping("update")
    public R editSave(@RequestBody BizLeave leave) {
        return toAjax(leaveService.updateBizLeave(leave));
    }

    /**
     * 删除
     */
    @PostMapping("remove")
    public R remove(String ids) {
        return toAjax(leaveService.deleteBizLeaveByIds(ids));
    }

    @GetMapping("test")
    public Object getSql() {
        return leaveService.getSql();
    }
}
