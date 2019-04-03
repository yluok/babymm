package com.babymm.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name="tb_record")
public class Record{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;			//id
	
	@Column(name="in_account_id")
	private Integer inAccountId;			//进账账户id
	
	@Column(name="out_account_id")
	private Integer outAccountId;			//出账账户id
	
	@Column(name="cash_amount")
	private Integer cashAmount;				//交易金额
	
	@Column(name="in_or_out")
	private Short inOrOut;				//记录标志，1表示收入，2表示支出，3表示转账
	
	private Date updatetime;			//记录更新时间
	
	private String content;				//交易备注
	
	@Column(name="user_id")
	private int userId;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date createtime;			//记录实际发生时间

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getInAccountId() {
		return inAccountId;
	}

	public void setInAccountId(Integer inAccountId) {
		this.inAccountId = inAccountId;
	}

	public Integer getOutAccountId() {
		return outAccountId;
	}

	public void setOutAccountId(Integer outAccountId) {
		this.outAccountId = outAccountId;
	}

	public Integer getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(Integer cashAmount) {
		this.cashAmount = cashAmount;
	}

	public Short getInOrOut() {
		return inOrOut;
	}

	public void setInOrOut(Short inOrOut) {
		this.inOrOut = inOrOut;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
