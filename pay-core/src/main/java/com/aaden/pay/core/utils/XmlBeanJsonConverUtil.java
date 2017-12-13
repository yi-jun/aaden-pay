package com.aaden.pay.core.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;

/**
 *  @Description xml和josn转换工具
 *  @author aaden
 *  @date 2017年12月11日
 */
public class XmlBeanJsonConverUtil {

	private static String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";

	/**
	 * 把对象转成XML
	 *
	 * @param obj
	 *            要转换的对象
	 * @param rootAlias
	 *            要转换的对象的根节点别名,如果为空，默认是类的名字
	 * @return
	 */
	public static String beanToXML(Object obj, String rootAlias) {
		XStream xstream = new XStream(new XppDriver(new NoNameCoder()));
		xstream.processAnnotations(obj.getClass());
		return xmlHeader + xstream.toXML(obj);
	}

	/**
	 * 把对象转成XML
	 *
	 * @param <T>
	 * @param obj
	 *            要转换的对象
	 * @param clss
	 *            是转换对象的所有对象的的LIST列表如aa.class
	 * @return
	 */
	public static <T> String beanToXML(Object obj, List<Class<T>> clss) {
		XStream xstream = new XStream(new XppDriver(new NoNameCoder()));
		for (Class<T> cls : clss) {
			xstream.processAnnotations(cls);
		}
		return xmlHeader + xstream.toXML(obj);
	}

	/**
	 * 把XML转成对象
	 *
	 * @param xmlStr
	 * @return Object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T xmlStringToBean(String xmlStr, Class<T> cls) {
		XStream xstream = new XStream(new DomDriver());
		xstream.processAnnotations(cls);
		xstream.autodetectAnnotations(true);
		return (T) xstream.fromXML(xmlStr);
	}

	/**
	 * 把XML转成对象
	 *
	 * @param xmlStr
	 * @param clss
	 *            是转换对象的所有对象的list列表如aa.class
	 * @return Object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T xmlStringToBean(String xmlStr, List<Class<T>> clss) {
		XStream xstream = new XStream(new DomDriver());
		for (Class<T> cls : clss) {
			xstream.processAnnotations(cls);
		}
		xstream.autodetectAnnotations(true);
		return (T) xstream.fromXML(xmlStr);
	}

	/**
	 * 把XML转成对象
	 *
	 * @param <T>
	 * @param cls
	 * @return Object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T xmlStringToBean(Reader reader, Class<T> cls) {
		XStream xstream = new XStream(new DomDriver());
		xstream.processAnnotations(cls);
		xstream.autodetectAnnotations(true);
		return (T) xstream.fromXML(reader);
	}

	/**
	 * 把XML转成对象
	 *
	 * @param input
	 *            InputStream
	 * @return Object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T xmlStringToBean(InputStream input, Class<T> cls) {
		XStream xstream = new XStream(new DomDriver());
		xstream.processAnnotations(cls);
		xstream.autodetectAnnotations(true);
		return (T) xstream.fromXML(input);
	}

}
