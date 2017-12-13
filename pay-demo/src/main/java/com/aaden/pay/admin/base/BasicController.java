package com.aaden.pay.admin.base;

import com.aaden.pay.core.logger.SimpleLogger;

/**
 *  @Description 抽象做消息处理公共操作
 *  @author aaden
 *  @date 2017年12月6日
 */
public abstract class BasicController {

	protected String userId = "uuid_of_user";// 用户ID

	protected String userLoginName = "zhang_san";// 用户登录名

	// 子类可以不用写这个了,直接用就可以
	protected SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

}
