package com.aaden.pay.service.biz.tp.allinpay;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.CardProp;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.PayType;
import com.aaden.pay.api.comm.enums.SendStatus;
import com.aaden.pay.api.comm.enums.TradeStatus;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.core.utils.BigDecimalUtils;
import com.aaden.pay.core.utils.CollectionUtils;
import com.aaden.pay.core.utils.DateUtils;
import com.aaden.pay.core.utils.FileUtils;
import com.aaden.pay.service.biz.annotation.ChannelValue;
import com.aaden.pay.service.biz.tp.AbstractThirdPayService;
import com.aaden.pay.service.biz.tp.allinpay.adaptor.AllinAuthpayAdaptor;
import com.aaden.pay.service.biz.vo.ThirdPayResponse;
import com.aipg.common.AipgRsp;
import com.aipg.common.InfoRsp;
import com.aipg.rtrsp.TransRet;
import com.aipg.transquery.QTDetail;
import com.aipg.transquery.QTransRsp;

/**
 * @Description 通联认证支付,代付实现
 * @author aaden
 * @date 2017年12月20日
 */
@Service("allinPayAuthService")
@ChannelValue(channel = PayChannel.ALLIN, payType = { PayType.AUTHPAY, PayType.PAYOUT })
public class AllinPayAuthServiceImpl extends AbstractThirdPayService {

	@Autowired
	private AllinAuthpayAdaptor adaptor;

	// 单笔实时代扣
	@Override
	public ThirdPayResponse recharge(ThirdPayRecord tr, PayRequest payRequest) {
		return this.singleTrade("100011", adaptor.daikou_business, tr, payRequest);
	}

	// 单笔实时代付
	@Override
	public ThirdPayResponse pay(ThirdPayRecord tr, PayRequest payRequest) {
		// 通联受渠道支付限制, 中国银行对公代付时 , 走批量api,其他走单笔api
		if (payRequest.getMust().getCardProp() == CardProp.company && BankType.BOC == tr.getBankType()) {
			return this.batchTrade("100002", adaptor.daifu_business, tr, payRequest);
		}

		return this.singleTrade("100014", adaptor.daifu_business, tr, payRequest);
	}

	@Override
	public ThirdPayResponse queryTrade(ThirdPayRecord tr) {
		AipgRsp aipgrsp = adaptor.sendQuery(tr);

		ThirdPayResponse trp = new ThirdPayResponse();
		trp.setSerialnumber(tr.getSerialnumber());
		if (aipgrsp == null) {// 响应数据为空，需要联系通联查询
			trp.setPayMessage("请求通联超时");
			trp.setTradeStatus(TradeStatus.RETRY);
			return trp;
		}
		InfoRsp inforp = aipgrsp.getINFO();
		if (inforp == null) {// 主数据异常 通联问题
			trp.setPayMessage("请求通联超时");
			trp.setTradeStatus(TradeStatus.RETRY);
			return trp;
		}

		if (!adaptor.SUCCESS_LIST.contains(inforp.getRET_CODE())) {
			TradeStatus tradeStatus = adaptor.parseTradeStatus(inforp.getRET_CODE());
			trp.setTradeStatus(tradeStatus);
			// 1002,代表订单不存在,判断支付订单是否已经过期失效
			boolean isTimeout = "1002".equals(inforp.getRET_CODE()) && adaptor.isTimeOut(tr);
			if (isTimeout) {
				trp.setTradeStatus(TradeStatus.OVERDUE);
			}
		}

		String rtCode = inforp.getRET_CODE();
		trp.setPayCode(rtCode);
		trp.setPayMessage(inforp.getERR_MSG());
		trp.setTradeStatus(adaptor.parseTradeStatus(rtCode));
		BigDecimal actAmount = BigDecimal.ZERO;

		/*
		 * 注意,此处代码, 当期系统设定批量接口只发送一笔交易,故而此处只取一行记录,如修改此设定,需要修改此处代码
		 */
		QTransRsp qrsq = aipgrsp.getTrxData() == null ? null : (QTransRsp) aipgrsp.getTrxData().get(0);
		QTDetail qtDtl = qrsq == null ? null : (QTDetail) qrsq.getDetails().get(0);
		if (qtDtl == null) {
			trp.setTradeStatus(TradeStatus.RETRY);
			logger.warn(" parseResultQuery have no details for serial number:" + trp.getSerialnumber());
		} else {
			TradeStatus tradeStatus = adaptor.parseTradeStatus(qtDtl.getRET_CODE());
			if (TradeStatus.RETRY == tradeStatus) {
				// 判断支付订单是否已经过期失效
				boolean isTimeout = "1002".equals(qtDtl.getRET_CODE()) && adaptor.isTimeOut(tr);
				tradeStatus = isTimeout ? TradeStatus.OVERDUE : tradeStatus;
			}
			trp.setPayCode(qtDtl.getRET_CODE());
			trp.setPayMessage(qtDtl.getERR_MSG());
			trp.setTradeStatus(tradeStatus);
			if (TradeStatus.SUCCEED == tradeStatus) {
				actAmount = new BigDecimal(qtDtl.getAMOUNT()).divide(BigDecimalUtils.ONE_HUNDRED);
				trp.setSettleTime(DateUtils.parseAuto(qtDtl.getSETTDAY()));// yyyyMMDD
			}
		}

		trp.setActAmount(actAmount);
		return trp;
	}

