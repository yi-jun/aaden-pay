package com.aaden.pay.api.biz.vo;

import java.io.Serializable;

/**
 *  @Description 银行验证签约响应
 *  @author aaden
 *  @date 2017年12月22日
 */
public class BankResponse implements Serializable {

	private static final long serialVersionUID = -8806867905696399959L;

	private boolean success;

	private String msg;

	private String agreeNo;// 完成绑卡时,支付返回的快捷支付授权号码

	private BankResponse() {

	}

	public static BankResponse getSuccessInstance(String msg) {
		BankResponse rep = new BankResponse();
		rep.setSuccess(Boolean.TRUE);
		rep.setMsg(msg);
		return rep;
	}

	public static BankResponse getFailInstance(String msg) {
		BankResponse rep = new BankResponse();
		rep.setSuccess(Boolean.FALSE);
		rep.setMsg(msg);
		return rep;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getAgreeNo() {
		return agreeNo;
	}

	public void setAgreeNo(String agreeNo) {
		this.agreeNo = agreeNo;
	}

}
