package com.aaden.pay.service.biz.route;

import com.aaden.pay.api.DbPayService;
import com.aaden.pay.api.biz.constants.PaymentConstans;
import com.aaden.pay.api.biz.enums.allinpay.AllinPayBankType;
import com.aaden.pay.api.biz.enums.baofoo.BaofooBankType;
import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.enums.PayType;
import com.aaden.pay.api.comm.model.ThirdPayQuota;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Description 充值路由
 * @author aaden
 * @date 2017年12月18日
 */
@Service
public class RechargeRoute {

	SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	@Autowired
	private DbPayService paymentQueryService;
	@Autowired
	private ThirdPayRoute thirdPayRoute;

	// 按单笔支付额度倒序排序,额度相同按费率由小而大排序
	Comparator<ThirdPayQuota> cp = new Comparator<ThirdPayQuota>() {
		@Override
		public int compare(ThirdPayQuota o1, ThirdPayQuota o2) {
			if (o1.getSingleAmount().compareTo(o2.getSingleAmount()) == 0) {
				return o1.getFeeRate().compareTo(o2.getFeeRate());
			}

			return o2.getSingleAmount().compareTo(o1.getSingleAmount());
		}
	};

	static List<ThirdPayQuota> netsaveList = new ArrayList<>();
	static {
		netsaveList.add(buildNetsaveQuota(BankType.BOC, PayChannel.ALLIN));
		netsaveList.add(buildNetsaveQuota(BankType.ABC, PayChannel.ALLIN));
		netsaveList.add(buildNetsaveQuota(BankType.ICBC, PayChannel.ALLIN));
		netsaveList.add(buildNetsaveQuota(BankType.CCB, PayChannel.ALLIN));
		netsaveList.add(buildNetsaveQuota(BankType.BOCO, PayChannel.ALLIN));
		netsaveList.add(buildNetsaveQuota(BankType.CITIC, PayChannel.ALLIN));
		netsaveList.add(buildNetsaveQuota(BankType.CEB, PayChannel.ALLIN));
		netsaveList.add(buildNetsaveQuota(BankType.CMBC, PayChannel.ALLIN));
		netsaveList.add(buildNetsaveQuota(BankType.GDB, PayChannel.ALLIN));
		netsaveList.add(buildNetsaveQuota(BankType.CMB, PayChannel.ALLIN));
		netsaveList.add(buildNetsaveQuota(BankType.CIB, PayChannel.ALLIN));
		netsaveList.add(buildNetsaveQuota(BankType.PSBC, PayChannel.ALLIN));
		netsaveList.add(buildNetsaveQuota(BankType.PAB, PayChannel.ALLIN));
		netsaveList.add(buildNetsaveQuota(BankType.HXB, PayChannel.ALLIN));
	}

	/**
	 * 获取网银充值银行列表
	 */
	public List<ThirdPayQuota> getNetsaveList() {
		return netsaveList;
	}

	/**
	 * 获取网银充值渠道信息
	 */
	public ThirdPayQuota getNetsaveQuota(BankType bankType) throws Exception {
		for (ThirdPayQuota item : netsaveList) {
			if (bankType == item.getBankType())
				return item;
		}
		throw new Exception("系统暂不支持该银行卡");
	}

	public ThirdPayQuota autoMatch(BankType bankType) throws Exception {

		ThirdPayQuota query = new ThirdPayQuota();
		query.setBankType(bankType);
		List<ThirdPayQuota> list = paymentQueryService.getPayQuotaList(query);
		if (CollectionUtils.isEmpty(list)) {
			return this.getDefaultQuota(bankType);
		}

		// 设置费率
		for (ThirdPayQuota item : list) {
			BigDecimal feeRate = thirdPayRoute.route(item.getPayChannel(), PayType.AUTHPAY).getRechargeFeeRate();
			item.setFeeRate(feeRate);
		}

		Collections.sort(list, cp);
		for (ThirdPayQuota item : list) {
			if (isSupport(item.getPayChannel(), bankType))
				return item;

		}

		// 数据库配置的不支持银行卡时,获取默认
		return this.getDefaultQuota(bankType);
	}

	// 数据库未找到配置信息,按费率排序
	private ThirdPayQuota getDefaultQuota(BankType bankType) throws Exception {
		List<ThirdPayQuota> list = new ArrayList<ThirdPayQuota>();
		for (PayChannel item : PayChannel.values()) {
			if (isSupport(item, bankType)) {
				ThirdPayQuota q = new ThirdPayQuota();
				q.setPayChannel(item);
				q.setBankType(bankType);
				q.setSingleAmount(PaymentConstans.MAX_RECHARGE_AMOUNT);
				q.setRemark("");
				BigDecimal feeRate = thirdPayRoute.route(item, PayType.AUTHPAY).getRechargeFeeRate();
				q.setFeeRate(feeRate);
				list.add(q);
			}
		}

		if (list.isEmpty()) {
			throw new Exception("系统暂不支持该银行卡");
		}
		Collections.sort(list, cp);
		ThirdPayQuota quota = paymentQueryService.getPayQuota(list.get(0).getPayChannel(), bankType);
		return quota == null ? list.get(0) : quota;
	}

	private boolean isSupport(PayChannel channel, BankType bankType) {

		boolean allinSupport = channel == PayChannel.ALLIN && AllinPayBankType.parse(bankType) != null;
		boolean baofooSupport = channel == PayChannel.BAOFOO && BaofooBankType.parse(bankType) != null;

		return allinSupport || baofooSupport;
	}

	private static ThirdPayQuota buildNetsaveQuota(BankType type, PayChannel chanel) {
		ThirdPayQuota p = new ThirdPayQuota();
		p.setPayChannel(chanel);
		p.setBankType(type);
		p.setRemark(PaymentConstans.getGatewayLimit(type));
		p.setSingleAmount(PaymentConstans.MAX_RECHARGE_AMOUNT);
		return p;
	}

}
