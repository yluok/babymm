package com.babymm.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.babymm.dao.AccountDao;
import com.babymm.dao.RecordDao;
import com.babymm.domain.PageResult;
import com.babymm.entity.Account;
import com.babymm.service.AccountService;
import com.babymm.utils.DateUtils;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private RecordDao recordDao;

	@Override
	@Transactional(readOnly = false)
	public void updateMoney(Integer accountid, Integer money,int userId) {
		accountDao.updateMoney(accountid, money,userId);
	}

	@Override
	@Transactional(readOnly = false)
	public void transfer(Integer in_accountid, Integer money, Integer out_accountid,int userId) {
		accountDao.updateMoney(in_accountid, money,userId);
		accountDao.updateMoney(out_accountid, -money,userId);
	}

	@Override
	@Transactional(readOnly = true)
	public Integer getCashAmount(int userId) {
		Integer cash_id = accountDao.getCashAccountId(userId);
		if (accountDao.getAmount(cash_id) == null) {
			throw new RuntimeException("未知账户，请核实查询账户是否存在");
		}
		return accountDao.getAmount(cash_id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Account> findList(boolean iscash,boolean ishide,int userId) {
		return accountDao.findList(iscash,ishide,userId);
	}

	@Override
	@Transactional(readOnly = false)
	public void setHideOrNot(int accountId, int ishide) {
		if (accountId <= 2) {
			throw new RuntimeException("现金或活期账户不允许隐藏");
		}
		accountDao.setHideOrNot(accountId, ishide);
	}

	@Override
	@Transactional(readOnly = true)
	public PageResult findPageList(int page, int limit,int userId) {
		return accountDao.findPageList(page, limit,userId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateIncome(Integer accountid, Integer money,int userId) {
		accountDao.updateIncome(accountid, money,userId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateConsume(Integer accountid, Integer money,int userId) {
		accountDao.updateConsume(accountid, money,userId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateNameById(String newName, int id) {
		accountDao.updateNameById(newName, id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAccountById(int id) {
		if (recordDao.findRelated(id)) { // 如果有关联记录，就不允许删除
			throw new RuntimeException("该账户有关联记录，无法删除");
		} else {
			accountDao.deleteAccountById(id);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void save(String accountName,int userId) {
		if (accountDao.findByAccountName(accountName,userId)) {
			throw new RuntimeException("已有相同账户名，无法创建");
		} else {
			Account account = new Account();
			account.setCreatetime(new Date());
			account.setName(accountName);
			account.setUserId(userId);
			account.setIshide(0);
			account.setCashAmount(0);
			account.setConsume(0);
			account.setIncome(0);
			accountDao.save(account);
		}

	}

	@Override
	@Transactional(readOnly = true)
	public double getTotalAccountLeft(int userId) {

		return accountDao.getTotalAccountLeft(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public double getTotalAmountIncrByYear(int userId) {
		Date year_start = DateUtils.FirstDayOfCurrentYear(); // 2019-01-01
																// 00:00:00

		Date year_end = DateUtils.FirstDayOfNextYear(); // 2020-01-01 00:00:00
		return recordDao.getTotalAmountIncrByYear(year_start, year_end,userId);
	}

	@Override
	@Transactional(readOnly = true)
	public double getTotalLuckyMoney(int userId) {
		return accountDao.getTotalLuckyMoney(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public double getLuckMoneyByYear(int userId) {
		Date year_start = DateUtils.FirstDayOfCurrentYear();		//本年度开始时间
		Date year_end = DateUtils.FirstDayOfNextYear();		//本年度结束时间
		//现金账户对应的id
		int cashAccountId = accountDao.getCashAccountId(userId);
		return recordDao.getLuckMoneyByYear(year_start, year_end,userId,cashAccountId);
	}

	@Override
	@Transactional(readOnly = true)
	public double getTotalIncome(int userId) {
		int cashId = accountDao.getCashAccountId(userId);
		return accountDao.getTotalIncome(userId,cashId);
	}

	@Override
	@Transactional(readOnly = true)
	public double getIncomeByYear(int userId) {
		Date year_start = DateUtils.FirstDayOfCurrentYear();
		Date year_end = DateUtils.FirstDayOfNextYear();
		int cashId = accountDao.getCashAccountId(userId);		//现金账户id
		return recordDao.getIncomeByYear(year_start, year_end,userId,cashId);
	}

	@Override
	@Transactional(readOnly = true)
	public double getConsumeByYear(int userId) {
		Date year_start = DateUtils.FirstDayOfCurrentYear();
		Date year_end = DateUtils.FirstDayOfNextYear();
		int cashId = accountDao.getCashAccountId(userId);		//现金账户id
		return recordDao.getConsumeByYear(year_start, year_end,userId,cashId);
	}

	@Override
	@Transactional(readOnly = true)
	public double getTotalConsume(int userId) {
		int cashId = accountDao.getCashAccountId(userId);		//现金账户id
		return accountDao.getTotalConsume(userId,cashId);
	}

	@Override
	@Transactional(readOnly = true)
	public int getCashAccountId(int userId) {
		return accountDao.getCashAccountId(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public int getBankAccountId(int userId) {
		return accountDao.getBankAccountId(userId);
	}
	
	

}
