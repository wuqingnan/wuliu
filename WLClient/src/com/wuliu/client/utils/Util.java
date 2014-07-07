package com.wuliu.client.utils;

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
	
}
