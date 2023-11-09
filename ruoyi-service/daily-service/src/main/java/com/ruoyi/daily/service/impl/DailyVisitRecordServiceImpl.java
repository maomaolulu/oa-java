package com.ruoyi.daily.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.daily.domain.DailyVisitRecord;
import com.ruoyi.daily.domain.DailyVisitRecordInfo;
import com.ruoyi.daily.domain.dto.DailyVisitDto;
import com.ruoyi.daily.mapper.DailyVisitRecordMapper;
import com.ruoyi.daily.mapper.daily.DailyMapper;
import com.ruoyi.daily.service.DailyVisitRecordInfoService;
import com.ruoyi.daily.service.DailyVisitRecordService;
import com.ruoyi.file.domain.SysAttachment;
import com.ruoyi.file.feign.RemoteFileService;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 拜访打卡
 *
 * @author zh
 * @date 2023-03-30
 */
@Service
public class DailyVisitRecordServiceImpl extends ServiceImpl<DailyVisitRecordMapper, DailyVisitRecord> implements DailyVisitRecordService {
    private final DailyVisitRecordInfoService dailyVisitRecordInfoService;
    private final RemoteFileService remoteFileService;
    private final DailyMapper dailyMapper;

    @Autowired
    public DailyVisitRecordServiceImpl(DailyVisitRecordInfoService dailyVisitRecordInfoService,
                                       RemoteFileService remoteFileService,
                                       DailyMapper dailyMapper) {

        this.dailyVisitRecordInfoService = dailyVisitRecordInfoService;
        this.remoteFileService = remoteFileService;
        this.dailyMapper = dailyMapper;
    }

    /**
     * listPage
     */


    /**
     * 查询当前人打卡
     *
     * @param dailyVisitRecord
     * @return
     */
    @Override
    public DailyVisitRecord getDailyVisitRecord(DailyVisitRecord dailyVisitRecord) {
        //当前人
        Long userId = SystemUtil.getUserId();

        //打卡时间
        Date clockInDate = dailyVisitRecord.getClockInDate();

        DailyVisitRecord dailyVisitRecord1 = this.getOne(new QueryWrapper<DailyVisitRecord>()
                .eq("user_id", userId)
                .eq("clock_in_date", clockInDate)
        );
        if (dailyVisitRecord1 != null) {
            List<DailyVisitRecordInfo> list = dailyVisitRecordInfoService.list(new QueryWrapper<DailyVisitRecordInfo>()
                    .eq("visit_record_id", dailyVisitRecord1.getId())
            );
            dailyVisitRecord1.setDailyVisitRecordInfos(list);
            for (DailyVisitRecordInfo dailyVisitRecordInfo : list
            ) {
                Date clockInTime = dailyVisitRecordInfo.getClockInTime();
                dailyVisitRecordInfo.setClockInTimeStr(DateUtil.format(clockInTime, "HH:mm:ss"));
                // 将上传的临时文件转为有效文件

                List<SysAttachment> files = remoteFileService.getListByIds(dailyVisitRecordInfo.getParentId().toString(), "sign-in");
                dailyVisitRecordInfo.setFiles(files);

            }

        } else {
            return new DailyVisitRecord();
        }

        return dailyVisitRecord1;
    }

    /**
     * 新增拜访打卡
     *
     * @param dailyVisitRecord
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DailyVisitRecord saveDailyVisitRecord(DailyVisitRecord dailyVisitRecord) {
        //时间转年月日
        Date date = new Date();
        String format = DateUtil.format(date, "yyyy-MM-dd");
        Date parse = DateUtil.parse(format);


        //当前人
        Long userId = SystemUtil.getUserId();
        Long deptId = SystemUtil.getDeptId();
        dailyVisitRecord.setUserId(userId);
        dailyVisitRecord.setDeptId(deptId);
        dailyVisitRecord.setCreateByName(SystemUtil.getUserNameCn());
        dailyVisitRecord.setClockInDate(parse);
        dailyVisitRecord.setCompany(SystemUtil.getCompanyName());

        DailyVisitRecord dailyVisitRecord1 = this.getOne(new QueryWrapper<DailyVisitRecord>()
                .eq("user_id", userId)
                .eq("clock_in_date", parse)
        );
        //详情
        DailyVisitRecordInfo dailyVisitRecordInfo = dailyVisitRecord.getDailyVisitRecordInfos().get(0);
        dailyVisitRecordInfo.setClockInTime(new Date());
        //无之前记录
        if (dailyVisitRecord1 == null) {
            this.save(dailyVisitRecord);
            dailyVisitRecordInfo.setVisitRecordId(dailyVisitRecord.getId());
        } else {
            //已有当天打卡记录
            dailyVisitRecordInfo.setVisitRecordId(dailyVisitRecord1.getId());
        }

        dailyVisitRecordInfoService.save(dailyVisitRecordInfo);

        // 将上传的临时文件转为有效文件
        ArrayList<Long> longs = new ArrayList<>();
        longs.add(dailyVisitRecordInfo.getParentId());
        int reimburse = remoteFileService.updateByIds(longs, "sign-in");
        if (reimburse == 0) {
            throw new StatefulException("将上传的文件转为有效文件失败");
        }
        return dailyVisitRecord;
    }

    /**
     * 获取拜访记录列表
     * @return 集合
     */
    @Override
    public List<DailyVisitDto> getDailyVisitRecordList(DailyVisitRecord dailyVisitRecord) {
        // 获取用户角色
        Long userId = SystemUtil.getUserId();
        Integer roleNum = dailyMapper.getRoleNum(167, userId);
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
        if (roleNum < 1) {
            // 普通员工
            queryWrapper.eq("t2.user_id", userId);
            queryWrapper.orderByDesc("t1.create_time");
        } else {
            // 负责人
            queryWrapper.like(StrUtil.isNotBlank(dailyVisitRecord.getCreateByName()),"t2.create_by_name",dailyVisitRecord.getCreateByName());
            queryWrapper.orderByDesc("t1.create_time");
        }
        return dailyMapper.getList(queryWrapper);
    }


}
