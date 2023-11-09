package com.ruoyi.daily.domain.sys.vo;

import com.ruoyi.daily.domain.sys.SysHelpFaqInfo;
import lombok.Data;

import java.util.List;

/**
 * Created by WuYang on 2022/8/18 14:34
 */
@Data
public class SysHelpFaqVO {
    /**
     *  帮助中心问答列表
     */
    private List<SysHelpFaqInfo> list;
    /**
     *  帮助中心问题 id
     */
    private Long id ;
    /**
     *  帮助中心标题
     */
    private String title;


}
