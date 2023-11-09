package com.ruoyi.training.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.training.entity.TraManage;
import com.ruoyi.training.entity.dto.TrainingManageDTO;
import com.ruoyi.training.entity.vo.TrainingManageVO;
import com.ruoyi.training.service.ITrainingManageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 培训管理controller
 *
 * @author yrb
 * @Date 2022/6/2 9:51
 */
@RestController
@RequestMapping("/training/manage")
public class TraManageController extends BaseController {
    private final ITrainingManageService trainingManageService;

    public TraManageController(ITrainingManageService trainingManageService) {
        this.trainingManageService = trainingManageService;
    }

    /**
     * 获取培训管理列表
     */
    @GetMapping("/list")
    public R list(TrainingManageDTO trainingManageDto) {
        try {
            pageUtil();
            List<TrainingManageVO> list = trainingManageService.findTrainingManageList(trainingManageDto);
            return resultData(list);
        } catch (Exception e) {
            logger.error("查询课程信息列表失败，异常信息：" + e);
            return R.error("查询课程信息列表失败");
        }
    }

    /**
     * 获取培训管理列表-新
     */
    @PostMapping("/newList")
    public R newList(@RequestBody TraManage manage) {
        try {
            pageUtil();
            List<TraManage> list = trainingManageService.selectTraManageList(manage);
            return resultData(list);
        } catch (Exception e) {
            logger.error("查询课程信息列表失败，异常信息：" + e);
            return R.error("查询课程信息列表失败");
        }
    }

    /**
     * 获取-->培训完成率、考核通过率
     */
    @GetMapping("/getRate")
    public R getRate(Long userId) {
        try {
            Map<String, Object> map = trainingManageService.getRate(userId);
            return R.data(map);
        } catch (Exception e) {
            logger.error("获取培训完成率、考核通过率失败，异常信息：" + e);
            return R.error("获取培训完成率、考核通过率失败");
        }
    }

    /**
     * 获取-->更多培训完成率、考核通过率（优维优化）
     */
    @GetMapping("/getMoreRate")
    public R getMoreRate(Long userId) {
        try {
            List<Map<String, Object>> listMaps = trainingManageService.getMoreRate(userId);
            return R.data(listMaps);
        } catch (Exception e) {
            logger.error("获取更多培训完成率、考核通过率失败，异常信息：" + e);
            return R.error("获取更多培训完成率、考核通过率失败");
        }
    }

    /**
     * 获取员工培训情况
     *
     * @param userId 用户id
     * @return 结果集
     * @author hjy
     * @date 2022/6/20 17:31
     */
    @GetMapping("/getTrainingDetail")
    private R getTrainingDetail(Long userId) {
        try {
            Map<String, Object> map = trainingManageService.getTrainingDetail(userId);
            return R.data(map);
        } catch (Exception e) {
            logger.error("获取员工培训情况失败，异常信息：" + e);
            return R.error("获取员工培训详情失败！");
        }
    }
}
