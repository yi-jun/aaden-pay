package com.aaden.pay.service.biz.ext;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.comm.enums.PayType;
import com.aaden.pay.api.comm.enums.SendStatus;
import com.aaden.pay.api.comm.enums.TradeStatus;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.api.comm.model.ThirdPayValidcode;
import com.aaden.pay.core.contants.ErrorMsgConstant;
import com.aaden.pay.core.eumus.YesOrNo;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.orm.exception.DataBaseAccessException;
import com.aaden.pay.core.serialnumber.KeyInfo;
import com.aaden.pay.core.utils.DateUtils;
import com.aaden.pay.service.biz.cache.PayCacheService;
import com.aaden.pay.service.biz.route.ThirdPayRoute;
import com.aaden.pay.service.biz.vo.ThirdPayResponse;
import com.aaden.pay.service.biz.vo.ThirdpayCache;
import com.aaden.pay.service.comm.service.ThirdPayRecordService;
import com.aaden.pay.service.comm.service.ThirdPayValidcodeService;

/**
 * @Description 支付事务实现
 * @author aaden
 * @date 2017年12月14日
 */
@Service
public class PaymentTransaction {

	SimpleLogger logger = SimpleLogger.getLogger(this.getClass());
	@Autowired
	ThirdPayRoute thirdPayRoute;
	@Autowired
	@Qualifier("dbCacheService")
	private PayCacheService payCacheService;
	@Autowired
	ThirdPayValidcodeService thirdPayValidcodeService;
	@Autowired
	ThirdPayRecordService thirdPayRecordService;

