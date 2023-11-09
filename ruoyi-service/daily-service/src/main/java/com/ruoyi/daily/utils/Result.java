package com.ruoyi.daily.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
	//返回码
	private Integer code;
	//返回消息，一般是错误提示信息
	private String msg;
	//泛型数据
	private T data;

	public Result(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}


	public static <T> Result<T> ok() {
		return ok("success");
	}
	public static <T> Result<T> ok(String message) {
		return new Result<>(200,message);
	}

	public static <T> Result<T> error() {
		return error("未知异常，请联系管理员");
	}
	public static <T> Result<T> error(String message) {
		return new Result<>(500,message);
	}
	public static <T> Result<T> data(T data) {
		return data(data,"success");
	}
	public static <T> Result<T> data(T data, String message) {
		return new Result<>(200,message,data);
	}





}