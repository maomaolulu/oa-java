package com.ruoyi.ehs.controller.signin;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.ehs.domain.signin.BizSignIn;
import com.ruoyi.ehs.domain.signin.dto.BizSignInAddDTO;
import com.ruoyi.ehs.domain.signin.dto.BizSignInDTO;
import com.ruoyi.ehs.service.BizSignInService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 打卡签到
 *
 * @author wuYang
 * @date 2022/9/19 15:55
 */
@Api("打开")
@RequestMapping("/signin")
@RestController
public class BizSignInController extends BaseController {

    @Resource
    BizSignInService bizSignInService;


    @ApiOperation("新增")
    @PostMapping("/add")
    @OperLog(title = "新增打卡", businessType = BusinessType.INSERT)
    public R add(@RequestBody BizSignInAddDTO bizSignIn) {
        try {
            bizSignInService.add(bizSignIn);
            return R.ok("新增成功");
        } catch (Exception e) {
            logger.error("新增失败" + e.getMessage());
            return R.ok("新增失败");
        }
    }

    @ApiOperation("查询")
    @GetMapping("/pagelist")
    public R select(BizSignInDTO dto) {
        try {
            LocalDateTime startTime = null;
            LocalDateTime endTime = null;
            String start = dto.getStartTime();
            if (StringUtils.isNotBlank(start)) {
                startTime = DateUtil.parse(start).toLocalDateTime();
            }
            String end = dto.getEndTime();
            if (StringUtils.isNotBlank(end)) {
                endTime = DateUtil.parse(end).toLocalDateTime();
            }
            pageUtil();
            List<BizSignIn> bizSignIns = bizSignInService.selectPage(dto, startTime, endTime);

            return resultData(bizSignIns);
        } catch (Exception e) {
            logger.error("新增失败" + e.getMessage());
            return R.ok("新增失败");
        }
    }
}
