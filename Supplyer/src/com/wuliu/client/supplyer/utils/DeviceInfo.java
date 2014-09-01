package com.wuliu.client.supplyer.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceInfo {
	
	private static final String TAG = DeviceInfo.class.getSimpleName();

	private static String sIMEI = null;
	private static String sVersion = null;
	private static String sPackageName = null;
	
	public static void init(Context context) {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		sIMEI = tm.getDeviceId();
		
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			sVersion = info.versionName;
			sPackageName = info.packageName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static String getAppVersion() {
		return sVersion;
	}

	public static String getIMEI() {
		return sIMEI;
	}
	
	public static String getOSVersion(){
		return Build.VERSION.RELEASE;
	}
	
	public static String getOSName() {
		return "Android";
	}
	
	/**
	 * 获取手机型号
	 * @return
	 */
	public static String getModel() {
		return Build.MODEL;
	}
	
	/**
	 * 获取制造商
	 * @return
	 */
	public static String getManufacturer() {
		return Build.MANUFACTURER;
	}
	
	/**
	 * 获取包名
	 * @return
	 */
	public static String getPackageName() {
		return sPackageName;
	}
}
