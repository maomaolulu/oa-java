package com.ruoyi.socket.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.socket.consts.UrlConstants;
import com.ruoyi.socket.domain.AbuProjectNote;
import com.ruoyi.socket.domain.dto.AbuSendNoteDTO;
import com.ruoyi.socket.mapper.AbuProjectNoteMapper;
import com.ruoyi.socket.service.IAbuProjectNoteService;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteUserService;
import com.ruoyi.system.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 项目留言Service业务层处理
 *
 * @author yrb
 * @date 2023-04-06
 */
@Service
public class AbuProjectNoteServiceImpl extends ServiceImpl<AbuProjectNoteMapper, AbuProjectNote> implements IAbuProjectNoteService {
    public static final Integer WECOM_AGENT_ID = 1000016;
    public static final String WECOM_PROJECT_TOKEN = "wecom_project_token";
    public static final String WECOM_CORPID = "ww886f70c97df35620";
    public static final String WECOM_CORPSECRET = "79pQlLks28WakCiZv0Zuip38mOD_fw81o1mEPVVX6as";
    public static final String WECOM_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?" + "corpid=" + WECOM_CORPID + "&corpsecret=" + WECOM_CORPSECRET;
    public static final String WECOM_TOUSER_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserid?access_token=";
    public static final String WECOM_MESSAGE_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";
    protected final Logger logger = LoggerFactory.getLogger(AbuProjectNoteServiceImpl.class);
    private final AbuProjectNoteMapper abuProjectNoteMapper;
    private final RemoteUserService remoteUserService;
    private final RedisUtils redisUtils;

    @Autowired
    public AbuProjectNoteServiceImpl(AbuProjectNoteMapper abuProjectNoteMapper,
                                     RemoteUserService remoteUserService,
                                     RedisUtils redisUtils) {
        this.abuProjectNoteMapper = abuProjectNoteMapper;
        this.remoteUserService = remoteUserService;
        this.redisUtils = redisUtils;
    }

    /**
     * @param abuSendNoteDTO 留言相关信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void sendMessage(AbuSendNoteDTO abuSendNoteDTO) {
        Long userId = SystemUtil.getUserId();
        // 将需要发送信息的号码存进list
        List<String> list = new ArrayList<>();
        // 项目负责人
        list.add(abuSendNoteDTO.getMasterPhone());
        // 通过中文名获取用户信息
        if (StrUtil.isNotBlank(abuSendNoteDTO.getWriter())) {
            SysUser sysUser = remoteUserService.selectSysUserByName(abuSendNoteDTO.getWriter());
            if (sysUser != null && StrUtil.isNotBlank(sysUser.getPhonenumber())) {
                list.add(sysUser.getPhonenumber());
            }
        }
        // 封装并保存留言信息
        String projectId = abuSendNoteDTO.getProjectId();
        AbuProjectNote abuProjectNote = new AbuProjectNote();
        abuProjectNote.setProjectId(projectId);
        abuProjectNote.setNote(abuSendNoteDTO.getNote());
        abuProjectNote.setCreateTime(new Date());
        abuProjectNote.setUserId(userId);
        if (baseMapper.insert(abuProjectNote) == 0) {
            throwException("留言信息插入数据库失败");
        }
        // 获取token
        String token = redisUtils.get(WECOM_PROJECT_TOKEN);
        if (StrUtil.isBlank(token)) {
            token = getToken();
        }
        // 通过电话号码获取企微用户id
        String touser = getQiWeiTouser(token, list);
        if (StrUtil.isBlank(touser)) {
            throwException("未获取到信息接收人微信id");
        }
        // 向企业微信用户发送消息
        String content = getMessage(abuSendNoteDTO);
        sendMessage(token, touser, content);
        // 异步保存到其他系统
        ThreadFactory build = new ThreadFactoryBuilder().setNameFormat("noteExecutor-").build();
        new ThreadPoolExecutor(
                2,
                2,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1),
                build
        ).execute(() -> {
            Map<String, String> param = new HashMap<>();
            param.put("projectNo", projectId);
            param.put("messageDate", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            param.put("messageContent", content);
//            String response = HttpUtil.post(UrlConstants.TEST_SAVE_NOTE_URL, JSON.toJSONString(param));
            String response = HttpUtil.post(UrlConstants.ONLINE_SAVE_NOTE_URL, JSON.toJSONString(param));
            Map<String, Object> map = (Map<String, Object>) JSON.parse(response);
            if ((Integer) map.get("code") != 0) {
                logger.error("调用第三方接口保存留言出错------------" + response);
            }
        });
    }

    /**
     * 查询留言相关信息
     *
     * @param projectId 项目编号
     * @return result
     */
    @Override
    public List<AbuProjectNote> selectProjectNoteList(String projectId) {
        QueryWrapper<AbuProjectNote> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        queryWrapper.orderByDesc("create_time");
        return baseMapper.selectList(queryWrapper);
    }

