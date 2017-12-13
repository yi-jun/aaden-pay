package com.aaden.pay.core.httpclient.exception;

/**
 *  @Description http请求交互异常
 *  @author aaden
 *  @date 2017年12月23日
 */
public class HttpClientException extends RuntimeException {

	private static final long serialVersionUID = -6331376879800219105L;

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public HttpClientException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 
	 * @param message
	 */
	public HttpClientException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param cause
	 */
	public HttpClientException(Throwable cause) {
		super(cause);
	}
}
