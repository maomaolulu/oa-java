package com.ruoyi.system.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.annotation.LoginUser;
import com.ruoyi.common.auth.annotation.HasPermissions;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.log.enums.OperatorType;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.common.utils.RandomUtil;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.config.properties.WxAuthorityProperties;
import com.ruoyi.system.domain.SysLogininfor;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysLogininforService;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.util.PasswordUtil;
import com.ruoyi.system.utils.MailService;
import com.ruoyi.system.vo.PasswordForm;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 用户 提供者
 *
 * @author zmr
 * @date 2019-05-20
 * @menu 用户管理
 */

@RestController
@RefreshScope
@RequestMapping("user")
public class SysUserController extends BaseController {

    @Value("${spring.mail.username}")
    private String fromEmail;
    @Autowired
    private RedisUtils redis;
    private final static long EXPIRE = 24 * 60 * 60;
    private final ISysUserService sysUserService;
    private final ISysMenuService sysMenuService;
    private final ISysLogininforService sysLogininforService;
    private final ISysDeptService sysDeptService;
    private final WxAuthorityProperties wxAuthorityProperties;
    private final MailService mailService;
//    @Autowired
//    private JavaMailSender javaMailSender;

    @Autowired
    public SysUserController(ISysUserService sysUserService, ISysMenuService sysMenuService, ISysLogininforService sysLogininforService, ISysDeptService sysDeptService, WxAuthorityProperties wxAuthorityProperties, MailService mailService) {
        this.sysUserService = sysUserService;
        this.sysMenuService = sysMenuService;
        this.sysLogininforService = sysLogininforService;
        this.sysDeptService = sysDeptService;
        this.wxAuthorityProperties = wxAuthorityProperties;
        this.mailService = mailService;
    }

    /**
     * 查询用户
     */
    @GetMapping("get/{userId}")
    public SysUser get(@PathVariable("userId") Long userId) {
        SysUser sysUser = sysUserService.selectUserById(userId);
        getCompanyInfo(sysUser);
        return sysUser;
    }

    @GetMapping("info")
    @ApiOperation(value = "查询用户信息", notes = "查询用户信息")
    public SysUser info(@LoginUser SysUser sysUser) {
        SysLogininfor date = sysLogininforService.selectLogininforLast(sysUser.getUserId());
        if (date == null) {
            sysUser.setLoginLast(new Date());
        } else {

            sysUser.setLoginLast(date.getLoginTime());
        }
        Set<String> strings = sysMenuService.selectPermsByUserId(sysUser.getUserId());
        Set<String> wxAuthority = wxAuthorityProperties.getWxAuthority();
        Set<String> bt2 = new HashSet<>();
        strings.stream().forEach(all -> {
            wxAuthority.stream().forEach(wx -> {
                if (all.equals(wx)) {
                    bt2.add(all);
                }
            });
        });
        sysUser.setButtons2(bt2);
        getCompanyInfo(sysUser);
        sysUser.setButtons(sysMenuService.selectPermsByUserId(sysUser.getUserId()));
        return sysUser;
    }

    private void getCompanyInfo(SysUser sysUser) {
        if (sysUser.getDept().getParentId() != null) {
            Map<String, Object> belongCompany = sysDeptService.getBelongCompany(sysUser.getDept().getDeptId());
            sysUser.setCompany(belongCompany.get("companyName").toString());
            sysUser.setCompanyId((Long) belongCompany.get("companyId"));
        }
    }

    /**
     * 查询用户
     */
    @GetMapping("find/{username}")
    public SysUser findByLoginName(@PathVariable("username") String loginname) {
        SysUser sysUser = sysUserService.selectUserByLoginName(loginname);
        getCompanyInfo(sysUser);
        return sysUser;
    }

    /**
     * 查询用户
     */
    @GetMapping("query/{username}")
    public SysUser getByUsername(@PathVariable("username") String username) {
        SysUser sysUser = sysUserService.selectUserByUserName(username);
        getCompanyInfo(sysUser);
        return sysUser;
    }