    private String getToken() {
        // 获取token
        String response = HttpUtil.get(WECOM_TOKEN_URL);
        if (StrUtil.isBlank(response)) {
            throwException("获取企微token时返回值为空,请求路径：" + WECOM_TOKEN_URL, "获取token时返回值为空");
        }
        // 解析返回值
        Map<String, Object> parse = (Map<String, Object>) JSON.parse(response);
        String token = (String) parse.get("access_token");
        if (StrUtil.isBlank(token)) {
            throwException("获取企微token时发生错误：" + response, "未获取到token");
        }
        // 存redis 有效期2小时
        redisUtils.set(WECOM_PROJECT_TOKEN, token, (Integer) parse.get("expires_in"));
        return token;
    }

    private String getQiWeiTouser(String token, List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String phone : list) {
            // 通过电话号码获取企微用户id
            String url = WECOM_TOUSER_URL + token;
            Map<String, Object> map = new HashMap<>();
            map.put("mobile", phone);
            String response = HttpUtil.post(url, JSON.toJSONString(map));
            if (StrUtil.isBlank(response)) {
                logger.error("通过手机号码获取touser时返回值为空，手机号码：------------------------" + phone);
                continue;
            }
            // 解析返回值
            Map<String, String> parse = (Map<String, String>) JSON.parse(response);
            String userId = parse.get("userid");
            if (StrUtil.isBlank(userId)) {
                logger.error("未能通过手机号码获取到touser------------------------" + response);
                continue;
            }
            stringBuilder.append(userId + "|");
        }
        if (stringBuilder.length() == 0) {
            return "";
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    private void sendMessage(String token, String touser, String content) {
        // 发消息
        String url = WECOM_MESSAGE_URL + token;
        // 封装数据并发送
        Map<String, Object> map2 = new HashMap<>();
        map2.put("content", content);
        Map<String, Object> map1 = new HashMap<>();
        map1.put("touser", touser);
        map1.put("msgtype", "text");
        map1.put("agentid", WECOM_AGENT_ID);
        map1.put("safe", 0);
        map1.put("text", map2);
        String response = HttpUtil.post(url, JSON.toJSONString(map1));
        if (StrUtil.isBlank(response)) {
            throwException("发送信息给企业微信用户时返回值为空，请求地址：" + url, "发送信息给企业微信用户时返回值为空");
        }
        Map<String, Object> parse = (Map<String, Object>) JSON.parse(response);
        Integer errcode = (Integer) parse.get("errcode");
        if (errcode == null || errcode != 0) {
            throwException("信息发送失败-----------------返回值为：" + response, "信息发送失败");
        }
    }

    private void throwException(String var1, String var2) {
        logger.error(var1);
        throw new RuntimeException(var2);
    }

    private void throwException(String var) {
        logger.error(var);
        throw new RuntimeException(var);
    }

    private String getMessage(AbuSendNoteDTO abuSendNoteDTO) {
        String message;
        String busiPhone = abuSendNoteDTO.getBusiPhone();
        if (StrUtil.isBlank(busiPhone)) {
            message = abuSendNoteDTO.getProjectName() + "（" + abuSendNoteDTO.getProjectId() + "），市场人员" +
                    abuSendNoteDTO.getSalesman() + "给你留言：“" + abuSendNoteDTO.getNote() + "” 请及时处理并反馈。";
        } else {
            message = abuSendNoteDTO.getProjectName() + "（" + abuSendNoteDTO.getProjectId() + "），市场人员" +
                    abuSendNoteDTO.getSalesman() + "（" + busiPhone + "）给你留言：“" + abuSendNoteDTO.getNote() + "” 请及时处理并反馈。";
        }
        return message;
    }
}
