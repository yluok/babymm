package com.babymm.domain;

import com.babymm.entity.Record;
/**
 * 用于前端展示的类，对应的是record类
 * 主要是将其中的账户id全部转换成对应的name
 * @author yluo0
 *
 */
public class RecordShow extends Record{
	
	private String inAccountName;			//进账账户名，这两个是需要显示在页面上的值

	private String outAccountName;			//出账账户名
	
	public String getInAccountName() {
		return inAccountName;
	}

	public void setInAccountName(String inAccountName) {
		this.inAccountName = inAccountName;
	}

	public String getOutAccountName() {
		return outAccountName;
	}

	public void setOutAccountName(String outAccountName) {
		this.outAccountName = outAccountName;
	}

}
