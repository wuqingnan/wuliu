package com.wuliu.client;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class WLApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		// ��ʹ�� SDK �����֮ǰ��ʼ�� context ��Ϣ������ ApplicationContext
		SDKInitializer.initialize(this);
	}

}
