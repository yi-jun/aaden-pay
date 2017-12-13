package com.aaden.pay.service.biz.cache;

import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.service.biz.vo.ThirdpayCache;

/**
 * @Description 支付缓存接口
 * @author aaden
 * @date 2017年12月27日
 */
public interface PayCacheService {

	/**
	 * 缓存时间,5分钟有效
	 */
	static final long BANK_BIND_SECONDS = 60 * 5 * 1000;

	/**
	 * 获取签约绑卡的缓存
	 */
	public ThirdBankSend getBankCache(String userId);

	/**
	 * 移除签约绑卡的缓存
	 */
	public void removeBankToken(String userId);

	/**
	 * 设置签约绑卡的缓存
	 */
	public void setBankToken(ThirdBankSend cache);

	/**
	 * 获取充值的缓存
	 */
	public ThirdpayCache getPayCache(String userId);

	/**
	 * 设置充值的缓存
	 */
	public void setPayCache(ThirdpayCache cache);

	/**
	 * 移除充值的缓存
	 */
	public void removePayToken(String userId);

}
