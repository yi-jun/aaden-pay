package com.aaden.pay.service.biz.cache.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.comm.enums.BankVerifyType;
import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.api.comm.model.ThirdPayValidcode;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.service.biz.cache.PayCacheService;
import com.aaden.pay.service.biz.vo.ThirdpayCache;
import com.aaden.pay.service.comm.service.ThirdBankSendService;
import com.aaden.pay.service.comm.service.ThirdPayRecordService;
import com.aaden.pay.service.comm.service.ThirdPayValidcodeService;

/**
 * @Description 支付缓存实现(数据库方式)
 * @author aaden
 * @date 2017年12月18日
 */
@Service("dbCacheService")
public class DbCacheServiceImpl implements PayCacheService {

	protected SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	@Autowired
	private ThirdBankSendService bankSendService;
	@Autowired
	private ThirdPayRecordService payRecordService;
	@Autowired
	private ThirdPayValidcodeService payValidcodeService;

	/**
	 * 数据库抓取,效率低 如有缓存服务器或其他允许的情况, 该方法可修改成: 将 ThirdBankSend 对象缓存, 然后从缓存中读取,
	 * 绑卡成功后移除该缓存
	 */
	@Override
	public ThirdBankSend getBankCache(String userId) {
		ThirdBankSend info = bankSendService.getLast(userId, BankVerifyType.APPLY);
		if (info == null)
			return null;

		// 判断是否过期失效
		long diff = System.currentTimeMillis() - info.getSendTime().getTime();
		if (diff > BANK_BIND_SECONDS) {
			return null;
		}

		// 判断是否该笔之后是否已经有成功的绑卡
		ThirdBankSend bind = bankSendService.getLast(userId, BankVerifyType.CONFIRM);

		if (bind != null && bind.getSendTime().after(info.getSendTime()))
			return null;

		return info;
	}

	@Override
	public ThirdpayCache getPayCache(String userId) {
		ThirdPayValidcode info = payValidcodeService.getLast(userId);
		if (info == null)
			return null;

		// 判断是否过期失效
		long diff = System.currentTimeMillis() - info.getSendTime().getTime();
		if (diff > BANK_BIND_SECONDS) {
			return null;
		}

		// 判断是否该笔之后是否已经有成功的支付记录
		ThirdPayRecord bind = payRecordService.findBySerialnumber(info.getSerialnumber());
		if (bind != null && !bind.isFail())
			return null;

		return new ThirdpayCache(info.getSerialnumber(), info.getToken(), userId);
	}

	@Override
	public void removeBankToken(String userId) {
	}

	@Override
	public void removePayToken(String userId) {
	}

	@Override
	public void setBankToken(ThirdBankSend cache) {
	}

	@Override
	public void setPayCache(ThirdpayCache cache) {
	}
}
