package com.ruoyi.daily.domain.asset.record.dto;

import com.ruoyi.daily.domain.asset.transfer.PurchaseTransferDTO;
import lombok.Data;

import java.util.List;

/**
 * @author wuYang
 * @date 2022/9/13 13:49
 */
@Data
public class PurchaseListDTO {

    private List<PurchaseTransferDTO> list;
}
