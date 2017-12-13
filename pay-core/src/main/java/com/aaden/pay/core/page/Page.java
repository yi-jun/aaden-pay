package com.aaden.pay.core.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *  @Description 与具体实现无关的分页参数及查询结果封装
 *  @author aaden
 *  @date 2017年12月25日
 */
public class Page<T> implements Serializable {

	private static final long serialVersionUID = 3166562173078024440L;
	// -- 公共变量 --//
	public static final String ASC = "asc";
	public static final String DESC = "desc";

	// -- 分页参数 --//
	protected int pageNo = 1;
	protected int pageSize = 15; // 默认为每页10条记录
	protected String orderBy = null; // 排序字段
	protected String order = null; // 排序方式
	protected List<T> result = Collections.emptyList(); // 用于封装结果集
	protected long totalCount = 0; // 总记录数

	// -- 构造函数 --//
	public Page() {
	}

	public Page(int pageSize) {
		setPageSize(pageSize);
	}

	public Page(int pageNo, int pageSize) {
		setPageNo(pageNo);
		setPageSize(pageSize);
	}

	// -- 访问查询参数函数 --//
	/**
	 * 获得当前页的页号,序号从1开始,默认为1.
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * 设置当前页的页号,序号从1开始,低于1时自动调整为1.
	 */
	public void setPageNo(final int pageNo) {
		this.pageNo = pageNo;
		if (pageNo < 1) {
			this.pageNo = 1;
		}
	}

	/**
	 * 获得每页的记录数量,默认为20
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 设置每页的记录数量,低于1时自动调整为1.
	 */
	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
		if (pageSize < 1) {
			this.pageSize = 1;
		}
	}

	/**
	 * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置,序号从1开始
	 */
	public int getFirst() {
		return ((pageNo - 1) * pageSize) + 1;
	}

	/**
	 * 获得排序字段,无默认值.多个排序字段时用','分隔
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * 设置排序字段,多个排序字段时用','分隔.
	 */
	public void setOrderBy(final String orderBy) {
		this.orderBy = orderBy;
	}

	public Page<T> orderBy(final String theOrderBy) {
		setOrderBy(theOrderBy);
		return this;
	}

	/**
	 * 获得排序方向.
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * 设置排序方式向.
	 * 
	 * @param order
	 *            可选值为desc或asc,多个排序字段时用','分隔.
	 */
	public void setOrder(final String order) {
		String lowcaseOrder = StringUtils.lowerCase(order);
		// 检查order字符串的合法值
		String[] orders = StringUtils.split(lowcaseOrder, ',');
		for (String orderStr : orders) {
			if (!StringUtils.equals(DESC, orderStr) && !StringUtils.equals(ASC, orderStr)) {
				throw new IllegalArgumentException("排序方向" + orderStr + "不是合法值");
			}
		}
		this.order = lowcaseOrder;
	}

	public Page<T> order(final String theOrder) {
		setOrder(theOrder);
		return this;
	}

	/**
	 * 是否已设置排序字段,无默认值
	 */
	public boolean isOrderBySetted() {
		return (StringUtils.isNotBlank(orderBy) && StringUtils.isNotBlank(order));
	}

	/**
	 * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置,序号从0开始 用于Mysql
	 */
	public int getOffset() {
		return ((pageNo - 1) * pageSize);
	}

	/**
	 * 实现Iterable接口,可以for(Object item : page)遍历使用
	 */
	@SuppressWarnings("unchecked")
	public Iterator<T> iterator() {
		return result == null ? IteratorUtils.EMPTY_ITERATOR : result.iterator();
	}

	// -- 访问查询结果函数 --//

	/**
	 * 取得页内的记录列表.
	 */
	public List<T> getResult() {
		return result;
	}

	/**
	 * 设置页内的记录列表.
	 */
	public void setResult(final List<T> result) {
		this.result = result;
	}

	/**
	 * 取得总记录数, 默认值为-1.
	 */
	public long getTotalCount() {
		return totalCount;
	}

	/**
	 * 设置总记录数.
	 */
	public void setTotalCount(final long totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * 是否第一页.
	 */
	public boolean getFirstPage() {
		return pageNo == 1;
	}

	/**
	 * 是否还有上一页
	 */
	public boolean getHasPrePage() {
		return (pageNo - 1 >= 1);
	}

	/**
	 * 是否还有下一页.
	 */
	public boolean getHasNextPage() {
		return (pageNo + 1 <= getTotalPages());
	}

	/**
	 * 是否最后一页.
	 */
	public boolean getLastPage() {
		return pageNo == getTotalPages();
	}

	/**
	 * 取得下页的页号, 序号从1开始 当前页为尾页时仍返回尾页序号
	 */
	public int getNextPage() {
		if (getHasNextPage()) {
			return pageNo + 1;
		} else {
			return pageNo;
		}
	}

	/**
	 * 取得上页的页号, 序号从1开始 当前页为首页时返回首页序号
	 */
	public int getPrePage() {
		if (getHasPrePage()) {
			return pageNo - 1;
		} else {
			return pageNo;
		}
	}

	/**
	 * 根据pageSize与totalCount计算总页数, 默认值为0
	 */
	public long getTotalPages() {
		if (totalCount < 0) {
			return -1;
		}
		long count = totalCount / pageSize;
		if (totalCount % pageSize > 0) {
			count++;
		}
		return count;
	}

	public List<Integer> getPageNos() {
		return getPageNos(getTotalPages(), this.pageNo);
	}

	public static List<Integer> getPageNos(long totalNo, int pageNo) {
		List<Integer> r = new ArrayList<Integer>();
		// long totalNo=getTotalPages();
		int startNo = 1;
		int endNo = (int) totalNo;
		if (pageNo < 5) {
			startNo = 1;
			endNo = (int) (totalNo > 5 ? 5 : totalNo);
			for (int i = startNo; i <= endNo; i++) {
				r.add(i);
			}
			if (totalNo > 5) {
				r.add(0);
			}
		} else if (pageNo >= totalNo - 3) {
			startNo = pageNo - 3;
			endNo = (int) totalNo;
			r.add(1);
			if (startNo > 2) {
				r.add(2);
				if (startNo != 3) {
					r.add(0);
				}

			}
			for (int i = startNo; i <= endNo; i++) {
				r.add(i);
			}
		} else {
			r.add(1);
			startNo = pageNo - 3;
			endNo = pageNo + 3;
			if (startNo > 2) {
				r.add(0);
			}
			for (int i = startNo; i <= endNo; i++) {
				r.add(i);
			}
			if (endNo < totalNo) {
				if (endNo < totalNo - 1) {
					r.add(0);
				}

				r.add((int) totalNo);
			}

		}
		return r;
	}

}
