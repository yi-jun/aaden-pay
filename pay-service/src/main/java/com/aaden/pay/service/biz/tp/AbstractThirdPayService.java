package com.aaden.pay.service.biz.tp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.comm.enums.TradeStatus;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.api.comm.model.ThirdPayValidcode;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.service.biz.vo.ThirdPayResponse;

/**
 * @Description 第三方支付的默认实现
 * @author aaden
 * @date 2017年12月19日
 */
public abstract class AbstractThirdPayService implements ThirdPayService {

	protected SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	@Override
	public ThirdPayResponse netsave(ThirdPayRecord tr) {
		return this.buildNotSupportResponse();
	}

	@Override
	public ThirdPayResponse callback(Map<String, String> returnParameter) {
		return this.buildNotSupportResponse();
	}

	@Override
	public ThirdPayResponse recharge(ThirdPayRecord tr, PayRequest payRequest) {
		return this.buildNotSupportResponse();
	}

	@Override
	public ThirdPayResponse pay(ThirdPayRecord tr, PayRequest payRequest) {
		return this.buildNotSupportResponse();
	}

	@Override
	public ThirdPayResponse queryTrade(ThirdPayRecord tr) {
		return this.buildNotSupportResponse();
	}

	@Override
	public List<ThirdPayResponse> queryTrade(Date checkDate) {
		return new ArrayList<ThirdPayResponse>();
	}

	@Override
	public BigDecimal getRechargeFeeRate() {
		return BigDecimal.ZERO;
	}

	/**
	 * 此处可选用内部的短信渠道发送短信,若支付API要求支付公司发送短信,覆盖此方法即可
	 */
	@Override
	public ThirdPayResponse rechargeSmsCode(PayRequest payRequest, ThirdPayValidcode valid) {
		logger.debug("-------------内部短信发送成功------------");
		ThirdPayResponse response = new ThirdPayResponse();
		response.setTradeStatus(TradeStatus.SUCCEED);
		response.setPayCode("success");
		response.setPayMessage("系统内部短信验证码发送成功");
		response.setSerialnumber(valid.getSerialnumber());
		return response;

	}

	private ThirdPayResponse buildNotSupportResponse() {
		ThirdPayResponse re = new ThirdPayResponse();
		re.setTradeStatus(TradeStatus.FAILURE);
		re.setPayMessage(" channel does not support this feature ");
		return re;
	}

}
