package com.aaden.pay.core.redis;

import redis.clients.jedis.Jedis;

/**
 * @Description redis分布式锁实现
 * @author aaden
 * @date 2017年12月19日
 */
public class JedisLock {

	Jedis jedis;

	/**
	 * Lock key path.
	 */
	String lockKey;

	/**
	 * Lock expiration in seconds.
	 */
	int expireMsecs = 60; // 锁超时时间,防止线程在入锁以后出错导致无限的等待

	/**
	 * Acquire timeout in seconds.
	 */
	int timeoutMsecs = 30; // 锁等待时间

	volatile boolean locked = Boolean.FALSE;

	/**
	 * Detailed constructor with default acquire timeout 30 secs and lock
	 * expiration of 60 secs.
	 * 
	 * @param jedis
	 * @param lockKey
	 *            lock key (ex. account:1, ...)
	 */
	public JedisLock(Jedis jedis, String lockKey) {
		this.jedis = jedis;
		this.lockKey = lockKey;
	}

	/**
	 * Detailed constructor with default lock expiration of 30 secs.
	 * 
	 * @param jedis
	 * @param lockKey
	 *            lock key (ex. account:1, ...)
	 * @param timeoutSecs
	 *            acquire timeout in secs. (default: 60 secs.)
	 */
	public JedisLock(Jedis jedis, String lockKey, int timeoutMsecs) {
		this(jedis, lockKey);
		this.timeoutMsecs = timeoutMsecs;
	}

	/**
	 * Detailed constructor.
	 * 
	 * @param jedis
	 * @param lockKey
	 *            lock key (ex. account:1, ...)
	 * @param timeoutSecs
	 *            acquire timeout in seconds (default: 30 secs)
	 * @param expireMsecs
	 *            lock expiration in seconds (default: 60 secs)
	 */
	public JedisLock(Jedis jedis, String lockKey, int timeoutMsecs, int expireMsecs) {
		this(jedis, lockKey, timeoutMsecs);
		this.expireMsecs = expireMsecs;
	}

	public JedisLock(String lockKey) {
		this(null, lockKey);
	}

	public JedisLock(String lockKey, int timeoutMsecs) {
		this(null, lockKey, timeoutMsecs);
	}

	public JedisLock(String lockKey, int timeoutMsecs, int expireMsecs) {
		this(null, lockKey, timeoutMsecs, expireMsecs);
	}

	/**
	 * @return lock key
	 */
	public String getLockKey() {
		return lockKey;
	}

	/**
	 * Acquire lock.
	 * 
	 * @param jedis
	 * @return true if lock is acquired, false acquire timeouted
	 * @throws InterruptedException
	 *             in case of thread interruption
	 */
	public synchronized boolean acquire() throws InterruptedException {
		return acquire(jedis);
	}

	/**
	 * Acquire lock.
	 * 
	 * @param jedis
	 * @return true if lock is acquired, false acquire timeouted
	 * @throws InterruptedException
	 *             in case of thread interruption
	 */
	public synchronized boolean acquire(Jedis jedis) throws InterruptedException {
		int timeout = timeoutMsecs * 1000;
		while (timeout >= 0) {
			if (jedis.setnx(lockKey, lockKey) == 1) {
				// lock acquired
				locked = Boolean.TRUE;
				jedis.setex(lockKey, expireMsecs, lockKey);
				return locked;
			}
			timeout -= 100;
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
		}
		return false;
	}

	/**
	 * Acqurired lock release.
	 */
	public synchronized void release() {
		release(jedis);
	}

	/**
	 * Acqurired lock release.
	 */
	public synchronized void release(Jedis jedis) {
		if (locked) {
			jedis.del(lockKey);
			locked = !locked;
		}
	}
}
