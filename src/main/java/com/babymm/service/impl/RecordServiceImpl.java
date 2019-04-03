package com.babymm.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.babymm.dao.RecordDao;
import com.babymm.domain.PageResult;
import com.babymm.entity.Record;
import com.babymm.service.AccountService;
import com.babymm.service.RecordService;

@Service
public class RecordServiceImpl implements RecordService {

	@Autowired
	private RecordDao recordDao;

	@Autowired
	private AccountService accountService;

	/**
	 * 收压岁钱
	 */
	@Override
	@Transactional(readOnly = false)
	public void getLuckyMoney(Record record) {
		int userId = record.getUserId();
		//1、获取现金账户id
		int cash_id = accountService.getCashAccountId(userId);
		
		int cashAmount = record.getCashAmount();
		////2、现金账户余额增加
		accountService.updateMoney(cash_id, cashAmount,record.getUserId());
		//3、账户收入增加
		accountService.updateIncome(cash_id,cashAmount,record.getUserId());
		
		record.setOutAccountId(0);		//将支出账户id设置为0，即没有对应的
		record.setInAccountId(cash_id);			//记录中的收入id设置成现金id
		// 2、生成一条记录
		recordDao.save(record); // 保存记录
	}

	@Override
	@Transactional(readOnly = false)
	public void storeLuckyMoney(Record record,int userId) {
		//1、获取现金账户id和银行账户id
		int cash_id = accountService.getCashAccountId(userId);
		int bank_id = accountService.getBankAccountId(userId);
		
		int cashAmount = record.getCashAmount();
		// 1、银行账户余额增加
		accountService.updateMoney(bank_id, cashAmount,userId);
		// 2、现金账户余额扣减
		accountService.updateMoney(cash_id, -cashAmount,userId);
		// 2、生成一条记录
		record.setInOrOut((short) 3); // 流水id为3,表示转账
		record.setInAccountId(bank_id);
		record.setOutAccountId(cash_id);
		record.setUpdatetime(new Date()); // 更新时间
		record.setUserId(userId);
		recordDao.save(record); // 保存记录

	}

	@Override
	@Transactional(readOnly = false)
	public void income(Record record,int userId) {
		int cashAmount = record.getCashAmount();
		record.setOutAccountId(0);
		Integer inAccountId = record.getInAccountId();
		// 1、账户余额增加
		accountService.updateMoney(inAccountId, cashAmount,userId);
		
		//2、账户收入增加
		accountService.updateIncome(inAccountId, cashAmount,userId);
		
		// 3、产生一条记录
		recordDao.save(record);

	}

	@Override
	@Transactional(readOnly = false)
	public void consume(Record record,int userId) {
		int cashAmount = record.getCashAmount();
		Integer outAccountId = record.getOutAccountId();
		// 1、账户余额减少
		accountService.updateMoney(outAccountId, -cashAmount,userId);
		
		//2、账户总支出增加
		accountService.updateConsume(outAccountId, cashAmount,userId);
		record.setInAccountId(0);
		
		// 3、产生一条记录
		recordDao.save(record);
	}

	@Override
	@Transactional(readOnly = false)
	public void transfer(Record record,int userId) {
		int cashAmount = record.getCashAmount();
		Integer outAccountId = record.getOutAccountId();
		Integer inAccountId = record.getInAccountId();
		
		// 1、转出账户出钱
		accountService.updateMoney(outAccountId, -cashAmount,userId);
		
		//2、转入账户收钱
		accountService.updateMoney(inAccountId, cashAmount,userId);

		// 3、产生一条记录
		recordDao.save(record);

	}

	@Override
	@Transactional(readOnly = true)
	public PageResult findPageList(int page, int limit,int userId) {
		return recordDao.findPageList(page, limit,userId);
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(Record record,int userId) {
		//1、删除记录
		recordDao.delete(record);
		//2、恢复账户操作的数据
		int flag = record.getInOrOut();
		int cashAmount = record.getCashAmount();
		Integer inAccountId = record.getInAccountId();
		Integer outAccountId = record.getOutAccountId();
		if(flag == 1) {			//收入
			//恢复收入金额
			accountService.updateIncome(inAccountId, -cashAmount,userId);
			//恢复剩余金额
			accountService.updateMoney(inAccountId, -cashAmount,userId);
		}else if(flag ==2) {		//支出
			accountService.updateConsume(outAccountId, -cashAmount,userId);
			//恢复剩余金额
			accountService.updateMoney(outAccountId, cashAmount,userId);
		}else {			//转账
			accountService.updateMoney(inAccountId, -cashAmount,userId);
			accountService.updateMoney(outAccountId, cashAmount,userId);
		}
		
	}

	@Override
	@Transactional(readOnly=true)
	public PageResult<Record> search(int page, int limit,Date createtime_start, Date createtime_end,
			Short inOrOut,int userId) {
		return recordDao.search(page, limit, createtime_start, createtime_end, inOrOut,userId);
	}
}
