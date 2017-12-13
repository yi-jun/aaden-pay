package com.aaden.pay.admin;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.aaden.pay.api.DbBankService;
import com.aaden.pay.api.DbPayService;
import com.aaden.pay.api.PaymentService;
import com.aaden.pay.api.biz.vo.PayResponse;
import com.aaden.pay.api.comm.enums.BankVerifyType;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.PayType;
import com.aaden.pay.api.comm.enums.TradeStatus;
import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.api.comm.model.ThirdPayValidcode;
import com.aaden.pay.core.eumus.SourceType;
import com.aaden.pay.core.eumus.YesOrNo;
import com.aaden.pay.core.page.Page;

/**
 * @Description 记录查询demo
 * @author aaden
 * @date 2017年12月1日
 */
@Controller
@Scope("prototype")
@RequestMapping("/query")
public class QueryController {

	@Autowired
	private DbPayService paymentQuerySerivce;
	@Autowired
	private DbBankService bankQueryService;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private DbPayService dbPayService;

	/**
	 * 支付日志
	 */
	@RequestMapping("/pay/list")
	public String listThirdPayLog(HttpServletRequest request, String pageNo, String pageSize, @ModelAttribute ThirdPayRecord thirdPayRecord, Model model) {
		try {
			Page<ThirdPayRecord> page = paymentQuerySerivce.getPayRecordPage(thirdPayRecord, pageNo, pageSize);
			model.addAttribute("page", page);
			model.addAttribute("vo", thirdPayRecord);
		} catch (Exception e) {
		}
		model.addAttribute("tradeStatus", TradeStatus.values());
		model.addAttribute("payChannels", PayChannel.values());
		model.addAttribute("payTypes", PayType.values());
		model.addAttribute("callStatus", YesOrNo.values());
		return "query/pay_list";
	}

	/**
	 * 支付日志
	 */
	@RequestMapping("/pay/recheck")
	@ResponseBody
	public PayResponse recheck(HttpServletRequest request, String serialnumber) {
		return paymentService.recheck(serialnumber);
	}

	/**
	 * 支付日志
	 */
	@RequestMapping("/pay/callback")
	@ResponseBody
	public String callback(HttpServletRequest request, String serialnumber) {
		// 系统内部回调方法, 如充值成功,更新用户余额,等等
		int rand = new Random().nextInt(10);
		// 根据系统内部方法结果,处理完成,则更新回调状态为成功,否则不更新,可再次回调,直到业务完成
		if (rand % 2 == 0) {
			return "随机回调,回调失败";
		}
		dbPayService.updateCallbackYes(serialnumber);
		return "随机回调,回调成功";
	}

	/**
	 * 第三方支付明细
	 */
	@RequestMapping("/pay/view")
	public String viewThirdPayLog(HttpServletRequest request, String serialnumber, Model model) {
		try {
			ThirdPayRecord thirdPayRecord = paymentQuerySerivce.getBySerialnumber(serialnumber);
			model.addAttribute("thirdPay", thirdPayRecord);
		} catch (Exception e) {
		}

		return "query/pay_view";
	}

	/**
	 * 第三方充值验证码日志
	 */
	@RequestMapping("/validcode/list")
	public String listThirdPayValidcodeLog(HttpServletRequest request, String pageNo, String pageSize, @ModelAttribute ThirdPayValidcode validcode, Model model) {
		try {
			Page<ThirdPayValidcode> page = paymentQuerySerivce.getPayValidcodePage(validcode, pageNo, pageSize);
			model.addAttribute("page", page);
			model.addAttribute("vo", validcode);
		} catch (Exception e) {
		}
		model.addAttribute("tradeStatus", TradeStatus.values());
		model.addAttribute("payChannels", PayChannel.values());
		return "query/validcode_list";
	}

	/**
	 * 银行卡验证记录日志
	 */
	@RequestMapping("/bank/list")
	public String listBankLog(HttpServletRequest request, String pageNo, String pageSize, ThirdBankSend bankSend, Model model) {
		try {
			Page<ThirdBankSend> page = bankQueryService.getPage(bankSend, pageNo, pageSize);
			model.addAttribute("page", page);
			model.addAttribute("vo", bankSend);
			model.addAttribute("verifyTypes", BankVerifyType.values());
			model.addAttribute("verifyChannels", PayChannel.values());
			model.addAttribute("channelTypes", SourceType.values());
		} catch (Exception e) {
		}
		return "query/bank_list";
	}

	/**
	 * 查看银行卡验证记录日志
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/bank/view")
	public String viewBankLog(HttpServletRequest request, String id, Model model) {
		try {
			ThirdBankSend bankSend = bankQueryService.getBankSend(id);
			model.addAttribute("bankSend", bankSend);
		} catch (Exception e) {
		}

		return "query/bank_view";
	}

}
