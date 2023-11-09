package com.ruoyi.daily.service.impl.government_centre;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.daily.domain.government_centre.BizMailRecord;
import com.ruoyi.daily.mapper.government_centre.BizMailRecordMapper;
import com.ruoyi.daily.service.government_centre.BizMailRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 邮件记录
 *
 * @author zx
 * @date 2022/3/24 15:12
 */
@Service
public class BizMailRecordServiceImpl implements BizMailRecordService {
    private final BizMailRecordMapper mailRecordMapper;

    @Autowired
    public BizMailRecordServiceImpl(BizMailRecordMapper mailRecordMapper) {
        this.mailRecordMapper = mailRecordMapper;
    }

    /**
     * 查询记录列表
     *
     * @param mailRecord
     * @return
     */
    @Override
    public List<BizMailRecord> getList(BizMailRecord mailRecord) {
        QueryWrapper<BizMailRecord> wrapper = new QueryWrapper<>();
        wrapper.between(StrUtil.isNotBlank(mailRecord.getStartDate())&&StrUtil.isNotBlank(mailRecord.getEndDate()),"create_time", mailRecord.getStartDate()+" 00:00:00",mailRecord.getEndDate()+" 23:59:59");
        wrapper.like(StrUtil.isNotBlank(mailRecord.getCreateBy()),"create_by",mailRecord.getCreateBy());
        wrapper.like(StrUtil.isNotBlank(mailRecord.getSendTo()),"send_to",mailRecord.getSendTo());
        wrapper.like(StrUtil.isNotBlank(mailRecord.getSubject()),"subject",mailRecord.getSubject());
        wrapper.eq(StrUtil.isNotBlank(mailRecord.getSendStatus()),"send_status",mailRecord.getSendStatus());
        wrapper.orderByDesc("create_time");
        List<BizMailRecord> bizMailRecords = mailRecordMapper.selectList(wrapper);
        return bizMailRecords;
    }

    /**
     * 保存邮件记录
     *
     * @param mailRecord
     */
    @Override
    public void save(BizMailRecord mailRecord) {
        mailRecord.setCreateTime(new Date());
        mailRecordMapper.insert(mailRecord);
    }
}
