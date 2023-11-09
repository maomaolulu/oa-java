package com.ruoyi.daily.service.sys;

import com.ruoyi.daily.domain.sys.dto.SysHelpFaqAddDTO;
import com.ruoyi.daily.domain.sys.dto.SysHelpFaqAddList;
import com.ruoyi.daily.domain.sys.dto.SysHelpFaqUpdateDTO;
import com.ruoyi.daily.domain.sys.vo.SysHelpFaqVO;

import java.util.List;

/**
 * 帮助中心服务
 * Created by WuYang on 2022/8/18 10:08
 */
public interface SysHelpFaqService  {
    /**
     * 获取帮助中心列表
     */
    List<SysHelpFaqVO> getLists();

    /**
     * 新增帮助中心条目
     */
    void add(SysHelpFaqAddList dto);
    /**
     *  删除
     */
    void delete(Long id);
    /**
     * 查询单个
     */
    SysHelpFaqVO get(Long id);
    /**
     * 更新
     */
    void update(SysHelpFaqUpdateDTO updateDTO);


}
