package com.aaden.pay.service.biz.tp.allinpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.biz.vo.BankRequest;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.service.biz.annotation.ChannelValue;
import com.aaden.pay.service.biz.tp.ThirdBankVerifyService;
import com.aaden.pay.service.biz.tp.allinpay.adaptor.AllinAuthpayAdaptor;
import com.aaden.pay.service.biz.tp.allinpay.vo.AllinpayAipgResp;
import com.aaden.pay.service.biz.tp.allinpay.vo.RNPARET;
import com.aaden.pay.service.biz.tp.allinpay.vo.RNPCRET;
import com.aaden.pay.service.biz.vo.ThirdBankResponse;

/**
 *  @Description 通联银行卡校验签约
 *  @author aaden
 *  @date 2017年12月12日
 */
@Service("allinBankVerifyService")
@ChannelValue(channel = PayChannel.ALLIN, payType = {})
public class AllinBankVerifyServiceImpl implements ThirdBankVerifyService {

	@Autowired
	private AllinAuthpayAdaptor adaptor;

	@Override
	public ThirdBankResponse bindApply(BankRequest request, ThirdBankSend bankSend) {
		AllinpayAipgResp aipgRsp = adaptor.sendPreBindcard(request, bankSend);

		ThirdBankResponse ret = null;
		String data = aipgRsp == null ? "" : aipgRsp.getReturnData();
		String code = aipgRsp == null ? "no-reply" : aipgRsp.getINFO().getRET_CODE();
		String msg = aipgRsp == null ? "" : aipgRsp.getINFO().getERR_MSG();
		if (adaptor.SUCCESS_LIST.contains(code)) {
			RNPARET ret2 = aipgRsp.getRNPARET();
			code = ret2.getRET_CODE();
			msg = ret2.getERR_MSG();
			if (adaptor.SUCCESS_LIST.contains(code) && "1".equals(ret2.getISSENDSMS())) {
				ret = ThirdBankResponse.getSuccessInstance(code, msg, data);
			} else {
				ret = ThirdBankResponse.getFailInstance(code, msg, data);
			}
		} else {
			ret = ThirdBankResponse.getFailInstance(code, msg, data);
		}

		return ret;

	}

	/** 验卡第二步,校验手机验证码 */
	@Override
	public ThirdBankResponse bindConfirm(BankRequest request, ThirdBankSend bankSend, String preReq) {
		AllinpayAipgResp aipgRsp = this.adaptor.sendConfirmBindcard(request, bankSend, preReq);

		ThirdBankResponse ret = null;
		String data = aipgRsp == null ? "" : aipgRsp.getReturnData();
		String code = aipgRsp == null ? "no-reply" : aipgRsp.getINFO().getRET_CODE();
		String msg = aipgRsp == null ? "" : aipgRsp.getINFO().getERR_MSG();
		if (adaptor.SUCCESS_LIST.contains(code)) {
			RNPCRET ret2 = aipgRsp.getRNPCRET();
			code = ret2.getRET_CODE();
			msg = ret2.getERR_MSG();
			if (adaptor.SUCCESS_LIST.contains(code) && "1".equals(ret2.getISSENDSMS())) {
				ret = ThirdBankResponse.getSuccessInstance(code, msg, data);
			} else {
				ret = ThirdBankResponse.getFailInstance(code, msg, data);
			}
		} else {
			ret = ThirdBankResponse.getFailInstance(code, msg, data);
		}
		return ret;
	}

}
