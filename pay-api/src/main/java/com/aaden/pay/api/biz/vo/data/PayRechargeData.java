package com.aaden.pay.api.biz.vo.data;

import java.io.Serializable;

import com.aaden.pay.api.comm.enums.PayChannel;

/**
 * 充值选填参数
 */
public class PayRechargeData implements Serializable {
	private static final long serialVersionUID = 3823491825293927785L;

	private String agreeNo;// 认证支付签约协议号

	private String idNo;// 身份证

	// private String idNoType;// 身份证类型,2017/12/25日注:作者目前遇到的支付渠道只支持身份证,取消该字段

	private String validCode;// 验证码

	private String mobile; // 预留手机号码

	private String clientIp;// 用户请求ip,(宝付支付充值时必填)

	private PayChannel payChannel; // 支付渠道(可指定,未指定由系统路由自动判断)

	public String getAgreeNo() {
		return agreeNo;
	}

	public PayRechargeData setAgreeNo(String agreeNo) {
		this.agreeNo = agreeNo;
		return this;
	}

	public String getIdNo() {
		return idNo;
	}

	public PayRechargeData setIdNo(String idNo) {
		this.idNo = idNo;
		return this;
	}

	public String getValidCode() {
		return validCode;
	}

	public PayRechargeData setValidCode(String validCode) {
		this.validCode = validCode;
		return this;
	}

	public String getMobile() {
		return mobile;
	}

	public PayRechargeData setMobile(String mobile) {
		this.mobile = mobile;
		return this;
	}

	public String getClientIp() {
		return clientIp;
	}

	public PayRechargeData setClientIp(String clientIp) {
		this.clientIp = clientIp;
		return this;
	}

	public PayChannel getPayChannel() {
		return payChannel;
	}

	public PayRechargeData setPayChannel(PayChannel payChannel) {
		this.payChannel = payChannel;
		return this;
	}

}
