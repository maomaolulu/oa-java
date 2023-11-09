package com.ruoyi.activiti.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.activiti.domain.dto.BizOrSignDTO;
import com.ruoyi.activiti.domain.proc.BizBusiness;
import com.ruoyi.activiti.mapper.BizOrSignMapper;
import com.ruoyi.activiti.service.BizOrSignService;
import com.ruoyi.system.util.SystemUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wuYang
 * @date 2022/9/6 17:43
 */
@Service
public class BizOrSignServiceImpl implements BizOrSignService {
    @Resource
    BizOrSignMapper bizOrSignMapper;

    /**
     * 我的或签
     *
     * @param dto
     */
    @Override
    public List<BizBusiness> getOrSign(BizOrSignDTO dto) {

        Long userId = SystemUtil.getUserId();
        QueryWrapper<BizBusiness> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("b.user_id",userId);
        queryWrapper.like(StringUtils.isNotEmpty(dto.getProcDefKey()),"n.proc_def_key","%"+dto.getProcDefKey() +"%");
        queryWrapper.eq(dto.getCompanyId()!=null,"n.company_id",dto.getCompanyId());
        queryWrapper.eq(dto.getDeptId()!=null,"n.dept_id",dto.getDeptId());
        queryWrapper.like(StringUtils.isNotEmpty(dto.getApplyCode()),"n.apply_code",dto.getApplyCode());
        queryWrapper.eq(dto.getResult()!=null,"n.result",dto.getResult());
        queryWrapper.like(StringUtils.isNotEmpty(dto.getProcName()),"n.proc_name",dto.getProcName());
        queryWrapper.ne("result",3);
        queryWrapper.orderBy(StringUtils.isNotBlank(dto.getSortField())&&StringUtils.isNotBlank(dto.getSortOrder()), dto.getSortOrder().equals("asc"),"n.apply_time");

        return bizOrSignMapper.getOrSign(queryWrapper);

    }
}
