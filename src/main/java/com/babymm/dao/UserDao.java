package com.babymm.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.babymm.entity.User;

@Repository
public class UserDao{
	
	@Autowired
	private SessionFactory SessionFactory;
	
	private Session getSession() {
		return this.SessionFactory.getCurrentSession();
	}

	/**
	 * 保存用户，用于注册
	 * 
	 * @param user
	 */
	public int save(User user) {
		getSession().save(user);
		return user.getId();
	};

	/**
	 * 更新用户信息
	 * 
	 * @param user
	 *            新的用户信息对象
	 */
	public void update(User user) {
		getSession().update(user);
	};

	/**
	 * 根据id查询用户信息
	 * 
	 * @param userid
	 *            用户id
	 * @return
	 */
	public User findById(Integer userid) {
		return (User) getSession().get(User.class, userid);
	};


	/**
	 * 根据用户名查询用户
	 * 
	 * @param username 用户名
	 * @return 返回用户id
	 */
	public Integer findUseridByUsername(String username) {
		String sql = "select id from tb_user where username = ?";
		
		List<Short> resultList = this.getSession().createSQLQuery(sql).setParameter(0,username).list();
		if (resultList == null || resultList.size() == 0) {
			return null;
		} else {
			return new Integer(resultList.get(0).intValue());
		}
	};
	
	/**
	 * 根据用户名查询用户
	 * @param username 用户名
	 * @return 返回用户对象
	 */
	public User findUserByUsername(String username) {
		String sql = "select id,username,nickname,password,email,image,status,wechat_id from tb_user where username = ?";
		
		List<User> resultList = this.getSession().createSQLQuery(sql).addEntity(User.class).setParameter(0,username).list();
		if (resultList == null || resultList.size() == 0) {
			return null;
		} else {
			return resultList.get(0);
		}
	}

	public void delete(Integer id) {
		System.out.println("删除对象");
		User user = new User();
		user.setId(id);
		getSession().delete(user);
	}

	public void setStatus(int status, int userId) {
		User user = (User) getSession().get(User.class, userId);
		user.setStatus(status);
	};
	
	/**
	 * 根据用户的昵称查询用户Id
	 * @param nickname
	 * @return
	 */
	public Integer findIdByNickname(String nickname) {
		Object result = getSession().createSQLQuery("select id from tb_user where nickname = ?").setParameter(0, nickname).uniqueResult();
		if(result == null) {
			return null;
		}
		return (Integer) result;
	}
	
	
}
