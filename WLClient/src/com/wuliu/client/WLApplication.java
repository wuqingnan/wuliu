package com.wuliu.client;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;

import android.app.Application;
import android.content.Context;

public class WLApplication extends Application {
	
	private static LocationClient mLocClient;
	private static Context mContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		// ��ʹ�� SDK �����֮ǰ��ʼ�� context ��Ϣ������ ApplicationContext
		SDKInitializer.initialize(this);
		mContext = this;
	}
	
	public static void setLocationClient(LocationClient client) {
		mLocClient = client;
	}
	
	public static LocationClient getLocationClient() {
		return mLocClient;
	}
	
	public static Context getContext() {
		return mContext;
	}
	
}
