package com.aaden.pay.api.biz.validation.impl;

import java.math.BigDecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Service;

import com.aaden.pay.api.biz.validation.DecimalScale;

/**
 * 自定义注解ListNotHasNull 的实现类 用于判断List集合中是否含有null元素
 */

@Service
public class DecimalScaleValidatorImpl implements ConstraintValidator<DecimalScale, BigDecimal> {

	private int value;

	@Override
	public void initialize(DecimalScale constraintAnnotation) {
		this.value = Math.abs(constraintAnnotation.value());
	}

	public boolean isValid(BigDecimal val, ConstraintValidatorContext constraintValidatorContext) {
		if (val == null)
			return false;

		BigDecimal round = val.setScale(this.value, BigDecimal.ROUND_DOWN);
		if (round.compareTo(val) != 0)
			return false;

		return true;
	}

}