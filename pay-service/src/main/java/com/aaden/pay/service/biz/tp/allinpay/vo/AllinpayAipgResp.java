package com.aaden.pay.service.biz.tp.allinpay.vo;

import com.aipg.common.InfoRsp;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *  @Description 通联vo
 *  @author aaden
 *  @date 2017年12月15日
 */
@XStreamAlias("AIPG")
public class AllinpayAipgResp {
	private InfoRsp INFO;
	private RNPARET RNPARET;
	private RNPCRET RNPCRET;

	private String returnData;// 返回报文

	public final InfoRsp getINFO() {
		return INFO;
	}

	public final void setINFO(InfoRsp iNFO) {
		INFO = iNFO;
	}

	public final RNPARET getRNPARET() {
		return RNPARET;
	}

	public final void setRNPARET(RNPARET rNPARET) {
		RNPARET = rNPARET;
	}

	public final RNPCRET getRNPCRET() {
		return RNPCRET;
	}

	public final void setRNPCRET(RNPCRET rNPCRET) {
		RNPCRET = rNPCRET;
	}

	public String getReturnData() {
		return returnData;
	}

	public void setReturnData(String returnData) {
		this.returnData = returnData;
	}

}
