package com.aaden.pay.service.biz.tp.allinpay.adaptor;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.aaden.pay.api.biz.enums.allinpay.AllinPayBankType;
import com.aaden.pay.api.biz.vo.BankRequest;
import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.comm.enums.TradeStatus;
import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.core.utils.BigDecimalUtils;
import com.aaden.pay.core.utils.DateUtils;
import com.aaden.pay.core.utils.FileUtils;
import com.aaden.pay.core.utils.XmlBeanJsonConverUtil;
import com.aaden.pay.service.biz.base.PaymentCommon;
import com.aaden.pay.service.biz.exception.PaymentSignException;
import com.aaden.pay.service.biz.tp.allinpay.prop.AllinpayProperties;
import com.aaden.pay.service.biz.tp.allinpay.util.AllinpayXmlTools;
import com.aaden.pay.service.biz.tp.allinpay.vo.AllinpayAipgResp;
import com.aaden.pay.service.biz.tp.allinpay.vo.RNPA;
import com.aaden.pay.service.biz.tp.allinpay.vo.RNPC;
import com.aaden.pay.service.biz.util.BankUtils;
import com.aipg.common.AipgReq;
import com.aipg.common.AipgRsp;
import com.aipg.common.InfoReq;
import com.aipg.common.XSUtil;
import com.aipg.payreq.Body;
import com.aipg.payreq.Trans_Detail;
import com.aipg.payreq.Trans_Sum;
import com.aipg.rtreq.Trans;
import com.aipg.transquery.TransQueryReq;
import com.allinpay.XmlTools;

/**
 * @Description 通联认证支付适配器
 * @author aaden
 * @date 2017年12月24日
 */
@Component
public class AllinAuthpayAdaptor extends PaymentCommon {

	public String daifu_business = AllinpayProperties.auth_daifu_business;
	public String daikou_business = AllinpayProperties.auth_daikou_business;
	protected String merchantId = AllinpayProperties.auth_merchantId;
	protected String url = AllinpayProperties.auth_url;
	protected String check_url = AllinpayProperties.auth_check_url;
	String pfxName = this.getCreditAbsolutePath(AllinpayProperties.auth_pfxPath);
	String pfxPassword = AllinpayProperties.auth_pfxPassword;
	protected String cerPath = this.getCreditAbsolutePath(AllinpayProperties.auth_cerPath);
	String username = AllinpayProperties.auth_username;
	String password = AllinpayProperties.auth_password;

	// 通联交易不确定的订单响应代码
	protected final List<String> RETRY_CODE_LIST = Arrays.asList("1002", "2000", "2001", "2003", "2005", "2007", "2008");

	// 通联交易不确定的订单响应代码
	public final List<String> SUCCESS_LIST = Arrays.asList("0000", "4000");