    /**
     * 查询用户
     */
    @GetMapping("queryByEmail/{email}")
    public SysUser getByEmail(@PathVariable("email") String email) {
        SysUser sysUser = sysUserService.selectUserByEmail(email);
        getCompanyInfo(sysUser);
        return sysUser;
    }

    /**
     * 查询拥有当前角色的所有用户
     */
    @GetMapping("hasRoles")
    public Set<Long> hasRoles(String roleIds) {
        Long[] arr = Convert.toLongArray(roleIds);
        return sysUserService.selectUserIdsHasRoles(arr);
    }

    /**
     * 查询所有当前部门中的用户
     */
    @ApiOperation(value = "查询所有当前部门中的用户", notes = "查询所有当前部门中的用户")
    @GetMapping("inDepts")
    public Set<Long> inDept(String deptIds) {
        Long[] arr = Convert.toLongArray(deptIds);
        return sysUserService.selectUserIdsInDepts(arr);
    }

    /**
     * 查询用户列表
     */
    @GetMapping("list")
    public R list(SysUser sysUser) {
        startPage();
        return result(sysUserService.selectUserList(sysUser));
    }

    /**
     * 查询用户列表(过滤离职人员)
     */
    @GetMapping("list2")
    public R list2(SysUser sysUser) {
        startPage();
        return result(sysUserService.selectUserList2(sysUser));
    }


    /**
     * 新增保存用户
     */
    @HasPermissions("system:user:add")
    @PostMapping("save")
    @OperLog(title = "用户管理", businessType = BusinessType.INSERT)
    public R addSave(@RequestBody SysUser sysUser) {
        if (UserConstants.USER_NAME_NOT_UNIQUE.equals(sysUserService.checkLoginNameUnique(sysUser.getLoginName()))) {
            return R.error("新增用户'" + sysUser.getLoginName() + "'失败，登录账号已存在");
        }
//        else if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(sysUserService.checkPhoneUnique(sysUser))) {
//            return R.error("新增用户'" + sysUser.getLoginName() + "'失败，手机号码已存在");
//        }
//        else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(sysUserService.checkEmailUnique(sysUser))) {
//            return R.error("新增用户'" + sysUser.getLoginName() + "'失败，邮箱账号已存在");
//        }   0a4637649c5b62658c58df83f2fb850d
        sysUser.setOtherDeptId(String.join(";", sysUser.getOtherDeptIds()));
        sysUser.setSalt(RandomUtil.randomStr(6));
        sysUser.setPassword("123456");
        sysUser.setPassword(
                PasswordUtil.encryptPassword(sysUser.getLoginName(), sysUser.getPassword(), sysUser.getSalt()));
        sysUser.setCreateBy(getLoginName());
        int i = sysUserService.insertUser(sysUser);
        if (i > 0) {
            return R.ok("success", sysUser.getUserId());
        }
        return R.error();
    }

    public static void main(String[] args) {
//        String s = PasswordUtil.encryptPassword("zhangdanli", "123456", "nSJHnT");
//        System.out.println(s);

    }

    /**
     * 修改保存用户
     */
    @HasPermissions("system:user:edit")
    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("update")
    public R editSave(@LoginUser SysUser loginUser, @RequestBody SysUser sysUser) {
        if (!SysUser.isAdmin(loginUser.getUserId())) {
            if (null != sysUser.getUserId() && SysUser.isAdmin(sysUser.getUserId())) {
                return R.error("不允许修改超级管理员用户");
            }
//            else if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(sysUserService.checkPhoneUnique(sysUser))) {
//                return R.error("修改用户'" + sysUser.getLoginName() + "'失败，手机号码已存在");
//            } else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(sysUserService.checkEmailUnique(sysUser))) {
//                return R.error("修改用户'" + sysUser.getLoginName() + "'失败，邮箱账号已存在");
//            }
        }
        if (sysUser.getOtherDeptIds() != null) {

            sysUser.setOtherDeptId(String.join(";", sysUser.getOtherDeptIds()));
        }
        if (sysUser.getUserPermissions() != null) {

            sysUser.setUserPermission(String.join(";", sysUser.getUserPermissions()));
        }
        return toAjax(sysUserService.updateUser(sysUser));
    }

