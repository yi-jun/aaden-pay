package com.aaden.pay.service.biz.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import com.aaden.pay.api.biz.config.Area;
import com.aaden.pay.api.biz.config.BankCardBin;
import com.aaden.pay.api.comm.enums.BankType;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.prop.SimpleProperty;
import com.aaden.pay.core.utils.FileUtils;
import com.aaden.pay.core.utils.XmlUtils;

/**
 *  @Description 内存解析,存储卡bin,行政区.xml
 *  @author aaden
 *  @date 2017年12月1日
 */
public class MemoryLabelParser {

	private static class InstanceHolder {
		public static MemoryLabelParser instance = new MemoryLabelParser();
	}

	public static MemoryLabelParser getInstance() {
		return InstanceHolder.instance; // 这里将导致InstanceHolder类被初始化
	}

	private static SimpleLogger logger = SimpleLogger.getLogger(MemoryLabelParser.class);
	// 卡bin缓存
	private Map<String, BankCardBin> BANK_CARD_BIN_MAP = null;

	// 行政区缓存,精度到市级
	private Map<String, Area> AREA_MAP = null;

	private String binPath = SimpleProperty.getProperty("bank_bin_path");
	private String areaPath = SimpleProperty.getProperty("area_config_path");

	private MemoryLabelParser() {
		if (logger.isDebugEnabled())
			logger.debug(" load bankcard bin from file " + binPath);
		this.initBankcardBinMap();

		if (logger.isDebugEnabled())
			logger.debug(" load area from file " + areaPath);
		this.initAreaMap();
	}

	/**
	 * 获取卡bin
	 */
	public BankCardBin getBankCardBin(String key) {
		return BANK_CARD_BIN_MAP == null ? null : BANK_CARD_BIN_MAP.get(key);
	}

	/**
	 * 获取行政区域
	 */
	public Area getArea(String key) {
		return AREA_MAP == null ? null : AREA_MAP.get(key);
	}

	/**
	 * 获取行政区域
	 */
	public List<Area> getAreaByParent(String parentCode) {
		List<Area> list = new ArrayList<>();
		if (AREA_MAP == null)
			return null;
		for (String key : AREA_MAP.keySet()) {
			Area area = AREA_MAP.get(key);
			if (area.getParentCode().equals(parentCode))
				list.add(area);
		}
		return list;
	}

	// xml属性
	private final String areaNode[] = { "area", "code", "name", "parentId" };

	private void initAreaMap() {
		try {
			String path = FileUtils.getClassOrSystemPath(areaPath);
			if (path == null) {
				throw new IllegalArgumentException(areaPath + " does not exist");
			}

			AREA_MAP = new HashMap<String, Area>();

			// 加载行政区
			Document document = XmlUtils.getDocument(path);

			Element element = null;
			Area area = null;
			for (@SuppressWarnings("unchecked")
			Iterator<Element> it = document.getRootElement().elementIterator(areaNode[0]); it.hasNext();) {
				element = it.next();
				area = new Area();
				area.setAreaCode(element.attributeValue(areaNode[1]));
				area.setAreaName(element.attributeValue(areaNode[2]));
				area.setParentCode(element.attributeValue(areaNode[3]));
				AREA_MAP.put(area.getAreaCode(), area);
			}
		} catch (Exception e) {
			logger.error(" 初始化行政区失败", e);
		}
	}

	// xml属性
	private final String binNode[] = { "cardstart", "bin", "code", "name", "type" };

	private void initBankcardBinMap() {
		try {

			String path = FileUtils.getClassOrSystemPath(binPath);
			if (path == null) {
				throw new IllegalArgumentException(binPath + " does not exist");
			}

			BANK_CARD_BIN_MAP = new HashMap<String, BankCardBin>();
			Document document = XmlUtils.getDocument(path);
			Element root = document.getRootElement();
			Element card = null;
			for (@SuppressWarnings("unchecked")
			Iterator<Element> it = root.elementIterator(binNode[0]); it.hasNext();) {
				// <cardstart bin="xx" code="xx" name="xx银行" type="贷记卡" />
				card = it.next();

				try {
					BankType.valueOf(card.attributeValue(binNode[2]));
				} catch (Exception e) {// 解析失败
					logger.warn("解析银行卡bin.xml出现系统不支持的银行卡bin");
					continue;
				}

				BankCardBin obj = new BankCardBin();
				String bin = card.attributeValue(binNode[1]);
				obj.setBankbin(bin);
				obj.setBankname(card.attributeValue(binNode[3]));
				obj.setCardType(card.attributeValue(binNode[4]));
				obj.setBankType(BankType.valueOf(card.attributeValue(binNode[2])));
				BANK_CARD_BIN_MAP.put(bin, obj);
			}
		} catch (Exception e) {
			logger.error(" 初始化银行卡bin失败:", e);
		}
	}

}
