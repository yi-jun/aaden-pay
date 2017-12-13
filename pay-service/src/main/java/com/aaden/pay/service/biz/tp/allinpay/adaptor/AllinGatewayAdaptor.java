package com.aaden.pay.service.biz.tp.allinpay.adaptor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.aaden.pay.api.biz.constants.PaymentConstans;
import com.aaden.pay.api.biz.enums.allinpay.AllinGatewayBankType;
import com.aaden.pay.api.comm.enums.CardProp;
import com.aaden.pay.api.comm.enums.TradeStatus;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.core.httpclient.HttpClientHelper;
import com.aaden.pay.core.utils.BigDecimalUtils;
import com.aaden.pay.core.utils.DateUtils;
import com.aaden.pay.service.biz.base.PaymentCommon;
import com.aaden.pay.service.biz.exception.PaymentSignException;
import com.aaden.pay.service.biz.tp.allinpay.prop.AllinpayProperties;
import com.alibaba.fastjson.JSON;
import com.allinpay.ets.client.PaymentResult;
import com.allinpay.ets.client.RequestOrder;
import com.allinpay.ets.client.SecurityUtil;
import com.allinpay.ets.client.StringUtil;

/**
 * @Description 通联网关支付适配器
 * @author aaden
 * @date 2017年12月24日
 */
@Component
public class AllinGatewayAdaptor extends PaymentCommon {

	final String url = AllinpayProperties.gateway_url;
	final String check_url = AllinpayProperties.gateway_check_url;
	final String privateKey = AllinpayProperties.gateway_privateKey;
	final String certPath = this.getCreditAbsolutePath(AllinpayProperties.gateway_certPath);
	final String signType = AllinpayProperties.gateway_signType;
	final String merchantId = AllinpayProperties.gateway_merchantId;
	final String callbank = this.callbackDomain + AllinpayProperties.gateway_callbank;
	final String asynCallback = this.callbackDomain + AllinpayProperties.gateway_asynCallback;

	final String SUCCESS_CODE = "1";

	public String getUrl() {
		return this.url;
	}

	public TradeStatus parseTradeStatus(String retCode) {
		if (StringUtils.isBlank(retCode)) {
			return TradeStatus.RETRY;
		}
		switch (retCode) {
		case SUCCESS_CODE:
			return TradeStatus.SUCCEED;
		default:
			return TradeStatus.RETRY;
		}
	}

	public PaymentResult builderPaymentResult(Map<String, String> map) throws PaymentSignException {
		PaymentResult paymentResult = new PaymentResult();
		paymentResult.setMerchantId(map.get("merchantId"));
		paymentResult.setVersion(map.get("version"));
		paymentResult.setLanguage(map.get("language"));
		paymentResult.setSignType(map.get("signType"));
		paymentResult.setPayType(map.get("payType"));
		paymentResult.setIssuerId(map.get("issuerId"));
		paymentResult.setPaymentOrderId(map.get("paymentOrderId"));
		paymentResult.setOrderNo(map.get("orderNo"));
		paymentResult.setOrderDatetime(map.get("orderDatetime"));
		paymentResult.setOrderAmount(map.get("orderAmount"));
		paymentResult.setPayAmount(map.get("payAmount"));
		paymentResult.setPayDatetime(map.get("payDatetime"));
		paymentResult.setExt1(map.get("ext1"));
		paymentResult.setExt2(map.get("ext2"));
		paymentResult.setPayResult(map.get("payResult"));
		paymentResult.setErrorCode(map.get("errorCode"));
		paymentResult.setReturnDatetime(map.get("returnDatetime"));
		paymentResult.setSignMsg(map.get("signMsg"));
		paymentResult.setKey(this.privateKey);
		paymentResult.setCertPath(this.certPath);
		boolean verifyResult = paymentResult.verify();// 测试环境不验证签名
		if (!verifyResult) {
			logger.error("通联网关支付返回报文签名验证失败,响应报文：" + JSON.toJSONString(map));
			throw new PaymentSignException("通联网关支付响应报文签名验证失败.");
		}
		return paymentResult;

	}

