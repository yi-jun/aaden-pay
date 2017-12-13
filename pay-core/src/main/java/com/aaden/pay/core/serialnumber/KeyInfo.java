package com.aaden.pay.core.serialnumber;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.redis.DistributedLock;
import com.aaden.pay.core.redis.RedisService;
import com.aaden.pay.core.utils.SpringContextHelper;

/**
 * @Description 唯一id生成类
 * @author aaden
 * @date 2017年12月3日
 */
public class KeyInfo {
	protected SimpleLogger logger = SimpleLogger.getLogger(this.getClass());
	private String redisKeyPrefix = "KEY_INFO_DATE_KEY_LOCK_";
	private RedisService redisService;
	private final Lock locallock = new ReentrantLock();

	private KeyInfo() {
		redisService = (RedisService) SpringContextHelper.getBean(RedisService.class);
	}

	static class SingletonHoder {
		static KeyInfo instance = new KeyInfo();
	}

	/** 返回当前对象实例,单例模式 */
	public static KeyInfo getInstance() {
		return SingletonHoder.instance;
	}

	public String getDateKey() {
		try {
			locallock.lock();
			SimpleDateFormat sdf = new SimpleDateFormat("YYMMddHHmmssSSS");
			StringBuffer orderNo = new StringBuffer(sdf.format(new Date()));
			return orderNo.append(generateNumRedis(orderNo.toString())).toString();
		} finally {
			locallock.unlock();
		}
	}

	private String generateNume() {
		StringBuilder sRand = new StringBuilder();
		String[] array = { "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		Random rand = new Random();
		for (int i = 9; i > 1; i--) {
			int index = rand.nextInt(i);
			String tmp = array[index];
			array[index] = array[i - 1];
			array[i - 1] = tmp;
		}
		for (int i = 4; i > 0; i--) {
			sRand.append(array[i]);
		}
		return sRand.toString();
	}

	// 分布式锁,生成订单号
	private String generateNumRedis(String orderNo) {
		long start = System.currentTimeMillis();
		String key = redisKeyPrefix + orderNo;

		DistributedLock lock = new DistributedLock(key, 10, 10);

		Integer cache = null;
		try {
			if (!lock.lockDown()) {// 锁定超时
				return generateNume();
			}

			try {
				cache = redisService.getValue(key);
				if (cache == null)
					cache = Integer.parseInt(generateNume());

				cache = cache % 9999 + 1;
				redisService.setValue(key, cache, 60);
			} catch (Exception e) {
				return generateNume();
			}

		} finally {
			lock.lockRelease();
		}

		long total = System.currentTimeMillis() - start;
		logger.debug("redis获取递增数用时:" + total + "毫秒");

		String val = "0000" + cache;
		return val.substring(val.length() - 4, val.length());

	}

}
