package com.aaden.pay.core.redis;

import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.redis.exception.RedisException;
import com.aaden.pay.core.redis.impl.RedisClient;
import com.aaden.pay.core.utils.SpringContextHelper;

import redis.clients.jedis.Jedis;

/**
 * @Description redis分布式锁封装类
 * @author aaden
 * @date 2017年12月19日
 */
public class DistributedLock {

	SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	private JedisLock jedisLock;
	private Jedis jedis;
	private RedisClient redisClient;
	private String lockKey;
	private int timeoutMsecs;// 锁等待时间 秒
	private int expireMsecs;// 锁超时时间 秒
	private volatile boolean isLocked = Boolean.FALSE;

	public DistributedLock(String lockKey) {
		this(lockKey, 30, 300);
	}

	public DistributedLock(String lockKey, int timeoutMsecs, int expireMsecs) {
		redisClient = SpringContextHelper.getBean(RedisClient.class);
		this.lockKey = lockKey;
		this.timeoutMsecs = timeoutMsecs;
		this.expireMsecs = expireMsecs;
		try {
			jedis = redisClient.getJedis();
		} catch (RedisException e) {
			logger.error(" SimpleLock RedisException ", e);
		}
		this.jedisLock = new JedisLock(jedis, lockKey.intern(), timeoutMsecs, expireMsecs);
	}

	/**
	 * 锁定，如果不能持有锁会休眠等待锁，直到超时退出
	 * 
	 * @return boolean
	 */
	public boolean lockDown() {
		long begin = System.currentTimeMillis();
		try {
			// timeout超时，等待入锁的时间，设置为3秒；expiration过期，锁存在的时间设置为5分钟
			if (jedisLock.acquire()) {
				logger.info(String.format("lock down for key %s,timeout %s seconds,expire %s seconds", lockKey, timeoutMsecs, expireMsecs));
				isLocked = Boolean.TRUE;
				return isLocked;
			} else {
				logger.info(String.format("get lock time out,wait for %s seconds ", timeoutMsecs));
			}
			logger.info(String.format("get lock %s cost %s seconds", lockKey, System.currentTimeMillis() - begin));
		} catch (Throwable t) {
			logger.error("lockDown exception:", t);
		}
		return Boolean.FALSE;
	}

	/**
	 * 释放锁
	 * 
	 * @param lock
	 * @param jedis
	 */
	public void lockRelease() {
		if (isLocked) {
			try {
				jedisLock.release();// 解锁
			} catch (Exception e) {
				logger.error(" lockRelease Exception:", e);
			}
			logger.info(String.format("release lock %s", lockKey));
		}
		if (jedis != null) {
			try {
				redisClient.releaseJedis(jedis);
			} catch (RedisException e) {
				logger.error(" lockRelease releaseJedis RedisException:", e);
			}
		}
	}
}