	/** 构造订单请求对象，并签名 */
	protected RequestOrder builderRequestOrder(ThirdPayRecord tr) {
		RequestOrder requestOrder = new RequestOrder();
		requestOrder.setPickupUrl(this.callbank);// 回调地址
		requestOrder.setReceiveUrl(this.asynCallback);// 后台回调地址
		requestOrder.setKey(this.privateKey); // MD5密钥
		requestOrder.setMerchantId(this.merchantId);// 商户号
		requestOrder.setOrderExpireDatetime(PaymentConstans.PAY_EXPIRE_MINUTE + "");// 过期时间,最大值为9999分钟,不填默认9999分钟
		requestOrder.setSignType(Integer.parseInt(this.signType));// 1表示请求使用MD5算法签名，通联响应结果使用证书签名
		requestOrder.setVersion("v1.0");// 固定填v1.0
		requestOrder.setInputCharset(1);// 1代表UTF-8、2代表GBK、3代表GB2312
		requestOrder.setLanguage(1);// 1代表简体中文、2代表繁体中文、3代表英文
		// 0不指定 1个人储蓄卡 4企业 11个人信用卡
		requestOrder.setPayType(CardProp.person == tr.getCardProp() ? 1 : 4);
		requestOrder.setPayerEmail("");// 付款人邮件
		requestOrder.setPayerTelephone("");// 付款人手机
		requestOrder.setPayerIDCard("");// 付款人身份证
		requestOrder.setPid("");// 合作伙伴的商户号
		requestOrder.setProductName("");// 商品名称
		requestOrder.setProductId("");// 商品代码
		requestOrder.setProductDesc("");// 商品描述
		requestOrder.setExt1("");
		requestOrder.setExt2("");
		// requestOrder.setProductPrice(-1L);// 签名代码 price <0?"":price
		// requestOrder.setProductNum(-1);// 签名代码 num <0?"":num
		requestOrder.setExtTL("");// 通联商户拓展业务字段，在v2.2.0版本之后才使用到的，用于开通分账等业务
		requestOrder.setPan("");// 付款的银行卡号
		requestOrder.setTradeNature("");// 贸易类型,人民币时,不填
		requestOrder.setOrderCurrency("0");// 0和156代表人民币、840代表美元、344代表港币
		AllinGatewayBankType type = AllinGatewayBankType.parse(tr.getBankType());
		requestOrder.setIssuerId(type == null ? "" : type.getCode());// 银行代码,不填列出所有
		if (this.url.contains("ceshi.allinpay.com")) {// 测试环境只支持虚拟银行
			requestOrder.setIssuerId("vbank");
		}
		requestOrder.setPayerName(tr.getRealName());// 付款人姓名
		requestOrder.setOrderNo(tr.getSerialnumber());
		requestOrder.setOrderAmount(tr.getOrderAmount().multiply(BigDecimalUtils.ONE_HUNDRED).longValue());// 通联单位:分,网站单位为元
		requestOrder.setOrderDatetime(DateUtils.cfs(tr.getSendTime()));// yyyyMMDDhhmmss
		String strSignMsg = requestOrder.doSign(); // 签名，设为signMsg字段值。
		requestOrder.setSignMsg(strSignMsg);
		return requestOrder;
	}

	/** 构造订单请求对象，签名，并返回form请求参数 */
	public Map<String, String> builderPostMap(ThirdPayRecord tr) {
		RequestOrder requestOrder = this.builderRequestOrder(tr);
		Map<String, String> postMap = new HashMap<String, String>();
		postMap.put("inputCharset", requestOrder.getInputCharset() + "");
		postMap.put("pickupUrl", requestOrder.getPickupUrl());
		postMap.put("receiveUrl", requestOrder.getReceiveUrl());
		postMap.put("version", requestOrder.getVersion());
		postMap.put("language", requestOrder.getLanguage() + "");
		postMap.put("signType", requestOrder.getSignType() + "");
		postMap.put("merchantId", requestOrder.getMerchantId());
		postMap.put("payerName", requestOrder.getPayerName());
		postMap.put("payerEmail", requestOrder.getPayerEmail());
		postMap.put("payerTelephone", requestOrder.getPayerTelephone());
		postMap.put("payerIDCard", requestOrder.getPayerIDCard());
		postMap.put("pid", requestOrder.getPid());
		postMap.put("orderNo", requestOrder.getOrderNo());
		postMap.put("orderAmount", requestOrder.getOrderAmount() + "");
		postMap.put("orderCurrency", requestOrder.getOrderCurrency());
		postMap.put("orderDatetime", requestOrder.getOrderDatetime());
		postMap.put("orderExpireDatetime", requestOrder.getOrderExpireDatetime());
		postMap.put("productName", requestOrder.getProductName());
		postMap.put("productPrice", "");
		postMap.put("productNum", "");
		postMap.put("productId", requestOrder.getProductId());
		postMap.put("productDesc", requestOrder.getProductDesc());
		postMap.put("ext1", requestOrder.getExt1());
		postMap.put("ext2", requestOrder.getExt2());
		postMap.put("payType", requestOrder.getPayType() + "");
		postMap.put("issuerId", requestOrder.getIssuerId());
		postMap.put("pan", requestOrder.getPan());
		postMap.put("tradeNature", requestOrder.getTradeNature());
		postMap.put("signMsg", requestOrder.getSignMsg());
		return postMap;
	}