	@Override
	public List<ThirdPayResponse> queryTrade(Date date) {
		List<ThirdPayResponse> list = new ArrayList<ThirdPayResponse>();

		File file = adaptor.getCheckFile(date);
		if (file == null || !file.exists())
			return list;

		List<String> contexts = FileUtils.readFileByLines(file.getAbsolutePath(), "utf-8");

		/*
		 * 1 交易批次号 2 交易序号 3 交易类型 0代付,1代收 4 交易状态 交易状态,参见附录定义 5 交易金额 单位分 6 对方账号 7
		 * 交易时间 格式YYYYMMDDhhmmss 8 结算日期 格式YYYYMMDD 9 自定义 10 手续费
		 */
		for (String line : contexts) {
			String[] lines = line.split(" ");
			if (lines.length < 9)
				continue;

			if (!adaptor.SUCCESS_LIST.contains(lines[3]))
				continue;

			ThirdPayResponse rsp = new ThirdPayResponse();
			rsp.setSettleTime(DateUtils.parseAuto(lines[7]));
			rsp.setSerialnumber(lines[0]);
			rsp.setActAmount(new BigDecimal(lines[4]).divide(BigDecimalUtils.ONE_HUNDRED));
			rsp.setTradeStatus(TradeStatus.SUCCEED);
			if (lines.length >= 10)
				rsp.setFee(new BigDecimal(lines[9]).divide(BigDecimalUtils.ONE_HUNDRED));
			list.add(rsp);

		}

		return list;

	}

	// ~~~~~~~单笔交易~~~~~~~~~~//
	private ThirdPayResponse singleTrade(String trx_code, String busicode, ThirdPayRecord tr, PayRequest payRequest) {
		AipgRsp aipgrsp = this.adaptor.sendTrade(trx_code, busicode, tr, payRequest);

		ThirdPayResponse trp = new ThirdPayResponse();
		trp.setSerialnumber(tr.getSerialnumber());
		trp.setActAmount(tr.getOrderAmount().divide(BigDecimalUtils.ONE_HUNDRED));

		if (aipgrsp == null || aipgrsp.getINFO() == null) {// 响应数据为空，需要联系通联查询
			trp.setPayMessage("请求通联超时");
			trp.setSendStatus(SendStatus.FAIL);
			trp.setTradeStatus(TradeStatus.RETRY);
			return trp;
		}

		// 响应正常
		InfoRsp inforp = aipgrsp.getINFO();
		trp.setSendStatus(SendStatus.SUCCEED);
		trp.setPayCode(inforp.getRET_CODE());
		trp.setPayMessage(inforp.getERR_MSG());

		trp.setTradeStatus(this.adaptor.parseTradeStatus(trp.getPayCode()));
		@SuppressWarnings("unchecked")
		List<TransRet> list = aipgrsp.getTrxData();
		if (CollectionUtils.isNotEmpty(list)) {
			TransRet ret = (TransRet) aipgrsp.getTrxData().get(0);

			String retCode = ret.getRET_CODE();
			TradeStatus tradeStatus = this.adaptor.parseTradeStatus(retCode);
			trp.setTradeStatus(tradeStatus);
			if (TradeStatus.SUCCEED == (tradeStatus)) {
				trp.setSettleTime(DateUtils.parseAuto(ret.getSETTLE_DAY()));// yyyyMMDD
			}
		} else {
			// 响应数据有问题,比如订单号冲突的时候 , inforp有,TransRet无.所以不能设置为retry,以免更新已经存在的记录
			trp.setTradeStatus(null);
		}

		return trp;
	}

	// ~~~~~~~~~~~~~~~批次处理~~~~~~~~~~~~~~//
	private ThirdPayResponse batchTrade(String trx_code, String busicode, ThirdPayRecord payRecord, PayRequest payRequest) {
		ThirdPayResponse trp = new ThirdPayResponse();
		AipgRsp aipgrsp = this.adaptor.sendBatch(trx_code, busicode, payRecord, payRequest);

		if (aipgrsp == null || aipgrsp.getINFO() == null) {// 响应数据为空，需要联系通联查询
			trp.setPayMessage("请求通联超时");
			trp.setSendStatus(SendStatus.FAIL);
			trp.setTradeStatus(TradeStatus.RETRY);
			return trp;
		}

		InfoRsp inforp = aipgrsp.getINFO();
		trp.setPayCode(inforp.getRET_CODE());
		trp.setPayMessage(inforp.getERR_MSG());
		trp.setSendStatus(SendStatus.SUCCEED);

		if (this.adaptor.SUCCESS_LIST.contains(trp.getPayCode())) {// 批次返回受理成功,需要轮询结果
			trp.setTradeStatus(TradeStatus.RETRY);
		} else {
			trp.setTradeStatus(TradeStatus.FAILURE);
		}

		return trp;
	}

	final BigDecimal feeRate = new BigDecimal("0.0012");// 认证支付费用比例
	final BigDecimal feeUnit = new BigDecimal("1.5");// 代付费用单位

	@Override
	public BigDecimal calculationCost(ThirdPayRecord tr) {
		if (PayType.PAYOUT == tr.getPayType()) {
			return this.calculationPay(1);
		}
		return this.calculationAuth(tr.getOrderAmount());
	}

	@Override
	public BigDecimal getRechargeFeeRate() {
		return feeRate;
	}

	private BigDecimal calculationAuth(BigDecimal amount) {
		BigDecimal cost = amount.multiply(feeRate);
		return cost.compareTo(BigDecimalUtils.TWO) <= 0 ? BigDecimalUtils.TWO : cost.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	private BigDecimal calculationPay(int size) {
		return feeUnit.multiply(new BigDecimal(size));
	}

}
