package com.aaden.pay.service.biz.tp.baofoo.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *  @Description 宝付vo代付交易查证
 *  @author aaden
 *  @date 2017年12月6日
 */
@XStreamAlias("trans_reqData")
public class TransReqBF0040005 {

	private String to_acc_name;// 收款人姓名
	private String to_acc_no;// 收款人银行帐号

	public String getTo_acc_name() {
		return to_acc_name;
	}

	public void setTo_acc_name(String to_acc_name) {
		this.to_acc_name = to_acc_name;
	}

	public String getTo_acc_no() {
		return to_acc_no;
	}

	public void setTo_acc_no(String to_acc_no) {
		this.to_acc_no = to_acc_no;
	}

}
