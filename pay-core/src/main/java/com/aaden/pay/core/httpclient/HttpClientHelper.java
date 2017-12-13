package com.aaden.pay.core.httpclient;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.aaden.pay.core.httpclient.exception.HttpClientException;
import com.aaden.pay.core.logger.SimpleLogger;

/**
 *  @Description http请求类
 *  @author aaden
 *  @date 2017年12月29日
 */
public class HttpClientHelper {

	public final int MAX_FETCHSIZE = 500000;

	private final SimpleLogger logger = SimpleLogger.getLogger(HttpClientHelper.class);

	static class SingletonHolder {
		static HttpClientHelper instance = new HttpClientHelper();
	}

	public static HttpClientHelper getInstance() {
		return SingletonHolder.instance;
	}

	private HttpClientHelper() {
	}

	/**
	 * 发送httpPost请求，请内容为map集合（需指定key-value）
	 * 
	 * @param url
	 *            请求地址
	 * @param charset
	 *            请求/响应字符编码
	 * @param map
	 *            参数列表
	 * @return 响应结果
	 */
	public String sendHttpPost(String url, Charset charset, Map<String, String> map) throws HttpClientException {
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug(" send http start ");
			if (StringUtils.isBlank(url))
				throw new HttpClientException(" sendHttpPost exception,url is blank.");
			if (charset == null)
				charset = Charset.forName("UTF-8");
			httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url.trim());
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (Entry<String, String> entry : map.entrySet())
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nvps, charset);
			httpPost.setConfig(this.getCustomConfig());
			httpPost.setEntity(urlEncodedFormEntity);
			// 执行请求，得到响应对象
			response = httpclient.execute(httpPost);
			if (logger.isDebugEnabled())
				logger.debug(" send http execute status is: " + response.getStatusLine());
			// 得到响应实体
			HttpEntity entity = response.getEntity();
			// 转换成字符串
			String resultXml = EntityUtils.toString(entity, charset);
			// 检查是否读取完毕
			EntityUtils.consume(entity);
			if (logger.isDebugEnabled())
				logger.debug(" send http end ");
			return resultXml != null && !"".equals(resultXml) ? resultXml : "";
		} catch (Exception e) {
			logger.error("sendHttpPost exception:", e);
			throw new HttpClientException(" sendHttpPost exception: ", e);
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

	/**
	 * 发送http get请求
	 * 
	 * @param url
	 *            请求地址
	 * @param charset
	 *            请求/响应字符编码
	 * @return 响应结果
	 */
	public String sendHttpGet(String url, Charset charset) throws HttpClientException {
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug(" send http start ");
			if (StringUtils.isBlank(url))
				throw new HttpClientException(" sendHttpGet exception,url is blank.");
			if (charset == null)
				charset = Charset.forName("UTF-8");
			httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(url.trim());
			httpget.setConfig(this.getCustomConfig());
			response = httpclient.execute(httpget);
			if (logger.isDebugEnabled())
				logger.debug(" send http execute status is: " + response.getStatusLine());
			HttpEntity entity = response.getEntity();
			String resultXml = EntityUtils.toString(entity, charset);
			EntityUtils.consume(entity);
			if (logger.isDebugEnabled())
				logger.debug(" send request end ");
			return resultXml != null && !"".equals(resultXml) ? resultXml : "";
		} catch (Exception e) {
			logger.error("sendHttpGet exception:", e);
			throw new HttpClientException(" sendHttpGet exception:", e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					logger.error("sendHttpGet response close IOException:", e);
				}
			}
			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					logger.error("sendHttpGet httpclient close IOException:", e);
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

	public String sendBaofoo(String url, Map<String, String> map) throws HttpClientException {
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug(" send http start ");

			Charset charset = Charset.forName("UTF-8");
			SSLContext sslContext = SSLContexts.createSystemDefault();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create().register("https", sslsf).build();

			HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(r);
			httpclient = HttpClients.custom().setConnectionManager(cm).build();
			HttpPost httpPost = new HttpPost(url.trim());

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (Entry<String, String> entry : map.entrySet())
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nvps, charset);

			// 三个参数依次为 请求超时时间 链接超时时间 数据响应超时时间
			RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(10000).setConnectTimeout(10000).setSocketTimeout(10000).build();
			httpPost.setConfig(requestConfig);
			httpPost.setEntity(urlEncodedFormEntity);
			// 执行请求，得到响应对象
			response = httpclient.execute(httpPost);
			if (logger.isDebugEnabled())
				logger.debug(" send http execute status is: " + response.getStatusLine());
			// 得到响应实体
			HttpEntity entity = response.getEntity();
			// 转换成字符串
			String resultXml = EntityUtils.toString(entity, charset);
			// 检查是否读取完毕
			EntityUtils.consume(entity);
			if (logger.isDebugEnabled())
				logger.debug(" send http end ");
			return resultXml != null && !"".equals(resultXml) ? resultXml : "";
		} catch (Exception e) {
			logger.error("sendHttpPost exception:", e);
			throw new HttpClientException(" sendHttpPost exception: ", e);
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

}
