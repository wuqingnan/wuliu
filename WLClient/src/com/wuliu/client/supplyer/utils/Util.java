package com.wuliu.client.supplyer.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

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
	 * 判断是否为正整数或0
	 * @param number
	 * @return
	 */
	public static boolean isInteger(String number) {
		if (number == null || number.length() == 0) {
			return false;
		}
		Pattern pattern = Pattern.compile("^(0|[1-9][0-9]*)$");
		// 正则匹配11位号码是否满足手机号码格式
		if (pattern.matcher(number).matches()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是否为正小数
	 * @param number
	 * @return
	 */
	public static boolean isDecimal(String number) {
		if (number == null || number.length() == 0) {
			return false;
		}
		Pattern pattern = Pattern.compile("^(0|[1-9][0-9]*)\\.[0-9]{1,}$");
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
			// 匹配英文数字下划线，长度6-16之间
			String rex = "[a-z0-9A-Z_]{6,16}";
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
	
	/**
	 * 显示提示
	 * 
	 * @param tips
	 */
	public static void showTips(Context context, String tips) {
		Toast.makeText(context, tips, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 发送短信
	 * @param context
	 * @param phone
	 * @param msg
	 */
	public static void sendMessage(Context context, String phone, String msg) {
		Intent intent = null;
		if (phone != null && Util.isPhoneNumber(phone)) {
			Uri uri = Uri.parse("smsto:" + phone);
			intent = new Intent(Intent.ACTION_SENDTO, uri);
		} else {
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setType("vnd.android-dir/mms-sms");
		}
		intent.putExtra("sms_body", msg);
		context.startActivity(intent);
	}
}
