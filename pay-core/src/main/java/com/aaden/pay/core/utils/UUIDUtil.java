package com.aaden.pay.core.utils;

import java.util.UUID;

/**
 *  @Description 生成随机主键
 *  @author aaden
 *  @date 2017年12月8日
 */
public final class UUIDUtil {

	/**
	 * 生成随机UUID
	 * 
	 * @return String
	 */
	public static String randomUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