	public Map<String, String> sendQuery(ThirdPayRecord thirdPayRecord) {

		// 提交查询请求
		Map<String, String> result = new HashMap<String, String>();
		String strResponse = this.doQueryConnetion(thirdPayRecord);
		String[] parameters = strResponse.split("&");
		for (int i = 0; i < parameters.length; i++) {
			String msg = parameters[i];
			int index = msg.indexOf('=');
			if (index > 0) {
				String name = msg.substring(0, index);
				String value = msg.substring(index + 1);
				result.put(name, value);
			}
		}
		return result;
	}

	/** 发起http查询服务 */
	private String doQueryConnetion(ThirdPayRecord thirdPayRecord) {
		String queryDatetime = DateUtils.cfs(new Date());
		String orderDatetime = DateUtils.cfs(thirdPayRecord.getSendTime());
		StringBuffer bufSignSrc = new StringBuffer();
		StringUtil.appendSignPara(bufSignSrc, "merchantId", this.merchantId);
		StringUtil.appendSignPara(bufSignSrc, "version", "v1.5");
		StringUtil.appendSignPara(bufSignSrc, "signType", signType);
		StringUtil.appendSignPara(bufSignSrc, "orderNo", thirdPayRecord.getSerialnumber());
		StringUtil.appendSignPara(bufSignSrc, "orderDatetime", orderDatetime);
		StringUtil.appendSignPara(bufSignSrc, "queryDatetime", queryDatetime);
		StringUtil.appendLastSignPara(bufSignSrc, "key", this.privateKey);
		String signMsg = SecurityUtil.MD5Encode(bufSignSrc.toString());
		logger.info(String.format("----------------通联网银充值对账查询开始,订单号:%s--------------", thirdPayRecord.getSerialnumber()));
		logger.info("通联网关查询原始报文:" + bufSignSrc.toString());
		try {
			Map<String, String> map = new HashMap<>();
			map.put("merchantId", this.merchantId);
			map.put("version", "v1.5");
			map.put("signType", signType);
			map.put("orderNo", thirdPayRecord.getSerialnumber());
			map.put("orderDatetime", orderDatetime);
			map.put("queryDatetime", queryDatetime);
			map.put("signMsg", signMsg);
			String strResponse = HttpClientHelper.getInstance().sendHttpPost(url, null, map);
			logger.info("----------------通联网银充值对账返回--------------");
			logger.info(strResponse);
			return strResponse == null ? "" : strResponse;
		} catch (Exception e) {
			logger.error("通联连接响应失败:", e);
			return "";
		}
	}

	public String downloadCheckFile(Date date) {
		String settleDate = DateUtils.formatDate(date); // 格式为yyyy-MM-dd

		// String fileSignMsg = ""; // 文件签名信息
		StringBuffer fileBuf = new StringBuffer(); // 签名信息前的字符串
		try {
			// 得到摘要(MD5Encode函数的传入参数为商户号+结算日期+md5key)
			String signMsg = SecurityUtil.MD5Encode(this.merchantId + settleDate + this.privateKey);
			String connneturl = check_url + "?mchtCd=" + this.merchantId + "&settleDate=" + settleDate + "&signMsg=" + signMsg;
			// 建立连接
			URL url = new URL(connneturl);
			URLConnection httpConn = url.openConnection();
			// 读取交易结果
			BufferedReader fileReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			String lines;
			String line = System.getProperty("line.separator");
			while ((lines = fileReader.readLine()) != null) {
				if (lines.length() > 0) {
					// 按行读，每行都有换行符
					fileBuf.append(lines + line);
				} else {
					// 文件中读到空行，则读取下一行为签名信息
					// fileSignMsg = fileReader.readLine();
				}
			}
			fileReader.close();
		} catch (Exception e) {
			logger.error("获取支付信息异常", e);
		}
		return fileBuf.toString();
	}

}
