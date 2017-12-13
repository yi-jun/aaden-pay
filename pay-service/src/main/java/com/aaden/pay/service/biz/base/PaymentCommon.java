package com.aaden.pay.service.biz.base;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.aaden.pay.api.biz.constants.PaymentConstans;
import com.aaden.pay.api.comm.enums.CardProp;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.PayType;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.utils.DateUtils;
import com.aaden.pay.core.utils.FileUtils;
import com.aaden.pay.core.utils.IpAddressUtil;
import com.aaden.pay.service.biz.properties.PaymentProperties;

/**
 *  @Description 支付基类
 *  @author aaden
 *  @date 2017年12月1日
 */
public class PaymentCommon {
	protected SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	// 网银回调网站根域名
	protected String callbackDomain = PaymentProperties.callback_domain;
	// 当期服务器公网ip,在线获取
	private String webip;
	// 对账文件存储目录
	protected String checkFileDir = PaymentProperties.check_file_directory;

	public PaymentCommon() {
		initParam();
	}

	protected String getInternetIp() {
		if (StringUtils.isNotEmpty(this.webip))
			return this.webip;

		this.webip = IpAddressUtil.getInternetIp();

		return this.webip;
	}

	private void initParam() {
		if (this.callbackDomain != null && this.callbackDomain.endsWith("/")) {
			this.callbackDomain = this.callbackDomain.substring(0, this.callbackDomain.length() - 1);
		}

		if (checkFileDir != null && !checkFileDir.endsWith(File.separator)) {
			checkFileDir = checkFileDir + File.separator;
		}
	}

	/**
	 * 判定订单是否失效
	 */
	public boolean isTimeOut(ThirdPayRecord payRecord) {
		if (payRecord == null || payRecord.getSendTime() == null) {
			return Boolean.FALSE;
		}

		// 企业网银支付状态更新很慢,时间延长3天
		if (CardProp.company == payRecord.getCardProp() && PayType.GATEWAY == (payRecord.getPayType())) {
			return DateUtils.isTimeOut(payRecord.getSendTime(), PaymentConstans.COMPANY_PAY_TIME_OUT_MINUTE * 60000);
		}

		return DateUtils.isTimeOut(payRecord.getSendTime(), PaymentConstans.PAY_TIME_OUT_MINUTE * 60000);
	}

	/**
	 * 获取证书绝对路径,证书不存在,抛出IllegalArgumentException异常
	 */
	protected String getCreditAbsolutePath(String configPath) {

		String path = FileUtils.getClassOrSystemPath(configPath);

		if (path != null)
			return path;

		throw new IllegalArgumentException("证书不存在:" + configPath);
	}

	/**
	 * 获取对账文件下载目录
	 */
	protected String getCheckFileDir(Date checkDate, PayChannel channel) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(checkDate);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		String path = this.checkFileDir + channel.name();
		path = path + File.separator + year;
		path = path + File.separator + month;
		path = path + File.separator + day;
		File f = new File(path);
		if (!f.exists())
			f.mkdirs();
		return f.getAbsolutePath() + File.separator;
	}

}
