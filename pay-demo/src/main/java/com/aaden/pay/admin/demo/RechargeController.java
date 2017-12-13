package com.aaden.pay.admin.demo;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aaden.pay.admin.base.BasicController;
import com.aaden.pay.api.DbBankService;
import com.aaden.pay.api.PaymentService;
import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.biz.vo.PayResponse;
import com.aaden.pay.api.comm.enums.BankVerifyType;
import com.aaden.pay.api.comm.enums.TradeStatus;
import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.core.eumus.IsValid;
import com.aaden.pay.core.utils.CollectionUtils;

/**
 *  @Description 充值demo
 *  @author aaden
 *  @date 2017年12月10日
 */
@Controller
@Scope("prototype")
@RequestMapping("/recharge")
public class RechargeController extends BasicController {

	@Autowired
	private PaymentService paymentService;
	@Autowired
	private DbBankService bankQueryService;
	private static String BANK_KEY = "bank_key";

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String getHelp(Model model, HttpServletRequest request) {
		
		// 从数据库获取一个已绑定的银行卡
		ThirdBankSend bankSend = new ThirdBankSend();
		bankSend.setBankVerifyType(BankVerifyType.CONFIRM);
		bankSend.setIsValid(IsValid.VALID.getValue());
		List<ThirdBankSend> list = bankQueryService.getPage(bankSend, "1", "1").getResult();

		ThirdBankSend bank = null;
		if (!CollectionUtils.isEmpty(list)) {
			bank = list.get(0);
			request.getSession().setAttribute(BANK_KEY, bank);
		}
		request.setAttribute("bank", bank);
		return "form/recharge_form";
	}

	/**
	 * 绑卡第一步申请
	 */
	@RequestMapping("/code")
	@ResponseBody
	public PayResponse rechargeCode(HttpServletRequest req, BigDecimal amount) {

		ThirdBankSend bank = (ThirdBankSend) req.getSession().getAttribute(BANK_KEY);
		if (bank == null) {
			PayResponse resp = new PayResponse();
			resp.setTradeStatus(TradeStatus.FAILURE);
			resp.setMsg("没有找到绑定的银行卡信息");
			return resp;
		}
		PayRequest payRequest = new PayRequest();

		payRequest.getMust().setBankType(bank.getBankType());
		payRequest.getMust().setAmount(amount);
		payRequest.getMust().setOrderCode("NO" + System.currentTimeMillis());
		payRequest.getMust().setUserId("uuid_of_user");
		payRequest.getMust().setCardNo(bank.getCardNo());
		payRequest.getMust().setRealName(bank.getRealName());
		payRequest.getMust().setUserLoginName("zhangsan");

		payRequest.getRecharge().setAgreeNo(bank.getRemark());
		// 宝付支付字段,用户请求IP,用于风控
		payRequest.getRecharge().setClientIp("100.0.0.0");
		// 指定支付渠道,可选
		payRequest.getRecharge().setPayChannel(bank.getChannel());

		PayResponse resp = paymentService.rechargeSmsCode(payRequest);
		return resp;
	}

	/**
	 * 绑卡第二步确认
	 */
	@RequestMapping("/submit")
	@ResponseBody
	public PayResponse rechargeSubmit(HttpServletRequest req, BigDecimal amount, String validCode) {

		ThirdBankSend bank = (ThirdBankSend) req.getSession().getAttribute(BANK_KEY);
		if (bank == null) {
			PayResponse resp = new PayResponse();
			resp.setTradeStatus(TradeStatus.FAILURE);
			resp.setMsg("没有找到绑定的银行卡信息");
			return resp;
		}

		PayRequest payRequest = new PayRequest();
		payRequest.getMust().setBankType(bank.getBankType());
		payRequest.getMust().setCardNo(bank.getCardNo());
		payRequest.getMust().setAmount(amount);
		payRequest.getMust().setOrderCode("NO" + System.currentTimeMillis());
		payRequest.getMust().setUserId("uuid_of_user");
		payRequest.getMust().setRealName(bank.getRealName());
		payRequest.getMust().setUserLoginName("zhangsan");
		// 指定支付渠道,可选
		payRequest.getRecharge().setPayChannel(bank.getChannel());
		payRequest.getRecharge().setValidCode(validCode);

		PayResponse resp = paymentService.recharge(payRequest);
		return resp;
	}

}
