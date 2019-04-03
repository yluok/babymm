package com.babymm.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.babymm.domain.PageResult;
import com.babymm.entity.Account;

@Repository
public class AccountDao {

	@Autowired
	private SessionFactory SessionFactory;

	private Session getSession() {
		return this.SessionFactory.getCurrentSession();
	}

	/**
	 * 修改账户余额
	 * @param accountid  修改的账户id
	 * @param money 交易账户金额
	 */
	public void updateMoney(Integer accountid, Integer money,int userId) {
		Account account = (Account) getSession()
				.createSQLQuery("select id,name,cash_amount,ishide,income,consume,createtime,user_id from tb_account where id = ? and user_id=?")
				.addEntity(Account.class)
				.setParameter(0, accountid)
				.setParameter(1, userId)
				.list().get(0);
		account.setCashAmount(account.getCashAmount() + money);
	}
	
	/**
	 * 修改账户总收入
	 * @param accountid 收入账户id
	 * @param money  收入金额
	 */
	public void updateIncome(Integer accountid, Integer money,int userId) {
		Account account = (Account) getSession()
				.createSQLQuery("select id,name,cash_amount,ishide,income,consume,createtime,user_id from tb_account where id = ? and user_id=?")
				.addEntity(Account.class)
				.setParameter(0, accountid)
				.setParameter(1, userId)
				.list().get(0);
		account.setIncome(account.getIncome() + money);
	}
	
	/**
	 * 修改账户总支出
	 * @param accountid  支出账户id
	 * @param money  支出金额
	 */
	public void updateConsume(Integer accountid, Integer money,int userId) {
		Account account = (Account) getSession()
				.createSQLQuery("select id,name,cash_amount,ishide,income,consume,createtime,user_id from tb_account where id = ? and user_id = ?")
				.addEntity(Account.class)
				.setParameter(0, accountid)
				.setParameter(1, userId)			
				.list().get(0);
		
		account.setConsume(account.getConsume()+ money);
	}
	
	/**
	 * 获取指定账户的剩余金额
	 * @param accountId  账户id
	 * @return   账户剩余金额
	 */
	public Integer getAmount(Integer accountId) {
		Object account = getSession().get(Account.class, accountId);
		if(account !=null) {
			return ((Account)account).getCashAmount();
		}else {
			return null;
		}
		
	}
	
	/**
	 * 获取账户列表
	 * @param ishide 是否隐藏,true表示获取隐藏的,false表示不获取隐藏的
	 * @return
	 */
	public List<Account> findList(boolean iscash,boolean ishide,int userId) {
		Criteria crt = getSession().createCriteria(Account.class);
		if(!iscash) {
			crt.add(Restrictions.ne("name", "现金账户"));
		}
		if(!ishide) {
			crt.add(Restrictions.ne("ishide", 1));
		}
		crt.add(Restrictions.eq("userId", userId));
		return crt.list();
	}
	
	/**
	 * 设置账户是否隐藏
	 * @param accountId
	 * @param ishide  0表示显示，1表示隐藏，默认为0
	 */
	public void setHideOrNot(int accountId,int ishide) {
		
		Account account = (Account) getSession().get(Account.class, accountId);
		account.setIshide(ishide);
		
	}
	
	/**
	 * 获取所有账户，分页
	 * @param page  当前页
	 * @param limit	每页条数
	 * @return
	 */
	public PageResult findPageList(int page,int limit,int userId) {
		//1、获取总条数
		String hql = "select count(id) from Account";
		Long pageTotal = (Long) getSession().createQuery(hql).uniqueResult();
		System.out.println("总条数：" + pageTotal);
		
		//2、获取当前页的记录数
		String sql = "select id,name,cash_amount,ishide,income,consume,createtime,user_id from tb_account where user_id = ? limit ?,?";
		List<Account> rowList = getSession().createSQLQuery(sql)
				.addEntity(Account.class)
				.setParameter(0, userId)
				.setParameter(1, (page-1)* limit)
				.setParameter(2, limit)
				.list();
		
		PageResult pageResult = new PageResult();
		pageResult.setCode(0);
		pageResult.setCount(pageTotal);
		pageResult.setData(rowList);
		pageResult.setMsg("");
		
		return pageResult;
	}
	
