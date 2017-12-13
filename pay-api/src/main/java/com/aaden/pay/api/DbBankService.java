package com.aaden.pay.api;

import java.util.List;
import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.core.page.Page;

/**
 *  @Description 银行卡验证数据库操作接口
 *  @author aaden
 *  @date 2017年12月2日
 */
public interface DbBankService {

	/**
	 * 获取验卡记录
	 */
	public List<ThirdBankSend> getList(ThirdBankSend obj);

	/**
	 * 获取验卡记录分页
	 */
	public Page<ThirdBankSend> getPage(ThirdBankSend bankSend, String pageNo, String pageSize);

	/**
	 * 获取单个
	 */
	public ThirdBankSend getBankSend(String banksendId);
}
