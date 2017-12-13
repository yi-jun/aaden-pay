package com.aaden.pay.service.biz.tp.allinpay.vo;

/**
 *  @Description 通联vo
 *  @author aaden
 *  @date 2017年12月27日
 */
public class RNPCRET {
	private String RET_CODE;// 返回码

	private String ERR_MSG;// 错误文本

	private String ISSENDSMS;// 是否发送短信验证码

	public final String getRET_CODE() {
		return RET_CODE;
	}

	public final void setRET_CODE(String rET_CODE) {
		RET_CODE = rET_CODE;
	}

	public final String getERR_MSG() {
		return ERR_MSG;
	}

	public final void setERR_MSG(String eRR_MSG) {
		ERR_MSG = eRR_MSG;
	}

	public final String getISSENDSMS() {
		return ISSENDSMS;
	}

	public final void setISSENDSMS(String iSSENDSMS) {
		ISSENDSMS = iSSENDSMS;
	}

}
