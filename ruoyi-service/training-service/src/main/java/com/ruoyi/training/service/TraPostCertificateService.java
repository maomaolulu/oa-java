package com.ruoyi.training.service;

import com.ruoyi.training.entity.TraCourseInfo;
import com.ruoyi.training.entity.TraPostCertificate;
import com.ruoyi.training.entity.vo.SysUserVo;

import java.util.List;
import java.util.Map;

/**
 * 证书岗位service接口
 *
 * @author hjy
 */
public interface TraPostCertificateService {
    /**
     * 获取证书岗位列表
     *
     * @param postCertificate 岗位信息
     * @return 列表
     */
    List<TraPostCertificate> selectTraPostCertificateUserList(TraPostCertificate postCertificate);

    /**
     * 新增证书岗位
     *
     * @param postCertificate 岗位相关信息
     * @return 状态
     */
    int insertTraPostCertificate(TraPostCertificate postCertificate);

    /**
     * 获取证书岗位详情
     *
     * @param id 岗位id
     * @return 岗位信息
     */
    TraPostCertificate selectTraPostCertificateById(Long id);

    /**
     * 更新岗位信息
     *
     * @param postCertificate 岗位信息
     * @return 状态
     */
    int updateTraPostCertificate(TraPostCertificate postCertificate);

    /**
     * 删除岗位
     *
     * @param ids 岗位id集合
     * @return 状态
     */
    int deleteTraPostCertificateByIds(Long[] ids);

    /**
     * 岗位批量绑定用户
     *
     * @param postId  岗位id
     * @param userIds 用户id集合
     * @return 状态
     */
    int insertPostUsers(Long postId, String userIds);

    /**
     * 岗位批量解除绑定用户
     *
     * @param userIds 用户id集合
     * @return 状态
     */
    int unbindPost(String userIds);

    /**
     * 获取岗位信息 通过用户id
     *
     * @param userId 用户id
     * @return 岗位信息
     */
    TraPostCertificate selectTraPostCertificateByUserId(Long userId);

    /**
     * 获取人员列表
     *
     * @param companyId 公司id
     * @return 公司人员列表
     */
    List<SysUserVo> getUserList(Long companyId);

    /**
     * 获取多公司人员列表
     *
     * @param companyIds 公司ids
     * @return 获取多公司人员列表
     */
    Map<Long, List<SysUserVo>> getUserLists(List<Long> companyIds);
}
