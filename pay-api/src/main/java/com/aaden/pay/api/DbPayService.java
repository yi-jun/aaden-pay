package com.aaden.pay.api;

import java.math.BigDecimal;
import java.util.List;

import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.model.ThirdPayQuota;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.api.comm.model.ThirdPayValidcode;
import com.aaden.pay.core.page.Page;

/**
 *  @Description 支付数据库操作接口
 *  @author aaden
 *  @date 2017年12月20日
 */
public interface DbPayService {

	/**
	 * 获取支付记录分页
	 */
	public Page<ThirdPayRecord> getPayRecordPage(ThirdPayRecord record, String pageNo, String pageSize);

	/**
	 * 根据系统订单号获取支付记录列表
	 */
	public List<ThirdPayRecord> getListByOrderNo(String orderNo);

	/**
	 * 流水号获取支付记录
	 */
	public ThirdPayRecord getBySerialnumber(String serialnumber);

	/**
	 * 根据条件获取所有记录,谨慎使用,勿传递空值
	 */
	public List<ThirdPayRecord> getPayRecordList(ThirdPayRecord query);

	/**
	 * 根据支付流水号,或者系统订单号,获取所有记录,谨慎使用,勿传递空值
	 */
	public List<ThirdPayRecord> getPayRecordList(List<String> orderNumOrSerialnum);

	/**
	 * 获取某个系统订单成功支付总金额
	 */
	public BigDecimal getSuccessAmount(String orderNo);

	/**
	 * 获取充值限额
	 */
	public ThirdPayQuota getPayQuota(PayChannel payChannel, BankType bankType);

	/**
	 * 获取充值限额,谨慎传递参数,勿传递空值
	 */
	public List<ThirdPayQuota> getPayQuotaList(ThirdPayQuota quota);

	/**
	 * 获取充值限额分页信息
	 */
	public Page<ThirdPayQuota> getPayQuotaPage(ThirdPayQuota quota, String pageNo, String pageSize);

	/**
	 * 充值验证码分页
	 */
	public Page<ThirdPayValidcode> getPayValidcodePage(ThirdPayValidcode validcode, String pageNo, String pageSize);

	/**
	 * 更新业务订单回调状态为成功
	 */
	public boolean updateCallbackYes(String orderNumOrSerialnum);

	/**
	 * 保存支付充值限额信息
	 */
	public boolean saveThirdPayQuota(String user, ThirdPayQuota quota);

	/**
	 * 更新支付充值限额信息
	 */
	public boolean updateThirdPayQuota(String user, ThirdPayQuota quota);

}