	/**
	 * 根据id修改账户名
	 * @param newName
	 * @param id
	 */
	public void updateNameById(String newName,int id) {
		
		Account account = (Account) getSession()
							.createSQLQuery("select id,name,cash_amount,ishide,income,consume,createtime,user_id from tb_account where id = ?" )
							.addEntity(Account.class)
							.setParameter(0, id)		
							.uniqueResult();
		account.setName(newName);
	}
	
	/**
	 * 删除账户
	 * @param id
	 */
	public void deleteAccountById(int id) {
		Account account = new Account();
		account.setId(id);
		getSession().delete(account);
	}
	
	/**
	 * 保存账户
	 * @param account 账户对象
	 */
	public void save(Account account) {
		getSession().save(account);
	}
	
	/**
	 * 查询数据库中是否有相同账户名存在
	 * @param accountName 账户名
	 * @return true表示有，false表示没有
	 */
	public boolean findByAccountName(String accountName,int userId) {
		Object result = getSession().createSQLQuery("select id from tb_account where name = ? and user_id =?")
				.setParameter(0, accountName)
				.setParameter(1, userId)
				.uniqueResult();
		if(result != null) {
			return true;
		}
		return false;
	}
	
	//获取账户总额
	public double getTotalAccountLeft(int userId) {
		BigDecimal result = (BigDecimal) getSession()
				.createSQLQuery("select sum(cash_amount) from tb_account where user_id = ?")
				.setParameter(0, userId)
				.uniqueResult();
		if(result == null) {
			return 0.0;
		}
		return result.intValue()/100.0;
	}
	
	
	//获取压岁钱历史总额，即现金账户总收入
	public double getTotalLuckyMoney(int userId) {
		Session session = getSession();
		String sql_forCashId = "select income from tb_account where user_id = ? and name = ?";
		Integer result = (Integer) session.createSQLQuery(sql_forCashId)
									.setParameter(0, userId)
									.setParameter(1, "现金账户")
									.uniqueResult();
		if(result == null) {
			return 0.0;
		}
		return result.intValue()/100.0;
	}
	
	//获取打理收入总额(即不含压岁钱收入)
	public double getTotalIncome(int userId,int cashId) {
		BigDecimal result = (BigDecimal) getSession()
								.createSQLQuery("select sum(income) from tb_account where user_id = ? and id !=?")
								.setParameter(0, userId)
								.setParameter(1, cashId)
								.uniqueResult();
		if(result == null) {
			return 0.0;
		}
		return result.intValue()/100.0;
	}
	
	
	//获取历史支出总额(该支出不会从现金账户中出)
	public double getTotalConsume(int userId,int cashId) {
		BigDecimal result = (BigDecimal) getSession()
								.createSQLQuery("select sum(consume) from tb_account where user_id = ? and id !=?")
								.setParameter(0, userId)
								.setParameter(1, cashId)
								.uniqueResult();
		if(result == null) {
			return 0.0;
		}
		return result.intValue()/100.0;
	}
	
	/**
	 * 获取对应用户的现金账户id
	 * @param userId
	 * @return
	 */
	public int getCashAccountId(int userId) {
		Short result =  (Short) getSession().createSQLQuery("select id from tb_account where user_id = ? and name=?")
					.setParameter(0, userId)
					.setParameter(1, "现金账户")
					.uniqueResult();
		return Integer.parseInt(result + "");
	}
	
	//获取银行账户id
	public int getBankAccountId(int userId) {
		Short result= (Short) getSession().createSQLQuery("select id from tb_account where user_id = ? and name = ?")
					.setParameter(0, userId)
					.setParameter(1, "银行账户")
					.uniqueResult();
		return Integer.parseInt(result + "");
	}
}
