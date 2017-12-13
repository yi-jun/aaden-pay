package com.aaden.pay.service.comm.service;

import java.util.List;

import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.api.comm.enums.PayChannel;
import com.aaden.pay.api.comm.model.ThirdPayQuota;
import com.aaden.pay.core.orm.DbOperateService;

/**
 *  @Description 认证支付限额
 *  @author aaden
 *  @date 2017年12月21日
 */
public interface ThirdPayQuotaService extends DbOperateService<ThirdPayQuota> {

	public List<ThirdPayQuota> getList(ThirdPayQuota quota);

	public ThirdPayQuota getPayQuota(PayChannel payChannel, BankType bankType);

}
