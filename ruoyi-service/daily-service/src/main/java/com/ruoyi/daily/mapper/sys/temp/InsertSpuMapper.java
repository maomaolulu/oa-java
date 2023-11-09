package com.ruoyi.daily.mapper.sys.temp;

import com.ruoyi.daily.domain.asset.AaSpu;
import com.ruoyi.daily.domain.sys.temp.InsertSpu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by WuYang on 2022/8/29 11:50
 */
@Repository
public interface InsertSpuMapper {
    @Select("SELECT " +
            " ( sum( IF ( operation = 1, amount, 0 ))- sum( IF ( operation = 2, amount, 0 )) ) AS number, " +
            " spu_id  " +
            "FROM " +
            " aa_sku  " +
            "GROUP BY " +
            " spu_id"
            )
    List<InsertSpu> getList();

    @Select("SELECT * FROM aa_spu WHERE storage_num is NULL")
    List<AaSpu> selectNull();

    @Update("update aa_spu set storage_num =#{storage_num} where id = #{id}")
    void  updateStorageNumById(@Param("id") Long id,@Param("storage_num") Integer storageNum);
}
