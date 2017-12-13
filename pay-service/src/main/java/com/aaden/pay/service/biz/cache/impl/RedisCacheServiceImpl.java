package com.aaden.pay.service.biz.cache.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.redis.RedisService;
import com.aaden.pay.core.redis.exception.RedisException;
import com.aaden.pay.service.biz.cache.PayCacheService;
import com.aaden.pay.service.biz.vo.ThirdpayCache;

/**
 * @Description 支付缓存实现(redis方式)
 * @author aaden
 * @date 2017年12月18日
 */
@Service("redisCacheService")
public class RedisCacheServiceImpl implements PayCacheService {

	protected SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	private static final String BANK_KEY = "redis_cache_bank_";
	private static final String RECHARGE_KEY = "redis_cache_recharge_";
	static final int REDIS_EXPIRE_SECONDS = (int) (BANK_BIND_SECONDS / 1000);

	@Autowired
	private RedisService redisService;

	/**
	 * 数据库抓取,效率低 如有缓存服务器或其他允许的情况, 该方法可修改成: 将 ThirdBankSend 对象缓存, 然后从缓存中读取,
	 * 绑卡成功后移除该缓存
	 */
	@Override
	public ThirdBankSend getBankCache(String userId) {
		try {
			return redisService.getValue(BANK_KEY + userId);
		} catch (RedisException e) {
			logger.error("redis 异常:", e);
			return null;
		}
	}

	@Override
	public ThirdpayCache getPayCache(String userId) {
		try {
			return redisService.getValue(RECHARGE_KEY + userId);
		} catch (RedisException e) {
			logger.error("redis 异常:", e);
			return null;
		}
	}

	@Override
	public void removeBankToken(String userId) {
		try {
			redisService.remove(BANK_KEY + userId);
		} catch (RedisException e) {
			logger.error("redis 异常:", e);
		}
	}

	@Override
	public void removePayToken(String userId) {
		try {
			redisService.remove(RECHARGE_KEY + userId);
		} catch (RedisException e) {
			logger.error("redis 异常:", e);
		}
	}

	@Override
	public void setBankToken(ThirdBankSend cache) {
		try {
			redisService.setValue(BANK_KEY + cache.getUserId(), cache, REDIS_EXPIRE_SECONDS);
		} catch (RedisException e) {
			logger.error("redis 异常:", e);
		}

	}

	@Override
	public void setPayCache(ThirdpayCache cache) {
		try {
			redisService.setValue(RECHARGE_KEY + cache.getUserId(), cache, REDIS_EXPIRE_SECONDS);
		} catch (RedisException e) {
			logger.error("redis 异常:", e);
		}
	}
}
