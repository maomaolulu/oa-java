package com.ruoyi.training.service;

import com.ruoyi.training.entity.TraCertificate;

import java.util.List;

/**
 * 证书service
 *
 * @author hjy
 */
public interface TraCertificateService {
    /**
     * 获取证书列表
     *
     * @param certificate 证书信息
     * @return 证书列表
     */
    List<TraCertificate> selectTraCertificateList(TraCertificate certificate);

    /**
     * 获取证书详情
     *
     * @param id 证书id
     * @return 证书详情信息
     */
    TraCertificate getCertificateInfo(String id);

    /**
     * 查询该岗位已生成的证书
     *
     * @param postCode 岗位编码
     * @return 证书数量
     */
    int selectCertificateNum(String postCode);

    /**
     * 生成证书
     *
     * @param certificate 证书信息
     * @return 状态
     */
    int insertCertificate(TraCertificate certificate);
}