    /**
     * 解绑微信
     *
     * @param sysUser
     * @return
     */
    @HasPermissions("system:user:unbound")
    @OperLog(title = "解绑微信", businessType = BusinessType.UPDATE)
    @PostMapping("update_wx")
    public R editSaveWx(@RequestBody SysUser sysUser) {
        try {

            return sysUserService.editSaveWx(sysUser);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return R.error("操作失败");
        }
    }

    /**
     * 绑定微信
     *
     * @param sysUser
     * @return
     */
    @OperLog(title = "绑定微信", businessType = BusinessType.UPDATE, operatorType = OperatorType.MOBILE)
    @PostMapping("update_wx2")
    public R editSaveWx2(@RequestBody SysUser sysUser) {
        try {

            return sysUserService.editSaveWx(sysUser);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return R.error("操作失败");
        }
    }

    ;

    /**
     * 修改用户信息
     *
     * @param sysUser
     * @return
     * @author zmr
     */
    @HasPermissions("system:user:edit")
    @PostMapping("update/info")
    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    public R updateInfo(@RequestBody SysUser sysUser) {
        return toAjax(sysUserService.updateUserInfo(sysUser));
    }

    /**
     * 修改用户头像
     *
     * @param sysUser
     * @return
     * @author zmr
     */
    @PostMapping("update/avatar")
    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    public R updateAvatar(@RequestBody SysUser sysUser) {
        return toAjax(sysUserService.updateAvatar(sysUser));
    }

    /**
     * 修改密码
     *
     * @param form
     * @return
     */
    @PostMapping("modify_pwd")
    @OperLog(title = "修改密码", businessType = BusinessType.UPDATE)
    public R modifyPwd(@RequestBody PasswordForm form) {
        if ("admin".equals(form.getLoginName())) {
            return R.ok("禁止修改超级管理员账号密码");
        }
        // 校验原密码
        SysUser user = sysUserService.selectUserById(form.getUserId());
        boolean matches = PasswordUtil.matches(user, form.getPassword());
        if (!matches) {
            return R.error("原密码不正确，请重新输入");
        }
        try {
            user.setSalt(RandomUtil.randomStr(6));
            user.setPassword(PasswordUtil.encryptPassword(user.getLoginName(), form.getNewPassword(), user.getSalt()));
            sysUserService.updateUserInfo(user);
            return R.ok("修改密码成功，请重新登录");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return R.error("修改密码失败");
        }

    }

    @PostMapping("forget_pwd")
    @ApiOperation(value = "忘记密码")
    @OperLog(title = "忘记密码", businessType = BusinessType.OTHER)
    public R forgetPwd(@RequestBody PasswordForm form) {
        try {
            String key = redis.get(form.getLoginName() + "forget_pwd");
            if (!StrUtil.isBlank(key)) {
                return R.error("24小时内只能重置一次密码");
            }
            // 获取用户信息
            SysUser user = sysUserService.selectUserByLoginName(form.getLoginName());
            if (user == null) {
                return R.error("用户不存在");
            }
            if (!user.getEmail().equals(form.getEmail())) {
                return R.error("您输入的邮箱不存在，请重新输入!");
            }
            String password = RandomUtil.randomStr(8);
            user.setSalt(RandomUtil.randomStr(6));
            user.setPassword(PasswordUtil.encryptPassword(user.getLoginName(), password, user.getSalt()));
            sysUserService.updateUserInfo(user);
            // 发送邮件
            sendSimpleMail(user.getEmail(), password, user.getUserName());
            redis.set(form.getLoginName() + "forget_pwd", form.getLoginName(), EXPIRE);
            return R.ok("重置密码成功，请登录邮箱查看!");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return R.error("重置密码失败");
        }

    }

