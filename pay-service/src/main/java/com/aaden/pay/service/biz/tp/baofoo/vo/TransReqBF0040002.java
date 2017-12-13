package com.aaden.pay.service.biz.tp.baofoo.vo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *  @Description 宝付vo代付交易查证
 *  @author aaden
 *  @date 2017年12月22日
 */
@XStreamAlias("trans_reqData")
public class TransReqBF0040002 {

    private String trans_batchid;//宝付批次号
	private String trans_no;// 商户订单号
	
	public String getTrans_batchid() {
		return trans_batchid;
	}

	public void setTrans_batchid(String trans_batchid) {
		this.trans_batchid = trans_batchid;
	}

	public String getTrans_no() {
		return trans_no;
	}

	public void setTrans_no(String trans_no) {
		this.trans_no = trans_no;
	}

}
