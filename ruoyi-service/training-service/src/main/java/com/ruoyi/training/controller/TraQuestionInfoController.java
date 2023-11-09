package com.ruoyi.training.controller;

import cn.hutool.core.exceptions.StatefulException;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.training.entity.TraQuestionInfo;
import com.ruoyi.training.entity.dto.QuestionDTO;
import com.ruoyi.training.entity.dto.SubmitDTO;
import com.ruoyi.training.entity.dto.VideoLog;
import com.ruoyi.training.service.TraQuestionInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

import static com.ruoyi.common.core.domain.R.data;

/**
 * 题目表
 * @author : zx
 * @date : 2022-5-30
 * @menu 题目表
 */
@Api(tags = "题目表")
@RestController
@RequestMapping("/tra_question")
public class TraQuestionInfoController extends BaseController{
    private final TraQuestionInfoService traQuestionInfoService;
    @Autowired
    public TraQuestionInfoController(TraQuestionInfoService traQuestionInfoService) {
        this.traQuestionInfoService = traQuestionInfoService;
    }
    
    /** 
     * 新增题目
     *
     * @param traQuestionInfo
     * @return 
     */
    @ApiOperation("新增题目")
    @PostMapping("info")
    @OperLog(title = "新增题目", businessType = BusinessType.INSERT)
    public R insert(@RequestBody TraQuestionInfo traQuestionInfo){
       try {
           traQuestionInfoService.insert(traQuestionInfo);
           return R.ok();
       }catch (Exception e){
           logger.error("新增题目失败",e);
           return R.error("新增题目失败");
       }
    }

     /**
      * 编辑题目
      *
      * @param traQuestionInfo
      * @return
      */
     @ApiOperation("编辑题目")
     @PutMapping("info")
     @OperLog(title = "编辑题目", businessType = BusinessType.UPDATE)
     public R update(@RequestBody TraQuestionInfo traQuestionInfo){
         try {
             traQuestionInfoService.update(traQuestionInfo);
             return R.ok();
         }catch (Exception e){
             logger.error("编辑题目失败",e);
             return R.error("编辑题目失败");
         }
     }

     /**
      * 删除题目
      *
      * @param id
      * @return
      */
     @ApiOperation("删除题目")
     @DeleteMapping("info/{id}")
     @OperLog(title = "删除题目", businessType = BusinessType.DELETE)
     public R delete(@PathVariable("id") BigInteger id){
         try {
             traQuestionInfoService.delete(id);
             return R.ok();
         }catch (Exception e){
             logger.error("删除题目失败",e);
             return R.error("删除题目失败");
         }
     }

     /**
      * 查询题目
      *
      * @param questionDto
      * @return
      */
     @ApiOperation("查询题目")
     @GetMapping("info_list")
     public R info(QuestionDTO questionDto){
         try {
             pageUtil();
             return traQuestionInfoService.getList(questionDto);
         }catch (Exception e){
             logger.error("查询题目失败",e);
             return R.error("查询题目失败");
         }
     }

     /**
      * 查询题目详情
      * @param id
      * @return
      */
     @ApiOperation("查询题目详情")
     @GetMapping("info")
     public R info(@RequestParam("id") BigInteger id){
         try {
             return data(traQuestionInfoService.getById(id));
         }catch (Exception e){
             logger.error("查询题目详情失败",e);
             return R.error("查询题目详情失败");
         }
     }

    /**
     * 生成试卷
     */
    @ApiOperation("生成试卷")
    @GetMapping("/generate")
    @OperLog(title = "生成试卷", businessType = BusinessType.OTHER)
    public R generate(Long examId){
        try {

            return data(traQuestionInfoService.generate(examId));
        }
        catch (StatefulException e){
            logger.error("生成试卷"+e.getMessage(), e);
            return R.error(e.getMessage());
        }
        catch (Exception e) {
            logger.error("生成试卷失败", e);
            return R.error("生成试卷失败");
        }
    }

    /**
     * 自定义试卷
     */
    @ApiOperation("自定义试卷")
    @GetMapping("/custom")
    @OperLog(title = "自定义试卷", businessType = BusinessType.OTHER)
    public R generateCustom(Long examId){
        try {

                return data(  traQuestionInfoService.generateNingBo(examId));

//            return data(traQuestionInfoService.generateCustom(examId));
        }
        catch (StatefulException e){
            logger.error("生成试卷"+e.getMessage(), e);
            return R.error(e.getMessage());
        }
        catch (Exception e) {
            logger.error("生成试卷失败", e);
            return R.error("生成试卷失败");
        }
    }

    /**
     * 提交试卷
     */
    @ApiOperation("提交试卷")
    @PostMapping("/submit")
    @OperLog(title = "提交试卷", businessType = BusinessType.INSERT)
    public R submit(@RequestBody SubmitDTO submitDTO){
        try {
            return data(traQuestionInfoService.submit(submitDTO));
        } catch (Exception e) {
            logger.error("提交试卷失败", e);
            return R.error("提交试卷失败");
        }
    }

    /**
     * 提交自定义试卷
     */
    @ApiOperation("提交自定义试卷")
    @PostMapping("/submit_costom")
    @OperLog(title = "提交自定义试卷", businessType = BusinessType.INSERT)
    public R submitCustom(@RequestBody SubmitDTO submitDTO){
        try {
            return data(traQuestionInfoService.submitCustom(submitDTO));
        } catch (Exception e) {
            logger.error("提交自定义试卷失败", e);
            return R.error("提交自定义试卷失败");
        }
    }

    /**
     * 查看错题
     */
    @ApiOperation("查看错题")
    @GetMapping("/err")
    public R getErrList(Long examId){
        try {
            return data(traQuestionInfoService.getErrList(examId));
        } catch (Exception e) {
            logger.error("查看错题失败", e);
            return R.error("查看错题失败");
        }
    }

    /**
     * 获取视频信息
     * @param courseId 课程id
     */
    @ApiOperation("获取视频信息")
    @GetMapping("/video_log")
    public R getVideoLog(String courseId,String md5){
        try {
            return data(traQuestionInfoService.getVideoLog(courseId,md5));
        } catch (Exception e) {
            logger.error("获取视频信息失败", e);
            return R.error("获取视频信息失败");
        }
    }

    /**
     * 保存视频观看记录
     */
    @ApiOperation("保存视频观看记录")
    @PostMapping("/video_log")
    @OperLog(title = "保存视频观看记录", businessType = BusinessType.INSERT)
    public R saveVideoLog(@RequestBody VideoLog videoLog){
        try {
            traQuestionInfoService.saveVideoLog(videoLog.getCourseId(),videoLog.getLog());
            return R.ok();
        } catch (Exception e) {
            logger.error("保存视频观看记录失败", e);
            return R.error("保存视频观看记录失败");
        }
    }


}