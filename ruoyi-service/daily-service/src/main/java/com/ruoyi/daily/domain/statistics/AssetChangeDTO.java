package com.ruoyi.daily.domain.statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 前端传数据 DTO----> 固定资产变化
 * @author wuYang
 * @date 2022/8/30 8:49
 */
@Data
public class AssetChangeDTO {
    /**
     * 公司id
     */
    @ApiModelProperty("公司id")
    private Long companyId;
    /**
     * 部门id
     */
    @ApiModelProperty("部门id")
    private Long deptId;
    /**
     * 统计维度 0 采购价 | 1 估值价 默认为 0
     */
    @ApiModelProperty("统计维度 0 采购价 | 1 估值价 默认为 0")
    private Integer dimension ;
    /**
     * 物品状态 状态:1在用;2准用,3待修,4维修中,5送检中,6报废,7外借,8封存 ,9库存
     */
    @ApiModelProperty("物品状态 状态:1在用;2准用,3待修,4维修中,5送检中,6报废,7外借,8封存 ,9库存")
    private Integer state;
    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    private String startTime;
    /**
     *  结束时间
     */
    @ApiModelProperty("结束时间")
    private String endTime;


}
