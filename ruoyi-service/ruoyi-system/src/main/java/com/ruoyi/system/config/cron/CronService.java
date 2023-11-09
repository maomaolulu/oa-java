package com.ruoyi.system.config.cron;

import cn.hutool.core.date.DateUtil;
import cn.hutool.cron.CronUtil;
import com.ruoyi.common.utils.RandomUtil;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.util.PasswordUtil;
import com.ruoyi.system.utils.MailService;
import com.ruoyi.system.utils.SpringBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.List;

/**
 * 定时任务
 * @author zx
 * @date 2022/3/11 10:55
 */
@Slf4j
public class CronService {

    public void resetPwd(){
        ISysUserService userService = SpringBeanUtil.getBean(ISysUserService.class);
        MailService mailService = SpringBeanUtil.getBean(MailService.class);
        List<SysUser> sysUsers = userService.selectUserList2(new SysUser());

        for (SysUser user : sysUsers) {
            String password = RandomUtil.randomStr(10);
            user.setSalt(RandomUtil.randomStr(6));
            user.setPassword(PasswordUtil.encryptPassword(user.getLoginName(), password, user.getSalt()));
            log.info("定时重置密码*****"+user.getUserName()+"*****"+password+"*****"+user.getSalt());
            userService.updateUserInfo(user);
            String txt = "<body> " +
                    " <p>亲爱的用户：</p> " +
                    " <p>&emsp;&emsp;您好！</p> " +
                    " <p>&emsp;&emsp;您的登录密码已自动重置为"+password+"。</p> " +
                    " <p align= 'right'>云管家小助手&emsp;&emsp;&emsp;</p> " +
                    " <p align= 'right'>"+ DateUtil.format(new Date(), "YYYY-MM-dd")+"&emsp;&emsp;&emsp;</p> " +
                    " </body>";
            try {
                mailService.send(txt,"重置密码",user.getEmail());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

}
