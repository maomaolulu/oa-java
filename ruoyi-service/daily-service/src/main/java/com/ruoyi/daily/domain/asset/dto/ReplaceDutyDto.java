package com.ruoyi.daily.domain.asset.dto;

import lombok.Data;

import java.util.List;

/**
 * 替换责任人
 * @author zx
 */
@Data
public class ReplaceDutyDto {
    /**
     * 固定资产id
     */
    private List<Long> ids;
    /**
     * 新责任人
     */
    private String newCharger;

}
