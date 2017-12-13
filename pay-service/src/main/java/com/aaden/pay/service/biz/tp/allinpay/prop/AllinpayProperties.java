package com.aaden.pay.service.biz.tp.allinpay.prop;

import com.aaden.pay.core.prop.SimpleProperty;

/**
 *  @Description 通联配置
 *  @author aaden
 *  @date 2017年12月27日
 */
public class AllinpayProperties {

	public static String auth_daifu_business = SimpleProperty.getProperty("allinpay_auth_payagent_busicode");
	public static String auth_daikou_business = SimpleProperty.getProperty("allinpay_auth_deduct_busicode");
	public static String auth_merchantId = SimpleProperty.getProperty("allinpay_auth_merchantId");
	public static String auth_url = SimpleProperty.getProperty("allinpay_auth_payurl");
	public static String auth_check_url = SimpleProperty.getProperty("allinpay_auth_checkurl");
	public static String auth_pfxPath = SimpleProperty.getProperty("allinpay_auth_pfxpath");
	public static String auth_pfxPassword = SimpleProperty.getProperty("allinpay_auth_pfxpassword");
	public static String auth_cerPath = SimpleProperty.getProperty("allinpay_auth_cerpath");
	public static String auth_username = SimpleProperty.getProperty("allinpay_auth_username");
	public static String auth_password = SimpleProperty.getProperty("allinpay_auth_password");

	public static String gateway_url = SimpleProperty.getProperty("allinpay_gateway_url");
	public static String gateway_check_url = SimpleProperty.getProperty("allinpay_gateway_check_url");
	public static String gateway_privateKey = SimpleProperty.getProperty("allinpay_gateway_private_key");
	public static String gateway_certPath = SimpleProperty.getProperty("allinpay_gateway_cert_path");
	public static String gateway_signType = SimpleProperty.getProperty("allinpay_gateway_sign_type");
	public static String gateway_merchantId = SimpleProperty.getProperty("allinpay_gateway_merchantId");
	public static String gateway_callbank = SimpleProperty.getProperty("allinpay_gateway_callback");
	public static String gateway_asynCallback = SimpleProperty.getProperty("allinpay_gateway_asyn_callback");

}
