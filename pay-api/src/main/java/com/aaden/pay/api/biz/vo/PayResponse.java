package com.aaden.pay.api.biz.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.aaden.pay.api.comm.enums.TradeStatus;

/**
 *  @Description 支付响应
 *  @author aaden
 *  @date 2017年12月21日
 */
public class PayResponse implements Serializable {

	private static final long serialVersionUID = 5467142708934373648L;

	private String serialnumber; // 交易流水号,代付可能会返回以,分割的多个流水号,参考getSerialnumberArray方法

	private TradeStatus tradeStatus;// 支付状态

	private BigDecimal amount; // 金额

	private BigDecimal costs;// 支付公司收取的手续费

	private String msg;// 第三方支付返回的错误信息

	private String postUrl;// 提交地址,网银类型充值返回
	private Map<String, String> postMap;// 提交表单,网银类型充值返回

	public PayResponse() {

	}

	public static PayResponse getFailInstall(String msg) {
		PayResponse r = new PayResponse();
		r.setTradeStatus(TradeStatus.FAILURE);
		r.setMsg(msg);
		return r;
	}

	public String getSerialnumber() {
		return serialnumber;
	}

	public String[] getSerialnumberArray() {
		return this.serialnumber == null ? null : this.serialnumber.split(",");
	}

	public String getPostUrl() {
		return postUrl;
	}

	public void setPostUrl(String postUrl) {
		this.postUrl = postUrl;
	}

	public Map<String, String> getPostMap() {
		return postMap;
	}

	public void setPostMap(Map<String, String> postMap) {
		this.postMap = postMap;
	}

	public void setSerialnumber(String serialnumber) {
		this.serialnumber = serialnumber;
	}

	public TradeStatus getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(TradeStatus tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getCosts() {
		return costs;
	}

	public void setCosts(BigDecimal costs) {
		this.costs = costs;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isSuccessed() {
		return TradeStatus.SUCCEED == this.getTradeStatus();
	}

	public boolean isNotSure() {
		return !this.isFailure() && !this.isSuccessed();
	}

	public boolean isFailure() {
		return TradeStatus.FAILURE == this.getTradeStatus() || TradeStatus.OVERDUE == this.getTradeStatus();
	}

}
