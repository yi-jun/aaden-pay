package com.aaden.pay.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.BankService;
import com.aaden.pay.api.biz.config.Area;
import com.aaden.pay.api.biz.config.BankCardBin;
import com.aaden.pay.api.biz.vo.BankRequest;
import com.aaden.pay.api.biz.vo.BankResponse;
import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.BankVerifyType;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.api.comm.model.ThirdPayQuota;
import com.aaden.pay.core.contants.ErrorMsgConstant;
import com.aaden.pay.core.eumus.IsValid;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.orm.exception.DataBaseAccessException;
import com.aaden.pay.core.search.SearchService;
import com.aaden.pay.core.search.model.IndexModel;
import com.aaden.pay.core.serialnumber.KeyInfo;
import com.aaden.pay.core.utils.DateUtils;
import com.aaden.pay.core.utils.IdCardUtils;
import com.aaden.pay.service.biz.cache.PayCacheService;
import com.aaden.pay.service.biz.route.RechargeRoute;
import com.aaden.pay.service.biz.route.ThirdBankRoute;
import com.aaden.pay.service.biz.tp.ThirdBankVerifyService;
import com.aaden.pay.service.biz.util.BankUtils;
import com.aaden.pay.service.biz.util.CloneUtils;
import com.aaden.pay.service.biz.util.PayMessageUtils;
import com.aaden.pay.service.biz.vo.ThirdBankResponse;
import com.aaden.pay.service.comm.service.ThirdBankSendService;

/**
 * @Description 银行卡验证签约实现
 * @author aaden
 * @date 2017年12月16日
 */
@Service("bankService")
public class BankServiceImpl implements BankService {

	protected SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	@Autowired
	private ThirdBankSendService bankSendService;
	@Autowired
	@Qualifier("dbCacheService")
	private PayCacheService payCacheService;
	@Autowired
	private ThirdBankRoute bankRoute;
	@Autowired
	private RechargeRoute rechargeRoute;
	@Autowired
	private SearchService searchService;

	@Override
	public BankResponse verifyBank(BankRequest request) {
		if (request.getBankVerifyType() == null)
			return BankResponse.getFailInstance("验证类型不能为空!");

		if (request.getBankVerifyType() == BankVerifyType.APPLY)
			return this.verifyBankInfo(request);

		return this.verifyPhoneCode(request);
	}

	@Override
	public List<Area> getAreaLabel(String areaCode) {
		return BankUtils.getAreaList(areaCode);
	}

	private BankResponse verifyBankInfo(BankRequest request) {

		try {
			this.checkData(request);
		} catch (Exception e2) {
			return BankResponse.getFailInstance(e2.getMessage());
		}

		// 获取路由
		ThirdBankVerifyService service = null;
		try {
			service = bankRoute.route(request);
		} catch (Exception e1) {
			return BankResponse.getFailInstance(e1.getMessage());
		}

		// 保存验卡记录
		String req = KeyInfo.getInstance().getDateKey();
		ThirdBankSend bankSend = initBankSend(request, req);
		try {
			bankSendService.save(null, bankSend);
		} catch (DataBaseAccessException e) {
			return BankResponse.getFailInstance(ErrorMsgConstant.ERR_OPERATE_DATABASE);
		}

		ThirdBankResponse ret = service.bindApply(request, CloneUtils.clone(bankSend));
		this.parseResponse(bankSend, ret);

		String friendMsg = PayMessageUtils.getFriendlyMsg(bankSend);
		if (ret.getSuccess()) {// 缓存token,
			payCacheService.setBankToken(bankSend);
			return BankResponse.getSuccessInstance(friendMsg);
		}
		return BankResponse.getFailInstance(friendMsg);
	}

	private void checkData(BankRequest request) throws Exception {
		BankRequest.InfoData vo = request.getInfo();
		if (StringUtils.isBlank(vo.getMobile())) {
			throw new Exception("请输入预留手机号!");
		}
		if (StringUtils.isBlank(vo.getCardNo()) || !BankUtils.isValidCardNo(vo.getCardNo())) {
			throw new Exception("请输入正确的银行卡号!");
		}
		if (StringUtils.isBlank(vo.getRealName())) {
			throw new Exception("请输入您的姓名!");
		}
		if (StringUtils.isBlank(vo.getIdNo())) {
			throw new Exception("请输入您的身份证号码!");
		}
		String error = IdCardUtils.IDCardValidate(vo.getIdNo());
		if (!StringUtils.isBlank(error)) {
			throw new Exception(error);
		}
		BankCardBin bin = BankUtils.getCardType(vo.getCardNo());
		if (bin != null) {
			if (vo.getBankType() == null) {// 自动填充
				vo.setBankType(bin.getBankType());
			} else {
				if (bin.getBankType() != vo.getBankType()) {
					throw new Exception("后台检测您的所属银行为:" + bin.getBankname());
				}
			}

		}
		if (vo.getBankType() == null) {
			throw new Exception("后台无法自动识别该卡号,请选择开户银行!");
		}

		ThirdPayQuota quota = rechargeRoute.autoMatch(vo.getBankType());
		if (quota == null) {
			throw new Exception("暂不支持该银行卡!");
		}
		// 填充
		if (vo.getChannel() == null) {
			vo.setChannel(quota.getPayChannel());
		}

	}

