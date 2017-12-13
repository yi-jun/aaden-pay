package com.aaden.pay.api.biz.enums.allinpay;

import com.aaden.pay.api.comm.enums.BankType;

/**
 *  @Description 通联快捷支付,代付银行类型
 *  @author aaden
 *  @date 2017年12月23日
 */
public enum AllinPayBankType {

	ABC(BankType.ABC, "0103"),

	ICBC(BankType.ICBC, "0102"),

	BOC(BankType.BOC, "0104"),

	CCB(BankType.CCB, "0105"),

	CITIC(BankType.CITIC, "0302"),

	CEB(BankType.CEB, "0303"),

	HXB(BankType.HXB, "0304"),

	CMB(BankType.CMB, "0308"),

	CIB(BankType.CIB, "0309"),

	CMBC(BankType.CMBC, "0305"),

	SPDB(BankType.SPDB, "0310"),

	POST(BankType.PSBC, "0403"),

	PAB(BankType.PAB, "0307"),

	BOCO(BankType.BOCO, "0301"),

	GDB(BankType.GDB, "0306"),

	BOB(BankType.BOB, "4031000"),

	BOS(BankType.BOS, "4012900"),

	;

	private final BankType bankType;// 平台统一编码,参考BankType.code属性
	private final String code;// 通联支付代码

	AllinPayBankType(BankType bankType, String code) {
		this.bankType = bankType;
		this.code = code;
	}

	public BankType getBankType() {
		return bankType;
	}

	public String getCode() {
		return code;
	}

	public static AllinPayBankType parse(BankType bankType) {
		for (AllinPayBankType item : AllinPayBankType.values()) {
			if (item.getBankType() == bankType)
				return item;
		}
		return null;
	}

}
