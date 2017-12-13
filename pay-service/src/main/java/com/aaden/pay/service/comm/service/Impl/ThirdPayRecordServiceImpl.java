package com.aaden.pay.service.comm.service.Impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.comm.enums.TradeStatus;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.core.eumus.YesOrNo;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.orm.BaseDao;
import com.aaden.pay.core.orm.exception.DataBaseAccessException;
import com.aaden.pay.core.page.Page;
import com.aaden.pay.core.utils.SecureUtil;
import com.aaden.pay.service.comm.service.ThirdPayRecordService;

/**
 * @Description 第三方交易记录
 * @author aaden
 * @date 2017年12月2日
 */
@Service
public class ThirdPayRecordServiceImpl implements ThirdPayRecordService {

	private SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	@Autowired
	private BaseDao<ThirdPayRecord> thirdPayRecordDao;

	private String mapper = ThirdPayRecord.class.getName() + "Mapper.";

	private void decordCardNo(ThirdPayRecord record) {
		if (record == null)
			return;
		try {
			record.setCardNo(SecureUtil.decodeCard(record.getCardNo()));
		} catch (Exception e) {
		}
	}

	private void encodeCardNo(ThirdPayRecord record) {
		if (record == null)
			return;
		try {
			record.setCardNo(SecureUtil.encodeCard(record.getCardNo()));
		} catch (Exception e) {
		}
	}

	@Override
	public ThirdPayRecord findByKey(String key) {
		ThirdPayRecord record = thirdPayRecordDao.get(mapper + "selectByPrimaryKey", key);
		this.decordCardNo(record);
		return record;
	}

	@Override
	public ThirdPayRecord findBySerialnumber(String key) {
		ThirdPayRecord record = thirdPayRecordDao.get(mapper + "selectBySerial", key);
		this.decordCardNo(record);
		return record;
	}

	@Override
	public Page<ThirdPayRecord> getPage(ThirdPayRecord thirdPayRecord, Page<ThirdPayRecord> page) {
		this.encodeCardNo(thirdPayRecord);
		if (null != page) {
			thirdPayRecordDao.getList(mapper + "getList", thirdPayRecord, page);
			for (ThirdPayRecord item : page.getResult()) {
				this.decordCardNo(item);
			}
		}
		this.decordCardNo(thirdPayRecord);
		return page;
	}

	@Override
	public boolean save(String user, ThirdPayRecord obj) throws DataBaseAccessException {
		try {
			this.encodeCardNo(obj);
			return thirdPayRecordDao.save(mapper + "insertByPrimaryKey", obj) > 0;
		} catch (DataBaseAccessException e) {
			logger.error("create ThirdPayRecord DataBaseAccessException", e);
			throw e;
		} finally {
			this.decordCardNo(obj);
		}
	}

	@Override
	public boolean update(String user, ThirdPayRecord obj) throws DataBaseAccessException {
		try {
			this.encodeCardNo(obj);
			return thirdPayRecordDao.update(mapper + "updateByPrimaryKey", obj) > 0;
		} catch (DataBaseAccessException e) {
			logger.error("update ThirdPayRecord DataBaseAccessException", e);
			throw e;
		} finally {
			this.decordCardNo(obj);
		}
	}

	@Override
	public boolean delete(String user, ThirdPayRecord obj) throws DataBaseAccessException {
		return false;
	}

	@Override
	public boolean updateCallbackYes(String orderNumOrSerialnum) throws DataBaseAccessException {
		ThirdPayRecord obj = new ThirdPayRecord();
		obj.setOrderCode(orderNumOrSerialnum);
		obj.setSerialnumber(orderNumOrSerialnum);
		obj.setCallbackStatus(YesOrNo.YES.getCode());
		obj.setUpdateTime(new Date());
		return thirdPayRecordDao.update(mapper + "updateCallBackStatus", obj) > 0;
	}

	@Override
	public List<ThirdPayRecord> getNotSureList() {
		TradeStatus[] array = new TradeStatus[] { TradeStatus.RETRY };
		List<ThirdPayRecord> list = thirdPayRecordDao.getList(mapper + "getNotSureList", array);
		if (list != null)
			for (ThirdPayRecord record : list) {
				this.decordCardNo(record);
			}
		return list;
	}

	@Override
	public List<ThirdPayRecord> getList(ThirdPayRecord thirdPayRecord) {
		List<ThirdPayRecord> list = thirdPayRecordDao.getList(mapper + "getList", thirdPayRecord);
		if (list != null)
			for (ThirdPayRecord record : list) {
				this.decordCardNo(record);
			}
		return list;
	}

	@Override
	public List<ThirdPayRecord> getList(List<String> keys) {
		if (keys == null || keys.isEmpty()) {
			return null;
		}
		List<ThirdPayRecord> list = thirdPayRecordDao.getList(mapper + "getListByKeys", keys);
		if (list != null)
			for (ThirdPayRecord record : list) {
				this.decordCardNo(record);
			}
		return list;
	}

}
