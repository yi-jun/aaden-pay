package com.aaden.pay.api.comm.model;

import java.math.BigDecimal;

import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.core.model.BaseModel;

/**
 * @Description 第三方认证支付额度
 * @author aaden
 * @date 2017年12月23日
 */
public class ThirdPayQuota extends BaseModel {

	private static final long serialVersionUID = 2386606487391649529L;

	private Long id; //

	private String quotaId; // 主键

	private PayChannel payChannel; // 支付渠道

	private BankType bankType; // 银行编号

	private BigDecimal singleAmount; // 单笔充值限制金额

	private String remark; // 限额文字描述信息

	private BigDecimal feeRate;// 支付渠道费率,非数据库字段,充值路由业务使用

	public final void setId(Long value) {
		this.id = value;
	}

	public final Long getId() {
		return this.id;
	}

	public final void setQuotaId(String value) {
		this.quotaId = value;
	}

	public final String getQuotaId() {
		return this.quotaId;
	}

	public final void setPayChannel(PayChannel value) {
		this.payChannel = value;
	}

	public final PayChannel getPayChannel() {
		return this.payChannel;
	}

	public final String getPayChannelCn() {
		return this.payChannel == null ? "" : this.payChannel.getCnName();
	}

	public final void setBankType(BankType value) {
		this.bankType = value;
	}

	public final BankType getBankType() {
		return this.bankType;
	}

	public final String getBankTypeCn() {
		return this.bankType == null ? "" : this.bankType.getCnName();
	}

	public final void setSingleAmount(BigDecimal value) {
		this.singleAmount = value;
	}

	public final BigDecimal getSingleAmount() {
		return this.singleAmount;
	}

	public final void setRemark(String value) {
		this.remark = value;
	}

	public final String getRemark() {
		return this.remark;
	}

	public void initPrimaryKey() {
		this.setQuotaId(randomUUID());
	}

	public BigDecimal getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}

}