	/**
	 * 批量交易
	 */
	public AipgRsp sendBatch(String trx_code, String busicode, ThirdPayRecord payRecord, PayRequest payRequest) {
		AipgReq aipg = new AipgReq();

		InfoReq info = this.getInfoReq(trx_code, (payRecord.getSerialnumber()));
		aipg.setINFO(info);
		Body body = new Body();
		Trans_Sum trans_sum = new Trans_Sum();
		trans_sum.setBUSINESS_CODE(busicode);
		trans_sum.setMERCHANT_ID(merchantId);
		trans_sum.setTOTAL_ITEM("1");
		BigDecimal amt = payRecord.getOrderAmount().multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_DOWN);
		trans_sum.setTOTAL_SUM(String.valueOf(amt));
		body.setTRANS_SUM(trans_sum);
		List<Trans_Detail> transList = new ArrayList<Trans_Detail>();
		Trans_Detail trans_detail = null;
		{
			trans_detail = new Trans_Detail();
			trans_detail.setSN(payRecord.getSerialnumber());
			trans_detail.setACCOUNT_NO(payRecord.getCardNo());
			trans_detail.setACCOUNT_NAME(payRecord.getRealName());
			trans_detail.setACCOUNT_PROP(payRecord.getCardProp().getCode());
			trans_detail.setBANK_CODE(AllinPayBankType.parse(payRecord.getBankType()).getCode());
			BigDecimal amtDtl = payRecord.getOrderAmount();
			trans_detail.setAMOUNT(String.valueOf(amtDtl.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_DOWN)));
			trans_detail.setCURRENCY("CNY");
			String prov = BankUtils.getAreaCnName(payRequest.getCash().getBankProv());
			String city = BankUtils.getAreaCnName(payRequest.getCash().getBankCity());
			prov = prov == null ? null : prov.replace("省", "");
			city = city == null ? null : city.replace("市", "");
			trans_detail.setPROVINCE(prov);
			trans_detail.setCITY(city);
			trans_detail.setBANK_NAME(payRequest.getCash().getBranchName());
			trans_detail.setREMARK(payRequest.getCash().getPayRemark());
			trans_detail.setSUMMARY(payRequest.getCash().getPayRemark());
			transList.add(trans_detail);
		}
		body.setDetails(transList);
		aipg.addTrx(body);
		String resultXml = null;
		try {
			String xml = AllinpayXmlTools.buildXml(aipg, Boolean.TRUE);
			resultXml = sendXml(xml, url, Boolean.FALSE);
		} catch (Exception e) {// 发送异常
			logger.error(" 通联连接异常:", e);
		}
		AipgRsp aipgrsp = resultXml == null ? null : XSUtil.parseRsp(resultXml);
		return aipgrsp;
	}

	/**
	 * 单笔交易,代扣,代付
	 */
	public AipgRsp sendTrade(String trx_code, String busicode, ThirdPayRecord tr, PayRequest payRequest) {
		AipgReq aipg = new AipgReq();
		AllinPayBankType bankType = AllinPayBankType.parse(tr.getBankType());
		InfoReq info = this.getInfoReq(trx_code, (tr.getSerialnumber()));
		aipg.setINFO(info);
		Trans trans = new Trans();
		trans.setBUSINESS_CODE(busicode);
		trans.setMERCHANT_ID(merchantId);
		trans.setSUBMIT_TIME(DateUtils.cfs(new Date()));
		trans.setACCOUNT_NO(tr.getCardNo());
		trans.setACCOUNT_NAME(tr.getRealName());
		trans.setBANK_CODE(bankType.getCode());
		trans.setACCOUNT_PROP(tr.getCardProp().getCode());
		BigDecimal amt = tr.getOrderAmount().multiply(BigDecimalUtils.ONE_HUNDRED).setScale(0, BigDecimal.ROUND_DOWN);
		trans.setAMOUNT(String.valueOf(amt));
		trans.setCUST_USERID(tr.getUserId());
		trans.setCURRENCY("CNY");
		trans.setID_TYPE("0");// 身份证
		trans.setTEL(payRequest.getRecharge().getMobile());
		trans.setID(payRequest.getRecharge().getIdNo());
		trans.setREMARK(payRequest.getCash().getPayRemark());
		trans.setSUMMARY(payRequest.getCash().getPayRemark());
		aipg.addTrx(trans);
		String resultXml = null;
		try {
			String xml = AllinpayXmlTools.buildXml(aipg, Boolean.TRUE);
			resultXml = this.sendXml(xml, url, Boolean.FALSE);
		} catch (Exception e) {// 发送异常
			logger.error(" 通联连接异常:", e);
		}
		AipgRsp aipgrsp = resultXml == null ? null : XSUtil.parseRsp(resultXml);
		return aipgrsp;
	}

	/**
	 * 查询报文
	 */
	public AipgRsp sendQuery(ThirdPayRecord tr) {
		AipgReq aipgReq = new AipgReq();
		InfoReq info = this.getInfoReq("200004", String.valueOf(System.currentTimeMillis()));
		aipgReq.setINFO(info);
		TransQueryReq dr = new TransQueryReq();
		aipgReq.addTrx(dr);
		dr.setMERCHANT_ID(merchantId);
		dr.setQUERY_SN((tr.getSerialnumber()));
		dr.setSTATUS(2);// 交易状态条件, 0成功,1失败, 2全部,3退票
		dr.setTYPE(1);// 0.按完成日期1.按提交日期，默认为1 ;如果使用0查询，未完成交易将查不到
		String xml = AllinpayXmlTools.buildXml(aipgReq, Boolean.TRUE);
		String resultXml = sendXml(xml, url, Boolean.FALSE);
		AipgRsp aipgrsp = resultXml == null ? null : XSUtil.parseRsp(resultXml);
		return aipgrsp;
	}

	/**
	 * 预绑卡
	 */
	public AllinpayAipgResp sendPreBindcard(BankRequest request, ThirdBankSend bankSend) {
		AipgReq aipg = new AipgReq();
		InfoReq info = this.getInfoReq("211006", (bankSend.getReq()));
		aipg.setINFO(info);

		String code = AllinPayBankType.parse(bankSend.getBankType()).getCode();

		RNPA rnpa = new RNPA();
		rnpa.setSUBMIT_TIME(DateUtils.cfs(new Date()));
		rnpa.setMERCHANT_ID(this.merchantId);
		rnpa.setBANK_CODE(code);
		rnpa.setACCOUNT_TYPE("00");// 00银行卡，01存折，02信用卡
		rnpa.setACCOUNT_NO(bankSend.getCardNo());
		rnpa.setACCOUNT_NAME(bankSend.getRealName());
		rnpa.setACCOUNT_PROP("0");// 0私人，1公司
		rnpa.setID_TYPE("0");// 0：身份证
		rnpa.setID(bankSend.getIdNo());// 身份证号码
		rnpa.setTEL(bankSend.getMobile());// 手机号
		// 测试环境
		if (this.url != null && this.url.contains("113.108.182.3")) {
			rnpa.setID("460103199202071222");// 身份证号码
			rnpa.setTEL("13511111111");// 手机号
			rnpa.setACCOUNT_NO("622848046229014713");
			rnpa.setACCOUNT_NAME("大王");
			rnpa.setBANK_CODE("0103");
		}
		aipg.addTrx(rnpa);
		String xml = XmlTools.buildXml(aipg, true).replaceAll(RNPA.class.getName(), RNPA.class.getSimpleName());
		String retXml = this.sendXml(xml, this.url, false);
		AllinpayAipgResp aipgRsp = retXml == null ? null : XmlBeanJsonConverUtil.xmlStringToBean(retXml, AllinpayAipgResp.class);
		if (aipgRsp != null)
			aipgRsp.setReturnData(retXml);
		return aipgRsp;
	}

	/**
	 * 确认绑卡
	 */
	public AllinpayAipgResp sendConfirmBindcard(BankRequest request, ThirdBankSend bankSend, String preReq) {
		AipgReq aipg = new AipgReq();
		InfoReq info = this.getInfoReq("211006C", (bankSend.getReq()));
		aipg.setINFO(info);

		RNPC rnpa = new RNPC();
		rnpa.setSRCREQSN((bankSend.getReq()));
		rnpa.setVERCODE(request.getBind().getValidCode());
		rnpa.setMERCHANT_ID(this.merchantId);
		aipg.addTrx(rnpa);

		String xml = XmlTools.buildXml(aipg, true).replaceAll(RNPC.class.getName(), RNPC.class.getSimpleName());
		String retXml = this.sendXml(xml, this.url, false);
		AllinpayAipgResp aipgRsp = retXml == null ? null : XmlBeanJsonConverUtil.xmlStringToBean(retXml, AllinpayAipgResp.class);
		if (aipgRsp != null)
			aipgRsp.setReturnData(retXml);
		return aipgRsp;
	}

	/** 将通联交易结果代码解析成平台交易状态 **/
	public TradeStatus parseTradeStatus(String returnCode) {
		if (returnCode == null || returnCode.length() == 0) {
			return TradeStatus.RETRY;
		} else if (SUCCESS_LIST.contains(returnCode)) {
			return TradeStatus.SUCCEED;
		} else if (RETRY_CODE_LIST.contains(returnCode)) {
			return TradeStatus.RETRY;
		} else {
			return TradeStatus.FAILURE;
		}
	}

	/**
	 * 报文签名
	 */
	protected String signMsg(String xml) throws PaymentSignException {
		try {
			return AllinpayXmlTools.signMsg(xml, this.pfxName, pfxPassword, Boolean.FALSE);
		} catch (Exception e) {
			logger.error("通联报文签名异常:", e);
			throw new PaymentSignException("allinpay sign Exception:", e);
		}
	}

	/**
	 * 获取交易请求对象
	 * 
	 * @param trxCode
	 *            交易代码
	 * @param orderNo
	 *            流水号
	 * @return
	 */
	protected InfoReq getInfoReq(String trxCode, String orderNo) {
		InfoReq info = new InfoReq();
		info.setTRX_CODE(trxCode);
		info.setREQ_SN(orderNo);
		info.setUSER_NAME(username);
		info.setUSER_PASS(password);
		info.setDATA_TYPE("2");// 数据类型 2为xml
		info.setVERSION("03");// 版本
		info.setLEVEL("5");// 优先级
		if ("300000".equals(trxCode) || "300001".equals(trxCode) || "300003".equals(trxCode)) {
			info.setMERCHANT_ID(merchantId);
		}
		return info;
	}

	protected String sendXml(String xml, String url, boolean isFront) {
		try {
			if (isFront) {
				xml = xml.replaceAll("<SIGNED_MSG></SIGNED_MSG>", "");
			} else {
				xml = this.signMsg(xml);
			}
			logger.info("通联支付请求报文:" + xml);
			String resultMsg = AllinpayXmlTools.send(url, xml);// XmlTools会调用System.out.println方法输出响应报文
			logger.info("通联支付响应报文:" + resultMsg);

			// 测试环境不验证签名
			if (resultMsg != null && !AllinpayXmlTools.verifySign(resultMsg, this.cerPath, Boolean.FALSE, isFront)) {
				logger.error("通联支付返回报文签名验证失败");
				throw new PaymentSignException("通联支付响应报文签名验证失败.");
			}

			return resultMsg;
		} catch (Exception e) {
			logger.error("通联请求发送异常:", e);
			return null;
		}
	}

	/**
	 * 获取对账文件目录
	 */
	protected String getCheckFileDir(Date checkDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(checkDate);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		String path = this.checkFileDir + "allinpay";
		path = path + File.separator + year;
		path = path + File.separator + month;
		path = path + File.separator + day;
		File f = new File(path);
		if (!f.exists())
			f.mkdirs();
		return f.getAbsolutePath() + File.separator;
	}

	/**
	 * 获取对账文件,优先取本地缓存
	 */
	public File getCheckFile(Date date) {
		String dateStr = DateUtils.formatDate(date);
		String fileName = dateStr + ".txt";
		File file = new File(this.getCheckFileDir(date) + fileName);
		if (file.exists()) {
			return file;
		}

		// 开始下载 url https://113.108.182.3/aipg/ProcessServlet
		String checkUrl = this.check_url + "?SETTDAY=%s&REQTIME=%s&MERID=%s&SIGN=%s&CONTFEE=%s";
		String setDate = DateUtils.df(date);// yyyyMMdd
		String reqDate = DateUtils.cfs(DateUtils.today()); // yyyyMMddHHmmss
		String confee = "1";
		// 签名原始内容为 清算日期|请求时间|商户号
		String sign = setDate + "|" + reqDate + "|" + this.merchantId;
		try {
			sign = AllinpayXmlTools.signPlain(sign, this.pfxName, this.pfxPassword, false);
		} catch (Exception e1) {
			logger.error("下载通联对账文件异常", e1);
			return null;
		}

		checkUrl = checkUrl + String.format(checkUrl, setDate, reqDate, this.merchantId, sign, confee);

		String resp = null;
		try {
			resp = this.sendXml("", checkUrl, true);
		} catch (Exception e1) {
			logger.error("下载通联对账文件异常", e1);
			return null;
		}
		resp = resp == null ? "" : resp;
		logger.debug("=====================通联对账文件:=======================");
		logger.debug(resp);

		String path = file.getAbsolutePath();
		FileUtils.wirte(path, resp, false);
		return new File(path);
	}
}
