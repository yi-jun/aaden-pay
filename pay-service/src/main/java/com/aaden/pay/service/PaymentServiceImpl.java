package com.aaden.pay.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.DbPayService;
import com.aaden.pay.api.PaymentService;
import com.aaden.pay.api.biz.constants.PaymentConstans;
import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.biz.vo.PayResponse;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.PayType;
import com.aaden.pay.api.comm.enums.TradeStatus;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.api.comm.model.ThirdPayValidcode;
import com.aaden.pay.core.contants.ErrorMsgConstant;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.orm.exception.DataBaseAccessException;
import com.aaden.pay.core.utils.BigDecimalUtils;
import com.aaden.pay.service.biz.ext.PaymentTransaction;
import com.aaden.pay.service.biz.ext.PaymentValidate;
import com.aaden.pay.service.biz.tp.ThirdPayService;
import com.aaden.pay.service.biz.util.CloneUtils;
import com.aaden.pay.service.biz.util.PayMessageUtils;
import com.aaden.pay.service.biz.vo.ThirdPayResponse;

/**
 *  @Description 支付业务实现
 *  @author aaden
 *  @date 2017年12月19日
 */
@Service("paymentService")
public class PaymentServiceImpl implements PaymentService {

	SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	@Autowired
	PaymentValidate paymentValidate;
	@Autowired
	PaymentTransaction paymentTransaction;
	@Autowired
	DbPayService dbPayService;

	@Override
	public PayResponse netsave(PayRequest payRequest) {
		payRequest.getSys().setPayType(PayType.GATEWAY);// 设定支付类型

		// 校验数据
		ThirdPayService service = null;
		try {
			service = paymentValidate.validateRecharge(payRequest);
		} catch (Exception e) {
			return PayResponse.getFailInstall(e.getMessage());
		}

		// 保存支付记录
		ThirdPayRecord payRecord = null;
		try {
			payRecord = paymentTransaction.initSavePayRecord(payRequest, null);
		} catch (DataBaseAccessException e) {
			return PayResponse.getFailInstall(ErrorMsgConstant.ERR_OPERATE_DATABASE);
		}

		// 发送第三方,解析结果
		ThirdPayResponse tpResp = service.netsave(CloneUtils.clone(payRecord));
		PayResponse payResponse = this.buildResponse(payRecord);
		payResponse.setPostUrl(tpResp.getPostUrl());
		payResponse.setPostMap(tpResp.getPostMap());
		// 网银返回成功
		payResponse.setTradeStatus(TradeStatus.SUCCEED);
		return payResponse;
	}

	// 重新查询结果
	@Override
	public PayResponse recheck(String serialNumber) {
		// 校验数据
		ThirdPayRecord payRecord = StringUtils.isBlank(serialNumber) ? null : dbPayService.getBySerialnumber(serialNumber);
		ThirdPayService service = null;
		try {
			service = paymentValidate.validateQuery(payRecord);
		} catch (Exception e) {
			return PayResponse.getFailInstall(e.getMessage());
		}

		// 发送第三方支付公司,解析结果
		ThirdPayResponse tpResp = service.queryTrade(CloneUtils.clone(payRecord));
		paymentTransaction.parseUpdatePayRecord(tpResp, payRecord);

		return this.buildResponse(payRecord);
	}

	// 充值 代扣业务
	@Override
	public PayResponse recharge(PayRequest payRequest) {
		payRequest.getSys().setPayType(PayType.AUTHPAY);

		// 验证码数据
		ThirdPayService service = null;
		try {
			service = paymentValidate.validateRecharge(payRequest);
		} catch (Exception e) {
			return PayResponse.getFailInstall(e.getMessage());
		}

		// 保存支付记录
		ThirdPayRecord payRecord = null;
		try {
			payRecord = paymentTransaction.doRechargeRecord(payRequest);
		} catch (DataBaseAccessException e) {
			return PayResponse.getFailInstall(e.getMessage());
		}

		// 发送第三方,解析结果
		ThirdPayResponse tpResp = service.recharge(CloneUtils.clone(payRecord), payRequest);
		paymentTransaction.parseUpdatePayRecord(tpResp, payRecord);
		return this.buildResponse(payRecord);
	}

	// 代付业务
	@Override
	public PayResponse cash(PayRequest payRequest) {
		payRequest.getSys().setPayType(PayType.PAYOUT);

		// 校验数据
		ThirdPayService service = null;
		try {
			service = paymentValidate.validateCash(payRequest);
		} catch (Exception e) {
			return PayResponse.getFailInstall(e.getMessage());
		}

		// 单笔限额
		BigDecimal singleLimit = PaymentConstans.getPayoutLimit(payRequest.getSys().getPayChannel());
		BigDecimal totalAmt = payRequest.getMust().getAmount();
		List<BigDecimal> list = BigDecimalUtils.split(singleLimit, totalAmt);

		List<ThirdPayRecord> payRecords = new ArrayList<>();
		for (BigDecimal bigDecimal : list) {
			// 设置为单笔金额
			payRequest.getMust().setAmount(bigDecimal);
			// 保存支付记录
			ThirdPayRecord payRecord = null;
			try {
				payRecord = paymentTransaction.initSavePayRecord(payRequest, null);
			} catch (DataBaseAccessException e) {
				return PayResponse.getFailInstall(ErrorMsgConstant.ERR_OPERATE_DATABASE);
			}
			// 发送第三方,解析结果
			ThirdPayResponse tpResp = service.pay(CloneUtils.clone(payRecord), payRequest);
			paymentTransaction.parseUpdatePayRecord(tpResp, payRecord);
			payRecords.add(payRecord);
		}

		// 单笔解析
		if (payRecords.size() == 1) {
			return this.buildResponse(payRecords.get(0));
		}

		return buildResponse(payRecords);
	}

