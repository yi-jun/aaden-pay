package com.aaden.pay.service.biz.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.aaden.pay.api.comm.enums.SendStatus;
import com.aaden.pay.api.comm.enums.TradeStatus;

/**
 *  @Description 第三方交易响应
 *  @author aaden
 *  @date 2017年12月4日
 */
public class ThirdPayResponse implements Serializable {

	private static final long serialVersionUID = 5042512052371737267L;

	private String serialnumber;// 流水号

	private String thirdnumber;// 支付平台订单号

	private TradeStatus tradeStatus;// 平台交易状态

	private SendStatus sendStatus;// 平台发送状态

	private String payCode;// 响应状态

	private String payMessage;// 第三方支付返回的错误信息

	private BigDecimal actAmount;// 实际金额

	private Date settleTime;// 支付公司清算日期

	private BigDecimal fee;// 第三方支付手续费

	private String token;// 认证充值时返回的token

	private String postUrl;// 提交地址,适用于页面跳转
	private Map<String, String> postMap;// 提交表单,适用于页面跳转

	public Map<String, String> getPostMap() {
		return postMap;
	}

	public void setPostMap(Map<String, String> postMap) {
		this.postMap = postMap;
	}

	public String getPostUrl() {
		return postUrl;
	}

	public void setPostUrl(String postUrl) {
		this.postUrl = postUrl;
	}

	public String getSerialnumber() {
		return serialnumber;
	}

	public void setSerialnumber(String serialnumber) {
		this.serialnumber = serialnumber;
	}

	public TradeStatus getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(TradeStatus tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public SendStatus getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(SendStatus sendStatus) {
		this.sendStatus = sendStatus;
	}

	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	public String getPayMessage() {
		return payMessage;
	}

	public void setPayMessage(String payMessage) {
		this.payMessage = payMessage;
	}

	public String getThirdnumber() {
		return thirdnumber;
	}

	public void setThirdnumber(String thirdnumber) {
		this.thirdnumber = thirdnumber;
	}

	public Date getSettleTime() {
		return settleTime;
	}

	public void setSettleTime(Date settleTime) {
		this.settleTime = settleTime;
	}

	public BigDecimal getActAmount() {
		return actAmount;
	}

	public void setActAmount(BigDecimal actAmount) {
		this.actAmount = actAmount;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isSuccessed() {
		return TradeStatus.SUCCEED == (this.getTradeStatus());
	}

	public boolean isNotSure() {
		return !this.isFailure() && !this.isSuccessed();
	}

	public boolean isFailure() {
		return TradeStatus.FAILURE == (this.getTradeStatus()) || TradeStatus.OVERDUE == (this.getTradeStatus());
	}
}
