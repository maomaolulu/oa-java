package com.ruoyi.activiti.service.impl;

import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.activiti.domain.proc.ActReProcdefPlus;
import com.ruoyi.activiti.mapper.ActReProcdefPlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.mapper.ActReProcdefMapper;
import com.ruoyi.activiti.service.IActReProcdefService;
import tk.mybatis.mapper.entity.Example;

/**
 * <p>File：ActReProcdefServiceImpl.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2020 2020年1月6日 上午10:22:35</p>
 * <p>Company: zmrit.com </p>
 *
 * @author zmr
 * @version 1.0
 */
@Service
public class ActReProcdefServiceImpl implements IActReProcdefService {
    @Autowired
    private ActReProcdefMapper procdefMapper;
    @Autowired
    private ActReProcdefPlusMapper procdefPlusMapper;


    /* (non-Javadoc)
     * @see com.ruoyi.activiti.service.IActReProcdefService#selectList(com.ruoyi.activiti.domain.proc.ActReProcdef)
     */
    @Override
    public List<ActReProcdefPlus> selectList(ActReProcdef actReProcdef) {
        QueryWrapper<ActReProcdefPlus> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(actReProcdef.getKey()),"KEY_",actReProcdef.getKey());
        wrapper.orderByDesc("VERSION_");
//        return procdefMapper.select(actReProcdef);
        return procdefPlusMapper.getList(wrapper);

    }

    /**
     * 获取流程定义信息
     *
     * @param name
     * @return
     */
    @Override
    public ActReProcdef getActReProcdefs(String name) {
        Example example = new Example(ActReProcdef.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", name);
        example.setOrderByClause("VERSION_ DESC");
        List<ActReProcdef> actReProcdefs = procdefMapper.selectByExample(example);
        if (actReProcdefs.isEmpty()) {
            return null;
        }
        return actReProcdefs.get(0);
    }
}
