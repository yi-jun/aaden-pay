package com.aaden.pay.api.biz.enums.baofoo;

import com.aaden.pay.api.comm.enums.BankType;

/**
 *  @Description 宝付认证支付银行
 *  @author aaden
 *  @date 2017年12月27日
 */
public enum BaofooBankType {

	ICBC(BankType.ICBC, "ICBC"),

	ABC(BankType.ABC, "ABC"),

	CCB(BankType.CCB, "CCB"),

	BOC(BankType.BOC, "BOC"),

	BOCO(BankType.BOCO, "BCOM"),

	CIB(BankType.CIB, "CIB"),

	CITIC(BankType.CITIC, "CITIC"),

	CEB(BankType.CEB, "CEB"),

	PAB(BankType.PAB, "PAB"),

	PSBC(BankType.PSBC, "PSBC"),

	SHB(BankType.BOS, "SHB"),

	SPDB(BankType.SPDB, "SPDB"),

	CMBC(BankType.CMBC, "CMBC"),

	CMB(BankType.CMB, "CMB"),

	GDB(BankType.GDB, "GDB"),

	HXB(BankType.HXB, "HXB"),

	;

	private final BankType bankType;// 平台统一编码,参考BankType.code属性
	private final String code;// 宝付支付代码

	BaofooBankType(BankType bankType, String code) {
		this.bankType = bankType;
		this.code = code;
	}

	public BankType getBankType() {
		return bankType;
	}

	public String getCode() {
		return code;
	}

	public static BaofooBankType parse(BankType bankType) {
		for (BaofooBankType item : BaofooBankType.values()) {
			if (item.getBankType() == bankType)
				return item;
		}
		return null;
	}

}
