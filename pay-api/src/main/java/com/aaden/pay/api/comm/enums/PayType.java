package com.aaden.pay.api.comm.enums;

/**
 *  @Description 支付类型
 *  @author aaden
 *  @date 2017年12月21日
 */
public enum PayType {

	PAYOUT("单笔代付"),

	GATEWAY("网银支付"),

	AUTHPAY("认证支付"),

	;

	private final String name;

	PayType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
