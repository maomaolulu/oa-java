package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.asset.BizClaim;
import com.ruoyi.activiti.domain.asset.SkuDto;
import com.ruoyi.common.core.domain.R;

import java.util.List;

/**
 * @author zh
 * @date 2022-1-6
 * @desc 领用申请
 */
public interface BizClaimService {
    /**
     * 查询领用申请列表
     *
     * @param dto 领用申请
     * @return 领用申请
     */

    public List<BizClaim> selectBizClaim(BizClaim dto);

    /**
     * 新增领用申请
     *
     * @param bizClaims 新增领用申请
     * @return 结果
     */

    public R insertBizClaim(List<BizClaim>  bizClaims);
    /**
     * 修改领用申请
     * @param bizClaims
     * @return
     */
    R update(List<BizClaim> bizClaims);

    /**
     * 删除领用申请
     * @param
     * @return
     */
    R delete(String [] ids) ;

    /**
     * 直接出库
     * @param data
     */
    void skuOut(List<SkuDto> data);

}
