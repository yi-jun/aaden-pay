package com.aaden.pay.service.resouce.lianlian;

import com.aaden.pay.api.comm.enums.BankType;

/**
 * @Description 连连工具类
 * @author aaden
 * @date 2017年12月12日
 */
public enum LianlianBankType {

	CCB(BankType.CCB, "01050000", "建设银行"),

	ICBC(BankType.ICBC, "01020000", "工商银行"),

	ABC(BankType.ABC, "01030000", "农业银行"),

	BOC(BankType.BOC, "01040000", "中国银行"),

	CMB(BankType.CMB, "03080000", "招商银行"),

	CMBC(BankType.CMBC, "03050000", "民生银行"),

	BOCO(BankType.BOCO, "03010000", "交通银行"),

	CIB(BankType.CIB, "03090000", "兴业银行"),

	CEB(BankType.CEB, "03030000", "光大银行"),

	PAB(BankType.PAB, "03070000", "平安银行"),

	CITIC(BankType.CITIC, "03020000", "中信银行"),

	SPDB(BankType.SPDB, "03100000", "上海浦发银行"),

	POST(BankType.PSBC, "01000000", "邮政储蓄银行"),

	GDB(BankType.GDB, "03060000", "广东发展银行"),

	HXB(BankType.HXB, "03040000", "华夏银行"),

	HKBEA(BankType.HKBEA, "03200000", "东亚银行"),

	BOS(BankType.BOS, "04012900", "上海银行"),

	CBHB(BankType.CBHB, "03170000", "渤海银行"),

	BOB(BankType.BOB, "04031000", "北京银行"),

	;

	private final BankType bankType;// 平台统一
	private final String code;// 连连代码
	private final String cnName;// 银行名称

	LianlianBankType(BankType bankType, String code, String cnName) {
		this.bankType = bankType;
		this.code = code;
		this.cnName = cnName;
	}

	public BankType getBankType() {
		return bankType;
	}

	public String getCode() {
		return code;
	}

	public String getCnName() {
		return cnName;
	}

	public static LianlianBankType parse(BankType bankType) {
		for (LianlianBankType item : LianlianBankType.values()) {
			if (item.getBankType() == bankType)
				return item;
		}
		return null;
	}

}
