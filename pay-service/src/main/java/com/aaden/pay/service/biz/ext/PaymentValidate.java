package com.aaden.pay.service.biz.ext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.DbPayService;
import com.aaden.pay.api.biz.enums.allinpay.AllinGatewayBankType;
import com.aaden.pay.api.biz.enums.allinpay.AllinPayBankType;
import com.aaden.pay.api.biz.enums.baofoo.BaofooBankType;
import com.aaden.pay.api.biz.enums.baofoo.BaofooPayBankType;
import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.biz.vo.PayRequest.MustData;
import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.PayType;
import com.aaden.pay.api.comm.model.ThirdPayQuota;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
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
		if (returnParameter == null || returnParameter.isEmpty()) {
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

		PayRequest.CashData cash = request.getCash();
		// 验证必要参数
		this.checkMust(request);

		if (cash.getOrderTotalAmount() == null)
			throw new Exception("提现订单支付总金额不能为空");
		if (cash.getOrderTotalAmount().compareTo(request.getMust().getAmount()) < 0)
			throw new Exception("提现支付总金额不能小于本次支付金额");
		if (StringUtils.isBlank(cash.getBankCity()) || StringUtils.isBlank(cash.getBankProv()) || StringUtils.isBlank(cash.getBranchName()))
			throw new Exception("支行信息不能为空");

		// 第三方渠道支持判断
		this.checkAndfillChannel(request);
		// 验证重复提交
		this.checkRepeat(request);

		// 获取第三方支付实现类
		return thirdPayRoute.route(request);
	}

	/**
	 * 充值
	 */
	public ThirdPayService validateRecharge(PayRequest request) throws Exception {
		// 验证必要参数
		this.checkMust(request);

		// 验证重复提交
		this.checkRepeat(request);
		// 支付渠道判断
		ThirdPayQuota quota = this.checkAndfillChannel(request);

		if (quota == null)
			quota = dbPayService.getPayQuota(request.getSys().getPayChannel(), request.getMust().getBankType());
		// 单笔限额判断
		if (quota != null && request.getMust().getAmount().compareTo(quota.getSingleAmount()) > 0) {
			throw new Exception(String.format("[%s]单笔充值额度为:%s", request.getMust().getBankType().getCnName(), BigDecimalUtils.format(quota.getSingleAmount())));
		}

		// 获取第三方支付实现类
		return thirdPayRoute.route(request);
	}

	private void checkMust(PayRequest request) throws Exception {
		if (request == null)
			throw new Exception("参数不能为空");
		MustData must = request.getMust();
		if (StringUtils.isBlank(must.getUserId()) || StringUtils.isBlank(must.getUserLoginName()))
			throw new Exception("用户信息不能为空");
		if (must.getBankType() == null)
			throw new Exception("银行卡信息不能为空");
		if (request.getSys().getPayType() != PayType.GATEWAY && (StringUtils.isBlank(must.getCardNo()) || StringUtils.isBlank(must.getRealName())))
			throw new Exception("银行卡信息不能为空");
		if (!BigDecimalUtils.isGreaterZero(must.getAmount()))
			throw new Exception("支付金额不正确");
		BigDecimal round = must.getAmount().setScale(2, BigDecimal.ROUND_DOWN);
		if (round.compareTo(must.getAmount()) != 0)
			throw new Exception("支付金额最多只能有两位小数");
		if (StringUtils.isBlank(must.getOrderCode()))
			throw new Exception("系统订单号不能为空");
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

	private void checkBankType(PayRequest request) throws Exception {
		PayChannel channel = request.getSys().getPayChannel();
		PayType type = request.getSys().getPayType();
		BankType bankType = request.getMust().getBankType();
		// 银行卡支付判断
		switch (channel) {
		case ALLIN:
			if (type == PayType.GATEWAY) {
				if (AllinGatewayBankType.parse(bankType) == null)
					throw new Exception("系统暂不支持该银行卡");
			} else {
				if (AllinPayBankType.parse(bankType) == null)
					throw new Exception("系统暂不支持该银行卡");
			}
			break;
		case BAOFOO:
			if (type == PayType.PAYOUT) {
				if (BaofooPayBankType.parse(bankType) == null)
					throw new Exception("系统暂不支持该银行卡");
			} else {
				if (BaofooBankType.parse(bankType) == null)
					throw new Exception("系统暂不支持该银行卡");
			}
			break;

		default:
			break;
		}
	}

	private ThirdPayQuota checkAndfillChannel(PayRequest request) throws Exception {
		PayChannel payChannel = null;
		try {
			if (request.getSys().getPayType() == PayType.PAYOUT) {
				payChannel = request.getCash().getPayChannel();
			} else {
				payChannel = request.getRecharge().getPayChannel();
			}

			if (payChannel != null) {
				return null;
			}
			// 未指定渠道, 由支付路由器,自动分配
			ThirdPayQuota quota = null;
			if (request.getSys().getPayType() == PayType.GATEWAY) {
				quota = rechargeRoute.getNetsaveQuota(request.getMust().getBankType());
			} else {
				quota = rechargeRoute.autoMatch(request.getMust().getBankType());
			}
			payChannel = quota.getPayChannel();
			return quota;
		} finally {
			if (payChannel != null) {
				request.getSys().setPayChannel(payChannel);
				logger.debug("-----------当前使用支付渠道[" + payChannel.getCnName() + "]---------------");
				// 验证该渠道是否支持该银行卡
				this.checkBankType(request);
			}

		}
	}
}
