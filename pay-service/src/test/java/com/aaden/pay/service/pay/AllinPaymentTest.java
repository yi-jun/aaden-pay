package com.aaden.pay.service.pay;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.aaden.pay.api.PaymentService;
import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.biz.vo.PayResponse;
import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.core.logger.SimpleLogger;
import com.alibaba.fastjson.JSON;

/**
 *  @Description 通联认证支付测试
 *  @author aaden
 *  @date 2017年12月3日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context-test.xml")
public class AllinPaymentTest {

	SimpleLogger logger = SimpleLogger.getLogger(AllinPaymentTest.class);

	@Autowired
	private PaymentService paymentSerivce;

	@Test
	public void testRechargeCode() throws Throwable {
		try {
			PayRequest payRequest = new PayRequest();
			payRequest.getMust().setBankType(BankType.CCB);
			payRequest.getMust().setAmount(new BigDecimal(101));
			payRequest.getMust().setOrderCode("NO" + System.currentTimeMillis());
			payRequest.getMust().setUserId("uuid_of_user");
			payRequest.getMust().setCardNo("622202112111211");
			payRequest.getMust().setRealName("张三");
			payRequest.getMust().setUserLoginName("zhangsan");

			// 指定支付渠道,可选
			payRequest.getRecharge().setPayChannel(PayChannel.ALLIN);
			payRequest.getRecharge().setIdNo("1111");
			PayResponse resp = paymentSerivce.rechargeSmsCode(payRequest);
			System.out.println(JSON.toJSON(resp));
		} catch (Exception e) {
			logger.error(" testDeduct PaymentException", e);
		}
	}

	@Test
	public void testRecharge() throws Throwable {
		try {
			PayRequest payRequest = new PayRequest();
			payRequest.getMust().setBankType(BankType.CCB);
			payRequest.getMust().setCardNo("622202112111211");
			payRequest.getMust().setAmount(new BigDecimal(101));
			payRequest.getMust().setOrderCode("NO" + System.currentTimeMillis());
			payRequest.getMust().setUserId("uuid_of_user");
			payRequest.getMust().setRealName("张三");
			payRequest.getMust().setUserLoginName("zhangsan");
			// 指定支付渠道,可选

			// 通联接口需要传递身份信息,和手机号码
			payRequest.getRecharge().setMobile("18588208652");
			payRequest.getRecharge().setIdNo("362422");
			payRequest.getRecharge().setPayChannel(PayChannel.ALLIN);
			payRequest.getRecharge().setValidCode("336633");
			PayResponse resp = paymentSerivce.recharge(payRequest);
			System.out.println(JSON.toJSON(resp));
		} catch (Exception e) {
			logger.error(" testDeduct PaymentException", e);
		}
	}

	@Test
	public void testPay() throws Throwable {
		try {
			PayRequest payRequest = new PayRequest();

			payRequest.getMust().setBankType(BankType.ICBC);
			payRequest.getMust().setCardNo("6222021704006344382");
			payRequest.getMust().setUserId("uuid_of_user");
			payRequest.getMust().setRealName("张三");
			payRequest.getMust().setUserLoginName("zhangsan");
			payRequest.getMust().setOrderCode("NO" + System.currentTimeMillis());
			payRequest.getMust().setAmount(new BigDecimal(10));

			// 指定渠道
			payRequest.getCash().setPayChannel(PayChannel.ALLIN);
			payRequest.getCash().setIdno("320301198502169142");
			payRequest.getCash().setBankCity("440300");
			payRequest.getCash().setBankProv("440000");
			payRequest.getCash().setBranchName("宝安支行");
			payRequest.getCash().setPayRemark("remark");
			payRequest.getCash().setMobile("18588208652");
			payRequest.getCash().setOrderTotalAmount(payRequest.getMust().getAmount());
			PayResponse resp = paymentSerivce.cash(payRequest);
			System.out.println(JSON.toJSON(resp));
		} catch (Exception e) {
			logger.error(" testQuery PaymentException", e);
		}
	}

	@Test
	public void testCheck() throws Throwable {
		try {
			PayResponse resp = paymentSerivce.recheck("1711241140252918257");
			System.out.println(JSON.toJSON(resp));
		} catch (Exception e) {
			logger.error(" testCheck PaymentException", e);
		}
	}
}
