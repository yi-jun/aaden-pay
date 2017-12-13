package com.aaden.pay.service.biz.exception;

/**
 *  @Description 支付签名异常
 *  @author aaden
 *  @date 2017年12月13日
 */
public class PaymentSignException extends Exception {

	private static final long serialVersionUID = -4924032398984276452L;

	public PaymentSignException() {
		super();
	}

	public PaymentSignException(String message, Throwable cause) {
		super(message, cause);
	}

	public PaymentSignException(String message) {
		super(message);
	}

	public PaymentSignException(Throwable cause) {
		super(cause);
	}

}
