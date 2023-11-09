package com.ruoyi.quote.service.impl;

import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.quote.domain.entity.QuoteTestItem;
import com.ruoyi.quote.mapper.QuoteTestItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.mapper.QuotePointInfoMapper;
import com.ruoyi.quote.domain.entity.QuotePointInfo;
import com.ruoyi.quote.service.IQuotePointInfoService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 点位信息Service业务层处理
 *
 * @author yrb
 * @date 2022-06-17
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class QuotePointInfoServiceImpl implements IQuotePointInfoService {
    private final QuotePointInfoMapper quotePointInfoMapper;
    private final QuoteTestItemMapper quoteTestItemMapper;

    @Autowired
    public QuotePointInfoServiceImpl(QuotePointInfoMapper quotePointInfoMapper,
                                     QuoteTestItemMapper quoteTestItemMapper) {
        this.quotePointInfoMapper = quotePointInfoMapper;
        this.quoteTestItemMapper = quoteTestItemMapper;
    }

    /**
     * 查询点位信息
     *
     * @param id 点位信息主键
     * @return 点位信息
     */
    @Override
    public QuotePointInfo selectQuotePointInfoById(Long id) {
        return quotePointInfoMapper.selectQuotePointInfoById(id);
    }

    /**
     * 查询点位信息列表
     *
     * @param quotePointInfo 点位信息
     * @return 点位信息
     */
    @Override
    public List<QuotePointInfo> selectQuotePointInfoList(QuotePointInfo quotePointInfo) {
        return quotePointInfoMapper.selectQuotePointInfoList(quotePointInfo);
    }

    /**
     * 新增点位信息
     *
     * @param quotePointInfo 点位信息
     * @return 结果
     */
    @Override
    public int insertQuotePointInfo(QuotePointInfo quotePointInfo) {
        quotePointInfo.setCreateTime(DateUtils.getNowDate());
        return quotePointInfoMapper.insertQuotePointInfo(quotePointInfo);
    }

    /**
     * 修改点位信息
     *
     * @param quotePointInfo 点位信息
     * @return 结果
     */
    @Override
    public int updateQuotePointInfo(QuotePointInfo quotePointInfo) {
        quotePointInfo.setUpdateTime(DateUtils.getNowDate());
        return quotePointInfoMapper.updateQuotePointInfo(quotePointInfo);
    }

    /**
     * 批量删除点位信息
     *
     * @param ids 需要删除的点位信息主键
     * @return 结果
     */
    @Override
    public int deleteQuotePointInfoByIds(Long[] ids) {
        return quotePointInfoMapper.deleteQuotePointInfoByIds(ids);
    }

    /**
     * 删除点位信息信息
     *
     * @param id 点位信息主键
     * @return 结果
     */
    @Override
    public int deleteQuotePointInfoById(Long id) {
        return quotePointInfoMapper.deleteQuotePointInfoById(id);
    }

    /**
     * 删除点位信息及关联的检测项目信息
     *
     * @param quotePointInfo 点位主键id、表单id、子类id
     * @return 结果
     */
    @Override
    public boolean deleteQuotePointInfoRelationTestItem(QuotePointInfo quotePointInfo) {
        QuoteTestItem item = new QuoteTestItem();
        item.setSheetId(quotePointInfo.getSheetId());
        item.setSubId(quotePointInfo.getSubId());
        item.setPointId(quotePointInfo.getId());
        List<QuoteTestItem> quoteTestItemList = quoteTestItemMapper.selectQuoteTestItemList(item);
        if (CollUtil.isNotEmpty(quoteTestItemList)) {
            if (quoteTestItemMapper.deleteQuoteTestItemAddTemp(item) == 0) {
                throw new RuntimeException("删除关联的检测项目信息失败！");
            }
        }
        if (quotePointInfoMapper.deleteQuotePointInfoById(quotePointInfo.getId()) == 0) {
            throw new RuntimeException("删除关联的检测项目信息失败！");
        }
        return true;
    }

    /**
     * 新增点位信息 返回带主键id的实体类
     *
     * @param quotePointInfo 点位信息
     * @return 结果
     */
    @Override
    public QuotePointInfo insertQuotePointInfoReturnResult(QuotePointInfo quotePointInfo) {
        quotePointInfo.setCreateTime(new Date());
        if (quotePointInfoMapper.insertQuotePointInfo(quotePointInfo) == 0) {
            throw new RuntimeException("插入点位信息失败！");
        }
        return quotePointInfoMapper.selectQuotePointInfoById(quotePointInfo.getId());
    }
}
