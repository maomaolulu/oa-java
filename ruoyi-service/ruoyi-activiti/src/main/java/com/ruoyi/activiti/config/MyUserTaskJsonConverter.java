package com.ruoyi.activiti.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.model.*;
import org.activiti.editor.language.json.converter.UserTaskJsonConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MyUserTaskJsonConverter extends UserTaskJsonConverter {
    private final String assigneeType = "assigneeType";
    private final String elememt = "customProperties";
    /**
    * 将bpmn元素转换成json
    */
    @Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement) {
        super.convertElementToJson(propertiesNode, baseElement);
        UserTask userTask = (UserTask) baseElement;
        //解析
        Map<String, List<ExtensionElement>> customerProperties = userTask.getExtensionElements();
        if (!CollectionUtils.isEmpty(customerProperties) && customerProperties.containsKey(elememt)) {
            ExtensionElement e = customerProperties.get(elememt).get(0);
            Map<String, List<ExtensionAttribute>> attributes = e.getAttributes();
            attributes.forEach((key, attr) -> {
                for (ExtensionAttribute extensionAttribute : attr) {
                    setPropertyValue(extensionAttribute.getName(), extensionAttribute.getValue(), propertiesNode);
                }
            });
        }
    }
    
    /**
    * 将json转换成bpmn元素
    */
    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap) {
        FlowElement flowElement = super.convertJsonToElement(elementNode,
                modelNode, shapeMap);
        //解析新增属性的业务逻辑
        UserTask userTask = (UserTask) flowElement;

        ExtensionElement ee = new ExtensionElement();
        ee.setName("activiti:" + elememt);
        ee.setNamespacePrefix("activiti");
        Map<String, List<ExtensionAttribute>> attributes = new HashMap<>();
        // 这里就是bpmn里自定义的assigneeType
        String propertyAssigneeType = getPropertyValueAsString(assigneeType, elementNode);
        if (StringUtils.hasLength(propertyAssigneeType)) {
            ExtensionAttribute assigneeTypeAttr = new ExtensionAttribute();
            assigneeTypeAttr.setName(assigneeType);
            assigneeTypeAttr.setValue(propertyAssigneeType);
            List<ExtensionAttribute> assigneeTypeList = new ArrayList<>(1);
            assigneeTypeList.add(assigneeTypeAttr);
            attributes.put(assigneeType, assigneeTypeList);
        }
        ee.setAttributes(attributes);

        userTask.addExtensionElement(ee);
        return userTask;
    }
}