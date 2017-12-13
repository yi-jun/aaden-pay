package com.aaden.pay.service.resouce.lianlian;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description 连连工具类
 * @author aaden
 * @date 2017年12月23日
 */
public class LianlianMd5Algorithm {

	static class InstanceHolder {
		static LianlianMd5Algorithm instance = new LianlianMd5Algorithm();
	}

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	private LianlianMd5Algorithm() {
	}

	public static LianlianMd5Algorithm getInstance() {
		return InstanceHolder.instance;
	}

	/**
	 * 转换字节数组�?6进制字串
	 * 
	 * @param b
	 *            字节数组
	 * @return 16进制字串
	 */
	private String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	/**
	 * 转换字节数组为高位字符串
	 * 
	 * @param b
	 *            字节数组
	 * @return
	 */
	private String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * MD5 摘要计算(byte[]).
	 * 
	 * @param src
	 *            byte[]
	 * @throws Exception
	 * @return String
	 */
	public String md5Digest(byte[] src) {
		MessageDigest alg;
		try {
			alg = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		return byteArrayToHexString(alg.digest(src));
	}
}
