package com.aaden.pay.api.biz.vo.data;

import java.io.Serializable;

import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.PayType;

/**
 * 系统参数
 */
public class PaySysData implements Serializable {
	private static final long serialVersionUID = 7052753973136720271L;
	private PayChannel payChannel; // 支付渠道
	private PayType payType;// 支付方式
	private String rechargeToken;// 充值验证码的token

	public PayChannel getPayChannel() {
		return payChannel;
	}

	public PaySysData setPayChannel(PayChannel payChannel) {
		this.payChannel = payChannel;
		return this;
	}

	public PayType getPayType() {
		return payType;
	}

	public PaySysData setPayType(PayType payType) {
		this.payType = payType;
		return this;
	}

	public String getRechargeToken() {
		return rechargeToken;
	}

	public PaySysData setRechargeToken(String rechargeToken) {
		this.rechargeToken = rechargeToken;
		return this;
	}

}