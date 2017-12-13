package com.aaden.pay.service.biz.util;

import org.springframework.beans.BeanUtils;

import com.aaden.pay.api.comm.model.ThirdBankSend;
import com.aaden.pay.api.comm.model.ThirdPayRecord;
import com.aaden.pay.api.comm.model.ThirdPayValidcode;

/**
 *  @Description 克隆工具类
 *  @author aaden
 *  @date 2017年12月28日
 */
public class CloneUtils {

	/** 克隆,防止后续修改该对象,造成不必要的影响 **/
	public static ThirdPayRecord clone(ThirdPayRecord db) {
		ThirdPayRecord target = new ThirdPayRecord();
		BeanUtils.copyProperties(db, target);
		return target;
	}

	/** 克隆,防止后续修改该对象,造成不必要的影响 **/
	public static ThirdPayValidcode clone(ThirdPayValidcode db) {
		ThirdPayValidcode target = new ThirdPayValidcode();
		BeanUtils.copyProperties(db, target);
		return target;
	}

	/** 克隆对象,防止后续修改该对象,造成不必要的影响 **/
	public static ThirdBankSend clone(ThirdBankSend db) {
		ThirdBankSend target = new ThirdBankSend();
		BeanUtils.copyProperties(db, target);
		return target;
	}
}
