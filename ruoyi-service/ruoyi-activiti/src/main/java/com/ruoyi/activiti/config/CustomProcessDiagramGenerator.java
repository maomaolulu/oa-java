//package com.ruoyi.activiti.config;
//
//import com.ruoyi.activiti.cover.CustomProcessDiagramCanvas;
//import com.ruoyi.activiti.cover.ICustomProcessDiagramGenerator;
//import org.activiti.bpmn.model.*;
//import org.activiti.bpmn.model.Process;
//import org.activiti.image.impl.DefaultProcessDiagramGenerator;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//
//import javax.imageio.ImageIO;
//import javax.imageio.stream.ImageOutputStream;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Collections;
//import java.util.List;
//import java.util.Set;
//
//@Component
//public class CustomProcessDiagramGenerator extends DefaultProcessDiagramGenerator implements ICustomProcessDiagramGenerator {
//    //预初始化流程图绘制，大大提升了系统启动后首次查看流程图的速度
//    static {
//        new CustomProcessDiagramCanvas(10,10,0,0,"png", "宋体","宋体","宋体",null);
//    }
//    @Override
//    public InputStream generateDiagram(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities, List<String> highLightedFlows, String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader, double scaleFactor, Color[] colors, Set<String> currIds) {
//        return null;
//    }
//}