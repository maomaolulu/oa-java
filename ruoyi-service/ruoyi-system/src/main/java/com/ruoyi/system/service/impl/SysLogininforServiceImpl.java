package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.system.domain.SysLogininfor;
import com.ruoyi.system.mapper.SysLogininforMapper;
import com.ruoyi.system.service.ISysLogininforService;
import tk.mybatis.mapper.entity.Example;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author ruoyi
 */
@Service
public class SysLogininforServiceImpl implements ISysLogininforService {

    @Autowired
    private SysLogininforMapper logininforMapper;

    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    @Override
    public void insertLogininfor(SysLogininfor logininfor) {
        logininfor.setLoginTime(new Date());
        logininforMapper.insertSelective(logininfor);
    }

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor) {
        return logininforMapper.selectLogininforList(logininfor);
    }

    /**
     * 查询上次登录时间
     *
     * @param userId 访问日志对象
     * @return 登录记录对象
     */
    @Override
    public SysLogininfor selectLogininforLast(Long userId) {
        Example example = new Example(SysLogininfor.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("msg", "登录成功");
        example.setOrderByClause("login_time DESC");
        RowBounds rowBounds = new RowBounds(1, 1);
        List<SysLogininfor> sysLogininfors = logininforMapper.selectByExampleAndRowBounds(example, rowBounds);
        if (!sysLogininfors.isEmpty()) {
            return sysLogininfors.get(0);
        }
        return null;

    }

    /**
     * 查询最新登录时间(按设备)
     *
     * @param userId 访问日志对象
     * @return 登录记录对象
     */
    @Override
    public SysLogininfor selectLogininforLastByDevice(Long userId, String type) {
        Example example = new Example(SysLogininfor.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("msg", "登录成功");
        Set<String> set = new HashSet<>();
        set.add("Windows 10");
        set.add("Windows 7");
        set.add("Mac OS X");
        if ("pc".equals(type)) {
            criteria.andIn("os", set);
        } else if ("wx".equals(type)) {
            criteria.andNotIn("os", set);
        }
        example.setOrderByClause("login_time DESC");
        RowBounds rowBounds = new RowBounds(0, 1);
        List<SysLogininfor> sysLogininfors = logininforMapper.selectByExampleAndRowBounds(example, rowBounds);
        if (!sysLogininfors.isEmpty()) {
            return sysLogininfors.get(0);
        }
        return null;

    }

    /**
     * 批量删除系统登录日志
     *
     * @param ids 需要删除的数据
     * @return
     */
    @Override
    public int deleteLogininforByIds(String ids) {
        return logininforMapper.deleteLogininforByIds(Convert.toStrArray(ids));
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor() {
        logininforMapper.cleanLogininfor();
    }
}
