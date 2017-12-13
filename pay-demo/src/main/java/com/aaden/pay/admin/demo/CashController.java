package com.aaden.pay.admin.demo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aaden.pay.admin.base.BasicController;
import com.aaden.pay.api.BankService;
import com.aaden.pay.api.DbBankService;
import com.aaden.pay.api.PaymentService;
import com.aaden.pay.api.biz.config.Area;
import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.biz.vo.PayResponse;
import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.BankVerifyType;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.TradeStatus;
import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.core.eumus.IsValid;
import com.aaden.pay.core.search.SearchService;
import com.aaden.pay.core.search.model.IndexModel;
import com.aaden.pay.core.utils.CollectionUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description 提现(代付接口)demo
 * @author aaden
 * @date 2017年12月13日
 */
@Controller
@Scope("prototype")
@RequestMapping("/cash")
public class CashController extends BasicController {

	@Autowired
	private PaymentService paymentService;
	@Autowired
	private DbBankService dbBankService;
	@Autowired
	private SearchService searchService;
	@Autowired
	private BankService bankService;

	private static String BANK_KEY = "bank_key";

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String getHelp(Model model, HttpServletRequest request) {

		// 从数据库获取一个已绑定的银行卡
		ThirdBankSend bankSend = new ThirdBankSend();
		bankSend.setBankVerifyType(BankVerifyType.CONFIRM);
		bankSend.setIsValid(IsValid.VALID.getValue());
		List<ThirdBankSend> list = dbBankService.getPage(bankSend, "1", "1").getResult();

		ThirdBankSend bank = null;
		if (!CollectionUtils.isEmpty(list)) {
			bank = list.get(0);
			request.getSession().setAttribute(BANK_KEY, bank);
		}

		// 省份列表
		List<Area> provs = bankService.getAreaLabel("000000");
		request.setAttribute("bank", bank);
		request.setAttribute("provs", provs);

		return "form/cash_form";
	}

	/**
	 * 获取城市列表
	 */
	@RequestMapping(value = "/cityList", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public JSONArray cityList(HttpServletRequest req, String areaCode) {
		// 省份列表
		List<Area> citys = bankService.getAreaLabel(areaCode);
		JSONArray array = new JSONArray();
		if (citys != null) {
			for (Area item : citys) {
				JSONObject json = new JSONObject();
				json.put("areaCode", item.getAreaCode());
				json.put("areaName", item.getAreaName());
				array.add(json);
			}
		}
		return array;
	}

	/**
	 * 获取支行信息
	 */
	@RequestMapping(value = "/branchList", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public JSONArray getBranchList(HttpServletRequest request, BankType bankType, String cityCode, String keyWord) {
		StringBuilder key = new StringBuilder();
		if (!StringUtils.isBlank(keyWord)) {
			key.append(keyWord);
		}
		List<IndexModel> list = searchService.queryBrank(cityCode, bankType.name(), key.toString());
		JSONArray array = new JSONArray();
		if (list != null) {
			for (IndexModel item : list) {
				JSONObject json = new JSONObject();
				json.put("label", item.getIndexBody());
				json.put("value", item.getIndexBody());
				array.add(json);
			}
		}
		// {label:"中国银行广州支行",value:"中国银行广州支行"}
		return array;
	}

	/**
	 * 提现步确认
	 */
	@RequestMapping("/submit")
	@ResponseBody
	public PayResponse rechargeSubmit(HttpServletRequest req, BigDecimal amount, String bankProv, String bankCity, String branchName) {
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
		payRequest.getMust().setUserId("uuid_of_user");
		payRequest.getMust().setRealName(bank.getRealName());
		payRequest.getMust().setUserLoginName("zhangsan");
		payRequest.getMust().setOrderCode("NO" + System.currentTimeMillis());
		payRequest.getMust().setAmount(amount);

		// 指定渠道
		payRequest.getCash().setPayChannel(randomChannel());
		// 本次订单支付总额
		payRequest.getCash().setOrderTotalAmount(payRequest.getMust().getAmount());

		// 支行信息
		payRequest.getCash().setBankCity(bankProv);
		payRequest.getCash().setBankProv(bankCity);
		payRequest.getCash().setBranchName(branchName);

		// 宝付选填
		payRequest.getCash().setIdno(bank.getIdNo());
		payRequest.getCash().setMobile(bank.getMobile());

		// 支付备注选填
		payRequest.getCash().setPayRemark("xxx公司");
		PayResponse resp = paymentService.cash(payRequest);
		return resp;
	}

	private PayChannel randomChannel() {
		PayChannel[] chls = PayChannel.values();
		int size = chls.length;
		int index = new Random().nextInt(size);
		return chls[index];
	}

}