	private BankResponse verifyPhoneCode(BankRequest request) {
		if (StringUtils.isBlank(request.getBind().getValidCode())) {
			return BankResponse.getFailInstance("请输入验证码");
		}
		ThirdBankSend cache = payCacheService.getBankCache(request.getBind().getUserId());
		if (cache == null) {
			return BankResponse.getFailInstance("验证码已失效,请重新获取!");
		}
		// 缓存中填充银行卡,充值金额等数据
		request.getInfo().setCardNo(cache.getCardNo());
		request.getInfo().setBankType(cache.getBankType());
		request.getInfo().setIdNo(cache.getIdNo());
		request.getInfo().setRealName(cache.getRealName());
		request.getInfo().setUserId(cache.getUserId());
		request.getInfo().setMobile(cache.getMobile());
		request.getInfo().setSourceType(cache.getSourceType());
		request.getInfo().setChannel(cache.getChannel());
		request.getInfo().setUserLoginName(cache.getUserLoginName());

		// 获取路由
		ThirdBankVerifyService service = null;
		try {
			service = bankRoute.route(request);
		} catch (Exception e1) {
			return BankResponse.getFailInstance(e1.getMessage());
		}

		// 保存验卡记录
		String req = request.getInfo().getChannel() == PayChannel.BAOFOO ? KeyInfo.getInstance().getDateKey() : cache.getReq();
		ThirdBankSend bankSend = initBankSend(request, req);
		try {
			bankSendService.save(null, bankSend);
		} catch (DataBaseAccessException e) {
			return BankResponse.getFailInstance(ErrorMsgConstant.ERR_OPERATE_DATABASE);
		}

		ThirdBankResponse ret = service.bindConfirm(request, CloneUtils.clone(bankSend), cache.getReq());
		this.parseResponse(bankSend, ret);

		String friendMsg = PayMessageUtils.getFriendlyMsg(bankSend);
		if (ret.getSuccess()) {
			BankResponse resp = BankResponse.getSuccessInstance(friendMsg);
			resp.setAgreeNo(ret.getAgreeNo());
			payCacheService.removeBankToken(cache.getUserId());
			return resp;
		}
		return BankResponse.getFailInstance(friendMsg);
	}

	private void parseResponse(ThirdBankSend bankSend, ThirdBankResponse ret) {
		try {
			bankSend.setReturnCode(ret.getReturnCode());
			bankSend.setReturnMsg(ret.getReturnMsg());
			bankSend.setIsValid(ret.getSuccess() ? IsValid.VALID.getValue() : IsValid.INVALID.getValue());
			bankSend.setRemark(ret.getAgreeNo());
			bankSendService.update(null, bankSend);
		} catch (DataBaseAccessException e) {
			logger.error("更新验卡记录出现异常!");
		}
	}

	private ThirdBankSend initBankSend(BankRequest request, String req) {
		ThirdBankSend bankSend = new ThirdBankSend();
		bankSend.setBankType(request.getInfo().getBankType());
		bankSend.setCardNo(request.getInfo().getCardNo());
		bankSend.setChannel(request.getInfo().getChannel());
		bankSend.setIdNo(request.getInfo().getIdNo());
		bankSend.setMobile(request.getInfo().getMobile());
		bankSend.setRealName(request.getInfo().getRealName());
		bankSend.setReq(req);
		bankSend.setBankVerifyType(request.getBankVerifyType());
		bankSend.setSourceType(request.getInfo().getSourceType());
		bankSend.setSendTime(DateUtils.today());
		bankSend.setUserId(request.getInfo().getUserId());
		bankSend.initPrimaryKey();
		bankSend.setIsValid(IsValid.INVALID.getValue());
		bankSend.setUserLoginName(request.getInfo().getUserLoginName());
		return bankSend;
	}

	@Override
	public List<IndexModel> queryBrank(String cityCode, BankType bankType, String key) {
		List<IndexModel> list = searchService.queryBrank(cityCode, bankType.name(), key);
		return list;
	}

}
