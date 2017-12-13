package com.aaden.pay.service.biz.tp.baofoo.adaptor;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.aaden.pay.api.biz.enums.baofoo.BaofooBankType;
import com.aaden.pay.api.biz.vo.BankRequest;
import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.api.comm.model.ThirdPayValidcode;
import com.aaden.pay.core.httpclient.HttpClientHelper;
import com.aaden.pay.core.httpclient.exception.HttpClientException;
import com.aaden.pay.core.serialnumber.KeyInfo;
import com.aaden.pay.core.utils.BigDecimalUtils;
import com.aaden.pay.core.utils.DateUtils;
import com.aaden.pay.core.utils.XmlUtils;
import com.aaden.pay.service.biz.tp.baofoo.base.BaofooCommon;
import com.aaden.pay.service.biz.tp.baofoo.prop.BaofooProperties;
import com.aaden.pay.service.biz.tp.baofoo.util.BaofooMapToXMLString;
import com.aaden.pay.service.biz.tp.baofoo.util.BaofooRsaCodingUtil;
import com.aaden.pay.service.biz.tp.baofoo.util.BaofooSecurityUtil;
import com.aaden.pay.service.biz.tp.baofoo.vo.BaofooResponse;
import com.alibaba.fastjson.JSONObject;

/**
 *  @Description 宝付认证支付适配器
 *  @author aaden
 *  @date 2017年12月23日
 */
@Component
public class BaofooAuthAdaptor extends BaofooCommon {

	private static final BaofooResponse BAOFOO_OBJ = new BaofooResponse();

	private String pfx_name = this.getCreditAbsolutePath(BaofooProperties.auth_pfx_name);
	private String pfx_pwd = BaofooProperties.auth_pfx_pwd;
	private String cer_name = this.getCreditAbsolutePath(BaofooProperties.auth_cer_name);
	private String terminal_id = BaofooProperties.auth_terminal_id;
	private String member_id = BaofooProperties.auth_member_id;
	private String data_type = BaofooProperties.auth_data_type;
	private String request_url = BaofooProperties.auth_request_url;

	/**
	 * 充值
	 */
	public BaofooResponse sendRecharge(ThirdPayRecord tr, PayRequest payRequest) {
		Map<String, String> post = this.buildRecharge(tr, payRequest);
		String retMsg = this.sendBaofoo(post);
		BaofooResponse resp = this.decodeResp(retMsg);
		return resp;
	}

	/**
	 * 充值验证码
	 */
	public BaofooResponse sendRechargeCode(PayRequest payRequest, ThirdPayValidcode valid) {
		Map<String, String> post = this.buildRechargeCode(payRequest, valid);
		String retMsg = this.sendBaofoo(post);
		BaofooResponse resp = this.decodeResp(retMsg);
		return resp;
	}

	/**
	 * 查询
	 */
	public BaofooResponse sendQuery(ThirdPayRecord tr) {
		Map<String, String> post = this.buildQuery(tr);
		String retMsg = this.sendBaofoo(post);
		BaofooResponse resp = this.decodeResp(retMsg);
		return resp;
	}

	/**
	 * 预绑卡
	 */
	public BaofooResponse sendPreBindcard(ThirdBankSend bankSend) {
		Map<String, String> post = this.builderPreBindcard(bankSend);
		String retMsg = this.sendBaofoo(post);
		BaofooResponse resp = this.decodeResp(retMsg);
		return resp;
	}

	/**
	 * 确认绑卡
	 */
	public BaofooResponse sendConfirmBindcard(BankRequest request, ThirdBankSend bankSend, String preReq) {
		Map<String, String> post = this.builderBindcard(request, bankSend, preReq);
		String retMsg = this.sendBaofoo(post);
		BaofooResponse resp = this.decodeResp(retMsg);
		return resp;
	}

	/**
	 * 读取对账文件
	 */
	public List<String> getCheckFile(Date checkDate) {
		return super.readLocalFile(checkDate, this.member_id, "fi");
	}

