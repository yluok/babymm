package com.babymm.service;

import java.util.List;

import com.babymm.domain.PageResult;
import com.babymm.entity.Account;

public interface AccountService {
	
	/**
	 * 更新账户金额
	 * @param accountid  收钱账户id
	 * @param money  收钱的金额
	 */
	public void updateMoney(Integer accountid, Integer money,int userId);
	
	
	/**
	 * 更新账户总支出
	 * @param accountid 账户id
	 * @param money  更新金额
	 */	
	public void updateConsume(Integer accountid, Integer money,int userId);
	
	
	/**
	 * 更新账户收入
	 * @param accountid 账户id
	 * @param money  更新金额
	 * @param userId 用户id
	 */
	public void updateIncome(Integer accountid, Integer money,int userId);
	
	
	/**
	 * 
	 * @param in_accountid 收款账户id
	 * @param money	转账金额
	 * @param out_accountid	付款账户id
	 * @param userId 当前用户id
	 */
	public void transfer(Integer in_accountid, Integer money,Integer out_accountid,int userId);
	
	
	
	/**
	 * 获取账户剩余金额
	 * @param userId 用户id
	 * @return  返回现金账户剩余金额
	 */
	public Integer getCashAmount(int userId);
	
	
	/**
	 * 获取账户列表,不含隐藏账户
	 * @param ishide 是否需要获取隐藏账户，true表示获取隐藏账户，false表示不获取隐藏的
	 * @param iscash 是否需要获取现金账户，true表示获取现金账户，false表示不获取现金的
	 * @param userId 用户id
	 * @return
	 */
	public List<Account> findList(boolean iscash,boolean ishide,int userId);
	
	
	/**
	 * 设置账户为隐藏或者不隐藏
	 * @param accountId 账户id
	 * @param ishide 是否隐藏，1为隐藏，0为不隐藏
	 */
	public void setHideOrNot(int accountId,int ishide);
	
	
	/**
	 * 查询账户列表，带分页
	 * @param page  当前页
	 * @param limit  每页条数\
	 * @param userId  用户id
	 * @return 分页对象
	 */
	public PageResult findPageList(int page,int limit,int userId);
	
	
	/**
	 * 根据id修改账户名
	 * @param newName 新的账户名
	 * @param id 账户id
	 */
	public void updateNameById(String newName,int id);
	
	/**
	 * 根据Id删除账户
	 * @param id
	 */
	public void deleteAccountById(int id);
	
	/**
	 * 保存账户
	 * @param accountName  账号名
	 * @param userId 用户id
	 */
	public void save(String accountName,int userId);
	
	/**
	 * 获取账户总额
	 * @param userId 用户id
	 * @return
	 */
	public double getTotalAccountLeft(int userId);
	
	
	/**
	 * 获取本年账户新增余额
	 * @return
	 */
	public double getTotalAmountIncrByYear(int userId);
	
	/**
	 * 获取压岁钱历史总额
	 * @param userId 用户id
	 * @return
	 */
	public double getTotalLuckyMoney(int userId);
	
	/**
	 * 获取本年度压岁钱总收入
	 * @return
	 */
	public double getLuckMoneyByYear(int userId);
	
	/**
	 * 获取总的打理收入
	 * @return
	 */
	public double getTotalIncome(int userId);
	
	/**
	 * 获取本年度打理收入
	 * @return
	 */
	public double getIncomeByYear(int userId);
	
	/**
	 * 获取本年度支出
	 * @return
	 */
	public double getConsumeByYear(int userId);
	
	/**
	 * 获取历史总支出
	 * @return
	 */
	public double getTotalConsume(int userId);
	
	/**
	 * 获取当前用户的现金账户id
	 * @param userId
	 * @return
	 */
	public int getCashAccountId(int userId);
	
	/**
	 * 获取当前用户的银行账户id
	 * @param userId
	 * @return
	 */
	public int getBankAccountId(int userId);
	
}
