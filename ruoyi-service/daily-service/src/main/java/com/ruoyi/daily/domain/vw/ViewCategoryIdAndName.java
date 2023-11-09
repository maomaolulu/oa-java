package com.ruoyi.daily.domain.vw;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Author 郝佳星
 * Date 2022/6/15 17:32
 **/
@TableName("View_category_id_and_name")
@Data
public class ViewCategoryIdAndName {
    /** 品类id */
    private Long id ;


    /** 品类名称 */
    private String categoryName ;
}
