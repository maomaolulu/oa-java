package com.ruoyi.activiti.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.ruoyi.activiti.config.CustomBpmnJsonConverter;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pagehelper.PageHelper;
import com.ruoyi.activiti.domain.proc.ActReModel;
import com.ruoyi.activiti.service.IActReModelService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;

/**
 * 模型管理
 *
 * @menu 模型管理
 */
@RestController
@RequestMapping("models")
public class ModelerController extends BaseController {
    @Autowired
    RepositoryService repositoryService;

    @Autowired
    ObjectMapper objectMapper;


    @Autowired
    private IActReModelService modelService;

    /**
     * 新建一个空模型
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("newModel")
    @OperLog(title = "新建模型", businessType = BusinessType.INSERT)
    public Object newModel() throws UnsupportedEncodingException {
        // 初始化一个空模型
        Model model = repositoryService.newModel();
        // 设置一些默认信息
        String name = "new-process";
        String description = "";
        int revision = 1;
        String key = "process";
        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);
        model.setName(name);
        model.setKey(key);
        model.setMetaInfo(modelNode.toString());
        repositoryService.saveModel(model);
        String id = model.getId();
        // 完善ModelEditorSource
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.replace("stencilset", stencilSetNode);
        repositoryService.addModelEditorSource(id, editorNode.toString().getBytes("utf-8"));
        return  id;
    }

    /**
     * 发布模型为流程定义
     *
     * @param id
     * @return
     * @throws Exception
     */
    @PostMapping("deploy/{id}")
    @ResponseBody
    @OperLog(title = "模型发布", businessType = BusinessType.OTHER)
    public R deploy(@PathVariable("id") String id) throws Exception {
        // 获取模型
        Model modelData = repositoryService.getModel(id);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());
        if (bytes == null) {
            return R.error("模型数据为空，请先设计流程并成功保存，再进行发布。");
        }
        JsonNode modelNode = new ObjectMapper().readTree(bytes);
        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        if (model.getProcesses().size() == 0) {
            return R.error("数据模型不符要求，请至少设计一条主线流程。");
        }
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
        // 发布流程
        String processName = modelData.getName() + ".bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment().name(modelData.getName())
                .addString(processName, new String(bpmnBytes, "UTF-8")).deploy();
        modelData.setDeploymentId(deployment.getId());
        repositoryService.saveModel(modelData);
        return R.ok();
    }


    @GetMapping("get/{id}")
    public R get(@PathVariable("id") String id) {
        Model model = repositoryService.createModelQuery().modelId(id).singleResult();
        return R.data(model);
    }

    @GetMapping("/getById/{modelId}")
    public R getBpmnXmnById(@PathVariable("modelId") String modelId) {
        Model modelData = repositoryService.getModel(modelId);
        if (modelData != null) {
            try {
                byte[] modelEditorSource = repositoryService.getModelEditorSource(modelData.getId());
                ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(modelEditorSource);
                CustomBpmnJsonConverter converter = new CustomBpmnJsonConverter();
                // 转model
                BpmnModel model = converter.convertToBpmnModel(modelNode);
                // 转xml
                byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
                return R.data(new String(bpmnBytes));
            } catch (IOException e) {
                e.printStackTrace();
                return R.error("获取失败");
            }
        }
        return R.error("不存在该模型");
    }

    @GetMapping("list")
    @ResponseBody
    public R getList(ActReModel actReModel) {
        startPage();
        PageHelper.orderBy("create_time_ desc");
        return result(modelService.selectActReModelList(actReModel));
    }

    @PostMapping("remove")
    @ResponseBody
    @OperLog(title = "删除模型", businessType = BusinessType.DELETE)
    public R deleteOne(String ids) {
        String[] idsArr = ids.split(",");
        for (String id : idsArr) {
            repositoryService.deleteModel(id);
        }
        return R.ok();
    }
}
