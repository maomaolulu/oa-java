package com.ruoyi.training.service;

import com.ruoyi.training.entity.TraManage;
import com.ruoyi.training.entity.dto.TrainingManageDTO;
import com.ruoyi.training.entity.vo.TrainingManageVO;

import java.util.List;
import java.util.Map;

/**
 * @Author yrb
 * @Date 2022/6/2 10:10
 * @Version 1.0
 * @Description 培训管理Service接口
 */
public interface ITrainingManageService {
    /**
     * 获取培训管理列表
     *
     * @return
     */
    List<TrainingManageVO> findTrainingManageList(TrainingManageDTO trainingManageDto);

    /**
     * 获取培训管理列表
     *
     * @param manage 可传值
     * @return 培训列表
     */
    List<TraManage> selectTraManageList(TraManage manage);

    /**
     * 获取培训完成率，考核通过率
     *
     * @param userId 用户id
     * @return 完成率，考核通过率
     */
    Map<String, Object> getRate(Long userId);

    /**
     * 获取员工培训情况
     *
     * @param userId 用户id
     * @return 结果集
     * @author hjy
     * @date 2022/6/20 17:34
     */
    Map<String, Object> getTrainingDetail(Long userId);

    /**
     * 获取更多考试通过率（公司级别）
     *
     * @param userId 当前登陆人
     * @return 结果
     */
    List<Map<String, Object>> getMoreRate(Long userId);
}
