package com.ruoyi.daily.domain.sys.dto;

import com.ruoyi.daily.domain.sys.vo.AnsAndQuestionVO;
import lombok.Data;

import java.util.List;

/**
 * Created by WuYang on 2022/8/19 12:38
 */
@Data
public class SysHelpFaqUpdateDTO {
    private Long id;
    private String title;
    private List<AnsAndQuestionVO> list;
    private List<Long> deleteId;

}

