package com.aaden.pay.service.resouce.lianlian;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aaden.pay.core.logger.SimpleLogger;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 *  @Description 连连工具类
 *  @author aaden
 *  @date 2017年12月5日
 */
public class LianlianPayUtil {
	private static SimpleLogger logger = SimpleLogger.getLogger(LianlianPayUtil.class);
	/**
	 * str空判断
	 * 
	 * @param str
	 * @return
	 * @author guoyx
	 */
	public static boolean isnull(String str) {
		if (null == str || str.equalsIgnoreCase("null") || str.equals("")) {
			return true;
		} else
			return false;
	}


	/**
	 * 生成待签名串
	 * 
	 * @param paramMap
	 * @return
	 * @author guoyx
	 */
	public static String genSignData(JSONObject jsonObject) {
		StringBuffer content = new StringBuffer();

		// 按照key做首字母升序排列
		List<String> keys = new ArrayList<String>(jsonObject.keySet());
		Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			if ("sign".equals(key)) {
				continue;
			}
			String value = jsonObject.getString(key);
			// 空串不参与签名
			if (isnull(value)) {
				continue;
			}
			content.append((i == 0 ? "" : "&") + key + "=" + value);

		}
		String signSrc = content.toString();
		if (signSrc.startsWith("&")) {
			signSrc = signSrc.replaceFirst("&", "");
		}
		System.out.println(signSrc);
		return signSrc;
	}

	/**
	 * 加签
	 * 
	 * @param reqObj
	 * @param rsa_private
	 * @param md5_key
	 * @return
	 * @author guoyx
	 */
	public static String addSign(JSONObject reqObj, String rsa_private, String md5_key) {
		if (reqObj == null) {
			return "";
		}
		String sign_type = reqObj.getString("sign_type");
		if (LianlianSignTypeEnum.MD5.getCode().equals(sign_type)) {
			return addSignMD5(reqObj, md5_key);
		} else {
			return addSignRSA(reqObj, rsa_private);
		}
	}

	/**
	 * 签名验证
	 * 
	 * @param reqStr
	 * @return
	 */
	public static boolean checkSign(String reqStr, String rsa_public, String md5_key) {
		JSONObject reqObj = JSON.parseObject(reqStr);
		if (reqObj == null) {
			return false;
		}
		String sign_type = reqObj.getString("sign_type");
		if (LianlianSignTypeEnum.MD5.getCode().equals(sign_type)) {
			return checkSignMD5(reqObj, md5_key);
		} else {
			return checkSignRSA(reqObj, rsa_public);
		}
	}

	/**
	 * RSA签名验证
	 * 
	 * @param reqObj
	 * @return
	 * @author guoyx
	 */
	private static boolean checkSignRSA(JSONObject reqObj, String rsa_public) {
		if (reqObj == null) {
			return false;
		}
		String sign = reqObj.getString("sign");
		// 生成待签名串
		String sign_src = genSignData(reqObj);
		try {
			if (LianlianTraderRSAUtil.checksign(rsa_public, sign_src, sign)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * MD5签名验证
	 * 
	 * @param signSrc
	 * @param sign
	 * @return
	 * @author guoyx
	 */
	private static boolean checkSignMD5(JSONObject reqObj, String md5_key) {
		if (reqObj == null) {
			return false;
		}
		String sign = reqObj.getString("sign");
		// 生成待签名串
		String sign_src = genSignData(reqObj);
		sign_src += "&key=" + md5_key;
		try {
			if (sign.equals(LianlianMd5Algorithm.getInstance().md5Digest(sign_src.getBytes("utf-8")))) {
				return true;
			} else {
				return false;
			}
		} catch (UnsupportedEncodingException e) {
			return false;
		}
	}

	/**
	 * RSA加签名
	 * 
	 * @param reqObj
	 * @param rsa_private
	 * @return
	 * @author guoyx
	 */
	private static String addSignRSA(JSONObject reqObj, String rsa_private) {
		if (reqObj == null) {
			return "";
		}
		// 生成待签名串
		String sign_src = genSignData(reqObj);
		logger.info("连连RSA签名原串数据:"+sign_src);
		try {
			return LianlianTraderRSAUtil.sign(rsa_private, sign_src);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * MD5加签名
	 * 
	 * @param reqObj
	 * @param md5_key
	 * @return
	 * @author guoyx
	 */
	private static String addSignMD5(JSONObject reqObj, String md5_key) {
		if (reqObj == null) {
			return "";
		}
		// 生成待签名串
		String sign_src = genSignData(reqObj);
		sign_src += "&key=" + md5_key;
		logger.info("连连MD5签名原串数据:"+sign_src);
		try {
			return LianlianMd5Algorithm.getInstance().md5Digest(sign_src.getBytes("utf-8"));
		} catch (Exception e) {
			return "";
		}
	}
//
//	/**
//	 * 读取request流
//	 * 
//	 * @param req
//	 * @return
//	 * @author guoyx
//	 */
//	public static String readReqStr(HttpServletRequest request) {
//		BufferedReader reader = null;
//		StringBuilder sb = new StringBuilder();
//		try {
//			reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
//			String line = null;
//
//			while ((line = reader.readLine()) != null) {
//				sb.append(line);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (null != reader) {
//					reader.close();
//				}
//			} catch (IOException e) {
//
//			}
//		}
//		return sb.toString();
//	}
}
