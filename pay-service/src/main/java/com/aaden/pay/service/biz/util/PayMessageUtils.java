package com.aaden.pay.service.biz.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.utils.FileUtils;

/**
 *  @Description 自定义友好支付消息工具类
 *  @author aaden
 *  @date 2017年12月23日
 */
public class PayMessageUtils {
	static SimpleLogger logger = SimpleLogger.getLogger(PayMessageUtils.class);

	/** 未能连接或返回为空的消息提示模板 */
	static final String NULL_MSG = "系统繁忙，请稍后再试，或联系客服。";

	static String allinPath = "label/allinpay/allinpayMessage.properties";
	static String baofooPath = "label/baofoo/baofooMessage.properties";

	static Properties allinProperties;
	static Properties baofooProperties;

	static {
		try {
			loadProperties();
		} catch (Exception e) {
			logger.error("加载自定义支付消息失败:", e);
		}
	}

	public static String getFriendlyMsg(ThirdPayRecord payRecord) {

		PayChannel payChannel = payRecord.getPayChannel();
		String code = payRecord.getPayCode();
		String defaultMsg = payRecord.getPayMessage();

		return getFriendlyMsg(payChannel, code, defaultMsg);
	}
	
	public static String getFriendlyMsg(ThirdBankSend bankSend) {
		PayChannel payChannel = bankSend.getChannel();
		String code = bankSend.getReturnCode();
		String defaultMsg = bankSend.getReturnMsg();
		return getFriendlyMsg(payChannel, code, defaultMsg);
	}

	private static String getFriendlyMsg(PayChannel payChannel, String code, String defaultMsg) {
		if (code == null) {
			return NULL_MSG;
		}
		String value = null;
		if (payChannel == PayChannel.ALLIN) {
			value = allinProperties.getProperty(code);
		} else if (payChannel == PayChannel.BAOFOO) {
			value = baofooProperties.getProperty(code);
		}
		return StringUtils.isBlank(value) ? defaultMsg : value;
	}
	


	static void loadProperties() throws Exception {
		allinProperties = new Properties();
		baofooProperties = new Properties();

		// 通联
		String path = FileUtils.getClassOrSystemPath(allinPath);
		if (path == null) {
			logger.warn(allinPath + " 路径不存在");
		} else {
			InputStream inputStream = new FileInputStream(path);
			allinProperties.load(new InputStreamReader(inputStream, "UTF-8"));
		}
		// 宝付
		path = FileUtils.getClassOrSystemPath(baofooPath);
		if (path == null) {
			logger.warn(baofooPath + " 路径不存在");
		} else {
			InputStream inputStream = new FileInputStream(path);
			baofooProperties.load(new InputStreamReader(inputStream, "UTF-8"));
		}
	}


}
