package com.ruoyi.system.service;

import java.util.Date;
import java.util.List;
import com.ruoyi.system.domain.SysLogininfor;

/**
 * 系统访问日志情况信息 服务层
 * 
 * @author ruoyi
 */
public interface ISysLogininforService
{
    /**
     * 新增系统登录日志
     * 
     * @param logininfor 访问日志对象
     */
    public void insertLogininfor(SysLogininfor logininfor);

    /**
     * 查询系统登录日志集合
     * 
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor);

    /**
     * 查询上次登录时间
     *
     * @param userId 访问日志对象
     * @return 登录记录对象
     */
    public SysLogininfor selectLogininforLast(Long userId);

    /**
     * 查询上次登录时间(按设备)
     *
     * @param userId 访问日志对象
     * @return 登录记录对象
     */
    public SysLogininfor selectLogininforLastByDevice(Long userId,String type);

    /**
     * 批量删除系统登录日志
     * 
     * @param ids 需要删除的数据
     * @return
     */
    public int deleteLogininforByIds(String ids);

    /**
     * 清空系统登录日志
     */
    public void cleanLogininfor();
}
