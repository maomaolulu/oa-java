package com.ruoyi.activiti.listener;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.activiti.service.IBizBusinessService;
import com.ruoyi.activiti.utils.SpringBeanUtil;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * process流程实例监听类
 */
@Component
@Slf4j
public class MyProcessExecutionListener implements ExecutionListener {
    private static final long serialVersionUID = 6173579003703470015L;

    @Override
    public void notify(DelegateExecution execution) {
        String eventName = execution.getEventName();
        String procInstId = execution.getProcessInstanceId();
        String businessKey = execution.getProcessBusinessKey();
        // start
        if ("start".equals(eventName)) {
            log.info("流程实例[{}]启动", procInstId);
        } else if ("end".equals(eventName)) {
            // 审批通过后的进行的操作
            if (execution.getVariableInstances() != null && execution.getVariableInstances().get("result") != null) {
                if ("2".equals(execution.getVariableInstances().get("result").getTextValue())) {
                    IBizBusinessService bean = SpringBeanUtil.getBean(IBizBusinessService.class);
                    String operator = "";
                    if (StrUtil.isNotEmpty(execution.getVariableInstances().get("operator").getTextValue())) {
                        operator = execution.getVariableInstances().get("operator").getTextValue();
                    }
//                    bean.getBusiness(businessKey, operator);
                    log.error("operator:" + operator);
                    if (execution.getVariableInstances().get("cc") != null) {
                        String cc = execution.getVariableInstances().get("cc").getTextValue();
                        System.out.println(cc);
                        List<String> ccList = new ArrayList<>();
                        if (StrUtil.isNotBlank(cc)) {
                            ccList = Arrays.asList(cc.split(","));
                        }
                        bean.ccEmail(businessKey, ccList, operator);
                    }
                }
            }
            log.info("流程实例[{}]结束,businessKey==[{}]，procInstId=[{}]", procInstId, businessKey, procInstId);
        }
    }
}
