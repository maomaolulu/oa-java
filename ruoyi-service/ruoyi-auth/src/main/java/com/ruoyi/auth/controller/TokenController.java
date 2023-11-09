package com.ruoyi.auth.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.ruoyi.auth.form.LoginForm;
import com.ruoyi.auth.service.AccessTokenService;
import com.ruoyi.auth.service.SysLoginService;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.common.utils.IpUtils;
import com.ruoyi.common.utils.OaConstants;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.model.LoginUser;
import eu.bitwalker.useragentutils.UserAgent;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @menu token管理
 */
@RestController
public class TokenController extends BaseController {
    private static final String WIN = "Windows";
    private static final String MAC = "Mac OS X";
    @Autowired
    private AccessTokenService tokenService;
    @Autowired
    private RedisUtils redis;
    @Autowired
    private SysLoginService sysLoginService;
    @Autowired
    private RemoteUserService userService;

    public static void main(String[] args) {
        System.out.println(OaConstants.UNIWE_IP.equals("60.190.21.178"));
    }

    @PostMapping("login")
    @ApiOperation("登录")
    public R login(@RequestBody LoginForm form) {

        return getLogin(form);

    }

    public R otherLogin(LoginForm form) {
        // 验证IP
        String hostIp = IpUtils.getIpAddr(ServletUtils.getRequest());
        logger.error(OaConstants.UNIWE_IP + "---------" + hostIp);
        if (!OaConstants.UNIWE_IP.equals(hostIp)) {
            if (!OaConstants.UNIWE_IP_TEST.equals(hostIp)) {
                logger.info("IP不合法{}", hostIp);
                return R.error("IP不合法");
            }
        }
        if (form.getUserId() == 1L || form.getUserId().equals(1)) {
            logger.info("非法操作{}", hostIp);
            return R.error("用户不存在001");
        }
        logger.info("免密登录ip来源{}", hostIp);
        // 判断是否登录，已登录不生成token
        String token = redis.get(Constants.ACCESS_USERID + form.getUserId() + "_pc");

        if (StrUtil.isNotBlank(token)) {
            // 获取登录token
            Map<String, Object> map = new HashMap<>(3);
            map.put("msg", "登录成功");
            map.put("code", 200);
            Map<String, Object> map2 = new HashMap<>(1);
            map2.put("token", token);
            map.put("data", map2);
            return R.ok(map);
        }
        SysUser user = sysLoginService.otherLogin(null, "账号密码（电脑）", form.getUserId());
        // 获取登录token
        Map<String, Object> map = new HashMap<>(3);
        map.put("msg", "登录成功");
        map.put("code", 200);
        map.put("data", tokenService.createToken(user, "_pc"));
        return R.ok(map);
    }

    /**
     * 运营2.0
     *
     * @param form 秘钥、邮箱
     * @return token
     */
    @PostMapping("/token")
    @ApiOperation("获取token")
    public Map<String, Object> token(@RequestBody LoginForm form) {
        Map<String, Object> result = Maps.newHashMap();
        if (StrUtil.isBlank(form.getPassword())) {
            return result;
        }
        if (!"d2e6fb1c22da59492f0ded58b4f9c2de".equals(form.getPassword())) {
            return result;
        }
        SysUser sysUser = userService.selectSysUserByEmail(form.getEmail());
        Long userId = sysUser.getUserId();
        if (sysUser != null && userId != null) {
            // 判断是否登录，已登录不生成token
            String token = redis.get(Constants.ACCESS_USERID + sysUser.getUserId() + "_pc");
            if (StrUtil.isNotBlank(token)) {
                // 获取登录token
                result.put("token", token);
                result.put("userId",userId);
                return result;
            }
            // 获取登录token
            Map<String, Object> map = tokenService.createToken(sysUser, "_pc");
            result.put("token", map.get("token").toString());
            result.put("userId",userId);
            return result;
        } else {
            return result;
        }
    }

    /**
     * 上海量远、运营系统2.0 获取token
     *
     * @param form
     * @return
     */
    public R loginLy(LoginForm form) {
        // 验证IP
        String hostIp = IpUtils.getIpAddr(ServletUtils.getRequest());
//        logger.error(OaConstants.UNIWE_IP+"---------"+hostIp);
//        if(!OaConstants.UNIWE_IP.equals(hostIp)){
//            if(!OaConstants.UNIWE_IP_TEST.equals(hostIp)){
//                logger.info("IP不合法{}",hostIp);
//                return R.error("IP不合法");
//            }
//        }

        logger.info("免密登录ip来源{}", hostIp);

        if (!"d2e6fb1c22da59492f0ded58b4f9c2de".equals(form.getPassword())) {
            return R.error("非法用户");
        }

        SysUser sysUser = userService.selectSysUserByEmail(form.getEmail());
        if (sysUser != null) {
            // 判断是否登录，已登录不生成token
            String token = redis.get(Constants.ACCESS_USERID + sysUser.getUserId() + "_pc");

            if (StrUtil.isNotBlank(token)) {
                // 获取登录token
                Map<String, Object> map = new HashMap<>(3);
                map.put("msg", "登录成功");
                map.put("code", 200);
                Map<String, Object> map2 = new HashMap<>(1);
                map2.put("token", token);
                map.put("data", map2);
                return R.ok(map);
            }
            SysUser user = sysLoginService.otherLogin(null, "账号密码（电脑）", sysUser.getUserId());
            // 获取登录token
            Map<String, Object> map = new HashMap<>(3);
            map.put("msg", "登录成功");
            map.put("code", 200);
            map.put("data", tokenService.createToken(user, "_pc"));
            return R.ok(map);
        } else {
            return R.error("该用户不存在");
        }
    }

