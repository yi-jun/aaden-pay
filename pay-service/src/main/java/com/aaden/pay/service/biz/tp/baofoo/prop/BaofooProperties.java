package com.aaden.pay.service.biz.tp.baofoo.prop;

import com.aaden.pay.core.prop.SimpleProperty;

/**
 *  @Description 宝付配置
 *  @author aaden
 *  @date 2017年12月25日
 */
public class BaofooProperties {

	public static String check_url = SimpleProperty.getProperty("baofoo_check_url");

	public static String pay_data_type = SimpleProperty.getProperty("baofoo_pay_data_type");
	public static String pay_prx_key = SimpleProperty.getProperty("baofoo_pay_prx_key");
	public static String pay_key_password = SimpleProperty.getProperty("baofoo_pay_key_password");
	public static String pay_pub_key = SimpleProperty.getProperty("baofoo_pay_pub_key");
	public static String pay_terminal_id = SimpleProperty.getProperty("baofoo_pay_terminal_id");
	public static String pay_member_id = SimpleProperty.getProperty("baofoo_pay_member_id");
	public static String pay_request_domain = SimpleProperty.getProperty("baofoo_pay_request_domain");

	public static String auth_char_set = SimpleProperty.getProperty("baofoo_auth_char_set");
	public static String auth_pfx_name = SimpleProperty.getProperty("baofoo_auth_pfx_name");
	public static String auth_pfx_pwd = SimpleProperty.getProperty("baofoo_auth_pfx_pwd");
	public static String auth_cer_name = SimpleProperty.getProperty("baofoo_auth_cer_name");
	public static String auth_terminal_id = SimpleProperty.getProperty("baofoo_auth_terminal_id");
	public static String auth_member_id = SimpleProperty.getProperty("baofoo_auth_member_id");
	public static String auth_data_type = SimpleProperty.getProperty("baofoo_auth_data_type");
	public static String auth_request_url = SimpleProperty.getProperty("baofoo_auth_request_url");

}
