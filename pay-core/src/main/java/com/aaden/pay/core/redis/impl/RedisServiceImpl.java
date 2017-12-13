package com.aaden.pay.core.redis.impl;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.redis.RedisService;
import com.aaden.pay.core.redis.exception.RedisException;
import com.aaden.pay.core.redis.util.RedisEncode;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 *  @Description redis工具实现类
 *  @author aaden
 *  @date 2017年12月5日
 */
@Service("redisService")
public class RedisServiceImpl implements RedisService {

	private SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	@Autowired
	RedisClient redisClient;

	// 消息推送
	public void publish(String channel, String message) {
		Jedis jedis = null;
		try {
			jedis = redisClient.getJedis();
			jedis.publish(channel, message);
			if (logger.isDebugEnabled())
				logger.debug(" publish for channel :" + channel + " message " + message);
		} catch (RedisException e) {
			logger.error(" publish RedisException:", e);
		} finally {
			if (jedis != null) {
				try {
					redisClient.releaseJedis(jedis);
				} catch (RedisException e) {
					logger.error(" publish returnResource RedisException:", e);
				}
			}
		}
	}

	// 消息订阅
	public void subScribe(JedisPubSub jedisPubSub, String... channels) {
		Jedis jedis = null;
		try {
			jedis = redisClient.getJedis();
			jedis.subscribe(jedisPubSub, channels);
			if (logger.isDebugEnabled())
				logger.debug(" subScribe for channel :" + Arrays.toString(channels));
		} catch (RedisException e) {
			logger.error(" subScribe RedisException:", e);
		} finally {
			if (jedis != null) {
				try {
					redisClient.releaseJedis(jedis);
				} catch (RedisException e) {
					logger.error(" subScribe returnResource RedisException:", e);
				}
			}
		}
	}

	public long getRemainingTimeByStrKey(String key) {
		long seconds = 0;
		Jedis jedis = null;
		try {
			jedis = redisClient.getJedis();
			byte[] byteKey = RedisEncode.encode(key);
			if (StringUtils.isNotBlank(key) && jedis.exists(byteKey)) {
				seconds = jedis.ttl(key);
			}
		} catch (RedisException e) {
			logger.error(" getRemainingTimeByKey exception for key:" + key, e);
		} finally {
			if (jedis != null) {
				try {
					redisClient.releaseJedis(jedis);
				} catch (RedisException e) {
					logger.error(" getRemainingTimeByKey returnResource Exception:", e);
				}
			}
		}
		return seconds;
	}

	/**
	 * 通过key得到对象
	 * 
	 * @return <T extends Serializable> T
	 */
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T getValue(String key) throws RedisException {
		Serializable decode = null;
		Jedis jedis = null;
		try {
			if (StringUtils.isBlank(key)) {
				return null;
			}
			jedis = redisClient.getJedis();
			byte[] byteKey = RedisEncode.encode(key);
			if (jedis.exists(byteKey))
				decode = RedisEncode.decode(jedis.get(byteKey));
			else
				return null;
		} catch (RedisException e) {
			logger.error(" getValue exception for key:" + key, e);
			throw new RedisException("  getValue Exception", e);
		} finally {
			if (jedis != null) {
				try {
					redisClient.releaseJedis(jedis);
				} catch (RedisException e) {
					logger.error(" getValue returnResource Exception:", e);
					throw new RedisException(" getValue returnResource Exception", e);
				}
			}
		}
		return (T) decode;
	}

