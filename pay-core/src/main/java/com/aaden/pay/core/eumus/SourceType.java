package com.aaden.pay.core.eumus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  @Description 来源枚举
 *  @author aaden
 *  @date 2017年12月4日
 */
public enum SourceType {

	/**
	 * 微站
	 */
	WAP("wap", "微站"),

	/**
	 * 移动端
	 */
	IOS("ios", "iPhone"),

	/**
	 * 移动端
	 */
	ANDROID("adro", "Android"),

	/**
	 * 前台网站
	 */
	WEB("web", "网站"),

	/**
	 * 管理后台
	 */
	ADMIN("adm", "后台"),

	;

	protected final String value;

	protected final String name;

	private SourceType(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	/**
	 * 解析字符串.
	 * 
	 * @return {@link ChannelType}
	 */
	public static final SourceType parse(String value) {
		for (SourceType st : SourceType.values()) {
			if (st.getValue().equals(value)) {
				return st;
			}
		}
		return null;
	}

	/**
	 * 解析字符串.
	 * 
	 * @return {@link ChannelType}
	 */
	public static final String getNameByValue(String value) {
		for (SourceType st : SourceType.values()) {
			if (st.getValue().equals(value)) {
				return st.getName();
			}
		}
		return "";
	}

	public static List<SourceType> getAllList() {
		List<SourceType> list = new ArrayList<SourceType>();
		list.addAll(Arrays.asList(SourceType.values()));
		return list;
	}

	public static boolean isMobileClient(String code) {
		SourceType type = parse(code);
		if (type == IOS || type == ANDROID) {
			return true;
		}
		return false;
	}
}
