package com.ruoyi.activiti.feign;

import com.ruoyi.activiti.domain.government_centre.BizMailRecord;
import com.ruoyi.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 邮件记录 Feign服务层
 * 
 * @author zmr
 * @date 2019-05-20
 */
@FeignClient(name = "daily-service")
public interface RemoteMailRecordService
{
    /**
     * 保存邮件记录
     * @return
     */
    @PostMapping("/mail_record/info")
    R save(@RequestBody BizMailRecord mailRecord);
}
