package com.ruoyi.quote.domain.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 客户信息对象 quote_customer_info
 * 
 * @author yrb
 * @date 2022-05-13
 */
public class QuoteCustomerInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    private Long id;

    /** 所属行业 */
    @Excel(name = "所属行业")
    private String industry;

    /** 所属地区 */
    @Excel(name = "所属地区")
    private String region;

    /** 省 */
    @Excel(name = "省")
    private String province;

    /** 市 */
    @Excel(name = "市")
    private String city;

    /** 县（区） */
    @Excel(name = "县", readConverterExp = "区=")
    private String county;

    /** 详细地址 */
    @Excel(name = "详细地址")
    private String address;

    /** 联系人 */
    @Excel(name = "联系人")
    private String customerName;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String telephone;

    /** 电子邮箱 */
    @Excel(name = "电子邮箱")
    private String email;

    /** 固定电话 */
    @Excel(name = "固定电话")
    private String fixedTelephone;

    /** 所属业务员ID */
    @Excel(name = "所属业务员ID")
    private Long salesmanId;

    /** 所属业务员姓名 */
    @Excel(name = "所属业务员姓名")
    private String salesman;

    /** 公司名称 */
    @Excel(name = "公司名称")
    private String companyName;

    /** 人员规模 */
    @Excel(name = "人员规模")
    private String personScale;

    /** 社会信用代码 */
    @Excel(name = "社会信用代码")
    private String code;

    /** 注册地址 */
    @Excel(name = "注册地址")
    private String registerAddress;

    /** 注册时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "注册时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date registerTime;

    /** 法人代表 */
    @Excel(name = "法人代表")
    private String representative;

    /** 经营范围 */
    @Excel(name = "经营范围")
    private String businessScope;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setIndustry(String industry) 
    {
        this.industry = industry;
    }

    public String getIndustry() 
    {
        return industry;
    }
    public void setRegion(String region) 
    {
        this.region = region;
    }

    public String getRegion() 
    {
        return region;
    }
    public void setProvince(String province) 
    {
        this.province = province;
    }

    public String getProvince() 
    {
        return province;
    }
    public void setCity(String city) 
    {
        this.city = city;
    }

    public String getCity() 
    {
        return city;
    }
    public void setCounty(String county) 
    {
        this.county = county;
    }

    public String getCounty() 
    {
        return county;
    }
    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }
    public void setCustomerName(String customerName) 
    {
        this.customerName = customerName;
    }

    public String getCustomerName() 
    {
        return customerName;
    }
    public void setTelephone(String telephone) 
    {
        this.telephone = telephone;
    }

    public String getTelephone() 
    {
        return telephone;
    }
    public void setEmail(String email) 
    {
        this.email = email;
    }

    public String getEmail() 
    {
        return email;
    }
    public void setFixedTelephone(String fixedTelephone) 
    {
        this.fixedTelephone = fixedTelephone;
    }

    public String getFixedTelephone() 
    {
        return fixedTelephone;
    }
    public void setSalesmanId(Long salesmanId) 
    {
        this.salesmanId = salesmanId;
    }

    public Long getSalesmanId() 
    {
        return salesmanId;
    }
    public void setSalesman(String salesman) 
    {
        this.salesman = salesman;
    }

    public String getSalesman() 
    {
        return salesman;
    }
    public void setCompanyName(String companyName) 
    {
        this.companyName = companyName;
    }

    public String getCompanyName() 
    {
        return companyName;
    }
    public void setPersonScale(String personScale) 
    {
        this.personScale = personScale;
    }

    public String getPersonScale() 
    {
        return personScale;
    }
    public void setCode(String code) 
    {
        this.code = code;
    }

    public String getCode() 
    {
        return code;
    }
    public void setRegisterAddress(String registerAddress) 
    {
        this.registerAddress = registerAddress;
    }

    public String getRegisterAddress() 
    {
        return registerAddress;
    }
    public void setRegisterTime(Date registerTime) 
    {
        this.registerTime = registerTime;
    }

    public Date getRegisterTime() 
    {
        return registerTime;
    }
    public void setRepresentative(String representative) 
    {
        this.representative = representative;
    }

    public String getRepresentative() 
    {
        return representative;
    }
    public void setBusinessScope(String businessScope) 
    {
        this.businessScope = businessScope;
    }

    public String getBusinessScope() 
    {
        return businessScope;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("industry", getIndustry())
            .append("region", getRegion())
            .append("province", getProvince())
            .append("city", getCity())
            .append("county", getCounty())
            .append("address", getAddress())
            .append("customerName", getCustomerName())
            .append("telephone", getTelephone())
            .append("email", getEmail())
            .append("fixedTelephone", getFixedTelephone())
            .append("salesmanId", getSalesmanId())
            .append("salesman", getSalesman())
            .append("companyName", getCompanyName())
            .append("personScale", getPersonScale())
            .append("code", getCode())
            .append("registerAddress", getRegisterAddress())
            .append("registerTime", getRegisterTime())
            .append("representative", getRepresentative())
            .append("businessScope", getBusinessScope())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
