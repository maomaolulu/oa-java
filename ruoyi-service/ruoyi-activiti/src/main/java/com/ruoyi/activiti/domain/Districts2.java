package com.ruoyi.activiti.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 地区表 districts
 * 
 * @author ruoyi
 * @date 2018-12-19
 */
@Data
@TableName("districts")
public class Districts2 implements Serializable
{
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    /** 编号 */
    private Integer           id;

    /** 上级编号 */
    private Integer           pid;

    /** 层级 */
    private Integer           deep;

    /** 名称 */
    private String            name;


    /** 拼音 */
    private String            pinyin;

    /** 拼音缩写 */
    private String            pinyinShor;

    /** 扩展名 */
    private String            extName;

    /** 操作人 */
    private String            operator;

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }

    public void setPid(Integer pid)
    {
        this.pid = pid;
    }

    public Integer getPid()
    {
        return pid;
    }

    public void setDeep(Integer deep)
    {
        this.deep = deep;
    }

    public Integer getDeep()
    {
        return deep;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }



    public void setPinyin(String pinyin)
    {
        this.pinyin = pinyin;
    }

    public String getPinyin()
    {
        return pinyin;
    }

    public void setPinyinShor(String pinyinShor)
    {
        this.pinyinShor = pinyinShor;
    }

    public String getPinyinShor()
    {
        return pinyinShor;
    }

    public void setExtName(String extName)
    {
        this.extName = extName;
    }

    public String getExtName()
    {
        return extName;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public String getOperator()
    {
        return operator;
    }


}
