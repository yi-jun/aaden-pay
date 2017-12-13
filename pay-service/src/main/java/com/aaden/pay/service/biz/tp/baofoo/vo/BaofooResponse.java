package com.aaden.pay.service.biz.tp.baofoo.vo;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 *  @Description 宝付vo
 *  @author aaden
 *  @date 2017年12月20日
 */
public class BaofooResponse {

	private final static List<String> NOT_SURE_LIST = Arrays.asList(
			new String[] { "BF00100", "BF00112", "BF00113", "BF00115", "BF00144", "BF00202", "BF00254", "BF00238" });

	private String version;
	private String req_reserved;
	private String additional_info;
	private String resp_code;
	private String resp_msg;
	private String member_id;
	private String terminal_id;
	private String data_type;
	private String txn_type;
	private String txn_sub_type;
	private String biz_type;
	private String trade_date;
	private String trans_serial_no;
	private String trans_id;
	private String pay_card_type;
	private String bind_id;
	private String succ_amt;
	private String business_no;

	private String returnData;// 返回的原始数据

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

	public String getBind_id() {
		return bind_id;
	}

	public void setBind_id(String bind_id) {
		this.bind_id = bind_id;
	}

	public String getSucc_amt() {
		return succ_amt;
	}

	public void setSucc_amt(String succ_amt) {
		this.succ_amt = succ_amt;
	}

	public String getTrans_id() {
		return trans_id;
	}

	public void setTrans_id(String trans_id) {
		this.trans_id = trans_id;
	}

	public String getReturnData() {
		return returnData;
	}

	public void setReturnData(String returnData) {
		this.returnData = returnData;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getReq_reserved() {
		return req_reserved;
	}

	public void setReq_reserved(String req_reserved) {
		this.req_reserved = req_reserved;
	}

	public String getAdditional_info() {
		return additional_info;
	}

	public void setAdditional_info(String additional_info) {
		this.additional_info = additional_info;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getTerminal_id() {
		return terminal_id;
	}

	public void setTerminal_id(String terminal_id) {
		this.terminal_id = terminal_id;
	}

	public String getData_type() {
		return data_type;
	}

	public void setData_type(String data_type) {
		this.data_type = data_type;
	}

	public String getTxn_type() {
		return txn_type;
	}

	public void setTxn_type(String txn_type) {
		this.txn_type = txn_type;
	}

	public String getTxn_sub_type() {
		return txn_sub_type;
	}

	public void setTxn_sub_type(String txn_sub_type) {
		this.txn_sub_type = txn_sub_type;
	}

	public String getBiz_type() {
		return biz_type;
	}

	public void setBiz_type(String biz_type) {
		this.biz_type = biz_type;
	}

	public String getTrade_date() {
		return trade_date;
	}

	public void setTrade_date(String trade_date) {
		this.trade_date = trade_date;
	}

	public String getTrans_serial_no() {
		return trans_serial_no;
	}

	public void setTrans_serial_no(String trans_serial_no) {
		this.trans_serial_no = trans_serial_no;
	}

	public String getPay_card_type() {
		return pay_card_type;
	}

	public void setPay_card_type(String pay_card_type) {
		this.pay_card_type = pay_card_type;
	}

	public String getBusiness_no() {
		return business_no;
	}

	public void setBusiness_no(String business_no) {
		this.business_no = business_no;
	}

	public boolean isSuccess() {
		return "0000".equals(this.resp_code);
	}

	public boolean isNotSure() {
		return StringUtils.isEmpty(this.resp_code) || NOT_SURE_LIST.contains(this.resp_code);
	}

}
