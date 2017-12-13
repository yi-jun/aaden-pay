package com.aaden.pay.core.search.model;

import java.io.Serializable;


/**
 *  @Description 索引
 *  @author aaden
 *  @date 2017年12月8日
 */
public class IndexModel implements Serializable {

	private static final long serialVersionUID = -6913257929002757518L;

	private String id;

	private String indexStr;

	private String indexBody;

	private String cityCode;// 城市代码

	private String bankCode;// 银行代码

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIndexStr() {
		return indexStr;
	}

	public void setIndexStr(String indexStr) {
		this.indexStr = indexStr;
	}

	public String getIndexBody() {
		return indexBody;
	}

	public void setIndexBody(String indexBody) {
		this.indexBody = indexBody;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

}
