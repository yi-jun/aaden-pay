package com.aaden.pay.service.biz.tp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.api.comm.model.ThirdPayValidcode;
import com.aaden.pay.service.biz.vo.ThirdPayResponse;

/**
 * @Description 第三方支付服务接口
 * @author aaden
 * @date 2017年12月29日
 */
public interface ThirdPayService {

	/**
	 * 网银充值操作
	 */
	public ThirdPayResponse netsave(ThirdPayRecord tr);

	/**
	 * 网银回调,或其他回调
	 */
	public ThirdPayResponse callback(Map<String, String> returnParameter);

	/**
	 * 单笔实时代扣操作
	 */
	public ThirdPayResponse recharge(ThirdPayRecord tr, PayRequest payRequest);

	/**
	 * 单笔实时代付操作
	 */
	public ThirdPayResponse pay(ThirdPayRecord tr, PayRequest payRequest);

	/**
	 * 交易结果查询
	 */
	public ThirdPayResponse queryTrade(ThirdPayRecord tr);

	/**
	 * 按天对账查询
	 */
	public List<ThirdPayResponse> queryTrade(Date checkDate);

	/**
	 * 发送充值验证码
	 */
	public ThirdPayResponse rechargeSmsCode(PayRequest payRequest, ThirdPayValidcode valid);

	/**
	 * 获取充值手续费费率, 用于充值路由,参考rechargeRoute.java
	 */
	public BigDecimal getRechargeFeeRate();

	/**
	 * 计算手续费
	 */
	public BigDecimal calculationCost(ThirdPayRecord tr);

	/**
	 * 是否支持银行卡
	 */
	public boolean supportBankType(BankType bankType);

	/**
	 * 退票
	 */
	// public ThirdPayResponse refund(ThirdPayRecord tr) throws
	// PaymentException;
}
