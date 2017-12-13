package com.aaden.pay.service.biz.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.aaden.pay.api.biz.config.Area;
import com.aaden.pay.api.biz.config.BankCardBin;
import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.core.httpclient.HttpClientHelper;
import com.alibaba.fastjson.JSONObject;

/**
 *  @Description 银行卡工具类
 *  @author aaden
 *  @date 2017年12月7日
 */
public class BankUtils {

	/** 根据编号获取省份中文名称 */
	public static String getAreaCnName(String provCode) {
		if (StringUtils.isBlank(provCode))
			return null;
		Area prov = MemoryLabelParser.getInstance().getArea(provCode);
		return prov == null ? null : prov.getAreaName();
	}

	/** 获取省份/城市类表 */
	public static List<Area> getAreaList(String areaCode) {
		return MemoryLabelParser.getInstance().getAreaByParent(areaCode);
	}

	/** 传入银行卡号,获取卡bin */
	public static synchronized BankCardBin getCardType(String cardNo) {
		for (int i = 10; i >= 3; i--) {
			String key = cardNo.substring(0, i);
			BankCardBin bin = MemoryLabelParser.getInstance().getBankCardBin(key);
			if (bin != null)
				return bin;
		}

		// 本地卡bin不存在,在线api查询
		BankCardBin online = getCardTypeOnline(cardNo);
		return online;
	}

	/** 根据算法,验证银行卡有效性 */
	public static boolean isValidCardNo(String cardNo) {
		if (StringUtils.isBlank(cardNo) || cardNo.trim().length() == 0 || !cardNo.matches("\\d+"))
			return Boolean.FALSE;
		if (cardNo.trim().length() < 12) {
			return Boolean.FALSE;
		}
		char bit = getBankCardCheckCode(cardNo.substring(0, cardNo.length() - 1));
		if (bit == 'N') {
			return Boolean.FALSE;
		}
		return cardNo.charAt(cardNo.length() - 1) == bit;
	}

	private static final String CARD_TYPE_ONLINE = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo=%s&cardBinCheck=true";

	private static BankCardBin getCardTypeOnline(String cardNo) {
		JSONObject json = null;
		try {
			String url = String.format(CARD_TYPE_ONLINE, cardNo);
			// {"bank":"CCB","validated":true,"cardType":"DC","key":"6217002020051880631","messages":[],"stat":"ok"}
			String resp = HttpClientHelper.getInstance().sendHttpGet(url, null);
			// DC: "储蓄卡", CC: "信用卡", SCC: "准贷记卡", PC: "预付费卡"
			json = JSONObject.parseObject(resp);
		} catch (Exception e) {
			return null;
		}

		String bank = json == null ? null : json.getString("bank");
		String cardType = json == null ? null : json.getString("cardType");
		if (bank == null || cardType == null)
			return null;

		BankType bankType = BankType.valueOf(bank.toUpperCase());
		if (bankType == null)
			return null;

		String cnName = bankType.getCnName();

		String type = null;
		switch (cardType) {
		case "DC":
			type = ("储蓄卡");
			break;
		case "CC":
			type = ("信用卡");
			break;
		case "SCC":
			type = ("准贷记卡");
			break;
		case "PC":
			type = ("预付费卡");
			break;
		default:
			type = ("未知卡");
			break;
		}

		BankCardBin obj = new BankCardBin();
		obj.setBankbin("");
		obj.setBankname(cnName);
		obj.setCardType(type);
		obj.setBankType(BankType.valueOf(bank.toUpperCase()));
		return obj;

	}

	// 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
	private static char getBankCardCheckCode(String nonCheckCodeCardId) {
		char[] chs = nonCheckCodeCardId.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}

}
