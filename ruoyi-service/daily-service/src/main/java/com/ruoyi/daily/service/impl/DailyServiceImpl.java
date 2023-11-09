package com.ruoyi.daily.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.daily.domain.BizDaily;
import com.ruoyi.daily.domain.BizDailyUser;
import com.ruoyi.daily.domain.vw.ViewDailyList;
import com.ruoyi.daily.mapper.daily.BizDailyUserMapper;
import com.ruoyi.daily.mapper.daily.DailyMapper;
import com.ruoyi.daily.mapper.daily.ViewDailyListMapper;
import com.ruoyi.daily.service.DailyService;
import com.ruoyi.system.feign.RemoteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;

/**
 * 日报
 *
 * @author zx
 * @date 2022/1/8 17:36
 */
@Service
public class DailyServiceImpl implements DailyService {
    private final ViewDailyListMapper viewDailyListMapper;
    private final DailyMapper dailyMapper;
    private final BizDailyUserMapper dailyUserMapper;
    private final RemoteUserService remoteUserService;

    @Autowired
    public DailyServiceImpl(ViewDailyListMapper viewDailyListMapper, DailyMapper dailyMapper, BizDailyUserMapper dailyUserMapper, RemoteUserService remoteUserService) {
        this.viewDailyListMapper = viewDailyListMapper;
        this.dailyMapper = dailyMapper;
        this.dailyUserMapper = dailyUserMapper;
        this.remoteUserService = remoteUserService;
    }

    /**
     * 查询接收人日报列表
     *
     * @param viewDailyList
     * @return
     */
    @Override
    public List<ViewDailyList> getVwList(ViewDailyList viewDailyList) {
        Long userId = SystemUtil.getUserId();
        QueryWrapper<ViewDailyList> wrapper = new QueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(viewDailyList.getUserName()), "user_name", viewDailyList.getUserName());
        wrapper.between(viewDailyList.getStartTime() != null && viewDailyList.getEndTime() != null, "create_time", viewDailyList.getStartTime(), viewDailyList.getEndTime());
        wrapper.eq(StrUtil.isNotBlank(viewDailyList.getIsRead()), "is_read", viewDailyList.getIsRead());
        wrapper.eq("receiver", userId);
        return viewDailyListMapper.selectList(wrapper);
    }

    /**
     * 查询拜访日报
     *
     * @param daily 查询条件
     * @return 查询结果
     */
    @Override
    public List<BizDaily> getDailyRecordList(BizDaily daily) {
        // 判断用户角色
        Long userId = SystemUtil.getUserId();
        QueryWrapper<BizDaily> wrapper = new QueryWrapper<>();
        Integer roleNum = dailyMapper.getRoleNum(167, userId);
        if (roleNum < 1) {
            // 普通员工
            wrapper.eq("user_id", userId);
            wrapper.like(StrUtil.isNotBlank(daily.getCreateBy()), "create_by", daily.getCreateBy());
            wrapper.orderByDesc("create_time");
        } else {
            // 部门负责人
            wrapper.like(StrUtil.isNotBlank(daily.getCreateBy()), "create_by", daily.getCreateBy());
            wrapper.orderByDesc("create_time");
        }
        return dailyMapper.selectList(wrapper);
    }

    /**
     * 提交拜访日报
     *
     * @param daily 日报信息
     * @return 日报是否添加成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R save(BizDaily daily) {
        try {
            // 校验是否提交过日报
            Long userId = SystemUtil.getUserId();
            QueryWrapper<BizDaily> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.eq("daily_time", daily.getDailyTime());
            BizDaily bizDaily = dailyMapper.selectOne(queryWrapper);
            if (bizDaily != null) {
                return R.error("当前日期已提交过日报");
            }

            // 设置默认信息
            daily.setCreateBy(SystemUtil.getUserNameCn());
            daily.setCreateTime(new Date());
            daily.setUserId(userId);
            daily.setDeptId(SystemUtil.getDeptId());
            daily.setComId(SystemUtil.getCompanyId());
            if (dailyMapper.insert(daily) == 1) {
                return R.ok("提交成功");
            }
            return R.error("提交失败");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }

    }

    /**
     * 查询日报
     *
     * @param id
     * @return
     */
    @Override
    public BizDaily getById(Long id) {
        return dailyMapper.selectById(id);
    }

    /**
     * 编辑日报
     *
     * @param daily
     */
    @Override
    public void update(BizDaily daily) {
        String userName = SystemUtil.getUserName();
        Date date = new Date();
        dailyMapper.updateById(daily);

//        QueryWrapper<BizDailyUser> wrapper = new QueryWrapper<>();
//        wrapper.eq("daily_id",daily.getId());
//        dailyUserMapper.delete(wrapper);
//        daily.getDailyUser().stream().forEach(bizDailyUser -> {
//            bizDailyUser.setDailyId(daily.getId());
//            bizDailyUser.setCreateBy(userName);
//            bizDailyUser.setCreateTime(date);
//            dailyUserMapper.insert(bizDailyUser);
//        });
    }

    /**
     * 已读标记
     *
     * @param id
     */
    @Override
    public void updateRead(Long id) {
        BizDailyUser bizDailyUser = dailyUserMapper.selectById(id);
        bizDailyUser.setIsRead("1");
        dailyUserMapper.updateById(bizDailyUser);
    }

    /**
     * 查询选中的日期是否提交过日报
     *
     * @param bizDaily 按日期查
     * @return 日报记录
     */
    @Override
    public BizDaily getDailyOne(BizDaily bizDaily) {
        Long userId = bizDaily.getUserId();
        QueryWrapper<BizDaily> queryWrapper = new QueryWrapper<>();
        if (userId == null) {
            queryWrapper.eq("user_id", SystemUtil.getUserId());
        } else {
            queryWrapper.eq("user_id", userId);
        }
        queryWrapper.eq("daily_time", bizDaily.getDailyTime());
        return dailyMapper.selectOne(queryWrapper);
    }
}
