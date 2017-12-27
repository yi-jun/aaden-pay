package com.aaden.pay.service;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Description 启动支付dubbo服务
 * @author aaden
 * @date 2017年12月26日
 */
public class StartPaymentService {

	public static void main(String[] args) {
		// 运行main启动服务前, 打开application-context-test.xml的dubbo服务注释
		// <import resource="classpath:spring/dubbo-pay-provider.xml" />

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "application-context-test.xml" });
		context.start();
		System.out.println("按回车键退出");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		context.close();
	}

}
