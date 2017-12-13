package com.aaden.pay.service.biz.tp.baofoo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.biz.vo.BankRequest;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.service.biz.annotation.ChannelValue;
import com.aaden.pay.service.biz.tp.ThirdBankVerifyService;
import com.aaden.pay.service.biz.tp.baofoo.adaptor.BaofooAuthAdaptor;
import com.aaden.pay.service.biz.tp.baofoo.vo.BaofooResponse;
import com.aaden.pay.service.biz.vo.ThirdBankResponse;

/**
 *  @Description 宝付验卡签约
 *  @author aaden
 *  @date 2017年12月8日
 */
@Service("baofooBankVerifyService")
@ChannelValue(channel = PayChannel.BAOFOO, payType = {})
public class BaofooBankVerifyServiceImpl implements ThirdBankVerifyService {

	@Autowired
	private BaofooAuthAdaptor adaptor;

	@Override
	public ThirdBankResponse bindApply(BankRequest request, ThirdBankSend bankSend) {
		BaofooResponse resp = adaptor.sendPreBindcard(bankSend);

		ThirdBankResponse ret = null;
		String code = resp == null ? "no-reply" : resp.getResp_code();
		String msg = resp == null ? "" : resp.getResp_msg();
		String data = resp == null ? "" : resp.getReturnData();
		if (resp != null && resp.isSuccess()) {
			ret = ThirdBankResponse.getSuccessInstance(code, msg, data);
		} else {
			ret = ThirdBankResponse.getFailInstance(code, msg, data);
		}
		return ret;
	}

	/** 验卡第二步,校验手机验证码 */
	@Override
	public ThirdBankResponse bindConfirm(BankRequest request, ThirdBankSend bankSend, String preReq) {
		BaofooResponse resp = adaptor.sendConfirmBindcard(request, bankSend, preReq);

		ThirdBankResponse ret = null;
		String code = resp == null ? "no-reply" : resp.getResp_code();
		String msg = resp == null ? "" : resp.getResp_msg();
		String data = resp == null ? "" : resp.getReturnData();
		if (resp != null && resp.isSuccess()) {
			ret = ThirdBankResponse.getSuccessInstance(code, msg, data);
			ret.setAgreeNo(resp.getBind_id());
		} else {
			ret = ThirdBankResponse.getFailInstance(code, msg, data);
		}
		return ret;
	}

}
