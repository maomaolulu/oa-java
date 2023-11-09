package com.ruoyi.activiti.mapper;

import com.ruoyi.activiti.domain.asset.BizClaimApply;
import com.ruoyi.common.core.dao.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * @author zx
 * @date 2021/11/30 20:07
 */
@Repository
public interface BizClaimApplyMapper extends BaseMapper<BizClaimApply> {
    /**
     * 新增
     * @param claimApply
     * @return
     */
    int insertBizClaimApply(BizClaimApply claimApply);

    int deleteClaimByIds(String[] ids);
    /**
     * logic删除
     * @param ids
     * @return
     * @author zmr
     */
    int deleteLogic(String[] ids);

}
