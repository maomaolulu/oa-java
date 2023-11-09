package com.ruoyi.training.entity;

import java.math.BigDecimal;
import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 自定义考试信息对象 tra_customize_exam
 * 
 * @author yrb
 * @date 2022-10-24
 */
public class TraCustomizeExam extends BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    private Long id;

    /** 考试名称 */
    @Excel(name = "考试名称")
    private String examName;

    /** 公司id */
    @Excel(name = "公司id")
    private Long companyId;

    /** 通过标准分值 */
    @Excel(name = "通过标准分值")
    private BigDecimal passScore;

    /** 单选题数量 */
    @Excel(name = "单选题数量")
    private Long singleNumber;

    /** 单选题分值 */
    @Excel(name = "单选题分值")
    private BigDecimal singleScore;

    /** 多选题数量 */
    @Excel(name = "多选题数量")
    private Long multiNumber;

    /** 多选题分值 */
    @Excel(name = "多选题分值")
    private BigDecimal multiScore;

    /** 判断题数量 */
    @Excel(name = "判断题数量")
    private Long judgeNumber;

    /** 判断题分值 */
    @Excel(name = "判断题分值")
    private BigDecimal judgeScore;

    /** 总分 */
    @Excel(name = "总分")
    private BigDecimal totalScore;

    /** 发布：0未发布，1已发布 */
    @Excel(name = "发布：0未发布，1已发布")
    private Integer issueFlag;

    /** 最后操作者 */
    @Excel(name = "最后操作者")
    private String lastModifier;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setExamName(String examName) 
    {
        this.examName = examName;
    }

    public String getExamName() 
    {
        return examName;
    }
    public void setCompanyId(Long companyId) 
    {
        this.companyId = companyId;
    }

    public Long getCompanyId() 
    {
        return companyId;
    }
    public void setPassScore(BigDecimal passScore) 
    {
        this.passScore = passScore;
    }

    public BigDecimal getPassScore() 
    {
        return passScore;
    }
    public void setSingleNumber(Long singleNumber) 
    {
        this.singleNumber = singleNumber;
    }

    public Long getSingleNumber() 
    {
        return singleNumber;
    }
    public void setSingleScore(BigDecimal singleScore) 
    {
        this.singleScore = singleScore;
    }

    public BigDecimal getSingleScore() 
    {
        return singleScore;
    }
    public void setMultiNumber(Long multiNumber) 
    {
        this.multiNumber = multiNumber;
    }

    public Long getMultiNumber() 
    {
        return multiNumber;
    }
    public void setMultiScore(BigDecimal multiScore) 
    {
        this.multiScore = multiScore;
    }

    public BigDecimal getMultiScore() 
    {
        return multiScore;
    }
    public void setJudgeNumber(Long judgeNumber) 
    {
        this.judgeNumber = judgeNumber;
    }

    public Long getJudgeNumber() 
    {
        return judgeNumber;
    }
    public void setJudgeScore(BigDecimal judgeScore) 
    {
        this.judgeScore = judgeScore;
    }

    public BigDecimal getJudgeScore() 
    {
        return judgeScore;
    }
    public void setTotalScore(BigDecimal totalScore) 
    {
        this.totalScore = totalScore;
    }

    public BigDecimal getTotalScore() 
    {
        return totalScore;
    }
    public void setIssueFlag(Integer issueFlag) 
    {
        this.issueFlag = issueFlag;
    }

    public Integer getIssueFlag() 
    {
        return issueFlag;
    }
    public void setLastModifier(String lastModifier) 
    {
        this.lastModifier = lastModifier;
    }

    public String getLastModifier() 
    {
        return lastModifier;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("examName", getExamName())
            .append("companyId", getCompanyId())
            .append("passScore", getPassScore())
            .append("singleNumber", getSingleNumber())
            .append("singleScore", getSingleScore())
            .append("multiNumber", getMultiNumber())
            .append("multiScore", getMultiScore())
            .append("judgeNumber", getJudgeNumber())
            .append("judgeScore", getJudgeScore())
            .append("totalScore", getTotalScore())
            .append("issueFlag", getIssueFlag())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("lastModifier", getLastModifier())
            .toString();
    }
}
