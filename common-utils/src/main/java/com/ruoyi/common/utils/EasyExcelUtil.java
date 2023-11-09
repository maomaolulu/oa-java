package com.ruoyi.common.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * excel工具类
 * @author zx
 * @date 2022-08-09 15:08:35
 */
@Slf4j
public class EasyExcelUtil {

    private EasyExcelUtil() {
    }

    /**
     * 创建对象的导出
     *
     * @param data
     * @param response
     * @throws IOException
     */
    public static void noModuleWriteByMap(NoModelWriteData data, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode(data.getFileName(), "UTF-8").replaceAll("\\+", "%20");
//            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream()).head(head(data.getHeadMap())).sheet(data.getFileName()).doWrite(dataList(data.getDataList(), data.getDataStrMap()));
        } catch (Exception e) {
            log.error("导出",e);
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = new HashMap<String, String>();
            map.put("code", "500");
            map.put("msg", "导出Excel失败" + e.getMessage());
            response.getWriter().println(JSON.toJSONString(map));
        }
    }
    /**
     * 创建对象的导出
     *
     * @param data
     * @param response
     * @throws IOException
     */
    public static void noModuleWriteByObject(NoModelWriteData data, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode(data.getFileName(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream()).head(head(data.getHeadMap())).sheet(data.getFileName()).doWrite(dataListFromObject(data.getObjList(), data.getDataStrMap()));
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = new HashMap<String, String>();
            map.put("code", "500");
            map.put("msg", "导出Excel失败" + e.getMessage());
            response.getWriter().println(JSON.toJSONString(map));
        }
    }

    /**
     * 创建对象的导出
     *
     * @param data 数据
     * @param clazz 对象类型
     * @param response
     * @param <T>
     * @throws IOException
     */
    public static  <T> void simpleWrite(SimpleWriteData data,Class<T> clazz, HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode(data.getFileName(), "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), clazz).sheet(data.getFileName()).doWrite(data.getDataList());
    }

    /**
     * 设置表头
     *
     * @param headMap
     * @return
     */
    private static List<List<String>> head(String[] headMap) {
        List<List<String>> list = new ArrayList<List<String>>();

        for (String head : headMap) {
            List<String> headList = new ArrayList<String>();
            headList.add(head);
            list.add(headList);
        }
        return list;
    }

    /**
     * 设置导出的数据内容
     *
     * @param dataList
     * @param dataStrMap
     * @return
     */
    private static List<List<String>> dataList(List<Map<String, Object>> dataList, String[] dataStrMap) {
        List<List<String>> list = new ArrayList<List<String>>();
        for (Map<String, Object> map : dataList) {
            List<String> data = new ArrayList<String>();
            for (int i = 0; i < dataStrMap.length; i++) {
                if(map.get(dataStrMap[i])==null){
                    data.add("");
                    continue;
                }
                data.add(map.get(dataStrMap[i]).toString());
            }
            list.add(data);
        }
        return list;
    }
    /**
     * 设置导出的数据内容
     *
     * @param dataList
     * @param dataStrMap
     * @return
     */
    private static List<List<String>> dataListFromObject(List<T> dataList, String[] dataStrMap) {
        List<List<String>> list = new ArrayList<List<String>>();
        for (T t : dataList) {
            Map map = JSON.parseObject(JSON.toJSONString(t), Map.class);
            List<String> data = new ArrayList<String>();
            for (int i = 0; i < dataStrMap.length; i++) {
                if(map.get(dataStrMap[i])==null){
                    data.add("");
                    continue;
                }
                data.add(map.get(dataStrMap[i]).toString());
            }
            list.add(data);
        }
        return list;
    }


}
