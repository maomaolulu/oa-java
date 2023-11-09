package com.ruoyi.quote.domain.enums;


import io.swagger.annotations.ApiModelProperty;

/**
 * @Author yrb
 * @Date 2022/7/29 10:53
 * @Version 1.0
 * @Description
 */
public enum QuoteDownloadTypeEnum {

    ZW_WORD(1,"职卫word下载"),
    ZW_PDF(2,"职卫pdf下载"),
    HJ_WORD(3,"环境word下载"),
    HJ_PDF(4,"环境pdf下载"),
    GW_WORD(5,"公卫word下载"),
    GW_PDF(6,"公卫pdf下载");

    QuoteDownloadTypeEnum(Integer type,String note){
        this.type = type;
        this.note = note;
    }

    public static Integer getType(String note){
        for (QuoteDownloadTypeEnum quoteDownloadTypeEnum:QuoteDownloadTypeEnum.values()){
            if (note.equals(quoteDownloadTypeEnum.getNote())){
                return quoteDownloadTypeEnum.getType();
            }
        }
        return null;
    }

    @ApiModelProperty("下载类型")
    private Integer type;

    @ApiModelProperty("类型说明")
    private String note;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
