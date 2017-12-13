package com.aaden.pay.service.biz.properties;

import com.aaden.pay.core.prop.SimpleProperty;

/**
 *  @Description 支付配置
 *  @author aaden
 *  @date 2017年12月11日
 */
public class PaymentProperties {
	// 网银回调网站根域名
	public static String callback_domain = SimpleProperty.getProperty("callback_domain");
	// 对账文件存储目录
	public static String check_file_directory = SimpleProperty.getProperty("check_file_directory");
}
