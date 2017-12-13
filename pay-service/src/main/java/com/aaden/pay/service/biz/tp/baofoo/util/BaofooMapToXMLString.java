package com.aaden.pay.service.biz.tp.baofoo.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *  @Description 宝付工具类
 *  @author aaden
 *  @date 2017年12月27日
 */
public class BaofooMapToXMLString {

    /**
     * Converter Map<Object, Object> instance to xml string. Note: currently,
     * we aren't consider more about some collection types, such as array,list,
     *
     * @param dataMap  the data map
     *
     * @return the string
     */
    public static String converter(Map<Object, Object> dataMap,String Rootstr)
    {
        synchronized (BaofooMapToXMLString.class)
        {
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            strBuilder.append("<"+Rootstr+">");
            Set<Object> objSet = dataMap.keySet();
            for (Object key : objSet)
            {
                if (key == null)
                {
                    continue;
                }
                strBuilder.append("<").append(key.toString()).append(">");
                Object value = dataMap.get(key);
                strBuilder.append(coverter(value));
                strBuilder.append("</"+key.toString()+">");
            }
            strBuilder.append("</"+Rootstr+">");
            return strBuilder.toString();
        }
    }

    public static String coverter(Object[] objects) {
        StringBuilder strBuilder = new StringBuilder();
        for(Object obj:objects) {
            strBuilder.append("<item className=").append(obj.getClass().getName()).append(">\n");
            strBuilder.append(coverter(obj));
            strBuilder.append("</item>\n");
        }
        return strBuilder.toString();
    }

    public static String coverter(Collection<?> objects)
    {
        StringBuilder strBuilder = new StringBuilder();
        for(Object obj:objects) {
            strBuilder.append("<item className=").append(obj.getClass().getName()).append(">\n");
            strBuilder.append(coverter(obj));
            strBuilder.append("</item>\n");
        }
        return strBuilder.toString();
    }

    /**
     * Coverter.
     *
     * @param object the object
     * @return the string
     */
    @SuppressWarnings("unused")
	public static String coverter(Object object)
    {
        if (object instanceof Object[])
        {
            return coverter((Object[]) object);
        }
        if (object instanceof Collection)
        {
            return coverter((Collection<?>) object);
        }
        StringBuilder strBuilder = new StringBuilder();
        if (isObject(object))
        {
            Class<? extends Object> clz = object.getClass();
            Field[] fields = clz.getDeclaredFields();

            for (Field field : fields)
            {
                field.setAccessible(true);
                if (field == null){
                    continue;
                }
                String fieldName = field.getName();
                Object value = null;
                try
                {
                    value = field.get(object);
                }
                catch (IllegalArgumentException e)
                {
                    continue;
                }
                catch (IllegalAccessException e)
                {
                    continue;
                }
                strBuilder.append("<").append(fieldName)
                        .append(" className=\"").append(
                                value.getClass().getName()).append("\">");
                if (isObject(value)) {
                    strBuilder.append(coverter(value));
                }else if (value == null){
                    strBuilder.append("null");
                }else{
                    strBuilder.append(value.toString() + "");
                }
                strBuilder.append("</").append(fieldName).append(">");
            }
        }else if (object == null){
            strBuilder.append("null");
        }else{
            strBuilder.append(object.toString() + "");
        }
        return strBuilder.toString();
    }

    /**
     * Checks if is object.
     *
     * @param obj the obj
     *
     * @return true, if is object
     */
    private static boolean isObject(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (obj instanceof String)
        {
            return false;
        }
        if (obj instanceof Integer)
        {
            return false;
        }
        if (obj instanceof Double)
        {
            return false;
        }
        if (obj instanceof Float)
        {
            return false;
        }
        if (obj instanceof Byte)
        {
            return false;
        }
        if (obj instanceof Long)
        {
            return false;
        }
        if (obj instanceof Character)
        {
            return false;
        }
        if (obj instanceof Short)
        {
            return false;
        }
        if (obj instanceof Boolean)
        {
            return false;
        }
        return true;
    }

}
