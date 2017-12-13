package com.aaden.pay.service.biz.tp.baofoo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.PayType;
import com.aaden.pay.api.comm.enums.SendStatus;
import com.aaden.pay.api.comm.enums.TradeStatus;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.api.comm.model.ThirdPayValidcode;
import com.aaden.pay.core.utils.BigDecimalUtils;
import com.aaden.pay.core.utils.CollectionUtils;
import com.aaden.pay.core.utils.DateUtils;
import com.aaden.pay.service.biz.annotation.ChannelValue;
import com.aaden.pay.service.biz.tp.AbstractThirdPayService;
import com.aaden.pay.service.biz.tp.baofoo.adaptor.BaofooAuthAdaptor;
import com.aaden.pay.service.biz.tp.baofoo.vo.BaofooResponse;
import com.aaden.pay.service.biz.vo.ThirdPayResponse;

/**
 * @Description 宝付快捷支付实现
 * @author aaden
 * @date 2017年12月10日
 */
@Service("baofooAuthService")
@ChannelValue(channel = PayChannel.BAOFOO, payType = { PayType.AUTHPAY })
public class BaofooAuthServiceImpl extends AbstractThirdPayService {

	@Autowired
	private BaofooAuthAdaptor adaptor;

	@Override
	public ThirdPayResponse recharge(ThirdPayRecord tr, PayRequest payRequest) {
		BaofooResponse resp = adaptor.sendRecharge(tr, payRequest);

		ThirdPayResponse response = new ThirdPayResponse();
		if (resp == null || resp.isNotSure()) {
			String msg = resp == null ? "系统繁忙,请稍后再试!" : resp.getResp_msg();
			response.setPayMessage(msg);
			response.setTradeStatus(TradeStatus.RETRY);
		} else if (resp.isSuccess()) {
			BigDecimal actAmt = new BigDecimal(resp.getSucc_amt()).divide(BigDecimalUtils.ONE_HUNDRED);// 单位为分
			response.setTradeStatus(TradeStatus.SUCCEED);
			response.setPayCode(resp.getResp_code());
			response.setPayMessage(resp.getResp_msg());
			response.setActAmount(actAmt);
			response.setSettleTime(DateUtils.todayFormat());
		} else {
			response.setPayCode(resp.getResp_code());
			response.setPayMessage(resp.getResp_msg());
			response.setTradeStatus(TradeStatus.FAILURE);
		}
		response.setThirdnumber(resp == null ? null : resp.getBusiness_no());
		response.setSerialnumber(tr.getSerialnumber());
		response.setSendStatus(resp == null ? SendStatus.FAIL : SendStatus.SUCCEED);
		return response;
	}

	@Override
	public ThirdPayResponse queryTrade(ThirdPayRecord tr) {
		BaofooResponse resp = adaptor.sendQuery(tr);
		ThirdPayResponse response = new ThirdPayResponse();
		if (resp == null || resp.isNotSure()) {
			String msg = resp == null ? "系统繁忙,请稍后再试!" : resp.getResp_msg();
			response.setPayMessage(msg);
			response.setTradeStatus(TradeStatus.RETRY);
		} else if (resp.isSuccess()) {
			response.setTradeStatus(TradeStatus.SUCCEED);
			response.setPayCode(resp.getResp_code());
			response.setPayMessage(resp.getResp_msg());
			response.setActAmount(tr.getOrderAmount());
		} else {
			response.setPayCode(resp.getResp_code());
			response.setPayMessage(resp.getResp_msg());
			response.setTradeStatus(TradeStatus.FAILURE);
		}
		response.setSendStatus(resp == null ? SendStatus.FAIL : SendStatus.SUCCEED);
		response.setSerialnumber(tr.getSerialnumber());
		return response;
	}

	@Override
	public List<ThirdPayResponse> queryTrade(Date checkDate) {
		List<ThirdPayResponse> list = new ArrayList<>();

		List<String> files = adaptor.getCheckFile(checkDate);
		if (CollectionUtils.isEmpty(files))
			return list;

		/*
		 * 商户号|终端号|交易类型|交易子类型|总笔数|总金额|总手续费|清算时间
		 * 100000178|0|00104|00|8|6127.37|91.93|2017-05-10 *
		 * 商户号|终端号|交易类型|交易子类型|宝付订单号|商户支付订单号|清算日期|订单状态|交易金额|手续费|宝付交易号|支付订单创建时间|
		 * 商户退款订单号|退款订单创建时间
		 * 100000178|0|00104|00|16868350|TI1494405152546||2017-05-10|1|1.00|0.02
		 * |||16868350|2017-05-10 16:32:19|
		 */
		for (String line : files) {
			if (StringUtils.isBlank(line))
				continue;
			String[] arr = line.split("\\|");
			try {
				Double.parseDouble(arr[9]);
			} catch (Exception e) {
				continue;
			}
			ThirdPayResponse rsp = new ThirdPayResponse();
			String status = arr[3];// 成功 00,退款 01,撤销 02
			if ("00".equals(status)) {
				rsp.setTradeStatus(TradeStatus.SUCCEED);
			} else {
				rsp.setTradeStatus(TradeStatus.FAILURE);
			}

			String sernum = arr[5];
			if (StringUtils.isBlank(sernum))
				continue;
			Date settle = DateUtils.parseAuto(arr[6]);
			if (settle != null)
				settle = DateUtils.parseAuto(DateUtils.formatDate(settle));
			BigDecimal amt = new BigDecimal(arr[8]);
			BigDecimal fee = new BigDecimal(arr[9]);

			rsp.setSettleTime(settle);
			rsp.setSerialnumber(sernum);
			rsp.setActAmount(amt);
			rsp.setFee(fee);
			list.add(rsp);
		}

		return list;
	}

	BigDecimal feeRate = new BigDecimal("0.0007");

	@Override
	public BigDecimal calculationCost(ThirdPayRecord tr) {
		// 认证万7,最低2元
		BigDecimal cost = feeRate.multiply(tr.getOrderAmount());
		cost = cost.setScale(2, BigDecimal.ROUND_HALF_UP);
		return cost.compareTo(BigDecimalUtils.TWO) <= 0 ? BigDecimalUtils.TWO : cost;
	}

	@Override
	public BigDecimal getRechargeFeeRate() {
		return feeRate;
	}

	@Override
	public ThirdPayResponse rechargeSmsCode(PayRequest payRequest, ThirdPayValidcode valid) {
		BaofooResponse resp = adaptor.sendRechargeCode(payRequest, valid);

		ThirdPayResponse response = new ThirdPayResponse();
		if (resp == null || resp.isNotSure()) {
			String msg = resp == null ? "系统繁忙,请稍后再试!" : resp.getResp_msg();
			response.setPayMessage(msg);
			response.setPayMessage(msg);
			response.setTradeStatus(TradeStatus.RETRY);
		} else if (resp.isSuccess()) {
			response.setTradeStatus(TradeStatus.SUCCEED);
			response.setPayCode(resp.getResp_code());
			response.setPayMessage(resp.getResp_msg());
			response.setToken(resp.getBusiness_no());
		} else {
			response.setPayCode(resp.getResp_code());
			response.setPayMessage(resp.getResp_msg());
			response.setPayMessage(resp.getResp_msg());
			response.setTradeStatus(TradeStatus.FAILURE);
		}
		response.setSendStatus(resp == null ? SendStatus.FAIL : SendStatus.SUCCEED);
		response.setSerialnumber(valid.getSerialnumber());
		return response;
	}

}
