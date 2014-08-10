package com.wuliu.client.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

	public static boolean isPhoneNumber(String number) {
		if (number == null || number.length() != 11) {
			return false;
		}
		Pattern pattern = Pattern.compile("^1[3,5,7,8]\\d{9}$");
		// 正则匹配11位号码是否满足手机号码格式
		if (pattern.matcher(number).matches()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 验证密码合法性
	 * 
	 * @return true-valid false invalid
	 */
	public static boolean isPasswordValid(String data) {
		if (data != null && !data.equals("")) {
			// 匹配英文数字下划线，长度4-32之间
			String rex = "[a-z0-9A-Z_]{4,32}";
			Pattern pattern = Pattern.compile(rex);
			Matcher m = pattern.matcher(data);
			return m.matches();
		}
		return false;
	}
	
	/**
	 * 验证用户名合法性
	 * 
	 * @return true-valid false invalid
	 */
	public static boolean isUserValid(String data) {
		if (data != null && !data.equals("")) {
			// 匹配中英文下划线，长度4-16之间
			String rex = "[a-z0-9A-Z_\u4e00-\u9fa5]{4,16}";
			Pattern pattern = Pattern.compile(rex);
			Matcher m = pattern.matcher(data);
			return m.matches();
		}
		return false;
	}
	
	/**
	 * 验证效验码合法性
	 * 
	 * @return true-valid false invalid
	 */
	public static boolean isCodeValid(String code) {
		if (code != null && !code.equals("")) {
			String rex = "[0-9]{4}";
			Pattern pattern = Pattern.compile(rex);
			Matcher m = pattern.matcher(code);
			return m.matches();
		}
		return false;
	}
	
	/**
	 * 验证身份证号码合法性
	 * 
	 * @return true-valid false invalid
	 */
	public static boolean isIDNumberValid(String code) {
		if (code != null && !code.equals("")) {
			String rex = "([0-9]{17})([0-9Xx])";
			Pattern pattern = Pattern.compile(rex);
			Matcher m = pattern.matcher(code);
			return m.matches();
		}
		return false;
	}
}
