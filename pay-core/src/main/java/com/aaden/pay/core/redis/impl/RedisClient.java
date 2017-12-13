package com.aaden.pay.core.redis.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.stereotype.Service;

import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.redis.exception.RedisException;
import com.aaden.pay.core.redis.properties.RedisProperties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Description redis客户端
 * @author aaden
 * @date 2017年12月17日
 */
@Service("redisClient")
public class RedisClient {

	private volatile boolean isInited = Boolean.FALSE;

	public RedisClient() {
		if (!isInited) {
			this.init();
		}
	}

	int dbNum = 0;

	private int timeout = 10000;

	private SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	private GenericObjectPoolConfig poolConfig = new JedisPoolConfig();

	private JedisPool jedisPool;

	public Jedis getJedis() throws RedisException {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis;
		} catch (Exception e) {
			closeJedis(jedis);
			throw new RedisException(" getJedis Exception", e);
		}
	}

	public void releaseJedis(Jedis jedis) throws RedisException {
		try {
			// jedisPool.returnResource(jedis);
			// 升级为2.7.2后 returnResource 废除
			closeJedis(jedis);
		} catch (Exception e) {
			throw new RedisException(" releaseJedis Exception", e);
		}
	}

	private void closeJedis(Jedis jedis) {
		if (jedis != null)
			jedis.close();
	}

	// 初始化客户端
	public synchronized boolean init() {
		if (isInited)
			return Boolean.TRUE;
		try {
			String host = RedisProperties.host;
			String port = RedisProperties.port;
			String password = RedisProperties.password;
			String maxActive = RedisProperties.maxActive;
			String maxIdle = RedisProperties.maxIdle;
			String minIdle = RedisProperties.minIdle;
			String maxWait = RedisProperties.maxWait;
			String minEvictableIdleTimeMillis = RedisProperties.minEvictableIdleTimeMillis;
			String timeBetweenEvictionRunsMillis = RedisProperties.timeBetweenEvictionRunsMillis;
			poolConfig.setMaxTotal(Integer.parseInt(StringUtils.isBlank(maxActive) ? "1000" : maxActive.trim()));
			poolConfig.setMaxIdle(Integer.parseInt(StringUtils.isBlank(maxIdle) ? "1000" : maxIdle.trim()));
			poolConfig.setMinIdle(Integer.parseInt(StringUtils.isBlank(minIdle) ? "10" : minIdle.trim()));
			poolConfig.setMaxWaitMillis(Long.parseLong(StringUtils.isBlank(maxWait) ? "20000" : maxWait.trim()));
			if (StringUtils.isNotBlank(minEvictableIdleTimeMillis)) // 设定多长时间视为失效链接
				poolConfig.setMinEvictableIdleTimeMillis(Integer.parseInt(minEvictableIdleTimeMillis.trim()));
			if (StringUtils.isNotBlank(timeBetweenEvictionRunsMillis)) // 设定每隔多长时间进行有效检查与上面参数同时使用
				poolConfig.setTimeBetweenEvictionRunsMillis(Integer.parseInt(timeBetweenEvictionRunsMillis.trim()));
			jedisPool = new JedisPool(poolConfig, host, Integer.parseInt(port.trim()), timeout, password);

			isInited = Boolean.TRUE;
			return Boolean.TRUE;
		} catch (Exception e) {
			logger.error(" Initialization Redis Exception :", e);
		}
		return Boolean.FALSE;

	}

	/**
	 * 选择哪个Redis库，默认选择序号为0的库，从0开始
	 * 
	 * @param int
	 * @return boolean
	 */
	public boolean selectRedisDB(int dbNum) {
		Jedis jedis = null;
		try {
			if (dbNum < 0)
				return Boolean.FALSE;
			jedis = this.getJedis();
			this.dbNum = dbNum;
			String statusCode = jedis.select(dbNum);
			if (statusCode.equalsIgnoreCase("ok"))
				return Boolean.TRUE;
			if (logger.isDebugEnabled())
				logger.debug(" selectRedisDB " + dbNum + " ");
		} catch (RedisException e) {
			logger.error(" selectRedisDB RedisException:", e);
		} finally {
			if (jedis != null) {
				try {
					this.releaseJedis(jedis);
				} catch (RedisException e) {
					logger.error(" selectRedisDB returnResource RedisException:", e);
				}
			}
		}
		return Boolean.FALSE;
	}
}
