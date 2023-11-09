package com.ruoyi.activiti.feign;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class Res implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer code;
    private Object data = "";
    private String message = "";
}