package com.aaden.pay.service.biz.tp.baofoo.util;

/**
 *  @Description 宝付工具类
 *  @author aaden
 *  @date 2017年12月21日
 */
public final class BaofooRsaConst {

	/** 编码 */
	public final static String ENCODE = "UTF-8";

	public final static String KEY_X509 = "X509";
	public final static String KEY_PKCS12 = "PKCS12";
	public final static String KEY_ALGORITHM = "RSA";
	public final static String CER_ALGORITHM = "MD5WithRSA";

	public final static String RSA_CHIPER = "RSA/ECB/PKCS1Padding";

	public final static int KEY_SIZE = 1024;
	/** 1024bit 加密块 大小 */
	public final static int ENCRYPT_KEYSIZE = 117;
	/** 1024bit 解密块 大小 */
	public final static int DECRYPT_KEYSIZE = 128;
}
