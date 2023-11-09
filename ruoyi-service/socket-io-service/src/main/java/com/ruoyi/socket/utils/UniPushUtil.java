package com.ruoyi.socket.utils;

import com.getui.push.v2.sdk.ApiHelper;
import com.getui.push.v2.sdk.GtApiConfiguration;
import com.getui.push.v2.sdk.api.PushApi;
import com.getui.push.v2.sdk.api.UserApi;
import com.getui.push.v2.sdk.common.ApiResult;
import com.getui.push.v2.sdk.dto.req.Audience;
import com.getui.push.v2.sdk.dto.req.Settings;
import com.getui.push.v2.sdk.dto.req.message.PushChannel;
import com.getui.push.v2.sdk.dto.req.message.PushDTO;
import com.getui.push.v2.sdk.dto.req.message.PushMessage;
import com.getui.push.v2.sdk.dto.req.message.android.AndroidDTO;
import com.getui.push.v2.sdk.dto.req.message.android.GTNotification;
import com.getui.push.v2.sdk.dto.req.message.android.ThirdNotification;
import com.getui.push.v2.sdk.dto.req.message.android.Ups;
import com.getui.push.v2.sdk.dto.res.CidStatusDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 个推工具
 *
 * @author zx
 * @date 2022-07-26 18:37:46
 */
@Slf4j
public class UniPushUtil {

    private static GtApiConfiguration initGtApiConfiguration() {
        GtApiConfiguration apiConfiguration = new GtApiConfiguration();
        // 填写应用配置，参数在“Uni Push”下的“应用配置”页面中获取
        apiConfiguration.setAppId("ffKiaTkI2yAJrGtYgH5GC8");
        apiConfiguration.setAppKey("yTCBNYtVqsAwsFX16OyBD1");
        apiConfiguration.setMasterSecret("KMWb2bmgna6s4WerlhVsm9");
        apiConfiguration.setDomain("https://restapi.getui.com/v2/");
        return apiConfiguration;
    }

    /**
     * 实例化ApiHelper对象，用于创建接口对象
     */
    private static ApiHelper apiHelper = ApiHelper.build(initGtApiConfiguration());
    /**
     * 创建对象，建议复用。目前有PushApi、StatisticApi、UserApi
     */
    private static PushApi pushApi = apiHelper.creatApi(PushApi.class);
    private static UserApi userApi = apiHelper.creatApi(UserApi.class);

    private static PushDTO<Audience> initSinglePush() {
        //根据cid进行单推
        PushDTO<Audience> pushDTO = new PushDTO<>();
        // 设置推送参数，requestid需要每次变化唯一
        pushDTO.setRequestId(System.currentTimeMillis() + "");
        Settings settings = new Settings();
        // 消息有效期，走厂商消息必须设置该值
        settings.setTtl(3600000);
        pushDTO.setSettings(settings);
        return pushDTO;
    }

    /**
     * cid单推透传消息
     *
     * @param cid
     * @param content
     * @return
     */
    public static void singleTransmission(String cid, String content) {
        if(cid==null || StringUtils.isBlank(cid)){
            return;
        }
        PushDTO<Audience> pushDTO = initSinglePush();

        boolean online = isOnline(cid);
        if (!online) {
            log.error(cid+"客户端不在线");
            return;
        }
        // 设置在线透传消息
        pushDTO.setPushMessage(createPushMessage(null, null, null, content));

        // 设置接收人信息
        Audience audience = new Audience();
        pushDTO.setAudience(audience);
        audience.addCid(cid);
        // 进行cid单推
        ApiResult<Map<String, Map<String, String>>> apiResult = pushApi.pushToSingleByCid(pushDTO);
        if (apiResult.isSuccess()) {
            // success
            log.info(cid+"发送成功"+content,apiResult.getData());
        } else {
            // failed
            log.error("发送失败 code:" + apiResult.getCode() + ", msg: " + apiResult.getMsg());
        }
    }