	@Override
	public PayResponse callback(Map<String, String> returnParameter, PayChannel payChannel) {
		// 校验数据
		ThirdPayService service = null;
		try {
			service = paymentValidate.validateNetsaveCallback(returnParameter, payChannel);
		} catch (Exception e) {
			return PayResponse.getFailInstall(e.getMessage());
		}

		// 解析结果
		ThirdPayResponse tpResp = service.callback(returnParameter);
		ThirdPayRecord payRecord = dbPayService.getBySerialnumber(tpResp.getSerialnumber());
		if (payRecord == null) {
			logger.warn("callback serial number has been missing.");
			return PayResponse.getFailInstall("订单不存在");
		}
		if (!payRecord.isSuccess()) {// 已经成功,不在解析
			paymentTransaction.parseUpdatePayRecord(tpResp, payRecord);
		}
		return this.buildResponse(payRecord);
	}

	@Override
	public PayResponse rechargeSmsCode(PayRequest payRequest) {

		payRequest.getSys().setPayType(PayType.AUTHPAY);

		ThirdPayService service = null;
		try {
			service = paymentValidate.validateRecharge(payRequest);
		} catch (Exception e) {
			return PayResponse.getFailInstall(e.getMessage());
		}

		ThirdPayValidcode valid = null;
		try {
			valid = paymentTransaction.initSaveValidcode(payRequest);
		} catch (Exception e) {
			return PayResponse.getFailInstall(e.getMessage());
		}

		ThirdPayResponse resp = service.rechargeSmsCode(payRequest, CloneUtils.clone(valid));
		paymentTransaction.parseUpdateValidcode(valid, resp);

		return this.buildResponse(valid);
	}

	// 充值验证码返回
	private PayResponse buildResponse(ThirdPayValidcode valid) {
		PayResponse payResponse = new PayResponse();
		payResponse.setSerialnumber(valid.getSerialnumber());
		payResponse.setTradeStatus(valid.getTradeStatus());
		payResponse.setMsg(valid.getRetMsg());
		payResponse.setAmount(valid.getAmount());
		return payResponse;
	}

	// 单笔支付返回
	private PayResponse buildResponse(ThirdPayRecord payRecord) {
		PayResponse payResponse = new PayResponse();

		payResponse.setCosts(payRecord.getCosts());
		payResponse.setSerialnumber(payRecord.getSerialnumber());
		payResponse.setTradeStatus(payRecord.getTradeStatus());
		payResponse.setMsg(PayMessageUtils.getFriendlyMsg(payRecord));
		payResponse.setAmount(payRecord.getOrderAmount());
		return payResponse;
	}

	// 批量交易返回
	private PayResponse buildResponse(List<ThirdPayRecord> payRecords) {
		// 支付流水号,以,号分割
		String serial = "";
		// 成功金额,失败金额,支付手续费,订单总金额
		BigDecimal succ = BigDecimal.ZERO, fail = BigDecimal.ZERO, costs = BigDecimal.ZERO, totalAmt = BigDecimal.ZERO;
		for (ThirdPayRecord payRecord : payRecords) {
			totalAmt = totalAmt.add(payRecord.getOrderAmount());
			costs = costs.add(payRecord.getCosts());
			if (payRecord.isSuccess()) {
				succ = succ.add(payRecord.getOrderAmount());
			} else if (payRecord.isFail()) {
				fail = fail.add(payRecord.getOrderAmount());
			}
			serial = serial + payRecord.getSerialnumber() + ",";
		}

		PayResponse payResponse = new PayResponse();
		payResponse.setAmount(totalAmt);
		payResponse.setCosts(costs);
		payResponse.setSerialnumber(serial.substring(0, serial.length() - 1));
		if (succ.compareTo(totalAmt) == 0) {// 全部成功
			payResponse.setTradeStatus(TradeStatus.SUCCEED);
			payResponse.setMsg(TradeStatus.SUCCEED.getName());
		} else if (fail.compareTo(totalAmt) == 0) {// 全部失败
			payResponse.setTradeStatus(TradeStatus.FAILURE);
			payResponse.setMsg(PayMessageUtils.getFriendlyMsg(payRecords.get(0)));
		} else {
			payResponse.setTradeStatus(TradeStatus.RETRY);
			payResponse.setMsg(TradeStatus.RETRY.getName());
		}

		return payResponse;
	}
}
