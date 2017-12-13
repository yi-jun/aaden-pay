package com.aaden.pay.api.biz.enums.baofoo;

import com.aaden.pay.api.comm.enums.BankType;

/**
 *  @Description 宝付代付银行枚举
 *  @author aaden
 *  @date 2017年12月21日
 */
public enum BaofooPayBankType {

	ICBC(BankType.ICBC, "工商银行"),

	ABC(BankType.ABC, "农业银行"),

	CCB(BankType.CCB, "建设银行"),

	BOC(BankType.BOC, "中国银行"),

	BOCO(BankType.BOCO, "交通银行"),

	PSBC(BankType.PSBC, "邮政储蓄"),

	CITIC(BankType.CITIC, "中信银行"),

	CEB(BankType.CEB, "光大银行"),

	CIB(BankType.CIB, "兴业银行"),

	HXB(BankType.HXB, "华夏银行"),

	PAB(BankType.PAB, "平安银行"),

	CMBC(BankType.CMBC, "民生银行"),

	CMB(BankType.CMB, "招商银行"),

	GDB(BankType.GDB, "广发银行"),

	SPDB(BankType.SPDB, "浦发银行"),

	SHB(BankType.BOS, "上海银行"),

	BOB(BankType.BOB, "北京银行"),

	CBHB(BankType.CBHB, "渤海银行"),

	CZBANK(BankType.CZBANK, "浙商银行"),

	EGBANK(BankType.EGBANK, "恒丰银行"),

	;

	private final BankType bankType;// 平台统一编码,参考BankType.code属性
	private final String code;// 宝付支付代码

	BaofooPayBankType(BankType bankType, String code) {
		this.bankType = bankType;
		this.code = code;
	}

	public BankType getBankType() {
		return bankType;
	}

	public String getCode() {
		return code;
	}

	public static BaofooPayBankType parse(BankType bankType) {
		for (BaofooPayBankType item : BaofooPayBankType.values()) {
			if (item.getBankType() == bankType)
				return item;
		}
		return null;
	}

}
