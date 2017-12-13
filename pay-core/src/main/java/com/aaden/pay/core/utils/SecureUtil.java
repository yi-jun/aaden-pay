package com.aaden.pay.core.utils;

import org.apache.commons.codec.binary.Base64;

import com.aaden.pay.core.logger.SimpleLogger;

/**
 *  @Description 加解密工具类
 *  @author aaden
 *  @date 2017年12月27日
 */
public final class SecureUtil {

	static SimpleLogger logger = SimpleLogger.getLogger(SecureUtil.class);

	// Base64加密
	public static String encodeCard(String str) {
		if (str == null)
			return "";
		byte[] base64Byte = null;
		String base64Str = "";
		try {
			base64Byte = Base64.encodeBase64(str.getBytes("utf-8"), true);
			base64Str = new String(base64Byte);
		} catch (Exception e) {
			logger.error("加密数据异常:", e);
		}
		return base64Str;
	}

	// Base64解密
	public static String decodeCard(String str) {
		if (str == null)
			return "";
		byte[] base64ByteBack = null;
		String base64StrBack = "";
		try {
			base64ByteBack = Base64.decodeBase64(str.getBytes("utf-8"));
			base64StrBack = new String(base64ByteBack);
		} catch (Exception e) {
			logger.error("解密数据异常:", e);
		}
		return base64StrBack;
	}

	public static void main(String[] args) {
		String s = "you are right";
		String enc = encodeCard(s);
		String dec = decodeCard(enc);
		System.out.println("加密后:" + enc);
		System.out.println("解密后:" + dec);
	}

}