	/**
	 * 有些渠道需先发送验证码,再提交充值, 部分情况(如验证码错误)可多次提交充值,
	 * 该情况下,需先查询数据库是否存在该笔流水号Serinum的支付记录,再做save或者update,
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ThirdPayRecord doRechargeRecord(PayRequest payRequest) throws DataBaseAccessException {
		ThirdpayCache cache = payCacheService.getPayCache(payRequest.getMust().getUserId());
		if (cache == null) {
			throw new DataBaseAccessException("验证码已失效,请重新获取");
		}
		payRequest.getSys().setRechargeToken(cache.getToken());

		ThirdPayRecord payRecord = null;
		ThirdPayRecord db = thirdPayRecordService.findBySerialnumber(cache.getSerialnumber());
		if (db != null) {
			if (db.isSuccess()) {
				throw new DataBaseAccessException("交易已成功,请勿重复支付");
			}
			payRecord = db;
		} else {
			try {// 使用缓存流水号
				payRecord = this.initSavePayRecord(payRequest, cache.getSerialnumber());
			} catch (DataBaseAccessException e) {
				throw new DataBaseAccessException(ErrorMsgConstant.ERR_OPERATE_DATABASE);
			}

		}
		return payRecord;
	}

	/** 解析支付返回,更新支付记录 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void parseUpdatePayRecord(ThirdPayResponse tpResp, ThirdPayRecord payRecord) {
		payRecord.setSendCount(payRecord.getSendCount() + 1);
		if (tpResp == null) {
			payRecord.setTradeStatus(TradeStatus.RETRY);
		} else {
			payRecord.setSendStatus(tpResp.getSendStatus());
			payRecord.setPayCode(tpResp.getPayCode());
			payRecord.setPayMessage(tpResp.getPayMessage());
			payRecord.setUpdateTime(DateUtils.today());
			payRecord.setSettleTime(tpResp.getSettleTime());
			payRecord.setThirdnumber(tpResp.getThirdnumber());
			payRecord.setTradeStatus(tpResp.getTradeStatus());
			if (payRecord.isSuccess()) {
				payRecord.setActAmount(tpResp.getActAmount());
			}
		}
		try {
			thirdPayRecordService.update(null, payRecord);
		} catch (DataBaseAccessException e) { // 更新支付记录数据库异常,不抛出
			logger.error(" doCallBack save ThirdPayRecordDtl DataBaseAccessException", e);
		}

		// 移除认证支付缓存
		if (payRecord.isSuccess() && payRecord.getPayType() == PayType.AUTHPAY) {
			payCacheService.removePayToken(payRecord.getUserId());
		}

	}

	/** 初始化,保存支付订单 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ThirdPayRecord initSavePayRecord(PayRequest payRequest, String sernum) throws DataBaseAccessException {
		// 未指定流水号,生成一个流水号
		sernum = sernum == null ? KeyInfo.getInstance().getDateKey() : sernum;
		ThirdPayRecord tpRecord = new ThirdPayRecord();
		tpRecord.initPrimaryKey();
		tpRecord.setSerialnumber(sernum);// 交易流水号
		Date now = DateUtils.today();
		tpRecord.setUserId(payRequest.getMust().getUserId());
		tpRecord.setCardNo(payRequest.getMust().getCardNo());
		tpRecord.setRealName(payRequest.getMust().getRealName());
		tpRecord.setActAmount(BigDecimal.ZERO);
		tpRecord.setBankType(payRequest.getMust().getBankType());
		tpRecord.setOrderAmount(payRequest.getMust().getAmount());
		tpRecord.setOrderCode(payRequest.getMust().getOrderCode());
		tpRecord.setCardProp(payRequest.getMust().getCardProp());
		tpRecord.setTradeStatus(TradeStatus.RETRY);
		tpRecord.setUpdateTime(now);
		tpRecord.setCallbackStatus(YesOrNo.NO.getCode());
		tpRecord.setSendCount(0);
		tpRecord.setSendStatus(SendStatus.SENDING);
		tpRecord.setSendTime(now);
		tpRecord.setPayChannel(payRequest.getSys().getPayChannel());
		tpRecord.setPayType(payRequest.getSys().getPayType());
		BigDecimal fee = BigDecimal.ZERO;
		try { // 计算手续费
			fee = this.thirdPayRoute.route(payRequest).calculationCost(tpRecord);
		} catch (Exception e) {
		}
		tpRecord.setCosts(fee);

		try {
			thirdPayRecordService.save(null, tpRecord);// 主表
		} catch (Exception e) {
			throw new DataBaseAccessException(ErrorMsgConstant.ERR_OPERATE_DATABASE);
		}
		return tpRecord;
	}

	/** 初始化,保存充值验证码记录 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ThirdPayValidcode initSaveValidcode(PayRequest payRequest) throws Exception {
		ThirdPayValidcode valid = new ThirdPayValidcode();
		valid.initPrimaryKey();
		valid.setAmount(payRequest.getMust().getAmount());
		valid.setPayChannel(payRequest.getSys().getPayChannel());
		valid.setSendTime(DateUtils.today());
		valid.setSerialnumber(KeyInfo.getInstance().getDateKey());
		valid.setUserId(payRequest.getMust().getUserId());
		valid.setUserLoginName(payRequest.getMust().getUserLoginName());
		try {
			thirdPayValidcodeService.save(null, valid);
		} catch (DataBaseAccessException e) {
			throw new Exception(ErrorMsgConstant.ERR_OPERATE_DATABASE);
		}
		return valid;
	}

	/** 解析第三方结果,更新记录 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void parseUpdateValidcode(ThirdPayValidcode valid, ThirdPayResponse resp) {
		valid.setTradeStatus(resp.getTradeStatus());
		valid.setRetCode(resp.getPayCode());
		valid.setRetMsg(resp.getPayMessage());
		valid.setToken(resp.getToken());
		try {
			thirdPayValidcodeService.update(null, valid);
		} catch (DataBaseAccessException e) {// 更新失败,不抛出异常
		}
		// 设置充值缓存
		if (valid.isSuccess()) {
			payCacheService.setPayCache(new ThirdpayCache(valid.getSerialnumber(), valid.getToken(), valid.getUserId()));
		}

	}
}
