package com.aaden.pay.service.biz.tp.baofoo.vo;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

/**
 *  @Description 宝付vo
 *  @author aaden
 *  @date 2017年12月18日
 */
public class BaofooCheckResp {

	// resp_code=0001&resp_msg=IP与报备的Ip不匹配&resp_body=null
	public static BaofooCheckResp parse(String retStr) {
		if (retStr != null) {
			JSONObject json = new JSONObject();
			String[] arr = retStr.split("&");
			for (String string : arr) {
				String[] keyValue = string.split("=", 2);
				String key = keyValue[0];
				String value = null;
				if (keyValue.length < 2) {
					value = "";
				} else {
					value = keyValue[1];
				}				
				json.put(key, value);
			}
			return json.toJavaObject(BaofooCheckResp.class);
		}
		return new BaofooCheckResp();
	}

	private String resp_code;
	private String resp_msg;
	private String resp_body;
	private String reserved;

	public String getResp_code() {
		return resp_code;
	}

	public void setResp_code(String resp_code) {
		this.resp_code = resp_code;
	}

	public String getResp_msg() {
		return resp_msg;
	}

	public void setResp_msg(String resp_msg) {
		this.resp_msg = resp_msg;
	}

	public String getResp_body() {
		return resp_body;
	}

	public void setResp_body(String resp_body) {
		this.resp_body = resp_body;
	}

	public String getReserved() {
		return reserved;
	}

	public void setReserved(String reserved) {
		this.reserved = reserved;
	}

	public boolean isSuccess() {
		return "0000".equals(this.resp_code) && !StringUtils.isEmpty(this.resp_body);
	}

}
