package com.aaden.pay.service.biz.tp.baofoo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.biz.vo.PayRequest;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.PayType;
import com.aaden.pay.api.comm.enums.SendStatus;
import com.aaden.pay.api.comm.enums.TradeStatus;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.core.utils.CollectionUtils;
import com.aaden.pay.core.utils.DateUtils;
import com.aaden.pay.service.biz.annotation.ChannelValue;
import com.aaden.pay.service.biz.tp.AbstractThirdPayService;
import com.aaden.pay.service.biz.tp.baofoo.adaptor.BaofooPayAdaptor;
import com.aaden.pay.service.biz.tp.baofoo.vo.TransContent;
import com.aaden.pay.service.biz.tp.baofoo.vo.TransHead;
import com.aaden.pay.service.biz.tp.baofoo.vo.TransRespBF0040002;
import com.aaden.pay.service.biz.tp.baofoo.vo.TransRespBF0040004;
import com.aaden.pay.service.biz.vo.ThirdPayResponse;

/**
 *  @Description 宝付代付实现
 *  @author aaden
 *  @date 2017年12月20日
 */
@Service("baofooPayService")
@ChannelValue(channel = PayChannel.BAOFOO, payType = { PayType.PAYOUT })
public class BaofooPayServiceImpl extends AbstractThirdPayService {

	@Autowired
	private BaofooPayAdaptor adaptor;

	@Override
	public ThirdPayResponse pay(ThirdPayRecord tr, PayRequest payRequest) {

		ThirdPayResponse response = new ThirdPayResponse();
		response.setSerialnumber(tr.getSerialnumber());

		TransContent<TransRespBF0040004> content = adaptor.sendPay(tr, payRequest);

		TransHead head = content == null ? null : content.getTrans_head();
		// 代付只受理,是否成功通过查询接口查询,
		if (head == null || head.isNotSure()) {
			String msg = head == null ? "系统繁忙,请稍后再试!" : head.getReturn_msg();
			response.setPayMessage(msg);
			response.setTradeStatus(TradeStatus.RETRY);
		} else {
			response.setPayCode(head.getReturn_code());
			response.setPayMessage(head.getReturn_msg());
			response.setTradeStatus(TradeStatus.FAILURE);
		}

		String thirdnumber = null;
		if (content != null && !CollectionUtils.isEmpty(content.getTrans_reqDatas())) {
			TransRespBF0040004 resp = content.getTrans_reqDatas().get(0);
			thirdnumber = resp.getTrans_orderid();
		}
		response.setSendStatus(content == null ? SendStatus.FAIL : SendStatus.SUCCEED);
		response.setThirdnumber(thirdnumber);
		return response;
	}

	@Override
	public ThirdPayResponse queryTrade(ThirdPayRecord tr) {
		ThirdPayResponse response = new ThirdPayResponse();
		response.setSerialnumber(tr.getSerialnumber());

		TransContent<TransRespBF0040002> content = adaptor.sendQuery(tr);
		TransHead head = content == null ? null : content.getTrans_head();
		String headCode = head == null ? null : head.getReturn_code();

		if ("0000".equals(headCode)) {// 正常响应
			TransRespBF0040002 resp = content.getTrans_reqDatas().get(0);
			String msg = StringUtils.isBlank(resp.getTrans_remark()) ? resp.getPayMsgByState() : resp.getTrans_remark();
			response.setPayMessage(msg);
			response.setPayCode(resp.getState());
			response.setThirdnumber(resp.getTrans_orderid());
			if (resp.isSuccess()) {
				response.setTradeStatus(TradeStatus.SUCCEED);
				response.setActAmount(tr.getOrderAmount());
			} else if (resp.isNotSure()) {
				response.setTradeStatus(TradeStatus.RETRY);
			} else {
				response.setTradeStatus(TradeStatus.FAILURE);
			}
			// 查无记录,宝付支付查无记录不保证失败,需查询第二天对账文件,建议第二天人工手动处理
			// } else if ("0401".equals(headCode) && this.isTimeOut(tr)) {//
			// response.setPayMessage(head.getReturn_msg());
			// response.setPayCode(headCode);
			// response.setTradeStatus(TradeStatus.FAILURE.getValue());
		} else {// 响应失败
			String msg = head == null ? "系统繁忙,请稍后再试!" : head.getReturn_msg();
			response.setPayMessage(msg);
			response.setPayCode(headCode);
			response.setTradeStatus(TradeStatus.RETRY);
		}
		response.setSendStatus(content == null ? SendStatus.FAIL : SendStatus.SUCCEED);
		return response;
	}

	@Override
	public List<ThirdPayResponse> queryTrade(Date checkDate) {

		List<ThirdPayResponse> list = new ArrayList<>();
		List<String> files = adaptor.getCheckFile(checkDate);
		if (CollectionUtils.isEmpty(files))
			return list;

		for (String line : files) {
			if (StringUtils.isBlank(line))
				continue;
			String[] arr = line.split("\\|");
			try {
				Double.parseDouble(arr[9]);
			} catch (Exception e) {
				continue;
			}
			String sernum = arr[5];// 商户流水号
			if (StringUtils.isBlank(sernum))
				continue;

			list.add(this.parse(arr, sernum));
		}

		return list;
	}

	/*
	 * 商户号|终端号|交易类型|交易子类型|总笔数|总金额|总手续费|清算时间
	 * 100000178|0|00104|00|8|6127.37|91.93|2017-05-10 *
	 * 商户号|终端号|交易类型|交易子类型|宝付订单号|商户代付订单号|批次号|清算日期|订单状态|交易金额|手续费|收款人账号|收款人姓名|
	 * 宝付交易号|代付订单创建时间|退款订单创建时间
	 * 100000178|0|00104|00|16868350|TI1494405152546||2017-05-10|1|1.00|0.02
	 * |||16868350|2017-05-10 16:32:19|
	 */
	private ThirdPayResponse parse(String[] arr, String sernum) {
		ThirdPayResponse rsp = new ThirdPayResponse();
		String status = arr[3];// 成功 00,退款 01,撤销 02
		if ("00".equals(status)) {
			rsp.setTradeStatus(TradeStatus.SUCCEED);
		} else {
			rsp.setTradeStatus(TradeStatus.FAILURE);
		}
		Date settle = DateUtils.parseAuto(arr[7]);
		if (settle != null)
			settle = DateUtils.parseAuto(DateUtils.formatDate(settle));
		BigDecimal amt = new BigDecimal(arr[9]);
		BigDecimal fee = new BigDecimal(arr[10]);

		rsp.setSettleTime(settle);
		rsp.setSerialnumber(sernum);
		rsp.setActAmount(amt);
		rsp.setFee(fee);
		return rsp;
	}

	@Override
	public BigDecimal calculationCost(ThirdPayRecord tr) {
		return BigDecimal.ONE;// 代付 1元一笔
	}

}