    private R getLogin(LoginForm form) {
        // 用户登录
        String wxCode = form.getWxCode();
        // PC端登录
        if (StrUtil.isBlank(wxCode)) {
            SysUser user = sysLoginService.login(null, "账号密码（电脑）", form.getUsername(), form.getPassword());
            // 获取登录token
            Map<String, Object> map = new HashMap<>(3);
            map.put("msg", "登录成功");
            map.put("code", 200);
            map.put("data", tokenService.createToken(user, "_pc"));
            return R.ok(map);
        } else if ("app".equals(wxCode)) {
            // app登录
            SysUser user = sysLoginService.login(null, form.getLoginType() + "（APP）", form.getUsername(), form.getPassword());
            user.setCid(form.getCid());
            user.setUpdateBy(user.getUserName());
            userService.editSaveWx2(user, user.getLoginName(), user.getUserName());
            // 获取登录token
            Map<String, Object> map = new HashMap<>(3);
            map.put("msg", "登录成功");
            map.put("code", 200);
            map.put("data", tokenService.createToken(user, "_app"));
            return R.ok(map);
        } else {
            // 小程序
            String code = loginWx(wxCode);

            SysUser user = sysLoginService.login(code, form.getLoginType() + "（小程序）", form.getUsername(), form.getPassword());
            logger.error("new---" + code + "---db---" + user.getWxCode());
            if (StrUtil.isBlank(user.getWxCode())) {
                user.setWxCode(code);
                user.setUntie(false);
                user.setUpdateBy(user.getUserName());
                userService.editSaveWx2(user, user.getLoginName(), user.getUserName());
                Map<String, Object> map = new HashMap<>(4);
                map.put("msg", "登录成功");
                map.put("code", 200);
                map.put("data", tokenService.createToken(user, "_wx"));
                map.put("is_wx", 0);
                return R.ok(map);
            } else {
                if (!user.getWxCode().equals(code)) {
                    return R.error("请使用已绑定的微信登录或联系系统管理员解绑");
                }
                Map<String, Object> map = new HashMap<>(4);
                map.put("msg", "登录成功");
                map.put("code", 200);
                map.put("data", tokenService.createToken(user, "_wx"));
                map.put("is_wx", 1);
                return R.ok(map);
            }
        }
    }

    @PostMapping("login_finger")
    @ApiOperation("登录")
    public R loginFingerprint(@RequestBody LoginForm form) {
        if (form.getUserId() != null) {
            return otherLogin(form);
        }
        return getLogin(form);
    }

    @PostMapping("login_ly")
    @ApiOperation("上海量远获取token")
    public R login_ly(@RequestBody LoginForm form) {
        if (StrUtil.isBlank(form.getPassword())) {
            return R.error("秘钥为空");
        }
        return loginLy(form);
    }

    @PostMapping("refresh")
    public R refresh(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            // 刷新令牌有效期
            tokenService.refreshToken(loginUser);
            return R.ok();
        }
        return R.ok();
    }

    public String loginWx(String wxCode) {
        String url = "https://api.weixin.qq.com/sns/jscode2session"
                + "?appid=" + "wx22f79ec3e21630a7"
//                + "&secret=" + "7ee9716fa5df52c777bf3672cee3b6de"
                + "&secret=" + "fe3adf98ad4b9063214f9e5889615192"
                + "&js_code=" + wxCode
                + "&grant_type=authorization_code";

        String body = HttpUtil.get(url);
        JSONObject jsonObject = JSONUtil.parseObj(body);
        String openid = jsonObject.getStr("openid");
        return openid;

    }

    @PostMapping("logout")
    public R logout(HttpServletRequest request) {
        final UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        // 获取客户端操作系统
        String os = userAgent.getOperatingSystem().getName();
        String deviceType = "";
        if (os.indexOf(WIN) != -1 || MAC.equals(os)) {
            deviceType = "_pc";
        } else {
            deviceType = "_wx";
        }
        String token = request.getHeader("token");
        SysUser user = tokenService.queryByToken(token, deviceType);
        if (null != user) {
            sysLoginService.logout(user.getLoginName());
            tokenService.expireToken(user.getUserId(), deviceType);
        }
        return R.ok("ok");
    }
}
