package com.aaden.pay.api.biz.vo.data;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import com.aaden.pay.api.biz.validation.DecimalScale;
import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.CardProp;

/**
 * 必须参数
 */
public class PayMustData implements Serializable {
	private static final long serialVersionUID = -1637895572493974205L;

	@NotNull(message = "用户信息不能为空")
	private String userLoginName;// 登录名,冗余,可免关联查询

	@NotNull(message = "用户信息不能为空")
	private String userId; // 用户ID

	@NotNull(message = "银行卡账号属性不能为空")
	private CardProp cardProp = CardProp.person; // 账号属性,默认为个人

	@NotNull(message = "银行卡号不能为空")
	private String cardNo; // 银行卡号,网银建议传入固定字符串,若希望可传入空值,请取消notNull注解

	@NotNull(message = "开户人姓名不能为空")
	private String realName; // 开户人姓名

	@NotNull(message = "银行卡类型不能为空")
	private BankType bankType;// 银行类型

	@NotNull(message = "支付金额不能为空")
	@DecimalMin(value = "0.01", message = "支付金额不能低于0")
	@DecimalScale(value = 2, message = "支付金额最多只能有两位小数")
	private BigDecimal amount; // 本次支付金额

	@NotNull(message = "系统订单号不能为空")
	private String orderCode; // 系统内部订单号,唯一标识一笔订单,用于重复支付判断

	public String getUserId() {
		return userId;
	}

	public PayMustData setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getUserLoginName() {
		return userLoginName;
	}

	public PayMustData setUserLoginName(String userLoginName) {
		this.userLoginName = userLoginName;
		return this;
	}

	public CardProp getCardProp() {
		return cardProp;
	}

	public PayMustData setCardProp(CardProp cardProp) {
		this.cardProp = cardProp;
		return this;
	}

	public String getCardNo() {
		return cardNo;
	}

	public PayMustData setCardNo(String cardNo) {
		this.cardNo = cardNo;
		return this;
	}

	public String getRealName() {
		return realName;
	}

	public PayMustData setRealName(String realName) {
		this.realName = realName;
		return this;
	}

	public BankType getBankType() {
		return bankType;
	}

	public PayMustData setBankType(BankType bankType) {
		this.bankType = bankType;
		return this;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public PayMustData setAmount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public PayMustData setOrderCode(String orderCode) {
		this.orderCode = orderCode;
		return this;
	}

}