package com.ruoyi.training.entity.vo;

import com.ruoyi.training.entity.TraTrainCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author yrb
 * @Date 2023/2/14 15:35
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraTrainCategoryVO{
    private Integer companyId;
    private String companyName;
    private List<TraTrainCategory> list;
}
