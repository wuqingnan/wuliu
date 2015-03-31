package cn.boweikeji.wuliu.driver.service;

import java.lang.ref.WeakReference;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.aidl.ILocationListener;
import cn.boweikeji.wuliu.driver.aidl.IReportService;
import cn.boweikeji.wuliu.driver.aidl.WLLocation;
import cn.boweikeji.wuliu.driver.api.BaseParams;
import cn.boweikeji.wuliu.http.AsyncHttp;
import cn.boweikeji.wuliu.utils.DeviceInfo;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ReportService extends Service {

	private static final String LOG_TAG = ReportService.class.getSimpleName();

	private BDLocationListener mBDLocListener = new BDLocationListener() {
		@Override
		public void onReceivePoi(BDLocation poiLocation) {

		}

		@Override
		public void onReceiveLocation(BDLocation location) {
			Log.d(LOG_TAG, "onReceiveLocation");
			if (mLocationListener == null) {
				return;
			}
			ILocationListener listener = mLocationListener.get();
			if (listener != null) {
				try {
					listener.onReceiveLocation();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private final ReportBinder mBinder = new ReportBinder();

	private LocationClient mLocClient;
	private ScheduledThreadPoolExecutor mTimerTask;

	private WeakReference<ILocationListener> mLocationListener;

	private String mUserCd;
	private String mClientId;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG, "shizy---ReportService.onCreate");
		DeviceInfo.init(this);
		initLocation();
		mTimerTask = new ScheduledThreadPoolExecutor(1);
		mTimerTask.scheduleAtFixedRate(new PositionTask(), 5, 300,
				TimeUnit.SECONDS);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(LOG_TAG, "Received start id " + startId + ": " + intent);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "shizy---ReportService.onDestroy");
		mLocClient.unRegisterLocationListener(mBDLocListener);
		mLocClient.stop();
		mTimerTask.shutdown();
		mTimerTask.shutdownNow();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	private void initLocation() {
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(mBDLocListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(60 * 1000);
		option.setIsNeedAddress(true);
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		mLocClient.setLocOption(option);
		mLocClient.start();
		mLocClient.requestLocation();
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		if (manager != null) {
			NetworkInfo[] infos = manager.getAllNetworkInfo();
			if (infos != null) {
				for (int i = 0; i < infos.length; i++) {
					if (infos[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public class ReportBinder extends IReportService.Stub {

		@Override
		public void setLocationListener(ILocationListener listener)
				throws RemoteException {
			if (listener == null) {
				mLocationListener = null;
				return;
			}
			mLocationListener = new WeakReference<ILocationListener>(listener);
		}

		@Override
		public void setUserCd(String userCd) throws RemoteException {
			mUserCd = userCd;
		}

		@Override
		public void setClientId(String clientId) throws RemoteException {
			mClientId = clientId;
		}

		@Override
		public void reportLocation() throws RemoteException {
			mTimerTask.execute(new PositionTask());
		}

		@Override
		public WLLocation getLastKnownLocation() throws RemoteException {
			return new WLLocation(mLocClient.getLastKnownLocation());
		}
	}

	private class PositionTask implements Runnable {

		private JsonHttpResponseHandler mHandler = new JsonHttpResponseHandler() {
			public void onFinish() {
				Log.d(LOG_TAG, "shizy---PositionTask.onFinish()");
			};
		};

		private SyncHttpClient mHttpClient;

		public PositionTask() {
			mHttpClient = new SyncHttpClient();
			mHttpClient.setURLEncodingEnabled(true);
		}

		@Override
		public void run() {
			if (!isNetworkAvailable()) {
				return;
			}
			BDLocation location = mLocClient.getLastKnownLocation();
			if (location == null) {
				return;
			}
			if (location.getLatitude() == 0 || location.getLongitude() == 0) {
				return;
			}
			BaseParams params = new BaseParams();
			params.add("method", "collectDriverInfos");
			params.add("driver_cd", (mUserCd == null ? Const.NULL : mUserCd));
			params.add("gps_j", "" + location.getLongitude());
			params.add("gps_w", "" + location.getLatitude());
			params.add("speed", "" + location.getSpeed());
			params.add("phone_type", "" + DeviceInfo.getModel());
			params.add("operate_system", "" + DeviceInfo.getOSName());
			params.add("sys_edtion", "" + DeviceInfo.getOSVersion());
			params.add("app_version", "" + DeviceInfo.getVersionName());
			params.add("clientid", "" + (mClientId == null ? Const.NULL : mClientId));
			mHttpClient.get(
					AsyncHttp.getAbsoluteUrl(Const.URL_POSITION_UPLOAD),
					params, mHandler);
		}
	}
}
