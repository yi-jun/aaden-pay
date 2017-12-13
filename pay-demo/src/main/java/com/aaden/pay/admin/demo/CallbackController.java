package com.aaden.pay.admin.demo;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aaden.pay.admin.base.BasicController;
import com.aaden.pay.api.PaymentService;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.alibaba.fastjson.JSON;

/**
 *  @Description 网银回调demo
 *  @author aaden
 *  @date 2017年12月20日
 */
@Controller
@Scope("prototype")
@RequestMapping("/rmi")
public class CallbackController extends BasicController {

	@Autowired
	private PaymentService paymentService;

	/**
	 * 通联网银支付同步回调
	 */
	@RequestMapping("/allinpay/callback")
	@ResponseBody
	public String callback(HttpServletRequest request) {
		logger.info(" -----通联网银同步回调开始----- ");
		try {
			Map<String, String> returnParam = this.buildMap(request);
			logger.info("通联网银同步回调参数:" + JSON.toJSONString(returnParam));
			paymentService.callback(returnParam, PayChannel.ALLIN);
		} catch (Exception e) {
			logger.error(" allinpay callback Exception ", e);
		}
		return "充值成功";
	}

	private Map<String, String> buildMap(HttpServletRequest request) {
		Map<String, String> returnParam = new HashMap<String, String>();
		@SuppressWarnings("unchecked")
		Enumeration<String> itrator = request.getParameterNames();
		while (itrator.hasMoreElements()) {
			String key = itrator.nextElement();
			returnParam.put(key, request.getParameter(key));
		}
		return returnParam;
	}

	/**
	 * 通联网银支付异步回调
	 */
	@RequestMapping("/allinpay/async")
	@ResponseBody
	public String asyncCallback(HttpServletRequest request) {
		logger.info(" -----通联网银异步回调开始----- ");
		try {
			Map<String, String> returnParam = this.buildMap(request);
			logger.info("通联网银异步回调参数:" + JSON.toJSONString(returnParam));
			paymentService.callback(returnParam, PayChannel.ALLIN);
			return "ok";
		} catch (Exception e) {
			logger.error(" allinpay callback Exception ", e);
			return "error";
		}
	}

}
