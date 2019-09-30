package com.aaden.pay.service.biz.route;

import com.aaden.pay.api.biz.vo.BankRequest;
import com.aaden.pay.service.biz.annotation.ChannelValue;
import com.aaden.pay.service.biz.tp.ThirdBankVerifyService;
import org.springframework.stereotype.Service;

/**
 *  @Description 第三方绑卡签约适配
 *  @author aaden
 *  @date 2017年12月26日
 */
@Service
public class ThirdBankRoute extends AbstractRoute<ThirdBankVerifyService> {

	public ThirdBankVerifyService route(BankRequest request) throws Exception {
		if (request == null || request.getInfo().getChannel() == null) {
			throw new Exception("当前暂不支持的第三方支付渠道");
		}

		if (thirdBanks == null)
			throw new Exception("当前暂不支持的第三方支付渠道");

		for (ThirdBankVerifyService bean : thirdBanks) {
			ChannelValue comment = bean.getClass().getAnnotation(ChannelValue.class);
			if (comment == null)
				continue;
			if (comment.channel() != request.getInfo().getChannel())
				continue;

			Service service = bean.getClass().getAnnotation(Service.class);
			if (service == null)
				continue;

			return bean;
		}

		throw new Exception("当前暂不支持的第三方支付渠道");
	}

	@Override
	Class<ThirdBankVerifyService> getClazz() {
		return ThirdBankVerifyService.class;
	}
}
