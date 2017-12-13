package com.aaden.pay.service.biz.route;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aaden.pay.api.biz.vo.BankRequest;
import com.aaden.pay.core.utils.ClassUtil;
import com.aaden.pay.core.utils.SpringContextHelper;
import com.aaden.pay.service.biz.annotation.ChannelValue;
import com.aaden.pay.service.biz.tp.ThirdBankVerifyService;

/**
 *  @Description 第三方绑卡签约适配
 *  @author aaden
 *  @date 2017年12月26日
 */
@Service
public class ThirdBankRoute {

	private static List<Class<?>> thirdClassList = ClassUtil.getAllSubClass(ThirdBankVerifyService.class);

	public ThirdBankVerifyService route(BankRequest request) throws Exception {
		if (request == null || request.getInfo().getChannel() == null) {
			throw new Exception("当前暂不支持的第三方支付渠道");
		}

		if (thirdClassList == null)
			throw new Exception("当前暂不支持的第三方支付渠道");

		for (Class<?> clz : thirdClassList) {
			ChannelValue comment = clz.getAnnotation(ChannelValue.class);
			if (comment == null)
				continue;
			if (comment.channel() != request.getInfo().getChannel())
				continue;

			Service service = clz.getAnnotation(Service.class);
			if (service == null)
				continue;

			return (ThirdBankVerifyService) SpringContextHelper.getBean(service.value());
		}

		throw new Exception("当前暂不支持的第三方支付渠道");
	}

}
