package com.babymm.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.babymm.domain.PageResult;
import com.babymm.domain.RecordShow;
import com.babymm.domain.Result;
import com.babymm.entity.Account;
import com.babymm.entity.Record;
import com.babymm.entity.User;
import com.babymm.service.AccountService;
import com.babymm.service.RecordService;
import com.babymm.utils.CopyEntityUtils;

@RestController
@RequestMapping("/record")
public class RecordController {
	
	@Autowired
	private RecordService recordService;
	
	@Autowired
	private AccountService accountService;
	
	@RequestMapping("/subforLuckyMoney")
	// 收压岁钱
	public Result subforLuckyMoney(String cashAmount,String content,String createtime) {

		try {
			int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();
			BigDecimal bigd_amount = new BigDecimal(cashAmount);
			float amount_float = bigd_amount.floatValue();
			if(amount_float == 0) {
				return new Result(false, "kidding me?压根没有收到压岁钱!");
			}
			if(amount_float < 0) {
				return new Result(false, "请确保填写的金额是正数!");
			}
			//数据库保存的金额单位是分，所以要乘以100
			bigd_amount = bigd_amount.multiply(new BigDecimal(100));	
			
			//封装记录数据
			Record record = new Record();
			record.setCashAmount(bigd_amount.intValue());		//交易金额,乘以100，单位分
			if("".equals(content)) {
				content = "收压岁钱";
			}
			record.setContent(content);			//备注
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			record.setCreatetime(sdf.parse(createtime));		//流水产生时间
			record.setInOrOut((short)1);		//标志为1，即收入
			record.setUpdatetime(new Date());		//设置流水更新时间
			record.setUserId(userId);			//用户id
			//调用收压岁钱的方法
			recordService.getLuckyMoney(record);
			return new Result(true,"收到压岁钱啦");
		} catch (Exception e) {		//这里产生异常，常见于前端传递过来的不是数字
			e.printStackTrace();
			return new Result(false, "警告，未知错误，压岁钱还没有到账！");
		}

	}

