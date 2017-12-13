package com.aaden.pay.core.eumus;

/**
 *  @Description 是否有效枚举
 *  @author aaden
 *  @date 2017年12月2日
 */
public enum IsValid {

	INVALID("0", "无效"),

	VALID("1", "有效");

	private String value;
	private String name;

	IsValid(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static String getNameByValue(String value) {
		for (IsValid att : IsValid.values()) {
			if (att.getValue().equals(value))
				return att.getName();
		}
		return null;
	}
}
