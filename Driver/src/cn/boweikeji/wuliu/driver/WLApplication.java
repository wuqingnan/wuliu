package cn.boweikeji.wuliu.driver;

import cn.boweikeji.wuliu.driver.db.DBHelper;

import com.baidu.location.LocationClient;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import android.app.Application;
import android.util.Log;

public class WLApplication extends Application {

	private static LocationClient mLocClient;
	
	private DBHelper mDBHelper;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("", "WLApplication.onCreate: " + this);
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
	
}
