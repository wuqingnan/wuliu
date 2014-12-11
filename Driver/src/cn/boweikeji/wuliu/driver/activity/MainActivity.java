package cn.boweikeji.wuliu.driver.activity;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMyLocationClickListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MyLocationConfigeration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.WLApplication;
import cn.boweikeji.wuliu.driver.WeakHandler;
import cn.boweikeji.wuliu.driver.api.BaseParams;
import cn.boweikeji.wuliu.driver.bean.Order;
import cn.boweikeji.wuliu.driver.bean.UserInfo;
import cn.boweikeji.wuliu.driver.fragment.BaseFragment;
import cn.boweikeji.wuliu.driver.fragment.FindFragment;
import cn.boweikeji.wuliu.driver.fragment.HomeFragment;
import cn.boweikeji.wuliu.driver.fragment.ActivityFragment;
import cn.boweikeji.wuliu.driver.fragment.MoreFragment;
import cn.boweikeji.wuliu.driver.fragment.OrderFragment;
import cn.boweikeji.wuliu.driver.http.AsyncHttp;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import cn.boweikeji.wuliu.driver.utils.DeviceInfo;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private static final int MSG_PUSH = 1 << 0;

	private static final int EXIT_TIME = 2000;

	public static final int[] TAB_IDS = { R.id.home_tab_home,
			R.id.home_tab_activity, R.id.home_tab_find, R.id.home_tab_order,
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
			updateMap();
		}
	};

	private JsonHttpResponseHandler mRequestHandler = new JsonHttpResponseHandler() {
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			requestResult(response);
		};

		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			requestResult(null);
		};
	};

	private OnMarkerClickListener mOnMarkerClickListener = new OnMarkerClickListener() {
		@Override
		public boolean onMarkerClick(Marker marker) {
			Log.d(TAG, "shizy---onMarkerClick: " + marker.getTitle());
			showMarkerInfo(marker);
			return true;
		}
	};

	private OnMyLocationClickListener mLocClickListener = new OnMyLocationClickListener() {
		@Override
		public boolean onMyLocationClick() {
			Log.d(TAG, "shizy---onMyLocationClick");
			showMyLocInfo();
			return false;
		}

	};

	private OnMapClickListener mOnMapClickListener = new OnMapClickListener() {
		@Override
		public boolean onMapPoiClick(MapPoi poi) {
			return false;
		}

		@Override
		public void onMapClick(LatLng point) {
			mBaiduMap.hideInfoWindow();
		}
	};

	@InjectView(R.id.bottom_layout)
	LinearLayout mBottomLayout;
	@InjectView(R.id.home_tab_group)
	RadioGroup mTabGroup;
	@InjectView(R.id.bmapView)
	MapView mMapView;

	private BaiduMap mBaiduMap;
	private UiSettings mUiSettings;

	private View mMyLocLayout;
	private TextView mMyLocTitle;
	private TextView mMyLocInfo;
	private View mDriverLayout;
	private ImageView mDriverPortrait;
	private TextView mDriverName;
	private TextView mDriverInfo;

	private boolean mMapShowing;
	private boolean mIsFirstLoc;
	private boolean mHasDriver;

	private FragmentManager mFragmentManager;
	private BaseFragment mCurFragment;

	private long mExitTime;

	private int mTabIndex;

	private LocationClient mLocClient;

	private ScheduledThreadPoolExecutor mTimerTask;

	private MainHandler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		pageJump(getIntent());
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
		mMapShowing = true;
		updateMap();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
		mMapShowing = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocClient.unRegisterLocationListener(mBDLocListener);
		mLocClient.stop();
		mTimerTask.shutdown();
		mTimerTask.shutdownNow();
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mCurFragment != null) {
				if (mCurFragment.onKeyDown(keyCode, event)) {
					return true;
				}
			}
			if (mTabIndex != 0) {
				goHome();
				return true;
			}
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == LoginActivity.REQUEST_CODE_REDIRECT) {
				if (data != null) {
					pageJump(data);
				}
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		pageJump(intent);
	}

	private void init() {
		ButterKnife.inject(this);
		mFragmentManager = getSupportFragmentManager();
		mHandler = new MainHandler(this);
		mIsFirstLoc = true;
		mHasDriver = false;
		initView();
		initMap();
		changeFragment(0);
		initLocation();
		mTimerTask = new ScheduledThreadPoolExecutor(1);
		mTimerTask.scheduleAtFixedRate(new PositionTask(), 30, 300,
				TimeUnit.SECONDS);
	}

	private void pageJump(Intent intent) {
		Log.d(TAG, "shizy---pageJump");
		boolean push = intent.getBooleanExtra("push", false);
		int type = intent.getIntExtra("type", -1);
		if (push && type == Const.PUSH_TYPE_ROB) {
			// 目前只有抢单须路转页面
			mHandler.removeMessages(MSG_PUSH);
			Message msg = Message.obtain();
			msg.what = MSG_PUSH;
			msg.obj = intent;
			mHandler.sendMessage(msg);
		}
	}

	private void pushIntent(Intent intent) {
		int type = intent.getIntExtra("type", -1);
		if (type > 0) {
			switch (type) {
			case Const.PUSH_TYPE_DETAIL:
				showDetail(intent);
				break;
			case Const.PUSH_TYPE_MSG:
				showMessage(intent);
				break;
			case Const.PUSH_TYPE_PERSONAL:
				showPersonal(intent);
				break;
			case Const.PUSH_TYPE_ROB:
				showRob(intent);
				break;
			}
		}
	}

	private void initView() {
		mTabGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mMyLocLayout = getLayoutInflater().inflate(R.layout.popup_myloc, null);
		mMyLocTitle = (TextView) mMyLocLayout.findViewById(R.id.myloc_title);
		mMyLocInfo = (TextView) mMyLocLayout.findViewById(R.id.myloc_info);

		mDriverLayout = getLayoutInflater()
				.inflate(R.layout.popup_driver, null);
		mDriverPortrait = (ImageView) mDriverLayout
				.findViewById(R.id.driver_portrait);
		mDriverName = (TextView) mDriverLayout.findViewById(R.id.driver_name);
		mDriverInfo = (TextView) mDriverLayout.findViewById(R.id.driver_info);
	}

	private void initMap() {
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mBaiduMap.setOnMapClickListener(mOnMapClickListener);
		mBaiduMap.setOnMyLocationClickListener(mLocClickListener);
		mBaiduMap.setOnMarkerClickListener(mOnMarkerClickListener);

		mUiSettings = mBaiduMap.getUiSettings();
		mUiSettings.setCompassEnabled(false);
		mUiSettings.setZoomGesturesEnabled(true);
		mUiSettings.setScrollGesturesEnabled(true);
		mUiSettings.setRotateGesturesEnabled(false);
		mUiSettings.setOverlookingGesturesEnabled(false);

		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(
				LocationMode.NORMAL, true, null));
	}

	private void updateMap() {
		BDLocation location = WLApplication.getLocationClient()
				.getLastKnownLocation();
		if (location == null || mMapView == null) {
			return;
		}
		if (!mMapShowing) {
			return;
		}
		MyLocationData locData = new MyLocationData.Builder()
				.accuracy(location.getRadius())
				.direction(location.getDirection())
				.latitude(location.getLatitude())
				.longitude(location.getLongitude()).build();
		mBaiduMap.setMyLocationData(locData);
		if (mIsFirstLoc) {
			mIsFirstLoc = false;
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
			showMyLocInfo();
		}
		if (!mHasDriver) {
			requestDriver(location);
		}
	}

	private boolean changeFragment(int index) {
		if (index != 0 && !LoginManager.getInstance().hasLogin()) {
			LoginActivity.startLoginActivity(this);
			return false;
		}
		BaseFragment fragment = null;
		switch (index) {
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			fragment = new ActivityFragment();
			break;
		case 2:
			fragment = new FindFragment();
			break;
		case 3:
			fragment = new OrderFragment();
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
		mCurFragment = fragment;
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

	private void addMarker(JSONArray markers) {
		if (markers == null || markers.length() <= 0) {
			return;
		}

		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.marker1);

		JSONObject info = null;
		String[] truckTypes = getResources().getStringArray(
				R.array.truck_type_list);
		for (int i = 0; i < markers.length(); i++) {
			try {
				info = markers.getJSONObject(i);
				int truck = info.optInt("trunk_type_code");
				LatLng ll = new LatLng(info.optDouble("gps_w"),
						info.optDouble("gps_j"));
				Bundle bundle = new Bundle();
				bundle.putString("name", info.optString("driver_name"));
				bundle.putString("phone", info.optString("phone"));
				if (truck >= 0 && truck < truckTypes.length) {
					bundle.putString("truck", truckTypes[truck]);
				}
				OverlayOptions option = new MarkerOptions().position(ll)
						.icon(bitmap).extraInfo(bundle);
				mBaiduMap.addOverlay(option);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void showMyLocInfo() {
		BDLocation loc = WLApplication.getLocationClient()
				.getLastKnownLocation();
		if (loc == null || loc.getAddrStr() == null) {
			return;
		}
		mMyLocTitle.setText(String.format(getString(R.string.myloc),
				loc.getStreet()));
		mMyLocInfo.setText(loc.getCity() + " " + loc.getDistrict());
		MyLocationData data = mBaiduMap.getLocationData();
		LatLng ll = new LatLng(data.latitude, data.longitude);
		InfoWindow infoWindow = new InfoWindow(mMyLocLayout, ll,
				new OnInfoWindowClickListener() {
					public void onInfoWindowClick() {
						mBaiduMap.hideInfoWindow();
					}
				});
		mBaiduMap.showInfoWindow(infoWindow);
	}

	private void showMarkerInfo(final Marker marker) {
		mBaiduMap.hideInfoWindow();
		Bundle bundle = marker.getExtraInfo();
		if (bundle != null) {
			// mDriverName.setText(bundle.getString("name"));
			// mDriverInfo.setText(bundle.getString("info"));
			Fragment fragment = mFragmentManager
					.findFragmentByTag(HomeFragment.class.getName());
			if (fragment != null && fragment.isAdded()) {
				String name = bundle.getString("name");
				String phone = bundle.getString("phone");
				String truck = bundle.getString("truck");
				((HomeFragment) fragment).showPopupLayout(name, phone, truck);
			}
		}
		// LatLng ll = marker.getPosition();
		// InfoWindow infoWindow = new InfoWindow(mDriverLayout, ll,
		// new OnInfoWindowClickListener() {
		// public void onInfoWindowClick() {
		// mBaiduMap.hideInfoWindow();
		// }
		// });
		// mBaiduMap.showInfoWindow(infoWindow);
	}

	private void requestDriver(BDLocation location) {
		BaseParams params = new BaseParams();
		params.add("method", "getNearDriversByDrv");
		params.add("radius", "5000");
		params.add("gps_j", "" + location.getLongitude());
		params.add("gps_w", "" + location.getLatitude());
		AsyncHttp.get(Const.URL_NEAR_DRIVER, params, mRequestHandler);
	}

	private void requestResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response123: " + response.toString());
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				if (res == 2) {// 成功
					if (mMapShowing) {
						addMarker(response.optJSONArray("info"));
						mHasDriver = true;
					}
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void exit() {
		finish();
		System.exit(0);
	}

	public void goHome() {
		((RadioButton) mTabGroup.findViewById(R.id.home_tab_home))
				.setChecked(true);
	}

	private void showDetail(Intent intent) {

	}

	private void showMessage(Intent intent) {

	}

	private void showPersonal(Intent intent) {

	}

	private void showRob(Intent intent) {
		try {
			if (LoginManager.getInstance().hasLogin()) {
				String infos = intent.getStringExtra("infos");
				if (infos != null) {
					JSONObject obj = new JSONObject(infos);
					Order order = new Order();
					order.setGoods_cd(obj.optString("good_cd"));
					order.setGoods_name(obj.optString("goods_name"));
					order.setIs_order(obj.optInt("is_order"));
					order.setStart_addr(obj.optString("start_addr"));
					order.setEnd_addr(obj.optString("end_addr"));
					order.setDistance(obj.optDouble("distance"));
					RobOrderActivity.startRobOrderActivity(this, order);
				}
			} else {
				LoginActivity.startLoginActivity(this, intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private class PositionTask implements Runnable {

		private JsonHttpResponseHandler mHandler = new JsonHttpResponseHandler() {
			public void onFinish() {
				Log.d(TAG, "shizy---PositionTask.onFinish()");
			};
		};

		private SyncHttpClient mHttpClient;

		public PositionTask() {
			mHttpClient = new SyncHttpClient();
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
			params.add("clientid", "" + Const.clientid);
			mHttpClient.get(
					AsyncHttp.getAbsoluteUrl(Const.URL_POSITION_UPLOAD),
					params, mHandler);
		}
	}

	public static class MainHandler extends WeakHandler<MainActivity> {

		public MainHandler(MainActivity reference) {
			super(reference);
		}

		@Override
		public void handleMessage(MainActivity reference, Message msg) {
			switch (msg.what) {
			case MSG_PUSH:
				Intent intent = (Intent) msg.obj;
				reference.pushIntent(intent);
				break;
			}
		}

	}
}
