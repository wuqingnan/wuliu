package cn.boweikeji.wuliu.driver;

import com.baidu.location.LocationClient;

import android.app.Application;
import android.util.Log;

public class WLApplication extends Application {

	private static LocationClient mLocClient;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("", "WLApplication.onCreate: " + this);
	}
	
	public static void setLocationClient(LocationClient client) {
		mLocClient = client;
	}
	
	public static LocationClient getLocationClient() {
		return mLocClient;
	}
	
}
