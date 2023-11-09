package com.ruoyi.training.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.training.entity.TraPostCertificate;
import com.ruoyi.training.entity.TraUserPost;
import com.ruoyi.training.entity.vo.SysUserVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 证书岗位mapper
 *
 * @author hjy
 */
@Repository
public interface TraPostCertificateMapper {

    /**
     * 获取证书岗位列表
     *
     * @param postCertificate 岗位信息集合
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
     * @param list 数据集合
     * @return 状态
     */
    int batchUserPost(List<TraUserPost> list);

    /**
     * 岗位批量解除绑定用户
     *
     * @param userIds 用户id集合
     * @return 状态
     */
    int unbindPost(Long[] userIds);

    /**
     * 通过用户id获取岗位信息
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
     * 获取人员列表
     *
     * @param wrapper
     * @return 公司人员列表
     */
    @Select(" SELECT su.user_id,su.user_name,su.dept_id ,sd.dept_name, " +
            " substring_index(substring_index(sd.ancestors,',',2),',',-1) as companyId " +
            "FROM  sys_user su " +
            "LEFT JOIN sys_dept sd ON su.dept_id = sd.dept_id " +
            "${ew.customSqlSegment}   ")
    List<SysUserVo> getUserLists(@Param(Constants.WRAPPER) QueryWrapper wrapper);


}
