package com.aaden.pay.service.biz.tp;

import com.aaden.pay.api.biz.vo.BankRequest;
import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.service.biz.vo.ThirdBankResponse;

/**
 *  @Description 第三方支付绑卡签约接口
 *  @author aaden
 *  @date 2017年12月26日
 */
public interface ThirdBankVerifyService {

	/**
	 * 第一步:验证银行卡信息
	 */
	public ThirdBankResponse bindApply(BankRequest request, ThirdBankSend bankSend);

	/**
	 * 第二步:验证预留手机验证码
	 */
	public ThirdBankResponse bindConfirm(BankRequest request, ThirdBankSend bankSend, String preReq);
}
