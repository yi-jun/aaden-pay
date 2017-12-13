package com.aaden.pay.admin.demo;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aaden.pay.admin.base.BasicController;
import com.aaden.pay.api.PaymentService;
import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.biz.vo.PayResponse;
import com.aaden.pay.api.comm.enums.BankType;

/**
 *  @Description 网银demo
 *  @author aaden
 *  @date 2017年12月4日
 */
@Controller
@Scope("prototype")
@RequestMapping("/gateway")
public class GatewayController extends BasicController {

	@Autowired
	private PaymentService paymentService;

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String getHelp(Model model, HttpServletRequest request) {

		request.setAttribute("bankTypes", BankType.values());
		return "form/gateway_form";
	}

	/**
	 * 绑卡第二步确认
	 */
	@RequestMapping("/submit")
	@ResponseBody
	public PayResponse rechargeSubmit(HttpServletRequest req, BigDecimal amount, BankType bankType) {

		PayRequest payRequest = new PayRequest();
		payRequest.getMust().setBankType(bankType);
		payRequest.getMust().setAmount(amount);
		payRequest.getMust().setOrderCode("NO" + System.currentTimeMillis());
		payRequest.getMust().setUserId("uuid_of_user");
		payRequest.getMust().setCardNo("");
		payRequest.getMust().setRealName("张三");
		payRequest.getMust().setUserLoginName("zhangsan");

//		payRequest.getSys().setPayChannel(bank.getChannel());
		PayResponse resp = paymentService.netsave(payRequest);
		return resp;
	}

}
