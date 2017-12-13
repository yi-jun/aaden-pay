package com.aaden.pay.service.comm.service;

import java.util.List;

import com.aaden.pay.api.comm.enums.BankVerifyType;
import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.core.orm.exception.DataBaseAccessException;
import com.aaden.pay.core.page.Page;

/**
 *  @Description 银行卡验证签约记录
 *  @author aaden
 *  @date 2017年12月25日
 */
public interface ThirdBankSendService {

	/**
	 * 带条件的分页查询
	 * 
	 * @param page
	 */
	public Page<ThirdBankSend> getPage(ThirdBankSend t, Page<ThirdBankSend> page);

	/**
	 * 查询
	 * 
	 * @param key
	 * 
	 * @return String 提示信息
	 */
	public ThirdBankSend findByKey(String key);

	/**
	 * 保存
	 * 
	 * @param user
	 *            操作的用户
	 * @param t
	 *            实例对象
	 * @return String 提示信息
	 * @throws DataBaseAccessException
	 */
	public boolean save(String user, ThirdBankSend t) throws DataBaseAccessException;

	/**
	 * 修改
	 * 
	 * @param user
	 *            操作的用户
	 * @param t
	 *            实例对象
	 * @return String 提示信息
	 */
	public boolean update(String user, ThirdBankSend t) throws DataBaseAccessException;

	/**
	 * 删除
	 * 
	 * @param user
	 *            操作的用户
	 * @param t
	 *            实例对象
	 * @return String 提示信息
	 */
	public boolean delete(String user, ThirdBankSend t) throws DataBaseAccessException;

	public List<ThirdBankSend> getList(ThirdBankSend obj);

	/**
	 * 获取最后一个验卡记录
	 */
	public ThirdBankSend getLast(String userId, BankVerifyType type);

}