	// 预绑卡
	private Map<String, String> builderPreBindcard(ThirdBankSend bankSend) {
		Map<Object, Object> XMLArray = new HashMap<Object, Object>();
		XMLArray.put("biz_type", "0000");
		XMLArray.put("terminal_id", this.terminal_id);
		XMLArray.put("member_id", this.member_id);
		XMLArray.put("trans_serial_no", bankSend.getReq());
		XMLArray.put("trans_id", bankSend.getReq());
		XMLArray.put("trade_date", DateUtils.cfs(bankSend.getSendTime()));// yyyyMMddHHmmss
		XMLArray.put("additional_info", "");//
		XMLArray.put("req_reserved", "");//
		XMLArray.put("id_card_type", "01");// 证件类型固定01（身份证）

		XMLArray.put("acc_no", bankSend.getCardNo());// 银行卡卡号
		XMLArray.put("id_card", bankSend.getIdNo());
		XMLArray.put("id_holder", bankSend.getRealName());
		String code = BaofooBankType.parse(bankSend.getBankType()).getCode();
		XMLArray.put("pay_code", code);//// 银行卡编码
		XMLArray.put("mobile", bankSend.getMobile());
		XMLArray.put("acc_pwd", "");
		XMLArray.put("valid_date", "");
		XMLArray.put("valid_no", "");
		XMLArray.put("txn_sub_type", "11");

		Map<String, String> HeadPostParam = this.initHeadParam();
		HeadPostParam.put("txn_sub_type", "11");

		String data_content = this.encodeData(XMLArray);
		HeadPostParam.put("data_content", data_content);

		return HeadPostParam;
	}

	// 确认绑卡
	private Map<String, String> builderBindcard(BankRequest model, ThirdBankSend bankSend, String preReq) {
		Map<Object, Object> XMLArray = new HashMap<Object, Object>();
		XMLArray.put("biz_type", "0000");
		XMLArray.put("terminal_id", this.terminal_id);
		XMLArray.put("member_id", this.member_id);
		XMLArray.put("trans_serial_no", bankSend.getReq());// 新的流水号
		XMLArray.put("trade_date", DateUtils.cfs(bankSend.getSendTime()));// yyyyMMddHHmmss
		XMLArray.put("additional_info", "");//
		XMLArray.put("req_reserved", "");//

		XMLArray.put("sms_code", model.getBind().getValidCode());//
		XMLArray.put("trans_id", preReq);// 商户订单号,预绑卡订单号
		XMLArray.put("txn_sub_type", "12");

		Map<String, String> HeadPostParam = this.initHeadParam();
		HeadPostParam.put("txn_sub_type", "12");

		String data_content = this.encodeData(XMLArray);
		HeadPostParam.put("data_content", data_content);

		return HeadPostParam;
	}

	// 充值验证码
	private Map<String, String> buildRechargeCode(PayRequest payRequest, ThirdPayValidcode valid) {
		Map<Object, Object> XMLArray = new HashMap<Object, Object>();
		// 宝付流水号做唯一性, 交易已 充值验证码的trans_id为准,故而重新生成流水号
		String seriNum = KeyInfo.getInstance().getDateKey();
		XMLArray.put("biz_type", "0000");
		XMLArray.put("terminal_id", this.terminal_id);
		XMLArray.put("member_id", this.member_id);
		XMLArray.put("trans_serial_no", seriNum);// 流水号每次不重复,对账使用的trans_id字段
		XMLArray.put("trade_date", DateUtils.cfs(DateUtils.today()));// yyyyMMddHHmmss
		XMLArray.put("additional_info", "");
		XMLArray.put("req_reserved", "");

		BigDecimal amt = payRequest.getMust().getAmount().multiply(BigDecimalUtils.ONE_HUNDRED);
		String txn_amt = String.valueOf(amt.setScale(0));// 支付金额
		String bind_id = payRequest.getRecharge().getAgreeNo();// 获取绑定标识
		Map<String, String> ClientIp = new HashMap<String, String>();
		ClientIp.put("client_ip", payRequest.getRecharge().getClientIp());
		XMLArray.put("bind_id", bind_id);

		XMLArray.put("trans_id", valid.getSerialnumber());// 该字段为交易订单号,标识一笔支付,对账文件也使用他
		XMLArray.put("risk_content", ClientIp);
		XMLArray.put("txn_amt", txn_amt);// 金额以分为单位(整型数据)并把元转换成分
		XMLArray.put("txn_sub_type", "15");

		Map<String, String> HeadPostParam = this.initHeadParam();
		HeadPostParam.put("txn_sub_type", "15");

		String data_content = this.encodeData(XMLArray);
		HeadPostParam.put("data_content", data_content);

		return HeadPostParam;

	}

