package com.ruoyi.daily.service.government_centre;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.daily.domain.government_centre.BizMailRecord;

import java.util.List;

/**
 * 邮件记录
 * @author zx
 * @date 2022/3/24 15:12
 */
public interface BizMailRecordService {
    /**
     * 查询记录列表
     * @param mailRecord
     * @return
     */
    List<BizMailRecord> getList(BizMailRecord mailRecord);

    /**
     * 保存邮件记录
     * @param mailRecord
     */
    void save(BizMailRecord mailRecord);
}
