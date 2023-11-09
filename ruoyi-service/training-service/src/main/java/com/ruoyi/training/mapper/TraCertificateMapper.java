package com.ruoyi.training.mapper;

import com.ruoyi.training.entity.TraCertificate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 证书mapper
 *
 * @author hjy
 */
@Repository
public interface TraCertificateMapper {
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
    TraCertificate selectTraCertificateById(String id);

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
