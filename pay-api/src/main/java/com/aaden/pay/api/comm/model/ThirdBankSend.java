package com.aaden.pay.api.comm.model;

import java.util.Date;

import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.BankVerifyType;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.core.eumus.SourceType;
import com.aaden.pay.core.model.BaseModel;

/**
 *  @Description 银行卡验证签约记录
 *  @author aaden
 *  @date 2017年12月18日
 */
public class ThirdBankSend extends BaseModel {

	private static final long serialVersionUID = -3217708161601722780L;

	private Long id; //

	private String sendId; //

	private String req; // 流水号

	private String userId; //

	private String userLoginName;// 冗余登录名

	private String realName; // 真实姓名

	private String idNo; // 身份证

	private BankType bankType; // 银行编码

	private String cardNo; // 银行卡号

	private String mobile; // 银行预留手机号码

	private String returnCode; // 响应代码

	private String returnMsg; // 验证结果

	private String isValid; // 验证是否通过

	private Date sendTime; // 验证时间

	private PayChannel channel; // 验证渠道

	private String sourceType;// 来源（网站、微站，未来会有APP）

	private BankVerifyType bankVerifyType;//

	private String remark; // 备注

	private String startTime;
	private String endTime;

	public final void setId(Long value) {
		this.id = value;
	}

	public final Long getId() {
		return this.id;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getSourceName() {
		return SourceType.getNameByValue(sourceType);
	}

	public String getUserLoginName() {
		return userLoginName;
	}

	public void setUserLoginName(String userLoginName) {
		this.userLoginName = userLoginName;
	}

	public final void setSendId(String value) {
		this.sendId = value;
	}

	public final String getSendId() {
		return this.sendId;
	}

	public final void setReq(String value) {
		this.req = value;
	}

	public final String getReq() {
		return this.req;
	}

	public final void setUserId(String value) {
		this.userId = value;
	}

	public final String getUserId() {
		return this.userId;
	}

	public final void setRealName(String value) {
		this.realName = value;
	}

	public final String getRealName() {
		return this.realName;
	}

	public final void setIdNo(String value) {
		this.idNo = value;
	}

	public final String getIdNo() {
		return this.idNo;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public final void setMobile(String value) {
		this.mobile = value;
	}

	public final String getMobile() {
		return this.mobile;
	}

	public final void setReturnCode(String value) {
		this.returnCode = value;
	}

	public final String getReturnCode() {
		return this.returnCode;
	}

	public final void setReturnMsg(String value) {
		this.returnMsg = value;
	}

	public final String getReturnMsg() {
		return this.returnMsg;
	}

	public final void setIsValid(String value) {
		this.isValid = value;
	}

	public final String getIsValid() {
		return this.isValid;
	}

	public final void setSendTime(Date value) {
		this.sendTime = value;
	}

	public final Date getSendTime() {
		return this.sendTime;
	}

	public final void setChannel(PayChannel value) {
		this.channel = value;
	}

	public final PayChannel getChannel() {
		return this.channel;
	}

	public final String getChannelCn() {
		return this.channel == null ? "" : this.channel.getCnName();
	}

	public final void setRemark(String value) {
		this.remark = value;
	}

	public final String getRemark() {
		return this.remark;
	}

	public void initPrimaryKey() {
		this.setSendId(this.randomUUID());
	}

	public BankType getBankType() {
		return bankType;
	}

	public String getBankTypeCn() {
		return bankType == null ? "" : bankType.getCnName();
	}

	public void setBankType(BankType bankType) {
		this.bankType = bankType;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public BankVerifyType getBankVerifyType() {
		return bankVerifyType;
	}

	public String getBankVerifyTypeCn() {
		return bankVerifyType == null ? "" : bankVerifyType.getCnName();
	}

	public void setBankVerifyType(BankVerifyType bankVerifyType) {
		this.bankVerifyType = bankVerifyType;
	}

}
