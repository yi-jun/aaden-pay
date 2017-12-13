package com.aaden.pay.service.bank;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.aaden.pay.api.BankService;
import com.aaden.pay.api.biz.vo.BankRequest;
import com.aaden.pay.api.biz.vo.BankResponse;
import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.BankVerifyType;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.core.logger.SimpleLogger;
import com.alibaba.fastjson.JSON;

/**
 *  @Description 银行卡验证签约测试
 *  @author aaden
 *  @date 2017年12月18日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context-test.xml")
public class BankVerifyTest {

	@Autowired
	BankService bankVerifyService;

	SimpleLogger logger = SimpleLogger.getLogger(BankVerifyTest.class);

	 @Test
	public void testVerifyBank() {
		BankRequest request = new BankRequest();
		// 用户填写,必填
		request.getInfo().setCardNo("6222021704006344382");
		request.getInfo().setIdNo("320301198502169142");
		request.getInfo().setBankType(BankType.ICBC);
		request.getInfo().setRealName("张宝");
		request.getInfo().setMobile("18588208652");

		// 系统补充,必填
		request.setBankVerifyType(BankVerifyType.APPLY);
		request.getInfo().setUserId("zhangsan_user_id");
		request.getInfo().setChannel(PayChannel.ALLIN);
		// 选填,冗余查询
		request.getInfo().setUserLoginName("zhangsan");
		BankResponse resp = bankVerifyService.verifyBank(request);
		System.out.println("=======================================");
		System.out.println(JSON.toJSON(resp));
	}

	@Test
	public void testVerifyValidCode() {
		// 必填
		BankRequest request = new BankRequest();
		request.getBind().setUserId("zhangsan_user_id");
		request.getBind().setValidCode("433003");// 用户填写的验证码
		request.setBankVerifyType(BankVerifyType.CONFIRM);
		BankResponse resp = bankVerifyService.verifyBank(request);
		System.out.println("=======================================");
		System.out.println(JSON.toJSON(resp));
		System.out.println("支付授权码:" + resp.getAgreeNo());
	}

}
