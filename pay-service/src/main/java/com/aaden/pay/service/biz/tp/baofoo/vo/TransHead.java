package com.aaden.pay.service.biz.tp.baofoo.vo;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 *  @Description 宝付vo
 *  @author aaden
 *  @date 2017年12月17日
 */
public class TransHead {

	private String return_code;

	private String return_msg;

	private String trans_count;

	private String trans_totalMoney;

	public String getReturn_code() {
		return return_code;
	}

	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}

	public String getReturn_msg() {
		return return_msg;
	}

	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}

	public String getTrans_count() {
		return trans_count;
	}

	public void setTrans_count(String trans_count) {
		this.trans_count = trans_count;
	}

	public String getTrans_totalMoney() {
		return trans_totalMoney;
	}

	public void setTrans_totalMoney(String trans_totalMoney) {
		this.trans_totalMoney = trans_totalMoney;
	}

	@XStreamOmitField
	private static final List<String> NOT_SURE_LIST = Arrays.asList(new String[] { "0000", "0300", "0999" });

	public boolean isNotSure() {
		return StringUtils.isEmpty(this.return_code) || NOT_SURE_LIST.contains(this.return_code);
	}

	public boolean isSuccess() {
		return "200".equals(this.return_code);
	}
}
