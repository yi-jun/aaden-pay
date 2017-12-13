package com.aaden.pay.service.biz.exception;

/**
 *  @Description 支付异常
 *  @author aaden
 *  @date 2017年12月23日
 */
public class PaymentException extends Exception {

	private static final long serialVersionUID = -4924032398984276452L;

	public PaymentException() {
		super();
	}

	public PaymentException(String message, Throwable cause) {
		super(message, cause);
	}

	public PaymentException(String message) {
		super(message);
	}

	public PaymentException(Throwable cause) {
		super(cause);
	}

}
