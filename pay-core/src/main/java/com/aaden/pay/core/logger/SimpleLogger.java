package com.aaden.pay.core.logger;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  @Description 日志工具
 *  @author aaden
 *  @date 2017年12月21日
 */
public class SimpleLogger {

	private Logger simpleLogger = null;

	static class SingletonHolder {
		static SimpleLogger instance = new SimpleLogger();
	}

	public static SimpleLogger getLogger() {
		return SingletonHolder.instance;
	}

	public static SimpleLogger getLogger(Class<?> clazz) {
		return new SimpleLogger(clazz);
	}

	private SimpleLogger() {
		simpleLogger = LoggerFactory.getLogger("aaden_logger");
	}

	private SimpleLogger(Class<?> clazz) {
		simpleLogger = LoggerFactory.getLogger(clazz);
	}

	public void error(String message) {
		simpleLogger.error(message);
	}

	public void error(String message, Throwable t) {
		simpleLogger.error(message, t);
	}

	public void error(String format, Object arg1, Object arg2) {
		simpleLogger.error(format, arg1, arg2);
	}

	public void error(String format, Object... args) {
		simpleLogger.error(format, args);
	}

	public void debug(String message) {
		simpleLogger.debug(message);
	}

	public void debug(String message, Throwable t) {
		simpleLogger.debug(message, t);
	}

	public void warn(String message) {
		simpleLogger.warn(message);
	}

	public void warn(String message, Throwable t) {
		simpleLogger.warn(message, t);
	}

	public void info(String message) {
		simpleLogger.info(message);
	}

	public void info(String message, Throwable t) {
		simpleLogger.info(message, t);
	}

	public void info(String format, Object arg) {
		simpleLogger.info(format, arg);
	}

	public void info(String format, Object arg1, Object arg2) {
		simpleLogger.info(format, arg1, arg2);
	}

	public void info(String format, Object[] argArray) {
		simpleLogger.info(format, argArray);
	}

	public boolean isDebugEnabled() {
		return simpleLogger.isDebugEnabled();
	}
}
