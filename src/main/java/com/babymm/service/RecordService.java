package com.babymm.service;

import java.util.Date;

import com.babymm.domain.PageResult;
import com.babymm.entity.Record;

/**
 * 记录类
 * @author yluo0
 *
 */
public interface RecordService {
	
	/**
	 * 收压岁钱
	 * @param record 记录对象，里面包含交易信息
	 */
	public void getLuckyMoney(Record record);
	
	/**
	 * 存压岁钱
	 * @param record 记录对象，里面包含交易信息
	 * @param userId 用户id
	 */
	public void storeLuckyMoney(Record record,int userId);

	/**
	 * 记录收入
	 * @param record 记录对象，里面包含交易信息
	 * @param userId 用户id
	 */
	public void income(Record record,int userId);

	/**
	 * 记录支出
	* @param userId 用户id
	 * @param record 记录对象，里面包含交易信息
	 */
	public void consume(Record record,int userId);

	/**
	 * 记录转账
	 * @param userId 用户id
	 * @param record 记录对象，里面包含交易信息
	 */
	public void transfer(Record record,int userId);
	
	/**
	 * 分页查询
	 * @param page  当前页码
	 * @param limit 每页总数
	 * @param userId 用户id
	 * @return
	 */
	public PageResult findPageList(int page, int limit,int userId);
	
	/**
	 * 删除
	 * @param record
	 * @param userId
	 */
	public void delete(Record record,int userId);
	
	/**
	 * 带条件查询
	 * @param page   当前页码
	 * @param limit   每页条数
	 * @param createtime_start   开始时间
	 * @param createtime_end  结束时间
	 * @param inOrOut   记录类型
	 * @param userId  用户id
	 * @return
	 */
	public PageResult<Record> search(int page,int limit,Date createtime_start,Date createtime_end,Short inOrOut,int userId);
	
}
