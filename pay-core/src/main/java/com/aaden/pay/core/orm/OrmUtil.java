package com.aaden.pay.core.orm;

import org.apache.commons.lang3.StringUtils;

import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.page.Page;

/**
 *  @Description 数据库底层工具类
 *  @author aaden
 *  @date 2017年12月3日
 */
public class OrmUtil {

	private static SimpleLogger logger = SimpleLogger.getLogger(OrmUtil.class);

	// 封装每页数据大小
	public static void formatPageSize(String pageNo, String limit, Page<?> page) {
		try {
			if (StringUtils.isNotBlank(pageNo)) {
				page.setPageNo(Integer.parseInt(pageNo));
			}
			if (StringUtils.isNotBlank(limit)) {
				page.setPageSize(Integer.parseInt(limit));
			}
		} catch (Exception e) {
			logger.error("page format excption for " + (page.getClass().getName()));
		}
	}
}
