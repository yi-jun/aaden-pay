package com.aaden.pay.service.bank;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.core.httpclient.HttpClientHelper;
import com.aaden.pay.core.httpclient.exception.HttpClientException;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.utils.FileUtils;

/**
 *  @Description 卡bin爬虫测试类
 *  @author aaden
 *  @date 2017年12月12日
 */
public class BankcardBinTest {
	static List<String> urls = new ArrayList<>();
	static String queryUrl = "http://www.chakahao.com/cardbin/";

	static {
		urls.add("http://www.chakahao.com/cardbin/chakahao_psbc.html");
		urls.add("http://www.chakahao.com/cardbin/chakahao_zs.html");
		urls.add("http://www.chakahao.com/cardbin/chakahao_boc.html");
		urls.add("http://www.chakahao.com/cardbin/chakahao_ccb.html");
		urls.add("http://www.chakahao.com/cardbin/chakahao_jt.html");
		urls.add("http://www.chakahao.com/cardbin/chakahao_icbc.html");
		urls.add("http://www.chakahao.com/cardbin/chakahao_abc.html");
		urls.add("http://www.chakahao.com/cardbin/chakahao_other.html");
	}

	SimpleLogger logger = SimpleLogger.getLogger(BankcardBinTest.class);

	public static void main(String[] args) {

		List<String> list = downloadList();
		System.out.println("-----------------开始执行,执行个数" + list.size());

		String xml = builderXml(list);

		String path = "C:\\Users\\Administrator\\Desktop\\bank_bin_list.xml";
		FileUtils.wirte(path, xml, false);
	}

	private static List<String> downloadList() {
		// 存在本地文件
		String path = "C:\\Users\\Administrator\\Desktop\\bin_download.txt";
		if (new File(path).exists()) {
			return FileUtils.readFileByLines(path, "utf-8");
		}

		// 线上解析url
		List<String> urls = fecthDetailList();

		String startStr = "<div class=\"chalist\">";
		String startText = "开头的银行卡类型是";
		String endText = "</p>";

		// 线上抓取
		List<String> list = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for (final String url : urls) {

			String resp = httpget(url);
			resp = resp.substring(resp.indexOf(startStr));
			resp = resp.substring(resp.indexOf(startText) + startText.length());
			// 招商银行招商银行信用卡贷记卡
			resp = resp.substring(0, resp.indexOf(endText));

			String bin = url.replace(queryUrl + "html/", "").replace(".html", "");
			resp = bin + "--" + resp;

			list.add(resp);
			sb.append(resp).append(System.lineSeparator());
		}
		System.out.println("开始写入文件");
		FileUtils.wirte(path, sb.toString(), false);
		return list;
	}

	private static String httpget(String url) {
		try {
			return HttpClientHelper.getInstance().sendHttpGet(url, Charset.forName("gb2312"));
		} catch (HttpClientException e) {
			e.printStackTrace();
			return httpget(url);
		}
	}

	static String template = "<cardstart bin=\"%s\" code=\"%s\" name=\"%s\" type=\"%s\" />";

	private static String builderXml(List<String> list) {
		StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>");
		for (String resp : list) {

			// 卡bin
			String bin = resp.split("--")[0];

			// 招商银行招商银行信用卡贷记卡
			resp = resp.split("--")[1];

			String type = parseType(resp);

			// xxx银行
			String bank = parseBankname(resp);

			String bankCode = "null";
			for (BankType bt : BankType.values()) {
				if (bank.contains(bt.getCnName()) ) {
					bankCode = bt.name();
					bank = bt.getCnName();
				}
			}

			String line = String.format(template, bin, bankCode, bank, type) + System.lineSeparator();
			sb.append(line);

		}
		sb.append("</root>");
		return sb.toString();

	}

	private static String parseBankname(String resp) {
		String bank = resp.replaceAll("准贷记卡", "").replaceAll("贷记卡", "").replaceAll("借记卡", "").replaceAll("信用卡", "")
				.replaceAll("储蓄卡", "").replaceAll("预付费卡", "");

		if (bank.contains("邮储银行") || bank.contains("邮政")) {
			bank = "中国邮政储蓄银行";
		}

		if ((bank.contains("中银") || bank.contains("中国银") || bank.contains("中行")) && !bank.contains("中国银盛")) {
			bank = "中国银行";
		}

		if (bank.contains("工行") || bank.contains("工银") || bank.contains("中国工商")) {
			bank = "中国工商银行";
		}
		if (bank.contains("浦东") || bank.contains("浦发")) {
			bank = "上海浦东发展银行";
		}
		if (bank.contains("深圳发展") || bank.contains("深发")) {
			bank = "平安银行";
		}
		if (bank.contains("东亚")) {
			bank = "东亚银行";
		}
		return bank;
	}

	private static String parseType(String resp) {
		String type = "未知卡";
		if (resp.contains("准贷记卡")) {
			type = "准贷记卡";
		} else if (resp.contains("贷记卡")) {
			type = "贷记卡";
		} else if (resp.contains("信用卡")) {
			type = "信用卡";
		} else if (resp.contains("储蓄卡")) {
			type = "借记卡";
		} else if (resp.contains("借记卡")) {
			type = "借记卡";
		} else if (resp.contains("预付费卡")) {
			type = "预付费卡";
		}
		return type;
	}

	private static List<String> fecthDetailList() {
		List<String> list = new ArrayList<>();
		String key = "href='";
		String startText = "<div style=\"font-size:17px;width:980px;line-height:40px;\">";
		String endText = "</div>";

		// 抓取页面
		for (String url : urls) {
			String resp = HttpClientHelper.getInstance().sendHttpGet(url, null);
			resp = resp.substring(resp.indexOf(startText) + startText.length());
			resp = resp.substring(0, resp.indexOf(endText));

			// 抓取bin页面链接
			for (String string : resp.split("\r\n")) {
				if (string.contains(key)) {
					String uri = string.substring(string.indexOf(key) + key.length());
					uri = uri.substring(0, uri.indexOf("'"));
					list.add(queryUrl + uri);
				}
			}
		}
		return list;
	}

}
