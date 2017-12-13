package com.aaden.pay.api.comm.model;

import java.math.BigDecimal;
import java.util.Date;

import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.TradeStatus;
import com.aaden.pay.core.model.BaseModel;

/**
 * @Description 认证支付充值验证码记录
 * @author aaden
 * @date 2017年12月24日
 */
public class ThirdPayValidcode extends BaseModel {

	private static final long serialVersionUID = 272702261795286514L;

	private Long id; //

	private String payId; //

	private String userId; //

	private BigDecimal amount; //

	private String serialnumber; //

	private TradeStatus tradeStatus; //

	private PayChannel payChannel; //

	private String retCode; //

	private String retMsg; //

	private String userLoginName; //

	private Date sendTime; //

	private String token;

	public final void setId(Long value) {
		this.id = value;
	}

	public final Long getId() {
		return this.id;
	}

	public final void setPayId(String value) {
		this.payId = value;
	}

	public final String getPayId() {
		return this.payId;
	}

	public final void setUserId(String value) {
		this.userId = value;
	}

	public final String getUserId() {
		return this.userId;
	}

	public final void setAmount(BigDecimal value) {
		this.amount = value;
	}

	public final BigDecimal getAmount() {
		return this.amount;
	}

	public final void setSerialnumber(String value) {
		this.serialnumber = value;
	}

	public final String getSerialnumber() {
		return this.serialnumber;
	}

	public final void setTradeStatus(TradeStatus value) {
		this.tradeStatus = value;
	}

	public final TradeStatus getTradeStatus() {
		return this.tradeStatus;
	}

	public final String getTradeStatusCn() {
		return this.tradeStatus == null ? "" : this.tradeStatus.getName();
	}

	public final void setPayChannel(PayChannel value) {
		this.payChannel = value;
	}

	public final String getPayChannelCn() {
		return this.payChannel == null ? "" : payChannel.getCnName();
	}

	public final PayChannel getPayChannel() {
		return this.payChannel;
	}

	public final void setRetCode(String value) {
		this.retCode = value;
	}

	public final String getRetCode() {
		return this.retCode;
	}

	public final void setRetMsg(String value) {
		this.retMsg = value;
	}

	public final String getRetMsg() {
		return this.retMsg;
	}

	public final void setUserLoginName(String value) {
		this.userLoginName = value;
	}

	public final String getUserLoginName() {
		return this.userLoginName;
	}

	public final void setSendTime(Date value) {
		this.sendTime = value;
	}

	public final Date getSendTime() {
		return this.sendTime;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void initPrimaryKey() {
		this.setPayId(randomUUID());
	}

	/**
	 * 是否为成功的支付
	 */
	public boolean isSuccess() {
		return TradeStatus.SUCCEED == (this.tradeStatus);
	}
}
