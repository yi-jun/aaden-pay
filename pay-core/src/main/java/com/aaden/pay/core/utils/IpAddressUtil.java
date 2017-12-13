package com.aaden.pay.core.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.aaden.pay.core.httpclient.HttpClientHelper;
import com.aaden.pay.core.httpclient.exception.HttpClientException;
import com.aaden.pay.core.logger.SimpleLogger;

/**
 *  @Description 通过解析访问地址
 *  @author aaden
 *  @date 2017年12月24日
 */
public class IpAddressUtil {

	protected static SimpleLogger logger = SimpleLogger.getLogger(IpAddressUtil.class);

	/**
	 * 获取用户登录IP
	 * 
	 * @param request
	 * @return String
	 */
	public static String getIpAddr(HttpServletRequest request) {
		if (request == null) {
			return "unknown";
		}

		String ip = request.getHeader("x-forwarded-for");

		ip = getIpAddr(ip, request, "X-Forwarded-For");
		ip = getIpAddr(ip, request, "Proxy-Client-IP");
		ip = getIpAddr(ip, request, "X-Real-IP");
		ip = getIpAddr(ip, request, "WL-Proxy-Client-IP");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		if (ip.equals("0:0:0:0:0:0:0:1")) {
			ip = "127.0.0.1";
		}

		if (ip.split(",").length > 1) {
			ip = ip.split(",")[0];
		}

		return ip;
	}

	private static String getIpAddr(String ip, HttpServletRequest request, String headerParam) {
		String tempIp = request.getHeader(headerParam) == null ? "" : request.getHeader(headerParam);
		logger.debug("#######" + headerParam + "#######:" + tempIp);
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = tempIp;
		}
		return ip;
	}

	/**
	 * 获取外网地址
	 */
	public static String getInternetIp() {
		int tryCount = 2;
		for (int i = 0; i < tryCount; i++) {
			String ret = parseIp138();
			if (!StringUtils.isEmpty(ret))
				return ret;
			ret = parseYY();
			if (!StringUtils.isEmpty(ret))
				return ret;
		}
		return "";
	}

	private static String parseIp138() {
		String url = "http://2017.ip138.com/ic.asp";
		String resp = null;
		try {
			resp = HttpClientHelper.getInstance().sendHttpGet(url, null);
		} catch (HttpClientException e) {
			return null;
		}
		if (StringUtils.isEmpty(resp))
			return null;

		int body = resp.indexOf("<body");
		if (body > 0)
			resp = resp.substring(body);

		// 您的IP是：[xxx] 来自：xx省xx市 联通
		int start = resp.indexOf("[") + 1;
		if (start < 0)
			return null;

		int end = resp.indexOf("]", start);
		return resp.substring(start, end);
	}

	private static String parseYY() {
		String url = "https://ipip.yy.com/get_ip_info.php";
		String resp = null;
		try {
			resp = HttpClientHelper.getInstance().sendHttpGet(url, null);
		} catch (Exception e) {
			return null;
		}
		if (StringUtils.isEmpty(resp))
			return null;
		// returnInfo={"cip":"xx","cname":"xx","country":"xx","province":"xx","city":"xx","isp":"xx"};
		int start = resp.indexOf("cip\":") + 6;
		if (start < 0)
			return null;
		int end = resp.indexOf("\"", start);
		return resp.substring(start, end);
	}
}
