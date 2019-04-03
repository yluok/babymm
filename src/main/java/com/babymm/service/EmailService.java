package com.babymm.service;

import com.babymm.entity.User;

public interface EmailService {

	
	//存储邮件相关信息
	public void storeEmail(String username,String active_url);
	//存储邮件相关信息
	public String getEmail(String username);
	
	//更新邮箱信息
	public void update(User user);
	
	/**
	 * 删除redis缓存信息
	 * @param username
	 */
	public void del(String username);
	
	/**
	 * 存储验证码信息
	 * @param username
	 * @param code
	 */
	public void storeCode(String username,String code);
	
	
	/**
	 * 获取验证码
	 * @param username
	 * @return
	 */
	public String getCode(String username);
}
