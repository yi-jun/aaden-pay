package com.aaden.pay.api.biz.vo;

import java.io.Serializable;

import com.aaden.pay.api.biz.vo.data.PayCashData;
import com.aaden.pay.api.biz.vo.data.PayMustData;
import com.aaden.pay.api.biz.vo.data.PayRechargeData;
import com.aaden.pay.api.biz.vo.data.PaySysData;

/**
 * @Description 支付请求数据
 * @author aaden
 * @date 2017年12月25日
 */
public class PayRequest implements Serializable {

	private static final long serialVersionUID = -8557764868964112564L;

	// 必填数据
	private PayMustData must = new PayMustData();

	// 充值选填数据
	private PayRechargeData recharge = new PayRechargeData();

	// 提现选填数据
	private PayCashData cash = new PayCashData();

	// 系统使用参数, 不需传递
	private PaySysData sys = new PaySysData();

	public PayMustData getMust() {
		return must;
	}

	public PayRechargeData getRecharge() {
		return recharge;
	}

	public PayCashData getCash() {
		return cash;
	}

	public PaySysData getSys() {
		return sys;
	}

}
