package com.ruoyi.system.service;

import java.util.Date;
import java.util.List;
import com.ruoyi.system.domain.SysUserOnline;
import com.ruoyi.system.model.LoginUser;

/**
 * 在线用户 服务层
 * 
 * @author ruoyi
 */
public interface ISysUserOnlineService
{
//    /**
//     * 通过会话序号查询信息
//     *
//     * @param sessionId 会话ID
//     * @return 在线用户信息
//     */
//    public SysUserOnline selectOnlineById(String sessionId);
//
//    /**
//     * 通过会话序号删除信息
//     *
//     * @param sessionId 会话ID
//     * @return 在线用户信息
//     */
//    public void deleteOnlineById(String sessionId);
//
//    /**
//     * 通过会话序号删除信息
//     *
//     * @param sessions 会话ID集合
//     * @return 在线用户信息
//     */
//    public void batchDeleteOnline(List<String> sessions);
//
//    /**
//     * 保存会话信息
//     *
//     * @param online 会话信息
//     */
//    public void saveOnline(SysUserOnline online);
//
//    /**
//     * 查询会话集合
//     *
//     * @param userOnline 分页参数
//     * @return 会话集合
//     */
//    public List<SysUserOnline> selectUserOnlineList(SysUserOnline userOnline);
//
//    /**
//     * 强退用户
//     *
//     * @param sessionId 会话ID
//     */
//    public void forceLogout(String sessionId);
//
//    /**
//     * 查询会话集合
//     *
//     * @param expiredDate 有效期
//     * @return 会话集合
//     */
//    public List<SysUserOnline> selectOnlineByExpired(Date expiredDate);
    /**
     * 新在线用户
     */
    /**
     * 通过登录地址查询信息
     *
     * @param ipaddr 登录地址
     * @param user 用户信息
     * @return 在线用户信息
     */
    public SysUserOnline selectOnlineByIpaddr(String ipaddr, LoginUser user,String key);

    /**
     * 通过用户名称查询信息
     *
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    public SysUserOnline selectOnlineByUserName(String userName, LoginUser user,String key);

    /**
     * 通过登录地址/用户名称查询信息
     *
     * @param ipaddr 登录地址
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    public SysUserOnline selectOnlineByInfo(String ipaddr, String userName, LoginUser user,String key);

    /**
     * 设置在线用户信息
     *
     * @param user 用户信息
     * @return 在线用户
     */
    public SysUserOnline loginUserToUserOnline(LoginUser user,String key);
}
