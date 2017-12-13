package com.aaden.pay.core.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 *  @Description xml配置文件解析
 *  @author aaden
 *  @date 2017年12月29日
 */
public class XmlUtils {

	public static Document getDocument(String path) throws DocumentException {
		Document document = null;
		SAXReader reader = new SAXReader();
		File file = new File(path);
		if (file.exists()) {
			document = reader.read(file);
		}
		return document;
	}

	public static Document parseStringDoc(String xml) throws DocumentException {
		return DocumentHelper.parseText(xml);
	}

	public static String languages(String language) {
		String[] l = language.split("_");
		if (l.length == 2) {
			return l[1].toUpperCase();
		} else {
			return "CN";
		}
	}

	/**
	 * 根据文件路夹路径返回符合规则的文件名集合
	 * 
	 * @param path
	 *            文件夹路径
	 * @param regex
	 *            验证的正则string
	 * @return String[] 文件名数组
	 */
	public static String[] fileList(String path, final String regex) {
		File filepath = new File(path);
		String[] list = filepath.list(new FilenameFilter() {
			private Pattern pattern = Pattern.compile(regex);

			public boolean accept(File dir, String filename) {
				return pattern.matcher(new File(filename).getName()).matches();
			}
		});
		return list;
	}

	/**
	 * 将XML转为对象
	 * 
	 * @param xml
	 *            xml字符串
	 * @param obj
	 *            需转换对象
	 * @return 转换对象
	 */
	public static Object simpleXmlToObject(String xml, Object obj) {
		XStream xStream = new XStream(new DomDriver());
		toListGenericsAlias(xStream, obj);
		xStream.alias(obj.getClass().getSimpleName(), obj.getClass());
		Object reobj = xStream.fromXML(xml);
		return reobj;
	}

	/**
	 * 将XML转为对象
	 * 
	 * @param stream
	 *            input输入流
	 * @param obj
	 *            需转换的对象
	 * @return 转换对象
	 */
	public static Object simpleXmlToObject(InputStream stream, Object obj) {
		XStream xStream = new XStream(new DomDriver());
		toListGenericsAlias(xStream, obj);
		xStream.alias(obj.getClass().getSimpleName(), obj.getClass());
		Object reobj = xStream.fromXML(stream);
		return reobj;
	}

	/**
	 * 将对象转为XML字符串
	 * 
	 * @param obj
	 *            需转换对象
	 * @return xml字符串
	 */
	public static String simpleObjectToXml(Object obj) {
		XStream xStream = new XStream();
		toListGenericsAlias(xStream, obj);
		xStream.alias(obj.getClass().getSimpleName(), obj.getClass());
		String xml = xStream.toXML(obj).replace("__", "_");
		return xml != null && !"".equals(xml) ? xml : null;
	}

	/**
	 * 将XML转成list对象
	 * 
	 * @param document
	 * @param t
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> xml2list(Document document, Class<T> t) throws Exception {
		List<T> list = new ArrayList<T>();
		StringBuffer sb = new StringBuffer();
		try {
			for (Iterator<T> iterator = document.getRootElement().elementIterator(); iterator.hasNext();) {
				Element iter = (Element) iterator.next();
				T entity = (T) Class.forName(t.getName()).newInstance();
				for (Iterator<Element> supiterator = iter.elementIterator(); supiterator.hasNext();) {
					Element supiter = supiterator.next();
					sb.append(supiter.getName());
					sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
					Method m = entity.getClass().getMethod("set" + sb.toString(),
							entity.getClass().getDeclaredField(supiter.getName()).getType());
					m.invoke(entity, supiter.getText());
					sb.setLength(0);
				}
				list.add(entity);
			}
		} catch (Exception e) {
			throw e;
		}
		return list;
	}

	/**
	 * 给List中的泛型取别名
	 * 
	 * @param xStream
	 * @param obj
	 */
	private static void toListGenericsAlias(XStream xStream, Object obj) {
		/** 得到所有的fields **/
		Field[] fs = obj.getClass().getDeclaredFields();
		for (Field f : fs) {
			/** 得到field的class及类型全路径 **/
			Class<?> fieldClazz = f.getType();
			/** 如果是List类型，得到其类的类型 **/
			if (fieldClazz.isAssignableFrom(List.class)) {
				/** 获取类的类型 **/
				Type fc = f.getGenericType();
				if (fc == null)
					continue;
				/** 是否是参数化泛型类型 **/
				if (fc instanceof ParameterizedType) {
					/** 得到泛型里的class类型对象 **/
					ParameterizedType pt = (ParameterizedType) fc;
					Class<?> genericClazz = (Class<?>) pt.getActualTypeArguments()[0];
					xStream.alias(genericClazz.getSimpleName(), genericClazz);
				}
			}
		}
	}

}
