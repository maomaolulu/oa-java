package com.ruoyi.daily.domain.statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 前端传递的参数 -------> 资产占比分析
 * @author wuYang
 * @date 2022/8/30 13:46
 */
@Data
public class AssetProportionDTO {

    @ApiModelProperty("公司id")
    private Long companyId;


    @ApiModelProperty("统计维度 0 采购价 | 1 估值价 默认为 0")
    private Integer dimension ;

    @ApiModelProperty("物品状态 状态:1在用;2准用,3待修,4维修中,5送检中,6报废,7外借,8封存 ,9库存")
    private Integer state;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;


}
