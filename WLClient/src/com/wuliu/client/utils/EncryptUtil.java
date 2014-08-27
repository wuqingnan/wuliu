package com.wuliu.client.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtil {
	
	public static final String TAG = EncryptUtil.class.getSimpleName();

	public static final String MD5 = "MD5";

	public static final String SHA1 = "SHA-1";

	public static String encrypt(String strSrc, String encName) {
		MessageDigest md = null;
		String strDes = null;
		byte[] bt = strSrc.getBytes();
		try {
			if (encName == null || encName.equals("")) {
				encName = MD5;
			}
			md = MessageDigest.getInstance(encName);
			md.update(bt);
			strDes = bytes2Hex(md.digest()).toUpperCase(); // to HexString
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		return strDes;
	}

	public static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		int btsLength = bts.length;
		for (int i = 0; i < btsLength; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}

	public static String toHexString(byte[] bytes, String separator,
			boolean upperCase) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			String str = Integer.toHexString(0xFF & b); // SUPPRESS CHECKSTYLE
			if (upperCase) {
				str = str.toUpperCase();
			}
			if (str.length() == 1) {
				hexString.append("0");
			}
			hexString.append(str).append(separator);
		}
		return hexString.toString();
	}
}
