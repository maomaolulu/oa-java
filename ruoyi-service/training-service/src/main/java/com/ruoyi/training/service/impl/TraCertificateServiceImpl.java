package com.ruoyi.training.service.impl;

import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.training.entity.TraCertificate;
import com.ruoyi.training.entity.vo.CourseUserVO;
import com.ruoyi.training.mapper.TraCertificateMapper;
import com.ruoyi.training.service.TraCertificateService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 培训证书业务逻辑实现层
 *
 * @author hjy
 * @date 2022/6/21 16:58
 */
@Service
public class TraCertificateServiceImpl implements TraCertificateService {

    private final TraCertificateMapper certificateMapper;

    public TraCertificateServiceImpl(TraCertificateMapper certificateMapper) {
        this.certificateMapper = certificateMapper;
    }

    /**
     * 获取证书列表
     *
     * @param certificate 证书信息
     * @return 证书列表
     */
    @Override
    public List<TraCertificate> selectTraCertificateList(TraCertificate certificate) {
        if (certificate.getUserId() == null) {
            certificate.setUserId(SystemUtil.getUserId());
        }
        return certificateMapper.selectTraCertificateList(certificate);
    }

    /**
     * 获取证书详情
     *
     * @param id 证书id
     * @return 证书详情信息
     */
    @Override
    public TraCertificate getCertificateInfo(String id) {
        TraCertificate traCertificate = certificateMapper.selectTraCertificateById(id);
        //计算全部学时
        List<CourseUserVO> courseList = traCertificate.getCourseList();
        if (courseList != null) {
            double temp = 0;
            //计算总学时
            for (CourseUserVO course : courseList) {
                double courseHour = course.getCourseHour().doubleValue();
                temp = temp + courseHour;
            }
            traCertificate.setAllHour(new BigDecimal(temp));
        }
        return traCertificate;
    }

    /**
     * 查询该岗位已生成的证书
     *
     * @param postCode 岗位编码
     * @return 证书数量
     */
    @Override
    public int selectCertificateNum(String postCode) {
        return certificateMapper.selectCertificateNum(postCode);
    }

    /**
     * 生成证书
     *
     * @param certificate 证书信息
     * @return 状态
     */
    @Override
    public int insertCertificate(TraCertificate certificate) {
        return certificateMapper.insertCertificate(certificate);
    }
}
