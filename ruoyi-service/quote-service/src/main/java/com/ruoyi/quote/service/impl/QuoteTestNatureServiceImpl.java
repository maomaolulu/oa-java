package com.ruoyi.quote.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.common.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.quote.utils.QuoteUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.mapper.QuoteTestNatureMapper;
import com.ruoyi.quote.domain.entity.QuoteTestNature;
import com.ruoyi.quote.service.IQuoteTestNatureService;

/**
 * 检测性质Service业务层处理
 *
 * @author yrb
 * @date 2022-09-13
 */
@Service
public class QuoteTestNatureServiceImpl extends ServiceImpl<QuoteTestNatureMapper, QuoteTestNature> implements IQuoteTestNatureService {
    private final QuoteTestNatureMapper quoteTestNatureMapper;
    private final RedisUtils redisUtils;

    @Autowired
    public QuoteTestNatureServiceImpl(QuoteTestNatureMapper quoteTestNatureMapper,
                                      RedisUtils redisUtils) {
        this.quoteTestNatureMapper = quoteTestNatureMapper;
        this.redisUtils = redisUtils;
    }

    /**
     * 查询检测性质
     *
     * @param id 检测性质主键
     * @return 检测性质
     */
    @Override
    public QuoteTestNature selectQuoteTestNatureById(Long id) {
        return quoteTestNatureMapper.selectQuoteTestNatureById(id);
    }

    /**
     * 查询检测性质列表
     *
     * @param quoteTestNature 检测性质
     * @return 检测性质
     */
    @Override
    public List<QuoteTestNature> selectQuoteTestNatureList(QuoteTestNature quoteTestNature) {
        return quoteTestNatureMapper.selectQuoteTestNatureList(quoteTestNature);
    }

    /**
     * 新增检测性质
     *
     * @param quoteTestNature 检测性质
     * @return 结果
     */
    @Override
    public int insertQuoteTestNature(QuoteTestNature quoteTestNature) {
        quoteTestNature.setCreateTime(DateUtils.getNowDate());
        return quoteTestNatureMapper.insertQuoteTestNature(quoteTestNature);
    }

    /**
     * 修改检测性质
     *
     * @param quoteTestNature 检测性质
     * @return 结果
     */
    @Override
    public int updateQuoteTestNature(QuoteTestNature quoteTestNature) {
        quoteTestNature.setUpdateTime(DateUtils.getNowDate());
        return quoteTestNatureMapper.updateQuoteTestNature(quoteTestNature);
    }

    /**
     * 批量删除检测性质
     *
     * @param ids 需要删除的检测性质主键
     * @return 结果
     */
    @Override
    public int deleteQuoteTestNatureByIds(Long[] ids) {
        return quoteTestNatureMapper.deleteQuoteTestNatureByIds(ids);
    }

    /**
     * 删除检测性质信息
     *
     * @param id 检测性质主键
     * @return 结果
     */
    @Override
    public int deleteQuoteTestNatureById(Long id) {
        return quoteTestNatureMapper.deleteQuoteTestNatureById(id);
    }

    /**
     * 标记选中的检测性质
     *
     * @param natureIds 检测性质id集合
     * @return result
     */
    @Override
    public List<QuoteTestNature> findQuoteTestNatureList(String natureIds) {
        String list = redisUtils.get(QuoteUtil.TEST_NATURE_LIST);
        List<QuoteTestNature> quoteTestNatureList = null;
        if (StrUtil.isBlank(list)) {
            synchronized (QuoteTestNatureServiceImpl.class) {
                if (StrUtil.isBlank(list)) {
                    quoteTestNatureList = quoteTestNatureMapper.selectQuoteTestNatureList(new QuoteTestNature());
                    redisUtils.set(QuoteUtil.TEST_NATURE_LIST, JSON.toJSONString(quoteTestNatureList), 4 * 3600L);
                }
            }
        } else {
            quoteTestNatureList = JSON.parseArray(list, QuoteTestNature.class);
        }
        if (StrUtil.isNotBlank(natureIds)) {
            List<String> ids = Arrays.asList(natureIds.split(","));
            if (CollUtil.isNotEmpty(quoteTestNatureList)) {
                for (QuoteTestNature quoteTestNature : quoteTestNatureList) {
                    if (ids.contains(quoteTestNature.getId().toString())){
                        quoteTestNature.setChecked(QuoteUtil.CHECKED_FLAG);
                    }
                }
            }
        }
        return quoteTestNatureList;
    }
}
