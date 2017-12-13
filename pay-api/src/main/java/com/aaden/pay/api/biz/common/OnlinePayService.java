package com.aaden.pay.api.biz.common;

import java.util.Map;

import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.biz.vo.PayResponse;
import com.aaden.pay.api.comm.enums.PayChannel;

/**
 *  @Description 网银在线支付接口
 *  @author aaden
 *  @date 2017年12月24日
 */
public interface OnlinePayService extends BasePayService{

	/**
	 * 网银充值,返回form表单
	 */
	public PayResponse netsave(PayRequest payRequest);

	/**
	 * 网银充值回调
	 */
	public PayResponse callback(Map<String, String> returnParameter, PayChannel payChannel);
}