	public <T extends Serializable> boolean setValue(String key, T value) throws RedisException {
		Jedis jedis = null;
		try {
			if (StringUtils.isBlank(key)) {
				return Boolean.FALSE;
			}
			jedis = redisClient.getJedis();
			byte[] byteKey = RedisEncode.encode(key);
			byte[] byteValue = RedisEncode.encode(value);
			String flag = jedis.set(byteKey, byteValue);
			if (flag.equalsIgnoreCase("OK")) {
				if (logger.isDebugEnabled())
					logger.debug(" setValue successful,key is : " + key);
				return Boolean.TRUE;
			}
		} catch (RedisException e) {
			logger.error(" setValue exception for key:" + key, e);
			throw new RedisException(" setValue Exception", e);
		} finally {
			if (jedis != null) {
				try {
					redisClient.releaseJedis(jedis);
				} catch (RedisException e) {
					logger.error(" setValue returnResource Exception:", e);
					throw new RedisException(" setValue returnResource Exception", e);
				}
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * 将对象存入redis，有一个过期时间seconds,成功返回true，失败返回false
	 * 
	 * @param <T>
	 * @param int
	 * @return boolean
	 */
	public <T extends Serializable> boolean setValue(String key, T value, int seconds) throws RedisException {
		Jedis jedis = null;
		try {
			if (StringUtils.isBlank(key)) {
				return Boolean.FALSE;
			}
			jedis = redisClient.getJedis();
			byte[] byteKey = RedisEncode.encode(key);
			byte[] byteValue = RedisEncode.encode(value);
			String flag = jedis.set(byteKey, byteValue);
			jedis.expire(byteKey, seconds);
			if (flag.equalsIgnoreCase("OK")) {
				if (logger.isDebugEnabled())
					logger.debug(" setValue successful,key is : " + key + " and timeout is " + seconds);
				return Boolean.TRUE;
			}
		} catch (RedisException e) {
			logger.error(" setValue exception for key:" + key, e);
			throw new RedisException("  setValue Exception", e);
		} finally {
			if (jedis != null) {
				try {
					redisClient.releaseJedis(jedis);
				} catch (RedisException e) {
					logger.error(" setValue returnResource Exception:", e);
					throw new RedisException(" setValue returnResource Exception", e);
				}
			}
		}
		return Boolean.FALSE;
	}

	public boolean exists(String key) throws RedisException {
		Jedis jedis = null;
		try {
			jedis = redisClient.getJedis();
			byte[] byteKey = RedisEncode.encode(key);
			if (jedis.exists(byteKey)) {
				return Boolean.TRUE;
			}
		} catch (RedisException e) {
			logger.error(" exists exception for key:" + key, e);
			throw new RedisException(" exists Exception", e);
		} finally {
			if (jedis != null) {
				try {
					redisClient.releaseJedis(jedis);
				} catch (RedisException e) {
					logger.error(" exists returnResource Exception:", e);
					throw new RedisException(" exists returnResource Exception", e);
				}
			}
		}
		return Boolean.FALSE;
	}

	public long remove(String key) {
		long number = 0;
		Jedis jedis = null;
		try {
			if (StringUtils.isBlank(key)) {
				return number;
			}
			jedis = redisClient.getJedis();
			byte[] byteKey = RedisEncode.encode(key);
			number = jedis.del(byteKey);
		} catch (RedisException e) {
			logger.error(" remove exception for key:" + key, e);
		} finally {
			if (jedis != null) {
				try {
					redisClient.releaseJedis(jedis);
				} catch (RedisException e) {
					logger.error(" remove returnResource Exception:", e);
				}
			}
		}
		return number;
	}

	/**
	 * <p>
	 * 原子递增,用于统计
	 * </p>
	 * <p>
	 * 方法调用一次加一
	 * </p>
	 * 
	 * @param key
	 *            递增的值的模块代号
	 * @param field
	 *            递增的值
	 */
	public Long increaseByKey(String key, String field) {
		Jedis jedis = null;
		Long count = 0l;
		try {
			jedis = redisClient.getJedis();
			// 原子递增
			count = jedis.hincrBy(key, field, 1);
			jedis.hset(key, field, String.valueOf(count));
		} catch (RedisException e) {
			logger.error(" increaseByKey RedisException:", e);
		} finally {
			if (jedis != null) {
				try {
					redisClient.releaseJedis(jedis);
				} catch (RedisException e) {
					logger.error(" returnResource RedisException:", e);
				}
			}
		}
		return count;
	}

	/**
	 * <p>
	 * 统计减少,用于清零
	 * </p>
	 * 
	 * @param key
	 *            递增的值的模块代号
	 * @param field
	 *            递增的值
	 * @param value
	 *            要减少的值
	 */
	public void delIncreaseByKey(String key, String field, Long value) {
		Jedis jedis = null;
		try {
			jedis = redisClient.getJedis();
			Long count = jedis.hincrBy(key, field, value > 0 ? 0L - value : value);
			jedis.hset(key, field, String.valueOf(count));
		} catch (RedisException e) {
			logger.error(" delIncreaseByKey RedisException:", e);
		} finally {
			if (jedis != null) {
				try {
					redisClient.releaseJedis(jedis);
				} catch (RedisException e) {
					logger.error(" returnResource RedisException:", e);
				}
			}
		}
	}

	/**
	 * <p>
	 * 统计初始化
	 * </p>
	 * 
	 * @param key
	 *            递增的值的模块代号
	 * @param field
	 *            递增的值
	 * @param value
	 *            要初始化的值
	 */
	public void initIncreaseByKey(String key, String field, Long value) {
		this.resetIncreaseByKey(key, field);
		Jedis jedis = null;
		try {
			jedis = redisClient.getJedis();
			Long count = jedis.hincrBy(key, field, value > 0 ? value : 0L - value);
			jedis.hset(key, field, String.valueOf(count));
		} catch (RedisException e) {
			logger.error(" delIncreaseByKey RedisException:", e);
		} finally {
			if (jedis != null) {
				try {
					redisClient.releaseJedis(jedis);
				} catch (RedisException e) {
					logger.error(" returnResource RedisException:", e);
				}
			}
		}
	}

	/**
	 * 清零
	 * 
	 * @param key
	 * @param field
	 * @param value
	 */
	public void resetIncreaseByKey(String key, String field) {
		Long curV = this.getIncreaseByKey(key, field);
		this.delIncreaseByKey(key, field, curV);
	}

	/**
	 * <p>
	 * 得到统计数据
	 * </p>
	 * 
	 * @param key
	 *            递增的值的模块代号
	 * @param field
	 *            递增的值
	 */
	public Long getIncreaseByKey(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = redisClient.getJedis();
			if (jedis.hexists(key, field)) {
				try {
					return Long.valueOf(jedis.hget(key, field));
				} catch (NumberFormatException e) {
					logger.error(" getIncreaseByKey NumberFormatException:", e);
					return -1L;
				}
			}
		} catch (RedisException e) {
			logger.error(" getIncreaseByKey RedisException:", e);
		} finally {
			if (jedis != null) {
				try {
					redisClient.releaseJedis(jedis);
				} catch (RedisException e) {
					logger.error(" returnResource RedisException:", e);
				}
			}
		}
		return 0L;
	}

}
