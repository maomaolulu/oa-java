package com.ruoyi.daily.domain.sys.vo;

import lombok.Data;

/**
 *  编辑修改帮助中心VO
 * Created by WuYang on 2022/8/19 13:54
 */
@Data
public class AnsAndQuestionVO {
    private Long id;
    private Long fid;
    private String answer;
    private String question;
}
