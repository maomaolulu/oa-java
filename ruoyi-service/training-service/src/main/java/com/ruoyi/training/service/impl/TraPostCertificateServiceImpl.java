package com.ruoyi.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.training.entity.TraCourseInfo;
import com.ruoyi.training.entity.TraPostCertificate;
import com.ruoyi.training.entity.TraUserPost;
import com.ruoyi.training.entity.vo.SysUserVo;
import com.ruoyi.training.mapper.TraPostCertificateMapper;
import com.ruoyi.training.service.TraPostCertificateService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 证书岗位业务逻辑实现层implement
 *
 * @author hjy
 * @date 2022/6/21 13:47
 */
@Service
public class TraPostCertificateServiceImpl implements TraPostCertificateService {

    private final TraPostCertificateMapper postCertificateMapper;

    public TraPostCertificateServiceImpl(TraPostCertificateMapper postCertificateMapper) {
        this.postCertificateMapper = postCertificateMapper;
    }

    /**
     * 获取证书岗位列表
     *
     * @param postCertificate 岗位信息
     * @return 列表
     */
    @Override
    public List<TraPostCertificate> selectTraPostCertificateUserList(TraPostCertificate postCertificate) {
        if (postCertificate.getCompanyId() == null) {
            postCertificate.setCompanyId(SystemUtil.getCompanyId());
        }
        return postCertificateMapper.selectTraPostCertificateUserList(postCertificate);
    }

    /**
     * 新增证书岗位
     *
     * @param postCertificate 岗位相关信息
     * @return 状态
     */
    @Override
    public int insertTraPostCertificate(TraPostCertificate postCertificate) {
        if (postCertificate.getCompanyId() == null) {
            postCertificate.setCompanyId(SystemUtil.getCompanyId());
        }
        postCertificate.setStatus(0);
        postCertificate.setCreateBy(SystemUtil.getUserName());
        return postCertificateMapper.insertTraPostCertificate(postCertificate);
    }

    /**
     * 获取证书岗位详情
     *
     * @param id 岗位id
     * @return 岗位信息
     */
    @Override
    public TraPostCertificate selectTraPostCertificateById(Long id) {
        TraPostCertificate traPostCertificate = postCertificateMapper.selectTraPostCertificateById(id);
        String userIds = traPostCertificate.getUserIds();
        if (userIds != null) {
            traPostCertificate.setUserList(userIds.split(","));
        }
        return traPostCertificate;
    }

    /**
     * 更新岗位信息
     *
     * @param postCertificate 岗位信息
     * @return 状态
     */
    @Override
    public int updateTraPostCertificate(TraPostCertificate postCertificate) {
        postCertificate.setUpdateBy(SystemUtil.getUserName());
        return postCertificateMapper.updateTraPostCertificate(postCertificate);
    }

    /**
     * 删除岗位
     *
     * @param ids 岗位id集合
     * @return 状态
     */
    @Override
    public int deleteTraPostCertificateByIds(Long[] ids) {
        return postCertificateMapper.deleteTraPostCertificateByIds(ids);
    }

    /**
     * 岗位批量绑定用户
     *
     * @param postId  岗位id
     * @param userIds 用户id集合
     * @return 状态
     */
    @Override
    public int insertPostUsers(Long postId, String userIds) {
        Long[] users = Convert.toLongArray(userIds);
        //新增用户与岗位管理
        List<TraUserPost> list = new ArrayList<>();
        for (Long userId : users) {
            TraUserPost up = new TraUserPost();
            up.setUserId(userId);
            up.setPostId(postId);
            list.add(up);
        }
        return postCertificateMapper.batchUserPost(list);
    }

    /**
     * 岗位批量解除绑定用户
     *
     * @param userIds 用户id集合
     * @return 状态
     */
    @Override
    public int unbindPost(String userIds) {
        return postCertificateMapper.unbindPost(Convert.toLongArray(userIds));
    }

    /**
     * 通过用户id获取岗位信息
     *
     * @param userId 用户id
     * @return 岗位信息
     */
    @Override
    public TraPostCertificate selectTraPostCertificateByUserId(Long userId) {
        return postCertificateMapper.selectTraPostCertificateByUserId(userId);
    }

    /**
     * 获取人员列表
     *
     * @param companyId 公司id
     * @return 公司人员列表
     */
    @Override
    public List<SysUserVo> getUserList(Long companyId) {
        if (companyId == null) {
            companyId = SystemUtil.getCompanyId();
        }
        return postCertificateMapper.getUserList(companyId);
    }
    /**
     * 获取多公司人员列表
     *
     * @param companyIds 公司ids
     * @return 获取多公司人员列表
     */
    @Override
   public   Map<Long, List<SysUserVo>> getUserLists(List<Long> companyIds) {
        QueryWrapper<SysUserVo> wrapper = new QueryWrapper<>();
        wrapper.in("substring_index(substring_index(sd.ancestors,',',2),',',-1)",companyIds);
        wrapper.eq("su.status",0);
        wrapper.ne("su.user_id",1);

        List<SysUserVo> userLists = postCertificateMapper.getUserLists(wrapper);
        Map<Long, List<SysUserVo>> map = userLists.stream().collect(Collectors.groupingBy(SysUserVo::getCompanyId));
        return map;
    }
}
