package com.aaden.pay.service.biz.tp.baofoo.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.aaden.pay.core.logger.SimpleLogger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**
 *  @Description 宝付工具类
 *  @author aaden
 *  @date 2017年12月25日
 */
@SuppressWarnings("restriction")
public class BaofooSecurityUtil {
	
	private static SimpleLogger logger = SimpleLogger.getLogger(BaofooSecurityUtil.class);
	/***
	 * MD5 加密
	 */
	public static String MD5(String str) {
		if (str == null)
			return null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(str.getBytes("UTF-8"));
			byte[] digest = md5.digest();
			StringBuffer hexString = new StringBuffer();
			String strTemp;
			for (int i = 0; i < digest.length; i++) {
				strTemp = Integer.toHexString((digest[i] & 0x000000FF) | 0xFFFFFF00).substring(6);
				hexString.append(strTemp);
			}
			return hexString.toString();
		} catch (Exception e) {
			logger.error("baofoo create MD5 exception", e);
		}
		return str;
	}

	// ==Base64加解密==================================================================
	/**
	 * Base64加密
	 */
	public static String Base64Encode(String str) throws UnsupportedEncodingException {
		return new BASE64Encoder().encode(str.getBytes("UTF-8"));
	}

	/**
	 * 解密
	 */
	public static String Base64Decode(String str) throws UnsupportedEncodingException, IOException {
//		str = str.replaceAll(" ", "+");
		return new String(new BASE64Decoder().decodeBuffer(str), "UTF-8");
	}

	// ==Aes加解密==================================================================
	/**
	 * aes解密-128位
	 */
	public static String AesDecrypt(String encryptContent, String password) {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(password.getBytes());
			keyGen.init(128, secureRandom);
			SecretKey secretKey = keyGen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			return new String(cipher.doFinal(hex2Bytes(encryptContent)));
		} catch (Exception e) {
			logger.error("AesDecrypt exception", e);
			return null;
		}
	}

	/**
	 * aes加密-128位
	 */
	public static String AesEncrypt(String content, String password) {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(password.getBytes());
			keyGen.init(128, secureRandom);
			SecretKey secretKey = keyGen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return byte2Hex(cipher.doFinal(content.getBytes("UTF-8")));
		} catch (Exception e) {
			logger.error("AesEncrypt exception", e);
			return null;
		}
	}

	/**
	 * 将byte[] 转换成字符串
	 */
	public static String byte2Hex(byte[] srcBytes) {
		StringBuilder hexRetSB = new StringBuilder();
		for (byte b : srcBytes) {
			String hexString = Integer.toHexString(0x00ff & b);
			hexRetSB.append(hexString.length() == 1 ? 0 : "").append(hexString);
		}
		return hexRetSB.toString();
	}

	/**
	 * 将16进制字符串转为转换成字符串
	 */
	public static byte[] hex2Bytes(String source) {
		byte[] sourceBytes = new byte[source.length() / 2];
		for (int i = 0; i < sourceBytes.length; i++) {
			sourceBytes[i] = (byte) Integer.parseInt(source.substring(i * 2, i * 2 + 2), 16);
		}
		return sourceBytes;
	}

	/**
	 * DES加密
	 */
	public static String desEncrypt(String source, String desKey) throws Exception {
		try {
			// 从原始密匙数据创建DESKeySpec对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(new DESKeySpec(desKey.getBytes()));
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, securekey);
			// 现在，获取数据并加密
			byte[] destBytes = cipher.doFinal(source.getBytes());
			StringBuilder hexRetSB = new StringBuilder();
			for (byte b : destBytes) {
				String hexString = Integer.toHexString(0x00ff & b);
				hexRetSB.append(hexString.length() == 1 ? 0 : "").append(hexString);
			}
			return hexRetSB.toString();
		} catch (Exception e) {
			throw new Exception("DES加密发生错误", e);
		}
	}

	/**
	 * DES解密
	 */
	public static String desDecrypt(String source, String desKey) throws Exception {
		// 解密数据
		byte[] sourceBytes = new byte[source.length() / 2];
		for (int i = 0; i < sourceBytes.length; i++) {
			sourceBytes[i] = (byte) Integer.parseInt(source.substring(i * 2, i * 2 + 2), 16);
		}
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(new DESKeySpec(desKey.getBytes()));
			Cipher cipher = Cipher.getInstance("DES");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.DECRYPT_MODE, securekey);
			// 现在，获取数据并解密
			byte[] destBytes = cipher.doFinal(sourceBytes);
			return new String(destBytes);
		} catch (Exception e) {
			throw new Exception("DES解密发生错误", e);
		}
	}

	/**
	 * 3DES加密
	 */
	public static byte[] threeDesEncrypt(byte[] src, byte[] keybyte) throws Exception {
		try {
			// 生成密钥
			byte[] key = new byte[24];
			if (keybyte.length < key.length) {
				System.arraycopy(keybyte, 0, key, 0, keybyte.length);
			} else {
				System.arraycopy(keybyte, 0, key, 0, key.length);
			}
			SecretKey deskey = new SecretKeySpec(key, "DESede");
			// 加密
			Cipher c1 = Cipher.getInstance("DESede/ECB/PKCS5Padding");
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (Exception e) {
			throw new Exception("3DES加密发生错误", e);
		}
	}

	/**
	 * 3DES解密
	 */
	public static byte[] threeDesDecrypt(byte[] src, byte[] keybyte) throws Exception {
		try {
			// 生成密钥
			byte[] key = new byte[24];
			if (keybyte.length < key.length) {
				System.arraycopy(keybyte, 0, key, 0, keybyte.length);
			} else {
				System.arraycopy(keybyte, 0, key, 0, key.length);
			}
			SecretKey deskey = new SecretKeySpec(key, "DESede");
			// 解密
			Cipher c1 = Cipher.getInstance("DESede/ECB/PKCS5Padding");
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (Exception e) {
			throw new Exception("3DES解密发生错误", e);
		}
	}

	/**
	 * 3DES加密
	 */
	public static String threeDesEncrypt(String src, String key) throws Exception {
		return byte2Hex(threeDesEncrypt(src.getBytes(), key.getBytes()));
	}

	/**
	 * 3DES加密
	 */
	public static String threeDesDecrypt(String src, String key) throws Exception {
		return new String(threeDesDecrypt(hex2Bytes(src), key.getBytes()));
	}

	public static void main(String[] args) throws Exception {
		String str = "数据加密的基本过程就是对原来为明文的文件或数据按某种算法进行处理，使其成为不可读的一段代码，通常称为“密文”，" 
				+ "使其只能在输入相应的密钥之后才能显示出本来内容，通过这样的途径来达到保护数据不被非法人窃取、阅读的目的。 " 
				+ "该过程的逆过程为解密，即将该编码信息转化为其原来数据的过程。";
		str+=str;str+=str;str+=str;
		String PWD = "SecurityUtil.PWD";
		System.out.println("原文:[" + str.length() + "]" + str);
		System.out.println("==MD5===============");
		System.out.println(MD5(str));
		System.out.println("==Base64============");
		String strBase64 = Base64Encode(str);
		System.out.println("加密:[" + strBase64.length() + "]" + strBase64);
		System.out.println("解密:" + Base64Decode(strBase64));
		System.out.println("==Aes============");
		String strAes = AesEncrypt(str, PWD);
		System.out.println("加密:[" + strAes.length() + "]" + strAes);
		System.out.println("解密:" + AesDecrypt(strAes, PWD));
		System.out.println("==Des==============");
		String strDes = desEncrypt(str, PWD);
		System.out.println("加密:[" + strDes.length() + "]" + strDes);
		System.out.println("解密:" + desDecrypt(strDes, PWD));
		System.out.println("==3Des==============");
		String str3Des = threeDesEncrypt(str, PWD);
		System.out.println("加密:[" + str3Des.length() + "]" + str3Des);
		System.out.println("解密:" + threeDesDecrypt(str3Des, PWD));
		
		//==========================================
		
		long t1=System.currentTimeMillis();   
		for (int i = 0; i < 10000; i++) 
			MD5(str);
		System.out.println("\nMD5:"+(System.currentTimeMillis()-t1));
		t1=System.currentTimeMillis();   
		for (int i = 0; i < 10000; i++) 
			Base64Encode(str);
		System.out.println("Base64:"+(System.currentTimeMillis()-t1));
		t1=System.currentTimeMillis();   
		for (int i = 0; i < 10000; i++) 
			AesEncrypt(str, PWD);
		System.out.println("Aes:"+(System.currentTimeMillis()-t1));
		t1=System.currentTimeMillis();   
		for (int i = 0; i < 10000; i++) 
			desEncrypt(str, PWD);
		System.out.println("Des:"+(System.currentTimeMillis()-t1));
		t1=System.currentTimeMillis();   
		for (int i = 0; i < 10000; i++) 
			threeDesEncrypt(str, PWD);
		System.out.println("3Des:"+(System.currentTimeMillis()-t1));
		//=======================================
		t1=System.currentTimeMillis();   
		for (int i = 0; i < 10000; i++) 
			Base64Decode(strBase64);
		System.out.println("\nBase64:"+(System.currentTimeMillis()-t1));
		t1=System.currentTimeMillis();   
		for (int i = 0; i < 10000; i++) 
			AesDecrypt(strAes, PWD);
		System.out.println("Aes:"+(System.currentTimeMillis()-t1));
		t1=System.currentTimeMillis();   
		for (int i = 0; i < 10000; i++) 
			desDecrypt(strDes, PWD);
		System.out.println("Des:"+(System.currentTimeMillis()-t1));
		t1=System.currentTimeMillis();   
		for (int i = 0; i < 10000; i++) 
			threeDesDecrypt(str3Des, PWD);
		System.out.println("3Des:"+(System.currentTimeMillis()-t1));

		
	}

}
