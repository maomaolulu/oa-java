package com.ruoyi.activiti.config;

import org.activiti.bpmn.model.UserTask;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;

public class CustomBpmnJsonConverter extends BpmnJsonConverter {

    public static void initCustomJsonConverter() {
        convertersToBpmnMap.put(STENCIL_TASK_USER, MyUserTaskJsonConverter.class);
        convertersToJsonMap.put(UserTask.class, MyUserTaskJsonConverter.class);
    }
}