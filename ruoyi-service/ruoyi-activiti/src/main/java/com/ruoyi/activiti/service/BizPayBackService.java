package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.fiance.BizPayBack;
import com.ruoyi.common.core.domain.R;

import java.util.List;

/**
 * @author zh
 * @date 2021/12/26
 * @desc 回款管理
 */
public interface BizPayBackService {

    /**
     * 查询回款管理列表
     *
     * @param dto 回款管理
     * @return 回款管理
     */

    public List<BizPayBack> selectBizPayBack(BizPayBack dto);

    /**
     * 新增回款管理
     *
     * @param bizPayBack 新增回款管理
     * @return 结果
     */

    public R insertBizPayBack(BizPayBack bizPayBack);
    /**
     * 修改收款账号
     * @param bizPayBack
     * @return
     */
    R update(BizPayBack bizPayBack);

    /**
     * 删除收款账号
     * @param
     * @return
     */
    R delete(Long [] ids) ;

}
