package com.babymm.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.babymm.dao.UserDao;
import com.babymm.entity.User;
import com.babymm.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	// 存储邮件相关信息
	public void storeEmail(String username, String active_url) {

		// 设置有效期为1天
		redisTemplate.boundValueOps(username).set(active_url, 1, TimeUnit.DAYS);
	}

	// 获取邮件相关信息
	public String getEmail(String username) {

		return redisTemplate.boundValueOps(username).get();
	}

	// 获取删除缓存
	public void del(String username) {
		redisTemplate.delete(username);
	}

	@Autowired
	private UserDao userDao;

	@Transactional(readOnly = false)
	public void update(User user) {
		User user_fromsql = userDao.findUserByUsername(user.getUsername());
		if (user_fromsql == null) {
			throw new RuntimeException("非法操作，没有该用户信息");
		} else {
			user_fromsql.setEmail(user.getEmail());
			// 由于采用了先读取再更新字段的方式，hibernate通过对比字段，发现不同后，会自动更新，不必执行update方法
			/* userDao.update(user_fromsql); */
		}
	}

	@Override
	public void storeCode(String username, String code) {
		redisTemplate.boundValueOps(username).set(code, 30, TimeUnit.MINUTES);
	}

	// 获取验证码信息，其与邮箱信息不可能同时存在
	public String getCode(String username) {
		return redisTemplate.boundValueOps(username).get();
	}
}
