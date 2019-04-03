package com.babymm.domain;

import java.util.List;

import com.babymm.entity.Account;

/**
 * 分页结果
 * @author yluo0
 *
 */
public class PageResult<T>{
	
	//总结果数
	private Long count;			//总页数
	
	private Integer code;		//状态，默认为0表示成功返回
	
	private String msg;			//信息
	
	//每页条数
	private List<T> data;		//每页数据

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	
	
}
