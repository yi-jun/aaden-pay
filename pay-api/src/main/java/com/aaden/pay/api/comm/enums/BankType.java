package com.aaden.pay.api.comm.enums;

/**
 *  @Description 系统内部使用银行枚举
 *  @author aaden
 *  @date 2017年12月8日
 */
public enum BankType {

	BOC("中国银行"),

	ABC("中国农业银行"),

	ICBC("中国工商银行"),

	CCB("中国建设银行"),

	BOCO("中国交通银行"),

	CITIC("中信银行"),

	CEB("中国光大银行"),

	CMBC("中国民生银行"),

	GDB("广东发展银行"),

	CMB("招商银行"),

	CIB("兴业银行"),

	SPDB("上海浦东发展银行"),

	BOS("上海银行"),

	PSBC("邮政储蓄银行"),

	PAB("平安银行"),

	BOB("北京银行"),

	HXB("华夏银行"),

	CBHB("渤海银行"),

	HKBEA("东亚银行"),

	CZBANK("浙商银行"),

	EGBANK("恒丰银行"),

	;

	private final String cnName;// 全称

	BankType(String cnName) {
		this.cnName = cnName;
	}

	public String getCnName() {
		return cnName;
	}

}
