package com.aaden.pay.service.biz.tp.allinpay;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.biz.enums.allinpay.AllinPayBankType;
import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.CardProp;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.PayType;
import com.aaden.pay.api.comm.enums.SendStatus;
import com.aaden.pay.api.comm.enums.TradeStatus;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.core.utils.BigDecimalUtils;
import com.aaden.pay.core.utils.DateUtils;
import com.aaden.pay.service.biz.annotation.ChannelValue;
import com.aaden.pay.service.biz.exception.PaymentSignException;
import com.aaden.pay.service.biz.tp.AbstractThirdPayService;
import com.aaden.pay.service.biz.tp.allinpay.adaptor.AllinGatewayAdaptor;
import com.aaden.pay.service.biz.vo.ThirdPayResponse;
import com.alibaba.fastjson.JSON;
import com.allinpay.ets.client.PaymentResult;

/**
 * @Description 通联网关支付交互实现
 * @author aaden
 * @date 2017年12月4日
 */
@Service("allinPayGatewayService")
@ChannelValue(channel = PayChannel.ALLIN, payType = { PayType.GATEWAY })
public class AllinPayGatewayServiceImpl extends AbstractThirdPayService {

	@Autowired
	private AllinGatewayAdaptor adaptor;

	@Override
	public ThirdPayResponse netsave(ThirdPayRecord tr) {
		Map<String, String> postMap = adaptor.builderPostMap(tr);
		logger.info("通联网银请求报文:" + JSON.toJSONString(postMap));
		ThirdPayResponse resp = new ThirdPayResponse();
		resp.setPostUrl(adaptor.getUrl());
		resp.setPostMap(postMap);
		resp.setSendStatus(SendStatus.SENDING);
		return resp;
	}

	/*
	 * 通联网关未成功,不会通知,调用查询接口,返回一段特殊字符串(未成功的,一律返回无此交易).
	 * 所以,只有成功时,才会执行到此,不需要做过期订单判断,由查询接口处判断是否过期,并处理相关业务
	 */
	@Override
	public ThirdPayResponse callback(Map<String, String> returnParameter) {

		if (returnParameter == null || returnParameter.isEmpty()) {
			ThirdPayResponse resp = new ThirdPayResponse();
			resp.setTradeStatus(TradeStatus.RETRY);
			resp.setPayMessage("响应参数为空, 拒绝访问");
			return resp;

		}
		logger.info("通联网关响应报文:" + JSON.toJSONString(returnParameter));

		ThirdPayResponse tpResponse = new ThirdPayResponse();
		try {
			PaymentResult payResult = adaptor.builderPaymentResult(returnParameter);
			tpResponse.setSerialnumber((payResult.getOrderNo()));
			tpResponse.setPayCode(payResult.getPayResult());
			tpResponse.setPayMessage(payResult.getErrorCode());// 交易成功,该值为空
			TradeStatus tradeStatus = adaptor.parseTradeStatus(payResult.getPayResult());
			tpResponse.setTradeStatus(tradeStatus);
			tpResponse.setActAmount(new BigDecimal(payResult.getOrderAmount()).divide(BigDecimalUtils.ONE_HUNDRED)
					.setScale(2, BigDecimal.ROUND_DOWN));// 整数,单位为分
			if (TradeStatus.SUCCEED == tradeStatus) {
				Date finsh = DateUtils.parseAuto(payResult.getPayDatetime());// yyyyMMDDhhmmss
				if (finsh != null) {
					tpResponse.setSettleTime(DateUtils.parseAuto(DateUtils.formatDate(finsh)));// 通联网银按支付完成时间
				}
				tpResponse.setPayMessage("交易成功");
			}

		} catch (PaymentSignException e) {
			ThirdPayResponse resp = new ThirdPayResponse();
			resp.setTradeStatus(TradeStatus.RETRY);
			resp.setPayMessage("签名验证失败");
			return resp;
		}

		return tpResponse;
	}

	/*
	 * 该方法应该在订单处于不确定状态时,判断是否过期,并处理相关业务(将订单设置为失败).
	 * 只有确定支付结果为成功,才去调用netsaveCallback方法,进行业务处理
	 */
	@Override
	public ThirdPayResponse queryTrade(ThirdPayRecord thirdPayRecord) {
		Map<String, String> result = this.adaptor.sendQuery(thirdPayRecord);

		// 通联只有成功后,才能查询到订单,否则ERRORCODE必有值,此处为成功
		if (null == result.get("ERRORCODE")) {
			return this.callback(result);
		}
		ThirdPayResponse tpResponse = new ThirdPayResponse();
		tpResponse.setSendStatus(result.isEmpty() ? SendStatus.FAIL : SendStatus.SUCCEED);
		tpResponse.setSerialnumber(thirdPayRecord.getSerialnumber());
		tpResponse.setPayCode(result.get("ERRORCODE"));
		tpResponse.setPayMessage(result.get("ERRORMSG"));
		// 判断支付订单是否已经过期失效
		boolean isTimeout = adaptor.isTimeOut(thirdPayRecord);
		TradeStatus tradeStatus = isTimeout ? TradeStatus.OVERDUE : TradeStatus.RETRY;
		tpResponse.setTradeStatus(tradeStatus);
		return tpResponse;
	}

	@Override
	public List<ThirdPayResponse> queryTrade(Date date) {
		List<ThirdPayResponse> list = new ArrayList<ThirdPayResponse>();
		// 建立连接
		String fileAsString = adaptor.downloadCheckFile(date);
		if (StringUtils.isBlank(fileAsString)) {
			return list;
		}
		// 第一行为汇总信息：第二行开始为交易明细：
		String arrays[] = fileAsString.split(System.getProperty("line.separator"));
		for (String line : arrays) {
			// 交易类型|结算日期|商户号|交易时间|商户订单号|通联流水号|交易金额|手续费|清算金额|币种|商户原始订单金额(分)
			// ZF|2015-09-22|109127551507002|2015-09-21 23:18:56|....
			String[] trades = line.split("\\|");
			if (trades[0] == null || trades[0].length() > 3) {// 判断是不是汇总信息,明细以ZF开头,汇总为YYYYMMDD开头
				continue;
			}
			ThirdPayResponse rsp = new ThirdPayResponse();
			// rsp.setSettleTime(DateUtils.parseAuto(trades[3]));通联网银按支付完成时间
			rsp.setSerialnumber((trades[4]));
			rsp.setActAmount(new BigDecimal(trades[6]));
			rsp.setFee(new BigDecimal(trades[7]));
			rsp.setTradeStatus(TradeStatus.SUCCEED);
			list.add(rsp);
		}
		return list;
	}

	// 个人网银费用
	final BigDecimal feeRate = new BigDecimal("0.0015");
	// 机构费率(单笔)
	final BigDecimal orgFee = new BigDecimal("10");

	@Override
	public BigDecimal calculationCost(ThirdPayRecord tr) {
		if (CardProp.company == tr.getCardProp())
			return orgFee;
		return tr.getOrderAmount().multiply(feeRate).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	@Override
	public boolean supportBankType(BankType bankType) {
		return AllinPayBankType.parse(bankType) != null;
	}

	@Override
	public BigDecimal getRechargeFeeRate() {
		return feeRate;
	}

}
