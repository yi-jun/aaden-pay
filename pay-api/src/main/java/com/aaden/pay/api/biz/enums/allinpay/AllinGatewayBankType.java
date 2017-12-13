package com.aaden.pay.api.biz.enums.allinpay;

import com.aaden.pay.api.comm.enums.BankType;

/**
 *  @Description 通联网关银行类型
 *  @author aaden
 *  @date 2017年12月23日
 */
public enum AllinGatewayBankType {

	ICBC(BankType.ICBC, "icbc"),

	ABC(BankType.ABC, "abc"),

	BOC(BankType.BOC, "boc"),

	CCB(BankType.CCB, "ccb"),

	BOCO(BankType.BOCO, "comm"),

	POST(BankType.PSBC, "psbc"),

	CMB(BankType.CMB, "cmb"),

	CIB(BankType.CIB, "cib"),

	CEB(BankType.CEB, "ceb"),

	CITIC(BankType.CITIC, "citic"),

	SPDB(BankType.SPDB, "spdb"),

	CMBC(BankType.CMBC, "cmbc"),

	HXB(BankType.HXB, "hxb"),

	GDB(BankType.GDB, "cgb"),

	PAB(BankType.PAB, "pingan"),

	BOS(BankType.BOS, "bos"),

	;

	private final BankType bankType;// 平台统一编码,参考BankType.code属性
	private final String code;// 通联支付代码

	AllinGatewayBankType(BankType bankType, String code) {
		this.bankType = bankType;
		this.code = code;
	}

	public BankType getBankType() {
		return bankType;
	}

	public String getCode() {
		return code;
	}

	public static AllinGatewayBankType parse(BankType bankType) {
		for (AllinGatewayBankType item : AllinGatewayBankType.values()) {
			if (item.getBankType() == bankType)
				return item;
		}
		return null;
	}
}
