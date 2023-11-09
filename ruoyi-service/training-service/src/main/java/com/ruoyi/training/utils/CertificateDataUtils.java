package com.ruoyi.training.utils;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.training.entity.TraCertificate;
import com.ruoyi.training.entity.vo.CourseUserVO;

import java.math.BigDecimal;
import java.util.*;

/**
 * 证书数据工具类
 *
 * @author hjy
 * @date 2022/6/28 20:44
 */
public class CertificateDataUtils {

    public static final Integer RANGE = 6;

    public static HashMap<String, Object> packageData(TraCertificate certificate) {
        HashMap<String, Object> map = new HashMap<>(16);
        //用户名称
        map.put("n", certificate.getUserName());
        //证书编号
        map.put("c", certificate.getId());
        //证书名称
        map.put("cn", certificate.getCertificateName());
        //证书学时
        map.put("h", certificate.getAllHour());
        if (certificate.getCertificateType() == 1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DateUtils.dateTime("yyyy-MM-dd", certificate.getStartDate()));
            //新员工-起始日期
            map.put("sy", calendar.get(Calendar.YEAR));
            map.put("sm", calendar.get(Calendar.MONTH) + 1);
            map.put("sd", calendar.get(Calendar.DATE));
        } else {
            //老员工
            map.put("sy", certificate.getTrainYear());
            map.put("sm", 1);
            map.put("sd", 1);
        }
        //通过日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(certificate.getCreateTime());
        map.put("em", calendar.get(Calendar.MONTH) + 1);
        map.put("ed", calendar.get(Calendar.DATE));
        //证书关联已完成课程
        List<CourseUserVO> courseList = certificate.getCourseList();
        if (courseList.size() > RANGE) {
            //两页
            map.put("list1", courseList.subList(0, RANGE));
            map.put("list2", courseList.subList(RANGE, courseList.size()));
        } else {
            //一页
            map.put("list1", courseList);
        }
        return map;
    }

}
