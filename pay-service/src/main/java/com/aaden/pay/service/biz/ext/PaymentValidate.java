package com.aaden.pay.service.biz.ext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.DbPayService;
import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.biz.vo.data.PayCashData;
import com.aaden.pay.api.biz.vo.data.PayMustData;
import com.aaden.pay.api.biz.vo.data.PayRechargeData;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.PayType;
import com.aaden.pay.api.comm.model.ThirdPayQuota;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.core.contants.ErrorMsgConstant;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.utils.BigDecimalUtils;
import com.aaden.pay.core.utils.CollectionUtils;
import com.aaden.pay.service.biz.exception.PaymentException;
import com.aaden.pay.service.biz.route.RechargeRoute;
import com.aaden.pay.service.biz.route.ThirdPayRoute;
import com.aaden.pay.service.biz.tp.ThirdPayService;

/**
 * @Description 支付验证数据
 * @author aaden
 * @date 2017年12月21日
 */
@Service
public class PaymentValidate {
	SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	@Autowired
	private RechargeRoute rechargeRoute;
	@Autowired
	ThirdPayRoute thirdPayRoute;
	@Autowired
	private DbPayService dbPayService;

	/**
	 * 网银回调
	 */
	public ThirdPayService validateNetsaveCallback(Map<String, String> returnParameter, PayChannel payChannel) throws Exception {
		if (CollectionUtils.isEmpty(returnParameter)) {
			throw new Exception("支付回调参数为空");
		}
		return thirdPayRoute.route(payChannel, PayType.GATEWAY);
	}

	/**
	 * 对账
	 */
	public ThirdPayService validateQuery(ThirdPayRecord record) throws Exception {
		if (record == null) {
			throw new Exception("该笔订单数据不存在");
		}
		if (record.isSuccess()) {
			throw new Exception("该笔订单已成功,无需对账");
		}
		return thirdPayRoute.route(record);
	}

	/**
	 * 提现
	 */
	public ThirdPayService validateCash(PayRequest request) throws Exception {
		// 参数校验
		this.validMust(request);
		this.validCash(request);

		// 业务校验
		this.checkRepeat(request);// 验证重复提交
		this.fillChannel(request);// 填充支付渠道(若未指定)

		ThirdPayService service = thirdPayRoute.route(request);// 路由
		this.checkBankType(request, service);// 验证该渠道是否支持该银行卡
		return service;
	}

	/**
	 * 充值
	 */
	public ThirdPayService validateRecharge(PayRequest request) throws Exception {
		// 参数校验
		this.validMust(request);
		this.validRecharge(request);

		// 业务校验
		this.checkRepeat(request);// 验证重复提交
		ThirdPayQuota quota = this.fillChannel(request);// 填充支付渠道(若未指定)
		this.checkSingleAmount(request, quota);// 验证单笔充值额度

		ThirdPayService service = thirdPayRoute.route(request);// 路由
		this.checkBankType(request, service);// 验证该渠道是否支持该银行卡
		return service;
	}

	private void checkSingleAmount(PayRequest request, ThirdPayQuota quota) throws Exception {
		if (quota == null)
			quota = dbPayService.getPayQuota(request.getSys().getPayChannel(), request.getMust().getBankType());
		// 单笔限额判断
		if (quota != null && request.getMust().getAmount().compareTo(quota.getSingleAmount()) > 0) {
			throw new Exception(String.format("[%s]单笔充值额度为:%s", request.getMust().getBankType().getCnName(), BigDecimalUtils.format(quota.getSingleAmount())));
		}
	}

	private void validMust(PayRequest request) throws Exception {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		validator.validate(request.getMust());
		Set<ConstraintViolation<PayMustData>> validators = validator.validate(request.getMust());
		for (ConstraintViolation<PayMustData> constraintViolation : validators) {
			throw new Exception(constraintViolation.getMessage());
		}
	}

	private void validCash(PayRequest request) throws Exception {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		validator.validate(request.getCash());
		Set<ConstraintViolation<PayCashData>> validators = validator.validate(request.getCash());
		for (ConstraintViolation<PayCashData> constraintViolation : validators) {
			throw new Exception(constraintViolation.getMessage());
		}
	}

	private void validRecharge(PayRequest request) throws Exception {
		if (request.getSys().getPayType() == PayType.GATEWAY)
			return;
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		validator.validate(request.getRecharge());
		Set<ConstraintViolation<PayRechargeData>> validators = validator.validate(request.getRecharge());
		for (ConstraintViolation<PayRechargeData> constraintViolation : validators) {
			throw new Exception(constraintViolation.getMessage());
		}
	}

	private void checkRepeat(PayRequest request) throws PaymentException, Exception {
		// 提现判断总金额
		if (request.getSys().getPayType() == PayType.PAYOUT) {
			BigDecimal totalAmount = request.getCash().getOrderTotalAmount();
			BigDecimal hasAmount = dbPayService.getSuccessAmount(request.getMust().getOrderCode());
			// 当前支付金额 + 历史成功金额 >= 订单总金额, 拒绝支付
			hasAmount = hasAmount.add(request.getMust().getAmount());
			if (hasAmount.compareTo(totalAmount) > 0) {// 支付金额超出
				String remaidStr = BigDecimalUtils.format(hasAmount.subtract(totalAmount));
				throw new PaymentException(String.format("支付金额超出,当前剩余可支付金额为[%s]", remaidStr));
			}
		} else {// 充值判断是否已经存在
			List<ThirdPayRecord> list = dbPayService.getListByOrderNo(request.getMust().getOrderCode());
			if (CollectionUtils.isNotEmpty(list)) {
				throw new Exception("订单已支付,请勿重复提交");
			}
		}
	}

	private void checkBankType(PayRequest request, ThirdPayService service) throws Exception {
		boolean support = service.supportBankType(request.getMust().getBankType());
		if (!support) {
			throw new Exception(ErrorMsgConstant.BANK_TYPE_NOT_SUPPORT);
		}
	}

	private ThirdPayQuota fillChannel(PayRequest request) throws Exception {
		PayChannel payChannel = null;
		if (request.getSys().getPayType() == PayType.PAYOUT) {
			payChannel = request.getCash().getPayChannel();
		} else {
			payChannel = request.getRecharge().getPayChannel();
		}

		if (payChannel != null) {
			request.getSys().setPayChannel(payChannel);
			logger.debug("-----------当前使用支付渠道[" + payChannel.getCnName() + "]---------------");
			return null;
		}
		// 未指定渠道, 由支付路由器,自动分配
		ThirdPayQuota quota = null;
		if (request.getSys().getPayType() == PayType.GATEWAY) {
			quota = rechargeRoute.getNetsaveQuota(request.getMust().getBankType());
		} else {
			quota = rechargeRoute.autoMatch(request.getMust().getBankType());
		}
		payChannel = quota == null ? null : quota.getPayChannel();
		if (payChannel == null) {
			throw new Exception(ErrorMsgConstant.BANK_TYPE_NOT_SUPPORT);
		}
		request.getSys().setPayChannel(payChannel);
		logger.debug("-----------当前使用支付渠道[" + payChannel.getCnName() + "]---------------");
		return quota; // 返回额度,充值时判断充值限额
	}
}
