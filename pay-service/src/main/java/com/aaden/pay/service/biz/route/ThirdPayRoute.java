package com.aaden.pay.service.biz.route;

import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.PayType;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.core.contants.ErrorMsgConstant;
import com.aaden.pay.core.utils.SpringContextHelper;
import com.aaden.pay.service.biz.annotation.ChannelValue;
import com.aaden.pay.service.biz.tp.ThirdPayService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

/**
 * @Description 第三方支付路由
 * @author aaden
 * @date 2017年12月27日
 */
@Service
public class ThirdPayRoute extends AbstractRoute<ThirdPayService> {

	public ThirdPayService route(PayRequest request) throws Exception {
		return this.route(request.getSys().getPayChannel(), request.getSys().getPayType());
	}

	public ThirdPayService route(ThirdPayRecord tr) throws Exception {
		return this.route(tr.getPayChannel(), tr.getPayType());
	}

	public ThirdPayService route(PayChannel channel, PayType payType) throws Exception {
		if (thirdBanks == null)
			throw new Exception(ErrorMsgConstant.CHANNEL_NOT_SUPPORT);

		for (ThirdPayService bean : thirdBanks) {
			ChannelValue comment = bean.getClass().getAnnotation(ChannelValue.class);
			if (comment == null)
				continue;
			if (comment.channel() != channel)
				continue;

			if (!ArrayUtils.contains(comment.payType(), payType))
				continue;

			Service service = bean.getClass().getAnnotation(Service.class);
			if (service == null)
				continue;

			return (ThirdPayService) SpringContextHelper.getBean(service.value());
		}

		throw new Exception(ErrorMsgConstant.CHANNEL_NOT_SUPPORT);

	}

	@Override
	Class<ThirdPayService> getClazz() {
		return ThirdPayService.class;
	}
}
