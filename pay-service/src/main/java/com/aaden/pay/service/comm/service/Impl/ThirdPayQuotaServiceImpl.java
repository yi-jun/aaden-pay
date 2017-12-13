package com.aaden.pay.service.comm.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.model.ThirdPayQuota;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.orm.BaseDao;
import com.aaden.pay.core.orm.exception.DataBaseAccessException;
import com.aaden.pay.core.page.Page;
import com.aaden.pay.core.utils.CollectionUtils;
import com.aaden.pay.service.comm.service.ThirdPayQuotaService;

/**
 *  @Description 认证支付限额
 *  @author aaden
 *  @date 2017年12月10日
 */
@Service
public class ThirdPayQuotaServiceImpl implements ThirdPayQuotaService {

	private SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	@Autowired
	private BaseDao<ThirdPayQuota> thirdPayQuotaDao;

	private String mapper = ThirdPayQuota.class.getName() + "Mapper.";

	public ThirdPayQuota findByKey(String key) {
		return thirdPayQuotaDao.get(mapper + "selectByPrimaryKey", key);
	}

	public Page<ThirdPayQuota> getPage(ThirdPayQuota thirdPayRechargeQuota, Page<ThirdPayQuota> page) {
		if (null != page)
			thirdPayQuotaDao.getList(mapper + "getList", thirdPayRechargeQuota, page);
		return page;
	}

	@Override
	public boolean save(String user, ThirdPayQuota obj) throws DataBaseAccessException {
		try {
			return thirdPayQuotaDao.save(mapper + "insertByPrimaryKey", obj) > 0;
		} catch (DataBaseAccessException e) {
			logger.error("create ThirdPayQuota DataBaseAccessException", e);
			throw e;
		}
	}

	@Override
	public boolean update(String user, ThirdPayQuota obj) throws DataBaseAccessException {
		try {
			return thirdPayQuotaDao.update(mapper + "updateByPrimaryKey", obj) > 0;
		} catch (DataBaseAccessException e) {
			logger.error("update ThirdPayQuota DataBaseAccessException", e);
			throw e;
		}
	}

	@Override
	public boolean delete(String user, ThirdPayQuota obj) throws DataBaseAccessException {
		try {
			return thirdPayQuotaDao.delete(mapper + "deleteByPrimaryKey", obj) > 0;
		} catch (DataBaseAccessException e) {
			logger.error("delete ThirdPayQuota DataBaseAccessException", e);
			throw e;
		}
	}

	@Override
	public List<ThirdPayQuota> getList(ThirdPayQuota quota) {
		return thirdPayQuotaDao.getList(mapper + "getList", quota);
	}

	@Override
	public ThirdPayQuota getPayQuota(PayChannel payChannel, BankType bankType) {
		ThirdPayQuota quota = new ThirdPayQuota();
		quota.setPayChannel(payChannel);
		quota.setBankType(bankType);
		List<ThirdPayQuota> list = this.getList(quota);
		return CollectionUtils.isEmpty(list) ? null : list.get(0);
	}
}
