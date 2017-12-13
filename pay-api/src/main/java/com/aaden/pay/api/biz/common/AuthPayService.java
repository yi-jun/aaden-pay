package com.aaden.pay.api.biz.common;

import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.biz.vo.PayResponse;

/**
 *  @Description 快捷支付接口
 *  @author aaden
 *  @date 2017年12月6日
 */
public interface AuthPayService extends BasePayService {
	/**
	 * 实时快捷充值
	 */
	public PayResponse recharge(PayRequest payRequest);

	/**
	 * 发送充值验证码(可能有,视渠道而定)
	 */
	public PayResponse rechargeSmsCode(PayRequest payRequest);
}
