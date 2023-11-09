package com.ruoyi.quote.service.impl;

import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.quote.domain.vo.QuotePostInfoVO;
import com.ruoyi.quote.utils.QuoteUtil;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.quote.domain.dto.PostHarmFactorDTO;
import com.ruoyi.quote.domain.entity.QuoteHarmFactor;
import com.ruoyi.quote.mapper.QuoteHarmFactorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quote.mapper.QuotePostInfoMapper;
import com.ruoyi.quote.domain.entity.QuotePostInfo;
import com.ruoyi.quote.service.IQuotePostInfoService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 岗位信息Service业务层处理
 *
 * @author yrb
 * @date 2022-04-26
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class QuotePostInfoServiceImpl implements IQuotePostInfoService {
    private final QuotePostInfoMapper quotePostInfoMapper;
    private final QuoteHarmFactorMapper quoteHarmFactorMapper;
    private final RedisUtils redisUtils;

    @Autowired
    public QuotePostInfoServiceImpl(QuotePostInfoMapper quotePostInfoMapper,
                                    QuoteHarmFactorMapper quoteHarmFactorMapper,
                                    RedisUtils redisUtils) {
        this.quotePostInfoMapper = quotePostInfoMapper;
        this.quoteHarmFactorMapper = quoteHarmFactorMapper;
        this.redisUtils = redisUtils;
    }

    /**
     * 查询岗位信息
     *
     * @param id 岗位信息主键
     * @return 岗位信息
     */
    @Override
    public QuotePostInfo selectQuotePostInfoById(Long id) {
        return quotePostInfoMapper.selectPostInfoById(id);
    }

    /**
     * 查询岗位信息列表
     *
     * @param quotePostInfoVO 岗位信息
     * @return 岗位信息
     */
    @Override
    public List<QuotePostInfoVO> selectQuotePostInfoList(QuotePostInfoVO quotePostInfoVO) {
        String postCheckedIdsKey = QuoteUtil.getZwPostCheckedIdsKey(quotePostInfoVO.getSheetId(), quotePostInfoVO.getSubId(), SystemUtil.getUserId());
        String idsValue = redisUtils.get(postCheckedIdsKey);
        List<QuotePostInfoVO> quotePostInfoVOS = quotePostInfoMapper.selectPostInfoUserList(quotePostInfoVO);
        if (StrUtil.isNotBlank(idsValue)) {
            List<Long> list = QuoteUtil.getList(idsValue);
            if (CollUtil.isNotEmpty(quotePostInfoVOS)) {
                for (QuotePostInfoVO qpi : quotePostInfoVOS) {
                    if (list.contains(qpi.getId())) {
                        qpi.setChecked(QuoteUtil.CHECKED_FLAG);
                    }
                }
            }
        }
        return quotePostInfoVOS;
    }

    /**
     * 新增岗位信息
     *
     * @param quotePostInfo 岗位信息
     * @return 结果
     */
    @Override
    public int insertQuotePostInfo(QuotePostInfo quotePostInfo) {
        quotePostInfo.setCreateTime(DateUtils.getNowDate());
        quotePostInfo.setCreator(SystemUtil.getUserNameCn());
        return quotePostInfoMapper.insertQuotePostInfo(quotePostInfo);
    }

    /**
     * 新增岗位信息及危害因素
     *
     * @param postHarmFactorDTO 岗位信息及危害因素
     * @return 结果
     */
    @Override
    public boolean insertBatch(PostHarmFactorDTO postHarmFactorDTO) {
        QuotePostInfo quotePostInfo = postHarmFactorDTO.getQuotePostInfo();
        QuotePostInfo postInfo = new QuotePostInfo();
        postInfo.setPostName(quotePostInfo.getPostName());
        postInfo.setIndustryId(quotePostInfo.getIndustryId());
        List<QuotePostInfo> quotePostInfoList = quotePostInfoMapper.selectQuotePostInfoList(postInfo);
        if (CollUtil.isNotEmpty(quotePostInfoList)) {
            throw new RuntimeException("当前岗位已存在");
        }
        quotePostInfo.setCreateTime(DateUtils.getNowDate());
        quotePostInfo.setCreator(SystemUtil.getUserNameCn());
        if (quotePostInfoMapper.insertQuotePostInfo(quotePostInfo) == 0) {
            throw new RuntimeException("新增岗位失败");
        }
        List<QuoteHarmFactor> quoteHarmFactorList = postHarmFactorDTO.getQuoteHarmFactorList();
        for (QuoteHarmFactor quoteHarmFactor : quoteHarmFactorList) {
            quoteHarmFactor.setCreateTime(DateUtils.getNowDate());
            quoteHarmFactor.setPostId(quotePostInfo.getId());
        }
        if (quoteHarmFactorMapper.insertBatch(quoteHarmFactorList) == 0) {
            throw new RuntimeException("关联危害因素信息失败");
        }
        return true;
    }

    /**
     * 修改岗位信息
     *
     * @param quotePostInfo 岗位信息
     * @return 结果
     */
    @Override
    public int updateQuotePostInfo(QuotePostInfo quotePostInfo) {
        quotePostInfo.setUpdateTime(DateUtils.getNowDate());
        quotePostInfo.setCreator(SystemUtil.getUserNameCn());
        return quotePostInfoMapper.updateQuotePostInfo(quotePostInfo);
    }

    @Override
    public int updateQuotePostInfoWithHarmFactor(PostHarmFactorDTO postHarmFactorDTO) {
        QuotePostInfo quotePostInfo = postHarmFactorDTO.getQuotePostInfo();
        Long postInfoId = quotePostInfo.getId();
        quotePostInfo.setUpdateTime(DateUtils.getNowDate());
        List<QuoteHarmFactor> quoteHarmFactorList = postHarmFactorDTO.getQuoteHarmFactorList();
        int i = quotePostInfoMapper.updateQuotePostInfo(quotePostInfo);
        if (i > 0) {
            for (QuoteHarmFactor quoteHarmFactor : quoteHarmFactorList) {
                quoteHarmFactor.setCreateTime(DateUtils.getNowDate());
                quoteHarmFactor.setPostId(quotePostInfo.getId());
                // 删除原有的
                QuoteHarmFactor harmFactor = new QuoteHarmFactor();
                harmFactor.setPostId(postInfoId);
                quoteHarmFactorMapper.deleteQuoteHarmFactorByIdAndPostId(harmFactor);
            }
            return quoteHarmFactorMapper.insertBatch(quoteHarmFactorList);
        }
        return i;
    }

    /**
     * 批量删除岗位信息
     *
     * @param ids 需要删除的岗位信息主键
     * @return 结果
     */
    @Override
    public int deleteQuotePostInfoByIds(Long[] ids) {
        return quotePostInfoMapper.deleteQuotePostInfoByIds(ids);
    }

    /**
     * 删除岗位信息信息
     *
     * @param id 岗位信息主键
     * @return 结果
     */
    @Override
    public int deleteQuotePostInfoById(Long id) {
        return quotePostInfoMapper.deleteQuotePostInfoById(id);
    }

    /**
     * 根据行业id查询岗位信息
     *
     * @return 岗位列表
     */
    @Override
    public List<QuotePostInfo> selectQuotePostInfoByIds(Long[] ids) {
        return quotePostInfoMapper.selectQuotePostInfoByIds(ids);
    }

    /**
     * 查询岗位信息
     *
     * @param quotePostInfo 岗位名称 行业id
     * @return 岗位信息
     */
    @Override
    public QuotePostInfo findQuotePostInfoByIndustryIdAndName(QuotePostInfo quotePostInfo) {
        return quotePostInfoMapper.selectQuotePostInfoByIndustryIdAndName(quotePostInfo);
    }

    /**
     * 查询岗位信息(筛选后)
     *
     * @param quotePostInfo 岗位
     * @return 岗位信息
     */
    @Override
    public List<QuotePostInfo> findQuotePostInfoUserList(QuotePostInfo quotePostInfo) {
        return quotePostInfoMapper.selectQuotePostInfoUserList(quotePostInfo);
    }
}
