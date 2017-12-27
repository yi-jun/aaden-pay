package com.aaden.pay.api.biz.constants;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.PayChannel;

/**
 *  @Description 支付常量
 *  @author aaden
 *  @date 2017年12月20日
 */
public class PaymentConstans {

	/**
	 * 支付订单过期时间(分),传递给第三方支付用,主要用于网银支付
	 */
	public static final int PAY_EXPIRE_MINUTE = 50;

	/**
	 * 系统判定订单是否失效的时间(分),考虑时间差的关系,比订单时间延迟一定时间
	 */
	public static final int PAY_TIME_OUT_MINUTE = PAY_EXPIRE_MINUTE + 10;

	/**
	 * 系统判定企业支付订单是否失效的时间(分),企业网银到账慢,经常次日才能查询到成功
	 */
	public static final int COMPANY_PAY_TIME_OUT_MINUTE = 2 * 24 * 60;

	/**
	 * 充值最大支付金额
	 */
	public static final BigDecimal MAX_RECHARGE_AMOUNT = new BigDecimal("5000000");

	/** 网银限额map */
	private static Map<BankType, String> gatewayMap = new HashMap<>();
	/** 代付单笔限额map */
	private static Map<PayChannel, BigDecimal> payoutMap = new HashMap<>();
	
	static {
		gatewayMap.put(BankType.ICBC, "U盾：单笔、单日100万元<br>电子银行口令卡（开通短信认证）：单笔2000元、单日5000元<br>电子银行口令卡（未通短信认证）：单笔500元、单日1000元");
		gatewayMap.put(BankType.ABC, "动态口令卡：单笔1000元、单日3000元<br>移动证书：无限额");
		gatewayMap.put(BankType.BOC, "单笔1万元，单日5万元");
		gatewayMap.put(BankType.CCB, "一代网银盾：单笔5万元、单日10万元<br>二代网银盾：单笔、单日500万元<br>动态口令卡：单笔5000元，单日5000元<br>账号支付：单笔1000元，单日1000元");
		gatewayMap.put(BankType.BOCO, "手机注册版：单笔5000元，单日5000元<br>证书认证版：单笔5万元，单日5万元");
		gatewayMap.put(BankType.PSBC, "个人网银短信客户：单笔5万元<br>手机银行普通客户：单笔、单日2万元<br>手机银行万能版客户：单笔、单日200万元<br>开通个人网银身份认证工具为UK+短信的金卡客户：单笔、单日500万元");
		gatewayMap.put(BankType.CMB, "一卡通支付：单笔5000元、单日1万元<br>一卡通直付：单笔、单日5000元<br>专业版：客户可通过专业版设置支付限额，最高为无限额");
		gatewayMap.put(BankType.CIB, "柜面开通支付功能：网盾或短信口令最高单笔、单日100万元，只有令牌保护单笔、单日5000元<br>网银开通支付功能：网盾最高单笔、单日100万元，只有令牌或短信或令牌＋短信的单笔、单日5000元");
		gatewayMap.put(BankType.CEB, "浏览器器证书：单笔5000元、单日5000元<br>阳光网盾证书：单笔20万元、单日50万元");
		gatewayMap.put(BankType.CITIC, "文件证书：单笔1000元、单日5000元<br>动态口令文件证书：单笔1万元、单日5万元<br>USBKEY移动证书：无限额");
		gatewayMap.put(BankType.SPDB, "数字证书版：客户自行设定单笔、单日限额 <br>动态密码版：单笔、单日5万元");
		gatewayMap.put(BankType.CMBC, "短信验证码：单笔、单日 5000元<br>浏览器证书：单笔、单日 5000元<br>动态令牌（OTP）：单笔、单日50万元<br>U宝：单笔、单日50万元");
		gatewayMap.put(BankType.HXB, "单笔、单日5000元");
		gatewayMap.put(BankType.GDB, "手机动态验证码：单笔、单日5000元<br>KEY令：单笔、单日5万元<br>KEY盾：单笔、单日100万元");
		gatewayMap.put(BankType.PAB, "手机动态口令：单笔、单日5万元<br>KEY盾：无限额");
		gatewayMap.put(BankType.BOS, "文件证书：单笔、单日5000元<br>易签宝：单笔、单日5万元");

		for (PayChannel item : PayChannel.values()) {
			payoutMap.put(item, new BigDecimal("500000"));
		}

	}

	public static String getGatewayLimit(BankType type) {
		return gatewayMap.get(type);
	}

	public static BigDecimal getPayoutLimit(PayChannel channel) {
		return payoutMap.get(channel);
	}

}
