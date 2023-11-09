package com.ruoyi.quote.domain.dto;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.quote.domain.vo.QuoteBaseFactorVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author yrb
 * @Date 2022/5/10 10:55
 * @Version 1.0
 * @Description 检测岗位新增检测项目
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SheetItemsDTO extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 所属岗位名称
     */
    @Excel(name = "所属岗位名称")
    private String postName;

    /**
     * 报价单id
     */
    @Excel(name = "报价单id")
    private String sheetId;

    /**
     * 子类id
     */
    @Excel(name = "子类id")
    private Long subId;

    /**
     * 岗位id
     */
    @Excel(name = "岗位id")
    private Long postId;

    /**
     *  岗位要检测的项目
     */
    @Excel(name = "岗位要检测的项目")
    private List<SheetFactorItemsDTO> sheetItemsDTOList;

    @ApiModelProperty(value = "危害因素列表区分是否已关联岗位")
    private List<QuoteBaseFactorVO> quoteBaseFactorVOList;
}
