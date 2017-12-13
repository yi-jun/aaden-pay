package com.aaden.pay.api.biz.common;

import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.biz.vo.PayResponse;

/**
 *  @Description 代付接口
 *  @author aaden
 *  @date 2017年12月25日
 */
public interface FoPayService extends BasePayService{

	public PayResponse cash(PayRequest payRequest);
}
