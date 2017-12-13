package com.aaden.pay.service.biz.tp.baofoo.adaptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.aaden.pay.api.biz.enums.baofoo.BaofooPayBankType;
import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.core.httpclient.HttpClientHelper;
import com.aaden.pay.core.utils.BigDecimalUtils;
import com.aaden.pay.service.biz.tp.baofoo.base.BaofooCommon;
import com.aaden.pay.service.biz.tp.baofoo.prop.BaofooProperties;
import com.aaden.pay.service.biz.tp.baofoo.util.BaofooRsaCodingUtil;
import com.aaden.pay.service.biz.tp.baofoo.vo.TransContent;
import com.aaden.pay.service.biz.tp.baofoo.vo.TransReqBF0040001;
import com.aaden.pay.service.biz.tp.baofoo.vo.TransReqBF0040002;
import com.aaden.pay.service.biz.tp.baofoo.vo.TransRespBF0040002;
import com.aaden.pay.service.biz.tp.baofoo.vo.TransRespBF0040004;
import com.aaden.pay.service.biz.util.BankUtils;
import com.alibaba.fastjson.JSONObject;

/**
 *  @Description 宝付代付适配器
 *  @author aaden
 *  @date 2017年12月2日
 */
@Component
public class BaofooPayAdaptor extends BaofooCommon {

	protected String data_type = BaofooProperties.pay_data_type;
	protected String prx_key = this.getCreditAbsolutePath(BaofooProperties.pay_prx_key);
	protected String key_password = BaofooProperties.pay_key_password;
	protected String pub_key = this.getCreditAbsolutePath(BaofooProperties.pay_pub_key);
	protected String terminal_id = BaofooProperties.pay_terminal_id;
	protected String member_id = BaofooProperties.pay_member_id;
	protected String request_domain = BaofooProperties.pay_request_domain;

	/**
	 * 代付
	 */
	public TransContent<TransRespBF0040004> sendPay(ThirdPayRecord tr, PayRequest payRequest) {
		// 构造请求报文
		Map<String, String> param = this.buildPay(tr, payRequest);
		// 发送报文
		String result = this.sendBaofoo(param, "/baofoo-fopay/pay/BF0040001.do");
		// 解析报文
		TransContent<TransRespBF0040004> content = this.decodeResp(result, TransRespBF0040004.class);
		return content;
	}

	/**
	 * 查询
	 */
	public TransContent<TransRespBF0040002> sendQuery(ThirdPayRecord tr) {
		// 构造请求报文
		Map<String, String> param = this.buildQuery(tr);
		// 发送报文
		String result = this.sendBaofoo(param, "/baofoo-fopay/pay/BF0040002.do");
		// 解析报文
		TransContent<TransRespBF0040002> content = this.decodeResp(result, TransRespBF0040002.class);
		return content;
	}

	/**
	 * 读取对账文件
	 */
	public List<String> getCheckFile(Date checkDate) {
		return super.readLocalFile(checkDate, this.member_id, "fo");
	}

	private Map<String, String> buildPay(ThirdPayRecord tr, PayRequest payRequest) {
		Map<String, String> map = new HashMap<String, String>();
		TransContent<TransReqBF0040001> transContent = new TransContent<TransReqBF0040001>(data_type);

		BaofooPayBankType type = BaofooPayBankType.parse(tr.getBankType());
		TransReqBF0040001 transReqData = new TransReqBF0040001();
		transReqData.setTrans_no(tr.getSerialnumber());
		transReqData.setTrans_money(BigDecimalUtils.format(tr.getOrderAmount()));
		transReqData.setTo_acc_name(tr.getRealName());
		transReqData.setTo_acc_no(tr.getCardNo());
		transReqData.setTo_bank_name(type.getCode());
		String prov = BankUtils.getAreaCnName(payRequest.getCash().getBankProv());
		String city = BankUtils.getAreaCnName(payRequest.getCash().getBankCity());
		transReqData.setTo_pro_name(prov == null ? "广东省" : prov);
		transReqData.setTo_city_name(city == null ? "深圳市" : city);
		transReqData.setTo_acc_dept(payRequest.getCash().getBranchName() == null ? "" : payRequest.getCash().getBranchName());
		transReqData.setTrans_summary(payRequest.getCash().getPayRemark() == null ? "" : payRequest.getCash().getPayRemark());
		transReqData.setTrans_card_id(payRequest.getCash().getIdno() == null ? "" : payRequest.getCash().getIdno());
		transReqData.setTrans_mobile(payRequest.getCash().getMobile() == null ? "" : payRequest.getCash().getMobile());
		List<TransReqBF0040001> trans_reqDatas = new ArrayList<TransReqBF0040001>();
		trans_reqDatas.add(transReqData);

		transContent.setTrans_reqDatas(trans_reqDatas);

		String bean2XmlString = transContent.obj2Str(transContent);
		logger.info("宝付代付请求原始报文: " + bean2XmlString);
		String origData = new String(Base64.encodeBase64(bean2XmlString.getBytes()));
		String encryptData = BaofooRsaCodingUtil.encryptByPriPfxFile(origData, prx_key, key_password);

		map.put("member_id", this.member_id);
		map.put("terminal_id", this.terminal_id);
		map.put("data_type", data_type);
		map.put("data_content", encryptData);
		map.put("version", "4.0.0");
		return map;
	}

	private Map<String, String> buildQuery(ThirdPayRecord tr) {
		Map<String, String> map = new HashMap<String, String>();

		TransContent<TransReqBF0040002> transContent = new TransContent<TransReqBF0040002>(data_type);
		List<TransReqBF0040002> trans_reqDatas = new ArrayList<TransReqBF0040002>();
		TransReqBF0040002 transReqData = new TransReqBF0040002();
		transReqData.setTrans_no(tr.getSerialnumber());
		trans_reqDatas.add(transReqData);
		transContent.setTrans_reqDatas(trans_reqDatas);

		String bean2XmlString = transContent.obj2Str(transContent);
		logger.info("宝付代付请求原始报文: " + bean2XmlString);
		String origData = new String(Base64.encodeBase64(bean2XmlString.getBytes()));
		String encryptData = BaofooRsaCodingUtil.encryptByPriPfxFile(origData, prx_key, key_password);

		map.put("member_id", this.member_id);
		map.put("terminal_id", this.terminal_id);
		map.put("data_type", data_type);
		map.put("data_content", encryptData);
		map.put("version", "4.0.0");

		return map;
	}

	private String sendBaofoo(Map<String, String> postParam, String url) {
		logger.info("宝付代付请求报文：" + JSONObject.toJSONString(postParam));
		String retMsg = null;
		try {
			retMsg = HttpClientHelper.getInstance().sendHttpPost(this.request_domain + url, null, postParam);
		} catch (Exception e1) {
			// 日志已记录,避免重复打印日志
		}
		logger.info("宝付代付响应报文：" + retMsg);
		return retMsg;
	}

	@SuppressWarnings("unchecked")
	private <T> TransContent<T> decodeResp(String result, Class<T> cls) {
		if (StringUtils.isEmpty(result))
			return null;
		TransContent<T> str2Obj = new TransContent<T>(data_type);
		if (result.contains("trans_content")) {
			str2Obj = (TransContent<T>) str2Obj.str2Obj(result, cls);
		} else {
			result = BaofooRsaCodingUtil.decryptByPubCerFile(result, pub_key);
			result = new String(new Base64().decode(result));
			str2Obj = (TransContent<T>) str2Obj.str2Obj(result, cls);
		}
		logger.info("宝付代付解密后报文:" + result);
		return str2Obj;
	}

}