	// 充值
	private Map<String, String> buildRecharge(ThirdPayRecord tr, PayRequest payRequest) {
		Map<Object, Object> XMLArray = new HashMap<Object, Object>();
		// 宝付流水号做唯一性, 交易已 充值验证码的trans_id为准,故而重新生成流水号
		String seriNum = KeyInfo.getInstance().getDateKey();
		XMLArray.put("biz_type", "0000");
		XMLArray.put("terminal_id", this.terminal_id);
		XMLArray.put("member_id", this.member_id);
		XMLArray.put("trans_serial_no", seriNum);// 流水号每次不重复,对账使用的trans_id字段
		XMLArray.put("trade_date", DateUtils.cfs(tr.getSendTime()));// yyyyMMddHHmmss
		XMLArray.put("additional_info", "");
		XMLArray.put("req_reserved", "");

		XMLArray.put("sms_code", payRequest.getRecharge().getValidCode());// 支付短信验证码
		XMLArray.put("business_no", payRequest.getSys().getRechargeToken());// 宝付业务流水号
		XMLArray.put("txn_sub_type", "16");

		Map<String, String> HeadPostParam = this.initHeadParam();
		HeadPostParam.put("txn_sub_type", "16");

		String data_content = this.encodeData(XMLArray);
		HeadPostParam.put("data_content", data_content);

		return HeadPostParam;
	}

	// 查询
	private Map<String, String> buildQuery(ThirdPayRecord tr) {
		// 宝付流水号做唯一性, 交易已 充值验证码的trans_id为准,故而重新生成流水号
		String seriNum = KeyInfo.getInstance().getDateKey();
		Map<Object, Object> XMLArray = new HashMap<Object, Object>();
		XMLArray.put("biz_type", "0000");
		XMLArray.put("terminal_id", this.terminal_id);
		XMLArray.put("member_id", this.member_id);
		XMLArray.put("trans_serial_no", seriNum);
		XMLArray.put("trade_date", DateUtils.cfs(tr.getSendTime()));// yyyyMMddHHmmss
		XMLArray.put("additional_info", "");
		XMLArray.put("req_reserved", "");

		XMLArray.put("orig_trans_id", tr.getSerialnumber());
		XMLArray.put("txn_sub_type", "06");

		Map<String, String> HeadPostParam = this.initHeadParam();
		HeadPostParam.put("txn_sub_type", "06");

		String data_content = this.encodeData(XMLArray);
		HeadPostParam.put("data_content", data_content);

		return HeadPostParam;
	}

	private String sendBaofoo(Map<String, String> postParam) {
		logger.info("宝付支付请求报文：" + JSONObject.toJSONString(postParam));
		String retMsg = null;
		try {
			retMsg = HttpClientHelper.getInstance().sendBaofoo(request_url, postParam);
		} catch (HttpClientException e1) {
		}
		logger.info("宝付支付响应报文：" + retMsg);

		return retMsg;
	}

	private BaofooResponse decodeResp(String retMsg) {
		String PostString = null;
		try {
			PostString = BaofooRsaCodingUtil.decryptByPubCerFile(retMsg, cer_name);
			PostString = BaofooSecurityUtil.Base64Decode(PostString);
		} catch (Exception e) {
		}
		logger.info("宝付支付响应解密结果:" + PostString);

		BaofooResponse resp = null;
		try {
			if (this.data_type.equals("xml")) {
				resp = (BaofooResponse) XmlUtils.simpleXmlToObject(PostString, BAOFOO_OBJ);
			} else {
				resp = JSONObject.parseObject(PostString, BaofooResponse.class);
			}
		} catch (Exception e) {
			logger.error("宝付解析对象失败:", e);
		}
		if (resp != null) {
			resp.setReturnData(retMsg);
		}
		return resp;
	}

	// 加密
	private String encodeData(Map<Object, Object> XMLArray) {
		String XmlOrJson = "";
		if (this.data_type.equals("xml")) {
			XmlOrJson = BaofooMapToXMLString.converter(XMLArray, "data_content");
		} else {
			XmlOrJson = JSONObject.toJSONString(XMLArray);
		}
		logger.info("宝付支付加密前数据:" + XmlOrJson);
		String base64str = "";
		try {
			base64str = BaofooSecurityUtil.Base64Encode(XmlOrJson);
		} catch (UnsupportedEncodingException e) {
		}
		String data_content = BaofooRsaCodingUtil.encryptByPriPfxFile(base64str, pfx_name, this.pfx_pwd);
		return data_content;
	}

	private Map<String, String> initHeadParam() {
		Map<String, String> HeadPostParam = new HashMap<String, String>();
		HeadPostParam.put("version", "4.0.0.0");
		HeadPostParam.put("member_id", this.member_id);
		HeadPostParam.put("terminal_id", this.terminal_id);
		HeadPostParam.put("txn_type", "0431");
		HeadPostParam.put("data_type", this.data_type);
		return HeadPostParam;
	}

}
