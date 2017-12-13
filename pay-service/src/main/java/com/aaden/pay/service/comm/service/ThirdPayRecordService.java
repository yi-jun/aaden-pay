package com.aaden.pay.service.comm.service;

import java.util.List;

import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.core.orm.exception.DataBaseAccessException;
import com.aaden.pay.core.page.Page;

/**
 *  @Description 支付记录
 *  @author aaden
 *  @date 2017年12月19日
 */
public interface ThirdPayRecordService {

	/**
	 * 逻辑主键查询
	 * 
	 * @param key
	 * @return ThirdPayRecord
	 */
	public ThirdPayRecord findByKey(String key);

	/**
	 * 交易流水号查询
	 * 
	 * @param key
	 * @return
	 */
	public ThirdPayRecord findBySerialnumber(String key);

	/**
	 * 列表查询
	 * 
	 * @param thirdPayRecord
	 * @param page
	 * @return
	 */
	public Page<ThirdPayRecord> getPage(ThirdPayRecord thirdPayRecord, Page<ThirdPayRecord> page);

	public List<ThirdPayRecord> getList(ThirdPayRecord db);

	/**
	 * 订单号,或者流水号 ,查询
	 */
	public List<ThirdPayRecord> getList(List<String> keys);

	public boolean save(String user, ThirdPayRecord obj) throws DataBaseAccessException;

	public boolean update(String user, ThirdPayRecord obj) throws DataBaseAccessException;

	public boolean delete(String user, ThirdPayRecord obj) throws DataBaseAccessException;

	public boolean updateCallbackYes(String orderNumOrSerialnum) throws DataBaseAccessException;

	/**
	 * 
	 * 自动任务获取不确定的支付订单列表
	 */
	public List<ThirdPayRecord> getNotSureList();

}
