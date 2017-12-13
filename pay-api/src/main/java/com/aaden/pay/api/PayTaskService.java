package com.aaden.pay.api;

/**
 *  @Description 支付任务接口
 *  @author aaden
 *  @date 2017年12月25日
 */
public interface PayTaskService {

	/**
	 * 每日对账,获取对账文件进行对账. 建议 8:00 - 9:00 间执行
	 */
	public void checkYestday();

	/**
	 * 频繁对账,查询状态未知的支付订单,发送第三方确认结果,建议 5 - 10分钟执行一次
	 */
	public void checkNotsureTask();
}
