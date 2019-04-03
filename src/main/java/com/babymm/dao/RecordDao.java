package com.babymm.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.babymm.domain.PageResult;
import com.babymm.entity.Record;

@Repository
public class RecordDao {

	@Autowired
	private SessionFactory SessionFactory;

	private Session getSession() {
		return this.SessionFactory.getCurrentSession();
	}

	/**
	 * 插入记录
	 * 
	 * @param accountid
	 * @param money
	 */
	public void save(Record record) {
		getSession().save(record);
	}

	/**
	 * 根据账户id查询其是否有关联记录
	 * 
	 * @param accountId
	 *            账户id
	 * @return false表示没有，true表示有
	 */
	public boolean findRelated(int accountId) {
		Object obj = getSession().createSQLQuery("select id from tb_record where in_account_id=? or out_account_id=?")
				.setParameter(0, accountId).setParameter(1, accountId).list();
		System.out.println(obj);
		if (obj == null || "[]".equals(obj.toString())) {
			return false;
		} else {
			return true;
		}
	}

	public PageResult findPageList(int page, int limit,int userId) {
		// 1、获取总条数
		String sql = "select count(id) from tb_record where user_id = ?";
		BigInteger pageTotal = (BigInteger) getSession().createSQLQuery(sql).setParameter(0, userId).uniqueResult();
		System.out.println("总条数：" + pageTotal);

		List<Record> recordList = getSession()
				.createSQLQuery(
						"select id,in_account_id,out_account_id,cash_amount,in_or_out,updatetime,content,createtime,user_id from tb_record where user_id = ? order by createtime desc limit ?,? ")
				.addEntity(Record.class)
				.setParameter(0, userId)
				.setParameter(1, (page-1)*limit)
				.setParameter(2, limit)
				.list();

		PageResult pageResult = new PageResult();
		pageResult.setCode(0);
		pageResult.setCount(pageTotal.longValue());
		pageResult.setData(recordList);
		pageResult.setMsg("");
		
		return pageResult;

	}
	
	public void delete(Record record) {
		getSession().delete(record);
	}
	
	/**
	 * 分页查询，带查询条件
	 * @param page
	 * @param limit
	 * @param createtime_start
	 * @param createtime_end
	 * @param inOrNot
	 * @return
	 */
	public PageResult search(int page,int limit,Date createtime_start,Date createtime_end,Short inOrOut,int userId) {
			Criteria crt = getSession().createCriteria(Record.class);
			crt = packageTarget(crt, createtime_start, createtime_end, inOrOut, userId);
			crt.setFirstResult((page-1)*limit);
			crt.setMaxResults(limit);
			crt.addOrder(Order.desc("createtime"));
			List<Record> records = crt.list();
			
			Criteria crt_amount = getSession().createCriteria(Record.class);
			crt_amount = packageTarget(crt_amount, createtime_start, createtime_end, inOrOut, userId);	
			int pageTotal = crt_amount.list().size();
			
			PageResult<Record> pageresult = new PageResult<Record> ();
			pageresult.setCode(0);
			pageresult.setCount((long)pageTotal);
			pageresult.setData(records);
			pageresult.setMsg("");
			
			return pageresult;
	}
	
	/**
	 * 封装条件的
	 * @param crt  条件对象
	 * @param createtime_start  条件1：开始时间
	 * @param createtime_end
	 * @param inOrOut
	 * @param userId
	 * @return
	 */
	private Criteria packageTarget(Criteria crt,Date createtime_start,Date createtime_end,Short inOrOut,int userId) {
		if(inOrOut != null) {
			crt.add(Restrictions.eq("inOrOut", inOrOut));
		}
		if(createtime_start != null) {
			crt.add(Restrictions.ge("createtime", createtime_start));
		}
		if(createtime_end != null) {
			crt.add(Restrictions.le("createtime", createtime_end));
		}
		crt.add(Restrictions.eq("userId", userId));
		return crt;
	}
	
	public double getTotalAmountIncrByYear(Date year_start,Date year_end,int userId) {
		String sql = "select sum(cash_amount) from tb_record where createtime >= ? and createtime < ? and in_or_out = ? and user_id = ?";
		BigDecimal income = (BigDecimal) getSession().createSQLQuery(sql)
								.setParameter(0, year_start)
								.setParameter(1, year_end)
								.setParameter(2, 1)
								.setParameter(3, userId)
								.uniqueResult();
		BigDecimal consume = (BigDecimal) getSession().createSQLQuery(sql)
								.setParameter(0, year_start)
								.setParameter(1, year_end)
								.setParameter(2, 2)
								.setParameter(3, userId)
								.uniqueResult();
		int income_int = 0;
		int consume_int = 0;
		if(income != null) {
			income_int = income.intValue();
		}
		if(consume != null) {
			consume_int = consume.intValue();
		}
		return (income_int-consume_int)/100.0;
	}
	
	//获取本年度收到多少压岁钱
	public double getLuckMoneyByYear(Date year_start,Date year_end,int userId,int cashAccountId) {
		
		String sql = "select sum(cash_amount) from tb_record where createtime >= ? and createtime < ? and in_or_out =1 and in_account_id = ? and user_id=?";   //流水类型为收入且账号为现金账户
		BigDecimal luckyMoney = (BigDecimal) getSession().createSQLQuery(sql)
				.setParameter(0, year_start)
				.setParameter(1, year_end)
				.setParameter(2, cashAccountId)
				.setParameter(3, userId)
				.uniqueResult();
		if(luckyMoney == null) {
			return 0.0;
		}
		return luckyMoney.intValue()/100.0;
	}
	
	//获取本年度打理收入是多少
	public double getIncomeByYear(Date year_start,Date year_end,int userId,int cashId) {
		String sql = "select sum(cash_amount) from tb_record where createtime >= ? and createtime < ? and in_or_out =1 and user_id = ? and in_account_id != ?";   //流水类型为收入且账号不为现金账户
		BigDecimal income = (BigDecimal) getSession().createSQLQuery(sql)
				.setParameter(0, year_start)
				.setParameter(1, year_end)
				.setParameter(2, userId)
				.setParameter(3, cashId)
				.uniqueResult();
		if(income == null) {
			return 0.0;
		}
		return income.intValue()/100.0;
	}
	
	//获取本年度总支出
	public double getConsumeByYear(Date year_start,Date year_end,int userId,int cashId) {
		String sql = "select sum(cash_amount) from tb_record where createtime >= ? and createtime < ? and in_or_out = 2 and user_id = ? and in_account_id != ?";   //流水类型为支出且账号不为现金账户
		BigDecimal consume = (BigDecimal) getSession().createSQLQuery(sql)
				.setParameter(0, year_start)
				.setParameter(1, year_end)
				.setParameter(2, userId)
				.setParameter(3, cashId)
				.uniqueResult();
		if(consume == null) {
			return 0.0;
		}
		return consume.intValue()/100.0;
	}
}
