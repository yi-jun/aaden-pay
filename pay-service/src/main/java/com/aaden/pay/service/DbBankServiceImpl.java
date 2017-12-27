package com.aaden.pay.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaden.pay.api.DbBankService;
import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.orm.OrmUtil;
import com.aaden.pay.core.page.Page;
import com.aaden.pay.service.comm.service.ThirdBankSendService;

/**
 *  @Description 验证银行数据库实现
 *  @author aaden
 *  @date 2017年12月25日
 */
@Service("dbBankService")
public class DbBankServiceImpl implements DbBankService {

	protected SimpleLogger logger = SimpleLogger.getLogger(this.getClass());

	@Autowired
	private ThirdBankSendService bankSendService;

	@Override
	public List<ThirdBankSend> getList(ThirdBankSend obj) {
		return bankSendService.getList(obj);
	}

	@Override
	public Page<ThirdBankSend> getPage(ThirdBankSend bankSend, String pageNo, String pageSize) {
		Page<ThirdBankSend> page = new Page<>();
		OrmUtil.formatPageSize(pageNo, pageSize, page);
		return bankSendService.getPage(bankSend, page);
	}

	@Override
	public ThirdBankSend getBankSend(String id) {
		return bankSendService.findByKey(id);
	}

}
