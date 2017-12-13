package com.aaden.pay.service.pay;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.Map;

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
 *  @Description 通联网银支付测试
 *  @author aaden
 *  @date 2017年12月6日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context-test.xml")
public class AllinGatewayTest {

	SimpleLogger logger = SimpleLogger.getLogger(AllinGatewayTest.class);

	@Autowired
	private PaymentService paymentService;

	@Test
	public void testRecharge() throws Throwable {
		String filePath = "C:\\Users\\Administrator\\Desktop\\allinpay_gateway.html";
		
		PayRequest payRequest = new PayRequest();
		payRequest.getMust().setBankType(BankType.BOC);
		payRequest.getMust().setAmount(new BigDecimal("100"));
		payRequest.getMust().setOrderCode("NO" + System.currentTimeMillis());
		payRequest.getMust().setUserId("uuid_of_user");
		payRequest.getMust().setCardNo("");
		payRequest.getMust().setRealName("张三");
		payRequest.getMust().setUserLoginName("zhangsan");

		// 指定渠道
		payRequest.getRecharge().setPayChannel(PayChannel.ALLIN);
		PayResponse resp = paymentService.netsave(payRequest);

		Map<String, String> map = resp.getPostMap();

		StringBuilder html = new StringBuilder(
				String.format("<html><head><meta http-equiv='Content-Type' content='text/html;charset=utf-8'></head><body><form method='post' action=\"%s\">", resp.getPostUrl()));
		for (String key : map.keySet()) {
			html.append(String.format("%s: <input name=\"%s\" value=\"%s\" style='width:400px;' /><br>", key, key, map.get(key)));
		}
		html.append("<input type='submit' /></form></body></html>");
		FileOutputStream fos = new FileOutputStream(filePath);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
		writer.write(html.toString());
		writer.close();

		try {
			Runtime.getRuntime().exec("cmd.exe /c start "+filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info(map.toString());
	}

	@Test
	public void testQuery() {
		try {
			PayResponse resp = paymentService.recheck("1711240958560734832");
			logger.info(JSON.toJSONString(resp));
		} catch (Exception e) {
			logger.error(" testPay PaymentException", e);
		}
	}
}
