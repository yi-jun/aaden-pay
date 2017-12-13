package com.aaden.pay.service.biz.tp.allinpay.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import com.allinpay.XmlTools;

/**
 *  @Description 通联工具类,覆盖发送方法,重新设定超时时间
 *  @author aaden
 *  @date 2017年12月2日
 */
public class AllinpayXmlTools extends XmlTools {

	public static String send(String url, String xml) throws Exception {
		OutputStream reqStream;
		InputStream resStream;
		String respText;
		reqStream = null;
		resStream = null;
		URLConnection request = null;
		respText = null;
		try {
			byte postData[] = xml.getBytes("GBK");
			request = createRequest(url, "POST");
			request.setRequestProperty("Content-type", "application/tlt-notify");
			request.setRequestProperty("Content-length", String.valueOf(postData.length));
			request.setRequestProperty("Keep-alive", "false");
			request.setReadTimeout(10000);
			reqStream = request.getOutputStream();
			reqStream.write(postData);
			reqStream.close();
			ByteArrayOutputStream ms = null;
			resStream = request.getInputStream();
			ms = new ByteArrayOutputStream();
			byte buf[] = new byte[4096];
			int count;
			while ((count = resStream.read(buf, 0, buf.length)) > 0)
				ms.write(buf, 0, count);
			resStream.close();
			respText = new String(ms.toByteArray(), "GBK");
		} catch (Exception ex) {
			throw ex;
		} finally {
			close(reqStream);
			close(resStream);
		}
		return respText;
	}

	private static URLConnection createRequest(String strUrl, String strMethod) throws Exception {
		URL url = new URL(strUrl);
		URLConnection conn = url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		if (conn instanceof HttpsURLConnection) {
			HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
			httpsConn.setRequestMethod(strMethod);
			httpsConn.setSSLSocketFactory(getSSLSF());
			httpsConn.setHostnameVerifier(getVerifier());
		} else if (conn instanceof HttpURLConnection) {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestMethod(strMethod);
		}
		return conn;
	}

	private static void close(OutputStream c) {
		try {
			if (c != null)
				c.close();
		} catch (Exception exception) {
		}
	}

	private static void close(InputStream c) {
		try {
			if (c != null)
				c.close();
		} catch (Exception exception) {
		}
	}
}
