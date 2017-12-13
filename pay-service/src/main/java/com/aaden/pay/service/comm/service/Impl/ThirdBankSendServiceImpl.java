package com.aaden.pay.service.comm.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.comm.enums.BankVerifyType;
import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.core.eumus.IsValid;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.orm.BaseDao;
import com.aaden.pay.core.orm.exception.DataBaseAccessException;
import com.aaden.pay.core.page.Page;
import com.aaden.pay.core.utils.SecureUtil;
import com.aaden.pay.service.comm.service.ThirdBankSendService;

/**
 *  @Description 银行卡签约验证
 *  @author aaden
 *  @date 2017年12月3日
 */
@Service
public class ThirdBankSendServiceImpl implements ThirdBankSendService {

	private SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	@Autowired
	private BaseDao<ThirdBankSend> bankSendDao;

	private String mapper = ThirdBankSend.class.getName() + "Mapper.";

	private void decordCardNo(ThirdBankSend record) {
		if (record == null)
			return;
		try {
			record.setCardNo(SecureUtil.decodeCard(record.getCardNo()));
		} catch (Exception e) {
		}
	}

	private void encodeCardNo(ThirdBankSend record) {
		if (record == null)
			return;
		try {
			record.setCardNo(SecureUtil.encodeCard(record.getCardNo()));
		} catch (Exception e) {
		}
	}

	public ThirdBankSend findByKey(String key) {
		ThirdBankSend record = bankSendDao.get(mapper + "selectByPrimaryKey", key);
		this.decordCardNo(record);
		return record;
	}

	public Page<ThirdBankSend> getPage(ThirdBankSend bankSend, Page<ThirdBankSend> page) {
		if (null != page)
			bankSendDao.getList(mapper + "getList", bankSend, page);
		if (page!=null&&page.getResult() != null) {
			for (ThirdBankSend record : page.getResult()) {
				this.decordCardNo(record);
			}
		}
		return page;
	}

	@Override
	public boolean save(String user, ThirdBankSend obj) throws DataBaseAccessException {
		try {
			this.encodeCardNo(obj);
			return bankSendDao.save(mapper + "insertByPrimaryKey", obj) > 0;
		} catch (DataBaseAccessException e) {
			logger.error("create BankSend DataBaseAccessException", e);
			throw e;
		} finally {
			this.decordCardNo(obj);
		}
	}

	@Override
	public boolean update(String user, ThirdBankSend obj) throws DataBaseAccessException {
		try {
			this.encodeCardNo(obj);
			return bankSendDao.update(mapper + "updateByPrimaryKey", obj) > 0;
		} catch (DataBaseAccessException e) {
			logger.error("update BankSend DataBaseAccessException", e);
			throw e;
		} finally {
			this.decordCardNo(obj);
		}
	}

	@Override
	public boolean delete(String user, ThirdBankSend obj) throws DataBaseAccessException {
		try {
			return bankSendDao.delete(mapper + "deleteByPrimaryKey", obj) > 0;
		} catch (DataBaseAccessException e) {
			logger.error("delete BankSend DataBaseAccessException", e);
			throw e;
		}
	}

	@Override
	public List<ThirdBankSend> getList(ThirdBankSend obj) {
		List<ThirdBankSend> list = bankSendDao.getList(mapper + "getList", obj);
		if (list != null && !list.isEmpty()) {
			for (ThirdBankSend bankSend : list) {
				this.decordCardNo(bankSend);
			}
		}
		return list;
	}

	@Override
	public ThirdBankSend getLast(String userId, BankVerifyType type) {
		ThirdBankSend obj = new ThirdBankSend();
		obj.setUserId(userId);
		obj.setBankVerifyType(type);
		obj.setIsValid(IsValid.VALID.getValue());
		ThirdBankSend send = bankSendDao.get(mapper + "getLast", obj);
		this.decordCardNo(send);
		return send;
	}

}
