package com.aaden.pay.admin.demo;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aaden.pay.admin.base.BasicController;
import com.aaden.pay.api.BankService;
import com.aaden.pay.api.biz.vo.BankRequest;
import com.aaden.pay.api.biz.vo.BankResponse;
import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.BankVerifyType;

/**
 *  @Description 银行卡验证demo
 *  @author aaden
 *  @date 2017年12月21日
 */
@Controller
@Scope("prototype")
@RequestMapping("/bank")
public class BankController extends BasicController {

	@Autowired
	private BankService bankBizService;

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String getHelp(Model model, HttpServletRequest request) {
		request.setAttribute("bankTypes", BankType.values());
		return "form/bank_form";
	}

	/**
	 * 绑卡第一步申请
	 */
	@RequestMapping("/apply")
	@ResponseBody
	public BankResponse bankApply(HttpServletRequest req, BankRequest.InfoData data) {
		data.setUserId(this.userId);
		data.setUserLoginName(this.userLoginName);
		BankRequest vo = new BankRequest();
		vo.setInfo(data);
		vo.setBankVerifyType(BankVerifyType.APPLY);
		return bankBizService.verifyBank(vo);
	}

	/**
	 * 绑卡第二步确认
	 */
	@RequestMapping("/confirm")
	@ResponseBody
	public BankResponse bankConfirm(HttpServletRequest req, BankRequest.BindData data) {
		data.setUserId(this.userId);
		BankRequest vo = new BankRequest();
		vo.setBind(data);
		vo.setBankVerifyType(BankVerifyType.CONFIRM);
		return bankBizService.verifyBank(vo);
	}

}
