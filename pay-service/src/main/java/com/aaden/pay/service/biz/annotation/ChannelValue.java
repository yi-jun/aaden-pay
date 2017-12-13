package com.aaden.pay.service.biz.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.PayType;

/**
 * @Description 渠道支持注解
 * @author aaden
 * @date 2017年12月25日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ChannelValue {

	PayChannel channel();

	PayType[] payType() default {};

}
