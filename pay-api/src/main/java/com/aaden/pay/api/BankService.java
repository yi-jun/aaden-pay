package com.aaden.pay.api;

import java.util.List;

import com.aaden.pay.api.biz.config.Area;
import com.aaden.pay.api.biz.vo.BankRequest;
import com.aaden.pay.api.biz.vo.BankResponse;
import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.core.search.model.IndexModel;

/**
 * @Description 第三方绑卡签约业务接口
 * @author aaden
 * @date 2017年12月21日
 */
public interface BankService {

	/**
	 * 获取行政区域
	 */
	public List<Area> getAreaLabel(String areaCode);

	/**
	 * 验证银行卡
	 */
	public BankResponse verifyBank(BankRequest request);

	/**
	 * 查询支行信息
	 */
	public List<IndexModel> queryBrank(String cityCode, BankType bankType, String key);
}
