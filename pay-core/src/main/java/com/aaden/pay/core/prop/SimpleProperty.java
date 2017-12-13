package com.aaden.pay.core.prop;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 *  @Description 配置文件工具类
 *  @author aaden
 *  @date 2017年12月26日
 */
public class SimpleProperty extends PropertyPlaceholderConfigurer {

	private static Map<String, String> propertyMap = new HashMap<String, String>();;

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
			throws BeansException {
		super.processProperties(beanFactoryToProcess, props);
		for (Object key : props.keySet()) {
			String keyStr = key.toString();
			String value = props.getProperty(keyStr);
			propertyMap.put(keyStr, value);
		}
	}

	public static String getProperty(String name) {
		return propertyMap.get(name);
	}
}
