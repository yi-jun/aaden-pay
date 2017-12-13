package com.aaden.pay.core.redis;

import java.io.Serializable;

import redis.clients.jedis.JedisPubSub;

import com.aaden.pay.core.redis.exception.RedisException;

/**
 * @Description redis的工具类
 * @author aaden
 * @date 2017年12月19日
 */
public interface RedisService {

	/**
	 * 推送消息
	 * 
	 * @param channel
	 * @param message
	 */
	public void publish(String channel, String message);

	/**
	 * 订阅消息
	 * 
	 * @param jedisPubSub
	 * @param channels
	 */
	public void subScribe(JedisPubSub jedisPubSub, String... channels);

	/**
	 * 如果key设置了过期时间，则可以获取key的存活时间
	 * 
	 * @param key
	 * @return
	 */
	public long getRemainingTimeByStrKey(String key);

	/**
	 * 将对象永久性的存入redis,成功返回true，失败返回false 注意：必须是序列化的对象
	 * 
	 * @param <T>
	 * @param key
	 * @param value
	 * @return
	 */
	public <T extends Serializable> boolean setValue(String key, T value) throws RedisException;

	/**
	 * 将对象存入redis，设置一个过期时间seconds,成功返回true，失败返回false 注意：必须是序列化的key和value
	 * 
	 * @param <T>
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public <T extends Serializable> boolean setValue(String key, T value, int seconds) throws RedisException;

	/**
	 * 通过key得到序列化后的对象 注意:key是String，和序列化的对象区分开来
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 */
	public <T extends Serializable> T getValue(String key) throws RedisException;

	/**
	 * 判断key是否存在 存储的类型是对象字符串时适用
	 * 
	 * @param key
	 * @return boolean
	 * @throws com.aaden.pay.core.redis.exception.RedisException
	 */
	public boolean exists(String key) throws RedisException;

	/**
	 * 将一个key删除掉
	 * 
	 * @param key
	 * @return
	 */
	public long remove(String key) throws RedisException;

	/**
	 * <p>
	 * 原子递增的统计初始化
	 * </p>
	 * <p>
	 * 只能调用一次，重复调用会导致数据异常
	 * </p>
	 * 
	 * @param key
	 *            递增的值的模块代号
	 * @param field
	 *            递增的值
	 * @param value
	 *            初始化的数字
	 */
	public void initIncreaseByKey(String key, String field, Long value);

	/**
	 * 清零
	 * 
	 * @param key
	 *            递增的值的模块代号
	 * @param field
	 *            递增的值
	 */
	public void resetIncreaseByKey(String key, String field);

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
	 * @return Long 调用后当前的值
	 */
	public Long increaseByKey(String key, String field);

	/**
	 * <p>
	 * 统计减少
	 * </p>
	 * 
	 * @param key
	 *            递增的值的模块代号
	 * @param field
	 *            递增的值
	 * @param value
	 *            要减少的值
	 */
	public void delIncreaseByKey(String key, String field, Long value);

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
	public Long getIncreaseByKey(String key, String field);

}
