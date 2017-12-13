package com.aaden.pay.api.biz.common;

import com.aaden.pay.api.biz.vo.PayResponse;

/**
 *  @Description 基础支付接口
 *  @author aaden
 *  @date 2017年12月19日
 */
public interface BasePayService {

	/**
	 * 重查交易结果并更新记录状态
	 */
	public PayResponse recheck(String serialNumber);

}
