package com.aaden.pay.service.biz.tp.baofoo.vo;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 *  @Description 宝付vo
 *  @author aaden
 *  @date 2017年12月29日
 */
@XStreamAlias("trans_reqDatas")
public class TransReqDatas<T> {
	
	//为空则不显示
	private String count;
	
	//去掉<trans_reqData>父节点<trans_reqDatas>
	@XStreamImplicit
	private List<T> trans_reqDatas;
	
	public TransReqDatas(){
		
	}
	
	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}


	public List<T> getTrans_reqDatas() {
		return trans_reqDatas;
	}

	public void setTrans_reqDatas(List<T> trans_reqDatas) {
		this.trans_reqDatas = trans_reqDatas;
	}

}
