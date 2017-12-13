package com.aaden.pay.api.comm.enums;

/**
 *  @Description 银行卡属性
 *  @author aaden
 *  @date 2017年12月21日
 */
public enum CardProp {

	person("对私", "0"),

	company("对公", "1"),

	;

	private final String name;
	private final String code;

	CardProp(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

}
