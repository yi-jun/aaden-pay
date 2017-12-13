package com.aaden.pay.service.biz.tp.allinpay.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *  @Description 通联vo
 *  @author aaden
 *  @date 2017年12月27日
 */
@XStreamAlias("RNPC")
public class RNPC implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6442421303107550520L;

	private String MERCHANT_ID; // 商户代码

	private String SRCREQSN;// 原请求流水

	private String VERCODE;// 验证码

	public final String getMERCHANT_ID() {
		return MERCHANT_ID;
	}

	public final void setMERCHANT_ID(String mERCHANT_ID) {
		MERCHANT_ID = mERCHANT_ID;
	}

	public final String getSRCREQSN() {
		return SRCREQSN;
	}

	public final void setSRCREQSN(String sRCREQSN) {
		SRCREQSN = sRCREQSN;
	}

	public final String getVERCODE() {
		return VERCODE;
	}

	public final void setVERCODE(String vERCODE) {
		VERCODE = vERCODE;
	}

}
