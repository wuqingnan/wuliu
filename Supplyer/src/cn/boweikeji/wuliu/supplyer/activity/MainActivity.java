package cn.boweikeji.wuliu.supplyer.activity;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

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
import com.umeng.analytics.MobclickAgent;

import cn.boweikeji.wuliu.http.AsyncHttp;
import cn.boweikeji.wuliu.supplyer.api.BaseParams;
import cn.boweikeji.wuliu.supplyer.bean.UpdateInfo;
import cn.boweikeji.wuliu.supplyer.bean.UserInfo;
import cn.boweikeji.wuliu.supplyer.event.ExitEvent;
import cn.boweikeji.wuliu.supplyer.manager.LoginManager;
import cn.boweikeji.wuliu.supplyer.manager.UpdateManager;
import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.R;
import cn.boweikeji.wuliu.supplyer.WLApplication;
import cn.boweikeji.wuliu.utils.DeviceInfo;
import cn.boweikeji.wuliu.utils.Util;
import cn.boweikeji.wuliu.view.MenuView;
import de.greenrobot.event.EventBus;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

public class MainActivity extends BaseActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private static final int EXIT_TIME = 2000;

	private final int DELAY_MILLIS = 1000 * 10;

	public static final String KEY_REDIRECT = "redirect";
	public static final String KEY_REDIRECT_TO = "redirect_to";
	public static final int REDIRECT_TO_ORDERDETAIL = 1 << 0;

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				showMenu();
			} else if (view == mMainSend) {
				if (LoginManager.getInstance().hasLogin()) {
					SendActivity.startSendActivity(MainActivity.this, false);
				} else {
					LoginActivity.startLoginActivity(MainActivity.this);
				}
			} else if (view == mMainBook) {
				if (LoginManager.getInstance().hasLogin()) {
					SendActivity.startSendActivity(MainActivity.this, true);
				} else {
					LoginActivity.startLoginActivity(MainActivity.this);
				}
			}
		}
	};

	private MenuView.OnMenuClickListener mOnMenuClickListener = new MenuView.OnMenuClickListener() {
		@Override
		public void onMenuClick(int menu) {
			mMenuIndex = menu;
			mDrawerLayout.closeDrawer(mMenuView);
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

	private DrawerListener mDrawerListener = new DrawerListener() {
		@Override
		public void onDrawerStateChanged(int newState) {

		}

		@Override
		public void onDrawerSlide(View drawerView, float slideOffset) {

		}

		@Override
		public void onDrawerOpened(View drawerView) {
			if (drawerView == mMenuView) {
				mMenuIndex = Integer.MAX_VALUE;
			}
		}

		@Override
		public void onDrawerClosed(View drawerView) {
			if (drawerView == mMenuView && mMenuIndex != Integer.MAX_VALUE) {
				switch (mMenuIndex) {
				case MenuView.MENU_PROFILE:
					ProfileActivity.startProfileActivity(MainActivity.this);
					break;
				case MenuView.MENU_ORDER:
					OrderActivity.startOrderActivity(MainActivity.this);
					break;
				case MenuView.MENU_ACTIVITY:
					WebViewActivity.startWebViewActivity(MainActivity.this,
							getString(R.string.title_activity),
							Const.getMsgUrl());
					break;
				case MenuView.MENU_GUIDE:
					WebViewActivity
							.startWebViewActivity(
									MainActivity.this,
									getResources().getString(
											R.string.title_send_guide),
									Const.URL_GUIDE);
					break;
				case MenuView.MENU_INVITE:
					Util.sendMessage(MainActivity.this, null, getResources()
							.getString(R.string.invite_msg));
					break;
				case MenuView.MENU_SHARE:
					Util.showShare(MainActivity.this);
					break;
				case MenuView.MENU_SUGGEST:
					startActivity(new Intent(MainActivity.this,
							SuggestActivity.class));
					break;
				case MenuView.MENU_SETTING:
					SetActivity.startSetActivity(MainActivity.this);
					break;
				}
			}
		}
	};

	private Runnable mMyPosRunnable = new Runnable() {
		@Override
		public void run() {
			updateMyInfo();
		}
	};

	@InjectView(R.id.drawer_layout)
	DrawerLayout mDrawerLayout;
	@InjectView(R.id.menuview)
	MenuView mMenuView;
	@InjectView(R.id.bmapView)
	MapView mMapView;
	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.main_send)
	LinearLayout mMainSend;
	@InjectView(R.id.main_book)
	LinearLayout mMainBook;
	@InjectView(R.id.my_pos_info)
	TextView mMyPosInfo;

	private BaiduMap mBaiduMap;
	private UiSettings mUiSettings;

	private View mDriverLayout;
	private TextView mDriverName;
	private TextView mDriverPhone;
	private TextView mDriverTruckNo;
	private TextView mDriverTruckType;

	private LocationClient mLocClient;

	private ScheduledThreadPoolExecutor mTimerTask;

	private long mExitTime;
	private int mMenuIndex;

	private boolean mMapShowing;
	private boolean mIsFirstLoc;
	private boolean mHasCheckUpdate;

	private Handler mHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
		mMapShowing = true;
		updateMap();
		checkUpdate();
	}

	@Override
	public void onPause() {
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
		mHandler.removeCallbacks(mMyPosRunnable);
		mHandler = null;
		EventBus.getDefault().unregister(this);
		EventBus.getDefault().unregister(mMenuView);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mDrawerLayout.isDrawerOpen(mMenuView)) {
				mDrawerLayout.closeDrawer(mMenuView);
				return true;
			}
			if (System.currentTimeMillis() - mExitTime > EXIT_TIME) {
				Toast.makeText(this, R.string.quit_next_time,
						Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
				return true;
			} else {
				exit();
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (LoginManager.getInstance().hasLogin()) {
				if (mDrawerLayout.isDrawerOpen(mMenuView)) {
					mDrawerLayout.closeDrawer(mMenuView);
				} else {
					mDrawerLayout.openDrawer(mMenuView);
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getBooleanExtra(KEY_REDIRECT, false)) {
			int to = intent.getIntExtra(KEY_REDIRECT_TO, 0);
			switch (to) {
			case REDIRECT_TO_ORDERDETAIL:
				OrderDetailActivity.startOrderDetailActivity(this,
						intent.getStringExtra("goods_cd"));
				break;
			}
		}
	}

	private void init() {
		ButterKnife.inject(this);
		mHandler = new Handler();
		mIsFirstLoc = true;
		mHasCheckUpdate = false;
		initView();
		initMenu();
		initMap();
		initLocation();
		EventBus.getDefault().register(this);
		mTimerTask = new ScheduledThreadPoolExecutor(1);
		mTimerTask.scheduleAtFixedRate(new PositionTask(), 30, 300,
				TimeUnit.SECONDS);
		mHandler.postDelayed(mMyPosRunnable, 1000);
	}

	private void initMenu() {
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerLayout.setDrawerTitle(GravityCompat.START, "MenuView");
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
				mMenuView);
		mDrawerLayout.setDrawerListener(mDrawerListener);
		mMenuView.setOnMenuClickListener(mOnMenuClickListener);
		EventBus.getDefault().register(mMenuView);
	}

	private void initView() {
		initTitle();
		initBottom();
		initPopup();
	}

	private void initTitle() {
		mTitle.setText(R.string.app_name);
		mMenuBtn.setImageResource(R.drawable.ic_navi_menu);
		mMenuBtn.setOnClickListener(mOnClickListener);
	}

	private void initBottom() {
		mMainSend.setOnClickListener(mOnClickListener);
		mMainBook.setOnClickListener(mOnClickListener);
	}

	private void initPopup() {
		mDriverLayout = getLayoutInflater()
				.inflate(R.layout.popup_driver, null);
		mDriverName = (TextView) mDriverLayout.findViewById(R.id.driver_name);
		mDriverPhone = (TextView) mDriverLayout.findViewById(R.id.driver_phone);
		mDriverTruckNo = (TextView) mDriverLayout
				.findViewById(R.id.driver_truck_no);
		mDriverTruckType = (TextView) mDriverLayout
				.findViewById(R.id.driver_truck_type);
	}

	private void initMap() {
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mBaiduMap.setOnMapClickListener(mOnMapClickListener);
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

		// 隐藏放大缩小
		for (int i = 0; i < mMapView.getChildCount(); i++) {
			if (mMapView.getChildAt(i) instanceof ZoomControls) {
				mMapView.getChildAt(i).setVisibility(View.GONE);
				break;
			}
		}
	}

	public void updateMyInfo() {
		BDLocation loc = WLApplication.getLocationClient()
				.getLastKnownLocation();
		if (loc != null && loc.getAddrStr() != null) {
			mMyPosInfo.setText(loc.getAddrStr());
			mMyPosInfo.setVisibility(View.VISIBLE);
		}
		mHandler.postDelayed(mMyPosRunnable, DELAY_MILLIS);
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
		}
		mBaiduMap.clear();
		requestDriver(location);
	}

	private void checkUpdate() {
		if (mHasCheckUpdate) {
			return;
		}
		mHasCheckUpdate = true;
		UpdateInfo updateInfo = UpdateManager.readUpdateInfo();
		if (updateInfo != null && updateInfo.isNeedUpdate()) {
			showUpdateDialog(updateInfo);
		}
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

	public void showMenu() {
		if (LoginManager.getInstance().hasLogin()) {
			mDrawerLayout.openDrawer(mMenuView);
		} else {
			LoginActivity.startLoginActivity(this);
		}
	}

	private void exit() {
		MobclickAgent.onKillProcess(this);
		finish();
		System.exit(0);
	}

	private void addMarker(JSONArray markers) {
		if (markers == null || markers.length() <= 0) {
			return;
		}

		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.ic_marker);

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
				bundle.putString("truck_no", info.optString("trunk_no"));
				if (truck >= 0 && truck < truckTypes.length) {
					bundle.putString("truck_type", truckTypes[truck]);
				}
				OverlayOptions option = new MarkerOptions().position(ll)
						.icon(bitmap).extraInfo(bundle);
				mBaiduMap.addOverlay(option);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void showMarkerInfo(final Marker marker) {
		Bundle bundle = marker.getExtraInfo();
		if (bundle != null) {
			mDriverName.setText(bundle.getString("name"));
			mDriverPhone
					.setText(String.format(getString(R.string.format_phone),
							bundle.getString("phone")));
			mDriverTruckNo.setText(String.format(
					getString(R.string.format_truck_no),
					bundle.getString("truck_no")));
			mDriverTruckType.setText(String.format(
					getString(R.string.format_truck_type),
					bundle.getString("truck_type")));
		}
		LatLng ll = marker.getPosition();
		InfoWindow infoWindow = new InfoWindow(mDriverLayout, ll,
				new OnInfoWindowClickListener() {
					public void onInfoWindowClick() {
						mBaiduMap.hideInfoWindow();
					}
				});
		mBaiduMap.showInfoWindow(infoWindow);
	}

	private void requestDriver(BDLocation location) {
		BaseParams params = new BaseParams();
		params.add("method", "getNearDrivers");
		params.add("radius", "5000");
		params.add("gps_j", "" + location.getLongitude());
		params.add("gps_w", "" + location.getLatitude());
		AsyncHttp.get(Const.URL_NEAR_DRIVER, params, mRequestHandler);
	}

	private void requestResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				if (res == 2) {// 成功
					if (mMapShowing) {
						addMarker(response.optJSONArray("info"));
					}
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void showUpdateDialog(final UpdateInfo info) {
		UpdateManager.showUpdateDialog(this, info,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(info.getUrl()));
						startActivity(intent);
					}
				}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (info.isForce()) {
							exit();
						}
					}
				});
	}

	public void onEventMainThread(ExitEvent event) {
		exit();
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
			params.add("method", "collectSupplyInfos");
			params.add("supplyer_cd", info.getSupplyer_cd());
			params.add("gps_j", "" + location.getLongitude());
			params.add("gps_w", "" + location.getLatitude());
			params.add("speed", "" + location.getSpeed());
			params.add("phone_type", "" + DeviceInfo.getModel());
			params.add("operate_system", "" + DeviceInfo.getOSName());
			params.add("sys_edtion", "" + DeviceInfo.getOSVersion());
			params.add("app_version", "" + DeviceInfo.getVersionName());
			params.add("clientid", "" + Const.clientid);
			mHttpClient.get(
					AsyncHttp.getAbsoluteUrl(Const.URL_POSITION_UPLOAD),
					params, mHandler);
		}
	}
}
