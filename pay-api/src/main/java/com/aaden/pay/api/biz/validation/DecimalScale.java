package com.aaden.pay.api.biz.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.aaden.pay.api.biz.validation.impl.DecimalScaleValidatorImpl;

/**
 * 自定义参数校验注解 校验 List 集合中是否有null 元素
 */

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DecimalScaleValidatorImpl.class) //// 此处指定了注解的实现类为ListNotHasNullValidatorImpl

public @interface DecimalScale {

	/**
	 * 小数位数量,默认为两位
	 */
	int value() default 2;

	String message() default "最多只能有两位小数";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	/**
	 * 定义List，为了让Bean的一个属性上可以添加多套规则
	 */
	@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
	@Retention(RUNTIME)
	@Documented
	@interface List {
		DecimalScale[] value();
	}
}