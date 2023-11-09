package com.ruoyi.socket.controller;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.socket.domain.dto.AbuSendNoteDTO;
import com.ruoyi.socket.service.IAbuProjectNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;

/**
 * 项目留言Controller
 *
 * @author yrb
 * @date 2023-04-06
 */
@RestController
@RequestMapping("/note")
public class AbuProjectNoteController extends BaseController {

    private final IAbuProjectNoteService abuProjectNoteService;

    @Autowired
    public AbuProjectNoteController(IAbuProjectNoteService abuProjectNoteService) {
        this.abuProjectNoteService = abuProjectNoteService;
    }

    @PostMapping("/sendMessage")
    public R sendMessage(@RequestBody AbuSendNoteDTO abuSendNoteDTO) {
        try {
            if (StrUtil.isBlank(abuSendNoteDTO.getMasterPhone())) {
                return R.error("未指定负责人，不能发起留言");
            }
            if (StrUtil.isBlank(abuSendNoteDTO.getProjectId())) {
                return R.error("项目编号不能为空");
            }
            if (StrUtil.isBlank(abuSendNoteDTO.getProjectName())) {
                return R.error("项目名称不能为空");
            }
            if (StrUtil.isBlank(abuSendNoteDTO.getNote())) {
                return R.error("留言信息不能为空");
            }
            if (StrUtil.isBlank(abuSendNoteDTO.getSalesman())){
                return R.error("业务员姓名不能为空");
            }
            abuProjectNoteService.sendMessage(abuSendNoteDTO);
            return R.ok();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return R.error("留言失败");
        }
    }

    @GetMapping("/getAbuProjectNoteList")
    public R getAbuProjectNoteList(String projectId) {
        if (StrUtil.isBlank(projectId)) {
            return R.error("项目id不能为空");
        }
        return R.data(abuProjectNoteService.selectProjectNoteList(projectId));
    }
}
