package com.aaden.pay.core.orm;

import com.aaden.pay.core.orm.exception.DataBaseAccessException;
import com.aaden.pay.core.page.Page;

/**
 *  @Description 数据库操作接口
 *  @author aaden
 *  @date 2017年12月27日
 */
public interface DbOperateService<T> {

	/**
	 * 带条件的分页查询
	 * 
	 * @param page
	 */
	public Page<T> getPage(T t, Page<T> page);

	/**
	 * 查询
	 * 
	 * @param key
	 * 
	 * @return String 提示信息
	 */
	public T findByKey(String key);

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
	public boolean save(String user, T t) throws DataBaseAccessException;

	/**
	 * 修改
	 * 
	 * @param user
	 *            操作的用户
	 * @param t
	 *            实例对象
	 * @return String 提示信息
	 */
	public boolean update(String user, T t) throws DataBaseAccessException;

	/**
	 * 删除
	 * 
	 * @param user
	 *            操作的用户
	 * @param t
	 *            实例对象
	 * @return String 提示信息
	 */
	public boolean delete(String user, T t) throws DataBaseAccessException;

}
