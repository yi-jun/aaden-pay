package com.aaden.pay.service.comm.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.comm.enums.TradeStatus;
import com.aaden.pay.api.comm.model.ThirdPayValidcode;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.orm.BaseDao;
import com.aaden.pay.core.orm.exception.DataBaseAccessException;
import com.aaden.pay.core.page.Page;
import com.aaden.pay.service.comm.service.ThirdPayValidcodeService;

/**
 *  @Description 充值验证码
 *  @author aaden
 *  @date 2017年12月15日
 */
@Service
public class ThirdPayValidcodeServiceImpl implements ThirdPayValidcodeService{

	private SimpleLogger logger = SimpleLogger.getLogger(this.getClass());
	
	@Autowired
	private BaseDao<ThirdPayValidcode> thirdPayValidcodeDao;
	
	private String mapper=ThirdPayValidcode.class.getName() + "Mapper.";
	
	public ThirdPayValidcode findByKey(String key) {
		return thirdPayValidcodeDao.get(mapper+"selectByPrimaryKey", key);
	}

	public Page<ThirdPayValidcode> getPage(ThirdPayValidcode thirdPayValidcode, Page<ThirdPayValidcode> page) {
		if (null != page)
			thirdPayValidcodeDao.getList(mapper+"getList", thirdPayValidcode, page);
		return page;
	}

	@Override
	public boolean save(String user, ThirdPayValidcode obj) throws DataBaseAccessException{
		try {
			return thirdPayValidcodeDao.save(mapper+"insertByPrimaryKey", obj) > 0;
		} catch (DataBaseAccessException e) {
			logger.error("create ThirdPayValidcode DataBaseAccessException", e);
			throw e;
		}
	}

	@Override
	public boolean update(String user, ThirdPayValidcode obj) throws DataBaseAccessException{
		try {
			return thirdPayValidcodeDao.update(mapper+"updateByPrimaryKey", obj) > 0;
		} catch (DataBaseAccessException e) {
			logger.error("update ThirdPayValidcode DataBaseAccessException", e);
			throw e;
		}
	}

	@Override
	public boolean delete(String user, ThirdPayValidcode obj) throws DataBaseAccessException{
		try {
			return thirdPayValidcodeDao.delete(mapper+"deleteByPrimaryKey", obj) > 0;
		} catch (DataBaseAccessException e) {
			logger.error("delete ThirdPayValidcode DataBaseAccessException", e);
			throw e;
		}
	}

	@Override
	public ThirdPayValidcode getLast(String userId) {
		ThirdPayValidcode obj = new ThirdPayValidcode();
		obj.setUserId(userId);
		obj.setTradeStatus(TradeStatus.SUCCEED);
		return thirdPayValidcodeDao.get(mapper + "getLast", obj);
	}
}
