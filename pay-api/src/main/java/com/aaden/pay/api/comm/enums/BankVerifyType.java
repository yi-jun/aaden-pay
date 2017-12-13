package com.aaden.pay.api.comm.enums;

import org.apache.commons.lang3.StringUtils;

/**
 *  @Description 银行卡验证类型
 *  @author aaden
 *  @date 2017年12月7日
 */
public enum BankVerifyType {

	APPLY("申请签约"),

	CONFIRM("确认签约");

	private String cnName;

	BankVerifyType(String cnName) {
		this.cnName = cnName;
	}

	public static final BankVerifyType parse(String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		try {
			return BankVerifyType.valueOf(value);
		} catch (Throwable t) {
			return null;
		}
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

}
