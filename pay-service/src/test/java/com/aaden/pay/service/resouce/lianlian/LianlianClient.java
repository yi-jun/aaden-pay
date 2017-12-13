package com.aaden.pay.service.resouce.lianlian;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.aaden.pay.core.httpclient.exception.HttpClientException;
import com.aaden.pay.core.logger.SimpleLogger;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 *  @Description 连连工具类
 *  @author aaden
 *  @date 2017年12月13日
 */
public class LianlianClient {

	SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	private String merchantId = "201408071000001543";
	private String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMlGNh/WsyZSYnQcHd9t5qUkhcOhuQmozrAY9DM4+7fhpbJenmYee4chREW4RB3m95+vsz9DqCq61/dIOoLK940/XmhKkuVjfPqHJpoyHJsHcMYy2bXCd2fI++rERdXtYm0Yj2lFbq1aEAckciutyVZcAIHQoZsFwF8l6oS6DmZRAgMBAAECgYAApq1+JN+nfBS9c2nVUzGvzxJvs5I5qcYhY7NGhySpT52NmijBA9A6e60Q3Ku7vQeICLV3uuxMVxZjwmQOEEIEvXqauyYUYTPgqGGcwYXQFVI7raHa0fNMfVWLMHgtTScoKVXRoU3re6HaXB2z5nUR//NE2OLdGCv0ApaJWEJMwQJBAPWoD/Cm/2LpZdfh7oXkCH+JQ9LoSWGpBDEKkTTzIqU9USNHOKjth9vWagsR55aAn2ImG+EPS+wa9xFTVDk/+WUCQQDRv8B/lYZD43KPi8AJuQxUzibDhpzqUrAcu5Xr3KMvcM4Us7QVzXqP7sFc7FJjZSTWgn3mQqJg1X0pqpdkQSB9AkBFs2jKbGe8BeM6rMVDwh7TKPxQhE4F4rHoxEnND0t+PPafnt6pt7O7oYu3Fl5yao5Oh+eTJQbyt/fwN4eHMuqtAkBx/ob+UCNyjhDbFxa9sgaTqJ7EsUpix6HTW9f1IirGQ8ac1bXQC6bKxvXsLLvyLSxCMRV/qUNa4Wxu0roI0KR5AkAZqsY48Uf/XsacJqRgIvwODstC03fgbml890R0LIdhnwAvE4sGnC9LKySRKmEMo8PuDhI0dTzaV0AbvXnsfDfp";
	private String md5Key = "201408071000001543test_20140812";
	private String signType = "MD5";
	private String queryUrl = "https://yintong.com.cn/traderapi/CNAPSCodeQuery.htm";

	/** 查询支行信息 */
	public JSONArray queryBranch(LianlianBankType bankType, String cityCode) {
		JSONObject post = new JSONObject();
		post.put("oid_partner", this.merchantId);
		post.put("sign_type", this.signType);
		post.put("card_no", "");
		post.put("bank_code", bankType.getCode());
		post.put("brabank_name", "银行");// 关键字
		post.put("city_code", cityCode);
		// 加签名
		String sign = LianlianPayUtil.addSign(post, this.privateKey, this.md5Key);
		post.put("sign", sign);
		String text = this.sendHttpPost(this.queryUrl, post.toString());
		logger.info("连连支付返回:" + text);

		JSONObject result = JSONObject.parseObject(text);
		if (!"0000".equals(result.getString("ret_code"))) {
			return null;
		}
		return result.getJSONArray("card_list");
	}

	public String sendHttpPost(String url, String str) throws HttpClientException {
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		try {
			LianlianSslUtils.ignoreSsl();
		} catch (Exception e1) {
			logger.error("SslUtils异常", e1);
		}
		try {
			if (logger.isDebugEnabled())
				logger.debug(" send http start ");
			if (StringUtils.isBlank(url))
				throw new HttpClientException(" sendHttpPost exception,url is blank.");
			Charset charset = Charset.forName("UTF-8");
			httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url.trim());
			StringEntity stringEntity = new StringEntity(str, charset);
			httpPost.setConfig(this.getCustomConfig());
			httpPost.setEntity(stringEntity);
			response = httpclient.execute(httpPost);
			if (logger.isDebugEnabled())
				logger.debug(" send http execute status is: " + response.getStatusLine());
			HttpEntity entity = response.getEntity();
			String resultXml = EntityUtils.toString(entity, charset);
			EntityUtils.consume(entity);
			if (logger.isDebugEnabled())
				logger.debug(" send request end ");
			return resultXml != null && !"".equals(resultXml) ? resultXml : "";
		} catch (Exception e) {
			logger.error("sendHttpPost exception:", e);
			throw new HttpClientException(" sendHttpPost exception:", e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					logger.error("sendHttpPost response close IOException:", e);
				}
			}
			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					logger.error("sendHttpPost httpclient close IOException:", e);
				}
			}
		}
	}

	// 获取自定义请求配置
	private RequestConfig getCustomConfig() {
		// 三个参数依次为 请求超时时间 链接超时时间 数据响应超时时间
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(10000).setConnectTimeout(10000).setSocketTimeout(10000).build();
		return requestConfig;
	}

}
