package cn.boweikeji.wuliu.driver.activity;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.loopj.android.http.AsyncHttpClient;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.WLApplication;
import cn.boweikeji.wuliu.driver.api.BaseParams;
import cn.boweikeji.wuliu.driver.bean.UserInfo;
import cn.boweikeji.wuliu.driver.fragment.FindFragment;
import cn.boweikeji.wuliu.driver.fragment.HomeFragment;
import cn.boweikeji.wuliu.driver.fragment.MessageFragment;
import cn.boweikeji.wuliu.driver.fragment.MoreFragment;
import cn.boweikeji.wuliu.driver.fragment.OrderFragment;
import cn.boweikeji.wuliu.driver.listener.ILocationListener;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import cn.boweikeji.wuliu.driver.utils.DeviceInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private static final int EXIT_TIME = 2000;

	public static final int[] TAB_IDS = { R.id.home_tab_home,
			R.id.home_tab_msg, R.id.home_tab_order, R.id.home_tab_find,
			R.id.home_tab_more };

	private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			for (int i = 0; i < TAB_IDS.length; i++) {
				if (TAB_IDS[i] == checkedId) {
					if (!changeFragment(i)) {
						mTabGroup.check(TAB_IDS[mTabIndex]);
					}
					break;
				}
			}
		}
	};
	
	private BDLocationListener mBDLocListener = new BDLocationListener() {

		@Override
		public void onReceivePoi(BDLocation poiLocation) {

		}

		@Override
		public void onReceiveLocation(BDLocation location) {
//			Log.d(TAG, "shizy---onReceiveLocation");
			if (mLocationListener != null) {
				mLocationListener.onLocation(location);
			}
		}
	};

	@InjectView(R.id.bottom_layout)
	LinearLayout mBottomLayout;
	@InjectView(R.id.home_tab_group)
	RadioGroup mTabGroup;

	private FragmentManager mFragmentManager;

	private long mExitTime;
	
	private int mTabIndex;

	private LocationClient mLocClient;
	private ILocationListener mLocationListener;
	
	private ScheduledThreadPoolExecutor mTimerTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocClient.unRegisterLocationListener(mBDLocListener);
		mLocClient.stop();
		mTimerTask.shutdown();
		mTimerTask.shutdownNow();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mFragmentManager.getBackStackEntryCount() == 0) {
				if (System.currentTimeMillis() - mExitTime > EXIT_TIME) {
					Toast.makeText(this, R.string.quit_next_time,
							Toast.LENGTH_SHORT).show();
					mExitTime = System.currentTimeMillis();
				} else {
					exit();
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void init() {
		mFragmentManager = getSupportFragmentManager();
		initView();
		changeFragment(0);
		initLocation();
		LoginManager.getInstance().autoLogin();
		mTimerTask = new ScheduledThreadPoolExecutor(1);
		mTimerTask.scheduleAtFixedRate(new PositionTask(), 30, 300, TimeUnit.SECONDS);
	}

	private void initView() {
		ButterKnife.inject(this);
		mTabGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
	}

	private boolean changeFragment(int index) {
		if (index != 0 && !LoginManager.getInstance().hasLogin()) {
			LoginActivity.startLoginActivity(this);
			return false;
		}
		Fragment fragment = null;
		switch (index) {
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			fragment = new MessageFragment();
			break;
		case 2:
			fragment = new OrderFragment();
			break;
		case 3:
			fragment = new FindFragment();
			break;
		case 4:
			fragment = new MoreFragment();
			break;
		}
		mFragmentManager
				.beginTransaction()
				.replace(R.id.container, fragment,
						fragment.getClass().getName()).commit();
		mTabIndex = index;
		return true;
	}
	
	private void initLocation() {
		mLocClient = new LocationClient(getApplicationContext());
		WLApplication.setLocationClient(mLocClient);
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

	private void exit() {
		finish();
		System.exit(0);
	}

	public void setLocationListener(ILocationListener listener) {
		mLocationListener = listener;
	}
	
	private class PositionTask implements Runnable {
		
		AsyncHttpClient mHttpClient;
		
		public PositionTask() {
			mHttpClient = new AsyncHttpClient();
			mHttpClient.setURLEncodingEnabled(true);
		}
		
		@Override
		public void run() {
			UserInfo info = LoginManager.getInstance().getUserInfo();
			if (info == null) {
				return;
			}
			
			LocationClient client = WLApplication.getLocationClient();
			if (client == null) { 
				return;
			}
			
			BDLocation location = client.getLastKnownLocation();
			
			BaseParams params = new BaseParams();
			params.add("method", "collectDriverInfos");
			params.add("driver_cd", info.getDriver_cd());
			params.add("gps_j", "" + location.getLongitude());
			params.add("gps_w", "" + location.getLatitude());
			params.add("speed", "" + location.getSpeed());
			params.add("phone_type", "" + DeviceInfo.getModel());
			params.add("operate_system", "" + DeviceInfo.getOSName());
			params.add("sys_edtion", "" + DeviceInfo.getOSVersion());
			params.add("app_version", "" + DeviceInfo.getAppVersion());
			
			Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(true, Const.URL_POSITION_UPLOAD, params));
			mHttpClient.get(Const.URL_POSITION_UPLOAD, params, null);
		}
	}
}
