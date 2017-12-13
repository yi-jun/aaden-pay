package com.aaden.pay.core.redis.properties;

import com.aaden.pay.core.prop.SimpleProperty;

/**
 * @Description redis配置
 * @author aaden
 * @date 2017年12月23日
 */
public class RedisProperties {

	public static String host = SimpleProperty.getProperty("jedis_host");
	public static String port = SimpleProperty.getProperty("jedis_port");
	public static String password = SimpleProperty.getProperty("jedis_password");

	public static String maxActive = SimpleProperty.getProperty("jedis_maxActive");
	public static String maxIdle = SimpleProperty.getProperty("jedis_maxIdle");
	public static String minIdle = SimpleProperty.getProperty("jedis_minIdle");
	public static String maxWait = SimpleProperty.getProperty("jedis_maxWait");

	public static String minEvictableIdleTimeMillis = SimpleProperty.getProperty("jedis_minEvictableIdleTimeMillis");
	public static String timeBetweenEvictionRunsMillis = SimpleProperty.getProperty("jedis_timeBetweenEvictionRunsMillis");

}
