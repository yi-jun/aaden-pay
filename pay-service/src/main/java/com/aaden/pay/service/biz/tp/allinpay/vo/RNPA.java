package com.aaden.pay.service.biz.tp.allinpay.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *  @Description 通联vo
 *  @author aaden
 *  @date 2017年12月18日
 */
@XStreamAlias("RNPA")
public class RNPA implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6442421303107550520L;

	private String MERCHANT_ID; // 商户代码

	private String BANK_CODE;// 银行代码

	private String ACCOUNT_TYPE;// 账号类型

	private String ACCOUNT_NO;// 账号

	private String ACCOUNT_NAME;// 账号名

	private String ACCOUNT_PROP;// 账号属性

	private String ID_TYPE;// 开户证件类型

	private String ID;// 证件号

	private String TEL;// 手机号/小灵通

	private String MERREM;// 商户保留信息

	private String REMARK;// 备注

	private String SUBMIT_TIME;//

	public final String getMERCHANT_ID() {
		return MERCHANT_ID;
	}

	public final void setMERCHANT_ID(String mERCHANT_ID) {
		MERCHANT_ID = mERCHANT_ID;
	}

	public final String getBANK_CODE() {
		return BANK_CODE;
	}

	public final void setBANK_CODE(String bANK_CODE) {
		BANK_CODE = bANK_CODE;
	}

	public final String getACCOUNT_TYPE() {
		return ACCOUNT_TYPE;
	}

	public final void setACCOUNT_TYPE(String aCCOUNT_TYPE) {
		ACCOUNT_TYPE = aCCOUNT_TYPE;
	}

	public final String getACCOUNT_NO() {
		return ACCOUNT_NO;
	}

	public final void setACCOUNT_NO(String aCCOUNT_NO) {
		ACCOUNT_NO = aCCOUNT_NO;
	}

	public final String getACCOUNT_NAME() {
		return ACCOUNT_NAME;
	}

	public final void setACCOUNT_NAME(String aCCOUNT_NAME) {
		ACCOUNT_NAME = aCCOUNT_NAME;
	}

	public final String getACCOUNT_PROP() {
		return ACCOUNT_PROP;
	}

	public final void setACCOUNT_PROP(String aCCOUNT_PROP) {
		ACCOUNT_PROP = aCCOUNT_PROP;
	}

	public final String getID_TYPE() {
		return ID_TYPE;
	}

	public final void setID_TYPE(String iD_TYPE) {
		ID_TYPE = iD_TYPE;
	}

	public final String getID() {
		return ID;
	}

	public final void setID(String iD) {
		ID = iD;
	}

	public final String getTEL() {
		return TEL;
	}

	public final void setTEL(String tEL) {
		TEL = tEL;
	}

	public final String getMERREM() {
		return MERREM;
	}

	public final void setMERREM(String mERREM) {
		MERREM = mERREM;
	}

	public final String getREMARK() {
		return REMARK;
	}

	public final void setREMARK(String rEMARK) {
		REMARK = rEMARK;
	}

	public final String getSUBMIT_TIME() {
		return SUBMIT_TIME;
	}

	public final void setSUBMIT_TIME(String sUBMIT_TIME) {
		SUBMIT_TIME = sUBMIT_TIME;
	}

}