	@RequestMapping("/storeforLuckyMoney")
	// 存压岁钱
	public Result storeforLuckyMoney(String cashAmount,String content,String createtime) {
		try {
			// 由于数据库中存放的金额单位为分，所以需转换成分再进行存储
			BigDecimal bigd_amount = new BigDecimal(cashAmount);
			float amount_float = bigd_amount.floatValue();
			if(amount_float == 0) {
				return new Result(false, "kidding me?压根没有存压岁钱!");
			}
			if(amount_float < 0) {
				return new Result(false, "请确保填写的金额是正数!");
			}
			//金额乘以100
			bigd_amount = bigd_amount.multiply(new BigDecimal(100));
			
			//封装数据
			Record record = new Record();
			record.setCashAmount(bigd_amount.intValue());		//交易金额
			if("".equals(content)) {
				content = "存压岁钱";
			}
			record.setContent(content);		//备注
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			record.setCreatetime(sdf.parse(createtime));		//流水产生时间
			record.setInOrOut((short)3);		//流水标志为3,即转账
			record.setUpdatetime(new Date());		//更新时间
			int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();
			recordService.storeLuckyMoney(record,userId);		
			
			return new Result(true, "压岁钱到银行账户啦");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "警告，未知错误，存压岁钱操作失败！");
		}

	}
	
	/**
	 * 添加收入
	 * @param record
	 * @return
	 */
	@RequestMapping(value="/income",method=RequestMethod.POST)
	public Result income(String cashAmount,int inAccountId,String content,String createtime) {

		try {
			BigDecimal bigd = new BigDecimal(cashAmount);
			float amount_float = bigd.floatValue();
			if(amount_float == 0) {
				return new Result(false, "kidding me?压根没有收入!");
			}
			if(amount_float < 0) {
				return new Result(false, "请确保填写的金额是正数!");
			}
			//金额乘以100，并存起来
			//封装数据
			Record record = new Record();
			record.setCashAmount(bigd.multiply(new BigDecimal(100)).intValue());		//交易金额乘以100，单位为分
			record.setContent(content);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			record.setCreatetime(sdf.parse(createtime));
			record.setInAccountId(inAccountId);
			record.setInOrOut((short)1);
			record.setUpdatetime(new Date());
			int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();
			record.setUserId(userId);
			recordService.income(record,userId);
			return new Result(true, "great,收入到账啦！");
		} catch (Exception e) {
			
			e.printStackTrace();
			return new Result(false, "警告，未知错误，收入录入失败！");
		}
	
	}
	
	/**
	 * 添加支出
	 * @return
	 */
	@RequestMapping(value="/consume",method=RequestMethod.POST)
	public Result consume(String cashAmount,int outAccountId,String content,String createtime) {
		System.out.println("前端的对象：" + cashAmount);
		
		try {
			BigDecimal bigd = new BigDecimal(cashAmount);
			float amount_float = bigd.floatValue();
			if(amount_float == 0) {
				return new Result(false, "kidding me?压根没有支出!");
			}
			if(amount_float < 0) {
				return new Result(false, "请确保填写的金额是正数!");
			}
			
			//封装数据
			Record record = new Record();
			record.setCashAmount(bigd.multiply(new BigDecimal(100)).intValue());		//交易金额乘以100，单位为分
			record.setContent(content);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			record.setCreatetime(sdf.parse(createtime));
			record.setOutAccountId(outAccountId);
			record.setInOrOut((short)2);
			record.setUpdatetime(new Date());
		
			int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();
			record.setUserId(userId);
			recordService.consume(record,userId);
			return new Result(true, "呜呜,压岁钱真的跑了。。。");
		} catch (Exception e) {		
			e.printStackTrace();
			return new Result(false, "警告，未知错误，支出录入失败！"); 
		}
	}
	
	/**
	 * 添加转账
	 * @return
	 */
	@RequestMapping(value="/transfer",method=RequestMethod.POST)
	public Result transfer(String cashAmount,int inAccountId,int outAccountId,String content,String createtime) {
		try {
			BigDecimal bigd = new BigDecimal(cashAmount);
			float amount_float = bigd.floatValue();
			if(amount_float == 0) {
				return new Result(false, "kidding me?压根没有支出!");
			}
			if(amount_float < 0) {
				return new Result(false, "请确保填写的金额是正数!");
			}
			if(inAccountId == outAccountId) {
				return new Result(false, "转账失败，请检查转账账户！");
			}
			//封装数据
			Record record = new Record();
			record.setCashAmount(bigd.multiply(new BigDecimal(100)).intValue());		//交易金额乘以100，单位为分
			record.setContent(content);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			record.setCreatetime(sdf.parse(createtime));
			record.setOutAccountId(outAccountId);
			record.setInAccountId(inAccountId);
			record.setInOrOut((short)3);
			record.setUpdatetime(new Date());
			int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();
			record.setUserId(userId);
			recordService.transfer(record,userId);
			return new Result(true, "转账成功");
		} catch (Exception e) {		
			e.printStackTrace();
			return new Result(false, "警告，未知错误，转账录入失败！"); 
		}
	}
	
	@RequestMapping("/findPageList")
	//由于返回的数据格式与需求不符，需要将账户id与name进行一下映射后再返回给页面	
	public PageResult findPageList(int page, int limit) {
		try {
			//获取到原始分页结果内容
			int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();		//获取用户id
			PageResult pageresult  = recordService.findPageList(page, limit,userId);
			
			
			return  ParseRecordToRecordShow(pageresult);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 将PageResult<Record> 转换成 PageResult<RecordShow>
	 * @param pageresult  PageResult<Record>对象
	 * @return    PageResult<RecordShow>对象
	 */
	private PageResult ParseRecordToRecordShow(PageResult pageresult) {
		int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();
		List<Record> pageRecorde =  pageresult.getData();
		//获取到所有账户列表
		List<Account> at = accountService.findList(true,true,userId);
		//创建一个用于装返回数据结果的列表
		List<RecordShow> rshlist = new ArrayList<RecordShow>();
		
		//遍历原始结果
		for(int i = 0;i < pageRecorde.size();i++) {
			//获取到其中一条记录
			Record record = pageRecorde.get(i);	
			RecordShow rshow = new RecordShow();	
			//将记录值全部copy至其子类中，该子类还包含id所对应的属性name
			CopyEntityUtils.copyFatherToChild(rshow, record);
			for(Account account: at) {
				if(record.getInOrOut() == 1) {		//收入
					if(account.getId() == record.getInAccountId()) {
						rshow.setInAccountName(account.getName());
						rshlist.add(rshow);
						break;
					}
				}else if(record.getInOrOut() == 2) {		//支出
					if(account.getId() == record.getOutAccountId()) {
						rshow.setOutAccountName(account.getName());
						rshlist.add(rshow);
						break;
					}					
				}else {		//转账
					if(account.getId() == record.getOutAccountId()) {
						rshow.setOutAccountName(account.getName());
					}if(account.getId() == record.getInAccountId()) {
						rshow.setInAccountName(account.getName());
					}if(rshow.getInAccountName() != null && rshow.getOutAccountName() != null) {
						rshlist.add(rshow);
						break;
					}
				}
			}
		}
		pageresult.setData(rshlist);
		return pageresult;
	}
	
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	public Result delete(@RequestBody Record record) {
		try {
			int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();
			recordService.delete(record,userId);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "未知错误，删除失败");
		}
	}
	
	
	@RequestMapping("/search")
	public PageResult<RecordShow> search(int page,int limit,String createtime_start,String createtime_end,Short inOrOut) {
		try {
			int userId = ((User)SecurityUtils.getSubject().getPrincipal()).getId();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date start = null;
			Date end = null;
			if(!"".equals(createtime_start)) {
				start = sdf.parse(createtime_start);
			}			
			if(!"".equals(createtime_end)) {
				end = sdf.parse(createtime_end);
			}
			PageResult pageResult = recordService.search(page, limit, start, end, inOrOut,userId);
			return ParseRecordToRecordShow(pageResult);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
		
	}
}
