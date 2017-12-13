package com.aaden.pay.service.biz.tp.baofoo.base;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.core.httpclient.HttpClientHelper;
import com.aaden.pay.core.utils.DateUtils;
import com.aaden.pay.core.utils.FileUtils;
import com.aaden.pay.core.utils.ZipUtils;
import com.aaden.pay.service.biz.base.PaymentCommon;
import com.aaden.pay.service.biz.tp.baofoo.prop.BaofooProperties;
import com.aaden.pay.service.biz.tp.baofoo.vo.BaofooCheckResp;

/**
 * @Description 宝付支付基类
 * @author aaden
 * @date 2017年12月28日
 */
public class BaofooCommon extends PaymentCommon {

	protected String check_url = BaofooProperties.check_url;

	protected List<String> readLocalFile(Date checkDate, String member_id, String file_type) {
		File zip = this.downloadCheckFile(checkDate, member_id, file_type);
		if (zip == null || !zip.exists())
			return null;

		File parent = new File(zip.getParent());

		try {
			ZipUtils.unZip(zip.getAbsolutePath(), parent.getAbsolutePath() + File.separator);
		} catch (IOException e) {
			logger.error("宝付解压对账文件异常:", e);
		}

		String key = DateUtils.formatDate(checkDate);
		for (File f : parent.listFiles()) {
			if (f.getName().endsWith(".txt") && f.getName().startsWith(file_type) && f.getName().contains(key)) {
				return FileUtils.readFileByLines(f.getAbsolutePath(), "utf-8");
			}
		}
		return null;
	}

	private File downloadCheckFile(Date checkDate, String member_id, String file_type) {
		String date = DateUtils.formatDate(checkDate);
		String fileName = file_type + date + ".zip";
		File zip = new File(this.getCheckFileDir(checkDate, PayChannel.BAOFOO) + fileName);
		if (zip.exists()) {
			return zip;
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("version", "4.0.0.2");
		map.put("member_id", member_id);
		map.put("client_ip", this.getInternetIp());
		map.put("file_type", file_type);// 收单：fi 出款：fo
		map.put("settle_date", date);
		String ret = HttpClientHelper.getInstance().sendBaofoo(this.check_url, map);
		BaofooCheckResp resp = BaofooCheckResp.parse(ret);
		if (resp == null || !resp.isSuccess()) {
			logger.error("宝付下载对账文件失败:" + (resp == null || resp.getResp_msg() == null ? "" : resp.getResp_msg()));
			return null;
		}
		byte[] data = new Base64().decode(resp.getResp_body());
		try {
			InputStream DateByte = new ByteArrayInputStream(data);// 把获取的zip文件的byte放入输入流
			OutputStream outStream = new FileOutputStream(zip);
			byte[] by = new byte[1024];
			while (DateByte.available() > 0) {
				DateByte.read(by); // 读取接收的文件流
				outStream.write(by); // 写入文件
			}
			DateByte.close();
			outStream.flush();
			outStream.close();
		} catch (Exception e) {
			logger.error("宝付下载对账文件异常:", e);
			return null;
		}
		return zip;
	}

}
