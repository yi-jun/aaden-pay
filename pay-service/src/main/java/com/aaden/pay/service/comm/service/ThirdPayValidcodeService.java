package com.aaden.pay.service.comm.service;


import com.aaden.pay.api.comm.model.ThirdPayValidcode;
import com.aaden.pay.core.orm.DbOperateService;

/**
 *  @Description 充值验证码记录
 *  @author aaden
 *  @date 2017年12月1日
 */
public interface ThirdPayValidcodeService extends DbOperateService<ThirdPayValidcode> {

	public ThirdPayValidcode getLast(String userId);


}