    private void sendSimpleMail(String email, String password, String createBy) {
        // 构建一个邮件对象
        SimpleMailMessage message = new SimpleMailMessage();
        // 设置邮件主题
        message.setSubject("安联云管家-重置密码");
        // 设置邮件发送者，这个跟application.yml中设置的要一致

        String nick = "";
        try {
            nick = javax.mail.internet.MimeUtility.encodeText("安联云管家");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        message.setFrom(nick + " <" + fromEmail + ">");
        // 设置邮件接收者，可以有多个接收者，中间用逗号隔开，以下类似
        // message.setTo("10*****16@qq.com","12****32*qq.com");
        message.setTo(email);
        // 设置邮件抄送人，可以有多个抄送人
//        message.setCc("12****32*qq.com");
        // 设置隐秘抄送人，可以有多个
//        message.setBcc("7******9@qq.com");
        // 设置邮件发送日期
        message.setSentDate(new Date());
        // 设置邮件的正文
        message.setText("安联人，您好！\n" +
                "       您的安联云管家账号密码已重置\n" +
                "       新密码：" + password + "     (新密码为随机生成)\n" +
                "                                                   安联云管家 \n" +
                "                                                   " + DateUtil.format(new Date(), "YYYY-MM-dd"));
        // 发送邮件
//        javaMailSender.send(message);
        String textSecret = "安联人，您好！\n" +
                "       您的安联云管家账号密码已重置\n" +
                "       新密码：******     (新密码为随机生成)\n" +
                "                                                   安联云管家 \n" +
                "                                                   " + DateUtil.format(new Date(), "YYYY-MM-dd");
        try {
            mailService.send(message.getText(), message.getSubject(), email);
            mailService.sendSecret(textSecret, message.getSubject(), email, createBy + "（忘记密码）");
        } catch (Exception e) {
            logger.error("忘记密码", e);
        }
    }

    /**
     * 记录登陆信息
     *
     * @param sysUser
     * @return
     * @author zmr
     */
    @PostMapping("update/login")
    public R updateLoginRecord(@RequestBody SysUser sysUser) {
        return toAjax(sysUserService.updateUser2(sysUser));
    }

    /**
     * 重置密码
     *
     * @param loginUser
     * @param user
     * @return
     */
    @HasPermissions("system:user:resetPwd")
    @OperLog(title = "重置密码", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    public R resetPwdSave(@LoginUser SysUser loginUser, @RequestBody SysUser user) {
        if (!SysUser.isAdmin(loginUser.getUserId())) {
            if (null != user.getUserId() && SysUser.isAdmin(user.getUserId())) {
                return R.error("不允许修改超级管理员用户");
            }
        }
        user.setSalt(RandomUtil.randomStr(6));
        user.setPassword(PasswordUtil.encryptPassword(user.getLoginName(), user.getPassword(), user.getSalt()));
        return toAjax(sysUserService.resetUserPwd(user));
    }

    /**
     * 重置密码(整个部门)
     *
     * @param deptId
     * @return
     */
    @HasPermissions("system:user:allreset")
    @OperLog(title = "重置密码(整个部门)", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd_dept")
    public R resetPwdDept(Long deptId) {

        List<SysUser> sysUsers = sysUserService.selectUserIdsInDept(deptId);
        SysUser user1 = sysUserService.selectUserById(SystemUtil.getUserId());
        for (SysUser user : sysUsers) {
            if (user.getUserId() == 1) {
                continue;
            }
            if ("1".equals(user.getStatus())) {
                continue;
            }
            String password = RandomUtil.randomStr(10);
            user.setSalt(RandomUtil.randomStr(6));
            user.setPassword(PasswordUtil.encryptPassword(user.getLoginName(), password, user.getSalt()));
            logger.error(user.getUserId() + "---------" + password + "----------" + user.getSalt());
            sysUserService.resetUserPwd(user);
            String txt = "<body> " +
                    " <p>亲爱的用户：</p> " +
                    " <p>&emsp;&emsp;您好！</p> " +
                    " <p>&emsp;&emsp;您的登录密码已自动重置为" + password + "。</p> " +
                    " <p align= 'right'>云管家小助手&emsp;&emsp;&emsp;</p> " +
                    " <p align= 'right'>" + DateUtil.format(new Date(), "YYYY-MM-dd") + "&emsp;&emsp;&emsp;</p> " +
                    " </body>";
            String textSecret = "<body> " +
                    " <p>亲爱的用户：</p> " +
                    " <p>&emsp;&emsp;您好！</p> " +
                    " <p>&emsp;&emsp;您的登录密码已自动重置为******。</p> " +
                    " <p align= 'right'>云管家小助手&emsp;&emsp;&emsp;</p> " +
                    " <p align= 'right'>" + DateUtil.format(new Date(), "YYYY-MM-dd") + "&emsp;&emsp;&emsp;</p> " +
                    " </body>";
            try {
                mailService.send(txt, "重置密码", user.getEmail());
                mailService.sendSecret(textSecret, "重置密码", user.getEmail(), user1.getUserName());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return R.ok("重置密码成功");
    }

    /**
     * 重置密码(单个用户)
     *
     * @param user
     * @return
     */
    @HasPermissions("system:user:allreset")
    @OperLog(title = "重置密码(单个用户)", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd_user")
    public R resetPwdUser(@RequestBody SysUser user, @LoginUser SysUser sysUser) {
        if (user.getUserId() == 1) {
            return R.ok("重置密码成功");
        }
        String password = RandomUtil.randomStr(10);
        user.setSalt(RandomUtil.randomStr(6));
        user.setPassword(PasswordUtil.encryptPassword(user.getLoginName(), password, user.getSalt()));
        sysUserService.resetUserPwd(user);
        String txt = "<body> " +
                " <p>亲爱的用户：</p> " +
                " <p>&emsp;&emsp;您好！</p> " +
                " <p>&emsp;&emsp;您的登录密码已自动重置为" + password + "。</p> " +
                " <p align= 'right'>云管家小助手&emsp;&emsp;&emsp;</p> " +
                " <p align= 'right'>" + DateUtil.format(new Date(), "YYYY-MM-dd") + "&emsp;&emsp;&emsp;</p> " +
                " </body>";
        String textSecret = "<body> " +
                " <p>亲爱的用户：</p> " +
                " <p>&emsp;&emsp;您好！</p> " +
                " <p>&emsp;&emsp;您的登录密码已自动重置为******。</p> " +
                " <p align= 'right'>云管家小助手&emsp;&emsp;&emsp;</p> " +
                " <p align= 'right'>" + DateUtil.format(new Date(), "YYYY-MM-dd") + "&emsp;&emsp;&emsp;</p> " +
                " </body>";
        try {
            mailService.send(txt, "重置密码", user.getEmail());
            SysUser user1 = sysUserService.selectUserById(SystemUtil.getUserId());
            mailService.sendSecret(textSecret, "重置密码", user.getEmail(), user1.getUserName());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return R.ok("重置密码成功");
    }

    /**
     * 修改状态
     *
     * @param user
     * @return
     * @author zmr
     */
    @HasPermissions("system:user:edit")
    @PostMapping("status")
    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    public R status(@LoginUser SysUser loginUser, @RequestBody SysUser user) {
        if (!SysUser.isAdmin(loginUser.getUserId())) {
            if (null != user.getUserId() && SysUser.isAdmin(user.getUserId())) {
                return R.error("不允许修改超级管理员用户");
            }
        }
        return toAjax(sysUserService.changeStatus(user));
    }

    /**
     * 删除用户
     *
     * @throws Exception
     */
    @HasPermissions("system:user:remove")
    @OperLog(title = "用户管理", businessType = BusinessType.DELETE)
    @PostMapping("remove")
    public R remove(String ids) throws Exception {
        return toAjax(sysUserService.deleteUserByIds(ids));
    }

    @OperLog(title = "用户管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user) throws IOException {
        List<SysUser> list = sysUserService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    @GetMapping("/getLeaderInfo/{userId}")
    public SysUser getLeaderInfo(@PathVariable("userId") Long userId) {
        return sysUserService.selectLeaderByUserId(userId);
    }

    /**
     * 获取所属公司人员
     *
     * @param companyId 公司ID
     * @return 集合
     */
    @GetMapping("/users/belongCompany")
    public List<SysUser> belongCompany(Long companyId){
        return sysUserService.selectUsersBelongCompany(companyId);
    }
}
