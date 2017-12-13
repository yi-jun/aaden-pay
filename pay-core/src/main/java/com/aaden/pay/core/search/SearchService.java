package com.aaden.pay.core.search;

import java.util.List;

import com.aaden.pay.core.search.exception.SearchException;
import com.aaden.pay.core.search.model.IndexModel;

/**
 *  @Description 全文搜索服务
 *  @author aaden
 *  @date 2017年12月3日
 */
public interface SearchService {

	/**
	 * 批量创建索引
	 */
	public void createIndex(List<IndexModel> list) throws SearchException;

	/**
	 * 
	 * @Description: 支行信息检索
	 * @param cityCode城市编号
	 *            ,可选
	 * @param bankCode银行编号
	 *            ,可选
	 * @param keyWord关键字
	 *            ,可选
	 */
	public List<IndexModel> queryBrank(String cityCode, String bankCode, String keyWord);

}
