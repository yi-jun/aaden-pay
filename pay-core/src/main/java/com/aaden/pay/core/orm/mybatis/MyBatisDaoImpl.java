package com.aaden.pay.core.orm.mybatis;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import com.aaden.pay.core.orm.BaseDao;
import com.aaden.pay.core.orm.exception.DataBaseAccessException;
import com.aaden.pay.core.page.Page;
import com.aaden.pay.core.utils.CollectionUtils;

/**
 *  @Description mybatis的中转代理类
 *  @author aaden
 *  @date 2017年12月9日
 */
public class MyBatisDaoImpl<T> extends SqlSessionDaoSupport implements BaseDao<T> {

	private static final String COUNT = "_Count";
	private static final int BATCH_MAX_COUNT = 10000;

	/**
	 * 
	 * @param key
	 * 
	 * @param entity
	 */
	public int save(String key, T entity) throws DataBaseAccessException {
		try {
			return getSqlSession().insert(key, entity);
		} catch (Exception e) {
			throw new DataBaseAccessException(getClass().getName() + " save exception: ", e);
		}
	}

	/**
	 * 
	 * @param key
	 * 
	 * @param entity
	 */
	public int save(String key, List<T> entity) throws DataBaseAccessException {
		if (CollectionUtils.isEmpty(entity))
			return 0;

		int succ = 0;
		List<List<T>> ll = CollectionUtils.split(entity, BATCH_MAX_COUNT);
		try {
			for (List<T> sub : ll)
				succ = succ + getSqlSession().insert(key, sub);
		} catch (Exception e) {
			throw new DataBaseAccessException(getClass().getName() + " save exception: ", e);
		}
		return succ;

	}

	/**
	 * 
	 * @param key
	 * 
	 * @param entity
	 */
	public int update(String key, T entity) throws DataBaseAccessException {
		try {
			return getSqlSession().update(key, entity);
		} catch (Exception e) {
			throw new DataBaseAccessException(getClass().getName() + " update exception: ", e);
		}
	}

	public int update(String key, Map<Object, T> entity) throws DataBaseAccessException {
		try {
			return getSqlSession().update(key, entity);
		} catch (Exception e) {
			throw new DataBaseAccessException(getClass().getName() + " update exception: ", e);
		}
	}

	@Override
	public int update(String key, List<T> entity) throws DataBaseAccessException {
		if (CollectionUtils.isEmpty(entity))
			return 0;

		int succ = 0;
		// 拆分,防止批量数据过大
		List<List<T>> ll = CollectionUtils.split(entity, BATCH_MAX_COUNT);
		try {
			for (List<T> sub : ll)
				succ = succ + getSqlSession().update(key, sub);
		} catch (Exception e) {
			throw new DataBaseAccessException(getClass().getName() + " update exception: ", e);
		}

		return succ;
	}

	/**
	 * 
	 * @param key
	 * 
	 * @param id
	 */
	public int delete(String key, Serializable id) throws DataBaseAccessException {
		try {
			return getSqlSession().delete(key, id);
		} catch (Exception e) {
			throw new DataBaseAccessException(getClass().getName() + " delete exception: ", e);
		}
	}

	public int delete(String key, T entity) throws DataBaseAccessException {
		try {
			return getSqlSession().delete(key, entity);
		} catch (Exception e) {
			throw new DataBaseAccessException(getClass().getName() + " delete exception: ", e);
		}
	}

	/**
	 * 
	 * @param key
	 * 
	 * @return List<T>
	 */
	public List<T> getAll(String key) {
		try {
			return getSqlSession().selectList(key);
		} catch (Exception e) {
			logger.error(getClass().getName() + " getAll exception and key is: " + key, e);
			return null;
		}
	}

	/**
	 * 
	 * @param key
	 * 
	 * @param params
	 * 
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public T get(String key, Object params) {
		try {
			return (T) getSqlSession().selectOne(key, params);
		} catch (Exception e) {
			logger.error(getClass().getName() + " get exception and key is: " + key, e);
			return null;
		}
	}

	/**
	 * 
	 * @param key
	 * 
	 * @param params
	 * 
	 * @return List<T>
	 */
	public List<T> getList(String key, Object params) {
		try {
			return getSqlSession().selectList(key, params);
		} catch (Exception e) {
			logger.error(getClass().getName() + " getList exception and key is: " + key, e);
			return null;
		}
	}

	/**
	 * 
	 * @param key
	 * 
	 * @param params
	 * 
	 * @param page
	 * 
	 * @return Page<T>
	 */
	public Page<T> getList(String key, Object params, Page<T> page) {
		try {
			Integer totalCounts = count(key + COUNT, params);
			// add 最大页数判断
			int pageM = maxPage(totalCounts, page.getPageSize(), page.getPageNo());
			if (pageM > 0) {
				page.setPageNo(pageM);
			} // end
			if (totalCounts != null && totalCounts.longValue() > 0) {
				List<T> list = getSqlSession().selectList(key, params,
						new RowBounds(page.getOffset(), page.getPageSize()));
				page.setResult(list);
				page.setTotalCount(totalCounts.longValue());
			}
			return page;
		} catch (Exception e) {
			logger.error(getClass().getName() + " getList exception and key is: " + key, e);
			return null;
		}
	}

	/**
	 * 判断是否超出最大页数
	 * 
	 * @param tcount
	 *            总条数
	 * @param pageS
	 *            每页条数
	 * @param pNo
	 *            当前页
	 * @return int 大于0表示超出最大页数,返回最大页数
	 */
	private int maxPage(Integer tcount, Integer pageS, Integer pNo) {
		int maxPage = tcount % pageS == 0 ? tcount / pageS : tcount / pageS + 1;// 最大页数
		if (maxPage < pNo)
			return maxPage;
		else
			return -1;
	}

	/**
	 * 
	 * @param key
	 * 
	 * @param params
	 * 
	 * @return Integer
	 */
	public Integer count(String key, Object params) {
		try {
			return (Integer) getSqlSession().selectOne(key, params);
		} catch (Exception e) {
			logger.error(getClass().getName() + " count exception and key is: " + key, e);
			return Integer.valueOf(0);
		}
	}

	public Connection getConnection() {
		SqlSessionTemplate st = (SqlSessionTemplate) getSqlSession();
		Connection connection = SqlSessionUtils
				.getSqlSession(st.getSqlSessionFactory(), st.getExecutorType(), st.getPersistenceExceptionTranslator())
				.getConnection();
		return connection;
		// return getSqlSession().getConnection();
	}

}
