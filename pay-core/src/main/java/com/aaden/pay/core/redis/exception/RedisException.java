package com.aaden.pay.core.redis.exception;

/**
 *  @Description redis缓存存储异常
 *  @author aaden
 *  @date 2017年12月24日
 */
public class RedisException extends Exception {

	private static final long serialVersionUID = 5116529467330605012L;

	public RedisException() {
		super();
	}

	public RedisException(String msg) {
		super(msg);
	}

	public RedisException(Throwable cause) {
		super(cause);
	}

	public RedisException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
