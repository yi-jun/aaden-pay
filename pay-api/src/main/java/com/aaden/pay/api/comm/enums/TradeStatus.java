package com.aaden.pay.api.comm.enums;

/**
 *  @Description 交易状态
 *  @author aaden
 *  @date 2017年12月20日
 */
public enum TradeStatus {

	SUCCEED("交易成功"),

	FAILURE("交易失败"),

	RETRY("交易处理中"),

	OVERDUE("过期失效");

	private final String name;

	TradeStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
