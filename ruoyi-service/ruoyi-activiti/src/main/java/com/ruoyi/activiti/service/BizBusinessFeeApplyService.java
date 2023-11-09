package com.ruoyi.activiti.service;


import com.ruoyi.activiti.domain.fiance.BizBusinessFeeApply;
import com.ruoyi.activiti.domain.dto.BizBusinessFeeApplyDto;
import com.ruoyi.common.core.domain.R;


import java.util.List;

/**
 * @author zh
 * @date 2021/12/17
 */
public interface BizBusinessFeeApplyService {

    /**
     * 查询业务费申请
     *
     * @param id 业务费申请ID
     * @return 业务费申请
     */

     BizBusinessFeeApply selectBizBusinessFeeApplyById(Integer id) ;
    /**
     * 查询业务费申请列表
     *
     * @param
     * @return
     */
     List<BizBusinessFeeApplyDto> selectBizBusinessFeeApplyListAll(BizBusinessFeeApplyDto dto) ;

    /**
     * 新增业务费申请
     *
     * @param bizBusinessFeeApply 新增业务费申请)
     * @return 结果
     */

     R insertBizPurchaseApply(BizBusinessFeeApply bizBusinessFeeApply) ;

    /**
     * 修改业务费用申请
     *
     * @param bizBusinessFeeApply 修改业务费用申请
     * @return 结果
     */

    BizBusinessFeeApply updateBizBusinessFeeApply(BizBusinessFeeApply bizBusinessFeeApply) ;

    /**
     * 删除业务费用申请
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */

     void deleteBizBusinessFeeApplyByIds(Integer[] ids) ;

    /**
     * 查询详情赋值抄送人
     * @param bizBusinessFeeApply
     * @return
     */
    BizBusinessFeeApplyDto getPurchase(BizBusinessFeeApplyDto bizBusinessFeeApply);

    /**
     * 查询详情
     * @param businessKey
     * @return
     */
    BizBusinessFeeApplyDto selectOne(String businessKey);
}
