package com.aaden.pay.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import com.aaden.pay.core.logger.SimpleLogger;

/**
 *  @Description 文件工具类
 *  @author aaden
 *  @date 2017年12月5日
 */
public class FileUtils {

	private static SimpleLogger logger = SimpleLogger.getLogger(FileUtils.class);

	/**
	 * 获取文件的绝对路径,class查找和系统路径查找
	 */
	public static String getClassOrSystemPath(String path) {
		ClassPathResource rs = new ClassPathResource(path);
		FileSystemResource fr = new FileSystemResource(path);
		if (rs.exists())
			return FileUtils.class.getClassLoader().getResource(path).getFile();

		if (fr.exists())
			return fr.getPath();

		return null;
	}

	/**
	 * 以行为单位读取文件，常用于读面向行的格式化文件
	 */
	public static List<String> readFileByLines(String fileName, String charSet) {
		BufferedReader reader = null;
		List<String> list = new ArrayList<String>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), charSet));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				list.add(tempString);
			}
			reader.close();
		} catch (IOException e) {
			logger.error(" readFileByLines Exception", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					logger.error(" readFileByLines IOException", e1);
				}
			}
		}
		return list;
	}

	/**
	 * 写文件
	 */
	public static boolean wirte(String filePath, String fileContent, boolean append) {
		File n = new File(filePath);
		FileWriter fw = null;
		try {
			fw = new FileWriter(n, append);
			fw.write(fileContent);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (fw != null)
					fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
