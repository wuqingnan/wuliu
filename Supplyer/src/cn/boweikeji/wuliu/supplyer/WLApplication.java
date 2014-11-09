package cn.boweikeji.wuliu.supplyer;

import cn.boweikeji.wuliu.supplyer.db.DBHelper;
import cn.boweikeji.wuliu.supplyer.utils.DeviceInfo;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import android.app.Application;
import android.content.Context;

public class WLApplication extends Application {
	
	private static LocationClient mLocClient;
	private static WLApplication mInstance;
	
	private DBHelper mDBHelper;
	
	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
		mInstance = this;
		Const.init(this);
		DeviceInfo.init(this);
	}
	
	public DBHelper getHelper() {
		if (mDBHelper == null) {
			mDBHelper = OpenHelperManager.getHelper(this, DBHelper.class);
		}
		return mDBHelper;
	}
	
	public static void setLocationClient(LocationClient client) {
		mLocClient = client;
	}
	
	public static LocationClient getLocationClient() {
		return mLocClient;
	}
	
	public static WLApplication getApplication() {
		return mInstance;
	}

}
