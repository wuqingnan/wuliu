package com.wuliu.client.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.widget.Toast;

public class Util {

	public static boolean isPhoneNumber(String number) {
		if (number == null || number.length() != 11) {
			return false;
		}
		Pattern pattern = Pattern.compile("^1[3,5,7,8]\\d{9}$");
		// ����ƥ��11λ�����Ƿ������ֻ������ʽ
		if (pattern.matcher(number).matches()) {
			return true;
		}
		return false;
	}
	
	/**
	 * �ж��Ƿ�Ϊ��������0
	 * @param number
	 * @return
	 */
	public static boolean isInteger(String number) {
		if (number == null || number.length() == 0) {
			return false;
		}
		Pattern pattern = Pattern.compile("^(0|[1-9][0-9]*)$");
		// ����ƥ��11λ�����Ƿ������ֻ������ʽ
		if (pattern.matcher(number).matches()) {
			return true;
		}
		return false;
	}
	
	/**
	 * �ж��Ƿ�Ϊ��С��
	 * @param number
	 * @return
	 */
	public static boolean isDecimal(String number) {
		if (number == null || number.length() == 0) {
			return false;
		}
		Pattern pattern = Pattern.compile("^(0|[1-9][0-9]*)\\.[0-9]{1,}$");
		// ����ƥ��11λ�����Ƿ������ֻ������ʽ
		if (pattern.matcher(number).matches()) {
			return true;
		}
		return false;
	}
	
	/**
	 * ��֤����Ϸ���
	 * 
	 * @return true-valid false invalid
	 */
	public static boolean isPasswordValid(String data) {
		if (data != null && !data.equals("")) {
			// ƥ��Ӣ�������»��ߣ�����4-32֮��
			String rex = "[a-z0-9A-Z_]{4,32}";
			Pattern pattern = Pattern.compile(rex);
			Matcher m = pattern.matcher(data);
			return m.matches();
		}
		return false;
	}
	
	/**
	 * ��֤�û����Ϸ���
	 * 
	 * @return true-valid false invalid
	 */
	public static boolean isUserValid(String data) {
		if (data != null && !data.equals("")) {
			// ƥ����Ӣ���»��ߣ�����4-16֮��
			String rex = "[a-z0-9A-Z_\u4e00-\u9fa5]{4,16}";
			Pattern pattern = Pattern.compile(rex);
			Matcher m = pattern.matcher(data);
			return m.matches();
		}
		return false;
	}
	
	/**
	 * ��֤Ч����Ϸ���
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
	 * ��֤���֤����Ϸ���
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
	 * ��ʾ��ʾ
	 * 
	 * @param tips
	 */
	public static void showTips(Context context, String tips) {
		Toast.makeText(context, tips, Toast.LENGTH_SHORT).show();
	}
}
