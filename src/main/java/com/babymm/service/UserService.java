package com.babymm.service;

import com.babymm.entity.User;

public interface UserService {
	
	
	/**
	 * 保存用户，用于注册
	 * @param user
	 */
	public void save(User user);
	
	/**
	 * 更新用户信息
	 * @param user   新的用户信息对象
	 */
	public void update(User user);
	
	/**
	 * 检测用户名是否存在：用于注册时检测用户名是否存在,如果存在，不允许注册
	 * @param username
	 * @return	返回查询到的用户对象ID
	 */
	public Integer findUseridByUsername(String username);
	
	/**
	 * 检测用户名是否存在
	 * @param username
	 * @return	返回查询到的用户对象ID
	 */
	public User findUserByUsername(String username);
	
	public void delete(Integer id);

	/**
	 * 修改状态
	 * @param status 状态值，0和1:0是默认值，表示从来没有登录过，1表示登陆过一次
	 * @param userId
	 */
	public void setStatus(int status, int userId);
	
	/**
	 * 根据用户昵称查询用户Id
	 * @param nickname
	 * @return
	 */
	public Integer findIdByNickname(String nickname);

}
