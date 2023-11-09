package com.ruoyi.activiti.service;

import java.util.List;

import com.ruoyi.activiti.domain.proc.ActReProcdef;
import com.ruoyi.activiti.domain.proc.ActReProcdefPlus;

/**
 * <p>File：IActReProcdefService.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2020 2020年1月6日 上午10:21:18</p>
 * <p>Company: zmrit.com </p>
 * @author zmr
 * @version 1.0
 */
public interface IActReProcdefService
{
    /**
     * 获取流程定义列表
     * @param actReProcdef
     * @return
     */
    public List<ActReProcdefPlus> selectList(ActReProcdef actReProcdef);

    /**
     * 获取流程定义信息
     * @param name
     * @return
     */
    ActReProcdef getActReProcdefs(String name);
}
