package com.babymm.controller;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.babymm.domain.PageResult;
import com.babymm.domain.Result;
import com.babymm.entity.Account;
import com.babymm.entity.User;
import com.babymm.service.AccountService;

@RestController
@RequestMapping("/account")
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	
	@RequestMapping("/cashAccount/amount")
	//获取现金账户剩余金额，即可存放的压岁钱
	public Double getCashAmount() {	
		try {
			int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();		//获取用户id
			return accountService.getCashAmount(userId)/100.0;
		} catch (Exception e) {
			e.printStackTrace();
			return  0.0;
		}
	}
	
	
	//获取所有账户
	/**
	 * 
	 * @param iscash 表示是否获取现金账户,true表示获取，false表示不获取
	 * @param ishide 表示是否获取隐藏账户,true表示获取,false表示不获取
	 * @return
	 */
	@RequestMapping("/findAccountList")
	public List<Account> findList(boolean iscash,boolean ishide) {			//该参数表示是否需要现金账户,true表示要获取现金账户,false表示不获取现金账户
		try {
			int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();		//获取用户id
			List<Account> accountList =  accountService.findList(iscash,ishide,userId);
			//如果获取现金账户，就不进行信息过滤
			return accountList;
			
		} catch (Exception e) {
			e.printStackTrace();
			return  null;
		}
	}
	
	@RequestMapping("/setHideOrNot")
	//设置账户状态
	public Result setHideOrNot(Integer accountId,int ishide) {			//该参数表示账户是否隐藏,true表示要隐藏账户,false表示不隐藏账户
		try {
			//此处就是将ishide从1变成0,0变成1后传入下面去;(ishide ^ 1)表示与其参与一次异或运算
			accountService.setHideOrNot(accountId, (ishide ^ 1));
			return new Result(true, "操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			if("现金或活期账户不允许隐藏".equals(e.getMessage())) {
				return new Result(false, "现金或活期账户不允许隐藏");
			}else {
				return new Result(false, "未知错误，操作失败");
			}
		}
	}
	
	//查询账户列表，带分页
	@RequestMapping("/findPageList")
	public PageResult findPageList(int page,int limit) {
		try {
			int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();		//获取用户id
			return accountService.findPageList(page, limit,userId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//修改账户名
	@RequestMapping(value="/updateNameById",method=RequestMethod.POST)
	public Result updateNameById(String newName,int id) {
		try {
			accountService.updateNameById(newName, id);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "未知错误，操作失败");
		}
		
	}
	
	//删除记录
	@RequestMapping("/deleteAccountById")
	public Result deleteAccountById(int id) {
		try {
			accountService.deleteAccountById(id);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			if("该账户有关联记录，无法删除".equals(e.getMessage()))  {
				return new Result(false, "该账户有关联记录，无法删除");
			}else {
				return new Result(false, "未知错误，删除失败");
			}		
		}
	}
	
	//添加账户
	@RequestMapping("/add")
	public Result add(String newAccountName) {
		try {
			int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();		//获取用户id
			accountService.save(newAccountName,userId);
			return new Result(true, "创建成功");
		} catch (Exception e) {
			e.printStackTrace();
			if("已有相同账户名，无法创建".equals(e.getMessage()))  {
				return new Result(false, "已有相同账户名，无法创建");
			}else {
				return new Result(false, "未知错误，创建失败");
			}		
		}
	}
	
	@RequestMapping("/totalAccountLeft")
	//获取账户总额
	public double totalAccountLeft() {
		int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();		//获取用户id
		
		return accountService.getTotalAccountLeft(userId);
	}
	
	
	@RequestMapping("/totalAmountIncrByYear")			//获取账户本年新增总额
	public double getTotalAmountIncrByYear() {
		int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();		//获取用户id
		return accountService.getTotalAmountIncrByYear(userId);
	}
	
	@RequestMapping("/totalLuckyMoney")			//获取压岁钱历史总额
	public double getTotalLuckyMoney() {
		int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();		//获取用户id
		return accountService.getTotalLuckyMoney(userId);
	}
	
	@RequestMapping("/luckMoneyByYear")			//获取本年度压岁钱收入
	public double  getLuckMoneyByYear() {
		int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();		//获取用户id
		return accountService.getLuckMoneyByYear(userId);
	}
	
	
	@RequestMapping("/totalIncome")			//获取历史打理收入总和
	public double getTotalIncome() {
		int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();		//获取用户id
		return accountService.getTotalIncome(userId);
	}
	
	
	@RequestMapping("/incomeByYear")			//获取本年度打理收入
	public double getIncomeByYear() {
		int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();		//获取用户id
		return accountService.getIncomeByYear(userId);
	}
	
	@RequestMapping("/consumeByYear")			//获取本年度支出
	public double getConsumeByYear() {
		int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();		//获取用户id
		return accountService.getConsumeByYear(userId);
	}
	
	@RequestMapping("/totalConsume")
	public double getTotalConsume() {		//历史总支出
		int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();		//获取用户id
		return accountService.getTotalConsume(userId);
	}
	
	@RequestMapping("/amountIncreRate")
	public double getAmountIncreRate() {		//账户总额今年新增率 = 今年新增总额/(目前总额-今年新增总额)
		int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();		//获取用户id
		double total = accountService.getTotalAccountLeft(userId);		//目前总额
		double increAmount = accountService.getTotalAmountIncrByYear(userId);		//今年新增总额
		if(total == increAmount && total != 0) {
			return 10;				//如果是第一年开始记账，去年没有记录，则默认新增率为1000%,返回10
		}else if(total == increAmount && total == 0) {		//如果没有相关记录，就返回0
			return 0;
		}else{
			return increAmount/(total-increAmount);
		}	
	}
	
	@RequestMapping("/incomeIncreRate")			//账户收入今年新增率 = 今年账户总收入/(目前账户总收入-今年账户新增收入)
	public double getIncomeIncreRate() {
		int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();		//获取用户id
		double totalIncome = accountService.getTotalIncome(userId);
		double increIncome = accountService.getIncomeByYear(userId);
		if(totalIncome == increIncome && totalIncome != 0) {
			return 10;
		}else if(totalIncome == increIncome && totalIncome == 0) {		//如果没有记录，那么就返回0
			return 0;
		}{
			return increIncome/(totalIncome-increIncome);
		}
	}
	
	@RequestMapping("/cosumeIncreRate")
	public double getConsumeIncreRate() {
		int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();		//获取用户id
		double totalConsume = accountService.getTotalConsume(userId);
		double increConsume = accountService.getConsumeByYear(userId);
		if(totalConsume == increConsume && totalConsume != 0) {
			return 10;
		}else if(totalConsume == increConsume && totalConsume == 0) {
			return 0;
		}else{
			return increConsume/(totalConsume-increConsume);
		}
	}
}
