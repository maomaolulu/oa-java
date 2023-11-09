package com.alt.controller;

import cn.hutool.core.exceptions.StatefulException;
import com.alt.entity.dto.MessageDto;
import com.alt.service.MessageService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 消息服务
 * @author zx
 * @date 2022/9/4 23:16
 * @description SendMsgController
 */
@RestController
@RequestMapping("producer")
public class SendMsgController extends BaseController {
    private final MessageService messageService;
    @Autowired
    public SendMsgController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 发送消息
     * @param msg
     * @throws Exception
     */
    @PostMapping("msg")
    public R sendMsg(@RequestBody MessageDto msg) {
        try {
            messageService.save(msg);
            return R.ok();
        }catch (StatefulException e){
            return R.error(e.getMessage());
        }
        catch (Exception e){
            logger.error("发送消息失败",e);
            return R.error();
        }
    }

}
