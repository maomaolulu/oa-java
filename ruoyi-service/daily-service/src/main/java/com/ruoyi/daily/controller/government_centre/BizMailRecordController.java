package com.ruoyi.daily.controller.government_centre;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.daily.domain.government_centre.BizMailRecord;
import com.ruoyi.daily.service.government_centre.BizMailRecordService;
import com.ruoyi.common.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.title;

/**
 * @author zx
 * @date 2022/3/24 15:53
 * @menu 邮件记录
 */
@RestController
@RequestMapping("mail_record")
public class BizMailRecordController extends BaseController {
    private final BizMailRecordService mailRecordService;

    @Autowired
    public BizMailRecordController(BizMailRecordService mailRecordService) {
        this.mailRecordService = mailRecordService;
    }

    /**
     * 查询邮件记录
     *
     * @param mailRecord
     * @return
     */
    @GetMapping("list")
    public R getList(BizMailRecord mailRecord) {
        try {
            pageUtil();
            return resultData(mailRecordService.getList(mailRecord));
        } catch (Exception e) {
            logger.error("查询记录失败", e);
            return R.error("查询记录失败");
        }
    }

    /**
     * 新增记录
     * @param mailRecord
     * @return
     */
    @PostMapping("info")
    public R save(@RequestBody BizMailRecord mailRecord) {
        try {
            mailRecordService.save(mailRecord);
            return R.ok();
        } catch (Exception e) {
            logger.error("查保存邮件记录失败", e);
            return R.error("保存邮件记录失败");
        }
    }
}
