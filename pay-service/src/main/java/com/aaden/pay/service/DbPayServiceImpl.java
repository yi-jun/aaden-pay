package com.aaden.pay.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.DbPayService;
import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.model.ThirdPayQuota;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.api.comm.model.ThirdPayValidcode;
import com.aaden.pay.core.orm.OrmUtil;
import com.aaden.pay.core.orm.exception.DataBaseAccessException;
import com.aaden.pay.core.page.Page;
import com.aaden.pay.core.utils.CollectionUtils;
import com.aaden.pay.service.comm.service.ThirdPayQuotaService;
import com.aaden.pay.service.comm.service.ThirdPayRecordService;
import com.aaden.pay.service.comm.service.ThirdPayValidcodeService;

/**
 *  @Description 支付记录数据库实现
 *  @author aaden
 *  @date 2017年12月24日
 */
@Service
public class DbPayServiceImpl implements DbPayService {

	@Autowired
	private ThirdPayRecordService thirdPayRecordService;
	@Autowired
	private ThirdPayQuotaService thirdPayQuotaService;
	@Autowired
	private ThirdPayValidcodeService thirdPayValidcodeService;

	Comparator<ThirdPayQuota> cp = new Comparator<ThirdPayQuota>() {
		@Override
		public int compare(ThirdPayQuota o1, ThirdPayQuota o2) {
			if (o1.getSingleAmount().compareTo(o2.getSingleAmount()) == 0) {
				return o2.getPayChannel().compareTo(o1.getPayChannel());
			}

			return o2.getSingleAmount().compareTo(o1.getSingleAmount());
		}
	};

	@Override
	public Page<ThirdPayRecord> getPayRecordPage(ThirdPayRecord record, String pageNo, String pageSize) {
		Page<ThirdPayRecord> page = new Page<>();
		OrmUtil.formatPageSize(pageNo, pageSize, page);
		return thirdPayRecordService.getPage(record, page);
	}

	@Override
	public List<ThirdPayRecord> getListByOrderNo(String orderNo) {
		ThirdPayRecord db = new ThirdPayRecord();
		db.setOrderCode(orderNo);
		return this.getPayRecordList(db);
	}

	@Override
	public ThirdPayRecord getBySerialnumber(String localSeriNum) {
		return thirdPayRecordService.findBySerialnumber(localSeriNum);
	}

	@Override
	public List<ThirdPayRecord> getPayRecordList(ThirdPayRecord db) {
		return thirdPayRecordService.getList(db);
	}

	@Override
	public List<ThirdPayRecord> getPayRecordList(List<String> payOrderNos) {
		return thirdPayRecordService.getList(payOrderNos);
	}

	@Override
	public BigDecimal getSuccessAmount(String orderNo) {
		BigDecimal sum = BigDecimal.ZERO;
		List<ThirdPayRecord> list = this.getListByOrderNo(orderNo);
		if (list != null && !list.isEmpty()) {
			for (ThirdPayRecord item : list) {
				if (item.isSuccess()) {
					sum = sum.add(item.getActAmount());
				}
			}
		}
		return sum;
	}

	@Override
	public List<ThirdPayQuota> getPayQuotaList(ThirdPayQuota quota) {
		List<ThirdPayQuota> list = thirdPayQuotaService.getList(quota);
		Collections.sort(list, cp);
		return list;
	}

	@Override
	public Page<ThirdPayQuota> getPayQuotaPage(ThirdPayQuota quota, String pageNo, String pageSize) {
		Page<ThirdPayQuota> page = new Page<>();
		OrmUtil.formatPageSize(pageNo, pageSize, page);
		return thirdPayQuotaService.getPage(quota, page);
	}

	@Override
	public ThirdPayQuota getPayQuota(PayChannel payChannel, BankType bankType) {
		ThirdPayQuota quota = new ThirdPayQuota();
		quota.setPayChannel(payChannel);
		quota.setBankType(bankType);
		List<ThirdPayQuota> list = this.getPayQuotaList(quota);
		if (CollectionUtils.isEmpty(list))
			return null;
		return list.get(0);

	}

	@Override
	public Page<ThirdPayValidcode> getPayValidcodePage(ThirdPayValidcode validcode, String pageNo, String pageSize) {
		Page<ThirdPayValidcode> page = new Page<>();
		OrmUtil.formatPageSize(pageNo, pageSize, page);
		return thirdPayValidcodeService.getPage(validcode, page);
	}

	@Override
	public boolean updateCallbackYes(String orderNumOrSerialnum) {
		try {
			return thirdPayRecordService.updateCallbackYes(orderNumOrSerialnum);
		} catch (DataBaseAccessException e) {
			return Boolean.FALSE;
		}

	}

	@Override
	public boolean saveThirdPayQuota(String user, ThirdPayQuota quota) {
		ThirdPayQuota db = thirdPayQuotaService.getPayQuota(quota.getPayChannel(), quota.getBankType());

		if (db != null) {// 已存在,则更新
			quota.setQuotaId(db.getQuotaId());
			return this.updateThirdPayQuota(user, quota);
		}

		try {
			return thirdPayQuotaService.save(user, quota);
		} catch (DataBaseAccessException e) {
			return Boolean.FALSE;
		}
	}

	@Override
	public boolean updateThirdPayQuota(String user, ThirdPayQuota quota) {
		try {
			return thirdPayQuotaService.update(user, quota);
		} catch (DataBaseAccessException e) {
			return Boolean.FALSE;
		}
	}
}
