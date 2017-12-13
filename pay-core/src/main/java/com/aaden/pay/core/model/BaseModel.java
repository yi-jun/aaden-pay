package com.aaden.pay.core.model;

import java.io.Serializable;
import java.util.UUID;

import com.alibaba.fastjson.JSON;

/**
 *  @Description model基类
 *  @author aaden
 *  @date 2017年12月20日
 */
public class BaseModel implements Serializable {

	private static final long serialVersionUID = -6845899986543323855L;

	public String toJson() {
		return JSON.toJSONString(this);
	}

	public String randomUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
