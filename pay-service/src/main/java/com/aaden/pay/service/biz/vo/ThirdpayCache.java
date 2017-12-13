package com.aaden.pay.service.biz.vo;

import java.io.Serializable;

/**
 * @Description 第三方充值验证码缓存,
 *              支付流程为:先调用充值验证码api,在调用充值api,验证码api会返回一个支付token,凭此token进行支付
 * @author aaden
 * @date 2017年12月10日
 */
public class ThirdpayCache implements Serializable{

	private static final long serialVersionUID = -6052342769851062664L;
	private String serialnumber;
	private String token;
	private String userId;

	public ThirdpayCache(String serialnumber, String token, String userId) {
		this.serialnumber = serialnumber;
		this.userId = userId;
		this.token = token;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSerialnumber() {
		return serialnumber;
	}

	public void setSerialnumber(String serialnumber) {
		this.serialnumber = serialnumber;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
