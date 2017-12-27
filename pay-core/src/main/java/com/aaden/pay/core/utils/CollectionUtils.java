package com.aaden.pay.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Description 集合工具类
 * @author aaden
 * @date 2017年12月10日
 */
public class CollectionUtils {

	/**
	 * 判断集合是否为空
	 */
	public static boolean isEmpty(final Collection<?> coll) {
		return coll == null || coll.isEmpty();
	}

	/**
	 * 判断集合是否非空
	 */
	public static boolean isNotEmpty(final Collection<?> coll) {
		return !isEmpty(coll);
	}

	/**
	 * 判断集合是否为空
	 */
	public static boolean isEmpty(final Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	/**
	 * 判断集合是否非空
	 */
	public static boolean isNotEmpty(final Map<?, ?> map) {
		return !isEmpty(map);
	}

	/**
	 * 拆分集合
	 * 
	 * @param <T>
	 * @param resList
	 *            要拆分的集合
	 * @param splitSize
	 *            每个集合的元素个数
	 * @return 返回拆分后的各个集合
	 */
	public static <T> List<List<T>> split(List<T> resList, int splitSize) {
		if (resList == null || resList.isEmpty())
			return new ArrayList<>();

		if (splitSize < 1) {
			splitSize = 1;
		}

		List<List<T>> ret = new ArrayList<List<T>>();
		int size = resList.size();
		if (size <= splitSize) { // 数据量不足count指定的大小
			ret.add(resList);
			return ret;
		}

		// 拆分子集个数
		int subCount = size / splitSize;
		// 前面的集合，每个大小都是count个元素
		for (int i = 0; i < subCount; i++) {
			List<T> itemList = new ArrayList<T>();
			for (int j = 0; j < splitSize; j++) {
				itemList.add(resList.get(i * splitSize + j));
			}
			ret.add(itemList);
		}

		int last = size % splitSize;
		// last的进行处理
		if (last > 0) {
			List<T> itemList = new ArrayList<T>();
			for (int i = 0; i < last; i++) {
				itemList.add(resList.get(subCount * splitSize + i));
			}
			ret.add(itemList);
		}
		return ret;
	}

}
