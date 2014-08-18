package com.wuliu.client;

import android.content.Context;

public class Const {
	
	public static final String NULL = "-9";
	
	public static final String URL_LOGIN = "http://218.21.213.76:7201/bss/loginCheck2.action";
	public static final String URL_REGISTER = "http://218.21.213.76:7201/bss/registerGoodSupplyer.action";
	public static final String URL_SEND_GOODS = "http://218.21.213.76:7201/bss/sendGoodInfos.action";
	
	private static String sFilePath = null;
	private static String sExternalPath = null;
	
	public static void init(Context context) {
		sFilePath = context.getFilesDir().getAbsolutePath();
		sExternalPath = context.getExternalFilesDir(null).getAbsolutePath();
	}
	
	public static String getFilePath() {
		return sFilePath;
	}
	
	public static String getExternalPath() {
		return sExternalPath;
	}
}
