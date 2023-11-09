package com.ruoyi.daily.service.impl.sys;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.daily.domain.sys.SysHelpFaq;
import com.ruoyi.daily.domain.sys.SysHelpFaqInfo;
import com.ruoyi.daily.domain.sys.dto.SysHelpFaqAddDTO;
import com.ruoyi.daily.domain.sys.dto.SysHelpFaqAddList;
import com.ruoyi.daily.domain.sys.dto.SysHelpFaqUpdateDTO;
import com.ruoyi.daily.domain.sys.vo.AnsAndQuestionVO;
import com.ruoyi.daily.domain.sys.vo.SysHelpFaqVO;
import com.ruoyi.daily.mapper.sys.SysHelpFaqInfoMapper;
import com.ruoyi.daily.mapper.sys.SysHelpFaqMapper;
import com.ruoyi.daily.service.sys.SysHelpFaqInfoService;
import com.ruoyi.daily.service.sys.SysHelpFaqService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by WuYang on 2022/8/18 10:10
 */
@Service
public class SysHelpFaqServiceImpl implements SysHelpFaqService {

    private final SysHelpFaqMapper sysHelpFaqMapper;
    private final SysHelpFaqInfoMapper sysHelpFaqInfoMapper;

    @Resource
    SysHelpFaqInfoService sysHelpFaqInfoService;

    @Autowired
    public SysHelpFaqServiceImpl(SysHelpFaqMapper sysHelpFaqMapper, SysHelpFaqInfoMapper sysHelpFaqInfoMapper) {
        this.sysHelpFaqMapper = sysHelpFaqMapper;
        this.sysHelpFaqInfoMapper = sysHelpFaqInfoMapper;
    }

    @Override
    public List<SysHelpFaqVO> getLists() {
        List<SysHelpFaq> sysHelpFaqs = sysHelpFaqMapper.selectList(new QueryWrapper<>());
        return sysHelpFaqs.stream().map((e) -> {
            QueryWrapper<SysHelpFaqInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("fid", e.getId());
            queryWrapper.orderByDesc("id");
            List<SysHelpFaqInfo> sysHelpFaqInfos = sysHelpFaqInfoMapper.selectList(queryWrapper);
            SysHelpFaqVO temp = new SysHelpFaqVO();
            temp.setList(sysHelpFaqInfos);
            temp.setId(e.getId());
            temp.setTitle(e.getTitle());
            return temp;
        }).collect(Collectors.toList());
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(SysHelpFaqAddList dto) {

        List<SysHelpFaqAddDTO> list = dto.getList();
        list.forEach(e->{
            if (dto.getId()==null){
                // 新增sys_help_faq表
                SysHelpFaq sysHelpFaq = new SysHelpFaq();
                sysHelpFaq.setTitle(dto.getTitle());
                sysHelpFaqMapper.insert(sysHelpFaq);
                // 新增sys_help_faq_info
                SysHelpFaqInfo info = SysHelpFaqInfo.builder()
                        .answer(e.getAnswer())
                        .question(e.getQuestion())
                        .fid(sysHelpFaq.getId()).build();

                sysHelpFaqInfoService.add(info);
            }else {
                SysHelpFaqInfo info = SysHelpFaqInfo.builder()
                        .answer(e.getAnswer())
                        .question(e.getQuestion())
                        .fid(dto.getId()).build();

                sysHelpFaqInfoService.add(info);
            }


        });
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long id) {
        // 删除sys_help_faq表内容
        sysHelpFaqMapper.deleteById(id);
        // 删除sys_help_faq_info
        QueryWrapper<SysHelpFaqInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fid",id);
        List<SysHelpFaqInfo> sysHelpFaqInfos = sysHelpFaqInfoMapper.selectList(queryWrapper);
        sysHelpFaqInfos.forEach(e -> {
            sysHelpFaqInfoMapper.deleteById(e.getId());
        });
    }

    public void deleteInfo(Long id) {
        sysHelpFaqInfoMapper.deleteById(id);
    }

    @Override
    public SysHelpFaqVO get(Long id) {
        SysHelpFaq sysHelpFaq = sysHelpFaqMapper.selectById(id);
        QueryWrapper<SysHelpFaqInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fid", sysHelpFaq.getId());
        List<SysHelpFaqInfo> sysHelpFaqInfos = sysHelpFaqInfoMapper.selectList(queryWrapper);
        SysHelpFaqVO temp = new SysHelpFaqVO();
        temp.setList(sysHelpFaqInfos);
        temp.setId(sysHelpFaq.getId());
        temp.setTitle(sysHelpFaq.getTitle());
        return temp;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(SysHelpFaqUpdateDTO updateDTO) {
        if (updateDTO.getDeleteId().size()>0){
            List<Long> deleteId = updateDTO.getDeleteId();
            deleteId.forEach(this::deleteInfo);
        }

        // 更新title
        SysHelpFaq sysHelpFaq = new SysHelpFaq();
        BeanUtils.copyProperties(updateDTO,sysHelpFaq);
        sysHelpFaqMapper.updateById(sysHelpFaq);
        // 跟新条目
        List<AnsAndQuestionVO> list = updateDTO.getList();
        list.forEach(e -> {
            SysHelpFaqInfo sysHelpFaqInfo = new SysHelpFaqInfo();
            BeanUtils.copyProperties(e,sysHelpFaqInfo);
            sysHelpFaqInfoMapper.updateById(sysHelpFaqInfo);
        });

    }
}
