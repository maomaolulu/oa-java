package com.ruoyi.training.mapper;

import com.ruoyi.training.entity.TraManage;
import com.ruoyi.training.entity.dto.TrainingManageDTO;
import com.ruoyi.training.entity.vo.TrainingManageVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author yrb
 * @Date 2022/6/2 10:08
 * @Version 1.0
 * @Description 培训管理Mapper接口
 */
@Repository
public interface TrainingManageMapper {

    /**
     * 获取培训管理列表
     *
     * @return
     */
    List<TrainingManageVO> selectTrainingManageList(TrainingManageDTO trainingManageDto);

    /**
     * 获取培训管理列表
     *
     * @param manage 可传值
     * @return 培训列表
     */
    List<TraManage> selectTraManageList(TraManage manage);

    /**
     * 获取公司培训完成率，考核通过率
     *
     * @param companyId 公司id
     * @return 获取培训完成率，考核通过率
     * @author hjy
     * @date 2022/6/20 11:05
     */
    Map<String, Object> getRateByCompanyId(Long companyId);

    /**
     * 获取部门培训完成率，考核通过率
     *
     * @param deptId 部门id
     * @return 获取培训完成率，考核通过率
     * @author hjy
     * @date 2022/6/20 11:05
     */
    Map<String, Object> getRateByDeptId(Long deptId);

    /**
     * 获取用户对应部门学时
     *
     * @param userId 用户id
     * @param type   用户类型
     * @return 结果集
     */
    Map<String, Object> getDeptHours(@Param("userId") Long userId, @Param("type") Integer type);

    /**
     * 获取更多培训管理列表信息-优维
     *
     * @param userId  用户id
     * @param deptIds 部门id集合
     * @return 结果集
     */
    List<TraManage> selectMoreTraManageList(@Param("userId") Long userId, @Param("deptIds") List<Long> deptIds);

    /**
     * 筛选出公司id
     *
     * @param ids 自定义部门信息
     * @return 公司id集合
     */
    List<Map<String, Object>> getCompanyId(@Param("ids") List<String> ids);
}
