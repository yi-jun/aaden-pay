package com.aaden.pay.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.PayTaskService;
import com.aaden.pay.api.PaymentService;
import com.aaden.pay.api.biz.vo.PayResponse;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.PayType;
import com.aaden.pay.api.comm.enums.TradeStatus;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.orm.exception.DataBaseAccessException;
import com.aaden.pay.core.utils.CollectionUtils;
import com.aaden.pay.core.utils.DateUtils;
import com.aaden.pay.service.biz.route.ThirdPayRoute;
import com.aaden.pay.service.biz.vo.ThirdPayResponse;
import com.aaden.pay.service.comm.service.ThirdPayRecordService;

/**
 *  @Description 支付任务实现
 *  @author aaden
 *  @date 2017年12月16日
 */
@Service
public class PayTaskServiceImpl implements PayTaskService {
	SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	@Autowired
	private ThirdPayRoute thirdPayRoute;
	@Autowired
	private ThirdPayRecordService payRecordService;
	@Autowired
	private PaymentService paymentService;

	final String LOG_TEMPLATE = "--------支付记录对账结束,支付流水号:%s,支付结果:%s,支付响应信息:%s---------";

	@Override
	public void checkNotsureTask() {
		logger.info("--------------------自动对账任务开始----------------------");
		List<ThirdPayRecord> list = payRecordService.getNotSureList();
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		for (ThirdPayRecord item : list) {
			try {
				PayResponse response = paymentService.recheck(item.getSerialnumber());
				logger.info(String.format(LOG_TEMPLATE, item.getSerialnumber(), response.getTradeStatus(), response.getMsg()));
				// 可填充业务回调的接口
			} catch (Exception e) {
				logger.error("对账任务出现异常!", e);
			}

		}
		logger.info("--------------------自动对账任务结束----------------------");
	}

	@Override
	public void checkYestday() {

		Date today = DateUtils.todayFormat();

		List<ThirdPayResponse> list = new ArrayList<ThirdPayResponse>();
		// 宝付
		list.addAll(this.sendCheck(PayChannel.BAOFOO, PayType.PAYOUT, today));
		list.addAll(this.sendCheck(PayChannel.BAOFOO, PayType.AUTHPAY, today));
		// 通联
		list.addAll(this.sendCheck(PayChannel.ALLIN, PayType.AUTHPAY, today));

		if (CollectionUtils.isEmpty(list)) {
			return;
		}

		// 根据对账文件结果,更新本地数据库状态
		this.checkTradeStatus(list);
	}

	private void checkTradeStatus(List<ThirdPayResponse> list) {

		Map<String, ThirdPayResponse> map = new HashMap<>();
		for (ThirdPayResponse item : list) {
			map.put(item.getSerialnumber(), item);
		}

		List<ThirdPayRecord> pays = payRecordService.getList(new ArrayList<String>(map.keySet()));
		for (ThirdPayRecord item : pays) {
			ThirdPayResponse resp = map.get(item.getSerialnumber());
			if (item.getTradeStatus().equals(resp.getTradeStatus()))
				continue;
			if (item.isSuccess())
				continue;

			ThirdPayRecord obj = new ThirdPayRecord();
			obj.setOldTradeStatus(item.getTradeStatus());
			obj.setTradeId(item.getTradeId());
			obj.setUpdateTime(DateUtils.today());
			if (resp.isSuccessed()) {
				obj.setActAmount(resp.getActAmount());
				obj.setTradeStatus(TradeStatus.SUCCEED);
			} else if (resp.isFailure()) {
				obj.setTradeStatus(TradeStatus.FAILURE);
			}
			try {
				payRecordService.update("autoTask", obj);
			} catch (DataBaseAccessException e) {
			}
		}
	}

	private List<ThirdPayResponse> sendCheck(PayChannel payChannel, PayType payType, Date date) {
		try {
			return thirdPayRoute.route(payChannel, payType).queryTrade(date);
		} catch (Exception e) {
			logger.error("支付对账任务出现异常:", e);
			return new ArrayList<ThirdPayResponse>();
		}
	}

}
