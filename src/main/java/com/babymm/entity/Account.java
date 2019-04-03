package com.babymm.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 账户类：包括所有账户，其中现金账户(不可转入)、活期账户是内置账户，不能对其删除和隐藏。
 * 现金账户：只能收压岁钱，存压岁钱（从现金账户转出至活期账户）,不能进行其余操作
 * 活期账户：存压岁钱（从现金账户转入），和其余账户发生转账操作，不能进行删除和隐藏操作
 * 其余账户：只能和活期账户发生关联性操作，可以进行所有操作，如果其有对应操作记录时，不能删除，只能隐藏。
 * @author yluo0
 *
 */
@Entity
@Table(name="tb_account")
public class Account {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	private String name;
	
	@Column(name="cash_amount")
	private Integer cashAmount;
	
	private int ishide;
	
	private int income;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date createtime;
	
	private int consume;
	
	@Column(name="user_id")
	private int userId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(Integer cashAmount) {
		this.cashAmount = cashAmount;
	}

	public int getIshide() {
		return ishide;
	}

	public void setIshide(int ishide) {
		this.ishide = ishide;
	}

	public int getIncome() {
		return income;
	}

	public void setIncome(int income) {
		this.income = income;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public int getConsume() {
		return consume;
	}

	public void setConsume(int consume) {
		this.consume = consume;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