    /**
     * cid单推通知
     *
     * @param cid
     * @param title
     * @param body
     * @return
     */
    public static boolean singleUniPush(String cid, String title, String body, String payload) {

        PushDTO<Audience> pushDTO = initSinglePush();

        // 设置在线用户通知消息
        pushDTO.setPushMessage(createPushMessage(title, body, payload, null));

        // 设置离线推送时的消息体
        pushDTO.setPushChannel(createChannel(title, body));


        // 设置接收人信息
        Audience audience = new Audience();
        pushDTO.setAudience(audience);
        audience.addCid(cid);
        // 进行cid单推
        ApiResult<Map<String, Map<String, String>>> apiResult = pushApi.pushToSingleByCid(pushDTO);
        boolean result = false;
        if (apiResult.isSuccess()) {
            // success
            System.out.println(apiResult.getData());
            result = true;
        } else {
            // failed
            System.out.println("code:" + apiResult.getCode() + ", msg: " + apiResult.getMsg());
        }
        return result;
    }

    private static boolean isOnline(String cid) {
        // 判断用户状态
        ApiResult<Map<String, CidStatusDTO>> mapApiResult = userApi.queryUserStatus(new HashSet<String>() {{
            add(cid);
        }});
        // 是否在线
        boolean online = false;
        if (mapApiResult.getCode() == 0) {
            Map<String, CidStatusDTO> data = mapApiResult.getData();
            for (Map.Entry<String, CidStatusDTO> cidStatusDTO : data.entrySet()) {
                String key = cidStatusDTO.getKey();
                System.out.println(key);
                CidStatusDTO value = cidStatusDTO.getValue();
                String status = value.getStatus();
                System.out.println(status);
                if ("online".equals(status)) {
                    online = true;
                }
            }
        }
        return online;
    }

    /**
     * 在线用户发送通知|透传消息
     *
     * @param title
     * @param body
     * @return
     */
    private static PushMessage createPushMessage(String title, String body, String payload, String transmission) {
        //此格式的透传消息由 unipush 做了特殊处理，会自动展示通知栏。开发者也可自定义其它格式，在客户端自己处理。
//        pushMessage.setTransmission(" {title:\"测试标题\",content:\"测试内容\",data:\"自定义数据\"}");

        // 在线走个推通道时推送的消息体
        PushMessage pushMessage = new PushMessage();
        if (transmission == null) {
            GTNotification gtNotification = new GTNotification();
            gtNotification.setTitle(title);
            gtNotification.setBody(body);
            if (StringUtils.isNotBlank(payload)) {
                // 自定义消息内容启动应用
                gtNotification.setPayload(payload);
                gtNotification.setClickType("payload");
            } else {
                gtNotification.setClickType("startapp");
            }
            pushMessage.setNotification(gtNotification);
        } else {
            pushMessage.setTransmission(transmission);
        }
        return pushMessage;
    }

    /**
     * 设置离线推送时的消息体
     *
     * @param title
     * @param body
     */
    private static PushChannel createChannel(String title, String body) {
        PushChannel pushChannel = new PushChannel();
        // 安卓离线厂商通道推送的消息体
        AndroidDTO androidDTO = new AndroidDTO();
        ThirdNotification thirdNotification = new ThirdNotification();
        thirdNotification.setTitle(title);
        thirdNotification.setBody(body);
        thirdNotification.setClickType("startapp");
        //注意：intent参数必须按下方文档（特殊参数说明）要求的固定格式传值，intent错误会导致客户端无法收到消息
//        thirdNotification.setIntent("请填写固定格式的intent");
        Ups ups = new Ups();
        ups.setNotification(thirdNotification);
        Map<String, Map<String, Object>> options = new HashMap<>(1);
        Map<String, Object> hw = new HashMap<>(3);
//        hw.put("/message/android/notification/badge/class","应用入口Activity路径名称");
//        hw.put("/message/android/notification/badge/add_num", 1);
//        hw.put("/message/android/notification/badge/set_num", 2);
        hw.put("/message/android/notification/importance", "NORMAL");
        options.put("HW", hw);
        ups.setOptions(options);
        androidDTO.setUps(ups);
        pushChannel.setAndroid(androidDTO);

        //ios离线apn通道推送的消息体
//        Alert alert = new Alert();
//        alert.setTitle("苹果离线通知栏标题");
//        alert.setBody("苹果离线通知栏内容");
//        Aps aps = new Aps();
//        aps.setContentAvailable(0);
//        aps.setSound("default");
//        aps.setAlert(alert);
//        IosDTO iosDTO = new IosDTO();
//        iosDTO.setAps(aps);
//        iosDTO.setType("notify");
//        pushChannel.setIos(iosDTO);

        return pushChannel;
    }


}
