package cn.boweikeji.wuliu.supplyer.activity;

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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import cn.boweikeji.wuliu.supplyer.api.BaseParams;
import cn.boweikeji.wuliu.supplyer.bean.UserInfo;
import cn.boweikeji.wuliu.supplyer.event.UpdateEvent;
import cn.boweikeji.wuliu.supplyer.fragment.BaseFragment;
import cn.boweikeji.wuliu.supplyer.fragment.LoginFragment;
import cn.boweikeji.wuliu.supplyer.fragment.MainFragment;
import cn.boweikeji.wuliu.supplyer.fragment.OrderFragment;
import cn.boweikeji.wuliu.supplyer.fragment.ProfileFragment;
import cn.boweikeji.wuliu.supplyer.fragment.SetFragment;
import cn.boweikeji.wuliu.supplyer.manager.LoginManager;
import cn.boweikeji.wuliu.supplyer.utils.DeviceInfo;
import cn.boweikeji.wuliu.supplyer.utils.UpdateUtil;
import cn.boweikeji.wuliu.supplyer.utils.Util;
import cn.boweikeji.wuliu.supplyer.view.MenuView;
import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.R;
import cn.boweikeji.wuliu.supplyer.WLApplication;
import de.greenrobot.event.EventBus;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private static final int EXIT_TIME = 2000;

	private static final int REQUEST_CODE_REGISTER = 1 << 0;

	public static final String KEY_REDIRECT = "redirect";
	public static final String KEY_REDIRECT_TO = "redirect_to";
	public static final int REDIRECT_TO_ORDERDETAIL = 1 << 0;

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
					changeFragment(new ProfileFragment());
					break;
				case MenuView.MENU_ORDER:
					changeFragment(new OrderFragment());
					break;
				case MenuView.MENU_MESSAGE:
					WebViewActivity.startWebViewActivity(MainActivity.this,
							getString(R.string.title_system_msg),
							Const.URL_SYSTEM_MSG);
					break;
				case MenuView.MENU_INVITE:
					Util.sendMessage(MainActivity.this, null, getResources()
							.getString(R.string.invite_msg));
					break;
				case MenuView.MENU_SHARE:
					Util.showShare(MainActivity.this);
					break;
				case MenuView.MENU_SETTING:
					changeFragment(new SetFragment());
					break;
				}
			}
		}
	};

	private DrawerLayout mDrawerLayout;
	private MenuView mMenuView;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private UiSettings mUiSettings;

	private View mMyLocLayout;
	private TextView mMyLocTitle;
	private TextView mMyLocInfo;
	private View mDriverLayout;
	private ImageView mDriverPortrait;
	private TextView mDriverName;
	private TextView mDriverInfo;

	private FragmentManager mFragmentManager;

	private LocationClient mLocClient;

	private ScheduledThreadPoolExecutor mTimerTask;

	private long mExitTime;
	private int mMenuIndex;

	private boolean mMapShowing;
	private boolean mIsFirstLoc;
	private boolean mHasDriver;
	private boolean mFirstCheckUpdate = true;
	private boolean mShowUpdateDialog = false;

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
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (mFragmentManager.getBackStackEntryCount() == 0
					&& LoginManager.getInstance().hasLogin()) {
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_REGISTER) {
			if (resultCode == RESULT_OK) {
				Fragment fragment = mFragmentManager
						.findFragmentByTag(LoginFragment.class.getName());
				if (fragment != null) {
					UserInfo info = (UserInfo) data
							.getSerializableExtra("userinfo");
					((LoginFragment) fragment).login(info);
				}
			}
		}
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
		} else {
			if (mFragmentManager.getBackStackEntryCount() > 0) {
				back();
			}
		}
	}

	private void init() {
		mIsFirstLoc = true;
		mHasDriver = false;
		initView();
		initMenu();
		initMap();
		initFragment();
		initLocation();
		EventBus.getDefault().register(this);
		UpdateUtil.checkUpdate();
		mTimerTask = new ScheduledThreadPoolExecutor(1);
		mTimerTask.scheduleAtFixedRate(new PositionTask(), 30, 300,
				TimeUnit.SECONDS);
	}

	private void initMenu() {
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerLayout.setDrawerTitle(GravityCompat.START, "MenuView");
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mMenuView);
		mDrawerLayout.setDrawerListener(mDrawerListener);
		mMenuView.setOnMenuClickListener(mOnMenuClickListener);
		EventBus.getDefault().register(mMenuView);
	}

	private void initView() {
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		mMenuView = (MenuView)findViewById(R.id.menuview);
		mMapView = (MapView)findViewById(R.id.bmapView);
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
		BDLocation location = WLApplication.getLocationClient().getLastKnownLocation();
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
	
	private void initFragment() {
		mFragmentManager = getSupportFragmentManager();
		mFragmentManager.beginTransaction()
				.replace(R.id.topLayout, new MainFragment()).commit();
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
			changeFragment(new LoginFragment());
		}
	}

	public void back() {
		mFragmentManager.popBackStack();
	}

	public void register() {
		startActivityForResult(new Intent(this, RegisterActivity.class),
				REQUEST_CODE_REGISTER);
	}

	public void changeFragment(BaseFragment fragment) {
		FragmentTransaction trans = mFragmentManager.beginTransaction();
		trans.setCustomAnimations(R.anim.push_in, R.anim.push_out,
				R.anim.pop_in, R.anim.pop_out);
		trans.replace(R.id.topLayout, fragment, fragment.getClass().getName());
		trans.addToBackStack(fragment.getClass().getSimpleName());
		trans.commit();
		mDrawerLayout.closeDrawer(mMenuView);
	}

	private void exit() {
		finish();
		System.exit(0);
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
				if (truck >= 0 && truck < truckTypes.length) {
					bundle.putString("info", truckTypes[truck]);
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
		mMyLocTitle.setText(String.format(
				getResources().getString(R.string.myloc), loc.getStreet()));
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
		Bundle bundle = marker.getExtraInfo();
		if (bundle != null) {
			mDriverName.setText(bundle.getString("name"));
			mDriverInfo.setText(bundle.getString("info"));
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
		AsyncHttpClient client = new AsyncHttpClient();
		client.setURLEncodingEnabled(true);

		BaseParams params = new BaseParams();
		params.add("method", "getNearDrivers");
		params.add("radius", "5000");
		params.add("gps_j", "" + location.getLongitude());
		params.add("gps_w", "" + location.getLatitude());

		Log.d(TAG,
				"URL: "
						+ AsyncHttpClient.getUrlWithQueryString(true,
								Const.URL_NEAR_DRIVER, params));
		client.get(Const.URL_NEAR_DRIVER, params, mRequestHandler);
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

	public void onEventMainThread(UpdateEvent event) {
		if (mFirstCheckUpdate) {
			mFirstCheckUpdate = false;
			showUpdateDialog(event);
		}
	}

	public synchronized void showUpdateDialog(final UpdateEvent event) {
		if (event != null) {
			if (event.isNeedUpdate()) {
				if (mShowUpdateDialog) {
					return;
				}
				mShowUpdateDialog = true;

				AlertDialog dialog = new AlertDialog.Builder(this)
						.setTitle(event.getVersion())
						.setMessage(event.getContent())
						.setCancelable(false)
						.setPositiveButton(R.string.upgrade,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										mShowUpdateDialog = false;
										Intent intent = new Intent(
												Intent.ACTION_VIEW);
										intent.setData(Uri.parse(event.getUrl()));
										startActivity(intent);
									}
								})
						.setNegativeButton(
								event.isForce() ? R.string.exit
										: R.string.cancel,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										mShowUpdateDialog = false;
										if (event.isForce()) {
											exit();
										}
									}
								}).create();
				dialog.show();
			}
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
			params.add("method", "collectSupplyInfos");
			params.add("supplyer_cd", info.getSupplyer_cd());
			params.add("gps_j", "" + location.getLongitude());
			params.add("gps_w", "" + location.getLatitude());
			params.add("speed", "" + location.getSpeed());
			params.add("phone_type", "" + DeviceInfo.getModel());
			params.add("operate_system", "" + DeviceInfo.getOSName());
			params.add("sys_edtion", "" + DeviceInfo.getOSVersion());
			params.add("app_version", "" + DeviceInfo.getAppVersion());
			params.add("clientid", "" + Const.clientid);

			Log.d(TAG,
					"URL: "
							+ AsyncHttpClient.getUrlWithQueryString(true,
									Const.URL_POSITION_UPLOAD, params));
			mHttpClient.get(Const.URL_POSITION_UPLOAD, params, mHandler);
		}
	}
}
