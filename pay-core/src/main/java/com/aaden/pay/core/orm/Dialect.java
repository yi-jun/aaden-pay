package com.aaden.pay.core.orm;

/**
 *  @Description 方言接口各种数据库可实现自己的实现类
 *  @author aaden
 *  @date 2017年12月24日
 */
public interface Dialect {

	boolean supportsLimit();

	boolean supportsLimitOffset();

	String getLimitString(String sql, int offset, int limit);

}
