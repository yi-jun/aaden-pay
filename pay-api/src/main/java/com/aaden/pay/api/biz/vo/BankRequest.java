package com.aaden.pay.api.biz.vo;

import java.io.Serializable;

import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.BankVerifyType;
import com.aaden.pay.api.comm.enums.PayChannel;

/**
 *  @Description 银行验证签约请求数据
 *  @author aaden
 *  @date 2017年12月16日
 */
public class BankRequest implements Serializable {

	private static final long serialVersionUID = 2638167358331422605L;

	// 绑卡类型
	private BankVerifyType bankVerifyType;
	// 绑卡第一步需要的数据
	private InfoData info = new InfoData();
	// 绑卡第二步需要的数据
	private BindData bind = new BindData();

	public BankVerifyType getBankVerifyType() {
		return bankVerifyType;
	}

	public void setBankVerifyType(BankVerifyType bankVerifyType) {
		this.bankVerifyType = bankVerifyType;
	}

	public InfoData getInfo() {
		return info;
	}

	public void setInfo(InfoData info) {
		this.info = info;
	}

	public BindData getBind() {
		return bind;
	}

	public void setBind(BindData bind) {
		this.bind = bind;
	}

	public static class InfoData implements Serializable{
		private static final long serialVersionUID = -542047022798776249L;
		private String userId;// 用户ID
		private String userLoginName;// 冗余用户名
		private String mobile;// 银行预留手机号码
		private String realName;// 真实姓名
		private String idNo;// 身份证号
		private String idNoType;// 证件类型
		private String cardNo;// 银行卡号
		private BankType bankType;// 银行类型
		private PayChannel channel;// 验证渠道
		private String sourceType;// 来源（网站、微站，未来会有APP）

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getUserLoginName() {
			return userLoginName;
		}

		public void setUserLoginName(String userLoginName) {
			this.userLoginName = userLoginName;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getRealName() {
			return realName;
		}

		public void setRealName(String realName) {
			this.realName = realName;
		}

		public String getIdNo() {
			return idNo;
		}

		public void setIdNo(String idNo) {
			this.idNo = idNo;
		}

		public String getIdNoType() {
			return idNoType;
		}

		public void setIdNoType(String idNoType) {
			this.idNoType = idNoType;
		}

		public String getCardNo() {
			return cardNo;
		}

		public void setCardNo(String cardNo) {
			this.cardNo = cardNo;
		}

		public BankType getBankType() {
			return bankType;
		}

		public void setBankType(BankType bankType) {
			this.bankType = bankType;
		}

		public PayChannel getChannel() {
			return channel;
		}

		public void setChannel(PayChannel channel) {
			this.channel = channel;
		}

		public String getSourceType() {
			return sourceType;
		}

		public void setSourceType(String sourceType) {
			this.sourceType = sourceType;
		}

	}

	public static class BindData implements Serializable{
		private static final long serialVersionUID = -6016576662619339383L;
		private String userId;// 用户ID
		private String validCode;// 手机验证码

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getValidCode() {
			return validCode;
		}

		public void setValidCode(String validCode) {
			this.validCode = validCode;
		}

	}

}
