package com.aaden.pay.api.biz.config;

/**
 *  @Description 行政地区类
 *  @author aaden
 *  @date 2017年12月21日
 */
public class Area implements java.io.Serializable {

	private static final long serialVersionUID = -559902338369903479L;

	private String areaCode;// 区域编号

	private String areaName;// 名称

	private String parentCode;

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

}
