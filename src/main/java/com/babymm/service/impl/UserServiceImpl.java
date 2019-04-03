package com.babymm.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.babymm.dao.AccountDao;
import com.babymm.dao.UserDao;
import com.babymm.entity.Account;
import com.babymm.entity.User;
import com.babymm.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private AccountDao accountDao;

	@Override
	@Transactional(readOnly=false)
	public void save(User user) {
		//先判断该用户名是否已存在，存在就不允许注册
		Integer userid_fromsql = userDao.findUseridByUsername(user.getUsername());
		if(userid_fromsql != null) {
			throw new RuntimeException("用户名已存在，注册失败！");
		}
		int userId = userDao.save(user);
		
		//2、创建出默认的2个账户（现金和银行卡账户）
		Account account = new Account();
		account.setCashAmount(0);
		account.setConsume(0);
		account.setCreatetime(new Date());
		account.setIncome(0);
		account.setIshide(0);
		account.setName("现金账户");
		account.setUserId(userId);
		accountDao.save(account);		//创建现金账户
		
		Account bankAccount = new Account();  //创建银行账户
		bankAccount.setCashAmount(0);
		bankAccount.setConsume(0);
		bankAccount.setCreatetime(new Date());
		bankAccount.setIncome(0);
		bankAccount.setIshide(0);
		bankAccount.setName("银行账户");		
		bankAccount.setUserId(userId);
		accountDao.save(bankAccount);
		
	}

	@Override
	@Transactional(readOnly=false)
	public void update(User user) {
		userDao.update(user);
	}


	@Override
	@Transactional(readOnly=true)
	public Integer findUseridByUsername(String username) {
		return userDao.findUseridByUsername(username);
	}

	@Override
	@Transactional(readOnly=true)
	public User findUserByUsername(String username) {
		
		return userDao.findUserByUsername(username);
	}

	@Override
	@Transactional(readOnly=false)
	public void delete(Integer id) {
		userDao.delete(id);
	}

	@Override
	@Transactional(readOnly=false)
	public void setStatus(int status, int userId) {
		
		userDao.setStatus(status,userId);
	}

	@Override
	@Transactional(readOnly=true)
	public Integer findIdByNickname(String nickname) {
		return userDao.findIdByNickname(nickname);
	}

}
