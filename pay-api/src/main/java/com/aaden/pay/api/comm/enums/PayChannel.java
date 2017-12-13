package com.aaden.pay.api.comm.enums;

/**
 *  @Description 支付渠道
 *  @author aaden
 *  @date 2017年12月9日
 */
public enum PayChannel {

	BAOFOO("宝付"),
	
	ALLIN("通联"),


	;

	private final String cnName;

	private PayChannel(String cnName) {
		this.cnName = cnName;
	}

	public final String getCnName() {
		return cnName;
	}

}
