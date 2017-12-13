package com.aaden.pay.api.comm.enums;

/**
 *  @Description 交易发送状态
 *  @author aaden
 *  @date 2017年12月17日
 */
public enum SendStatus {

	SENDING("发送中"),

	SUCCEED("发送并且响应成功"),

	SUC_FAIL("发送成功,获取数据失败,需手动查询!"),

	FAIL("发送失败!");

	private final String cnName;

	SendStatus(String cnName) {
		this.cnName = cnName;
	}

	public String getCnName() {
		return cnName;
	}

}
