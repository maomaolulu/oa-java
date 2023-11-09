package com.ruoyi.training.utils;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.training.entity.TraCertificate;
import com.ruoyi.training.entity.TraPostCertificate;
import com.ruoyi.training.entity.dto.SubmitDTO;
import com.ruoyi.training.service.TraCertificateService;
import com.ruoyi.training.service.TraPostCertificateService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Map;

/**
 * 考试证书生成-切面
 *
 * @author hjy
 * @date 2022/6/22 11:12
 *
 */
@Component
@Aspect
public class CertificateAspect {

    @Autowired
    private TraCertificateService certificateService;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private TraPostCertificateService postCertificateService;

    @Pointcut("execution(public * com.ruoyi.training.service.impl.TraQuestionInfoServiceImpl.submit(..))")
    public void pointCut() {
    }

    @AfterReturning(pointcut = "pointCut()", returning = "result")
    public void afterReturn(JoinPoint joinPoint, Map<String, Object> result) {
        //判断
        Integer status = (Integer) result.get("status");
        if (status == 2) {
            //获取参数并转换为对象
            SubmitDTO dto = (SubmitDTO) joinPoint.getArgs()[0];
            long examId = dto.getExamId();
            Long userId = SystemUtil.getUserId();
            //指定用户信息
            SysUser sysUser = remoteUserService.selectSysUserByUserId(userId);
            //判断新老员工
            Map<String, Integer> map = TrainingUtils.judgeStaffType(sysUser);
            Integer type = map.get("type");
            TraPostCertificate post = postCertificateService.selectTraPostCertificateByUserId(userId);
            //岗位编号
            String postCode = post.getPostCode();
            //已生成同类岗位证书数量
            int num = certificateService.selectCertificateNum(postCode);
            TraCertificate certificate = new TraCertificate();
            StringBuilder id = new StringBuilder();
            int year = Calendar.getInstance().get(Calendar.YEAR);
            id.append("AL").append(year).append(postCode).append(String.format("%03d", num + 1));
            //证书id
            certificate.setId(id.toString());
            //证书岗位
            certificate.setPostCode(postCode);
            //证书用户
            certificate.setUserId(userId);
            //用户姓名
            certificate.setUserName(sysUser.getUserName());
            //证书部门
            certificate.setDeptId(sysUser.getDeptId());
            //证书年份
            certificate.setTrainYear(year);
            //证书所绑定的考试
            certificate.setExamId(examId);
            if (type == 1) {
                certificate.setStartDate(DateUtils.parseDateToStr("yyyy-MM-dd", sysUser.getCreateTime()));
                certificate.setCertificateName("新上岗专业技术人员能力培训");
            } else {
                certificate.setStartDate(Calendar.getInstance().get(Calendar.YEAR) + "-01-01");
                certificate.setCertificateName("专业技术人员年度继续教育培训");
            }
            //证书类型
            certificate.setCertificateType(type);
            certificateService.insertCertificate(certificate);
        }

    }


}
