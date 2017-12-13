package com.aaden.pay.api;
import com.aaden.pay.api.biz.common.AuthPayService;
import com.aaden.pay.api.biz.common.FoPayService;
import com.aaden.pay.api.biz.common.OnlinePayService;

/**
 *  @Description 支付统一服务接口
 *  @author aaden
 *  @date 2017年12月27日
 */
public interface PaymentService extends OnlinePayService, AuthPayService, FoPayService {

}
