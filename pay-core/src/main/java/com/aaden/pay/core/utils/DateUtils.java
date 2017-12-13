package com.aaden.pay.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.aaden.pay.core.logger.SimpleLogger;

/**
 * @Description 日期工具类
 * @author aaden
 * @date 2017年12月8日
 */
public class DateUtils {

	private static SimpleLogger logger = SimpleLogger.getLogger(DateUtils.class);

	final static long daytimes = 24 * 60 * 60 * 1000;

	final static long hourtimes = 1 * 60 * 60 * 1000;

	public static Date parseAuto(String source) {
		SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dfsFull = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat cfs = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		if (StringUtils.isBlank(source))
			return null;
		try {
			if (source.indexOf('-') > 0) {// 以"-"分隔
				if (source.indexOf(':') > 0) {
					return datetimeFormat.parse(source);
				} else {
					return dateFormat.parse(source);
				}
			}
			if (source.indexOf('/') > 0) {// 以"/"分隔
				if (source.indexOf(':') > 0) {
					return dfsFull.parse(source);
				} else {
					return dfs.parse(source);
				}
			}
			try {
				return cfs.parse(source);
			} catch (Exception e) {
				return df.parse(source);
			}
		} catch (Exception e) {
			logger.error("格式化异常", e);
			return null;
		}
	}

	/**
	 * 获得当前日期
	 * <p>
	 * 日期格式yyyy-MM-dd
	 * 
	 * @return String
	 */
	private static String currentDate() {
		return new SimpleDateFormat("yyyy-MM-dd").format(today());
	}

	/**
	 * 获得当前时间的<code>java.util.Date</code>对象
	 */
	public static Date today() {
		return new Date();
	}

	/**
	 * 获取格式化之后的当前日期 日期格式yyyy-MM-dd
	 */
	public static Date todayFormat() {
		try {
			return parseDate(currentDate());
		} catch (Exception e) {
			logger.error("格式化异常", e);
			return new Date();
		}
	}

	/**
	 * 格式化日期
	 * <p>
	 * 日期格式yyyy-MM-dd
	 * 
	 */
	public static String formatDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}

	/**
	 * 格式化日期
	 * <p>
	 * 日期格式yyyyMMdd
	 * 
	 */
	public static String df(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		return df.format(date);
	}

	/**
	 * 格式化日期
	 * <p>
	 * 日期格式yyyyMMDDhhmmss
	 * 
	 */
	public static String cfs(Date date) {
		SimpleDateFormat cfs = new SimpleDateFormat("yyyyMMddHHmmss");
		return cfs.format(date);
	}

	/**
	 * 将字符串日期转换成java.util.Date类型
	 * <p>
	 * 日期时间格式yyyy-MM-dd
	 * 
	 */
	private static Date parseDate(String date) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd").parse(date);
	}

	/**
	 * 判断是否超时
	 * 
	 * @param startDate
	 *            开始时间
	 * @param intervalTime
	 *            间隔时间，以毫秒为单位
	 */
	public static boolean isTimeOut(Date startDate, long intervalTime) {
		boolean ret = false;
		Date currentDate = new Date();
		long minuteDif = currentDate.getTime() - startDate.getTime();
		if (minuteDif >= intervalTime) {
			ret = true;
		}
		return ret;
	}

}
