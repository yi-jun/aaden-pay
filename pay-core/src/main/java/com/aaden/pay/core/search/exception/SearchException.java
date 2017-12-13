package com.aaden.pay.core.search.exception;

/**
 *  @Description 搜索异常
 *  @author aaden
 *  @date 2017年12月7日
 */
public class SearchException extends RuntimeException {

	private static final long serialVersionUID = -4938602878226291836L;

	public SearchException(String message) {
		super(message);
	}

	public SearchException(Throwable cause) {
		super(cause);
	}

	public SearchException(String message, Throwable cause) {
		super(message, cause);
	}
}
