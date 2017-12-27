package com.aaden.pay.api.biz.vo.data;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import com.aaden.pay.api.biz.validation.DecimalScale;
import com.aaden.pay.api.comm.enums.PayChannel;

/**
 * 代付选填参数
 */
public class PayCashData implements Serializable {
	private static final long serialVersionUID = -5088931332493583471L;

	/**
	 * 判断重复支付依据为: 本次系统订单号,(当前支付成功金额+处理中的金额+amount) <= orderTotalAmount,则可以支付
	 */
	@NotNull(message = "")
	@DecimalMin(value = "0.01", message = "提现订单总金额不能为空")
	@DecimalScale(value = 2, message = "提现订单总金额只能有两位小数")
	private BigDecimal orderTotalAmount;// 该订单总金额,

	@NotNull(message = "开户支行名称不能为空")
	private String branchName;// 开户行支行名称

	@NotNull(message = "开户支行城市不能为空")
	private String bankCity;// 开户行城市的编码

	@NotNull(message = "开户支行省份不能为空")
	private String bankProv;// 开户行省份编码

	private String mobile;// 预留手机号码(宝付代付选填)
	private String idno;// 身份证号码(宝付代付选填)
	private String payRemark; // 代付银行流水备注(部分银行支持)
	private PayChannel payChannel; // 支付渠道

	public BigDecimal getOrderTotalAmount() {
		return orderTotalAmount;
	}

	public PayCashData setOrderTotalAmount(BigDecimal orderTotalAmount) {
		this.orderTotalAmount = orderTotalAmount;
		return this;
	}

	public String getPayRemark() {
		return payRemark;
	}

	public PayCashData setPayRemark(String payRemark) {
		this.payRemark = payRemark;
		return this;
	}

	public String getBranchName() {
		return branchName;
	}

	public PayCashData setBranchName(String branchName) {
		this.branchName = branchName;
		return this;
	}

	public String getBankCity() {
		return bankCity;
	}

	public PayCashData setBankCity(String bankCity) {
		this.bankCity = bankCity;
		return this;
	}

	public String getBankProv() {
		return bankProv;
	}

	public PayCashData setBankProv(String bankProv) {
		this.bankProv = bankProv;
		return this;
	}

	public String getMobile() {
		return mobile;
	}

	public PayCashData setMobile(String mobile) {
		this.mobile = mobile;
		return this;
	}

	public String getIdno() {
		return idno;
	}

	public PayCashData setIdno(String idno) {
		this.idno = idno;
		return this;
	}

	public PayChannel getPayChannel() {
		return payChannel;
	}

	public PayCashData setPayChannel(PayChannel payChannel) {
		this.payChannel = payChannel;
		return this;
	}

}