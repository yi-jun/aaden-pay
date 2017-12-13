package com.aaden.pay.core.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 工具类
 * @author aaden
 * @date 2017年12月9日
 */
public final class BigDecimalUtils {

	/** 100 */
	public static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

	/** 2 */
	public static final BigDecimal TWO = new BigDecimal("2");;

	/**
	 * 格式化 BigDecimal为 xxx.xx格式字符串
	 * 
	 * @param amt
	 * @return
	 */
	public static String format(BigDecimal amt) {
		DecimalFormat df = new DecimalFormat("#0.00");
		return df.format(amt);
	}

	/**
	 * 判断BigDecimal值是否大于0
	 */
	public static Boolean isGreaterZero(BigDecimal value) {
		if (value == null) {
			value = new BigDecimal(BigInteger.ZERO);
		}
		return value.compareTo(BigDecimal.ZERO) > 0;
	}

	/**
	 * 将一个数total,按一个基数single,拆分成list
	 */
	public static List<BigDecimal> split(BigDecimal single, BigDecimal total) {
		List<BigDecimal> list = new ArrayList<>();
		// 拆分后的总个数
		int size = total.divide(single, 0, BigDecimal.ROUND_UP).intValue();

		for (int i = 0; i < size; i++) {
			if (i == (size - 1)) {
				list.add(total.subtract(new BigDecimal(size - 1).multiply(single)));
			} else {
				list.add(single);
			}
		}
		return list;
	}

}
