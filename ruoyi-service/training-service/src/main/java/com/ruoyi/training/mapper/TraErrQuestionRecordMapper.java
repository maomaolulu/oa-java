package com.ruoyi.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.training.entity.TraErrQuestionRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zx
 * @date 2022-06-15 11:20:10
 */
@Repository
public interface TraErrQuestionRecordMapper extends BaseMapper<TraErrQuestionRecord> {
    /**
     * 查看错题
     * @param userId
     * @return
     */
    @Select("")
    List<TraErrQuestionRecord> getErrList(@Param("userId") Long userId);
}
