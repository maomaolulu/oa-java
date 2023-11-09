package com.ruoyi.file.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
 
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Res implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer code;
    private Object data = "";
    private String message = "";
}