package com.aaden.pay.service.resouce;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.aaden.pay.api.biz.config.Area;
import com.aaden.pay.core.logger.SimpleLogger;
import com.aaden.pay.core.search.SearchService;
import com.aaden.pay.core.search.model.IndexModel;
import com.aaden.pay.core.utils.XmlUtils;
import com.aaden.pay.service.biz.util.BankUtils;
import com.aaden.pay.service.resouce.lianlian.LianlianBankType;
import com.aaden.pay.service.resouce.lianlian.LianlianClient;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 *  @Description 连连支行信息爬虫测试类
 *  @author aaden
 *  @date 2017年12月26日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context-test.xml")
public class LianlianBranchTest {

	SimpleLogger logger = SimpleLogger.getLogger(LianlianBranchTest.class);

	LianlianClient clent = new LianlianClient();
	List<IndexModel> branchList = new ArrayList<IndexModel>();

	@Autowired
	SearchService searchService;

	public static String SAVE_PATH = "C:\\Users\\Administrator\\Desktop\\bank_branch_list.xml";

	// 查询支行信息,保存xml在本地
	@Test
	public void testQueryBranch() throws Exception {
		List<String> list = this.getCityList();// 城市代码列表

		ArrayList<JSONArray> branchs = new ArrayList<JSONArray>();
		for (LianlianBankType bankType : LianlianBankType.values()) {
			for (String city : list) {
				JSONArray arr = queryFormLL(bankType, city);// 查询第三方获取支行
				if (arr != null)
					branchs.add(arr);
			}
		}
		// 写入XML
		this.writeXml(branchs);

	}

	// 根据本地xml文件,创建索引
	@Test
	public void testIndex() {
		// 读取xml文件
		this.getBankBranch();
		// 创建索引,索引路径参考配置文件lucene_index_dir
		if (!branchList.isEmpty()) {
			searchService.createIndex(branchList);
		}
	}
	// 查询支行信息
	@Test
	public void testQueryIndex() {
		searchService.queryBrank("110100", "ABC", "银行");
	}
	

	private void getBankBranch() {
		Document document = null;
		try {
			// 加载支行信息
			document = XmlUtils.getDocument(SAVE_PATH);
			if (logger.isDebugEnabled())
				logger.debug(" load bank branch from file bank_branch_list.xml");
			if (document != null) {
				this.getBankBranchLabel(document);
			}
		} catch (Exception e) {
			logger.error(this.getClass().getName() + " load label from file Exception", e);
		}
	}

	private final String bNode[] = { "branch", "branchId", "bankCode", "cityCode", "branchName" };

	private void getBankBranchLabel(Document document) {
		Element element = null;
		Element roote = document.getRootElement();
		for (@SuppressWarnings("unchecked")
		Iterator<Element> it = roote.elementIterator(bNode[0]); it.hasNext();) {
			element = it.next();
			IndexModel index = new IndexModel();
			index.setId(element.attributeValue(bNode[1]));
			index.setIndexStr(element.attributeValue(bNode[4]));
			index.setIndexBody(element.attributeValue(bNode[4]));
			index.setBankCode(element.attributeValue(bNode[2]));
			index.setCityCode(element.attributeValue(bNode[3]));
			branchList.add(index);
			
		}
	}

	private void writeXml(List<JSONArray> list) throws Exception {
		File file = new File(SAVE_PATH);
		if (file.exists())
			file.delete();

		// 生成一个文档
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("root");
		for (JSONArray jsonArray : list) {
			for (Object object : jsonArray) {
				JSONObject json = (JSONObject) object;
				System.out.println(json);
				Element element = root.addElement("branch");
				// 为cdr设置属性名和属性值
				element.addAttribute("branchId", json.getString("prcptcd").trim());// 支行行号
				element.addAttribute("bankCode", json.getString("bankCode").trim());// 银行类型
				element.addAttribute("cityCode", json.getString("cityCode").trim());// 城市代码
				element.addAttribute("branchName", json.getString("brabank_name").trim());// 支行名称
			}
		}
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(new File(SAVE_PATH)), "UTF-8"), format);
		// 写入新文件
		writer.write(document);
		writer.flush();
		writer.close();
	}

	private List<String> getCityList() {
		List<Area> citys = BankUtils.getAreaList("000000");
		List<String> list = new ArrayList<>();
		for (Area area : citys) {
			list.add(area.getAreaCode());
		}

		// 加上直辖市的省份代码
		list.addAll(Arrays.asList(new String[] { "110000", "500000", "310000", "120000" }));
		return list;
	}

	private JSONArray queryFormLL(LianlianBankType bankType, String city) {
		JSONArray array = doQuery(bankType, city);
		if (array != null) {
			for (Object object : array) {
				JSONObject json = (JSONObject) object;
				json.put("bankCode", bankType.getBankType().name());
				// 直辖市处理
				if ("110000".equals(city)) {
					json.put("cityCode", "110100");
				} else if ("120000".equals(city)) {
					json.put("cityCode", "120100");
				} else if ("310000".equals(city)) {
					json.put("cityCode", "310100");
				} else if ("500000".equals(city)) {
					json.put("cityCode", "500100");
				} else {
					json.put("cityCode", city);
				}
			}
		}
		return array;

	}

	private JSONArray doQuery(LianlianBankType bankType, String city) {
		try {
			return clent.queryBranch(bankType, city);
		} catch (Exception e) {
			logger.error("查询失败,再次查询.....");
			return clent.queryBranch(bankType, city);
		}
	}
}
