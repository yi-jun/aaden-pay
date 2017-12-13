package com.aaden.pay.api.biz.config;

import org.apache.commons.lang3.StringUtils;

import com.aaden.pay.api.comm.enums.BankType;

/**
 *  @Description 银行卡bin
 *  @author aaden
 *  @date 2017年12月19日
 */
public class BankCardBin implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2090351600601606057L;
	private String bankbin;// bin头
	private BankType bankType;// 银行类型
	private String bankname;// 银行名称
	private String cardType;// 类型,如借记卡,信用卡

	public final String getBankbin() {
		return bankbin;
	}

	public final void setBankbin(String bankbin) {
		this.bankbin = bankbin;
	}

	public BankType getBankType() {
		return bankType;
	}

	public void setBankType(BankType bankType) {
		this.bankType = bankType;
	}

	public final String getBankname() {
		return bankname;
	}

	public final void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public final boolean isjieji() {
		return StringUtils.equals(this.getCardType(), "借记卡");
	}

	public final boolean match(BankType bankType) {

		if (this.getBankname().contains(bankType.getCnName())) {
			return true;
		}

		if (this.getBankType() == bankType) {
			return true;
		}
		return false;
	}

}
